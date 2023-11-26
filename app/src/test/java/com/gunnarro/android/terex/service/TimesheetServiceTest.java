package com.gunnarro.android.terex.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.gunnarro.android.terex.TestData;
import com.gunnarro.android.terex.domain.entity.Timesheet;
import com.gunnarro.android.terex.domain.entity.TimesheetEntry;
import com.gunnarro.android.terex.domain.entity.TimesheetSummary;
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
class TimesheetServiceTest {

    private TimesheetService timesheetService;

    @Mock
    private TimesheetRepository timesheetRepositoryMock;

    @BeforeEach
    public void setup() {
        timesheetService = new TimesheetService(timesheetRepositoryMock);
    }


    @Test
    void saveTimesheet_new() throws ExecutionException, InterruptedException {
        when(timesheetRepositoryMock.getTimesheet(anyString(), anyString(), anyInt(), anyInt())).thenReturn(null);
        when(timesheetRepositoryMock.insertTimesheet(any())).thenReturn(23L);
        assertEquals(23, timesheetService.saveTimesheet(Timesheet.createDefault("gunnarro", "test-project", 2023, 11)));
    }

    @Test
    void saveTimesheet_timesheet_new_already_exist() throws ExecutionException, InterruptedException {
        Timesheet timesheet = Timesheet.createDefault("gunnarro", "test-project", 2023, 11);
        timesheet.setId(23L);
        when(timesheetRepositoryMock.getTimesheet(anyString(), anyString(), anyInt(), anyInt())).thenReturn(timesheet);
        TerexApplicationException ex = assertThrows(TerexApplicationException.class, () -> {
            timesheetService.saveTimesheet(timesheet);
        });

        Assertions.assertEquals("Application error! Please Report error to app developer. Error=timesheet is already exist, timesheetId=23 NEW", ex.getMessage());
    }

