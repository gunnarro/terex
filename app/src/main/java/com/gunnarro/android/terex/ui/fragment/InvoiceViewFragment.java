package com.gunnarro.android.terex.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.gunnarro.android.terex.R;
import com.gunnarro.android.terex.service.InvoiceService;
import com.gunnarro.android.terex.utility.PdfUtility;
import com.gunnarro.android.terex.utility.Utility;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class InvoiceViewFragment extends Fragment {

    private InvoiceService invoiceService;
    private List<InvoiceService.InvoiceAttachmentTypesEnum> invoiceAttachmentTypes;

    private InvoiceService.InvoiceAttachmentTypesEnum selectedInvoiceAttachmentType = InvoiceService.InvoiceAttachmentTypesEnum.TIMESHEET_SUMMARY;
    private Long invoiceId;

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
        invoiceId = getArguments().getLong(InvoiceListFragment.INVOICE_ID_KEY);
        Log.d(Utility.buildTag(getClass(), "onCreateView"), "");
        return view;
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        invoiceAttachmentTypes = invoiceService.getInvoiceAttachmentTypes();
        loadInvoiceAttachment(invoiceId, selectedInvoiceAttachmentType);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.attachment_to_pdf) {
            exportAttachment(selectedInvoiceAttachmentType.name(), selectedInvoiceAttachmentType.getFileName());
        }
        return true;
    }

    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        // clear current menu items
        menu.clear();
        // set fragment specific menu items
        inflater.inflate(R.menu.invoice_attachment_menu, menu);
        MenuItem pdfMenuItem = menu.findItem(R.id.attachment_to_pdf);
        //  Objects.requireNonNull(m.getActionView()).setOnClickListener(v -> exportAttachment());

        MenuItem invoiceAttachmentMenuItem = menu.findItem(R.id.invoice_attachment_dropdown_menu);
        Spinner spinner = (Spinner) invoiceAttachmentMenuItem.getActionView();
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<InvoiceService.InvoiceAttachmentTypesEnum> adapter = new ArrayAdapter<>(requireActivity().getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, invoiceAttachmentTypes);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedInvoiceAttachmentType = invoiceAttachmentTypes.get(position);
                try {
                    loadInvoiceAttachment(invoiceId, selectedInvoiceAttachmentType);
                } catch (Exception e) {
                    // simply ignore, during startup this may be called before the view is ready.
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Log.d(Utility.buildTag(getClass(), "onCreateOptionsMenu"), "menu: " + menu);
    }

    private void loadInvoiceAttachment(Long invoiceId, InvoiceService.InvoiceAttachmentTypesEnum invoiceAttachmentType) {
        Log.d("loadInvoiceAttachment", String.format("%s %s", invoiceId, invoiceAttachmentType));
        WebView webView;
        try {
            webView = requireView().findViewById(R.id.invoice_web_view);
        } catch (Exception e) {
            // ignore, view is not ready yet
            Log.e("loadInvoiceAttachment", e.getMessage());
            return;
        }
        invoiceAttachmentTypes = invoiceService.getInvoiceAttachmentTypes();
        byte[] invoiceAttachmentHtml = null;
        try {
            invoiceAttachmentHtml = invoiceService.getInvoiceAttachment(invoiceId, invoiceAttachmentType.name(), "html").getAttachmentFileContent();
        } catch (Exception e) {
            showInfoDialog("Error", String.format("Application error!%sError: %s%s Please report.", e.getMessage(), System.lineSeparator(), System.lineSeparator()));
        }

        Log.d("", "html=" + new String(invoiceAttachmentHtml));
        webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        webView.getSettings().setJavaScriptEnabled(false);
        webView.getSettings().setLoadsImagesAutomatically(true);
    //    webView.setWebViewClient(new LocalContentWebViewClient());
        //webView.loadDataWithBaseURL(null, invoiceHtml, "text/html", "utf-8", null);
        Log.d("invoice timesheet attachment", String.format("%s", new String(invoiceAttachmentHtml)));
        webView.loadDataWithBaseURL("file://android_asset/", new String(invoiceAttachmentHtml), "text/html", "UTF-8", null);
    }

    private void returnToInvoiceList() {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_content_frame, InvoiceListFragment.class, null)
                .setReorderingAllowed(true)
                .commit();
    }

    private void exportAttachment(String invoiceAttachmentType, String fileName) {
        String invoiceAttachmentFileName = fileName + "_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        PrintManager printManager = (PrintManager) getActivity().getSystemService(Context.PRINT_SERVICE);
        WebView webView = requireView().findViewById(R.id.invoice_web_view);
        PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter(invoiceAttachmentFileName);

        PrintAttributes printAttributes = new PrintAttributes.Builder()
                .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                .setResolution(new PrintAttributes.Resolution("pdf", "pdf", 600, 600))
                .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                .build();

        PrintJob printJob = printManager.print(invoiceAttachmentFileName, printAdapter, printAttributes);
        Log.d("", "printJob status=" + printJob.isCompleted());
        java.io.File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM + "/PDFTest/");
/*
        byte[] invoiceAttachmentHtml = null;
        try {
            invoiceAttachmentHtml = invoiceService.getInvoiceAttachment(invoiceId, invoiceAttachmentType, "html").getAttachmentFileContent();
        } catch (Exception e) {
            showInfoDialog("Error", String.format("Application error!%sError: %s%s Please report.", e.getMessage(), System.lineSeparator(), System.lineSeparator()));
        }
        try {
            String invoiceAttachmentFileName = fileName + "_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
            PdfUtility.saveFile(new String(invoiceAttachmentHtml), PdfUtility.getLocalDir() + "/" + invoiceAttachmentFileName + ".pdf");
            showInfoDialog("Info", String.format("%s Exported to %s", selectedInvoiceAttachmentType, invoiceAttachmentFileName));
        } catch (Exception e) {
            showInfoDialog("Error", String.format("Export to pdf failed! file=%s, error=%s", fileName, e.getMessage()));
        }

 */
    }

    /**
     * content://com.android.providers.downloads.documents/document/downloads
     */
    private String readInvoiceFile(LocalDate invoiceDate) throws IOException {
        String invoiceFileName = "invoice_attachment_" + invoiceDate.format(DateTimeFormatter.ISO_DATE) + ".html";
        return PdfUtility.readFile(PdfUtility.getLocalDir() + "/" + invoiceFileName);
    }

    private void showInfoDialog(String severity, String message) {
        new MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
                .setTitle(severity)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok", (dialog, which) -> dialog.cancel())
                .create()
                .show();
    }
}
