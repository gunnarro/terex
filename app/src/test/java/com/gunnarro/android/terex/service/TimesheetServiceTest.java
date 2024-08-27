package com.gunnarro.android.terex.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gunnarro.android.terex.TestData;
import com.gunnarro.android.terex.domain.entity.Timesheet;
import com.gunnarro.android.terex.domain.entity.TimesheetEntry;
import com.gunnarro.android.terex.exception.InputValidationException;
import com.gunnarro.android.terex.repository.TimesheetRepository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.concurrent.ExecutionException;

@ExtendWith(MockitoExtension.class)
class TimesheetServiceTest {

    private TimesheetService timesheetService;

    @Mock
    private TimesheetRepository timesheetRepositoryMock;
    @Mock
    private UserAccountService userAccountServiceMock;
    @Mock
    private ProjectService projectServiceMock;
    @Mock
    private ClientService clientServiceMock;

    @BeforeEach
    public void setup() {
        timesheetService = new TimesheetService(timesheetRepositoryMock, userAccountServiceMock, projectServiceMock, clientServiceMock, null);
    }

    @Test
    void generateTimesheetForMonth() {
        assertEquals(30, TestData.createTimesheetEntriesForMonth(1L, LocalDate.of(2023, 1, 1)).size());
    }

    @Test
    void saveTimesheet_new() throws ExecutionException, InterruptedException {
        Timesheet timesheet = Timesheet.createDefault(100L, 10L, 2023, 11);
        when(timesheetRepositoryMock.find(anyLong(), anyLong(), anyInt(), anyInt())).thenReturn(null);
        when(timesheetRepositoryMock.insertTimesheet(any())).thenReturn(23L);
        assertEquals(23, timesheetService.saveTimesheet(timesheet));

        verify(timesheetRepositoryMock, times(1)).find(timesheet.getUserId(), timesheet.getClientId(), timesheet.getYear(), timesheet.getMonth());
        verify(timesheetRepositoryMock, times(1)).insertTimesheet(any());
    }

    @Disabled // no need for this, timesheet is simply updated
    @Test
    void saveTimesheet_timesheet_new_already_exist() {
        Timesheet timesheet = Timesheet.createDefault(100L, 10L, 2023, 11);
        timesheet.setId(23L);
        when(timesheetRepositoryMock.find(anyLong(), anyLong(), anyInt(), anyInt())).thenReturn(timesheet);
        InputValidationException ex = assertThrows(InputValidationException.class, () -> {
            timesheetService.saveTimesheet(timesheet);
        });

        Assertions.assertEquals("Timesheet already exist! Timesheet{userId=100, clientId=10, year=2023, month=11, status=OPEN}", ex.getMessage());
    }

    @Test
    void saveTimesheet_timesheet_update_already_billed() {
        Timesheet timesheetExisting = Timesheet.createDefault(100L, 10L, 2023, 11);
        timesheetExisting.setId(23L);
        timesheetExisting.setStatus(Timesheet.TimesheetStatusEnum.BILLED.name());
        when(timesheetRepositoryMock.find(anyLong(), anyLong(), anyInt(), anyInt())).thenReturn(timesheetExisting);
        Timesheet timesheetUpdated = Timesheet.createDefault(100L, 10L, 2023, 11);
        timesheetUpdated.setStatus(Timesheet.TimesheetStatusEnum.ACTIVE.name());
        InputValidationException ex = assertThrows(InputValidationException.class, () -> {
            timesheetService.saveTimesheet(timesheetUpdated);
        });

        Assertions.assertEquals("Timesheet is already billed, no changes is allowed. Timesheet{id=23, userId=100, clientId=10, year=2023, month=11, status=BILLED}", ex.getMessage());
    }

    @Test
    void updateTimesheet_worked_hours_and_days() {
        Timesheet timesheet = Timesheet.createDefault(100L, 10L, 2023, 11);
        timesheet.setId(23L);
        timesheet.setStatus(Timesheet.TimesheetStatusEnum.ACTIVE.name());
        TimesheetEntry timesheetEntry1 = TimesheetEntry.createDefault(1L, 11L, LocalDate.now());
        timesheetEntry1.setWorkedSeconds((long) 450 * 60);
        TimesheetEntry timesheetEntry2 = TimesheetEntry.createDefault(2L, 11L, LocalDate.now());
        timesheetEntry2.setWorkedSeconds((long) 450 * 60);

//        when(timesheetRepositoryMock.getTimesheetEntryList(any())).thenReturn(List.of(timesheetEntry1, timesheetEntry2));
//        when(timesheetRepositoryMock.getTimesheet(timesheet.getId())).thenReturn(timesheet);
/*
        Timesheet timesheetUpdated = timesheetService.updateTimesheetWorkedHoursAndDays(timesheet.getId());
        assertEquals(15, timesheetUpdated.getTotalWorkedHours());
        assertEquals(2, timesheetUpdated.getTotalWorkedDays());
        assertEquals(165, timesheetUpdated.getWorkingHoursInMonth());
        assertEquals(22, timesheetUpdated.getWorkingDaysInMonth());
        assertEquals(Timesheet.TimesheetStatusEnum.ACTIVE.name(), timesheetUpdated.getStatus());

 */
    }

