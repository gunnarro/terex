package com.gunnarro.android.terex.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import androidx.annotation.NonNull;

import com.gunnarro.android.terex.TestData;
import com.gunnarro.android.terex.domain.dto.ClientDto;
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
        TimesheetSummary timesheetSummaryWeek1 = TestData.createTimesheetSummary(22L);

        ClientDto clientDto = new ClientDto();
        clientDto.setId(234L);

        List<TimesheetSummary> timesheetSummaries = List.of(timesheetSummaryWeek1);
        Long timesheetId = 1L;
        when(timesheetServiceMock.createTimesheetSummaryForBilling(anyLong(), anyInt())).thenReturn(TimesheetMapper.toTimesheetSummaryDtoList(timesheetSummaries));
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
        List<TimesheetEntry> timesheetEntries = TestData.generateTimesheetEntries(2023, 2);
        assertEquals(19, timesheetEntries.size());
        assertEquals(30, timesheetEntries.get(0).getBreakInMin());
        assertEquals("Open", timesheetEntries.get(0).getStatus());
        assertEquals(450, timesheetEntries.get(0).getWorkedMinutes());
        assertEquals("2023-02-01", timesheetEntries.get(0).getWorkdayDate().toString());
        assertEquals("08:00", timesheetEntries.get(0).getFromTime().toString());
        assertEquals("15:30", timesheetEntries.get(0).getToTime().toString());
        assertEquals(null, timesheetEntries.get(0).getComment());
    }

    /**
     * Just for generate test data
     */
    @Disabled
    @Test
    void buildInvoiceSummary() {
        TimesheetService timesheetService = new TimesheetService();
        List<TimesheetSummary> timesheetSummaries = TestData.buildTimesheetSummaryByWeek(23L, 2023, 2, 1250);
        assertEquals(5, timesheetSummaries.size());
        assertEquals(0, timesheetSummaries.get(0).getTimesheetId());
        assertEquals(24187.5, timesheetSummaries.get(0).getTotalBilledAmount());
        assertEquals(3, timesheetSummaries.get(0).getTotalWorkedDays());
        assertEquals(1350, timesheetSummaries.get(0).getTotalWorkedHours());
    }
}
