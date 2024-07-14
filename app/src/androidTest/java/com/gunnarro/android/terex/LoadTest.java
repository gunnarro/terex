package com.gunnarro.android.terex;

import static org.junit.Assert.assertEquals;

import com.gunnarro.android.terex.domain.entity.Timesheet;
import com.gunnarro.android.terex.domain.entity.TimesheetEntry;
import com.gunnarro.android.terex.repository.TimesheetRepository;
import com.gunnarro.android.terex.service.ProjectService;
import com.gunnarro.android.terex.service.TimesheetService;
import com.gunnarro.android.terex.service.UserAccountService;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

@Ignore
public class LoadTest extends IntegrationTestSetup {

    private TimesheetService timesheetService;


    @Before
    public void setup() {
        super.setupDatabase();
        timesheetService = new TimesheetService(new TimesheetRepository(), new UserAccountService(), new ProjectService());
    }

    @After
    public void cleanUp() {
        super.cleanUpDatabase();
    }

    @Test
    public void timesheetLoadTest() {
        for (long projectId = 200; projectId < 210; projectId++) {
            for (int month = 1; month < 12; month++) {
                createTimesheet(100L, 1L, projectId, 2023, month);
                createTimesheet(100L, 1L, projectId, 2024, month);
            }
        }
        assertEquals(220, timesheetService.getTimesheetsByStatus(Timesheet.TimesheetStatusEnum.ACTIVE.name()).size());
        assertEquals(30, timesheetService.getTimesheetEntryList(1L).size());
        assertEquals(6470, timesheetService.countTimesheetEntries());
    }

    private void createTimesheet(Long userId, Long clientId, Long projectId, Integer year, Integer month) {
        Timesheet newTimesheet = Timesheet.createDefault(userId, clientId, year, month);
        Long timesheetId = timesheetService.saveTimesheet(newTimesheet);
        List<TimesheetEntry> timesheetEntryList = TestData.createTimesheetEntriesForMonth(timesheetId, year, month);
        timesheetEntryList.forEach(e -> timesheetService.saveTimesheetEntry(e));
    }
}