    @Test
    void updateTimesheetWorkedHoursAndDays_set_status_to_completed() {
        Timesheet timesheet = Timesheet.createDefault(100L, 10L, 2023, 11);
        timesheet.setId(23L);
        timesheet.setStatus(Timesheet.TimesheetStatusEnum.ACTIVE.name());
        TimesheetEntry timesheetEntry1 = TimesheetEntry.createDefault(1L, 11L, LocalDate.now());
        TimesheetEntry timesheetEntry2 = TimesheetEntry.createDefault(2L, 11L, LocalDate.now());
        // simulate worked hours for a month
        timesheetEntry1.setWorkedSeconds((long) 5080 * 60);
        timesheetEntry2.setWorkedSeconds((long) 5080 * 60);
//        when(timesheetRepositoryMock.getTimesheet(timesheet.getClientName(), timesheet.getProjectCode(), timesheet.getYear(), timesheet.getMonth())).thenReturn(timesheet);
//        when(timesheetRepositoryMock.getTimesheetEntryList(any())).thenReturn(List.of(timesheetEntry1, timesheetEntry2));
//        when(timesheetRepositoryMock.getTimesheet(timesheet.getId())).thenReturn(timesheet);
/*
        Timesheet timesheetUpdated = timesheetService.updateTimesheetWorkedHoursAndDays(timesheet.getId());
        assertEquals(169, timesheetUpdated.getTotalWorkedHours());
        assertEquals(2, timesheetUpdated.getTotalWorkedDays());
        assertEquals(165, timesheetUpdated.getWorkingHoursInMonth());
        assertEquals(22, timesheetUpdated.getWorkingDaysInMonth());
        assertEquals(Timesheet.TimesheetStatusEnum.COMPLETED.name(), timesheetUpdated.getStatus());

 */
    }

    @Test
    void saveTimesheet_timesheet_update_to_completed() throws ExecutionException, InterruptedException {
        Timesheet timesheetExisting = Timesheet.createDefault(100L, 10L, 2023, 11);
        timesheetExisting.setId(23L);
        timesheetExisting.setStatus(Timesheet.TimesheetStatusEnum.ACTIVE.name());
        when(timesheetRepositoryMock.find(anyLong(), anyLong(), anyInt(), anyInt())).thenReturn(timesheetExisting);
        when(timesheetRepositoryMock.updateTimesheet(any())).thenReturn(1);

        Timesheet timesheetUpdated = Timesheet.createDefault(100L, 10L, 2023, 11);
        timesheetUpdated.setId(timesheetExisting.getId());
        timesheetUpdated.setStatus(Timesheet.TimesheetStatusEnum.COMPLETED.name());
        assertNotNull(timesheetService.saveTimesheet(timesheetUpdated));
    }

    @Test
    void deleteTimesheet_status_not_allowed() {
        Timesheet timesheetExisting = Timesheet.createDefault(100L, 10L, 2023, 11);
        timesheetExisting.setId(23L);
        timesheetExisting.setStatus(Timesheet.TimesheetStatusEnum.BILLED.name());

        when(timesheetRepositoryMock.getTimesheet(anyLong())).thenReturn(timesheetExisting);

        InputValidationException ex = assertThrows(InputValidationException.class, () -> {
            timesheetService.deleteTimesheet(timesheetExisting.getId());
        });

        Assertions.assertEquals("Timesheet is BILLED, not allowed to delete or update", ex.getMessage());
    }

    @Test
    void saveTimesheetEntry_new_already_exist() {
        TimesheetEntry timesheetEntryExisting = TimesheetEntry.createDefault(23L, 11L, LocalDate.of(2023, 12, 2));
        timesheetEntryExisting.setStatus(TimesheetEntry.TimesheetEntryStatusEnum.CLOSED.name());
        TimesheetEntry timesheetEntry = TimesheetEntry.createDefault(null, null, LocalDate.now());

        when(timesheetRepositoryMock.getTimesheetEntry(timesheetEntry.getTimesheetId(), timesheetEntry.getProjectId(), timesheetEntry.getWorkdayDate())).thenReturn(timesheetEntryExisting);
        InputValidationException ex = assertThrows(InputValidationException.class, () -> {
            timesheetService.saveTimesheetEntry(timesheetEntry);
        });

        Assertions.assertEquals("timesheet entry have status closed, no changes is allowed. workday date=2023-12-02, status=CLOSED", ex.getMessage());
    }

