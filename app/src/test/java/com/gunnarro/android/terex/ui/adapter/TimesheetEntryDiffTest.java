package com.gunnarro.android.terex.ui.adapter;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.gunnarro.android.terex.domain.entity.TimesheetEntry;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class TimesheetEntryDiffTest {

    @Test
    void itemsEqual() {
        TimesheetEntry timesheetEntry1 = new TimesheetEntry();
        timesheetEntry1.setId(1L);
        timesheetEntry1.setTimesheetId(23L);
        timesheetEntry1.setWorkdayDate(LocalDate.of(2023, 9, 9));

        TimesheetEntry timesheetEntry2 = timesheetEntry1;

        TimesheetEntryListAdapter.TimesheetEntryDiff diff = new TimesheetEntryListAdapter.TimesheetEntryDiff();

        assertTrue(diff.areItemsTheSame(timesheetEntry1, timesheetEntry2));
    }

    @Test
    void itemsNotEqual() {
        TimesheetEntry timesheetEntry1 = new TimesheetEntry();
        timesheetEntry1.setId(1L);
        timesheetEntry1.setTimesheetId(23L);
        timesheetEntry1.setWorkdayDate(LocalDate.of(2023, 9, 9));

        TimesheetEntry timesheetEntry2 = new TimesheetEntry();
        timesheetEntry2.setId(2L);
        timesheetEntry2.setTimesheetId(23L);
        timesheetEntry2.setWorkdayDate(LocalDate.of(2023, 9, 9));

        TimesheetEntryListAdapter.TimesheetEntryDiff diff = new TimesheetEntryListAdapter.TimesheetEntryDiff();

        assertFalse(diff.areItemsTheSame(timesheetEntry1, timesheetEntry2));
    }

    @Test
    void contentsEqual() {
        TimesheetEntry timesheetEntry1 = new TimesheetEntry();
        timesheetEntry1.setId(1L);
        timesheetEntry1.setTimesheetId(23L);
        timesheetEntry1.setWorkdayDate(LocalDate.of(2023, 9, 9));

        TimesheetEntry timesheetEntry2 = new TimesheetEntry();
        timesheetEntry2.setId(2L);
        timesheetEntry2.setTimesheetId(23L);
        timesheetEntry2.setWorkdayDate(LocalDate.of(2023, 9, 9));

        TimesheetEntryListAdapter.TimesheetEntryDiff diff = new TimesheetEntryListAdapter.TimesheetEntryDiff();

        assertTrue(diff.areContentsTheSame(timesheetEntry1, timesheetEntry2));
    }

    @Test
    void contentsNotEqual() {
        TimesheetEntry timesheetEntry1 = new TimesheetEntry();
        timesheetEntry1.setId(1L);
        timesheetEntry1.setTimesheetId(23L);
        timesheetEntry1.setWorkdayDate(LocalDate.of(2023, 9, 9));

        TimesheetEntry timesheetEntry2 = new TimesheetEntry();
        timesheetEntry2.setId(2L);
        timesheetEntry2.setTimesheetId(23L);
        timesheetEntry2.setWorkdayDate(LocalDate.of(2023, 9, 10));

        TimesheetEntryListAdapter.TimesheetEntryDiff diff = new TimesheetEntryListAdapter.TimesheetEntryDiff();

        assertFalse(diff.areContentsTheSame(timesheetEntry1, timesheetEntry2));
        assertFalse(diff.areContentsTheSame(timesheetEntry2, timesheetEntry1));
    }
}
