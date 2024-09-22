package com.gunnarro.android.terex.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import androidx.core.content.FileProvider;

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
        /*
        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{toEmailAddress});
        email.putExtra(Intent.EXTRA_SUBJECT, subject);
        email.putExtra(Intent.EXTRA_TEXT, message);
        email.putExtra(Intent.EXTRA_STREAM, pdfFile);
        // need this to prompts email client only
        email.setType("message/rfc822");
        startActivity(Intent.createChooser(email, "Choose Email client:"));
        */
        //File file = new File("/Downloads/omegapoint_norge_as_timeliste_2024-01.pdf");
       // File file = readFile("Downloads/omegapoint_norge_as_timeliste_2024-01.pdf");
       // Uri fileUri = FileProvider.getUriForFile(requireContext(), requireContext().getApplicationContext().getPackageName() + ".provider", file);
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("vnd.android.cursor.dir/email");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{toEmailAddress});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);
      //  emailIntent.setDataAndType(fileUri, "text/*");
      //  emailIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
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
}
