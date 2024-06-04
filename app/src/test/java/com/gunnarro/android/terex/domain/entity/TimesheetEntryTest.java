package com.gunnarro.android.terex.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

class TimesheetEntryTest {

    @Test
    void cloneTimesheetEntry() {
        TimesheetEntry timesheetEntry1 = new TimesheetEntry();
        timesheetEntry1.setId(23L);
        timesheetEntry1.setCreatedDate(LocalDateTime.now());
        timesheetEntry1.setLastModifiedDate(LocalDateTime.now());
        timesheetEntry1.setWorkdayDate(LocalDate.now());
        timesheetEntry1.setStatus(TimesheetEntry.TimesheetEntryStatusEnum.OPEN.name());
        timesheetEntry1.setBreakInMin(30);
        timesheetEntry1.setWorkdayDate(LocalDate.of(2023, 9, 9));

        TimesheetEntry clone = TimesheetEntry.clone(timesheetEntry1);

        assertNull(clone.getId());
        assertNull(clone.getCreatedDate());
        assertNull(clone.getLastModifiedDate());
        assertNull(clone.getWorkdayDate());
    }

    @Test
    void type() {
        TimesheetEntry timeSheetEntry = TimesheetEntry.createDefault(23L, LocalDate.of(2023, 12, 2));
        assertTrue(timeSheetEntry.isRegularWorkDay());
        timeSheetEntry.setType(TimesheetEntry.TimesheetEntryTypeEnum.VACATION.name());
        assertFalse(timeSheetEntry.isRegularWorkDay());
    }

    @Test
    void status() {
        TimesheetEntry timeSheetEntry = TimesheetEntry.createDefault(23L, LocalDate.of(2023, 12, 2));
        assertTrue(timeSheetEntry.isOpen());
        timeSheetEntry.setStatus(TimesheetEntry.TimesheetEntryStatusEnum.CLOSED.name());
        assertTrue(timeSheetEntry.isBilled());
    }

    @Test
    void timesheetEntryAreEqual() {
        TimesheetEntry timesheetEntry1 = new TimesheetEntry();
        timesheetEntry1.setId(1L);
        timesheetEntry1.setTimesheetId(23L);
        timesheetEntry1.setWorkdayDate(LocalDate.of(2023, 9, 9));

        TimesheetEntry timesheetEntry2 = new TimesheetEntry();
        timesheetEntry2.setId(2L);
        timesheetEntry2.setTimesheetId(23L);
        timesheetEntry2.setWorkdayDate(LocalDate.of(2023, 9, 9));

        assertEquals(timesheetEntry1, timesheetEntry2);
    }

    @Test
    void timesheetEntryNotEqual() {
        TimesheetEntry timesheetEntry1 = new TimesheetEntry();
        timesheetEntry1.setId(1L);
        timesheetEntry1.setTimesheetId(23L);
        timesheetEntry1.setWorkdayDate(LocalDate.of(2023, 9, 9));

        TimesheetEntry timesheetEntry2 = new TimesheetEntry();
        timesheetEntry2.setId(2L);
        timesheetEntry2.setTimesheetId(23L);
        timesheetEntry2.setWorkdayDate(LocalDate.of(2023, 9, 10));

        assertNotEquals(timesheetEntry1, timesheetEntry2);
    }
}
