package com.gunnarro.android.terex.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.content.res.AssetManager;

import com.gunnarro.android.terex.TestData;
import com.gunnarro.android.terex.domain.dto.TimesheetSummaryDto;
import com.gunnarro.android.terex.domain.entity.Timesheet;
import com.gunnarro.android.terex.domain.entity.TimesheetEntry;
import com.gunnarro.android.terex.domain.entity.TimesheetSummary;
import com.gunnarro.android.terex.exception.InputValidationException;
import com.gunnarro.android.terex.exception.TerexApplicationException;
import com.gunnarro.android.terex.repository.OrganizationRepository;
import com.gunnarro.android.terex.repository.TimesheetRepository;
import com.gunnarro.android.terex.utility.Utility;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;

@ExtendWith(MockitoExtension.class)
class TimesheetServiceTest {

    private TimesheetService timesheetService;

    @Mock
    private TimesheetRepository timesheetRepositoryMock;
    @Mock
    private OrganizationRepository OrganizationRepositoryMock;

    @BeforeEach
    public void setup() {
        timesheetService = new TimesheetService(timesheetRepositoryMock, OrganizationRepositoryMock);
    }

    @Test
    void getTimesheetForMonth() {
        List<TimesheetEntry> timesheetEntryList = new ArrayList<>();
        // get timesheet
        Timesheet timesheet = new Timesheet();
        timesheet.setFromDate(LocalDate.of(2023, 1, 1));
        timesheet.setToDate(LocalDate.of(2023, 1, Utility.getLastDayOfMonth(LocalDate.of(2023, 1, 1)).getDayOfMonth()));
        // interpolate missing days in the month.
        TimesheetEntry timesheetEntry = new TimesheetEntry();
        timesheetEntry.setTimesheetId(2L);
        for (LocalDate date = timesheet.getFromDate(); date.isBefore(timesheet.getToDate()); date = date.plusDays(1)) {
            boolean isInList = false;
            for (TimesheetEntry e : timesheetEntryList) {
                if (e.getWorkdayDate().equals(date)) {
                    isInList = true;
                    break;
                }
            }
            if (!isInList) {
                TimesheetEntry timesheetEntry1 = new TimesheetEntry();
                timesheetEntry1.setWorkdayDate(date);
                timesheetEntry1.setWorkedMinutes(0);
                timesheetEntryList.add(timesheetEntry1);
            }
        }
        timesheetEntryList.sort(Comparator.comparing(TimesheetEntry::getWorkdayDate));
        assertEquals(30, timesheetEntryList.size());
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
        InputValidationException ex = assertThrows(InputValidationException.class, () -> {
            timesheetService.saveTimesheet(timesheet);
        });

        Assertions.assertEquals("timesheet already exist. gunnarro test-project, status=NEW", ex.getMessage());
    }

    @Test
    void saveTimesheet_timesheet_update_already_billed() throws ExecutionException, InterruptedException {
        Timesheet timesheetExisting = Timesheet.createDefault("gunnarro", "test-project", 2023, 11);
        timesheetExisting.setId(23L);
        timesheetExisting.setStatus(Timesheet.TimesheetStatusEnum.BILLED.name());
        when(timesheetRepositoryMock.getTimesheet(anyString(), anyString(), anyInt(), anyInt())).thenReturn(timesheetExisting);
        Timesheet timesheetUpdated = Timesheet.createDefault("gunnarro", "test-project", 2023, 11);
        timesheetUpdated.setStatus(Timesheet.TimesheetStatusEnum.ACTIVE.name());
        InputValidationException ex = assertThrows(InputValidationException.class, () -> {
            timesheetService.saveTimesheet(timesheetUpdated);
        });

        Assertions.assertEquals("timesheet is already billed, no changes is allowed. gunnarro test-project, status=BILLED", ex.getMessage());
    }

