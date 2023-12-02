package com.gunnarro.android.terex.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import com.gunnarro.android.terex.config.AppDatabase;
import com.gunnarro.android.terex.domain.entity.Timesheet;
import com.gunnarro.android.terex.domain.entity.TimesheetEntry;
import com.gunnarro.android.terex.exception.TerexApplicationException;
import com.gunnarro.android.terex.repository.TimesheetRepository;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

public class TimesheetServiceTest {

    AppDatabase db;
    private TimesheetRepository timesheetRepository;

    private TimesheetService timesheetService;

    @Before
    public void setup() {
        Context appContext = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(appContext, AppDatabase.class).build();
        timesheetRepository = new TimesheetRepository(appContext);
        timesheetService = new TimesheetService(timesheetRepository);
    }

    @Test
    public void getTimesheetsByStatus() {
        assertEquals(0, timesheetService.getTimesheets(Timesheet.TimesheetStatusEnum.NEW.name()).size());
        assertEquals(0, timesheetService.getTimesheets(Timesheet.TimesheetStatusEnum.ACTIVE.name()).size());
        assertEquals(0, timesheetService.getTimesheets(Timesheet.TimesheetStatusEnum.COMPLETED.name()).size());
        assertEquals(0, timesheetService.getTimesheets(Timesheet.TimesheetStatusEnum.BILLED.name()).size());
    }

    @Test
    public void newTimesheet() {
        Timesheet newTimesheet = Timesheet.createDefault("gunnarro", "terex", 2023, 11);
        newTimesheet.setDescription("Times used to develop android timesheet app");
        Long timesheetId = timesheetService.saveTimesheet(newTimesheet);
        Timesheet timesheet = timesheetService.getTimesheet(timesheetId);
        assertEquals("gunnarro", timesheet.getClientName());
        assertEquals("terex", timesheet.getProjectCode());
        assertEquals(2023, timesheet.getYear().intValue());
        assertEquals(11, timesheet.getMonth().intValue());
        assertEquals("2023-11-01", timesheet.getFromDate().toString());
        assertEquals("2023-11-30", timesheet.getToDate().toString());
        assertEquals("Times used to develop android timesheet app", timesheet.getDescription());
        assertTrue(timesheet.isNew());
        assertNotNull(timesheet.getCreatedDate());
        assertNotNull(timesheet.getLastModifiedDate());
        assertEquals(157, timesheet.getWorkingHoursInMonth().intValue());
        assertEquals(21, timesheet.getWorkingDaysInMonth().intValue());
        assertEquals(0, timesheet.getTotalWorkedHours().intValue());
        assertEquals(0, timesheet.getTotalWorkedDays().intValue());
    }

    @Test
    public void addTimesheetEntry() {
        Timesheet newTimesheet = Timesheet.createDefault("gunnarro", "terex", 2023, 11);
        newTimesheet.setDescription("Times used to develop android timesheet app");
        Long timesheetId = timesheetService.saveTimesheet(newTimesheet);
        TimesheetEntry timesheetEntry = TimesheetEntry.createDefault(timesheetId, LocalDate.of(2023, 11, 27));
        timesheetEntry.setWorkedMinutes(450);
        timesheetEntry.setBreakInMin(30);
        timesheetEntry.setHourlyRate(1900);
        TerexApplicationException ex = assertThrows(TerexApplicationException.class, () -> {
            timesheetService.saveTimesheetEntry(timesheetEntry);
        });

        assertEquals("Application error! Please Report error to app developer. Error=timesheet is already exist, timesheetId=1 NEW", ex.getMessage());
    }
}
