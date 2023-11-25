package com.gunnarro.android.terex.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.gunnarro.android.terex.domain.entity.Timesheet;
import com.gunnarro.android.terex.domain.entity.TimesheetEntry;
import com.gunnarro.android.terex.exception.TerexApplicationException;
import com.gunnarro.android.terex.repository.TimesheetRepository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutionException;

@ExtendWith(MockitoExtension.class)
public class TimesheetServiceTest {

    private TimesheetService timesheetService;

    @Mock
    private TimesheetRepository timesheetRepositoryMock;

    @BeforeEach
    public void setup() {
    //    timesheetRepositoryMock = Mockito.mock(TimesheetRepository.class);
        timesheetService = new TimesheetService(timesheetRepositoryMock);
    }


    @Test
    public void saveTimesheet_new() throws ExecutionException, InterruptedException {
        when(timesheetRepositoryMock.getTimesheet(anyString(), anyString(), anyInt(), anyInt())).thenReturn(null);
        when(timesheetRepositoryMock.insertTimesheet(any())).thenReturn(23L);
        assertEquals(23, timesheetService.saveTimesheet(Timesheet.createDefault("gunnarro", "test-project", 2023, 11)));
    }

    @Test
    public void saveTimesheet_timesheet_new_already_exist() throws ExecutionException, InterruptedException {
        Timesheet timesheet = Timesheet.createDefault("gunnarro", "test-project", 2023, 11);
        timesheet.setId(23L);
        when(timesheetRepositoryMock.getTimesheet(anyString(), anyString(), anyInt(), anyInt())).thenReturn(timesheet);
        TerexApplicationException ex = assertThrows(TerexApplicationException.class, () -> {
            timesheetService.saveTimesheet(timesheet);
        });

        Assertions.assertEquals("Application error! Please Report error to app developer. Error=timesheet is already exist, timesheetId=23 NEW", ex.getMessage());
    }

    @Test
    public void saveTimesheet_timesheet_update_already_billed() throws ExecutionException, InterruptedException {
        Timesheet timesheetExisting = Timesheet.createDefault("gunnarro", "test-project", 2023, 11);
        timesheetExisting.setId(23L);
        timesheetExisting.setStatus(Timesheet.TimesheetStatusEnum.BILLED.name());
        when(timesheetRepositoryMock.getTimesheet(anyString(), anyString(), anyInt(), anyInt())).thenReturn(timesheetExisting);
        Timesheet timesheetUpdated = Timesheet.createDefault("gunnarro", "test-project", 2023, 11);
        timesheetUpdated.setStatus(Timesheet.TimesheetStatusEnum.ACTIVE.name());
        TerexApplicationException ex = assertThrows(TerexApplicationException.class, () -> {
            timesheetService.saveTimesheet(timesheetUpdated);
        });

        Assertions.assertEquals("Application error! Please Report error to app developer. Error=timesheet is already billed, no changes is allowed. timesheetId=23 BILLED", ex.getMessage());
    }

    @Test
    public void updateTimesheet_worked_hours_and_days() {
        Timesheet timesheet = Timesheet.createDefault("gunnarro", "test-project", 2023, 11);
        timesheet.setId(23L);
        timesheet.setStatus(Timesheet.TimesheetStatusEnum.ACTIVE.name());
        TimesheetEntry timesheetEntry1 = TimesheetEntry.createDefault(1L, Timesheet.TimesheetStatusEnum.NEW.name(), 30, LocalDate.now(), 450, 1900);
        TimesheetEntry timesheetEntry2 = TimesheetEntry.createDefault(2L, Timesheet.TimesheetStatusEnum.NEW.name(), 30, LocalDate.now(), 450, 1900);

        when(timesheetRepositoryMock.getTimesheetEntryList(any())).thenReturn(List.of(timesheetEntry1, timesheetEntry2));
        when(timesheetRepositoryMock.getTimesheet(timesheet.getId())).thenReturn(timesheet);

        Timesheet timesheetUpdated = timesheetService.updateTimesheetWorkedHoursAndDays(timesheet.getId());
        assertEquals(15, timesheetUpdated.getTotalWorkedHours());
        assertEquals(2, timesheetUpdated.getTotalWorkedDays());
        assertEquals(157, timesheetUpdated.getWorkingHoursInMonth());
        assertEquals(21, timesheetUpdated.getWorkingDaysInMonth());
        assertEquals(Timesheet.TimesheetStatusEnum.ACTIVE.name(), timesheetUpdated.getStatus());
    }