    @Test
    void updateTimesheet_worked_hours_and_days() {
        Timesheet timesheet = Timesheet.createDefault("gunnarro", "test-project", 2023, 11);
        timesheet.setId(23L);
        timesheet.setStatus(Timesheet.TimesheetStatusEnum.ACTIVE.name());
        TimesheetEntry timesheetEntry1 = TimesheetEntry.createDefault(1L, LocalDate.now());
        timesheetEntry1.setWorkedMinutes(450);
        timesheetEntry1.setHourlyRate(1900);
        TimesheetEntry timesheetEntry2 = TimesheetEntry.createDefault(2L, LocalDate.now());
        timesheetEntry2.setWorkedMinutes(450);
        timesheetEntry2.setHourlyRate(1900);

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
        Timesheet timesheet = Timesheet.createDefault("gunnarro", "test-project", 2023, 11);
        timesheet.setId(23L);
        timesheet.setStatus(Timesheet.TimesheetStatusEnum.ACTIVE.name());
        TimesheetEntry timesheetEntry1 = TimesheetEntry.createDefault(1L, LocalDate.now());
        TimesheetEntry timesheetEntry2 = TimesheetEntry.createDefault(2L, LocalDate.now());
        // simulate worked hours for a month
        timesheetEntry1.setWorkedMinutes(5080);
        timesheetEntry2.setWorkedMinutes(5080);
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
        InputValidationException ex = assertThrows(InputValidationException.class, () -> {
            timesheetService.deleteTimesheet(timesheetExisting);
        });

        Assertions.assertEquals("Timesheet is BILLED, not allowed to delete or update", ex.getMessage());
    }

    @Test
    void saveTimesheetEntry_new_already_exist() throws ExecutionException, InterruptedException {
        TimesheetEntry timesheetEntryExisting = TimesheetEntry.createDefault(23L, LocalDate.of(2023, 12, 2));
        timesheetEntryExisting.setStatus(TimesheetEntry.TimesheetEntryStatusEnum.CLOSED.name());
        TimesheetEntry timesheetEntry = TimesheetEntry.createDefault(null, LocalDate.now());

        when(timesheetRepositoryMock.getTimesheetEntry(timesheetEntry.getTimesheetId(), timesheetEntry.getWorkdayDate())).thenReturn(timesheetEntryExisting);
        InputValidationException ex = assertThrows(InputValidationException.class, () -> {
            timesheetService.saveTimesheetEntry(timesheetEntry);
        });

        Assertions.assertEquals("timesheet entry have status billed, no changes is allowed. workday date=2023-12-02, status=CLOSED", ex.getMessage());
    }

    @Test
    void saveTimesheetEntry_work_date_after_timesheet_to_date() throws ExecutionException, InterruptedException {
        Timesheet timesheet = Timesheet.createDefault("gunnarro", "test-project", 2023, 11);
        timesheet.setId(1L);
        TimesheetEntry timesheetEntryAfterToDate = TimesheetEntry.createDefault(timesheet.getId(), LocalDate.of(2023, 12, 21));

        when(timesheetRepositoryMock.getTimesheet(anyLong())).thenReturn(timesheet);
        when(timesheetRepositoryMock.getTimesheetEntry(timesheetEntryAfterToDate.getTimesheetId(), timesheetEntryAfterToDate.getWorkdayDate())).thenReturn(null);
        InputValidationException ex = assertThrows(InputValidationException.class, () -> {
            timesheetService.saveTimesheetEntry(timesheetEntryAfterToDate);
        });
        Assertions.assertEquals("timesheet entry work date not in the to and from date range of the timesheet. 2023-12-21 <> 2023-11-01 - 2023-11-30", ex.getMessage());
    }

    @Test
    void saveTimesheetEntry_work_date_before_timesheet_from_date() throws ExecutionException, InterruptedException {
        Timesheet timesheet = Timesheet.createDefault("gunnarro", "test-project", 2023, 11);
        timesheet.setId(1L);
        TimesheetEntry timesheetEntryBeforeFromDate = TimesheetEntry.createDefault(timesheet.getId(), LocalDate.of(2023, 10, 2));
        when(timesheetRepositoryMock.getTimesheet(anyLong())).thenReturn(timesheet);
        when(timesheetRepositoryMock.getTimesheetEntry(timesheetEntryBeforeFromDate.getTimesheetId(), timesheetEntryBeforeFromDate.getWorkdayDate())).thenReturn(null);
        InputValidationException ex = assertThrows(InputValidationException.class, () -> {
            timesheetService.saveTimesheetEntry(timesheetEntryBeforeFromDate);
        });
        Assertions.assertEquals("timesheet entry work date not in the to and from date range of the timesheet. 2023-10-02 <> 2023-11-01 - 2023-11-30", ex.getMessage());
    }

