package com.gunnarro.android.terex.service;


import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gunnarro.android.terex.domain.entity.InvoiceSummary;
import com.gunnarro.android.terex.domain.entity.TimesheetEntry;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.List;

public class InvoiceServiceTest {

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Mock
    android.content.Context applicationContextMock;

    @Test
    void generateTimesheet() {
        InvoiceService invoiceService = new InvoiceService(applicationContextMock);
        List<TimesheetEntry> timesheets = invoiceService.generateTimesheet(2023, 2);
        assertEquals(19, timesheets.size());
        assertEquals(30, timesheets.get(0).getBreakInMin());
        assertEquals(1075, timesheets.get(0).getHourlyRate());
        assertEquals("Open", timesheets.get(0).getStatus());
        assertEquals(450, timesheets.get(0).getWorkedMinutes());
        assertEquals("2023-02-01", timesheets.get(0).getWorkdayDate().toString());
        assertEquals("08:00", timesheets.get(0).getFromTime().toString());
        assertEquals("15:30", timesheets.get(0).getToTime().toString());
        assertEquals(null, timesheets.get(0).getComment());

    }

    @Test
    void buildInvoiceSummary() {
        InvoiceService invoiceService = new InvoiceService(applicationContextMock);
        List<InvoiceSummary> invoiceSummaries = invoiceService.buildInvoiceSummaryByWeek(2023,2);
        assertEquals(5, invoiceSummaries.size());
        assertEquals(0, invoiceSummaries.get(0).getInvoiceId());
        assertEquals(24187.5, invoiceSummaries.get(0).getSumBilledWork());
        assertEquals(3, invoiceSummaries.get(0).getSumWorkedDays());
        assertEquals(1350, invoiceSummaries.get(0).getSumWorkedHours());
    }

    @org.junit.Test
    public void buildInvoiceSummaryByWeek()  {
        InvoiceService invoiceService = new InvoiceService(applicationContextMock);
        List<InvoiceSummary> list = invoiceService.buildInvoiceSummaryByWeek(2022, 3);
        list.forEach( t -> {
            System.out.println(t);
        });
    }

    @org.junit.Test
    public void jsonToTimesheet()  {
        InvoiceService invoiceService = new InvoiceService(applicationContextMock);
        List<TimesheetEntry> timesheets = invoiceService.generateTimesheet(2022, 3);
        String jsonStr = null;
        try {
            jsonStr = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(timesheets);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        assertNotNull(jsonStr);
    }
}
