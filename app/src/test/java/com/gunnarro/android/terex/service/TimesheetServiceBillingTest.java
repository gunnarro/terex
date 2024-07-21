package com.gunnarro.android.terex.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.content.res.AssetManager;

import com.gunnarro.android.terex.TestData;
import com.gunnarro.android.terex.domain.dto.ClientDto;
import com.gunnarro.android.terex.domain.dto.ProjectDto;
import com.gunnarro.android.terex.domain.dto.UserAccountDto;
import com.gunnarro.android.terex.domain.entity.Timesheet;
import com.gunnarro.android.terex.domain.entity.TimesheetEntry;
import com.gunnarro.android.terex.domain.entity.TimesheetSummary;
import com.gunnarro.android.terex.exception.TerexApplicationException;
import com.gunnarro.android.terex.repository.TimesheetRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class TimesheetServiceBillingTest {

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
    void createClientTimesheetHtml() throws IOException {
        LocalDate month = LocalDate.of(2023, 11, 1);
        Timesheet timesheet = new Timesheet();
        timesheet.setId(23L);
        timesheet.setUserId(1L);
        timesheet.setClientId(444L);
        timesheet.setFromDate(LocalDate.of(month.getYear(), month.getMonthValue(), 1));
        timesheet.setToDate(LocalDate.of(month.getYear(), month.getMonthValue(), 30));
        timesheet.setYear(month.getYear());
        timesheet.setMonth(month.getMonthValue());

        File mustacheTemplateFile = new File("src/main/assets/" + InvoiceService.InvoiceAttachmentTypesEnum.CLIENT_TIMESHEET.getTemplate());
        Context applicationContextMock = mock(Context.class);
        AssetManager assetManagerMock = mock(AssetManager.class);
        when(assetManagerMock.open(anyString())).thenReturn(new FileInputStream(mustacheTemplateFile));
        when(applicationContextMock.getAssets()).thenReturn(assetManagerMock);

        List<TimesheetEntry> timesheetEntryList = TestData.generateTimesheetEntries(timesheet.getFromDate().getYear(), timesheet.getFromDate().getMonthValue(), 200L, List.of(8, 16), List.of(14, 15));

        when(userAccountServiceMock.getUserAccount(anyLong())).thenReturn(TestData.createUserAccountDto(23L, "Petter Dass"));
        when(timesheetRepositoryMock.getTimesheet(anyLong())).thenReturn(timesheet);
        when(timesheetRepositoryMock.getTimesheetEntryList(anyLong())).thenReturn(timesheetEntryList);

        String clientTimesheetMustacheTemplate = loadMustacheTemplate(applicationContextMock, InvoiceService.InvoiceAttachmentTypesEnum.CLIENT_TIMESHEET);

        ClientDto clientDto = TestData.createClientDto(100L, "Software Development AS");
        clientDto.setOrganizationDto(TestData.createOrganizationDto(234L, "Customer Ogr Name", "123456789"));
        clientDto.setProjectList(List.of(TestData.createProjectDto(300L, clientDto.getId(), "apotek1")));
        UserAccountDto userAccountDto = TestData.createUserAccountDto(300L, "Gunnar Besseheim");

        String templateHtml = timesheetService.createTimesheetListHtml(23L, userAccountDto, clientDto, clientTimesheetMustacheTemplate);
        assertNotNull(templateHtml);
        saveToFile("src/test/" + InvoiceService.InvoiceAttachmentTypesEnum.CLIENT_TIMESHEET.getFileName() + ".html", templateHtml);
    }

    @Test
    void createTimesheetSummaryAttachmentHtml() throws IOException {
        Timesheet timesheet = Timesheet.createDefault(100L, 10L, 2023, 11);
        timesheet.setId(23L);
        timesheet.setClientId(444L);

        UserAccountDto invoiceIssuer = TestData.createUserAccountDto(1000L, "guro");
        invoiceIssuer.setOrganizationDto(TestData.createOrganizationDto(100L, "gunnarro as", "822 707 922"));
        ClientDto invoiceReceiver = TestData.createClientDto(300L, "client organization name");
        invoiceReceiver.setProjectList(List.of(TestData.createProjectDto(300L, invoiceReceiver.getId(), "apotek1")));
        invoiceReceiver.setOrganizationDto(TestData.createOrganizationDto(1001L, "client organization name", "988 232 999"));
        invoiceReceiver.setContactPersonDto(TestData.createContactPerson(600L, "kontaktperson hos klient"));
        ProjectDto projectDto = new ProjectDto();
        projectDto.setName("terex project");
        projectDto.setHourlyRate(1000);
        File mustacheTemplateFile = new File("src/main/assets/" + InvoiceService.InvoiceAttachmentTypesEnum.TIMESHEET_SUMMARY.getTemplate());

        Context applicationContextMock = mock(Context.class);
        AssetManager assetManagerMock = mock(AssetManager.class);
        when(assetManagerMock.open(anyString())).thenReturn(new FileInputStream(mustacheTemplateFile));
        when(applicationContextMock.getAssets()).thenReturn(assetManagerMock);

        List<TimesheetSummary> timesheetSummaryList = TestData.buildTimesheetSummaryByWeek(23L, 200L,2023, 1, 1000);
        when(timesheetRepositoryMock.getTimesheet(anyLong())).thenReturn(timesheet);
        when(timesheetRepositoryMock.getTimesheetSummary(anyLong())).thenReturn(timesheetSummaryList);

        String timesheetSummaryMustacheTemplate = loadMustacheTemplate(applicationContextMock, InvoiceService.InvoiceAttachmentTypesEnum.TIMESHEET_SUMMARY);

        String templateHtml = timesheetService.createTimesheetSummaryAttachmentHtml(timesheet.getId(), invoiceIssuer, invoiceReceiver, timesheetSummaryMustacheTemplate);
        assertNotNull(templateHtml);
        saveToFile("src/test/" + InvoiceService.InvoiceAttachmentTypesEnum.TIMESHEET_SUMMARY.getFileName() + ".html", templateHtml);
    }

    private void saveToFile(String filePath, String content) throws IOException {
        File myObj = new File(filePath);
        if (myObj.createNewFile()) {
            System.out.println("File created: " + myObj.getName());
        } else {
            System.out.println("File already exists.");
        }
        try (FileWriter fileWriter = new FileWriter(Path.of(filePath).toFile())) {
            fileWriter.write(content);
        }
    }

    private String loadMustacheTemplate(Context applicationContext, InvoiceService.InvoiceAttachmentTypesEnum template) {
        StringBuilder mustacheTemplateStr = new StringBuilder();
        try (InputStream fis = applicationContext.getAssets().open(template.getTemplate()); InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8); BufferedReader br = new BufferedReader(isr)) {
            br.lines().forEach(mustacheTemplateStr::append);
            return mustacheTemplateStr.toString();
        } catch (IOException e) {
            throw new TerexApplicationException("error reading mustache template", "50050", e);
        }
    }
}
