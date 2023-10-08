package com.gunnarro.android.terex.ui.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.fragment.app.Fragment;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.gunnarro.android.terex.R;
import com.gunnarro.android.terex.domain.entity.Address;
import com.gunnarro.android.terex.domain.entity.Company;
import com.gunnarro.android.terex.domain.entity.Contact;
import com.gunnarro.android.terex.domain.entity.Invoice;
import com.gunnarro.android.terex.domain.entity.InvoiceSummary;
import com.gunnarro.android.terex.domain.entity.Person;
import com.gunnarro.android.terex.domain.entity.SpinnerItem;
import com.gunnarro.android.terex.domain.entity.Timesheet;
import com.gunnarro.android.terex.exception.TerexApplicationException;
import com.gunnarro.android.terex.repository.TimesheetRepository;
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
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class InvoiceNewFragment extends Fragment {

    private final static String INVOICE_TIMESHEET_SUMMARY_ATTACHMENT_TEMPLATE = "html/template/invoice-attachment.mustache";
    private final static String RECRUITMENT_TIMESHEET_TEMPLATE = "html/template/norway-consulting-timesheet-template.mustache";
    // @Inject
    private InvoiceService invoiceService;

    @Inject
    public InvoiceNewFragment() {
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
        View view = inflater.inflate(R.layout.fragment_invoice_new, container, false);
        List<Timesheet> timesheetList = invoiceService.getTimesheets(TimesheetRepository.TimesheetStatusEnum.OPEN.name());
        List<SpinnerItem> timesheetItems = timesheetList.stream().map(t -> new SpinnerItem(t.id, t.getProjectCode())).collect(Collectors.toList());
        final AutoCompleteTextView timesheetSpinner = view.findViewById(R.id.invoice_timesheet_spinner);
        ArrayAdapter<SpinnerItem> timesheetAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, timesheetItems);
        timesheetSpinner.setAdapter(timesheetAdapter);
        timesheetSpinner.setListSelection(1);

        view.findViewById(R.id.btn_invoice_create).setOnClickListener(v -> {
            try {
                String selectedTimesheet = timesheetSpinner.getText().toString();
                SpinnerItem item = timesheetItems.stream().filter(i -> i.name().equals(selectedTimesheet)).findFirst().orElse(null);
                if (item == null) {
                    showInfoDialog("Please select a timesheet!", requireContext());
                } else {
                    Long invoiceId = createInvoice(item.id());
                    createInvoiceSummaryAttachment(invoiceId);
                }
            } catch (TerexApplicationException e) {
                showInfoDialog("Error creating invoice!", requireContext());
            }
        });

        view.findViewById(R.id.btn_invoice_send_email).setOnClickListener(v -> {
            try {
                File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File pdfFile = new File(path.getPath() + "/invoice_attachment_" + LocalDate.now().format(DateTimeFormatter.ISO_DATE) + ".pdf");
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

    /**
     * Create a new invoice for the given timesheet.
     *
     * @param timesheetId to be created invoice for
     * @return id of the invoice
     */
    private Long createInvoice(Long timesheetId) {
        Long invoiceId = invoiceService.createInvoice(getCompany(), getClient(), timesheetId);
        if (invoiceId == null) {
            showInfoDialog("No timesheet found! timesheetId=" + timesheetId, requireContext());
        }
        return invoiceId;
    }

    private void createInvoiceSummaryAttachment(Long invoiceId) {
        try {
            Invoice invoice = invoiceService.getInvoice(invoiceId);
            Log.d("createInvoiceSummaryAttachment", "invoiceSummary week: " + invoice.getInvoiceSummaryList());
            StringBuilder mustacheTemplateStr = new StringBuilder();
            // first read the invoice summary mustache html template
            try (InputStream fis = requireContext().getAssets().open(INVOICE_TIMESHEET_SUMMARY_ATTACHMENT_TEMPLATE);
                 InputStreamReader isr = new InputStreamReader(fis,
                         StandardCharsets.UTF_8);
                 BufferedReader br = new BufferedReader(isr)) {
                br.lines().forEach(mustacheTemplateStr::append);
            }

            Double sumBilledAmount = invoice.getInvoiceSummaryList().stream()
                    .mapToDouble(InvoiceSummary::getSumBilledWork)
                    .sum();

            Double sumBilledHours = invoice.getInvoiceSummaryList().stream()
                    .mapToDouble(InvoiceSummary::getSumWorkedHours)
                    .sum();

            String invoiceSummaryHtml = createInvoiceSummaryHtml(mustacheTemplateStr.toString(), invoice.getInvoiceSummaryList(), sumBilledHours.toString(), sumBilledAmount.toString());
            Log.d("createInvoiceSummaryAttachment", "" + invoiceSummaryHtml);
            createPdf(invoiceSummaryHtml, "invoice_attachment");
            Log.d("createInvoiceSummaryAttachment", "" + invoiceSummaryHtml);
        } catch (Exception e) {
            throw new TerexApplicationException(String.format("Error crating invoice attachment, invoice ref=%s", invoiceId), "50023", e);
        }
    }

    private String createInvoiceSummaryHtml(String mustacheTemplateStr, List<InvoiceSummary> invoiceSummaryList, String sumBilledHours, String sumBilledAmount) {
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile(new StringReader(mustacheTemplateStr), "");
        Map<String, Object> context = new HashMap<>();
        context.put("title", "Vedlegg til faktura " + LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        context.put("company", getCompany());
        context.put("client", getClient());
        context.put("timesheetProjectCode", "techlead-catalystone-solution-as");
        context.put("invoiceSummaryList", invoiceSummaryList);
        context.put("sunBilledHours", sumBilledHours);
        context.put("sumBilledAmount", sumBilledAmount);
        StringWriter writer = new StringWriter();
        mustache.execute(writer, context);
        return writer.toString();
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
        showInfoDialog("created pdf: " + pdfFile.getPath(), requireContext());
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

    private Company getCompany() {
        Company company = new Company();
        company.setId(10L);
        company.setName("gunnarro:as");
        company.setOrganizationNumber("828 707 922");
        company.setBankAccountNumber("9230 26 98831");
        Address address = new Address();
        address.setStreetNumber("35");
        address.setStreetName("Stavangergata");
        address.setPostCode("0467");
        address.setCity("Oslo");
        address.setCountryCode("no");
        company.setBusinessAddress(address);
        return company;
    }

    private Company getClient() {
        Company client = new Company();
        client.setId(20L);
        client.setName("Norway Consulting AS");
        client.setOrganizationNumber("");
        Address address = new Address();
        address.setStreetNumber("16");
        address.setStreetName("Grensen");
        address.setPostCode("0159");
        address.setCity("Oslo");
        address.setCountryCode("no");
        client.setBusinessAddress(address);
        Person contactPerson = new Person();
        contactPerson.setFirstName("Anita");
        contactPerson.setLastName("Lundtveit");
        client.setContactPerson(contactPerson);

        Contact contactInfo = new Contact();
        client.setContactInfo(contactInfo);
        return client;
    }
}
