package com.gunnarro.android.terex.service;

import android.content.Context;
import android.util.Log;

import com.gunnarro.android.terex.domain.dto.TimesheetDto;
import com.gunnarro.android.terex.domain.dto.TimesheetEntryDto;
import com.gunnarro.android.terex.domain.entity.Company;
import com.gunnarro.android.terex.domain.entity.Invoice;
import com.gunnarro.android.terex.domain.entity.InvoiceSummary;
import com.gunnarro.android.terex.domain.entity.RegisterWork;
import com.gunnarro.android.terex.domain.entity.Timesheet;
import com.gunnarro.android.terex.domain.entity.TimesheetEntry;
import com.gunnarro.android.terex.repository.InvoiceRepository;
import com.gunnarro.android.terex.repository.TimesheetRepository;
import com.gunnarro.android.terex.utility.Utility;

import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.ValueRange;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class InvoiceService {

    private final TimesheetRepository timesheetRepository;
    private InvoiceRepository invoiceRepository;


    /**
     * default constructor
     */

    @Inject
    public InvoiceService(Context applicationContext) {
        timesheetRepository = new TimesheetRepository(applicationContext);
        invoiceRepository = new InvoiceRepository(applicationContext);
    }

    public Invoice getInvoice(Long invoiceId) {
        return invoiceRepository.getInvoice(invoiceId);
    }

    public Long createInvoice(Company company, Company client, Long timesheetId) {
        // create the invoice
        Invoice invoice = new Invoice();
        invoice.setTimesheetId(timesheetId);
        invoice.setStatus("processed");
        invoice.setDueDate(LocalDate.now().plusDays(10));
        invoice.setBillingDate(LocalDate.now());
        Long invoiceId = invoiceRepository.insertInvoice(invoice);
        Log.d("createInvoice", "created invoice=" + invoiceId);
        // then accumulate timesheet entries
        List<InvoiceSummary> invoiceSummaries = createInvoiceSummary(invoiceId, timesheetId);
        // save the invoice summaries
        invoiceSummaries.forEach(i -> {
            invoiceRepository.insertInvoiceSummary(i);
            Log.d("inserted invoice summary", "" + i);
        });
        return invoiceId;
    }

    /**
     * @return
     */
    private List<InvoiceSummary> createInvoiceSummary(Long invoiceId, Long timesheetId) {
        Log.d("createInvoiceSummary", String.format("invoiceId=%s, timesheetId=%s", invoiceId, timesheetId));
        List<TimesheetEntry> timesheetEntryList = timesheetRepository.getTimesheetEntryList(timesheetId);
        if (timesheetEntryList == null || timesheetEntryList.isEmpty()) {
            return null;
        }
        Log.d("createInvoiceSummary", "timesheet entries: " + timesheetEntryList);
        // accumulate timesheet by week for the mount
        Map<Integer, List<TimesheetEntry>> timesheetWeekMap = timesheetEntryList.stream().collect(Collectors.groupingBy(TimesheetEntry::getWorkdayWeek));
        List<InvoiceSummary> invoiceSummaryByWeek = new ArrayList<>();
        timesheetWeekMap.forEach((k, e) -> {
            invoiceSummaryByWeek.add(buildInvoiceSummaryForWeek(invoiceId, timesheetId, k, e));
        });

        invoiceSummaryByWeek.sort(Comparator.comparing(InvoiceSummary::getWeekInYear));
        return invoiceSummaryByWeek;
    }

    public void createInvoice(Invoice invoice) {
        invoiceRepository.insertInvoice(invoice);
    }

    public List<Timesheet> getTimesheets(String status) {
        return timesheetRepository.getTimesheets(status);
    }

    public List<TimesheetEntry> getTimesheetEntryList(Long timesheetId) {
        return timesheetRepository.getTimesheetEntryList(timesheetId);
    }

    public TimesheetDto getTimesheetDto(Long timesheetId) {
        TimesheetDto timesheetDto = new TimesheetDto();
        List<TimesheetEntry> timesheetEntryList = timesheetRepository.getTimesheetEntryList(timesheetId);
        timesheetDto.setTimesheetEntryDtoList(timesheetEntryList.stream().map(this::mapToTimesheetEntryDto).collect(Collectors.toList()));
        return timesheetDto;
    }

    private TimesheetEntryDto mapToTimesheetEntryDto(TimesheetEntry timesheetEntry) {
        TimesheetEntryDto timesheetEntryDto = new TimesheetEntryDto();
        timesheetEntryDto.setComments(timesheetEntry.getComment());
        timesheetEntryDto.setFromTime(timesheetEntry.getFromTime().toString());
        timesheetEntryDto.setToTime(timesheetEntry.getToTime().toString());
        timesheetEntryDto.setWorkdayDate(timesheetEntry.getWorkdayDate());
        return timesheetEntryDto;
    }

    public RegisterWork getRegisterWork(Integer id) {
        RegisterWork registerWork = null;
        if (id == null) {
            registerWork = new RegisterWork();
        }
        return registerWork;
    }

    /**
     * @param year  current year
     * @param month from january to december, for example Month.MARCH
     * @return
     */
    public List<TimesheetEntry> generateTimesheet(Integer year, Integer month) {
        return getWorkingDays(year, month).stream().map(this::createTimesheet).collect(Collectors.toList());
    }

    private InvoiceSummary buildInvoiceSummaryForWeek(Long invoiceId, Long timesheetId, Integer week, List<TimesheetEntry> timesheets) {
        InvoiceSummary invoiceSummary = new InvoiceSummary();
        invoiceSummary.setInvoiceId(invoiceId);
        invoiceSummary.setTimesheetId(timesheetId);
        invoiceSummary.setWeekInYear(week);
        invoiceSummary.setYear(timesheets.get(0).getWorkdayDate().getYear());
        invoiceSummary.setFromDate(Utility.getFirstDayOfWeek(timesheets.get(0).getWorkdayDate(), week));
        invoiceSummary.setToDate(Utility.getLastDayOfWeek(timesheets.get(0).getWorkdayDate(), week));
        invoiceSummary.setSumWorkedDays(timesheets.size());
        timesheets.forEach(t -> {
            invoiceSummary.setSumBilledWork(invoiceSummary.getSumBilledWork() + (t.getHourlyRate() * ((double) t.getWorkedMinutes() / 60)));
            invoiceSummary.setSumWorkedHours(invoiceSummary.getSumWorkedHours() + (double) t.getWorkedMinutes() / 60);
        });
        return invoiceSummary;
    }

    public List<InvoiceSummary> buildInvoiceSummaryByWeek(Integer year, Integer month) {
        Map<Integer, List<TimesheetEntry>> weekMap = new HashMap<>();
        generateTimesheet(year, month).forEach(t -> {
            int week = getWeek(t.getWorkdayDate());
            if (!weekMap.containsKey(week)) {
                weekMap.put(week, new ArrayList<>());
            }
            Objects.requireNonNull(weekMap.get(week)).add(t);
        });
        List<InvoiceSummary> invoiceSummaryByWeek = new ArrayList<>();

        weekMap.forEach((k, e) -> {
            InvoiceSummary invoiceSummary = new InvoiceSummary();
            invoiceSummary.setWeekInYear(k);
            //invoiceSummary.setFromDate(e.get(0).getFromDate().toLocalDate());
            //invoiceSummary.setToDate(e.get(e.size() -1).getToDate().toLocalDate());
            invoiceSummary.setSumWorkedDays(e.size());
            invoiceSummaryByWeek.add(invoiceSummary);
            Objects.requireNonNull(weekMap.get(k)).forEach(t -> {
                invoiceSummary.setSumBilledWork(invoiceSummary.getSumBilledWork() + (t.getHourlyRate() * ((double) t.getWorkedMinutes() / 60)));
                invoiceSummary.setSumWorkedHours(invoiceSummary.getSumWorkedHours() + (double) t.getWorkedMinutes() / 60);
            });
        });
        return invoiceSummaryByWeek;
    }

    private List<LocalDate> getWorkingDays(Integer year, Integer month) {
        // validate year and mount
        try {
            Year.of(year);
        } catch (DateTimeException e) {
            throw new RuntimeException(e.getMessage());
        }
        try {
            Month.of(month);
        } catch (DateTimeException e) {
            throw new RuntimeException(e.getMessage());
        }

        LocalDate startDate = LocalDate.of(Year.of(year).getValue(), Month.of(month).getValue(), 1);
        ValueRange range = startDate.range(ChronoField.DAY_OF_MONTH);
        LocalDate endDate = startDate.withDayOfMonth((int) range.getMaximum());

        Predicate<LocalDate> isWeekend = date -> date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY;

        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        return Stream.iterate(startDate, date -> date.plusDays(1)).limit(daysBetween).filter(isWeekend.negate()).collect(Collectors.toList());
    }

    private TimesheetEntry createTimesheet(LocalDate day) {
        return TimesheetEntry.createDefault(timesheetRepository.getCurrentTimesheetId("*", "*"), Utility.DEFAULT_STATUS, Utility.DEFAULT_DAILY_BREAK_IN_MINUTES, day, Utility.DEFAULT_DAILY_WORKING_HOURS_IN_MINUTES, Utility.DEFAULT_HOURLY_RATE);
    }

    private static int getWeek(LocalDate date) {
        TemporalField woy = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
        return date.get(woy);
    }

    private static boolean isWeekend(final LocalDate date) {
        DayOfWeek day = DayOfWeek.of(date.get(ChronoField.DAY_OF_WEEK));
        return day == DayOfWeek.SUNDAY || day == DayOfWeek.SATURDAY;
    }
}