    @Test
    void deleteTimesheetEntry_status_not_allowed() {
        TimesheetEntry timesheetEntry = TimesheetEntry.createDefault(23L, LocalDate.of(2023, 12, 2));
        timesheetEntry.setStatus(TimesheetEntry.TimesheetEntryStatusEnum.CLOSED.name());
        InputValidationException ex = assertThrows(InputValidationException.class, () -> {
            timesheetService.deleteTimesheetEntry(timesheetEntry);
        });

        Assertions.assertEquals("Timesheet entry is closed, not allowed to delete or update", ex.getMessage());
    }

    @Test
    void createTimesheetSummary_not_ready_for_billing() {
        Timesheet timesheetExisting = Timesheet.createDefault("gunnarro", "test-project", 2023, 11);
        timesheetExisting.setId(23L);
        timesheetExisting.setStatus(Timesheet.TimesheetStatusEnum.ACTIVE.name());

        when(timesheetRepositoryMock.getTimesheet(timesheetExisting.getId())).thenReturn(timesheetExisting);

        TerexApplicationException ex = assertThrows(TerexApplicationException.class, () -> timesheetService.createTimesheetSummaryForBilling(timesheetExisting.getId()));

        Assertions.assertEquals("Application error! Please Report error to app developer. Error=Application error, timesheet not fulfilled! timesheetId=23, status=ACTIVE", ex.getMessage());
    }

    @Test
    void createTimesheetSummary_no_entries() {
        Timesheet timesheetExisting = Timesheet.createDefault("gunnarro", "test-project", 2023, 11);
        timesheetExisting.setId(23L);
        timesheetExisting.setStatus(Timesheet.TimesheetStatusEnum.COMPLETED.name());
        TimesheetEntry timesheetEntry = TimesheetEntry.createDefault(timesheetExisting.getId(), LocalDate.of(2023, 12, 21));
        timesheetEntry.setWorkedMinutes(450);
        timesheetEntry.setBreakInMin(30);
        timesheetEntry.setHourlyRate(1900);

        when(timesheetRepositoryMock.getTimesheet(timesheetExisting.getId())).thenReturn(timesheetExisting);
        when(timesheetRepositoryMock.getTimesheetEntryList(timesheetEntry.getTimesheetId())).thenReturn(List.of());

        TerexApplicationException ex = assertThrows(TerexApplicationException.class, () -> timesheetService.createTimesheetSummaryForBilling(timesheetExisting.getId()));

        Assertions.assertEquals("Application error! Please Report error to app developer. Error=Application error, timesheet not ready for billing, no entries found! timesheetId=23, status=COMPLETED", ex.getMessage());
    }

    @Test
    void getMostRecentTimeSheetEntry_no_emtries() {
        Timesheet timesheet = Timesheet.createDefault("gunnarro", "test-project", 2023, 11);
        when(timesheetRepositoryMock.getTimesheet(timesheet.getId())).thenReturn(timesheet);
        when(timesheetRepositoryMock.getMostRecentTimeSheetEntry(timesheet.getId())).thenReturn(null);
        assertEquals("2023-11-01", timesheetService.getMostRecentTimeSheetEntry(timesheet.getId()).getWorkdayDate().toString());
    }

