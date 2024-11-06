package com.gunnarro.android.terex.ui.fragment;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.provider.DocumentsContract;
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

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;

import com.gunnarro.android.terex.R;
import com.gunnarro.android.terex.domain.dto.InvoiceDto;
import com.gunnarro.android.terex.domain.entity.InvoiceAttachment;
import com.gunnarro.android.terex.exception.TerexApplicationException;
import com.gunnarro.android.terex.service.InvoiceService;
import com.gunnarro.android.terex.ui.MainActivity;
import com.gunnarro.android.terex.utility.Utility;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class InvoiceDetailsFragment extends BaseFragment {

    private static final String ACCOUNTANT_EMAIL_ADDRESS = "post@regnskapsservice1.no";
    private final InvoiceService invoiceService;
    private List<InvoiceService.InvoiceAttachmentTypesEnum> invoiceAttachmentTypes;
    private InvoiceService.InvoiceAttachmentTypesEnum selectedInvoiceAttachmentType = InvoiceService.InvoiceAttachmentTypesEnum.TIMESHEET_SUMMARY;
    private Long invoiceId;
    private InvoiceAttachment selectedInvoiceAttachment;

    @Inject
    public InvoiceDetailsFragment() {
        // Needed by dagger framework
        invoiceService = new InvoiceService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        requireActivity().setTitle(R.string.title_invoice_attachment);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_invoice_details, container, false);
        setHasOptionsMenu(true);
        invoiceId = getArguments().getLong(InvoiceListFragment.INVOICE_ID_KEY);
        Log.d(Utility.buildTag(getClass(), "onCreateView"), String.format("invoiceId=%s", invoiceId));
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
            exportAttachment(selectedInvoiceAttachment.getFileName());
        } else if (item.getItemId() == R.id.send_email) {
            InvoiceDto invoiceDto = invoiceService.getInvoiceDto(invoiceId);
            String billingPeriod = invoiceDto.getBillingPeriodStartDate().format(DateTimeFormatter.ofPattern(Utility.MONTH_YEAR_DATE_PATTERN));
            String emailSubject = String.format("Fakturavedlegg %s", billingPeriod);
            String emailMessage = String.format("Hei,\nHer kommer vedlegg til faktura for %s \n\nFaktura skal sendes per epost til:\n%s\n\nHilsen\nGunnar Rønneberg\ngunnarro as", billingPeriod, invoiceDto.getInvoiceRecipient().getInvoiceEmailAddress());
            sendInvoiceToClient(ACCOUNTANT_EMAIL_ADDRESS, emailSubject, emailMessage, readFile(selectedInvoiceAttachmentType.getFileName()));
        }
        return true;
    }

    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        // clear current menu items
        menu.clear();
        // set fragment specific menu items
        inflater.inflate(R.menu.invoice_attachment_menu, menu);
        // MenuItem pdfMenuItem = menu.findItem(R.id.attachment_to_pdf);
        // Objects.requireNonNull(m.getActionView()).setOnClickListener(v -> exportAttachment());
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
        Log.d("loadInvoiceAttachment", String.format("invoiceId=%s, attachmentType=%s", invoiceId, invoiceAttachmentType));
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
            selectedInvoiceAttachment = invoiceService.getInvoiceAttachment(invoiceId, invoiceAttachmentType, InvoiceService.InvoiceAttachmentFileTypes.HTML);
            invoiceAttachmentHtml = selectedInvoiceAttachment.getFileContent();
        } catch (Exception e) {
            showInfoDialog("Error", String.format("Application error!%sError: %s%s Please report.", e.getMessage(), System.lineSeparator(), System.lineSeparator()));
        }

        webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        webView.getSettings().setJavaScriptEnabled(false);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.loadDataWithBaseURL("file:///android_asset/", new String(invoiceAttachmentHtml), "text/html", "UTF-8", null);
    }

    private void exportAttachment(String fileName) {
        PrintManager printManager = (PrintManager) requireActivity().getSystemService(Context.PRINT_SERVICE);
        WebView webView = requireView().findViewById(R.id.invoice_web_view);
        PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter(fileName);

        PrintAttributes printAttributes = new PrintAttributes.Builder()
                .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                .setResolution(new PrintAttributes.Resolution("pdf", "pdf", 600, 600))
                .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                .build();

        PrintJob printJob = printManager.print(fileName, printAdapter, printAttributes);
        Log.d("exportAttachment", "printJob status=" + printJob.isCompleted() + ", file:" + fileName);
    }

    private void sendInvoiceToClient(String toEmailAddress, String subject, String message, File pdfFile) {

        File root = Environment.getExternalStorageDirectory();
/*        Log.d("sendInvoiceToClient", "external storage path: " + root.getPath());
        Log.d("sendInvoiceToClient", "pdf file path: " + pdfFile.getPath());
        File file = new File("Downloads/omegapoint_norge_as_timeliste_2024-01.pdf");
    //    openFile(Uri.fromFile(pdfFile));
        if (!file.exists() || !file.canRead()) {
            showInfoDialog("ERROR", "Attachment not found! " + file.getPath());
            return;
        }
*/

       // File file = readFile("Downloads/omegapoint_norge_as_timeliste_2024-01.pdf");
       // Uri fileUri = FileProvider.getUriForFile(requireContext(), requireContext().getApplicationContext().getPackageName() + ".provider", file);
        Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        emailIntent.setType("vnd.android.cursor.dir/email");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{toEmailAddress});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, new ArrayList<>(List.of((message))));
        // A Uri pointing to the attachment. If using the ACTION_SEND_MULTIPLE action, this instead is an ArrayList containing multiple Uri objects.
        //Uri attachment = Uri.fromFile(file);

        //ContentUris.withAppendedId()
        content://com.android.providers.downloads.documents/document/92
        Log.d("sendInvoiceToClient", "pdffile uri: " + Uri.fromParts("content","com.android.providers.downloads.documents",null).toString());
        Log.d("sendInvoiceToClient", "root: " + root.toURI());

        //Log.d("sendInvoiceToClient", "attachment uri:" + attachment.getPath());
        // emailIntent.putExtra(Intent.EXTRA_STREAM, new ArrayList<>(List.of(Uri.fromParts("content", "com.android.providers.downloads.documents", null))));
        emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(emailIntent, "Choose Email client:"));
    }

    private File readFile(String fileName) {
        try {
            return new File(MainActivity.getInternalAppDir(), fileName);
        } catch (Exception e) {
            throw new TerexApplicationException(e.getMessage(), "50500", e);
        }
    }
/*
    // read and write file to , open a file picker dialog
    private static final int CREATE_FILE = 1;
    private static final int READ_REQUEST_PDF_FILE_CODE = 200;

    private void createFile(Uri pickerInitialUri) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_TITLE, "invoice.pdf");

        // Optionally, specify a URI for the directory that should be opened in
        // the system file picker when your app creates the document.
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);
        //ActivityResultContract.
        startActivityForResult(intent, CREATE_FILE);
    }

    private void openFile(Uri pickerInitialUri) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        // Optionally, specify a URI for the file that should appear in the
        // system file picker when it loads.
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);
        startActivityForResult(intent, READ_REQUEST_PDF_FILE_CODE);
    }

 */
/*
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == READ_REQUEST_PDF_FILE_CODE && resultCode == Activity.RESULT_OK) {
            // The result data contains a URI for the document or directory that
            // the user selected.
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                // Perform operations on the document using its URI.
            }
            Log.d("onActivityResult", "read file path: " + uri);
        }
    }

 */
}
