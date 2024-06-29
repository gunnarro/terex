package com.gunnarro.android.terex.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.gunnarro.android.terex.R;
import com.gunnarro.android.terex.domain.dto.SpinnerItem;
import com.gunnarro.android.terex.domain.entity.Timesheet;
import com.gunnarro.android.terex.exception.TerexApplicationException;
import com.gunnarro.android.terex.service.ClientService;
import com.gunnarro.android.terex.service.InvoiceService;
import com.gunnarro.android.terex.service.ProjectService;
import com.gunnarro.android.terex.service.TimesheetService;
import com.gunnarro.android.terex.service.UserAccountService;
import com.gunnarro.android.terex.utility.Utility;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class InvoiceNewFragment extends BaseFragment {

    private InvoiceService invoiceService;
    private TimesheetService timesheetService;
    private ClientService clientService;
    private UserAccountService userAccountService;
    private ProjectService projectService;

    @Inject
    public InvoiceNewFragment() {
        // Needed by dagger framework
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.invoiceService = new InvoiceService();
        this.timesheetService = new TimesheetService();
        this.clientService = new ClientService();
        this.userAccountService = new UserAccountService();
        this.projectService = new ProjectService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        requireActivity().setTitle(R.string.title_invoice);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_invoice_new, container, false);
        // only completed time sheets can be used a attachment to a invoice.
        List<Timesheet> timesheetList = timesheetService.getTimesheetsByStatus(Timesheet.TimesheetStatusEnum.COMPLETED.name());
        List<SpinnerItem> timesheetItems = timesheetList.stream().map(t -> new SpinnerItem(t.getId(), String.format("%s-%s %s", t.getYear(), t.getMonth(), t.toString()))).collect(Collectors.toList());
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
                    returnToInvoiceList(bundle);
                }
            } catch (TerexApplicationException e) {
                showInfoDialog("Info", "Error creating invoice!" + e.getMessage());
            }
        });

        view.findViewById(R.id.btn_invoice_new_cancel).setOnClickListener(v -> {
            view.findViewById(R.id.btn_invoice_new_cancel).setBackgroundColor(getResources().getColor(R.color.color_btn_bg_cancel, view.getContext().getTheme()));
            // Simply return back to credential list
            returnToInvoiceList(savedInstanceState);
        });

        Log.d(Utility.buildTag(getClass(), "onCreateView"), "");
        return view;
    }

    /**
     * Check data here
     */
    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        // must call super first in order to use the navigation controller, that must be initialized first.
        super.onViewCreated(view, savedInstanceState);
        final AutoCompleteTextView timesheetSpinner = view.findViewById(R.id.invoice_timesheet_spinner);
        if (timesheetSpinner.getAdapter().isEmpty()) {
            showInfoDialog("Info", "No timesheet ready for billing. Please fulfill the timesheet and try again.");
            navigateTo(R.id.nav_to_invoice_list, savedInstanceState);
        }
    }

    /**
     * Create a new invoice for the given timesheet.
     *
     * @param timesheetId to be created invoice for
     */
    private Long createInvoice(@NotNull Long timesheetId) {
        try {
            Long invoiceReceiverId = clientService.getClientIdByTimesheetId(timesheetId);
            // get invoice issuer, which is your company id
            Long invoiceIssuerId = userAccountService.getActiveUserAccountId();
            Integer hourlyRate = projectService.getProjectHourlyRate(timesheetId);
            Long invoiceId = invoiceService.createInvoice(invoiceIssuerId, invoiceReceiverId, timesheetId, hourlyRate);
            if (invoiceId == null) {
                showInfoDialog("Info", "No timesheet found! timesheetId=" + timesheetId);
            }
            String timesheetSummeryMustacheTemplate = Utility.loadMustacheTemplate(requireContext(), InvoiceService.InvoiceAttachmentTypesEnum.TIMESHEET_SUMMARY);
            invoiceService.createTimesheetSummaryAttachment(invoiceId, timesheetSummeryMustacheTemplate);

            String clientTimesheetMustacheTemplate = Utility.loadMustacheTemplate(requireContext(), InvoiceService.InvoiceAttachmentTypesEnum.CLIENT_TIMESHEET);
            invoiceService.createClientTimesheetAttachment(invoiceId, timesheetId, clientTimesheetMustacheTemplate);

            showInfoDialog("Info", String.format("Successfully created invoice for timesheet. invoiceId=%s, timesheetId=%s)", invoiceId, timesheetId));
            return invoiceId;
        } catch (Exception e) {
            throw new TerexApplicationException(String.format("Error creating invoice for timesheet, timesheetId=%s", timesheetId), "50023", e);
        }
    }

    private void returnToInvoiceList(Bundle bundle) {
        navigateTo(R.id.nav_to_invoice_list, bundle);
    }
}
