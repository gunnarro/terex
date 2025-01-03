package com.gunnarro.android.terex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import androidx.test.core.app.ApplicationProvider;

import com.gunnarro.android.terex.domain.dto.ClientDto;
import com.gunnarro.android.terex.domain.dto.UserAccountDto;
import com.gunnarro.android.terex.domain.entity.Invoice;
import com.gunnarro.android.terex.domain.entity.InvoiceAttachment;
import com.gunnarro.android.terex.domain.entity.Timesheet;
import com.gunnarro.android.terex.domain.entity.TimesheetEntry;
import com.gunnarro.android.terex.domain.entity.TimesheetSummary;
import com.gunnarro.android.terex.exception.InputValidationException;
import com.gunnarro.android.terex.integration.jira.TempoApi;
import com.gunnarro.android.terex.repository.InvoiceRepository;
import com.gunnarro.android.terex.repository.TimesheetRepository;
import com.gunnarro.android.terex.service.ClientService;
import com.gunnarro.android.terex.service.InvoiceService;
import com.gunnarro.android.terex.service.ProjectService;
import com.gunnarro.android.terex.service.TimesheetService;
import com.gunnarro.android.terex.service.UserAccountService;
import com.gunnarro.android.terex.utility.Utility;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

public class FunctionalTest extends IntegrationTestSetup {

    private TimesheetService timesheetService;
    private UserAccountService userAccountService;
    private ClientService clientService;
    private ProjectService projectService;
    private InvoiceService invoiceService;

    @Before
    public void setup() {
        super.setupDatabase();
        userAccountService = new UserAccountService();
        projectService = new ProjectService();
        timesheetService = new TimesheetService(new TimesheetRepository(), new UserAccountService(), new ProjectService(), new ClientService(), new TempoApi());
        clientService = new ClientService();
        invoiceService = new InvoiceService();
    }

    @After
    public void cleanUp() {
        super.cleanUpDatabase();
    }

