package com.gunnarro.android.terex.service;

import android.content.Context;
import android.util.Log;

import androidx.room.Transaction;

import com.gunnarro.android.terex.domain.entity.Company;
import com.gunnarro.android.terex.domain.entity.Invoice;
import com.gunnarro.android.terex.domain.entity.InvoiceAttachment;
import com.gunnarro.android.terex.domain.entity.TimesheetSummary;
import com.gunnarro.android.terex.exception.TerexApplicationException;
import com.gunnarro.android.terex.repository.InvoiceRepository;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import kotlin.random.Random;

@Singleton
public class InvoiceService {

    public enum InvoiceAttachmentTypesEnum {
        CLIENT_TIMESHEET("html/template/norway-consulting-timesheet.mustache"), TIMESHEET_SUMMARY("html/template/invoice-timesheet-attachment.mustache");

        private final String template;
        InvoiceAttachmentTypesEnum(String template) {
            this.template = template;
        }
        public String getTemplate() {
            return template;
        }

        public String getPdfFileName() {
            return template.split("/")[2].replace("mustache", "pdf");
        }
    }

    private final TimesheetService timesheetService;
    private final InvoiceRepository invoiceRepository;


    /**
     * default constructor
     */
    @Inject
    public InvoiceService(Context applicationContext) {
        timesheetService = new TimesheetService(applicationContext);
        invoiceRepository = new InvoiceRepository(applicationContext);
    }

    public Invoice getInvoice(Long invoiceId) {
        return invoiceRepository.getInvoice(invoiceId);
    }

    public Long saveInvoiceAttachment(InvoiceAttachment invoiceAttachment) {
        InvoiceAttachment invoiceAttachmentExisting = invoiceRepository.findInvoiceAttachment(invoiceAttachment.getInvoiceId(), invoiceAttachment.getAttachmentType(), invoiceAttachment.getAttachmentFileName(), invoiceAttachment.getAttachmentFileType());
        if (invoiceAttachmentExisting != null) {
            throw new TerexApplicationException(String.format("Attachment already exist. id=%s, type=%s, file=%s, type=%s", invoiceAttachment.getId(), invoiceAttachment.getAttachmentType(), invoiceAttachment.getAttachmentFileName(), invoiceAttachment.getAttachmentFileType()), "50050", null);
        }
        return invoiceRepository.saveInvoiceAttachment(invoiceAttachment);
    }

    public InvoiceAttachment getInvoiceAttachment(Long invoiceId, String attachmentType, String invoiceFileType) {
        return invoiceRepository.getInvoiceAttachment(invoiceId, attachmentType, invoiceFileType);
    }

    @Transaction
    public Long createInvoice(Company company, Company client, Long timesheetId) {
        // first accumulate timesheet entries
        List<TimesheetSummary> timesheetSummaries = timesheetService.createTimesheetSummary(timesheetId);
        // save the invoice summaries
        timesheetSummaries.forEach(i -> {
            timesheetService.saveTimesheetSummary(i);
            Log.d("saved timesheet summary", "" + i);
        });
        // there after create the invoice
        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(Random.Default.nextInt(100, 10000));
        invoice.setTimesheetId(timesheetId);
        invoice.setClientId(client.getId());
        // ensure that a timesheet is only billed once.
        invoice.setReference(String.format("%s-%s", client.getName(), timesheetId));
        invoice.setStatus(InvoiceRepository.InvoiceStatusEnum.NEW.name());
        // The date when the customer is billed
        invoice.setBillingDate(LocalDate.now());
        invoice.setInvoicePeriod("month");
        invoice.setBillingPeriodStartDate(null);
        invoice.setBillingPeriodEndDate(null);
        // due date is defaulted to 10 days after billing date
        invoice.setDueDate(invoice.getBillingDate().plusDays(10));
        double sumAmount = timesheetSummaries.stream().mapToDouble(TimesheetSummary::getTotalBilledAmount).sum();
        invoice.setAmount(sumAmount);
        invoice.setCurrency("NOK");
        invoice.setStatus(InvoiceRepository.InvoiceStatusEnum.COMPLETED.name());
        return invoiceRepository.saveInvoice(invoice);
    }

    public void saveInvoice(@NotNull Invoice invoice) {
        invoiceRepository.saveInvoice(invoice);
    }

    public List<InvoiceAttachmentTypesEnum> getInvoiceAttachmentTypes() {
        return Arrays.asList(InvoiceAttachmentTypesEnum.values());
    }

}