    @Test
    void createTimesheetSummary() {
        Timesheet timesheetExisting = Timesheet.createDefault("gunnarro", "test-project", 2023, 11);
        timesheetExisting.setId(23L);
        timesheetExisting.setStatus(Timesheet.TimesheetStatusEnum.COMPLETED.name());
        TimesheetEntry timesheetEntry = TimesheetEntry.createDefault(timesheetExisting.getId(), LocalDate.of(2023, 12, 21));
        timesheetEntry.setWorkedMinutes(450);
        timesheetEntry.setBreakInMin(30);
        timesheetEntry.setHourlyRate(1900);

        List<TimesheetEntry> timesheetEntryList = TestData.generateTimesheetEntries(timesheetExisting.getYear(), timesheetExisting.getMonth());
        when(timesheetRepositoryMock.getTimesheet(timesheetExisting.getId())).thenReturn(timesheetExisting);
        when(timesheetRepositoryMock.getTimesheetEntryList(timesheetEntry.getTimesheetId())).thenReturn(timesheetEntryList);

        List<TimesheetSummaryDto> timesheetSummaryDtoList = timesheetService.createTimesheetSummaryForBilling(timesheetExisting.getId());
        // week 1
        assertEquals(5, timesheetSummaryDtoList.size());
        assertEquals(23, timesheetSummaryDtoList.get(0).getTimesheetId());
        assertEquals(0, timesheetSummaryDtoList.get(0).getTotalDaysOff());
        assertEquals(3, timesheetSummaryDtoList.get(0).getTotalWorkedDays().intValue());
        assertEquals("22.5", timesheetSummaryDtoList.get(0).getTotalWorkedHours());
        assertEquals("28125.00", timesheetSummaryDtoList.get(0).getTotalBilledAmount());
        // week 2
        assertEquals(23, timesheetSummaryDtoList.get(1).getTimesheetId());
        assertEquals(0, timesheetSummaryDtoList.get(1).getTotalDaysOff());
        assertEquals(5, timesheetSummaryDtoList.get(1).getTotalWorkedDays().intValue());
        assertEquals("37.5", timesheetSummaryDtoList.get(1).getTotalWorkedHours());
        assertEquals("46875.00", timesheetSummaryDtoList.get(1).getTotalBilledAmount());
        // week 3
        assertEquals(23, timesheetSummaryDtoList.get(2).getTimesheetId());
        assertEquals(0, timesheetSummaryDtoList.get(2).getTotalDaysOff());
        assertEquals(5, timesheetSummaryDtoList.get(2).getTotalWorkedDays().intValue());
        assertEquals("37.5", timesheetSummaryDtoList.get(2).getTotalWorkedHours());
        assertEquals("46875.00", timesheetSummaryDtoList.get(2).getTotalBilledAmount());
        // week 4
        assertEquals(23, timesheetSummaryDtoList.get(3).getTimesheetId());
        assertEquals(0, timesheetSummaryDtoList.get(3).getTotalDaysOff());
        assertEquals(5, timesheetSummaryDtoList.get(3).getTotalWorkedDays().intValue());
        assertEquals("37.5", timesheetSummaryDtoList.get(3).getTotalWorkedHours());
        assertEquals("46875.00", timesheetSummaryDtoList.get(3).getTotalBilledAmount());
        // week 5
        assertEquals(23, timesheetSummaryDtoList.get(4).getTimesheetId());
        assertEquals(0, timesheetSummaryDtoList.get(4).getTotalDaysOff());
        assertEquals(3, timesheetSummaryDtoList.get(4).getTotalWorkedDays().intValue());
        assertEquals("22.5", timesheetSummaryDtoList.get(4).getTotalWorkedHours());
        assertEquals("28125.00", timesheetSummaryDtoList.get(4).getTotalBilledAmount());
    }

