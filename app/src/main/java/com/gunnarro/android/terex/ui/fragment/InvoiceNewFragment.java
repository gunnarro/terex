package com.gunnarro.android.terex.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.gunnarro.android.terex.R;
import com.gunnarro.android.terex.domain.dto.SpinnerItem;
import com.gunnarro.android.terex.domain.entity.Invoice;
import com.gunnarro.android.terex.domain.entity.InvoiceAttachment;
import com.gunnarro.android.terex.domain.entity.Timesheet;
import com.gunnarro.android.terex.exception.TerexApplicationException;
import com.gunnarro.android.terex.service.InvoiceService;
import com.gunnarro.android.terex.service.TimesheetService;
import com.gunnarro.android.terex.utility.Utility;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class InvoiceNewFragment extends BaseFragment {

    private InvoiceService invoiceService;
    private TimesheetService timesheetService;

    @Inject
    public InvoiceNewFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requireActivity().setTitle(R.string.title_invoice);
        invoiceService = new InvoiceService();
        timesheetService = new TimesheetService();
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
                    Long invoiceId = createInvoice(item.id());
                    Bundle bundle = new Bundle();
                    bundle.putLong(InvoiceListFragment.INVOICE_ID_KEY, invoiceId);
                    bundle.putString(InvoiceListFragment.INVOICE_ACTION_KEY, InvoiceListFragment.INVOICE_ACTION_VIEW);
                    navigateTo(R.id.nav_from_invoice_new_to_invoice_details, bundle);
                }
            } catch (TerexApplicationException e) {
                showInfoDialog("Info", "Error creating invoice!" + e.getMessage());
            }
        });

        view.findViewById(R.id.btn_invoice_new_cancel).setOnClickListener(v -> {
            view.findViewById(R.id.btn_invoice_new_cancel).setBackgroundColor(getResources().getColor(R.color.color_btn_bg_cancel, view.getContext().getTheme()));
            // Simply return back to credential list
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
     * Create a new invoice for the given timesheet.
     *
     * @param timesheetId to be created invoice for
     */
    private Long createInvoice(@NotNull Long timesheetId) {
        Long invoiceId = invoiceService.createInvoice(timesheetId);
        if (invoiceId == null) {
            showInfoDialog("Info", "No timesheet found! timesheetId=" + timesheetId);
        }
        Invoice invoice = invoiceService.getInvoice(invoiceId);
        createTimesheetSummaryAttachment(invoice.getId());
        createClientTimesheetAttachment(invoice.getId(), invoice.getTimesheetId());
        showInfoDialog("Info", "Successfully generated invoice attachment. invoiceId=" + invoiceId);
        return invoiceId;
    }

    private void createTimesheetSummaryAttachment(Long invoiceId) {
        try {
            Invoice invoice = invoiceService.getInvoice(invoiceId);
            Log.d("createTimesheetSummaryAttachment", "timesheetId=" + invoice.getTimesheetId());

            String invoiceSummaryHtml = timesheetService.createTimesheetSummaryAttachmentHtml(invoice.getTimesheetId(), requireContext());
            Log.d("createInvoiceSummaryAttachment", invoiceSummaryHtml);
            String invoiceAttachmentFileName = InvoiceService.InvoiceAttachmentTypesEnum.TIMESHEET_SUMMARY.name().toLowerCase() + "_attachment_" + invoice.getBillingDate().format(DateTimeFormatter.ofPattern("yyyy-MM"));

            InvoiceAttachment timesheetSummaryAttachment = new InvoiceAttachment();
            timesheetSummaryAttachment.setInvoiceId(invoiceId);
            timesheetSummaryAttachment.setAttachmentType(InvoiceService.InvoiceAttachmentTypesEnum.TIMESHEET_SUMMARY.name());
            timesheetSummaryAttachment.setAttachmentFileName(invoiceAttachmentFileName);
            timesheetSummaryAttachment.setAttachmentFileType("html");
            timesheetSummaryAttachment.setAttachmentFileContent(invoiceSummaryHtml.getBytes(StandardCharsets.UTF_8));
            invoiceService.saveInvoiceAttachment(timesheetSummaryAttachment);
        } catch (Exception e) {
            throw new TerexApplicationException(String.format("Error crating invoice attachment, invoice ref=%s", invoiceId), "50023", e);
        }
    }

    /**
     * The timesheet should contain an entry for all days in the month.
     * Days not worked, sick, or vacation are simple blank.
     */
    private void createClientTimesheetAttachment(Long invoiceId, Long timesheetId) {
        try {
            String timesheetAttachmentHtml = timesheetService.createTimesheetListHtml(timesheetId, requireContext());
            String timesheetAttachmentFileName = InvoiceService.InvoiceAttachmentTypesEnum.CLIENT_TIMESHEET.name().toLowerCase() + "_attachment_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
            InvoiceAttachment clientTimesheetAttachment = new InvoiceAttachment();
            clientTimesheetAttachment.setInvoiceId(invoiceId);
            clientTimesheetAttachment.setAttachmentType(InvoiceService.InvoiceAttachmentTypesEnum.CLIENT_TIMESHEET.name());
            clientTimesheetAttachment.setAttachmentFileName(timesheetAttachmentFileName);
            clientTimesheetAttachment.setAttachmentFileType("html");
            clientTimesheetAttachment.setAttachmentFileContent(timesheetAttachmentHtml.getBytes(StandardCharsets.UTF_8));
            invoiceService.saveInvoiceAttachment(clientTimesheetAttachment);
        } catch (Exception e) {
            throw new TerexApplicationException(String.format("Error crating timesheet attachment, timesheetId=%s", timesheetId), "50023", e);
        }
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

    private void returnToInvoiceList() {
        navigateTo(R.id.nav_from_invoice_new_to_invoice_list, null);
    }

}
