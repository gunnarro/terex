package com.gunnarro.android.terex.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.gunnarro.android.terex.TestData;
import com.gunnarro.android.terex.domain.dto.ProjectDto;
import com.gunnarro.android.terex.domain.dto.TimesheetSummaryDto;
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
import java.util.function.Predicate;

@ExtendWith(MockitoExtension.class)
class TimesheetServiceSummaryTest {

    private TimesheetService timesheetService;

    @Mock
    private TimesheetRepository timesheetRepositoryMock;
    @Mock
    private UserAccountService userAccountServiceMock;
    @Mock
    private ProjectService projectServiceMock;

    @BeforeEach
    public void setup() {
        timesheetService = new TimesheetService(timesheetRepositoryMock, userAccountServiceMock, projectServiceMock);
    }

    @Test
    void createTimesheetSummary_not_ready_for_billing() {
        Timesheet timesheetExisting = Timesheet.createDefault(100L, 10L, 200L, 2023, 11);
        timesheetExisting.setId(23L);
        timesheetExisting.setStatus(Timesheet.TimesheetStatusEnum.ACTIVE.name());

        when(timesheetRepositoryMock.getTimesheet(timesheetExisting.getId())).thenReturn(timesheetExisting);

        TerexApplicationException ex = assertThrows(TerexApplicationException.class, () -> timesheetService.createTimesheetSummaryForBilling(timesheetExisting.getId()));

        Assertions.assertEquals("Application error! Please Report error to app developer. Error=Application error, timesheet not fulfilled! timesheetId=23, status=ACTIVE", ex.getMessage());
    }

    @Test
    void createTimesheetSummary_no_entries() {
        Timesheet timesheetExisting = Timesheet.createDefault(100L, 10L, 200L, 2023, 11);
        timesheetExisting.setId(23L);
        timesheetExisting.setStatus(Timesheet.TimesheetStatusEnum.COMPLETED.name());
        TimesheetEntry timesheetEntry = TimesheetEntry.createDefault(timesheetExisting.getId(), -1L, LocalDate.of(2023, 12, 21));
        timesheetEntry.setWorkedSeconds((long) 450 * 60);
        timesheetEntry.setBreakSeconds(30 * 60);

        when(timesheetRepositoryMock.getTimesheet(timesheetExisting.getId())).thenReturn(timesheetExisting);
        when(timesheetRepositoryMock.getTimesheetEntryList(timesheetEntry.getTimesheetId())).thenReturn(List.of());

        TerexApplicationException ex = assertThrows(TerexApplicationException.class, () -> timesheetService.createTimesheetSummaryForBilling(timesheetExisting.getId()));

        Assertions.assertEquals("Application error! Please Report error to app developer. Error=Application error, timesheet not ready for billing, no entries found! timesheetId=23, status=COMPLETED", ex.getMessage());
    }

    @Test
    void createTimesheetSummaryForMount() {
        LocalDate month = LocalDate.of(2023, 11, 1);

        assertEquals("NOVEMBER", month.getMonth().name());
        assertEquals(11, month.getMonth().getValue());

        Timesheet timesheetExisting = Timesheet.createDefault(100L, 10L, 200L, month.getYear(), month.getMonthValue());
        timesheetExisting.setId(23L);
        assertEquals(month.toString(), timesheetExisting.getFromDate().toString());
        // Set timesheet ready for billing
        timesheetExisting.setStatus(Timesheet.TimesheetStatusEnum.COMPLETED.name());
        // create regular time sheet entry, i.e, worked 7.5 hours and 30 min break
        TimesheetEntry regularTimesheetEntry = TimesheetEntry.createDefault(timesheetExisting.getId(), -1L, LocalDate.of(month.getYear(), month.getMonthValue(), 21));
        regularTimesheetEntry.setWorkedSeconds((long) 450 * 60);
        regularTimesheetEntry.setBreakSeconds(30 * 60);

        ProjectDto projectDto = new ProjectDto();
        projectDto.setId(200L);
        projectDto.setHourlyRate(1250);

        when(projectServiceMock.getProject(projectDto.getId())).thenReturn(projectDto);
        List<TimesheetEntry> timesheetEntryList = TestData.generateTimesheetEntries(timesheetExisting.getYear(), timesheetExisting.getMonth(), 200L, List.of(), List.of());
        when(timesheetRepositoryMock.getTimesheet(timesheetExisting.getId())).thenReturn(timesheetExisting);
        when(timesheetRepositoryMock.getTimesheetEntryList(regularTimesheetEntry.getTimesheetId())).thenReturn(timesheetEntryList);

        // check generated month
        assertEquals(22, timesheetEntryList.size());
        List<TimesheetSummaryDto> timesheetSummaryDtoList = timesheetService.createTimesheetSummaryForBilling(timesheetExisting.getId());
        // number of week in the month
        assertEquals(5, timesheetSummaryDtoList.size());
        // check date
        assertEquals("WEEK", timesheetSummaryDtoList.get(0).getSummedByPeriod());
        // week 1
        assertTimesheetSummaryDto(timesheetSummaryDtoList.get(0), month, 23, 44, "WEEK", 3, 0, 0, "22.5", "28125.00");
        // week 2
        assertTimesheetSummaryDto(timesheetSummaryDtoList.get(1), month, 23, 45, "WEEK", 5, 0, 0, "37.5", "46875.00");
        // week 3
        assertTimesheetSummaryDto(timesheetSummaryDtoList.get(2), month, 23, 46, "WEEK", 5, 0, 0, "37.5", "46875.00");
        // week 4
        assertTimesheetSummaryDto(timesheetSummaryDtoList.get(3), month, 23, 47, "WEEK", 5, 0, 0, "37.5", "46875.00");
        // week 5
        assertTimesheetSummaryDto(timesheetSummaryDtoList.get(4), month, 23, 48, "WEEK", 4, 0, 0, "30.0", "37500.00");
    }

