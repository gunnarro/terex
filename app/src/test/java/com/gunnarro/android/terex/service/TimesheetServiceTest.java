package com.gunnarro.android.terex.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.gunnarro.android.terex.domain.entity.Timesheet;
import com.gunnarro.android.terex.exception.TerexApplicationException;
import com.gunnarro.android.terex.repository.TimesheetRepository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    public void saveTimesheet_timesheet_update_to_completed() throws ExecutionException, InterruptedException {
        Timesheet timesheetExisting = Timesheet.createDefault("gunnarro", "test-project", 2023, 11);
        timesheetExisting.setId(23L);
        timesheetExisting.setStatus(Timesheet.TimesheetStatusEnum.ACTIVE.name());
        when(timesheetRepositoryMock.getTimesheet(anyString(), anyString(), anyInt(), anyInt())).thenReturn(timesheetExisting);
        when(timesheetRepositoryMock.updateTimesheet(any())).thenReturn(1);

        Timesheet timesheetUpdated = Timesheet.createDefault("gunnarro", "test-project", 2023, 11);
        timesheetUpdated.setId(timesheetExisting.getId());
        timesheetUpdated.setStatus(Timesheet.TimesheetStatusEnum.COMPLETED.name());
        assertEquals(null, timesheetService.saveTimesheet(timesheetUpdated));
    }

    @Test
    public void saveTimesheet_timesheet_delete_not_allowed() throws ExecutionException, InterruptedException {
        Timesheet timesheetExisting = Timesheet.createDefault("gunnarro", "test-project", 2023, 11);
        timesheetExisting.setId(23L);
        timesheetExisting.setStatus(Timesheet.TimesheetStatusEnum.BILLED.name());
        TerexApplicationException ex = assertThrows(TerexApplicationException.class, () -> {
            timesheetService.deleteTimesheet(timesheetExisting);
        });

        Assertions.assertEquals("Timesheet is BILLED, not allowed to delete or update", ex.getMessage());
    }
}
