package com.gunnarro.android.terex.ui.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.gunnarro.android.terex.R;
import com.gunnarro.android.terex.service.InvoiceService;
import com.gunnarro.android.terex.utility.Utility;

import org.jetbrains.annotations.NotNull;

import java.io.File;

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
        requireActivity().setTitle(R.string.title_admin);
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
