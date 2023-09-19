package com.gunnarro.android.terex.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.gunnarro.android.terex.R;
import com.gunnarro.android.terex.domain.entity.InvoiceSummary;
import com.gunnarro.android.terex.domain.entity.Timesheet;
import com.gunnarro.android.terex.service.InvoiceService;
import com.gunnarro.android.terex.utility.Utility;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
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
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AdminFragment extends Fragment {

    // @Inject
    InvoiceService invoiceService;

    @Inject
    public AdminFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        invoiceService = new InvoiceService(getActivity().getApplicationContext());
        Log.d(Utility.buildTag(getClass(), "onCreate"), "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin, container, false);
        view.findViewById(R.id.btn_admin_generate_timesheet).setOnClickListener(v -> {
            try {
                //generateTimesheet();
                createTimesheetAttachment();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        view.findViewById(R.id.btn_admin_generate_invoice).setOnClickListener(v -> {
            try {
                //  createInvoice();
                createInvoiceSummaryAttachment();
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

    private void generateTimesheet() {
        int month = ((Spinner) requireView().findViewById(R.id.admin_invoice_month_spinner)).getSelectedItemPosition();
        List<Timesheet> timesheets = invoiceService.generateTimesheet(2023, month + 1);
        timesheets.forEach(t -> Log.i("generateTimesheet", t.toString()));
        Toast.makeText(requireContext(), "Generate timesheet for " + month, Toast.LENGTH_SHORT).show();
    }

    private void createInvoice() {
        int month = ((Spinner) requireView().findViewById(R.id.admin_invoice_month_spinner)).getSelectedItemPosition();
        Toast.makeText(requireContext(), "Create invoice for " + month, Toast.LENGTH_SHORT).show();
        List<InvoiceSummary> invoiceSummaries = invoiceService.buildInvoiceSummaryByWeek(2022, month);
        invoiceSummaries.forEach(i -> Log.i("generateInvoiceSummary", i.toString()));
    }

    private void createInvoiceSummaryAttachment() throws IOException {
        List<InvoiceSummary> invoiceSummaries = invoiceService.createInvoiceSummaryForMonth("Norway Consulting AS", "catalystOne monolith", 9);
        Log.d("createInvoiceSummaryAttachment", "invoiceSummary week: " + invoiceSummaries);
        StringBuilder mustacheTemplateStr = new StringBuilder();
        // first read the invoice summary mustache html template
        try (InputStream fis = requireContext().getAssets().open("template/invoice-timesheet-summary-attachment.mustache");
             InputStreamReader isr = new InputStreamReader(fis,
                     StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(isr)) {
            br.lines().forEach(mustacheTemplateStr::append);
        }


        Double sumBilledAmount = invoiceSummaries.stream()
                .mapToDouble(InvoiceSummary::getSumBilledWork)
                .sum();

        Double sumBilledHours = invoiceSummaries.stream()
                .mapToDouble(InvoiceSummary::getSumWorkedMinutes)
                .sum()/60;

        String invoiceSummaryHtml = createInvoiceSummaryHtml(mustacheTemplateStr.toString(), invoiceSummaries, sumBilledHours.toString(), sumBilledAmount.toString());

        WebView webView = requireView().findViewById(R.id.invoice_overview_html);
        webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        webView.loadDataWithBaseURL(null, invoiceSummaryHtml, "text/html", "utf-8", null);

        Log.d("createInvoiceSummaryAttachment", "" + invoiceSummaryHtml);
        // thereafter convert the html to pdf
        //  PdfUtility.htmlToPdf(invoiceSummaryHtml, requireContext().getFilesDir() + "/invoice-timesheet-summary.pdf");
    }

    private void createTimesheetAttachment() throws IOException {
        List<Timesheet> timesheets = invoiceService.getTimesheetForMonth("Norway Consulting AS", "catalystOne monolith", 9);

        StringBuilder mustacheTemplateStr = new StringBuilder();
        // first read the invoice summary mustache html template
        try (InputStream fis = requireContext().getAssets().open("template/norway-consulting-timesheet-template.mustache");
             InputStreamReader isr = new InputStreamReader(fis,
                     StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(isr)) {
            br.lines().forEach(mustacheTemplateStr::append);
        }


        Double sumBilledHours = timesheets.stream()
                .mapToDouble(Timesheet::getWorkedMinutes)
                .sum()/60;

        String timesheetHtml = createTimesheetListHtml(mustacheTemplateStr.toString(), timesheets, sumBilledHours.toString());

        WebView webView = requireView().findViewById(R.id.invoice_overview_html);
        webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        webView.loadDataWithBaseURL(null, timesheetHtml, "text/html", "utf-8", null);

        Log.d("createTimesheetAttachment", "" + timesheetHtml);
        // thereafter convert the html to pdf
        //  PdfUtility.htmlToPdf(invoiceSummaryHtml, requireContext().getFilesDir() + "/invoice-timesheet-summary.pdf");
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

    private String createTimesheetListHtml(String mustacheTemplateStr, List<Timesheet> timesheetList, String sumBilledHours) {
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile(new StringReader(mustacheTemplateStr), "");
        Map<String, Object> context = new HashMap<>();
        context.put("title", "Timeliste for konsulentbistan");
        context.put("timesheetPeriod", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM")));
        context.put("timesheetList", timesheetList);
        context.put("sunBilledHours", sumBilledHours);
        StringWriter writer = new StringWriter();
        mustache.execute(writer, context);
        return writer.toString();
    }
}
