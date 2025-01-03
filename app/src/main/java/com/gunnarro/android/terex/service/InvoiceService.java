package com.gunnarro.android.terex.service;

import android.util.Log;

import androidx.room.Transaction;

import com.gunnarro.android.terex.domain.dto.ClientDto;
import com.gunnarro.android.terex.domain.dto.InvoiceDto;
import com.gunnarro.android.terex.domain.dto.TimesheetDto;
import com.gunnarro.android.terex.domain.dto.TimesheetSummaryDto;
import com.gunnarro.android.terex.domain.dto.UserAccountDto;
import com.gunnarro.android.terex.domain.entity.Invoice;
import com.gunnarro.android.terex.domain.entity.InvoiceAttachment;
import com.gunnarro.android.terex.domain.mapper.TimesheetMapper;
import com.gunnarro.android.terex.exception.TerexApplicationException;
import com.gunnarro.android.terex.repository.InvoiceRepository;

import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import kotlin.random.Random;

@Singleton
public class InvoiceService {

    private static final int VAT_PERCENTAGE = 25;
    private static final int DUE_DATE_DAYS = 30;
    private static final String CURRENCY = "NOK";

    public enum InvoiceAttachmentTypesEnum {
        CLIENT_TIMESHEET("template/html/default-client-timesheet.mustache"),
        TIMESHEET_SUMMARY("template/html/invoice-timesheet-summary-attachment.mustache");

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

    public enum InvoiceAttachmentFileTypes {
        PDF, HTML
    }

    private final TimesheetService timesheetService;
    private final UserAccountService userAccountService;
    private final ClientService clientService;
    private final InvoiceRepository invoiceRepository;

    /**
     * For unit test onlu
     */
    public InvoiceService(InvoiceRepository invoiceRepository, TimesheetService timesheetService, ClientService clientService, UserAccountService userAccountService) {
        this.invoiceRepository = invoiceRepository;
        this.timesheetService = timesheetService;
        this.clientService = clientService;
        this.userAccountService = userAccountService;
    }

    /**
     * default constructor
     */
    @Inject
    public InvoiceService() {
        this(new InvoiceRepository(), new TimesheetService(), new ClientService(), new UserAccountService());
    }

    public List<Invoice> getAllInvoices() {
        return invoiceRepository.getAllInvoices();
    }

    public List<InvoiceDto> getInvoiceDtoList() {
        List<Long> invoceIdList = invoiceRepository.getAllInvoiceIds();
        return invoceIdList.stream().map(this::getInvoiceDto).sorted(Comparator.comparing(InvoiceDto::getBillingDate).reversed()).collect(Collectors.toList());
    }

    public InvoiceDto getInvoiceDto(Long invoiceId) {
        Invoice invoice = invoiceRepository.getInvoice(invoiceId);
        InvoiceDto invoiceDto = TimesheetMapper.toInvoiceDto(invoice);
        if (invoiceDto != null) {
            invoiceDto.setInvoiceIssuer(userAccountService.getUserAccount(invoice.getIssuerId()));
            invoiceDto.setInvoiceRecipient(clientService.getClient(invoice.getRecipientId()));
        }
        return invoiceDto;
    }

    public Invoice getInvoice(Long invoiceId) {
        return invoiceRepository.getInvoice(invoiceId);
    }

    public Long saveInvoiceAttachment(InvoiceAttachment invoiceAttachment) {
        InvoiceAttachment invoiceAttachmentExisting = invoiceRepository.findInvoiceAttachment(invoiceAttachment.getInvoiceId(), invoiceAttachment.getType(), invoiceAttachment.getFileName(), invoiceAttachment.getFileType());
        if (invoiceAttachmentExisting != null) {
            throw new TerexApplicationException(String.format("Attachment already exist. id=%s, type=%s, file=%s, type=%s", invoiceAttachment.getId(), invoiceAttachment.getType(), invoiceAttachment.getFileName(), invoiceAttachment.getFileType()), "50050", null);
        }
        return invoiceRepository.saveInvoiceAttachment(invoiceAttachment);
    }

    public InvoiceAttachment getInvoiceAttachment(Long invoiceId, InvoiceAttachmentTypesEnum attachmentType, InvoiceAttachmentFileTypes invoiceFileType) {
        return invoiceRepository.getInvoiceAttachment(invoiceId, attachmentType.name(), invoiceFileType.name());
    }

    @Transaction
    public Long createInvoice(@NotNull Long invoiceIssuerId, @NotNull Long clientId, @NotNull Long timesheetId, @NotNull Integer hourlyRate) {
        TimesheetDto timesheetDto = timesheetService.getTimesheetDto(timesheetId);
        // first accumulate timesheet entries
        List<TimesheetSummaryDto> timesheetSummaryDtoList = timesheetService.createTimesheetSummaryForBilling(timesheetId);
        // there after create the invoice
        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(generateInvoiceNumber());
        invoice.setTimesheetId(timesheetId);
        invoice.setRecipientId(clientId);
        invoice.setIssuerId(invoiceIssuerId);
        // ensure that a timesheet is only billed once.
        invoice.setStatus(InvoiceRepository.InvoiceStatusEnum.NEW.name());
        // The date when the customer is billed
        invoice.setBillingDate(LocalDate.now());
        invoice.setInvoicePeriod(Invoice.InvoicePeriodEnum.MONTH.name());
        invoice.setBillingPeriodStartDate(timesheetDto.getFromDate());
        invoice.setBillingPeriodEndDate(timesheetDto.getToDate());
        // due date is defaulted to 10 days after billing date
        invoice.setDueDate(invoice.getBillingDate().plusDays(DUE_DATE_DAYS));
        // do the billing part, i.e calculate the billing amount.
        double sumAmount = timesheetSummaryDtoList.stream().mapToDouble(TimesheetSummaryDto::getBilledAmount).sum();
        double sumVat = sumAmount * ((double) VAT_PERCENTAGE / 100);
        invoice.setVatPercent(VAT_PERCENTAGE);
        invoice.setAmount(sumAmount);
        invoice.setVatAmount(sumVat);
        invoice.setCurrency(CURRENCY);
        invoice.setStatus(InvoiceRepository.InvoiceStatusEnum.COMPLETED.name());
        return invoiceRepository.saveInvoice(invoice);
    }

