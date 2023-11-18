package com.gunnarro.android.terex.ui.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

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
        requireActivity().setTitle(R.string.title_invoice);
        invoiceService = new InvoiceService(requireActivity().getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_invoice_view, container, false);
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
        view.findViewById(R.id.btn_invoice_view_back).setOnClickListener(v -> {
            returnToInvoiceList();
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


    private void viewInvoiceAsHtml(String invoiceHtml) {

    }

    private void returnToInvoiceList() {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, InvoiceListFragment.class, null)
                .setReorderingAllowed(true)
                .commit();
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
