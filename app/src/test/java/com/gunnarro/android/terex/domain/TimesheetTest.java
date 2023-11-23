package com.gunnarro.android.terex.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import android.content.Context;

import com.gunnarro.android.terex.domain.entity.Timesheet;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mock;

public class TimesheetTest {

    @Mock
    Context applicationContext;

    @Before
    public void init() {
    }

    @Test
    public void workedHours() {
        Timesheet timeSheet = Timesheet.createDefault("gunnarro", "timesheet", 2023, 11);
        assertEquals(0, timeSheet.getTotalWorkedDays().intValue());
        assertEquals(0, timeSheet.getTotalWorkedMinutes().intValue());
        assertEquals(21, timeSheet.getWorkingDaysInMonth().intValue());
        assertEquals(157, timeSheet.getWorkingHoursInMonth().intValue());
        assertFalse(timeSheet.getTotalWorkedMinutes() / 60 >= timeSheet.getWorkingHoursInMonth());
        assertFalse(timeSheet.getTotalWorkedDays() >= timeSheet.getWorkingDaysInMonth());
        timeSheet.setTotalWorkedMinutes(15700);
        timeSheet.setTotalWorkedDays(22);
        assertTrue(timeSheet.getTotalWorkedMinutes() / 60 >= timeSheet.getWorkingHoursInMonth());
        assertTrue(timeSheet.getTotalWorkedDays() >= timeSheet.getWorkingDaysInMonth());
    }


    @Test
    public void timesheetAreEqual() {
        Timesheet timesheet1 = new Timesheet();
        timesheet1.setClientName("clientname_1");
        timesheet1.setProjectCode("projectcode_1");
        timesheet1.setYear(2023);
        timesheet1.setMonth(11);

        Timesheet timesheet2 = new Timesheet();
        timesheet2.setClientName("clientname_1");
        timesheet2.setProjectCode("projectcode_1");
        timesheet2.setYear(2023);
        timesheet2.setMonth(11);

        Assertions.assertEquals(timesheet1, timesheet2);
    }

    @Test
    public void timesheetNotEqual() {
        Timesheet timesheet1 = new Timesheet();
        timesheet1.setClientName("clientname_1");
        timesheet1.setProjectCode("projectcode_1");
        timesheet1.setYear(2023);
        timesheet1.setMonth(11);

        Timesheet timesheet2 = new Timesheet();
        timesheet2.setClientName("clientname_1");
        timesheet2.setProjectCode("projectcode_1");
        timesheet2.setYear(2023);
        timesheet2.setMonth(12);

        Assertions.assertNotEquals(timesheet1, timesheet2);
    }
}
