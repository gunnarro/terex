package com.gunnarro.android.terex.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.gunnarro.android.terex.TestData;
import com.gunnarro.android.terex.domain.dto.ClientDto;
import com.gunnarro.android.terex.domain.dto.TimesheetDto;
import com.gunnarro.android.terex.domain.entity.TimesheetEntry;
import com.gunnarro.android.terex.domain.entity.TimesheetSummary;
import com.gunnarro.android.terex.domain.mapper.TimesheetMapper;
import com.gunnarro.android.terex.repository.InvoiceRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceTest {

    private InvoiceService invoiceService;

    @Mock
    private TimesheetService timesheetServiceMock;

    @Mock
    private ClientService clientServiceMock;

    @Mock
    private UserAccountService userAccountServiceMock;

    @Mock
    private InvoiceRepository invoiceRepositoryMock;

    @BeforeEach
    public void setup() {
        invoiceService = new InvoiceService(invoiceRepositoryMock, timesheetServiceMock, clientServiceMock, userAccountServiceMock);
    }

    @Test
    void invoiceTemplateEnum() {
        assertEquals("template/html/default-client-timesheet.mustache", InvoiceService.InvoiceAttachmentTypesEnum.CLIENT_TIMESHEET.getTemplate());
        assertEquals("template/html/invoice-timesheet-summary-attachment.mustache", InvoiceService.InvoiceAttachmentTypesEnum.TIMESHEET_SUMMARY.getTemplate());
        assertEquals("default-client-timesheet", InvoiceService.InvoiceAttachmentTypesEnum.CLIENT_TIMESHEET.getFileName());
        assertEquals("invoice-timesheet-summary-attachment", InvoiceService.InvoiceAttachmentTypesEnum.TIMESHEET_SUMMARY.getFileName());
    }

    @Test
    void createInvoice() {
        long timesheetId = 22L;
        TimesheetSummary timesheetSummaryWeek1 = TestData.createTimesheetSummary(timesheetId);

        ClientDto clientDto = new ClientDto(null);
        clientDto.setId(234L);

        TimesheetDto timesheetDto = new TimesheetDto(timesheetId);
        timesheetDto.setFromDate(LocalDate.of(2024, 6, 1));
        timesheetDto.setToDate(LocalDate.of(2024, 6, 30));

        List<TimesheetSummary> timesheetSummaries = List.of(timesheetSummaryWeek1);
        when(timesheetServiceMock.getTimesheetDto(anyLong())).thenReturn(timesheetDto);
        when(timesheetServiceMock.createTimesheetSummaryForBilling(anyLong())).thenReturn(TimesheetMapper.toTimesheetSummaryDtoList(timesheetSummaries));
        when(invoiceRepositoryMock.saveInvoice(any())).thenReturn(23L);
        Long invoiceId = invoiceService.createInvoice(timesheetId, 100L, 200L, 1250);
        assertEquals(23, invoiceId);
    }

    /**
     * Just for generate test data
     */
    @Disabled
    @Test
    void generateTimesheet() {
        TimesheetService timesheetService = new TimesheetService();
        List<TimesheetEntry> timesheetEntries = TestData.generateTimesheetEntries(2023, 2, 200L, List.of(), List.of());
        assertEquals(19, timesheetEntries.size());
        assertEquals("Open", timesheetEntries.get(0).getStatus());
        assertEquals(450, timesheetEntries.get(0).getWorkedSeconds());
        assertEquals("2023-02-01", timesheetEntries.get(0).getWorkdayDate().toString());
        assertEquals("08:00", timesheetEntries.get(0).getStartTime().toString());
        assertEquals("15:30", timesheetEntries.get(0).getEndTime().toString());
        assertNull(timesheetEntries.get(0).getComments());
    }

    /**
     * Just for generate test data
     */
    @Disabled
    @Test
    void buildInvoiceSummary() {
        TimesheetService timesheetService = new TimesheetService();
        List<TimesheetSummary> timesheetSummaries = TestData.buildTimesheetSummaryByWeek(23L, 200L,2023, 2, 1250);
        assertEquals(5, timesheetSummaries.size());
        assertEquals(0, timesheetSummaries.get(0).getTimesheetId());
        assertEquals(24187.5, timesheetSummaries.get(0).getTotalBilledAmount());
        assertEquals(3, timesheetSummaries.get(0).getTotalWorkedDays());
        assertEquals(1350, timesheetSummaries.get(0).getTotalWorkedHours());
    }
}