    @Test
    void saveTimesheetEntry_work_date_after_timesheet_to_date() {
        Timesheet timesheet = Timesheet.createDefault(100L, 10L, 2023, 11);
        timesheet.setId(1L);
        TimesheetEntry timesheetEntryAfterToDate = TimesheetEntry.createDefault(timesheet.getId(), -1L, LocalDate.of(2023, 12, 21));

        when(timesheetRepositoryMock.getTimesheet(anyLong())).thenReturn(timesheet);
        when(timesheetRepositoryMock.getTimesheetEntry(timesheetEntryAfterToDate.getTimesheetId(), timesheetEntryAfterToDate.getProjectId(), timesheetEntryAfterToDate.getWorkdayDate())).thenReturn(null);
        InputValidationException ex = assertThrows(InputValidationException.class, () -> {
            timesheetService.saveTimesheetEntry(timesheetEntryAfterToDate);
        });
        Assertions.assertEquals("timesheet entry work date not in the to and from date range of the timesheet! workDate = 2023-12-21 <> dateRange = 2023-11-01 - 2023-11-30", ex.getMessage());
    }

    @Test
    void saveTimesheetEntry_work_date_before_timesheet_from_date() {
        Timesheet timesheet = Timesheet.createDefault(100L, 10L, 2023, 11);
        timesheet.setId(1L);
        TimesheetEntry timesheetEntryBeforeFromDate = TimesheetEntry.createDefault(timesheet.getId(), -1L, LocalDate.of(2023, 10, 2));
        when(timesheetRepositoryMock.getTimesheet(anyLong())).thenReturn(timesheet);
        when(timesheetRepositoryMock.getTimesheetEntry(timesheetEntryBeforeFromDate.getTimesheetId(), timesheetEntryBeforeFromDate.getProjectId(), timesheetEntryBeforeFromDate.getWorkdayDate())).thenReturn(null);
        InputValidationException ex = assertThrows(InputValidationException.class, () -> {
            timesheetService.saveTimesheetEntry(timesheetEntryBeforeFromDate);
        });
        Assertions.assertEquals("timesheet entry work date not in the to and from date range of the timesheet! workDate = 2023-10-02 <> dateRange = 2023-11-01 - 2023-11-30", ex.getMessage());
    }

    @Test
    void deleteTimesheetEntry_status_not_allowed() {
        TimesheetEntry timesheetEntry = TimesheetEntry.createDefault(23L, 11L, LocalDate.of(2023, 12, 2));
        timesheetEntry.setId(11L);
        timesheetEntry.setStatus(TimesheetEntry.TimesheetEntryStatusEnum.CLOSED.name());

        when(timesheetRepositoryMock.getTimesheetEntry(anyLong())).thenReturn(timesheetEntry);
        InputValidationException ex = assertThrows(InputValidationException.class, () -> {
            timesheetService.deleteTimesheetEntry(timesheetEntry);
        });

        Assertions.assertEquals("Timesheet entry is closed, not allowed to delete or update", ex.getMessage());
    }

    @Test
    void getMostRecentTimeSheetEntry_no_emtries() {
        Timesheet timesheet = Timesheet.createDefault(100L, 10L, 2023, 11);
        timesheet.setId(33L);
        when(timesheetRepositoryMock.getTimesheet(timesheet.getId())).thenReturn(timesheet);
        when(timesheetRepositoryMock.getMostRecentTimeSheetEntry(timesheet.getId())).thenReturn(null);

        TimesheetEntry mostRecentTimesheetEntry = timesheetService.getMostRecentTimeSheetEntry(timesheet.getId());
        assertNull(mostRecentTimesheetEntry.getId());
        assertNull(mostRecentTimesheetEntry.getWorkdayDate());
        assertNull(mostRecentTimesheetEntry.getCreatedDate());
        assertNull(mostRecentTimesheetEntry.getLastModifiedDate());
        assertNull(mostRecentTimesheetEntry.getWorkdayYear());
        assertNull(mostRecentTimesheetEntry.getWorkdayMonth());
        assertNull(mostRecentTimesheetEntry.getWorkdayWeek());
        assertEquals(27000, mostRecentTimesheetEntry.getWorkedSeconds());
        assertEquals("08:00", mostRecentTimesheetEntry.getStartTime().toString());
        assertEquals("REGULAR", mostRecentTimesheetEntry.getType());
        assertEquals(-1, mostRecentTimesheetEntry.getProjectId());
        assertEquals(33, mostRecentTimesheetEntry.getTimesheetId());
    }

}
