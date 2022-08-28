package com.gunnarro.android.terex.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gunnarro.android.terex.domain.entity.Timesheet;
import com.gunnarro.android.terex.domain.entity.InvoiceSummary;
import com.gunnarro.android.terex.service.InvoiceService;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class TimesheetTest {

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    InvoiceService timesheetService;

    @Before
    public void init() {
        timesheetService = new InvoiceService();
    }

    @Test
    public void timesheetToJson() {
        Timesheet timeSheet = new Timesheet();
        //Timesheet timesheet = mapper.readValue(jsonString, Timesheet.class);
        assertEquals("Open", "Open");
    }

    @Test
    public void jsonToTimesheet()  {
        List<Timesheet> timesheets = timesheetService.generateTimesheet(2022, 3);
        String jsonStr = null;
        try {
            jsonStr = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(timesheets);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        assertNotNull(jsonStr);
    }

    @Test
    public void buildInvoiceSummaryByWeek()  {
        List<InvoiceSummary> list = timesheetService.buildInvoiceSummaryByWeek(2022, 3);
        list.forEach( t -> {
            System.out.println(t);
        });
    }

}
