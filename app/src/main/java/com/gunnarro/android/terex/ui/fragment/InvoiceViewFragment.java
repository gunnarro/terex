package com.gunnarro.android.terex.ui.fragment;

import android.os.Bundle;
import android.os.Environment;
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

import java.io.File;
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
        Log.d(Utility.buildTag(getClass(), "onCreate"), "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_invoice_view, container, false);
        String invoiceHtml = "";
        WebView webView = requireView().findViewById(R.id.invoice_view);
        webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        webView.loadDataWithBaseURL(null, invoiceHtml, "text/html", "utf-8", null);
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
        WebView webView = requireView().findViewById(R.id.invoice_view);
        webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        webView.loadDataWithBaseURL(null, invoiceHtml, "text/html", "utf-8", null);
    }

    private void returnToInvoiceList() {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, InvoiceListFragment.class, null)
                .setReorderingAllowed(true)
                .commit();
    }

    private String readInvoiceFile(LocalDate invoiceDate) throws IOException {
        String invoiceFileName = "timesheet_attachment" + invoiceDate.format(DateTimeFormatter.ISO_DATE);
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        return PdfUtility.readFile(path.getPath() + "/" + invoiceFileName);
    }

}
