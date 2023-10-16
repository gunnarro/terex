package com.gunnarro.android.terex.service;

import android.content.Context;
import android.util.Log;

import androidx.room.Transaction;

import com.gunnarro.android.terex.domain.dto.TimesheetEntryDto;
import com.gunnarro.android.terex.domain.entity.Company;
import com.gunnarro.android.terex.domain.entity.Invoice;
import com.gunnarro.android.terex.domain.entity.InvoiceSummary;
import com.gunnarro.android.terex.domain.entity.Timesheet;
import com.gunnarro.android.terex.domain.entity.TimesheetEntry;
import com.gunnarro.android.terex.exception.TerexApplicationException;
import com.gunnarro.android.terex.repository.InvoiceRepository;
import com.gunnarro.android.terex.utility.Utility;

import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

import kotlin.random.Random;

@Singleton
public class InvoiceService {

    private final TimesheetService timesheetService;
    private final InvoiceRepository invoiceRepository;


    /**
     * default constructor
     */
    @Inject
    public InvoiceService(Context applicationContext) {
        timesheetService = new TimesheetService(applicationContext);
        invoiceRepository = new InvoiceRepository(applicationContext);
    }

    public Invoice getInvoice(Long invoiceId) {
        return invoiceRepository.getInvoice(invoiceId);
    }

    @Transaction
    public Long createInvoice(Company company, Company client, Long timesheetId) {
        // create the invoice
        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(Random.Default.nextInt(100, 10000));
        invoice.setClientId(client.getId());
        // ensure that a timesheet is only billed once.
        invoice.setReference(String.format("%s-%s", client.getName(), timesheetId));
        invoice.setStatus(InvoiceRepository.InvoiceStatusEnum.OPEN.name());
        // fixme, should be unique for a timesheet
        invoice.setBillingDate(LocalDate.now());
        // defaulted to 10 days after billing date
        invoice.setDueDate(invoice.getBillingDate().plusDays(10));
        Long invoiceId = invoiceRepository.saveInvoice(invoice);
        Log.d("createInvoice", "created invoice=" + invoiceId);
        // then accumulate timesheet entries
        List<InvoiceSummary> invoiceSummaries = createInvoiceSummary(invoiceId, timesheetId);
        // save the invoice summaries
        invoiceSummaries.forEach(i -> {
            invoiceRepository.insertInvoiceSummary(i);
            Log.d("inserted invoice summary", "" + i);
        });

        double sumAmount = invoiceSummaries.stream().mapToDouble(InvoiceSummary::getSumBilledWork).sum();
        invoice.setAmount(sumAmount);
        invoice.setStatus(InvoiceRepository.InvoiceStatusEnum.CREATED.name());
        invoiceRepository.saveInvoice(invoice);
        return invoiceId;
    }

    private List<InvoiceSummary> createInvoiceSummary(Long invoiceId, Long timesheetId) {
        // check timesheet status
        Timesheet timesheet = timesheetService.getTimesheet(timesheetId);
        if (timesheet.getStatus().equals(Timesheet.TimesheetStatusEnum.BILLED.name())) {
            throw new TerexApplicationException("Application error, timesheet is closed", "50023", null);
        }
        Log.d("createInvoiceSummary", String.format("invoiceId=%s, timesheetId=%s", invoiceId, timesheetId));
        List<TimesheetEntry> timesheetEntryList = timesheetService.getTimesheetEntryList(timesheetId);
        if (timesheetEntryList == null || timesheetEntryList.isEmpty()) {
            return null;
        }
        Log.d("createInvoiceSummary", "timesheet entries: " + timesheetEntryList);
        // accumulate timesheet by week for the mount
        Map<Integer, List<TimesheetEntry>> timesheetWeekMap = timesheetEntryList.stream().collect(Collectors.groupingBy(TimesheetEntry::getWorkdayWeek));
        List<InvoiceSummary> invoiceSummaryByWeek = new ArrayList<>();
        timesheetWeekMap.forEach((k, e) -> {
            invoiceSummaryByWeek.add(buildInvoiceSummaryForWeek(invoiceId, k, e));
        });
        Log.d("createInvoiceSummary", "timesheet summary by week: " + invoiceSummaryByWeek);
        // close the timesheet after invoice have been generated, is not possible to do any form of changes on the time list.
        timesheet.setStatus(Timesheet.TimesheetStatusEnum.BILLED.name());
        timesheetService.saveTimesheet(timesheet);

        invoiceSummaryByWeek.sort(Comparator.comparing(InvoiceSummary::getWeekInYear));
        return invoiceSummaryByWeek;
    }

    public List<Timesheet> getTimesheets(String status) {
        return timesheetService.getTimesheets(status);
    }

    public List<TimesheetEntry> getTimesheetEntryList(Long timesheetId) {
        return timesheetService.getTimesheetEntryList(timesheetId);
    }

    private TimesheetEntryDto mapToTimesheetEntryDto(TimesheetEntry timesheetEntry) {
        TimesheetEntryDto timesheetEntryDto = new TimesheetEntryDto();
        timesheetEntryDto.setComments(timesheetEntry.getComment());
        timesheetEntryDto.setFromTime(timesheetEntry.getFromTime().toString());
        timesheetEntryDto.setToTime(timesheetEntry.getToTime().toString());
        timesheetEntryDto.setWorkdayDate(timesheetEntry.getWorkdayDate());
        return timesheetEntryDto;
    }

    /**
     * @param year  current year
     * @param month from january to december, for example Month.MARCH
     * @return
     */
    public List<TimesheetEntry> generateTimesheet(Integer year, Integer month) {
        return getWorkingDays(year, month).stream().map(this::createTimesheet).collect(Collectors.toList());
    }

    private InvoiceSummary buildInvoiceSummaryForWeek(Long invoiceId, Integer week, List<TimesheetEntry> timesheets) {
        InvoiceSummary invoiceSummary = new InvoiceSummary();
        invoiceSummary.setCreatedDate(LocalDateTime.now());
        invoiceSummary.setLastModifiedDate(LocalDateTime.now());
        invoiceSummary.setInvoiceId(invoiceId);
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
        return TimesheetEntry.createDefault(new java.util.Random().nextLong(), Timesheet.TimesheetStatusEnum.OPEN.name(), Utility.DEFAULT_DAILY_BREAK_IN_MINUTES, day, Utility.DEFAULT_DAILY_WORKING_HOURS_IN_MINUTES, Utility.DEFAULT_HOURLY_RATE);
    }

    private static int getWeek(LocalDate date) {
        TemporalField woy = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
        return date.get(woy);
    }
}
