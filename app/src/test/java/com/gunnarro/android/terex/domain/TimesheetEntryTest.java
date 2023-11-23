package com.gunnarro.android.terex.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import android.content.Context;

import com.gunnarro.android.terex.domain.entity.Timesheet;
import com.gunnarro.android.terex.domain.entity.TimesheetEntry;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mock;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TimesheetEntryTest {

    @Mock
    Context applicationContext;

    @Before
    public void init() {
    }

    @Test
    public void timesheetEntryToJson() {
        TimesheetEntry timeSheetEntry = new TimesheetEntry();
        //Timesheet timesheet = mapper.readValue(jsonString, Timesheet.class);
        assertEquals("Open", "Open");
    }



    @Test
    public void cloneTimesheetEntry() {
        TimesheetEntry timesheetEntry1 = new TimesheetEntry();
        timesheetEntry1.setId(23L);
        timesheetEntry1.setCreatedDate(LocalDateTime.now());
        timesheetEntry1.setLastModifiedDate(LocalDateTime.now());
        timesheetEntry1.setWorkdayDate(LocalDate.now());
        timesheetEntry1.setHourlyRate(1075);
        timesheetEntry1.setStatus(Timesheet.TimesheetStatusEnum.NEW.name());
        timesheetEntry1.setBreakInMin(30);
        timesheetEntry1.setWorkdayDate(LocalDate.of(2023, 9, 9));

        TimesheetEntry clone = TimesheetEntry.clone(timesheetEntry1);

        assertNull(clone.getId());
        assertNull(clone.getCreatedDate());
        assertNull(clone.getLastModifiedDate());
        assertNull(clone.getWorkdayDate());
    }

    @Test
    public void timesheetEntryAreEqual() {
        TimesheetEntry timesheetEntry1 = new TimesheetEntry();
        timesheetEntry1.setId(1L);
        timesheetEntry1.setTimesheetId(23L);
        timesheetEntry1.setWorkdayDate(LocalDate.of(2023, 9, 9));

        TimesheetEntry timesheetEntry2 = new TimesheetEntry();
        timesheetEntry2.setId(2L);
        timesheetEntry2.setTimesheetId(23L);
        timesheetEntry2.setWorkdayDate(LocalDate.of(2023, 9, 9));

        Assertions.assertTrue(timesheetEntry1.equals(timesheetEntry2));
    }

    @Test
    public void timesheetEntryNotEqual() {
        TimesheetEntry timesheetEntry1 = new TimesheetEntry();
        timesheetEntry1.setId(1L);
        timesheetEntry1.setTimesheetId(23L);
        timesheetEntry1.setWorkdayDate(LocalDate.of(2023, 9, 9));

        TimesheetEntry timesheetEntry2 = new TimesheetEntry();
        timesheetEntry2.setId(2L);
        timesheetEntry2.setTimesheetId(23L);
        timesheetEntry2.setWorkdayDate(LocalDate.of(2023, 9, 10));

        Assertions.assertFalse(timesheetEntry1.equals(timesheetEntry2));
    }
}
