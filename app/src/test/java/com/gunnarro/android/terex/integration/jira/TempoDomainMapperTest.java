package com.gunnarro.android.terex.integration.jira;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.gunnarro.android.terex.domain.entity.TimesheetEntry;

import org.junit.jupiter.api.Test;

class TempoDomainMapperTest {
    @Test
    void toLocalDateTime() {
        assertEquals("2024-07-08T08:00", TempoDomainMapper.toLocalDateTime("2024-07-08 08:00:00").toString());
        assertEquals("2024-07-08", TempoDomainMapper.toLocalDateTime("2024-07-08 08:00:00").toLocalDate().toString());
        assertEquals("08:00", TempoDomainMapper.toLocalDateTime("2024-07-08 08:00:00").toLocalTime().toString());
    }

    @Test
    void fromTempoTimesheetEntryCvs() {
        TimesheetEntry timesheetEntry = TempoDomainMapper.fromTempoTimesheetEntryCvs("JIRA-TASK-234,2024-07-01 08:00:00Z,7.5h,test");
        assertEquals("2024-07-01", timesheetEntry.getWorkdayDate().toString());
        assertEquals("08:00", timesheetEntry.getStartTime().toString());
        assertEquals(27000, timesheetEntry.getWorkedSeconds());
        assertEquals("7.5", timesheetEntry.getWorkedHours());
        assertEquals("JIRA-TASK-234, test", timesheetEntry.getComments());
    }
}