    @Test
    void createTimesheetSummaryForMountPartlyFilled() {
        LocalDate month = LocalDate.of(2023, 11, 1);
        assertEquals("NOVEMBER", month.getMonth().name());
        Timesheet timesheetExisting = Timesheet.createDefault(100L, 10L, 200L, month.getYear(), month.getMonthValue());
        timesheetExisting.setId(23L);
        // Set timesheet ready for billing
        timesheetExisting.setStatus(Timesheet.TimesheetStatusEnum.COMPLETED.name());
        // create regular time sheet entry, i.e, worked 7.5 hours and 30 min break
        TimesheetEntry regularTimesheetEntry = TimesheetEntry.createDefault(timesheetExisting.getId(), -1L, LocalDate.of(month.getYear(), month.getMonthValue(), 21));
        regularTimesheetEntry.setWorkedSeconds((long) 450 * 60);
        regularTimesheetEntry.setBreakSeconds(30 * 60);

        List<TimesheetEntry> timesheetEntryList = TestData.generateTimesheetEntries(timesheetExisting.getYear(), timesheetExisting.getMonth(), 200L, List.of(), List.of());
        // remove 3 days from week 3
        Predicate<TimesheetEntry> workday8 = item -> item.getWorkdayDate().isEqual(LocalDate.of(month.getYear(), month.getMonthValue(), 8));
        Predicate<TimesheetEntry> workday14 = item -> item.getWorkdayDate().isEqual(LocalDate.of(month.getYear(), month.getMonthValue(), 14));
        Predicate<TimesheetEntry> workday15 = item -> item.getWorkdayDate().isEqual(LocalDate.of(month.getYear(), month.getMonthValue(), 15));
        Predicate<TimesheetEntry> workday16 = item -> item.getWorkdayDate().isEqual(LocalDate.of(month.getYear(), month.getMonthValue(), 16));
        timesheetEntryList.removeIf(workday8.or(workday14).or(workday15).or(workday16));

        ProjectDto projectDto = new ProjectDto();
        projectDto.setId(200L);
        projectDto.setHourlyRate(1250);

        when(projectServiceMock.getProject(projectDto.getId())).thenReturn(projectDto);
        when(timesheetRepositoryMock.getTimesheet(timesheetExisting.getId())).thenReturn(timesheetExisting);
        when(timesheetRepositoryMock.getTimesheetEntryList(regularTimesheetEntry.getTimesheetId())).thenReturn(timesheetEntryList);
        // check generated month
        assertEquals(18, timesheetEntryList.size());

        List<TimesheetSummaryDto> timesheetSummaryDtoList = timesheetService.createTimesheetSummaryForBilling(timesheetExisting.getId());
        // number of week in the month
        assertEquals(5, timesheetSummaryDtoList.size());
        // check date
        assertEquals("WEEK", timesheetSummaryDtoList.get(0).getSummedByPeriod());
        // week 1
        assertTimesheetSummaryDto(timesheetSummaryDtoList.get(0), month, 23, 44, "WEEK", 3, 0, 0, "22.5", "28125.00");
        // week 2
        assertTimesheetSummaryDto(timesheetSummaryDtoList.get(1), month, 23, 45, "WEEK", 4, 0, 0, "30.0", "37500.00");
        // week 3
        assertTimesheetSummaryDto(timesheetSummaryDtoList.get(2), month, 23, 46, "WEEK", 2, 0, 0, "15.0", "18750.00");
        // week 4
        assertTimesheetSummaryDto(timesheetSummaryDtoList.get(3), month, 23, 47, "WEEK", 5, 0, 0, "37.5", "46875.00");
        // week 5
        assertTimesheetSummaryDto(timesheetSummaryDtoList.get(4), month, 23, 48, "WEEK", 4, 0, 0, "30.0", "37500.00");
    }

