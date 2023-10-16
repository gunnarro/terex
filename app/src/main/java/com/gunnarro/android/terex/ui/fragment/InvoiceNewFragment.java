package com.gunnarro.android.terex.ui.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.google.android.material.navigation.NavigationView;
import com.gunnarro.android.terex.R;
import com.gunnarro.android.terex.domain.entity.Address;
import com.gunnarro.android.terex.domain.entity.Company;
import com.gunnarro.android.terex.domain.entity.Contact;
import com.gunnarro.android.terex.domain.entity.Invoice;
import com.gunnarro.android.terex.domain.entity.Person;
import com.gunnarro.android.terex.domain.entity.SpinnerItem;
import com.gunnarro.android.terex.domain.entity.Timesheet;
import com.gunnarro.android.terex.domain.entity.TimesheetEntry;
import com.gunnarro.android.terex.domain.entity.TimesheetSummary;
import com.gunnarro.android.terex.exception.TerexApplicationException;
import com.gunnarro.android.terex.service.InvoiceService;
import com.gunnarro.android.terex.service.TimesheetService;
import com.gunnarro.android.terex.utility.PdfUtility;
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

    private final static String INVOICE_TIMESHEET_SUMMARY_ATTACHMENT_TEMPLATE = "html/template/invoice-attachment.mustache";
    private final static String RECRUITMENT_TIMESHEET_TEMPLATE = "html/template/norway-consulting-timesheet-template.mustache";
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
        Log.d(Utility.buildTag(getClass(), "onCreate"), "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_invoice_new, container, false);
        // only completed time sheets can be used a attachment to a invoice.
        List<Timesheet> timesheetList = timesheetService.getTimesheets(Timesheet.TimesheetStatusEnum.COMPLETED.name());
        List<SpinnerItem> timesheetItems = timesheetList.stream().map(t -> new SpinnerItem(t.id, t.getProjectCode())).collect(Collectors.toList());
        final AutoCompleteTextView timesheetSpinner = view.findViewById(R.id.invoice_timesheet_spinner);
        ArrayAdapter<SpinnerItem> timesheetAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, timesheetItems);
        timesheetSpinner.setAdapter(timesheetAdapter);
        timesheetSpinner.setListSelection(1);

        view.findViewById(R.id.btn_invoice_new_create).setOnClickListener(v -> {
            try {
                String selectedTimesheet = timesheetSpinner.getText().toString();
                SpinnerItem item = timesheetItems.stream().filter(i -> i.name().equals(selectedTimesheet)).findFirst().orElse(null);
                if (item == null) {
                    showInfoDialog("Please select a timesheet!", requireContext());
                } else {
                    Invoice invoice = createInvoice(item.id());
                    createTimesheetSummaryAttachment(invoice.getId());
                    createTimesheetAttachment(item.id());
                }
            } catch (TerexApplicationException e) {
                showInfoDialog("Error creating invoice!", requireContext());
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

        if (timesheetList == null || timesheetList.isEmpty()) {
            // disable dropdown and create button
            view.findViewById(R.id.invoice_timesheet_spinner).setEnabled(false);
            view.findViewById(R.id.btn_invoice_new_create).setEnabled(false);
            showInfoDialog("No timesheet ready for billing. Please fulfill the timesheet and try again.", requireContext());
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
     * @return id of the invoice
     */
    private Invoice createInvoice(@NotNull Long timesheetId) {
        Long invoiceId = invoiceService.createInvoice(getCompany(), getClient(), timesheetId);
        if (invoiceId == null) {
            showInfoDialog("No timesheet found! timesheetId=" + timesheetId, requireContext());
        }
        // finally close the timesheet
        return invoiceService.getInvoice(invoiceId);
    }

    private boolean createTimesheetSummaryAttachment(Long invoiceId) {
        try {
            Invoice invoice = invoiceService.getInvoice(invoiceId);
            Log.d("createTimesheetSummaryAttachment", "timesheetSummary week: " + invoice.getTimesheetSummaryList());
            StringBuilder mustacheTemplateStr = new StringBuilder();
            // first read the invoice summary mustache html template
            try (InputStream fis = requireContext().getAssets().open(INVOICE_TIMESHEET_SUMMARY_ATTACHMENT_TEMPLATE);
                 InputStreamReader isr = new InputStreamReader(fis,
                         StandardCharsets.UTF_8);
                 BufferedReader br = new BufferedReader(isr)) {
                br.lines().forEach(mustacheTemplateStr::append);
            }

            Double sumBilledAmount = invoice.getTimesheetSummaryList().stream()
                    .mapToDouble(TimesheetSummary::getSumBilledWork)
                    .sum();

            Double sumBilledHours = invoice.getTimesheetSummaryList().stream()
                    .mapToDouble(TimesheetSummary::getTotalWorkedHours)
                    .sum();

            String invoiceSummaryHtml = createInvoiceSummaryHtml(mustacheTemplateStr.toString(), invoice.getTimesheetSummaryList(), sumBilledHours.toString(), sumBilledAmount.toString());
            Log.d("createInvoiceSummaryAttachment", "" + invoiceSummaryHtml);
            return PdfUtility.htmlToPdf(invoiceSummaryHtml, "invoice_attachment");
        } catch (Exception e) {
            throw new TerexApplicationException(String.format("Error crating invoice attachment, invoice ref=%s", invoiceId), "50023", e);
        }
    }

    private String createInvoiceSummaryHtml(String mustacheTemplateStr, List<TimesheetSummary> timesheetSummaryList, String sumBilledHours, String sumBilledAmount) {
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile(new StringReader(mustacheTemplateStr), "");
        Map<String, Object> context = new HashMap<>();
        context.put("title", "Vedlegg til faktura " + LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        context.put("company", getCompany());
        context.put("client", getClient());
        context.put("timesheetProjectCode", "techlead-catalystone-solution-as");
        context.put("timesheetSummaryList", timesheetSummaryList);
        context.put("sunBilledHours", sumBilledHours);
        context.put("sumBilledAmount", sumBilledAmount);
        StringWriter writer = new StringWriter();
        mustache.execute(writer, context);
        return writer.toString();
    }


    private boolean createTimesheetAttachment(Long timesheetId) {
        try {
            List<TimesheetEntry> timesheetEntryList = timesheetService.getTimesheetEntryList(timesheetId);
            StringBuilder mustacheTemplateStr = new StringBuilder();
            // first read the invoice summary mustache html template
            try (InputStream fis = requireContext().getAssets().open(RECRUITMENT_TIMESHEET_TEMPLATE);
                 InputStreamReader isr = new InputStreamReader(fis,
                         StandardCharsets.UTF_8);
                 BufferedReader br = new BufferedReader(isr)) {
                br.lines().forEach(mustacheTemplateStr::append);
            }

            Double sumBilledHours = timesheetEntryList.stream()
                    .mapToDouble(TimesheetEntry::getWorkedMinutes)
                    .sum() / 60;

            String timesheetHtml = createTimesheetListHtml(mustacheTemplateStr.toString(), timesheetEntryList, sumBilledHours.toString());
            return PdfUtility.htmlToPdf(timesheetHtml, "timesheet_attachment");
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
        context.put("sunWorkDays", timesheetList.size());
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

    private void showInfoDialog(String infoMessage, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Info");
        builder.setMessage(infoMessage);
        // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
        builder.setCancelable(false);
        // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
        builder.setPositiveButton("Ok", (dialog, which) -> dialog.cancel());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private Company getCompany() {
        Company company = new Company();
        company.setId(10L);
        company.setName("gunnarro:as");
        company.setOrganizationNumber("828 707 922");
        company.setBankAccountNumber("9230 26 98831");
        Address address = new Address();
        address.setStreetNumber("35");
        address.setStreetName("Stavangergata");
        address.setPostCode("0467");
        address.setCity("Oslo");
        address.setCountryCode("no");
        company.setBusinessAddress(address);
        return company;
    }

    private Company getClient() {
        Company client = new Company();
        client.setId(20L);
        client.setName("Norway Consulting AS");
        client.setOrganizationNumber("");
        Address address = new Address();
        address.setStreetNumber("16");
        address.setStreetName("Grensen");
        address.setPostCode("0159");
        address.setCity("Oslo");
        address.setCountryCode("no");
        client.setBusinessAddress(address);
        Person contactPerson = new Person();
        contactPerson.setFirstName("Anita");
        contactPerson.setLastName("Lundtveit");
        client.setContactPerson(contactPerson);

        Contact contactInfo = new Contact();
        client.setContactInfo(contactInfo);
        return client;
    }

    private void returnToInvoiceList() {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, InvoiceListFragment.class, null)
                .setReorderingAllowed(true)
                .commit();
    }

}
