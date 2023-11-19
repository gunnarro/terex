package com.gunnarro.android.terex.ui.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.gunnarro.android.terex.R;
import com.gunnarro.android.terex.service.InvoiceService;
import com.gunnarro.android.terex.utility.PdfUtility;
import com.gunnarro.android.terex.utility.Utility;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class InvoiceViewFragment extends Fragment {

    private InvoiceService invoiceService;

    @Inject
    public InvoiceViewFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requireActivity().setTitle(R.string.title_invoice_attachment);
        invoiceService = new InvoiceService(requireActivity().getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_invoice_view, container, false);
        setHasOptionsMenu(true);
        String invoiceId = getArguments().getString(InvoiceListFragment.INVOICE_ID_KEY);
        byte[] invoiceHtml = null;
        byte[] invoicePdf = null;
        try {
            //invoiceHtml = readInvoiceFile(LocalDate.now());
            invoiceHtml = invoiceService.getInvoiceAttachment(Long.valueOf(invoiceId), "html").getAttachmentFileContent();
            //invoicePdf = invoiceService.getInvoiceAttachment(Long.valueOf(invoiceId), "pdf").getAttachmentFileContent();
        } catch (Exception e) {
            showInfoDialog("Error", String.format("Application error!%sError: %s%s Please report.", e.getMessage(), System.lineSeparator(), System.lineSeparator()), getActivity());
        }
        WebView webView = view.findViewById(R.id.invoice_web_view);
        webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        webView.getSettings().setJavaScriptEnabled(false);
        webView.getSettings().setLoadsImagesAutomatically(true);
        //webView.loadDataWithBaseURL(null, invoiceHtml, "text/html", "utf-8", null);
        Log.d("invoice timesheet attachment", String.format("%s", new String(invoiceHtml)));
        webView.loadDataWithBaseURL(null, new String(invoiceHtml), "text/html", "UTF-8", null);
        //  webView.loadDataWithBaseURL(null, new String(invoicePdf), "application/pdf", "UTF-8", null);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.attachment_to_pdf) {
            exportAttachment();
        }
        return true;
    }

    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        // clear current menu items
        menu.clear();
        // set fragment specific menu items
        inflater.inflate(R.menu.invoice_attachment_menu, menu);
        MenuItem m = menu.findItem(R.id.attachment_to_pdf);
      //  Objects.requireNonNull(m.getActionView()).setOnClickListener(v -> exportAttachment());
        Log.d(Utility.buildTag(getClass(), "onCreateOptionsMenu"), "menu: " + menu);
    }

    private void returnToInvoiceList() {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, InvoiceListFragment.class, null)
                .setReorderingAllowed(true)
                .commit();
    }

    private void exportAttachment() {
        String invoiceAttachmentFileName = "invoice_attachment_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        //    PdfUtility.saveFile(timesheetHtml, PdfUtility.getLocalDir() + "/" + invoiceFileName + ".pdf") && PdfUtility.saveFile(timesheetHtml, PdfUtility.getLocalDir() + "/" + invoiceFileName + ".html");
        showInfoDialog("Info", String.format("Exported to %s", invoiceAttachmentFileName), requireContext());
    }

    /**
     * content://com.android.providers.downloads.documents/document/downloads
     */
    private String readInvoiceFile(LocalDate invoiceDate) throws IOException {
        String invoiceFileName = "invoice_attachment_" + invoiceDate.format(DateTimeFormatter.ISO_DATE) + ".html";
        return PdfUtility.readFile(PdfUtility.getLocalDir() + "/" + invoiceFileName);
    }

    private void showInfoDialog(String title, String infoMessage, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(infoMessage);
        // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
        builder.setCancelable(false);
        // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
        builder.setPositiveButton("Ok", (dialog, which) -> dialog.cancel());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
