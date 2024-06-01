package com.gunnarro.android.terex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import com.gunnarro.android.terex.config.AppDatabase;
import com.gunnarro.android.terex.domain.dto.ClientDto;
import com.gunnarro.android.terex.domain.entity.Timesheet;
import com.gunnarro.android.terex.domain.entity.TimesheetEntry;
import com.gunnarro.android.terex.repository.TimesheetRepository;
import com.gunnarro.android.terex.service.ClientService;
import com.gunnarro.android.terex.service.InvoiceService;
import com.gunnarro.android.terex.service.ProjectService;
import com.gunnarro.android.terex.service.TimesheetService;
import com.gunnarro.android.terex.service.UserAccountService;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

public class LoadTest {

    private TimesheetService timesheetService;
    private UserAccountService userAccountService;
    private ClientService clientService;
    private ProjectService projectService;
    private InvoiceService invoiceService;

    @Before
    public void setup() {
        Context appContext = ApplicationProvider.getApplicationContext();
        AppDatabase.init(appContext);
        timesheetService = new TimesheetService(new TimesheetRepository());
        userAccountService = new UserAccountService();
        clientService = new ClientService();
        projectService = new ProjectService();
        invoiceService = new InvoiceService();
    }

    @Test
    public void loadTest() {
        int year = 2024;
        for (int month = 1; month < 12; month++) {
            Timesheet newTimesheet = Timesheet.createDefault(100L, 200L, year, month);
            Long timesheetId = timesheetService.saveTimesheet(newTimesheet);
            List<TimesheetEntry> timesheetEntryList = TestData.createTimesheetEntriesForMonth(timesheetId, year, month);
            timesheetEntryList.forEach(e -> timesheetService.saveTimesheetEntry(e));

            newTimesheet = Timesheet.createDefault(100L, 201L, year, month);
            timesheetId = timesheetService.saveTimesheet(newTimesheet);
            timesheetEntryList = TestData.createTimesheetEntriesForMonth(timesheetId, year, month);
            timesheetEntryList.forEach(e -> timesheetService.saveTimesheetEntry(e));
        }
        assertEquals(22, timesheetService.getTimesheetsByStatus(Timesheet.TimesheetStatusEnum.ACTIVE.name()).size());
        //assertEquals(30, timesheetService.getTimesheetEntryList(timesheetId).size());
    }

    /**
     * Test all step in the timesheet flow, from time registration and to invoice generating.
     */
    @Test
    public void timesheetBusinessFlowTest() {
        Long userAccountId = userAccountService.saveUserAccount(TestData.createUserAccountDto(null, "guro-unit-test"));
        assertNotNull(userAccountId);
        ClientDto clientDto = TestData.createClientDto(null, "client-company-name");
        clientDto.setOrganizationDto(TestData.createOrganizationDto(null, "gunnarro as", "828777999"));
        Long clientId = clientService.saveClient(clientDto);
        assertNotNull(clientId);
        Long projectId = projectService.saveProject(TestData.createProjectDto(null, clientId, "Terex development"));
        assertNotNull(projectId);
        Timesheet newTimesheet = Timesheet.createDefault(userAccountId, projectId, 2024, 1);
        Long timesheetId = timesheetService.saveTimesheet(newTimesheet);
        assertNotNull(timesheetId);
        Long timesheetEntryId = timesheetService.saveTimesheetEntry(TimesheetEntry.createDefault(timesheetId, LocalDate.of(2024, 1, 5)));
        assertNotNull(timesheetEntryId);
        Timesheet timesheet = timesheetService.getTimesheet(timesheetId);
        timesheet.setStatus(Timesheet.TimesheetStatusEnum.COMPLETED.name());
        assertNotNull(timesheetService.saveTimesheet(timesheet));
        Long invoiceId = invoiceService.createInvoice(userAccountId, clientId, timesheetId, projectService.getProjectHourlyRate(timesheetId));
        assertNotNull(invoiceId);
    }
}