    @Test
    void saveTimesheet_timesheet_update_already_billed() throws ExecutionException, InterruptedException {
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
    void updateTimesheet_worked_hours_and_days() {
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
    void updateTimesheetWorkedHoursAndDays_set_status_to_completed() {
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
    void saveTimesheet_timesheet_update_to_completed() throws ExecutionException, InterruptedException {
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
    void deleteTimesheet_status_not_allowed() {
        Timesheet timesheetExisting = Timesheet.createDefault("gunnarro", "test-project", 2023, 11);
        timesheetExisting.setId(23L);
        timesheetExisting.setStatus(Timesheet.TimesheetStatusEnum.BILLED.name());
        TerexApplicationException ex = assertThrows(TerexApplicationException.class, () -> {
            timesheetService.deleteTimesheet(timesheetExisting);
        });

        Assertions.assertEquals("Application error! Please Report error to app developer. Error=Timesheet is BILLED, not allowed to delete or update", ex.getMessage());
    }

    @Test
    void saveTimesheetEntry_new_already_exist() throws ExecutionException, InterruptedException {
        TimesheetEntry timesheetEntryExisting = TimesheetEntry.createDefault(23L, Timesheet.TimesheetStatusEnum.BILLED.name(), 30, LocalDate.now(), 450, 1900);
        TimesheetEntry timesheetEntry = TimesheetEntry.createDefault(null, Timesheet.TimesheetStatusEnum.NEW.name(), 30, LocalDate.now(), 450, 1900);

        when(timesheetRepositoryMock.getTimesheetEntry(timesheetEntry.getTimesheetId(), timesheetEntry.getWorkdayDate())).thenReturn(timesheetEntryExisting);
        TerexApplicationException ex = assertThrows(TerexApplicationException.class, () -> {
            timesheetService.saveTimesheetEntry(timesheetEntry);
        });

        Assertions.assertEquals("Application error! Please Report error to app developer. Error=timesheet entry already exist, timesheetId=null, status=BILLED", ex.getMessage());
    }

    @Test
    void saveTimesheetEntry_work_date_not_in_timesheet_date_range() throws ExecutionException, InterruptedException {
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
    void deleteTimesheetEntry_status_not_allowed() {
        TimesheetEntry timesheetEntry = TimesheetEntry.createDefault(23L, Timesheet.TimesheetStatusEnum.BILLED.name(), 30, LocalDate.now(), 450, 1900);
        TerexApplicationException ex = assertThrows(TerexApplicationException.class, () -> {
            timesheetService.deleteTimesheetEntry(timesheetEntry);
        });

        Assertions.assertEquals("Application error! Please Report error to app developer. Error=Timesheet entry is closed, not allowed to delete or update", ex.getMessage());
    }

    @Test
    void createTimesheetSummary_not_ready_for_billing() throws ExecutionException, InterruptedException {
        Timesheet timesheetExisting = Timesheet.createDefault("gunnarro", "test-project", 2023, 11);
        timesheetExisting.setId(23L);
        timesheetExisting.setStatus(Timesheet.TimesheetStatusEnum.ACTIVE.name());

        when(timesheetRepositoryMock.getTimesheet(timesheetExisting.getId())).thenReturn(timesheetExisting);

        TerexApplicationException ex = assertThrows(TerexApplicationException.class, () -> timesheetService.createTimesheetSummary(timesheetExisting.getId()));

        Assertions.assertEquals("Application error! Please Report error to app developer. Error=Application error, timesheet not fulfilled! timesheetId=23, status=ACTIVE", ex.getMessage());

    }

    @Test
    void createTimesheetSummary_no_entries() throws ExecutionException, InterruptedException {
        Timesheet timesheetExisting = Timesheet.createDefault("gunnarro", "test-project", 2023, 11);
        timesheetExisting.setId(23L);
        timesheetExisting.setStatus(Timesheet.TimesheetStatusEnum.COMPLETED.name());
        TimesheetEntry timesheetEntry = TimesheetEntry.createDefault(timesheetExisting.getId(), Timesheet.TimesheetStatusEnum.NEW.name(), 30, LocalDate.of(2023, 12, 21), 450, 1900);

        when(timesheetRepositoryMock.getTimesheet(timesheetExisting.getId())).thenReturn(timesheetExisting);
        when(timesheetRepositoryMock.getTimesheetEntryList(timesheetEntry.getTimesheetId())).thenReturn(List.of());

        TerexApplicationException ex = assertThrows(TerexApplicationException.class, () -> timesheetService.createTimesheetSummary(timesheetExisting.getId()));

        Assertions.assertEquals("Application error! Please Report error to app developer. Error=Application error, timesheet not ready for billing, no entries found! timesheetId=23, status=COMPLETED", ex.getMessage());
    }

    @Test
    void createTimesheetSummary() {
        Timesheet timesheetExisting = Timesheet.createDefault("gunnarro", "test-project", 2023, 11);
        timesheetExisting.setId(23L);
        timesheetExisting.setStatus(Timesheet.TimesheetStatusEnum.COMPLETED.name());
        TimesheetEntry timesheetEntry = TimesheetEntry.createDefault(timesheetExisting.getId(), Timesheet.TimesheetStatusEnum.NEW.name(), 30, LocalDate.of(2023, 12, 21), 450, 1900);

        List<TimesheetEntry> timesheetEntryList = TestData.generateTimesheetEntries(timesheetExisting.getYear(), timesheetExisting.getMonth());
        when(timesheetRepositoryMock.getTimesheet(timesheetExisting.getId())).thenReturn(timesheetExisting);
        when(timesheetRepositoryMock.getTimesheetEntryList(timesheetEntry.getTimesheetId())).thenReturn(timesheetEntryList);

        List<TimesheetSummary> timesheetSummaryList = timesheetService.createTimesheetSummary(timesheetExisting.getId());
        // week 1
        assertEquals(5, timesheetSummaryList.size());
        assertEquals(23, timesheetSummaryList.get(0).getTimesheetId());
        assertEquals(0, timesheetSummaryList.get(0).getTotalDaysOff());
        assertEquals(3, timesheetSummaryList.get(0).getTotalWorkedDays().intValue());
        assertEquals(22.5, timesheetSummaryList.get(0).getTotalWorkedHours());
        assertEquals(28125.0, timesheetSummaryList.get(0).getTotalBilledAmount());
        // week 2
        assertEquals(23, timesheetSummaryList.get(1).getTimesheetId());
        assertEquals(0, timesheetSummaryList.get(1).getTotalDaysOff());
        assertEquals(5, timesheetSummaryList.get(1).getTotalWorkedDays().intValue());
        assertEquals(37.5, timesheetSummaryList.get(1).getTotalWorkedHours());
        assertEquals(46875.0, timesheetSummaryList.get(1).getTotalBilledAmount());
        // week 3
        assertEquals(23, timesheetSummaryList.get(2).getTimesheetId());
        assertEquals(0, timesheetSummaryList.get(2).getTotalDaysOff());
        assertEquals(5, timesheetSummaryList.get(2).getTotalWorkedDays().intValue());
        assertEquals(37.5, timesheetSummaryList.get(2).getTotalWorkedHours());
        assertEquals(46875.0, timesheetSummaryList.get(2).getTotalBilledAmount());
        // week 4
        assertEquals(23, timesheetSummaryList.get(3).getTimesheetId());
        assertEquals(0, timesheetSummaryList.get(3).getTotalDaysOff());
        assertEquals(5, timesheetSummaryList.get(3).getTotalWorkedDays().intValue());
        assertEquals(37.5, timesheetSummaryList.get(3).getTotalWorkedHours());
        assertEquals(46875.0, timesheetSummaryList.get(3).getTotalBilledAmount());
        // week 5
        assertEquals(23, timesheetSummaryList.get(4).getTimesheetId());
        assertEquals(0, timesheetSummaryList.get(4).getTotalDaysOff());
        assertEquals(3, timesheetSummaryList.get(4).getTotalWorkedDays().intValue());
        assertEquals(22.5, timesheetSummaryList.get(4).getTotalWorkedHours());
        assertEquals(28125.0, timesheetSummaryList.get(4).getTotalBilledAmount());
    }
}
