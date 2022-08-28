package com.gunnarro.android.terex.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.gunnarro.android.terex.R;
import com.gunnarro.android.terex.domain.entity.Timesheet;
import com.gunnarro.android.terex.repository.TimesheetRepository;
import com.gunnarro.android.terex.service.InvoiceService;
import com.gunnarro.android.terex.utility.Utility;

import org.jetbrains.annotations.NotNull;

import java.time.Month;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AdminFragment extends Fragment {

   // @Inject
    InvoiceService invoiceService = new InvoiceService();

    @Inject
    public AdminFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Log.d(Utility.buildTag(getClass(), "onCreate"), "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin, container, false);
        view.findViewById(R.id.btn_admin_generate_timesheet).setOnClickListener(v -> {
            try {
              generateTimesheet();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        view.findViewById(R.id.btn_admin_generate_invoice).setOnClickListener(v -> {
            try {
                createInvoice();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
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

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void generateTimesheet() {
        int month = ((Spinner)requireView().findViewById(R.id.admin_invoice_month_spinner)).getSelectedItemPosition();
        List<Timesheet> timesheets = invoiceService.generateTimesheet(2022, month+1);
        timesheets.forEach(t -> Log.i("generateTimesheet", t.toString()));
        Toast.makeText(requireContext(), "Generate timesheet for " + month, Toast.LENGTH_SHORT).show();
    }

    private void createInvoice() {
       int month = ((Spinner)requireView().findViewById(R.id.admin_invoice_month_spinner)).getSelectedItemPosition();
       Toast.makeText(requireContext(), "Create invoice for " + month, Toast.LENGTH_SHORT).show();
        invoiceService.buildInvoiceSummaryByWeek(2022, month);
    }
}
