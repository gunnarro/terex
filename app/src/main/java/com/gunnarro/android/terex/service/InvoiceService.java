package com.gunnarro.android.terex.service;

import android.content.Context;
import android.util.Log;

import com.gunnarro.android.terex.domain.entity.InvoiceSummary;
import com.gunnarro.android.terex.domain.entity.RegisterWork;
import com.gunnarro.android.terex.domain.entity.Timesheet;
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

    private TimesheetRepository timesheetRepository;


    /**
     * default constructor
     */

    @Inject
    public InvoiceService(Context applicationContext) {
        timesheetRepository = new TimesheetRepository(applicationContext);
    }

    /**
     * @param monthNumber note! is from 1 to 12
     * @return
     */
    public List<InvoiceSummary> createInvoiceSummaryForMonth(String clientName, String projectCode, Integer monthNumber) {
        Log.d("createInvoiceSummaryForMonth", "timesheets for mount: " + clientName + ", " + projectCode + ", " + monthNumber);
        List<Timesheet> timesheets = timesheetRepository.getTimesheets(clientName, projectCode, monthNumber);
        Log.d("createInvoiceSummaryForMonth", "timesheets for mount: " + timesheets);
        // accumulate timesheet by week for the mount
       Map<Integer, List<Timesheet>> timesheetWeekMap = timesheets.stream().collect(Collectors.groupingBy(Timesheet::getWorkdayWeek));

        List<InvoiceSummary> invoiceSummaryByWeek = new ArrayList<>();

        timesheetWeekMap.forEach((k, e) -> {
            invoiceSummaryByWeek.add(buildInvoiceSummaryForWeek(k, e));

        });
        return invoiceSummaryByWeek;
    }

    public List<Timesheet> getTimesheetForMonth(String clientName, String projectCode, Integer monthNumber) {
        return timesheetRepository.getTimesheets(clientName, projectCode, monthNumber);
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
    public List<Timesheet> generateTimesheet(Integer year, Integer month) {
        return getWorkingDays(year, month).stream().map(this::createTimesheet).collect(Collectors.toList());
    }

    private InvoiceSummary buildInvoiceSummaryForWeek(Integer week, List<Timesheet> timesheets) {
        InvoiceSummary invoiceSummary = new InvoiceSummary();
        invoiceSummary.setWeekInYear(week);
        invoiceSummary.setYear(timesheets.get(0).getWorkdayDate().getYear());
        invoiceSummary.setFromDate(Utility.getFirstDayOfWeek(timesheets.get(0).getWorkdayDate(), week));
        invoiceSummary.setToDate(Utility.getLastDayOfWeek(timesheets.get(0).getWorkdayDate(), week));
        invoiceSummary.setSumWorkedDays(timesheets.size());
        timesheets.forEach(t -> {
            invoiceSummary.setSumBilledWork(invoiceSummary.getSumBilledWork() + (t.getHourlyRate() * ((double) t.getWorkedMinutes() / 60)));
            invoiceSummary.setSumWorkedMinutes(invoiceSummary.getSumWorkedMinutes() + t.getWorkedMinutes());
        });
        return invoiceSummary;
    }

    public List<InvoiceSummary> buildInvoiceSummaryByWeek(Integer year, Integer month) {
        Map<Integer, List<Timesheet>> weekMap = new HashMap<>();
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
                invoiceSummary.setSumWorkedMinutes(invoiceSummary.getSumWorkedMinutes() + t.getWorkedMinutes());
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

        Predicate<LocalDate> isWeekend = date -> date.getDayOfWeek() == DayOfWeek.SATURDAY
                || date.getDayOfWeek() == DayOfWeek.SUNDAY;

        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        return Stream.iterate(startDate, date -> date.plusDays(1))
                .limit(daysBetween)
                .filter(isWeekend.negate())
                .collect(Collectors.toList());
    }

    private Timesheet createTimesheet(LocalDate day) {
        return Timesheet.createDefault(null, null, Utility.DEFAULT_STATUS, Utility.DEFAULT_DAILY_BREAK_IN_MINUTES, day, Utility.DEFAULT_DAILY_WORKING_HOURS_IN_MINUTES, Utility.DEFAULT_HOURLY_RATE);
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
