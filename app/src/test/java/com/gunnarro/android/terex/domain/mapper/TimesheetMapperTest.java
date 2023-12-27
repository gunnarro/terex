package com.gunnarro.android.terex.domain.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    void toTimesheetEntryDtoList() {
        LocalDate localDate = LocalDate.of(2023, 12, 1);
        List<TimesheetEntry> timesheetEntryList = TestData.generateTimesheetEntries(localDate.getYear(), localDate.getMonthValue());

        List<TimesheetEntryDto> timesheetEntryDtoList = TimesheetMapper.toTimesheetEntryDtoList(timesheetEntryList);
        assertEquals(21, timesheetEntryDtoList.size());
    }

    @Test
    void toTimesheetSummaryDtoList() {
        LocalDate localDate = LocalDate.of(2023, 12, 1);
        List<TimesheetSummary> timesheetSummaryList = TestData.buildTimesheetSummaryByWeek(localDate.getYear(), localDate.getMonthValue());

        List<TimesheetSummaryDto> timesheetSummaryDtoList = TimesheetMapper.toTimesheetSummaryDtoList(timesheetSummaryList);
        assertEquals(5, timesheetSummaryDtoList.size());
    }
}