    @Test
    void createTimesheetSummaryForMountWithDaysOff() {
        LocalDate month = LocalDate.of(2023, 11, 1);
        assertEquals("NOVEMBER", month.getMonth().name());
        Timesheet timesheetExisting = Timesheet.createDefault(100L, 10L, 200L, month.getYear(), month.getMonthValue());
        timesheetExisting.setId(23L);
        // Set timesheet ready for billing
        timesheetExisting.setStatus(Timesheet.TimesheetStatusEnum.COMPLETED.name());
        // create regular time sheet entry, i.e, worked 7.5 hours and 30 min break
        TimesheetEntry regularTimesheetEntry = TimesheetEntry.createDefault(timesheetExisting.getId(),-1L, LocalDate.of(month.getYear(), month.getMonthValue(), 21));
        regularTimesheetEntry.setWorkedSeconds((long) 450 * 60);
        regularTimesheetEntry.setBreakSeconds(30 * 60);

        List<TimesheetEntry> timesheetEntryList = TestData.generateTimesheetEntries(timesheetExisting.getYear(), timesheetExisting.getMonth(), 200L, List.of(8, 16), List.of(14, 15));

        ProjectDto projectDto = new ProjectDto();
        projectDto.setId(200L);
        projectDto.setHourlyRate(1250);

        when(projectServiceMock.getProject(projectDto.getId())).thenReturn(projectDto);
        when(timesheetRepositoryMock.getTimesheet(timesheetExisting.getId())).thenReturn(timesheetExisting);
        when(timesheetRepositoryMock.getTimesheetEntryList(regularTimesheetEntry.getTimesheetId())).thenReturn(timesheetEntryList);

        // check generated month
        assertEquals(22, timesheetEntryList.size());
        List<TimesheetSummaryDto> timesheetSummaryDtoList = timesheetService.createTimesheetSummaryForBilling(timesheetExisting.getId());
        // number of week in the month
        assertEquals(5, timesheetSummaryDtoList.size());
        // check date
        assertEquals("WEEK", timesheetSummaryDtoList.get(0).getSummedByPeriod());
        // week 1
        assertTimesheetSummaryDto(timesheetSummaryDtoList.get(0), month, 23, 44, "WEEK", 3, 0, 0, "22.5", "28125.00");
        // week 2
        assertTimesheetSummaryDto(timesheetSummaryDtoList.get(1), month, 23, 45, "WEEK", 4, 0, 1, "30.0", "37500.00");
        // week 3
        assertTimesheetSummaryDto(timesheetSummaryDtoList.get(2), month, 23, 46, "WEEK", 2, 2, 1, "15.0", "18750.00");
        // week 4
        assertTimesheetSummaryDto(timesheetSummaryDtoList.get(3), month, 23, 47, "WEEK", 5, 0, 0, "37.5", "46875.00");
        // week 5
        assertTimesheetSummaryDto(timesheetSummaryDtoList.get(4), month, 23, 48, "WEEK", 4, 0, 0, "30.0", "37500.00");
    }


    private static void assertTimesheetSummaryDto(TimesheetSummaryDto timesheetSummaryDto, LocalDate month, long timesheetId, int weekInYear, String period, int workedDays, int vacationDays, int sickDays, String workedHours, String billedAmount) {
        assertEquals(timesheetId, timesheetSummaryDto.getTimesheetId());
        assertEquals(month.getYear(), timesheetSummaryDto.getYear());
        assertEquals(weekInYear, timesheetSummaryDto.getWeekInYear());
        assertEquals(period, timesheetSummaryDto.getSummedByPeriod());
        assertEquals(workedDays, timesheetSummaryDto.getTotalWorkedDays().intValue());
        assertEquals(vacationDays, timesheetSummaryDto.getTotalVacationDays());
        assertEquals(sickDays, timesheetSummaryDto.getTotalSickLeaveDays());
        assertEquals(workedHours, timesheetSummaryDto.getTotalWorkedHours());
        assertEquals(billedAmount, timesheetSummaryDto.getTotalBilledAmount());
    }
}
