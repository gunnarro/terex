package com.gunnarro.android.terex.domain.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class TimesheetEntryDtoTest {

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
    }
}

