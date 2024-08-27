package com.gunnarro.android.terex.domain.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TimesheetSummaryDtoTest {

    @Test
    void timesheetSummaryDto() {
        TimesheetSummaryDto timesheetSummaryDto = new TimesheetSummaryDto();
        timesheetSummaryDto.setWeekInYear(44);
        timesheetSummaryDto.setTimesheetId(23L);
        timesheetSummaryDto.setYear(2024);
        timesheetSummaryDto.setCurrency("nok");
        timesheetSummaryDto.setSummedByPeriod("week");
        assertEquals("week", timesheetSummaryDto.getSummedByPeriod());
    }
}
