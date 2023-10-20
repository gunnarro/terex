package com.gunnarro.android.terex.service;

import android.content.Context;
import android.util.Log;

import androidx.room.Transaction;

import com.gunnarro.android.terex.domain.entity.Company;
import com.gunnarro.android.terex.domain.entity.Invoice;
import com.gunnarro.android.terex.domain.entity.TimesheetSummary;
import com.gunnarro.android.terex.repository.InvoiceRepository;

import java.time.LocalDate;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import kotlin.random.Random;

@Singleton
public class InvoiceService {

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
        invoice.setStatus(InvoiceRepository.InvoiceStatusEnum.OPEN.name());
        invoice.setBillingDate(LocalDate.now());
        // defaulted to 10 days after billing date
        invoice.setDueDate(invoice.getBillingDate().plusDays(10));
        double sumAmount = timesheetSummaries.stream().mapToDouble(TimesheetSummary::getTotalBilledAmount).sum();
        invoice.setAmount(sumAmount);
        invoice.setStatus(InvoiceRepository.InvoiceStatusEnum.COMPLETED.name());
        return invoiceRepository.saveInvoice(invoice);
    }

}
