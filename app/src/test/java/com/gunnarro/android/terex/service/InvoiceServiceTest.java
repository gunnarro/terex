package com.gunnarro.android.terex.service;


import static org.junit.jupiter.api.Assertions.assertEquals;

import com.gunnarro.android.terex.domain.entity.InvoiceSummary;
import com.gunnarro.android.terex.domain.entity.Timesheet;

import org.junit.jupiter.api.Test;

import java.util.List;

public class InvoiceServiceTest {

    @Test
    void generateTimesheet() {
        InvoiceService invoiceService = new InvoiceService();
        List<Timesheet> timesheets = invoiceService.generateTimesheet(2023, 2);
        assertEquals(19, timesheets.size());
        assertEquals(30, timesheets.get(0).getBreakInMin());
        assertEquals(1075, timesheets.get(0).getHourlyRate());
        assertEquals("Open", timesheets.get(0).getStatus());
        assertEquals(450, timesheets.get(0).getWorkedMinutes());
        assertEquals("2023-02-01", timesheets.get(0).getWorkdayDate().toString());
        assertEquals("08:00", timesheets.get(0).getFromTime().toString());
        assertEquals("15:30", timesheets.get(0).getToTime().toString());
        assertEquals(null, timesheets.get(0).getComment());
        assertEquals(null, timesheets.get(0).getClientName());
        assertEquals(null, timesheets.get(0).getProjectName());
    }

    @Test
    void buildInvoiceSummary() {
        InvoiceService invoiceService = new InvoiceService();
        List<InvoiceSummary> invoiceSummaries = invoiceService.buildInvoiceSummaryByWeek(2023,2);
        assertEquals(5, invoiceSummaries.size());
        assertEquals(0, invoiceSummaries.get(0).getInvoiceId());
        assertEquals(24187.5, invoiceSummaries.get(0).getSumBilledWork());
        assertEquals(3, invoiceSummaries.get(0).getSumWorkedDays());
        assertEquals(1350, invoiceSummaries.get(0).getSumWorkedMinutes());
    }
}
