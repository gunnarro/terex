package com.gunnarro.android.terex.domain.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.gunnarro.android.terex.domain.entity.TimesheetEntry;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class TimesheetEntryDtoTest {


   @Test
   void isClosed() {
       TimesheetEntryDto timesheetEntryDto = new TimesheetEntryDto();
       timesheetEntryDto.setStatus(TimesheetEntry.TimesheetEntryStatusEnum.CLOSED.name());
       assertTrue(timesheetEntryDto.isClosed());
       assertFalse(timesheetEntryDto.isOpen());
   }

    @Test
    void isOPen() {
        TimesheetEntryDto timesheetEntryDto = new TimesheetEntryDto();
        timesheetEntryDto.setStatus(TimesheetEntry.TimesheetEntryStatusEnum.OPEN.name());
        assertTrue(timesheetEntryDto.isOpen());
        assertFalse(timesheetEntryDto.isClosed());
    }

    @Test
    void isWeekend() {
        TimesheetEntryDto timesheetEntryDto = new TimesheetEntryDto();
        timesheetEntryDto.setWorkdayDate(LocalDate.of(2024, 6, 8));
        assertEquals("lørdag", timesheetEntryDto.getWorkdayDateDayName());
        assertTrue(timesheetEntryDto.isWeekend());

        timesheetEntryDto.setWorkdayDate(LocalDate.of(2024, 6, 9));
        assertEquals("søndag", timesheetEntryDto.getWorkdayDateDayName());
        assertTrue(timesheetEntryDto.isWeekend());
    }

    @Test
    void workdayDate() {
        TimesheetEntryDto timesheetEntryDto = new TimesheetEntryDto();
        timesheetEntryDto.setWorkdayDate(LocalDate.of(2024, 6, 9));
        assertEquals("9", timesheetEntryDto.getWorkdayDateDay());

        timesheetEntryDto.setWorkdayDate(LocalDate.of(2024, 6, 10));
        assertEquals("10", timesheetEntryDto.getWorkdayDateDay());
    }

    @Test
    void workedHours() {
        TimesheetEntryDto timesheetEntryDto = new TimesheetEntryDto();
        assertNull(timesheetEntryDto.getWorkedHours());
        timesheetEntryDto.setWorkedSeconds((long) 480 * 60);
        assertEquals("8.0", timesheetEntryDto.getWorkedHours());
        timesheetEntryDto.setWorkedSeconds((long) 450 * 60);
        assertEquals("7.5", timesheetEntryDto.getWorkedHours());
        assertNull(timesheetEntryDto.getStartTime());
        assertNull(timesheetEntryDto.getEndTime());
    }

    @Test
    void isRegularWorkDayWithHours() {
        TimesheetEntryDto timesheetEntryDto = new TimesheetEntryDto();
        timesheetEntryDto.setWorkdayDate(LocalDate.of(2024, 6, 8));
        timesheetEntryDto.setType(TimesheetEntry.TimesheetEntryTypeEnum.REGULAR.name());
        timesheetEntryDto.setWorkedSeconds(3600L);
        assertTrue(timesheetEntryDto.isRegularWorkDay());
        assertTrue(timesheetEntryDto.isRegularWorkDayWithHours());
        assertFalse(timesheetEntryDto.isSickDay());
        assertFalse(timesheetEntryDto.isVacationDay());
    }

    @Test
    void timesheetEntryAreEqual() {
        TimesheetEntryDto timesheetEntryDto1 = new TimesheetEntryDto();
        timesheetEntryDto1.setId(1L);
        timesheetEntryDto1.setTimesheetId(23L);
        timesheetEntryDto1.setProjectId(100L);
        timesheetEntryDto1.setWorkdayDate(LocalDate.of(2023, 9, 9));

        TimesheetEntryDto timesheetEntryDto2 = new TimesheetEntryDto();
        timesheetEntryDto2.setId(2L);
        timesheetEntryDto2.setTimesheetId(23L);
        timesheetEntryDto2.setProjectId(100L);
        timesheetEntryDto2.setWorkdayDate(LocalDate.of(2023, 9, 9));

        assertEquals(timesheetEntryDto1, timesheetEntryDto2);
    }

    @Test
    void timesheetEntryNotEqual() {
        TimesheetEntryDto timesheetEntryDto1 = new TimesheetEntryDto();
        timesheetEntryDto1.setId(1L);
        timesheetEntryDto1.setTimesheetId(23L);
        timesheetEntryDto1.setProjectId(100L);
        timesheetEntryDto1.setWorkdayDate(LocalDate.of(2023, 9, 9));

        TimesheetEntryDto timesheetEntryDto2 = new TimesheetEntryDto();
        timesheetEntryDto2.setId(2L);
        timesheetEntryDto2.setTimesheetId(23L);
        timesheetEntryDto2.setProjectId(100L);
        timesheetEntryDto2.setWorkdayDate(LocalDate.of(2023, 9, 10));

        assertNotEquals(timesheetEntryDto1, timesheetEntryDto2);
    }
}

