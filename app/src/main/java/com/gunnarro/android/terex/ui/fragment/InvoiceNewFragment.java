package com.gunnarro.android.terex.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.fragment.app.Fragment;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.gunnarro.android.terex.R;
import com.gunnarro.android.terex.domain.entity.Invoice;
import com.gunnarro.android.terex.domain.entity.InvoiceAttachment;
import com.gunnarro.android.terex.domain.entity.SpinnerItem;
import com.gunnarro.android.terex.domain.entity.Timesheet;
import com.gunnarro.android.terex.domain.entity.TimesheetEntry;
import com.gunnarro.android.terex.domain.entity.TimesheetSummary;
import com.gunnarro.android.terex.exception.TerexApplicationException;
import com.gunnarro.android.terex.repository.InvoiceRepository;
import com.gunnarro.android.terex.service.InvoiceService;
import com.gunnarro.android.terex.service.TimesheetService;
import com.gunnarro.android.terex.utility.Utility;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class InvoiceNewFragment extends Fragment {

    // @Inject
    private InvoiceService invoiceService;
    private TimesheetService timesheetService;

    @Inject
    public InvoiceNewFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requireActivity().setTitle(R.string.title_invoice);
        invoiceService = new InvoiceService(requireActivity().getApplicationContext());
        timesheetService = new TimesheetService(requireActivity().getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_invoice_new, container, false);
        // only completed time sheets can be used a attachment to a invoice.
        List<Timesheet> timesheetList = timesheetService.getTimesheets(Timesheet.TimesheetStatusEnum.COMPLETED.name());
        List<SpinnerItem> timesheetItems = timesheetList.stream().map(t -> new SpinnerItem(t.getId(), String.format("%s-%s %s", t.getYear(), t.getMonth(), t.getProjectCode()))).collect(Collectors.toList());
        final AutoCompleteTextView timesheetSpinner = view.findViewById(R.id.invoice_timesheet_spinner);
        ArrayAdapter<SpinnerItem> timesheetAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, timesheetItems);
        timesheetSpinner.setAdapter(timesheetAdapter);
        timesheetSpinner.setListSelection(1);

        view.findViewById(R.id.btn_invoice_new_create).setOnClickListener(v -> {
            try {
                String selectedTimesheet = timesheetSpinner.getText().toString();
                SpinnerItem item = timesheetItems.stream().filter(i -> i.name().equals(selectedTimesheet)).findFirst().orElse(null);
                if (item == null) {
                    showInfoDialog("Info", "Please select a timesheet!");
                } else {
                    // item id is equal to selected timesheet id
                    createInvoice(item.id());

                }
            } catch (TerexApplicationException e) {
                showInfoDialog("Info", "Error creating invoice!");
            }
        });
