package com.gunnarro.android.terex.integration.jira;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.gunnarro.android.terex.domain.entity.TimesheetEntry;
import com.gunnarro.android.terex.integration.jira.model.Workload;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

class TempoApiTest {

    @Test
    void getTimesheetWorkload() throws IOException {
        TempoApi tempoApi = new TempoApi();
        Workload workload = tempoApi.getTimesheetWorkload();
        assertEquals(1, workload.getResults().size());
        assertEquals("2019-08-24", workload.getResults().get(0).startDate);
        assertEquals("20:06:00", workload.getResults().get(0).startTime);
        assertEquals(3600, workload.getResults().get(0).timeSpentSeconds);
        assertEquals(126, workload.getResults().get(0).tempoWorklogId);
        assertEquals(124, workload.getResults().get(0).issue.id);// jira task id
    }

    @Test
    void getTimesheetExportCvs() throws IOException {
        TempoApi tempoApi = new TempoApi();
        List<TimesheetEntry> timesheetEntryList = TempoDomainMapper.fromTempoTimesheetCvs(tempoApi.getTimesheetExportCvs());

        assertEquals(23, timesheetEntryList.size());
        assertEquals("2024-07-01", timesheetEntryList.get(0).getWorkdayDate().toString());
        assertEquals("08:00", timesheetEntryList.get(0).getStartTime().toString());
        assertEquals(27000, timesheetEntryList.get(0).getWorkedSeconds());
        assertEquals("7.5", timesheetEntryList.get(0).getWorkedHours());
        assertEquals("JIRA-TASK-234, test", timesheetEntryList.get(0).getComments());
    }
}
