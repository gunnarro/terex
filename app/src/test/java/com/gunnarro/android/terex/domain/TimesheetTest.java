package com.gunnarro.android.terex.domain;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import com.gunnarro.android.terex.domain.entity.Timesheet;
import com.gunnarro.android.terex.domain.entity.TimesheetEntry;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mock;

import java.time.LocalDate;

public class TimesheetTest {

    @Mock
    Context applicationContext;

    @Before
    public void init() {
    }

    @Test
    public void timesheetEntryToJson() {
        Timesheet timeSheet = new Timesheet();
        assertEquals("Open", "Open");
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
