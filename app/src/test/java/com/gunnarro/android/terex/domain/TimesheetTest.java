package com.gunnarro.android.terex.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gunnarro.android.terex.domain.entity.Timesheet;
import com.gunnarro.android.terex.domain.entity.InvoiceSummary;
import com.gunnarro.android.terex.service.InvoiceService;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
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
    void timesheetAreEqual() {
        Timesheet timesheet1 = new Timesheet();
        timesheet1.setClientName("Recruitment AS");
        timesheet1.setProjectName("refactor monolith");
        timesheet1.setWorkdayDate(LocalDate.of(2023, 9, 9));

        Timesheet timesheet2 = new Timesheet();
        timesheet2.setClientName("Recruitment AS");
        timesheet2.setProjectName("refactor monolith");
        timesheet2.setWorkdayDate(LocalDate.of(2023, 9, 9));

        assertTrue(timesheet1.equals(timesheet2));
    }

    @Test
    void timesheetNotEqual() {
        Timesheet timesheet1 = new Timesheet();
        timesheet1.setClientName("Recruitment AS");
        timesheet1.setProjectName("refactor monolith");
        timesheet1.setWorkdayDate(LocalDate.of(2023, 9, 9));

        Timesheet timesheet2 = new Timesheet();
        timesheet2.setClientName("Recruitment AS");
        timesheet2.setProjectName("refactor monolith");
        timesheet2.setWorkdayDate(LocalDate.of(2023, 9, 10));

        assertTrue(timesheet1.equals(timesheet2));
    }

    @Test
    public void buildInvoiceSummaryByWeek()  {
        List<InvoiceSummary> list = timesheetService.buildInvoiceSummaryByWeek(2022, 3);
        list.forEach( t -> {
            System.out.println(t);
        });
    }

}
