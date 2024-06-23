package com.gunnarro.android.terex.integration.jira;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.gunnarro.android.terex.integration.jira.model.Workload;

import org.junit.jupiter.api.Test;

import java.io.IOException;

class TempoApiTest {

    @Test
    void getWorkload() throws IOException {
        TempoApi tempoApi = new TempoApi();
        Workload workload = tempoApi.getWorkload();
        assertEquals(1, workload.getResults().size());
        assertEquals("2019-08-24", workload.getResults().get(0).startDate);
        assertEquals("20:06:00", workload.getResults().get(0).startTime);
        assertEquals(3600, workload.getResults().get(0).timeSpentSeconds);
        assertEquals(126, workload.getResults().get(0).tempoWorklogId);
        assertEquals(124, workload.getResults().get(0).issue.id);// jira task id
    }
}
