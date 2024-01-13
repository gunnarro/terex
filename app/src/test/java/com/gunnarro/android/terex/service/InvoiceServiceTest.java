package com.gunnarro.android.terex.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.gunnarro.android.terex.TestData;
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
    private InvoiceRepository invoiceRepositoryMock;

    @BeforeEach
    public void setup() {
        invoiceService = new InvoiceService(invoiceRepositoryMock, timesheetServiceMock);
    }

    @Test
    void invoiceTemplateEnum() {
        assertEquals("template/html/norway-consulting-timesheet.mustache", InvoiceService.InvoiceAttachmentTypesEnum.CLIENT_TIMESHEET.getTemplate());
        assertEquals("template/html/invoice-timesheet-attachment.mustache", InvoiceService.InvoiceAttachmentTypesEnum.TIMESHEET_SUMMARY.getTemplate());
        assertEquals("norway-consulting-timesheet", InvoiceService.InvoiceAttachmentTypesEnum.CLIENT_TIMESHEET.getFileName());
        assertEquals("invoice-timesheet-attachment", InvoiceService.InvoiceAttachmentTypesEnum.TIMESHEET_SUMMARY.getFileName());
    }

    @Test
    void createInvoice() {
        TimesheetSummary timesheetSummaryWeek1 = new TimesheetSummary();
        timesheetSummaryWeek1.setTimesheetId(1L);
        timesheetSummaryWeek1.setCurrency("NOK");
        timesheetSummaryWeek1.setYear(2023);
        timesheetSummaryWeek1.setWeekInYear(40);
        timesheetSummaryWeek1.setTotalDaysOff(0);
        timesheetSummaryWeek1.setTotalWorkedDays(5);
        timesheetSummaryWeek1.setTotalWorkedHours(37.5);
        timesheetSummaryWeek1.setTotalBilledAmount(25000d);

        List<TimesheetSummary> timesheetSummaries = List.of(timesheetSummaryWeek1);
        Long timesheetId = 1L;
        when(timesheetServiceMock.createTimesheetSummaryForBilling(anyLong())).thenReturn(TimesheetMapper.toTimesheetSummaryDtoList(timesheetSummaries));
        when(invoiceRepositoryMock.saveInvoice(any())).thenReturn(23L);
        Long invoiceId = invoiceService.createInvoice(timesheetId );
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
        assertEquals(1075, timesheetEntries.get(0).getHourlyRate());
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
        List<TimesheetSummary> timesheetSummaries = TestData.buildTimesheetSummaryByWeek(23L, 2023, 2);
        assertEquals(5, timesheetSummaries.size());
        assertEquals(0, timesheetSummaries.get(0).getTimesheetId());
        assertEquals(24187.5, timesheetSummaries.get(0).getTotalBilledAmount());
        assertEquals(3, timesheetSummaries.get(0).getTotalWorkedDays());
        assertEquals(1350, timesheetSummaries.get(0).getTotalWorkedHours());
    }
}