    public void deleteInvoice(Long invoiceId) {
        Invoice invoice = invoiceRepository.getInvoice(invoiceId);
        // first, cleanup all invoice relations
        timesheetService.deleteTimesheetSummaryForBilling(invoice.getTimesheetId());
        invoiceRepository.deleteInvoiceAttachments(invoiceId);
        // finally, the invoice can be deleted
        invoiceRepository.deleteInvoice(invoice);
        Log.d("deleteInvoice", "deleted invoice, invoiceId=" + invoiceId);
    }

    public List<InvoiceAttachmentTypesEnum> getInvoiceAttachmentTypes() {
        return Arrays.asList(InvoiceAttachmentTypesEnum.values());
    }

    public long createTimesheetSummaryAttachment(Long invoiceId, String timesheetSummaryTemplate) {
        try {
            Invoice invoice = getInvoice(invoiceId);
            Log.d("createTimesheetSummaryAttachment", "timesheetId=" + invoice.getTimesheetId());
            UserAccountDto invoiceIssuer = userAccountService.getUserAccount(invoice.getIssuerId());
            ClientDto invoiceReceiver = clientService.getClient(invoice.getRecipientId());

            String invoiceSummaryHtml = timesheetService.createTimesheetSummaryAttachmentHtml(invoice.getTimesheetId(), invoiceIssuer, invoiceReceiver, timesheetSummaryTemplate);
            Log.d("createInvoiceSummaryAttachment", invoiceSummaryHtml);
            String invoiceAttachmentFileName = String.format("%s_fakturerte_timer_%s", invoiceReceiver.getName().replace(" ", "_").toLowerCase(), invoice.getBillingPeriodStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM")));

            InvoiceAttachment timesheetSummaryAttachment = new InvoiceAttachment();
            timesheetSummaryAttachment.setInvoiceId(invoiceId);
            timesheetSummaryAttachment.setType(InvoiceService.InvoiceAttachmentTypesEnum.TIMESHEET_SUMMARY.name());
            timesheetSummaryAttachment.setFileName(invoiceAttachmentFileName);
            timesheetSummaryAttachment.setFileType(InvoiceAttachmentFileTypes.HTML.name());
            timesheetSummaryAttachment.setFileContent(invoiceSummaryHtml.getBytes(StandardCharsets.UTF_8));
            return saveInvoiceAttachment(timesheetSummaryAttachment);
        } catch (Exception e) {
            throw new TerexApplicationException(String.format("Error crating invoice attachment, invoice ref=%s", invoiceId), "50023", e);
        }
    }

    /**
     * The timesheet should contain an entry for all days in the month.
     * Days not worked, sick, or vacation are simple blank.
     */
    public Long createClientTimesheetAttachment(Long invoiceId, Long timesheetId, String clientTimesheetTemplate) {
        try {
            UserAccountDto userAccountDto = userAccountService.getDefaultUserAccount();
            ClientDto clientDto = clientService.getClientByTimesheetId(timesheetId);
            String timesheetAttachmentHtml = timesheetService.createTimesheetListHtml(timesheetId, userAccountDto, clientDto, clientTimesheetTemplate);
            InvoiceDto invoiceDto = getInvoiceDto(invoiceId);
            String timesheetAttachmentFileName = String.format("%s_timeliste_%s", invoiceDto.getInvoiceRecipient().getName().replace(" ", "_").toLowerCase(), invoiceDto.getBillingPeriodStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM")));

            InvoiceAttachment clientTimesheetAttachment = new InvoiceAttachment();
            clientTimesheetAttachment.setInvoiceId(invoiceId);
            clientTimesheetAttachment.setType(InvoiceAttachmentTypesEnum.CLIENT_TIMESHEET.name());
            clientTimesheetAttachment.setFileName(timesheetAttachmentFileName);
            clientTimesheetAttachment.setFileType(InvoiceAttachmentFileTypes.HTML.name());
            clientTimesheetAttachment.setFileContent(timesheetAttachmentHtml.getBytes(StandardCharsets.UTF_8));
            return saveInvoiceAttachment(clientTimesheetAttachment);
        } catch (Exception e) {
            throw new TerexApplicationException(String.format("Error crating client timesheet attachment, timesheetId=%s", timesheetId), "50023", e);
        }
    }

    private int generateInvoiceNumber() {
        return Random.Default.nextInt(100, 10000);
    }
}
