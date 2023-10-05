package com.gunnarro.android.terex.ui.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.print.PdfPrint;
import android.print.PrintAttributes;
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
import com.gunnarro.android.terex.domain.entity.Company;
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
                createInvoiceSummaryAttachment(1L);
                //  sendInvoiceToClient("gunnar_ronneberg@yahoo.no", "test", "message", null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        view.findViewById(R.id.btn_invoice_attachment_send_email).setOnClickListener(v -> {
            try {
                File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File pdfFile = new File(path.getPath() +  "/invoice_attachment_" + LocalDate.now().format(DateTimeFormatter.ISO_DATE) + ".pdf");
                sendInvoiceToClient("gunnar_ronneberg@yahoo.no", "test", "message", pdfFile);
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
    private void createInvoiceSummaryAttachment(Long timesheetId) throws IOException {
        List<InvoiceSummary> invoiceSummaries = invoiceService.createInvoiceSummary(timesheetId);
        Log.d("createInvoiceSummaryAttachment", "invoiceSummary week: " + invoiceSummaries);
        StringBuilder mustacheTemplateStr = new StringBuilder();
        // first read the invoice summary mustache html template
        try (InputStream fis = requireContext().getAssets().open(INVOICE_TIMESHEET_SUMMARY_ATTACHMENT_TEMPLATE);
             InputStreamReader isr = new InputStreamReader(fis,
                     StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(isr)) {
            br.lines().forEach(mustacheTemplateStr::append);
        }

        Double sumBilledAmount = invoiceSummaries.stream()
                .mapToDouble(InvoiceSummary::getSumBilledWork)
                .sum();

        Double sumBilledHours = invoiceSummaries.stream()
                .mapToDouble(InvoiceSummary::getSumWorkedHours)
                .sum();

        String invoiceSummaryHtml = createInvoiceSummaryHtml(mustacheTemplateStr.toString(), invoiceSummaries, sumBilledHours.toString(), sumBilledAmount.toString());
        // need some time in order to load date before printed.
        WebView webViewPrint = new WebView(requireContext());
        webViewPrint.loadDataWithBaseURL(null, invoiceSummaryHtml, "text/html", "utf-8", null);

        WebView webView = requireView().findViewById(R.id.invoice_overview_html);
        webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        webView.loadDataWithBaseURL("file:///android_asset/", invoiceSummaryHtml, "text/html", "utf-8", null);

        Log.d("createInvoiceSummaryAttachment", "" + invoiceSummaryHtml);
        createPdf(invoiceSummaryHtml, "invoice_attachment");
        Log.d("createInvoiceSummaryAttachment", "" + invoiceSummaryHtml);
    }

    private void createTimesheetAttachment(Long timesheetId) throws IOException {
        List<TimesheetEntry> timesheetEntryList = invoiceService.getTimesheetEntryList(timesheetId);

        StringBuilder mustacheTemplateStr = new StringBuilder();
        // first read the invoice summary mustache html template
        try (InputStream fis = requireContext().getAssets().open(RECRUITMENT_TIMESHEET_TEMPLATE);
             InputStreamReader isr = new InputStreamReader(fis,
                     StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(isr)) {
            br.lines().forEach(mustacheTemplateStr::append);
        }

        Double sumBilledHours = timesheetEntryList.stream()
                .mapToDouble(TimesheetEntry::getWorkedMinutes)
                .sum() / 60;

        String timesheetHtml = createTimesheetListHtml(mustacheTemplateStr.toString(), timesheetEntryList, sumBilledHours.toString());
        // need some time in order to load date before printed.
        WebView webViewPrint = new WebView(requireContext());
        webViewPrint.loadDataWithBaseURL(null, timesheetHtml, "text/html", "utf-8", null);

        WebView webView = requireView().findViewById(R.id.invoice_overview_html);
        webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        webView.loadDataWithBaseURL(null, timesheetHtml, "text/html", "utf-8", null);

        Log.d("createTimesheetAttachment", "" + timesheetHtml);
        createPdf(timesheetHtml, "invoice-attachment-new");
    }

    private String createInvoiceSummaryHtml(String mustacheTemplateStr, List<InvoiceSummary> invoiceSummaryList, String sumBilledHours, String sumBilledAmount) {
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile(new StringReader(mustacheTemplateStr), "");
        Map<String, Object> context = new HashMap<>();
        context.put("title", "Vedlegg til faktura " + LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        context.put("invoiceSummaryList", invoiceSummaryList);
        context.put("sunBilledHours", sumBilledHours);
        context.put("sumBilledAmount", sumBilledAmount);
        StringWriter writer = new StringWriter();
        mustache.execute(writer, context);
        return writer.toString();
    }

    private String createTimesheetListHtml(String mustacheTemplateStr, List<TimesheetEntry> timesheetList, String sumBilledHours) {
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile(new StringReader(mustacheTemplateStr), "");
        Map<String, Object> context = new HashMap<>();
        context.put("title", "Timeliste for konsulentbistand");
        context.put("timesheetPeriod", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM")));
        context.put("timesheetList", timesheetList);
        context.put("sunWorkDays", timesheetList.size());
        context.put("sunBilledHours", sumBilledHours);
        StringWriter writer = new StringWriter();
        mustache.execute(writer, context);
        return writer.toString();
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

    private File readPdfFile(String fileName) {
        return new File(String.format("%s/%s", requireContext().getFilesDir().getPath(), fileName));
    }

    private void sendInvoiceToClient(String toEmailAddress, String subject, String message, File pdfFile) {
        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{toEmailAddress});
        email.putExtra(Intent.EXTRA_SUBJECT, subject);
        email.putExtra(Intent.EXTRA_TEXT, message);
        email.putExtra(Intent.EXTRA_STREAM, pdfFile);
        //need this to prompts email client only
        email.setType("message/rfc822");
        startActivity(Intent.createChooser(email, "Choose Email client:"));
    }

    private Company getClient() {
        return null;
    }

    private Company getMyCompany() {
        return null;
    }

    private void openFile(Uri pickerInitialUri) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");

        // Optionally, specify a URI for the file that should appear in the
        // system file picker when it loads.
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);
    }

    public void createPdf(String html, String fileName) throws IOException {

        String pdfFileName = fileName + "_" + LocalDate.now().format(DateTimeFormatter.ISO_DATE) + ".pdf";
        String htmlFileName = fileName + "_" + LocalDate.now().format(DateTimeFormatter.ISO_DATE) + ".html";
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File pdfFile = new File(path, pdfFileName);
        File htmlFile = new File(path, htmlFileName);
        try {
            FileOutputStream fos = new FileOutputStream(htmlFile);
            fos.write(html.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // save css to disk


        // save logo to disk


        ConverterProperties converterProperties = new ConverterProperties();
        converterProperties.setBaseUri("file:///" + path.getPath());
       // converterProperties.setBaseUri(".");
     /*   FontProvider fontProvider  = new FontProvider();
        fontProvider.addFont("/path/to/my-font.ttf");
        fontProvider.addStandardPdfFonts();
        fontProvider.addSystemFonts(); //for fallback
        converterProperties.setFontProvider(fontProvider);
        HtmlConverter.convertToPdf(new File("./input.html"), new File("output.pdf"), converterProperties);
  */
        HtmlConverter.convertToPdf(htmlFile, pdfFile, converterProperties);
      //  HtmlConverter.convertToPdf(html, new FileOutputStream(pdfFile));

    }
}
