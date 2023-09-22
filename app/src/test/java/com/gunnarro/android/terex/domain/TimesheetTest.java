package com.gunnarro.android.terex.domain;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import android.content.Context;

import com.gunnarro.android.terex.domain.entity.TimesheetEntry;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TimesheetTest {

    @Mock
    Context applicationContext;

    @Before
    public void init() {
    }

    @Test
    public void timesheetToJson() {
        TimesheetEntry timeSheet = new TimesheetEntry();
        //Timesheet timesheet = mapper.readValue(jsonString, Timesheet.class);
        assertEquals("Open", "Open");
    }



    @Test
    public void cloneTimesheet() {
        TimesheetEntry timesheet1 = new TimesheetEntry();
        timesheet1.setId(23L);
        timesheet1.setCreatedDate(LocalDateTime.now());
        timesheet1.setLastModifiedDate(LocalDateTime.now());
        timesheet1.setWorkdayDate(LocalDate.now());
        timesheet1.setClientName("Recruitment AS");
        timesheet1.setProjectCode("refactor monolith");
        timesheet1.setHourlyRate(1075);
        timesheet1.setStatus("open");
        timesheet1.setBreakInMin(30);
        timesheet1.setWorkdayDate(LocalDate.of(2023, 9, 9));

        TimesheetEntry clone = TimesheetEntry.clone(timesheet1);

        assertNull(clone.getId());
        assertNull(clone.getCreatedDate());
        assertNull(clone.getLastModifiedDate());
        assertNull(clone.getWorkdayDate());
    }

    @Test
    public void timesheetAreEqual() {
        TimesheetEntry timesheet1 = new TimesheetEntry();
        timesheet1.setClientName("Recruitment AS");
        timesheet1.setProjectCode("refactor monolith");
        timesheet1.setWorkdayDate(LocalDate.of(2023, 9, 9));

        TimesheetEntry timesheet2 = new TimesheetEntry();
        timesheet2.setClientName("Recruitment AS");
        timesheet2.setProjectCode("refactor monolith");
        timesheet2.setWorkdayDate(LocalDate.of(2023, 9, 9));

        assertTrue(timesheet1.equals(timesheet2));
    }

    @Test
    public void timesheetNotEqual() {
        TimesheetEntry timesheet1 = new TimesheetEntry();
        timesheet1.setClientName("Recruitment AS");
        timesheet1.setProjectCode("refactor monolith");
        timesheet1.setWorkdayDate(LocalDate.of(2023, 9, 9));

        TimesheetEntry timesheet2 = new TimesheetEntry();
        timesheet2.setClientName("Recruitment AS");
        timesheet2.setProjectCode("refactor monolith");
        timesheet2.setWorkdayDate(LocalDate.of(2023, 9, 10));

        assertTrue(!timesheet1.equals(timesheet2));
    }



}
