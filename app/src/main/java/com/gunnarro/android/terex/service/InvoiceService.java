package com.gunnarro.android.terex.service;

import androidx.room.Transaction;

import com.gunnarro.android.terex.domain.dto.TimesheetSummaryDto;
import com.gunnarro.android.terex.domain.entity.Invoice;
import com.gunnarro.android.terex.domain.entity.InvoiceAttachment;
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
        CLIENT_TIMESHEET("template/html/norway-consulting-timesheet.mustache"),
        TIMESHEET_SUMMARY_2("template/html/timesheet-summary.mustache"),
        TIMESHEET_SUMMARY("template/html/invoice-timesheet-attachment.mustache");

        private final String template;

        InvoiceAttachmentTypesEnum(String template) {
            this.template = template;
        }

        public String getTemplate() {
            return template;
        }

        public String getFileName() {
            return template.split("/")[2].replace(".mustache", "");
        }
    }

    private final TimesheetService timesheetService;
    private final ClientService clientService;
    private final InvoiceRepository invoiceRepository;

    /**
     * For unit test onlu
     */
    public InvoiceService(InvoiceRepository invoiceRepository, TimesheetService timesheetService, ClientService clientSerice) {
        this.timesheetService = timesheetService;
        this.invoiceRepository = invoiceRepository;
        this.clientService = clientSerice;
    }

    /**
     * default constructor
     */
    @Inject
    public InvoiceService() {
        this(new InvoiceRepository(), new TimesheetService(), new ClientService());
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
    public Long createInvoice(@NotNull Long invoiceIssuerId, @NotNull Long clientId, @NotNull Long timesheetId) {
        // first accumulate timesheet entries
        List<TimesheetSummaryDto> timesheetSummaryDtoList = timesheetService.createTimesheetSummaryForBilling(timesheetId);
        // there after create the invoice
        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(generateInvoiceNumber());
        invoice.setTimesheetId(timesheetId);
        invoice.setClientId(clientId);
        invoice.setInvoiceRecipientId(clientId);
        invoice.setInvoiceIssuerId(invoiceIssuerId);
        // ensure that a timesheet is only billed once.
        //invoice.setReference(String.format("%s-%s", client.getName(), timesheetId));
        invoice.setStatus(InvoiceRepository.InvoiceStatusEnum.NEW.name());
        // The date when the customer is billed
        invoice.setBillingDate(LocalDate.now());
        invoice.setInvoicePeriod(Invoice.InvoicePeriodEnum.MONTH.name());
        invoice.setBillingPeriodStartDate(null);
        invoice.setBillingPeriodEndDate(null);
        // due date is defaulted to 10 days after billing date
        invoice.setDueDate(invoice.getBillingDate().plusDays(10));
        //fixme do this in timesheet service
        double sumAmount = timesheetSummaryDtoList.stream().mapToDouble(TimesheetSummaryDto::getBilledAmount).sum();
        invoice.setAmount(sumAmount);
        invoice.setCurrency("NOK");
        invoice.setStatus(InvoiceRepository.InvoiceStatusEnum.COMPLETED.name());
        return invoiceRepository.saveInvoice(invoice);
    }

    public List<InvoiceAttachmentTypesEnum> getInvoiceAttachmentTypes() {
        return Arrays.asList(InvoiceAttachmentTypesEnum.values());
    }

    private int generateInvoiceNumber() {
        return Random.Default.nextInt(100, 10000);
    }
}
