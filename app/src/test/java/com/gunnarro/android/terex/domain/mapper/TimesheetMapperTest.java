package com.gunnarro.android.terex.domain.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.gunnarro.android.terex.TestData;
import com.gunnarro.android.terex.domain.dto.TimesheetEntryDto;
import com.gunnarro.android.terex.domain.dto.TimesheetSummaryDto;
import com.gunnarro.android.terex.domain.entity.TimesheetEntry;
import com.gunnarro.android.terex.domain.entity.TimesheetSummary;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

class TimesheetMapperTest {

    @Test
    void toTimesheetEntryDto() {
        LocalDate localDate = LocalDate.of(2023, 12, 1);
        List<TimesheetEntry> timesheetEntryList = TestData.generateTimesheetEntries(localDate.getYear(), localDate.getMonthValue());

        TimesheetEntryDto timesheetEntryDto = TimesheetMapper.toTimesheetEntryDto(timesheetEntryList.get(0));
        assertEquals("2023-12-01", timesheetEntryDto.getWorkdayDate().toString());
        assertEquals("08:00", timesheetEntryDto.getFromTime().toString());
        assertEquals("15:30", timesheetEntryDto.getToTime().toString());
        assertEquals("7.5", timesheetEntryDto.getWorkedHours());
        assertNull(timesheetEntryDto.getComments());
    }

    @Test
    void toTimesheetEntryDtoList() {
        LocalDate localDate = LocalDate.of(2023, 12, 1);
        List<TimesheetEntry> timesheetEntryList = TestData.generateTimesheetEntries(localDate.getYear(), localDate.getMonthValue());

        List<TimesheetEntryDto> timesheetEntryDtoList = TimesheetMapper.toTimesheetEntryDtoList(timesheetEntryList);
        assertEquals(21, timesheetEntryDtoList.size());
    }

    @Test
    void toTimesheetSummaryDto() {
        LocalDate localDate = LocalDate.of(2023, 12, 1);
        List<TimesheetSummary> timesheetSummaryList = TestData.buildTimesheetSummaryByWeek(23L, localDate.getYear(), localDate.getMonthValue());

        TimesheetSummaryDto timesheetSummaryDto = TimesheetMapper.toTimesheetSummaryDto(timesheetSummaryList.get(0));
        assertEquals("01.12", timesheetSummaryDto.getFromDateDDMM());
        assertEquals("9375.00", timesheetSummaryDto.getTotalBilledAmount());
        assertEquals("12", timesheetSummaryDto.getFromDateMM());
        assertEquals("48", timesheetSummaryDto.getWeekInYear().toString());
        assertEquals("7.5", timesheetSummaryDto.getTotalWorkedHours());
    }

    @Test
    void toTimesheetSummaryDtoList() {
        LocalDate localDate = LocalDate.of(2023, 12, 1);
        List<TimesheetSummary> timesheetSummaryList = TestData.buildTimesheetSummaryByWeek(23L, localDate.getYear(), localDate.getMonthValue());

        List<TimesheetSummaryDto> timesheetSummaryDtoList = TimesheetMapper.toTimesheetSummaryDtoList(timesheetSummaryList);
        assertEquals(5, timesheetSummaryDtoList.size());
    }
}