    /**
     * Test all step in the timesheet flow, from time registration and to invoice generating.
     */
    @Test
    public void timesheetBusinessFlowTest() {
        // create user account
        UserAccountDto userAccountDto = TestData.createUserAccountDto(null, "guro-unit-test");
        userAccountDto.setOrganizationDto(TestData.createOrganizationDto(null, "gunnarro test", "23232323"));
        Long userAccountId = userAccountService.saveUserAccount(userAccountDto);
        assertNotNull(userAccountId);

        // create client
        ClientDto clientDto = TestData.createClientDto(null, "client-company-name");
        clientDto.setOrganizationDto(TestData.createOrganizationDto(null, "gunnarro as", "828777999"));
        Long clientId = clientService.saveClient(clientDto);
        assertTrue(clientId > 0);

        // create project and assign it to the client
        // test with 2 projects, assigned to different timesheet entries
        Long devProjectId = projectService.saveProject(TestData.createProjectDto(null, clientId, "Terex development", 1060));
        assertTrue(devProjectId > 0);
        Long testProjectId = projectService.saveProject(TestData.createProjectDto(null, clientId, "Terex testing", 1240));
        assertTrue(testProjectId > 0);

        // create timesheet and assign it to the client
        Timesheet newTimesheet = Timesheet.createDefault(userAccountId, clientId, 2020, 1);
        Long timesheetId = timesheetService.saveTimesheet(newTimesheet);
        assertTrue(timesheetId > 0);
        assertEquals("2020-01-01", newTimesheet.getFromDate().toString());
        assertEquals("2020-01-31", newTimesheet.getToDate().toString());

        // add time entry to the timesheet and assign the entry to a project
        Long devTimesheetEntryId = timesheetService.saveTimesheetEntry(TimesheetEntry.createDefault(timesheetId, devProjectId, LocalDate.of(2020, 1, 1)));
        assertTrue(devTimesheetEntryId > 0);
        Long testTimesheetEntryId = timesheetService.saveTimesheetEntry(TimesheetEntry.createDefault(timesheetId, testProjectId, LocalDate.of(2020, 1, 2)));
        assertTrue(testTimesheetEntryId > 0);
        // sick and vacation should not be included in the invoice summary
        Long sickTimesheetEntryId = timesheetService.saveTimesheetEntry(TimesheetEntry.createNotWorked(timesheetId, testProjectId, LocalDate.of(2020, 1, 6), TimesheetEntry.TimesheetEntryTypeEnum.SICK.name()));
        assertTrue(sickTimesheetEntryId > 0);
        Long vacationTimesheetEntryId = timesheetService.saveTimesheetEntry(TimesheetEntry.createNotWorked(timesheetId, testProjectId, LocalDate.of(2020, 1, 7), TimesheetEntry.TimesheetEntryTypeEnum.VACATION.name()));
        assertTrue(vacationTimesheetEntryId > 0);

        // check number of timesheet entries
        assertEquals(1, timesheetService.getTimesheetDtoList(2020).size());
        assertEquals(2, timesheetService.getTimesheetDto(1L).getClientDto().getProjectList().size());

        // set timesheet ready for billing
        Timesheet timesheet = timesheetService.getTimesheet(timesheetId);
        timesheet.setStatus(Timesheet.TimesheetStatusEnum.COMPLETED.name());
        assertNotNull(timesheetService.saveTimesheet(timesheet));

        // create invoice based on the timesheet
        Long invoiceId = invoiceService.createInvoice(userAccountId, clientId, timesheetId, projectService.getProjectHourlyRate(timesheetId));
        assertTrue(invoiceId > 0);

        // create timesheet summary attachment
        String timesheetSummaryMustacheTemplate = Utility.loadMustacheTemplate(ApplicationProvider.getApplicationContext(), InvoiceService.InvoiceAttachmentTypesEnum.TIMESHEET_SUMMARY);
        Long timesheetSummaryAttachmentId = invoiceService.createTimesheetSummaryAttachment(invoiceId, timesheetSummaryMustacheTemplate);
        assertNotNull(timesheetSummaryAttachmentId);
        InvoiceAttachment timesheetSummaryAttachment = invoiceService.getInvoiceAttachment(invoiceId, InvoiceService.InvoiceAttachmentTypesEnum.TIMESHEET_SUMMARY, InvoiceService.InvoiceAttachmentFileTypes.HTML);
        assertEquals("client-company-name_fakturerte_timer_2020-01", timesheetSummaryAttachment.getFileName());
        assertNotNull(timesheetSummaryAttachment.getFileContent());

        // create client timesheet pdf and timesheet summery pdf, used as attachment for the invoice
        String clientTimesheetMustacheTemplate = Utility.loadMustacheTemplate(ApplicationProvider.getApplicationContext(), InvoiceService.InvoiceAttachmentTypesEnum.CLIENT_TIMESHEET);
        Long invoiceAttachmentId = invoiceService.createClientTimesheetAttachment(invoiceId, timesheetId, clientTimesheetMustacheTemplate);
        assertTrue(invoiceAttachmentId > 0);
        InvoiceAttachment clientTimesheetAttachment = invoiceService.getInvoiceAttachment(invoiceId, InvoiceService.InvoiceAttachmentTypesEnum.CLIENT_TIMESHEET, InvoiceService.InvoiceAttachmentFileTypes.HTML);
        assertEquals("client-company-name_timeliste_2020-01", clientTimesheetAttachment.getFileName());
        assertNotNull(clientTimesheetAttachment.getFileContent());

        // check timesheet summary
        List<TimesheetSummary> timesheetSummaryList = timesheetService.getTimesheetSummary(timesheetId);
        assertEquals(2, timesheetSummaryList.size());
        assertEquals("WEEK", timesheetSummaryList.get(0).getSummedByPeriod());
        assertEquals("2019-12-30", timesheetSummaryList.get(0).getFromDate().toString());
        assertEquals("2020-01-05", timesheetSummaryList.get(0).getToDate().toString());
        assertEquals("17250.0", timesheetSummaryList.get(0).getTotalBilledAmount().toString());
        assertEquals("0", timesheetSummaryList.get(0).getTotalVacationDays().toString());
        assertEquals("0", timesheetSummaryList.get(0).getTotalSickLeaveDays().toString());
        assertEquals("2", timesheetSummaryList.get(0).getTotalWorkedDays().toString());
        assertEquals("15.0", timesheetSummaryList.get(0).getTotalWorkedHours().toString());
        assertEquals("2020", timesheetSummaryList.get(0).getYear().toString());
        assertEquals("Dec", timesheetSummaryList.get(0).getMonthInYear());
        assertEquals("1", timesheetSummaryList.get(0).getWeekInYear().toString());
        assertEquals("30.12", timesheetSummaryList.get(0).getFromDateDDMM());
        assertEquals("05.01", timesheetSummaryList.get(0).getToDateDDMM());

        // check invoice status
        Invoice invoice = invoiceService.getInvoice(invoiceId);
        assertEquals(invoiceId, invoice.getId());
        assertEquals(timesheetId, invoice.getTimesheetId());
        assertEquals(userAccountId, invoice.getIssuerId());
        assertEquals(clientId, invoice.getRecipientId());
        assertEquals(25, invoice.getVatPercent());
        assertEquals("NOK", invoice.getCurrency());
        assertEquals(17250.0, invoice.getAmount(), 0.0);
        assertEquals(4312.5, invoice.getVatAmount(), 0.0);
        assertEquals(InvoiceRepository.InvoiceStatusEnum.COMPLETED.name(), invoice.getStatus());

        // FIXME must fix foreign key config in order to prevent project delete when the project is referenced by timesheet entries
        // try to delete project, should not be possible when timesheet entries are assigned
        //ProjectDto closedProjetcDto = projectService.getProject(devProjectId);
        //closedProjetcDto.setStatus("CLOSED");
        //projectService.saveProject(closedProjetcDto);
        try {
            projectService.deleteProject(devProjectId);
            fail();
        } catch (InputValidationException e) {
            assertEquals("Project is active, not allowed to be delete.", e.getMessage());
        }

        // try to delete timesheet after billing, not allowed
        try {
            timesheetService.deleteTimesheet(timesheetId);
            fail();
        } catch (Exception e) {
            assertEquals("Timesheet is BILLED, not allowed to delete or update", e.getMessage());
        }
    }
}
