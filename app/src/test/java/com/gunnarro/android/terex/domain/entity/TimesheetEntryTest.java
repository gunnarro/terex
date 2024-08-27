package com.gunnarro.android.terex.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.gunnarro.android.terex.utility.Utility;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

class TimesheetEntryTest {

    @Test
    void create() {
        TimesheetEntry timesheetEntry = TimesheetEntry.create(22L, 11L, LocalDate.of(2024, 5, 1), LocalTime.of(8, 0), Utility.fromHoursToSeconds("7.5"), "jira-1220");
        assertNull(timesheetEntry.getId());
        assertEquals(22, timesheetEntry.getTimesheetId());
        assertEquals(11, timesheetEntry.getProjectId());
        assertEquals("2024-05-01", timesheetEntry.getWorkdayDate().toString());
        assertEquals("7.5", timesheetEntry.getWorkedHours());
        assertEquals(27000, timesheetEntry.getWorkedSeconds());
        assertEquals("REGULAR", timesheetEntry.getType());
        assertEquals("OPEN", timesheetEntry.getStatus());
        assertEquals("08:00", timesheetEntry.getStartTime().toString());
        assertEquals("15:30", timesheetEntry.getEndTime().toString());
        assertEquals(18, timesheetEntry.getWorkdayWeek());
        assertNull(timesheetEntry.getCreatedDate());
        assertNull(timesheetEntry.getLastModifiedDate());
        assertEquals("jira-1220", timesheetEntry.getComments());
    }

    @Test
    void createDefault() {
        TimesheetEntry timesheetEntry = TimesheetEntry.createDefault(22L, 11L, LocalDate.of(2024, 5, 1));
        assertNull(timesheetEntry.getId());
        assertEquals(22, timesheetEntry.getTimesheetId());
        assertEquals(11, timesheetEntry.getProjectId());
        assertEquals("2024-05-01", timesheetEntry.getWorkdayDate().toString());
        assertEquals("7.5", timesheetEntry.getWorkedHours());
        assertEquals(27000, timesheetEntry.getWorkedSeconds());
        assertEquals("REGULAR", timesheetEntry.getType());
        assertEquals("OPEN", timesheetEntry.getStatus());
        assertEquals("08:00", timesheetEntry.getStartTime().toString());
        assertEquals("15:30", timesheetEntry.getEndTime().toString());
        assertEquals(18, timesheetEntry.getWorkdayWeek());
        assertNull(timesheetEntry.getCreatedDate());
        assertNull(timesheetEntry.getLastModifiedDate());
        assertNull(timesheetEntry.getComments());
        assertFalse(timesheetEntry.isVacationDay());
        assertFalse(timesheetEntry.isSickDay());
        assertTrue(timesheetEntry.isRegularWorkDay());
    }


    @Test
    void createNotWorked() {
        TimesheetEntry timesheetEntry = TimesheetEntry.createNotWorked(22L, 11L, LocalDate.of(2024, 5, 1), TimesheetEntry.TimesheetEntryTypeEnum.SICK.name());
        assertNull(timesheetEntry.getId());
        assertEquals(22, timesheetEntry.getTimesheetId());
        assertEquals(11, timesheetEntry.getProjectId());
        assertEquals("2024-05-01", timesheetEntry.getWorkdayDate().toString());
        assertEquals("0.0", timesheetEntry.getWorkedHours());
        assertEquals(0, timesheetEntry.getWorkedSeconds());
        assertEquals("SICK", timesheetEntry.getType());
        assertEquals("CLOSED", timesheetEntry.getStatus());
        assertNull(timesheetEntry.getStartTime());
        assertNull(timesheetEntry.getEndTime());
        assertEquals(18, timesheetEntry.getWorkdayWeek());
        assertNull(timesheetEntry.getCreatedDate());
        assertNull(timesheetEntry.getLastModifiedDate());
        assertNull(timesheetEntry.getComments());
    }

    @Test
    void typeRegular() {
        TimesheetEntry timeSheetEntry = TimesheetEntry.createDefault(23L, 11L, LocalDate.of(2023, 12, 2));
        assertTrue(timeSheetEntry.isRegularWorkDay());
        assertFalse(timeSheetEntry.isSickDay());
        assertFalse(timeSheetEntry.isVacationDay());
    }

    @Test
    void typeVacation() {
        TimesheetEntry timeSheetEntry = TimesheetEntry.createDefault(23L, 11L, LocalDate.of(2023, 12, 2));
        timeSheetEntry.setType(TimesheetEntry.TimesheetEntryTypeEnum.VACATION.name());
        assertTrue(timeSheetEntry.isVacationDay());
        assertFalse(timeSheetEntry.isRegularWorkDay());
        assertFalse(timeSheetEntry.isSickDay());
    }

    @Test
    void typeSick() {
        TimesheetEntry timeSheetEntry = TimesheetEntry.createDefault(23L, 11L, LocalDate.of(2023, 12, 2));
        timeSheetEntry.setType(TimesheetEntry.TimesheetEntryTypeEnum.SICK.name());
        assertTrue(timeSheetEntry.isSickDay());
        assertFalse(timeSheetEntry.isVacationDay());
        assertFalse(timeSheetEntry.isRegularWorkDay());
    }

    @Test
    void status() {
        TimesheetEntry timeSheetEntry = TimesheetEntry.createDefault(23L, 11L, LocalDate.of(2023, 12, 2));
        assertTrue(timeSheetEntry.isOpen());
        timeSheetEntry.setStatus(TimesheetEntry.TimesheetEntryStatusEnum.CLOSED.name());
        assertTrue(timeSheetEntry.isClosed());
    }

    @Test
    void areEqual() {
        TimesheetEntry timesheetEntry1 = new TimesheetEntry();
        timesheetEntry1.setId(1L);
        timesheetEntry1.setTimesheetId(23L);
        timesheetEntry1.setProjectId(100L);
        timesheetEntry1.setWorkdayDate(LocalDate.of(2023, 9, 9));

        TimesheetEntry timesheetEntry2 = new TimesheetEntry();
        timesheetEntry2.setId(2L);
        timesheetEntry2.setTimesheetId(23L);
        timesheetEntry2.setProjectId(100L);
        timesheetEntry2.setWorkdayDate(LocalDate.of(2023, 9, 9));

        assertEquals(timesheetEntry1, timesheetEntry2);
    }

    @Test
    void areNotEqual() {
        TimesheetEntry timesheetEntry1 = new TimesheetEntry();
        timesheetEntry1.setId(1L);
        timesheetEntry1.setTimesheetId(23L);
        timesheetEntry1.setProjectId(100L);
        timesheetEntry1.setWorkdayDate(LocalDate.of(2023, 9, 9));

        TimesheetEntry timesheetEntry2 = new TimesheetEntry();
        timesheetEntry2.setId(2L);
        timesheetEntry2.setTimesheetId(23L);
        timesheetEntry2.setProjectId(100L);
        timesheetEntry2.setWorkdayDate(LocalDate.of(2023, 9, 10));

        assertNotEquals(timesheetEntry1, timesheetEntry2);
    }
}
