package com.gunnarro.android.terex.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TimesheetTest {

    @Test
    void timesheetNew() {
        Timesheet timeSheet = Timesheet.createDefault(100L, 10L,2023, 11);
        assertEquals(100, timeSheet.getUserId());
        assertEquals(10, timeSheet.getClientId());
        assertEquals(11, timeSheet.getMonth().intValue());
        assertEquals(2023, timeSheet.getYear().intValue());
        assertEquals("2023-11-01", timeSheet.getFromDate().toString());
        assertEquals("2023-11-30", timeSheet.getToDate().toString());
        assertNull(timeSheet.getCreatedDate());
        assertNull(timeSheet.getLastModifiedDate());


        assertNull(timeSheet.getDescription());
        assertEquals("Timesheet{userId=100, clientId=10, year=2023, month=11, status=NEW}", timeSheet.toString());
        assertEquals(Timesheet.TimesheetStatusEnum.NEW.name(), timeSheet.getStatus());
    }

    @Test
    void status() {
        Timesheet timeSheet = Timesheet.createDefault(100L, 10L, 2023, 11);
        assertTrue(timeSheet.isNew());
        timeSheet.setStatus(Timesheet.TimesheetStatusEnum.ACTIVE.name());
        assertTrue(timeSheet.isActive());
        timeSheet.setStatus(Timesheet.TimesheetStatusEnum.COMPLETED.name());
        assertTrue(timeSheet.isCompleted());
        timeSheet.setStatus(Timesheet.TimesheetStatusEnum.BILLED.name());
        assertTrue(timeSheet.isBilled());
    }
/*
    @Test
    void workedHours() {
        Timesheet timeSheet = Timesheet.createDefault("gunnarro", "timesheet", 2023, 11);
        assertEquals(0, timeSheet.getTotalWorkedDays().intValue());
        assertEquals(0, timeSheet.getTotalWorkedHours().intValue());
        assertEquals(22, timeSheet.getWorkingDaysInMonth().intValue());
        assertEquals(165, timeSheet.getWorkingHoursInMonth().intValue());
        assertFalse(timeSheet.getTotalWorkedHours() >= timeSheet.getWorkingHoursInMonth());
        assertFalse(timeSheet.getTotalWorkedDays() >= timeSheet.getWorkingDaysInMonth());
        timeSheet.setTotalWorkedHours(165);
        timeSheet.setTotalWorkedDays(22);
        assertTrue(timeSheet.getTotalWorkedHours() >= timeSheet.getWorkingHoursInMonth());
        assertTrue(timeSheet.getTotalWorkedDays() >= timeSheet.getWorkingDaysInMonth());
    }
*/

    @Test
    void timesheetAreEqual() {
        Timesheet timesheet1 = new Timesheet();
        timesheet1.setUserId(100L);
        timesheet1.setClientId(200L);
        timesheet1.setYear(2023);
        timesheet1.setMonth(11);

        Timesheet timesheet2 = new Timesheet();
        timesheet2.setUserId(100L);
        timesheet2.setClientId(200L);
        timesheet2.setYear(2023);
        timesheet2.setMonth(11);

        assertEquals(timesheet1, timesheet2);
    }

    @Test
    void timesheetNotEqual() {
        Timesheet timesheet1 = new Timesheet();
        timesheet1.setUserId(100L);
        timesheet1.setClientId(200L);
        timesheet1.setYear(2023);
        timesheet1.setMonth(11);

        Timesheet timesheet2 = new Timesheet();
        timesheet2.setUserId(100L);
        timesheet2.setClientId(200L);
        timesheet2.setYear(2023);
        timesheet2.setMonth(12);

        assertNotEquals(timesheet1, timesheet2);
    }
}
