package com.gunnarro.android.terex.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.fragment.app.Fragment;

import com.gunnarro.android.terex.R;
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

    @Inject
    public InvoiceViewFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requireActivity().setTitle(R.string.title_invoice);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_invoice_view, container, false);
        if (getArguments() == null) {

        }
        String invoiceId = getArguments().getString(InvoiceListFragment.INVOICE_ID_KEY);
        String invoiceHtml = null;
        try {
            invoiceHtml = readInvoiceFile(LocalDate.now());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        WebView webView = view.findViewById(R.id.invoice_web_view);
        webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        webView.loadDataWithBaseURL(null, invoiceHtml, "text/html", "utf-8", null);
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

}
