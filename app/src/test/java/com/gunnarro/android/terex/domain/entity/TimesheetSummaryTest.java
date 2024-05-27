package com.gunnarro.android.terex.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TimesheetSummaryTest {

    @Test
    public void timesheetSummary() {
        TimesheetSummary timesheetSummary = new TimesheetSummary();
        timesheetSummary.setTimesheetId(234L);
        timesheetSummary.setCreatedDate(LocalDateTime.now());
        timesheetSummary.setLastModifiedDate(LocalDateTime.now());
        timesheetSummary.setFromDate(LocalDate.of(2024, 5, 1));
        timesheetSummary.setToDate(LocalDate.of(2024, 5, 8));
        timesheetSummary.setYear(2024);
        timesheetSummary.setWeekInYear(18);
        timesheetSummary.setCurrency("NOK");
        timesheetSummary.setTotalWorkedDays(23);
        assertEquals("01.05", timesheetSummary.getFromDateDDMM());
        assertEquals("08.05", timesheetSummary.getToDateDDMM());
        assertEquals("May", timesheetSummary.getMonthInYear());
    }
}
