package com.gunnarro.android.terex.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import com.gunnarro.android.terex.config.AppDatabase;
import com.gunnarro.android.terex.domain.entity.Timesheet;
import com.gunnarro.android.terex.domain.entity.TimesheetEntry;
import com.gunnarro.android.terex.exception.InputValidationException;
import com.gunnarro.android.terex.repository.TimesheetRepository;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

public class TimesheetServiceTest {

    private TimesheetService timesheetService;

    @Before
    public void setup() {
        Context appContext = ApplicationProvider.getApplicationContext();
        AppDatabase.init(appContext);
        timesheetService = new TimesheetService(new TimesheetRepository(), new UserAccountService(), new ProjectService());
    }

    @Test
    public void getTimesheetsByStatus() {
        assertEquals(0, timesheetService.getTimesheetsByStatus(Timesheet.TimesheetStatusEnum.NEW.name()).size());
        assertEquals(0, timesheetService.getTimesheetsByStatus(Timesheet.TimesheetStatusEnum.ACTIVE.name()).size());
        assertEquals(0, timesheetService.getTimesheetsByStatus(Timesheet.TimesheetStatusEnum.COMPLETED.name()).size());
        assertEquals(0, timesheetService.getTimesheetsByStatus(Timesheet.TimesheetStatusEnum.BILLED.name()).size());
    }

    @Test
    public void newTimesheet() {
        Timesheet newTimesheet = Timesheet.createDefault(100L, 200L, 2023, 11);
        newTimesheet.setDescription("Times used to develop android timesheet app");
        Long timesheetId = timesheetService.saveTimesheet(newTimesheet);
        Timesheet timesheet = timesheetService.getTimesheet(timesheetId);
        assertEquals(100, timesheet.getUserId().intValue());
        assertEquals(200, timesheet.getProjectId().intValue());
        assertEquals(2023, timesheet.getYear().intValue());
        assertEquals(11, timesheet.getMonth().intValue());
        assertEquals("2023-11-01", timesheet.getFromDate().toString());
        assertEquals("2023-11-30", timesheet.getToDate().toString());
        assertEquals("Times used to develop android timesheet app", timesheet.getDescription());
        assertTrue(timesheet.isActive());
        assertNotNull(timesheet.getCreatedDate());
        assertNotNull(timesheet.getLastModifiedDate());
        assertEquals(165, timesheet.getWorkingHoursInMonth().intValue());
        assertEquals(22, timesheet.getWorkingDaysInMonth().intValue());
    }

    @Test
    public void newTimesheet_already_exist() {
        Timesheet newTimesheet = Timesheet.createDefault(100L, 200L, 2023, 10);
        newTimesheet.setDescription("Times used to develop android timesheet app");
        InputValidationException ex = assertThrows(InputValidationException.class, () -> {
            timesheetService.saveTimesheet(newTimesheet);
            timesheetService.saveTimesheet(Timesheet.createDefault(newTimesheet.getUserId(), newTimesheet.getProjectId(), newTimesheet.getYear(), newTimesheet.getMonth()));
        });
        assertEquals("Timesheet already exist! Timesheet{userId=100, projectId=200, year=2023, month=10, status=ACTIVE}", ex.getMessage());
    }

    @Test
    public void newTimesheetEntry_already_exist() {
        Timesheet newTimesheet = Timesheet.createDefault(100L, 200L, 2023, 8);
        newTimesheet.setDescription("Times used to develop android timesheet app");
        Long timesheetId = timesheetService.saveTimesheet(newTimesheet);
        TimesheetEntry timesheetEntry = TimesheetEntry.createDefault(timesheetId, LocalDate.of(2023, 8, 27));
        timesheetEntry.setWorkedMinutes(450);
        timesheetEntry.setBreakInMin(30);
        InputValidationException ex = assertThrows(InputValidationException.class, () -> {
            timesheetService.saveTimesheetEntry(timesheetEntry);
            timesheetService.saveTimesheetEntry(TimesheetEntry.createDefault(timesheetEntry.getTimesheetId(), timesheetEntry.getWorkdayDate()));
        });
        assertEquals("Workday already registered and update is not allowed! timesheetId=1, date=2023-08-27, hours=7.5, status=OPEN", ex.getMessage());
    }

    @Test
    public void getMostRecentTimesheetEntry() {
        Timesheet newTimesheet = Timesheet.createDefault(10011L, 200L, 2023, 9);
        Long timesheetId = timesheetService.saveTimesheet(newTimesheet);
        TimesheetEntry timesheetEntry = timesheetService.getMostRecentTimeSheetEntry(timesheetId);
        assertEquals(newTimesheet.getFromDate().toString(), timesheetEntry.getWorkdayDate().toString());

        Long timesheetEntryId = timesheetService.saveTimesheetEntry(timesheetEntry);
        TimesheetEntry newTimesheetEntry = timesheetService.getTimesheetEntry(timesheetEntryId);
        assertEquals("2023-09-01", newTimesheetEntry.getWorkdayDate().toString());
        assertEquals(timesheetEntryId, newTimesheetEntry.getId());
        assertEquals(timesheetId, newTimesheetEntry.getTimesheetId());

        TimesheetEntry mostRecentTimesheetEntry = timesheetService.getMostRecentTimeSheetEntry(timesheetId);
        assertEquals("2023-09-01", mostRecentTimesheetEntry.getWorkdayDate().toString());
        assertNull(mostRecentTimesheetEntry.getId());
        assertEquals(timesheetId, mostRecentTimesheetEntry.getTimesheetId());
    }
}