/*
        view.findViewById(R.id.btn_invoice_new_send_email).setOnClickListener(v -> {
            try {
                File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File pdfFile = new File(path.getPath() + "/invoice_attachment_" + LocalDate.now().format(DateTimeFormatter.ISO_DATE) + ".pdf");
                sendInvoiceToClient("gunnar_ronneberg@yahoo.no", "test", "message", pdfFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
*/
        view.findViewById(R.id.btn_invoice_new_cancel).setOnClickListener(v -> {
            view.findViewById(R.id.btn_invoice_new_cancel).setBackgroundColor(getResources().getColor(R.color.color_btn_bg_cancel, view.getContext().getTheme()));
            // Simply return back to credential list
            NavigationView navigationView = requireActivity().findViewById(R.id.navigationView);
            requireActivity().onOptionsItemSelected(navigationView.getMenu().findItem(R.id.nav_invoice_list));
            returnToInvoiceList();
        });

        if (timesheetList.isEmpty()) {
            // disable dropdown and create button
            view.findViewById(R.id.invoice_timesheet_spinner).setEnabled(false);
            view.findViewById(R.id.btn_invoice_new_create).setEnabled(false);
            showInfoDialog("Info", "No timesheet ready for billing. Please fulfill the timesheet and try again.");
        }

        Log.d(Utility.buildTag(getClass(), "onCreateView"), "");
        return view;
    }

    /**
     * Update backup info after view is successfully create
     */
    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * Create a new invoice for the given timesheet.
     *
     * @param timesheetId to be created invoice for
     */
    private void createInvoice(@NotNull Long timesheetId) {
        Long invoiceId = invoiceService.createInvoice(timesheetService.getCompany(timesheetId), timesheetService.getClient(timesheetId), timesheetId);
        if (invoiceId == null) {
            showInfoDialog("Info", "No timesheet found! timesheetId=" + timesheetId);
        }
        // finally close the timesheet
        Invoice invoice = invoiceService.getInvoice(invoiceId);
        createTimesheetSummaryAttachment(invoice.getId());
        createClientTimesheetAttachment(invoice.getId(), invoice.getTimesheetId());
        invoice.setStatus(InvoiceRepository.InvoiceStatusEnum.COMPLETED.name());
        invoiceService.saveInvoice(invoice);
    }

    private Long createTimesheetSummaryAttachment(Long invoiceId) {
        try {
            Invoice invoice = invoiceService.getInvoice(invoiceId);
            Log.d("createTimesheetSummaryAttachment", "timesheetSummary week: " + invoice.getTimesheetSummaryList());
            StringBuilder mustacheTemplateStr = new StringBuilder();
            // first read the invoice summary mustache html template
            try (InputStream fis = requireContext().getAssets().open(InvoiceService.InvoiceAttachmentTypesEnum.TIMESHEET_SUMMARY.getTemplate());
                 InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
                 BufferedReader br = new BufferedReader(isr)) {
                br.lines().forEach(mustacheTemplateStr::append);
            }

            double sumBilledAmount = invoice.getTimesheetSummaryList().stream().mapToDouble(TimesheetSummary::getTotalBilledAmount).sum();
            double sumBilledHours = invoice.getTimesheetSummaryList().stream().mapToDouble(TimesheetSummary::getTotalWorkedHours).sum();
            double totalVat = sumBilledAmount * 0.25;
            double totalBilledAmountWithVat = sumBilledAmount + totalVat;

            String invoiceSummaryHtml = createTimesheetSummaryAttachmentHtml(mustacheTemplateStr.toString(), invoice.getTimesheetSummaryList(), Double.toString(sumBilledHours), Double.toString(sumBilledAmount), Double.toString(totalBilledAmountWithVat), Double.toString(totalVat));
            Log.d("createInvoiceSummaryAttachment", "" + invoiceSummaryHtml);
            String invoiceAttachmentFileName = InvoiceService.InvoiceAttachmentTypesEnum.TIMESHEET_SUMMARY.name().toLowerCase() + "_attachment_" + invoice.getBillingDate().format(DateTimeFormatter.ofPattern("yyyy-MM"));
            File path = Environment.getExternalStorageDirectory();

            InvoiceAttachment timesheetSummaryAttachment = new InvoiceAttachment();
            timesheetSummaryAttachment.setInvoiceId(invoiceId);
            timesheetSummaryAttachment.setAttachmentType(InvoiceService.InvoiceAttachmentTypesEnum.TIMESHEET_SUMMARY.name());
            timesheetSummaryAttachment.setAttachmentFileName(invoiceAttachmentFileName);
            timesheetSummaryAttachment.setAttachmentFileType("html");
            timesheetSummaryAttachment.setAttachmentFileContent(invoiceSummaryHtml.getBytes(StandardCharsets.UTF_8));
            return invoiceService.saveInvoiceAttachment(timesheetSummaryAttachment);

            //return PdfUtility.saveFile(invoiceSummaryHtml, PdfUtility.getLocalDir() + "/" + invoiceAttachmentFileName + ".pdf")
            //        && PdfUtility.saveFile(invoiceSummaryHtml, PdfUtility.getLocalDir() + "/" + invoiceAttachmentFileName + ".html");
        } catch (Exception e) {
            throw new TerexApplicationException(String.format("Error crating invoice attachment, invoice ref=%s", invoiceId), "50023", e);
        }
    }

    private String createTimesheetSummaryAttachmentHtml(String mustacheTemplateStr, List<TimesheetSummary> timesheetSummaryList, String totalBilledHours, String totalBilledAmount, String totalBilledAmountWithVat, String totalVat) {
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile(new StringReader(mustacheTemplateStr), "");
        Map<String, Object> context = new HashMap<>();
        context.put("invoiceAttachmentTitle", "Vedlegg til faktura");
        context.put("invoiceBillingPeriod", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM")));
        context.put("company", timesheetService.getCompany(null));
        context.put("client", timesheetService.getClient(null));
        context.put("timesheetProjectCode", "techlead-catalystone-solution-as");
        context.put("timesheetSummaryList", timesheetSummaryList);
        context.put("totalBilledHours", totalBilledHours);
        context.put("totalBilledAmount", totalBilledAmount);
        context.put("vatInPercent", "25%");
        context.put("totalVat", totalVat);
        context.put("totalBilledAmountWithVat", totalBilledAmountWithVat);
        StringWriter writer = new StringWriter();
        mustache.execute(writer, context);
        return writer.toString();
    }


    private Long createClientTimesheetAttachment(Long invoiceId, Long timesheetId) {
        try {
            List<TimesheetEntry> timesheetEntryList = timesheetService.getTimesheetEntryList(timesheetId);
            StringBuilder mustacheTemplateStr = new StringBuilder();
            // first read the invoice summary mustache html template
            try (InputStream fis = requireContext().getAssets().open(InvoiceService.InvoiceAttachmentTypesEnum.CLIENT_TIMESHEET.getTemplate()); InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8); BufferedReader br = new BufferedReader(isr)) {
                br.lines().forEach(mustacheTemplateStr::append);
            }

            double sumBilledHours = timesheetEntryList.stream().mapToDouble(TimesheetEntry::getWorkedMinutes).sum() / 60;

            String timesheetAttachmentHtml = createTimesheetListHtml(mustacheTemplateStr.toString(), timesheetEntryList, Double.toString(sumBilledHours));
            String timesheetAttachmentFileName = InvoiceService.InvoiceAttachmentTypesEnum.CLIENT_TIMESHEET.name().toLowerCase() + "_attachment_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
            InvoiceAttachment timesheetSummaryAttachment = new InvoiceAttachment();
            timesheetSummaryAttachment.setInvoiceId(invoiceId);
            timesheetSummaryAttachment.setAttachmentType(InvoiceService.InvoiceAttachmentTypesEnum.CLIENT_TIMESHEET.name());
            timesheetSummaryAttachment.setAttachmentFileName(timesheetAttachmentFileName);
            timesheetSummaryAttachment.setAttachmentFileType("html");
            timesheetSummaryAttachment.setAttachmentFileContent(timesheetAttachmentHtml.getBytes(StandardCharsets.UTF_8));
            return invoiceService.saveInvoiceAttachment(timesheetSummaryAttachment);

            //return PdfUtility.saveFile(timesheetHtml, PdfUtility.getLocalDir() + "/" + invoiceFileName + ".pdf")
            //        && PdfUtility.saveFile(timesheetHtml, PdfUtility.getLocalDir() + "/" + invoiceFileName + ".html");
        } catch (Exception e) {
            throw new TerexApplicationException(String.format("Error crating timesheet attachment, timesheetId=%s", timesheetId), "50023", e);
        }
    }

    private String createTimesheetListHtml(String mustacheTemplateStr, List<TimesheetEntry> timesheetList, String sumBilledHours) {
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile(new StringReader(mustacheTemplateStr), "");
        Map<String, Object> context = new HashMap<>();
        context.put("title", "Timeliste for konsulentbistand");
        context.put("timesheetPeriod", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM")));
        context.put("timesheetList", timesheetList);
        context.put("totalWorkDays", timesheetList.size());
        context.put("sunBilledHours", sumBilledHours);
        StringWriter writer = new StringWriter();
        mustache.execute(writer, context);
        return writer.toString();
    }

    private void sendInvoiceToClient(String toEmailAddress, String subject, String message, File pdfFile) {
        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{toEmailAddress});
        email.putExtra(Intent.EXTRA_SUBJECT, subject);
        email.putExtra(Intent.EXTRA_TEXT, message);
        email.putExtra(Intent.EXTRA_STREAM, pdfFile);
        //need this to prompts email client only
        email.setType("message/rfc822");
        startActivity(Intent.createChooser(email, "Choose Email client:"));
    }

    private void showInfoDialog(String severity, String message) {
        new MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme).setTitle(severity).setMessage(message).setCancelable(false).setPositiveButton("Ok", (dialog, which) -> dialog.cancel()).create().show();
    }

    private void returnToInvoiceList() {
        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, InvoiceListFragment.class, null).setReorderingAllowed(true).commit();
    }

}