    @Test
    void createTimesheetListHtml() throws IOException {
        Timesheet timesheet = new Timesheet();
        timesheet.setId(23L);
        timesheet.setFromDate(LocalDate.of(2023, 12, 1));
        timesheet.setToDate(LocalDate.of(2023, 12, 31));

        File mustacheTemplateFile = new File("src/main/assets/" + InvoiceService.InvoiceAttachmentTypesEnum.CLIENT_TIMESHEET.getTemplate());
        Context applicationContextMock = mock(Context.class);
        AssetManager assetManagerMock = mock(AssetManager.class);
        when(assetManagerMock.open(anyString())).thenReturn(new FileInputStream(mustacheTemplateFile));
        when(applicationContextMock.getAssets()).thenReturn(assetManagerMock);
        List<TimesheetEntry> timesheetEntryList = TestData.generateTimesheetEntries(timesheet.getFromDate().getYear(), timesheet.getFromDate().getMonthValue());

        when(timesheetRepositoryMock.getTimesheet(anyLong())).thenReturn(timesheet);
        when(timesheetRepositoryMock.getTimesheetEntryList(anyLong())).thenReturn(timesheetEntryList);

        String templateHtml = timesheetService.createTimesheetListHtml(23L, applicationContextMock);
        Assertions.assertNotNull(templateHtml);
        //saveToFile("src/test/" + InvoiceService.InvoiceAttachmentTypesEnum.CLIENT_TIMESHEET.getFileName() + ".html", templateHtml);
    }

    @Test
    void createTimesheetSummaryHtml() throws IOException {
        Timesheet timesheet = new Timesheet();
        timesheet.setId(23L);
        timesheet.setFromDate(LocalDate.of(2023, 12, 1));
        timesheet.setToDate(LocalDate.of(2023, 12, 31));
        File mustacheTemplateFile = new File("src/main/assets/" + InvoiceService.InvoiceAttachmentTypesEnum.TIMESHEET_SUMMARY.getTemplate());
        Context applicationContextMock = mock(Context.class);
        AssetManager assetManagerMock = mock(AssetManager.class);
        when(assetManagerMock.open(anyString())).thenReturn(new FileInputStream(mustacheTemplateFile));
        when(applicationContextMock.getAssets()).thenReturn(assetManagerMock);

        when(timesheetRepositoryMock.getTimesheet(anyLong())).thenReturn(timesheet);
        when(timesheetRepositoryMock.getTimesheetEntryList(anyLong())).thenReturn(TestData.generateTimesheetEntries(2023, 12));
        String templateHtml = timesheetService.createTimesheetSummaryHtml(23L, applicationContextMock);
        Assertions.assertNotNull(templateHtml);
        // saveToFile("src/test/" + InvoiceService.InvoiceAttachmentTypesEnum.TIMESHEET_SUMMARY.getFileName() + ".html", templateHtml);
    }

    @Test
    void createTimesheetSummaryAttachmentHtml() throws IOException {
        Timesheet timesheet = Timesheet.createDefault("gunnarro", "test-project", 2023, 11);
        timesheet.setId(23L);
        File mustacheTemplateFile = new File("src/main/assets/" + InvoiceService.InvoiceAttachmentTypesEnum.TIMESHEET_SUMMARY.getTemplate());
        Context applicationContextMock = mock(Context.class);
        AssetManager assetManagerMock = mock(AssetManager.class);
        when(assetManagerMock.open(anyString())).thenReturn(new FileInputStream(mustacheTemplateFile));
        when(applicationContextMock.getAssets()).thenReturn(assetManagerMock);

        List<TimesheetSummary> timesheetSummaryList = TestData.buildTimesheetSummaryByWeek(23L, 2023, 1);
        when(timesheetRepositoryMock.getTimesheet(anyLong())).thenReturn(timesheet);
        when(timesheetRepositoryMock.getTimesheetSummary(anyLong())).thenReturn(timesheetSummaryList);
        String templateHtml = timesheetService.createTimesheetSummaryAttachmentHtml(23L, applicationContextMock);
        Assertions.assertNotNull(templateHtml);
        // saveToFile("src/test/" + InvoiceService.InvoiceAttachmentTypesEnum.TIMESHEET_SUMMARY.getFileName() + ".html", templateHtml);
    }

    private void saveToFile(String filePath, String content) throws IOException {
        File myObj = new File(filePath);
        if (myObj.createNewFile()) {
            System.out.println("File created: " + myObj.getName());
        } else {
            System.out.println("File already exists.");
        }
        try (FileWriter fileWriter = new FileWriter(Path.of(filePath).toFile())) {
            fileWriter.write(content);
        }
    }
}
