package com.gunnarro.android.terex.ui.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.gunnarro.android.terex.R;
import com.gunnarro.android.terex.domain.entity.InvoiceSummary;
import com.gunnarro.android.terex.domain.entity.TimesheetEntry;
import com.gunnarro.android.terex.service.InvoiceService;
import com.gunnarro.android.terex.utility.Utility;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AdminFragment extends Fragment {

    private final static String INVOICE_TIMESHEET_SUMMARY_ATTACHMENT_TEMPLATE = "html/template/invoice-attachment.mustache";
    private final static String RECRUITMENT_TIMESHEET_TEMPLATE = "html/template/norway-consulting-timesheet-template.mustache";
    // @Inject
    private InvoiceService invoiceService;

    @Inject
    public AdminFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        invoiceService = new InvoiceService(requireActivity().getApplicationContext());
        Log.d(Utility.buildTag(getClass(), "onCreate"), "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin, container, false);
        /**
         view.findViewById(R.id.btn_admin_generate_timesheet).setOnClickListener(v -> {
         try {
         //generateTimesheet();
         createTimesheetAttachment(1L);
         } catch (Exception e) {
         e.printStackTrace();
         }
         });
         */
        view.findViewById(R.id.btn_create_invoice_attachment).setOnClickListener(v -> {
            try {

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        view.findViewById(R.id.btn_invoice_attachment_send_email).setOnClickListener(v -> {
            try {
                File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
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
/*
    private void generateTimesheet() {
        int month = ((Spinner) requireView().findViewById(R.id.admin_invoice_month_spinner)).getSelectedItemPosition();
        List<TimesheetEntry> timesheets = invoiceService.generateTimesheet(2023, month + 1);
        timesheets.forEach(t -> Log.i("generateTimesheet", t.toString()));
        Toast.makeText(requireContext(), "Generate timesheet for " + month, Toast.LENGTH_SHORT).show();
    }

    private void createInvoice() {
        int month = ((Spinner) requireView().findViewById(R.id.admin_invoice_month_spinner)).getSelectedItemPosition();
        Toast.makeText(requireContext(), "Create invoice for " + month, Toast.LENGTH_SHORT).show();
        List<InvoiceSummary> invoiceSummaries = invoiceService.buildInvoiceSummaryByWeek(2022, month);
        invoiceSummaries.forEach(i -> Log.i("generateInvoiceSummary", i.toString()));
    }
*/

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
}
