package com.gunnarro.android.terex.service;


import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.gunnarro.android.terex.domain.entity.TimesheetEntry;
import com.gunnarro.android.terex.domain.entity.TimesheetSummary;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@Disabled
@ExtendWith(MockitoExtension.class)
public class InvoiceServiceTest {

    @Mock
    android.content.Context applicationContextMock;

    @Test
    void invoiceTemplateEnum() {
        assertEquals("html/template/norway-consulting-timesheet.mustache", InvoiceService.InvoiceAttachmentTypesEnum.CLIENT_TIMESHEET.getTemplate());
        assertEquals("html/template/invoice-timesheet-attachment.mustache", InvoiceService.InvoiceAttachmentTypesEnum.TIMESHEET_SUMMARY.getTemplate());
        assertEquals("norway-consulting-timesheet.pdf", InvoiceService.InvoiceAttachmentTypesEnum.CLIENT_TIMESHEET.getPdfFileName());
        assertEquals("invoice-timesheet-attachment.pdf", InvoiceService.InvoiceAttachmentTypesEnum.TIMESHEET_SUMMARY.getPdfFileName());
    }

    @Test
    void generateTimesheet() {
        TimesheetService timesheetService = new TimesheetService(applicationContextMock);
        List<TimesheetEntry> timesheets = timesheetService.generateTimesheet(2023, 2);
        assertEquals(19, timesheets.size());
        assertEquals(30, timesheets.get(0).getBreakInMin());
        assertEquals(1075, timesheets.get(0).getHourlyRate());
        assertEquals("Open", timesheets.get(0).getStatus());
        assertEquals(450, timesheets.get(0).getWorkedMinutes());
        assertEquals("2023-02-01", timesheets.get(0).getWorkdayDate().toString());
        assertEquals("08:00", timesheets.get(0).getFromTime().toString());
        assertEquals("15:30", timesheets.get(0).getToTime().toString());
        assertEquals(null, timesheets.get(0).getComment());

    }

    @Test
    void buildInvoiceSummary() {
        TimesheetService timesheetService = new TimesheetService(applicationContextMock);
        List<TimesheetSummary> timesheetSummaries = timesheetService.buildTimesheetSummaryByWeek(2023, 2);
        assertEquals(5, timesheetSummaries.size());
        assertEquals(0, timesheetSummaries.get(0).getTimesheetId());
        assertEquals(24187.5, timesheetSummaries.get(0).getTotalBilledAmount());
        assertEquals(3, timesheetSummaries.get(0).getTotalWorkedDays());
        assertEquals(1350, timesheetSummaries.get(0).getTotalWorkedHours());
    }


    @Test
    public void jsonToTimesheet() {
        TimesheetService timesheetService = new TimesheetService(applicationContextMock);
        List<TimesheetEntry> timesheets = timesheetService.generateTimesheet(2022, 3);
        String jsonStr = null;
        /*try {
            jsonStr = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(timesheets);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }*/
        assertNotNull(jsonStr);
    }
}
