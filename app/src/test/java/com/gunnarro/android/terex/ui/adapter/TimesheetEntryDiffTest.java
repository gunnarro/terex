package com.gunnarro.android.terex.ui.adapter;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.gunnarro.android.terex.domain.dto.TimesheetEntryDto;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class TimesheetEntryDiffTest {

    @Test
    void itemsEqual() {
        TimesheetEntryDto timesheetEntryDto1 = new TimesheetEntryDto();
        timesheetEntryDto1.setId(1L);
        timesheetEntryDto1.setTimesheetId(23L);
        timesheetEntryDto1.setProjectId(100L);
        timesheetEntryDto1.setWorkdayDate(LocalDate.of(2023, 9, 9));

        TimesheetEntryDto timesheetEntryDto2 = timesheetEntryDto1;

        TimesheetEntryListAdapter.TimesheetEntryDiff diff = new TimesheetEntryListAdapter.TimesheetEntryDiff();

        assertTrue(diff.areItemsTheSame(timesheetEntryDto1, timesheetEntryDto2));
    }

    @Test
    void itemsNotEqual() {
        TimesheetEntryDto timesheetEntryDto1 = new TimesheetEntryDto();
        timesheetEntryDto1.setId(1L);
        timesheetEntryDto1.setTimesheetId(23L);
        timesheetEntryDto1.setProjectId(100L);
        timesheetEntryDto1.setWorkdayDate(LocalDate.of(2023, 9, 9));

        TimesheetEntryDto timesheetEntryDto2 = new TimesheetEntryDto();
        timesheetEntryDto2.setId(2L);
        timesheetEntryDto2.setTimesheetId(23L);
        timesheetEntryDto1.setProjectId(100L);
        timesheetEntryDto2.setWorkdayDate(LocalDate.of(2023, 9, 9));

        TimesheetEntryListAdapter.TimesheetEntryDiff diff = new TimesheetEntryListAdapter.TimesheetEntryDiff();

        assertFalse(diff.areItemsTheSame(timesheetEntryDto1, timesheetEntryDto2));
    }

    @Test
    void contentsEqual() {
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

        TimesheetEntryListAdapter.TimesheetEntryDiff diff = new TimesheetEntryListAdapter.TimesheetEntryDiff();

        assertTrue(diff.areContentsTheSame(timesheetEntryDto1, timesheetEntryDto2));
    }

    @Test
    void contentsNotEqual() {
        TimesheetEntryDto timesheetEntryDto1 = new TimesheetEntryDto();
        timesheetEntryDto1.setId(1L);
        timesheetEntryDto1.setTimesheetId(23L);
        timesheetEntryDto1.setTimesheetId(23L);
        timesheetEntryDto1.setWorkdayDate(LocalDate.of(2023, 9, 9));

        TimesheetEntryDto timesheetEntryDto2 = new TimesheetEntryDto();
        timesheetEntryDto2.setId(2L);
        timesheetEntryDto2.setTimesheetId(23L);
        timesheetEntryDto2.setTimesheetId(23L);
        timesheetEntryDto2.setWorkdayDate(LocalDate.of(2023, 9, 10));

        TimesheetEntryListAdapter.TimesheetEntryDiff diff = new TimesheetEntryListAdapter.TimesheetEntryDiff();

        assertFalse(diff.areContentsTheSame(timesheetEntryDto1, timesheetEntryDto2));
        assertFalse(diff.areContentsTheSame(timesheetEntryDto2, timesheetEntryDto1));
    }
}