    @Test
    public void updateTimesheetWorkedHoursAndDays_set_status_to_completed() {
        Timesheet timesheet = Timesheet.createDefault("gunnarro", "test-project", 2023, 11);
        timesheet.setId(23L);
        timesheet.setStatus(Timesheet.TimesheetStatusEnum.ACTIVE.name());
        TimesheetEntry timesheetEntry1 = TimesheetEntry.createDefault(1L, Timesheet.TimesheetStatusEnum.NEW.name(), 30, LocalDate.now(), 450, 1900);
        TimesheetEntry timesheetEntry2 = TimesheetEntry.createDefault(2L, Timesheet.TimesheetStatusEnum.NEW.name(), 30, LocalDate.now(), 450, 1900);
        // simulate worked hours for a month
        timesheetEntry1.setWorkedMinutes(5080);
        timesheetEntry2.setWorkedMinutes(5080);
        when(timesheetRepositoryMock.getTimesheetEntryList(any())).thenReturn(List.of(timesheetEntry1, timesheetEntry2));
        when(timesheetRepositoryMock.getTimesheet(timesheet.getId())).thenReturn(timesheet);

        Timesheet timesheetUpdated = timesheetService.updateTimesheetWorkedHoursAndDays(timesheet.getId());
        assertEquals(169, timesheetUpdated.getTotalWorkedHours());
        assertEquals(2, timesheetUpdated.getTotalWorkedDays());
        assertEquals(157, timesheetUpdated.getWorkingHoursInMonth());
        assertEquals(21, timesheetUpdated.getWorkingDaysInMonth());
        assertEquals(Timesheet.TimesheetStatusEnum.COMPLETED.name(), timesheetUpdated.getStatus());
    }


    @Test
    public void saveTimesheet_timesheet_update_to_completed() throws ExecutionException, InterruptedException {
        Timesheet timesheetExisting = Timesheet.createDefault("gunnarro", "test-project", 2023, 11);
        timesheetExisting.setId(23L);
        timesheetExisting.setStatus(Timesheet.TimesheetStatusEnum.ACTIVE.name());
        when(timesheetRepositoryMock.getTimesheet(anyString(), anyString(), anyInt(), anyInt())).thenReturn(timesheetExisting);
        when(timesheetRepositoryMock.updateTimesheet(any())).thenReturn(1);

        Timesheet timesheetUpdated = Timesheet.createDefault("gunnarro", "test-project", 2023, 11);
        timesheetUpdated.setId(timesheetExisting.getId());
        timesheetUpdated.setStatus(Timesheet.TimesheetStatusEnum.COMPLETED.name());
        assertNull(timesheetService.saveTimesheet(timesheetUpdated));
    }

    @Test
    public void deleteTimesheet_status_not_allowed() {
        Timesheet timesheetExisting = Timesheet.createDefault("gunnarro", "test-project", 2023, 11);
        timesheetExisting.setId(23L);
        timesheetExisting.setStatus(Timesheet.TimesheetStatusEnum.BILLED.name());
        TerexApplicationException ex = assertThrows(TerexApplicationException.class, () -> {
            timesheetService.deleteTimesheet(timesheetExisting);
        });

        Assertions.assertEquals("Application error! Please Report error to app developer. Error=Timesheet is BILLED, not allowed to delete or update", ex.getMessage());
    }

    @Test
    public void saveTimesheetEntry_new_already_exist() throws ExecutionException, InterruptedException {
        TimesheetEntry timesheetEntryExisting = TimesheetEntry.createDefault(23L, Timesheet.TimesheetStatusEnum.BILLED.name(), 30, LocalDate.now(), 450, 1900);
        TimesheetEntry timesheetEntry = TimesheetEntry.createDefault(null, Timesheet.TimesheetStatusEnum.NEW.name(), 30, LocalDate.now(), 450, 1900);

        when(timesheetRepositoryMock.getTimesheetEntry(timesheetEntry.getTimesheetId(), timesheetEntry.getWorkdayDate())).thenReturn(timesheetEntryExisting);
        TerexApplicationException ex = assertThrows(TerexApplicationException.class, () -> {
            timesheetService.saveTimesheetEntry(timesheetEntry);
        });

        Assertions.assertEquals("Application error! Please Report error to app developer. Error=timesheet entry already exist, timesheetId=null, status=BILLED", ex.getMessage());
    }

    @Test
    public void saveTimesheetEntry_work_date_not_in_timesheet_to_from_date_range() throws ExecutionException, InterruptedException {
        Timesheet timesheet = Timesheet.createDefault("gunnarro", "test-project", 2023, 11);
        timesheet.setId(1L);
        TimesheetEntry timesheetEntry = TimesheetEntry.createDefault(timesheet.getId(), Timesheet.TimesheetStatusEnum.NEW.name(), 30, LocalDate.of(2023, 12, 21), 450, 1900);

        when(timesheetRepositoryMock.getTimesheet(anyLong())).thenReturn(timesheet);
        when(timesheetRepositoryMock.getTimesheetEntry(timesheetEntry.getTimesheetId(), timesheetEntry.getWorkdayDate())).thenReturn(null);
        TerexApplicationException ex = assertThrows(TerexApplicationException.class, () -> {
            timesheetService.saveTimesheetEntry(timesheetEntry);
        });

        Assertions.assertEquals("Application error! Please Report error to app developer. Error=timesheet entry work date not in the to and from date range of the timesheet. 2023-12-21 <> 2023-11-01 - 2023-11-30", ex.getMessage());
    }

    @Test
    public void deleteTimesheetEntry_status_not_allowed() {
        TimesheetEntry timesheetEntry = TimesheetEntry.createDefault(23L, Timesheet.TimesheetStatusEnum.BILLED.name(), 30, LocalDate.now(), 450, 1900);
        TerexApplicationException ex = assertThrows(TerexApplicationException.class, () -> {
            timesheetService.deleteTimesheetEntry(timesheetEntry);
        });

        Assertions.assertEquals("Application error! Please Report error to app developer. Error=Timesheet entry is closed, not allowed to delete or update", ex.getMessage());
    }
}
