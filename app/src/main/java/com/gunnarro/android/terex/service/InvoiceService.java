package com.gunnarro.android.terex.service;

import com.gunnarro.android.terex.domain.entity.RegisterWork;
import com.gunnarro.android.terex.domain.entity.Timesheet;
import com.gunnarro.android.terex.domain.entity.InvoiceSummary;

import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
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
    private static final Integer DEFAULT_DAILY_BREAK_IN_MINUTES = 30;
    private static final Long DEFAULT_DAILY_WORKING_HOURS_IN_MINUTES = (8 * 60L) - DEFAULT_DAILY_BREAK_IN_MINUTES;
    private static final Integer DEFAULT_HOURLY_RATE = 1075;
    private static final String DEFAULT_STATUS = "Open";
    private static final String TIMESHEET_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";

    /**
     * default constructor
     */
    @Inject
    public InvoiceService() {
    }

    public RegisterWork getRegisterWork(Integer id) {
        RegisterWork registerWork = null;
        if (id == null) {
            registerWork = new RegisterWork();
        }
        return registerWork;
    }

    /**
     *
     * @param year current year
     * @param month from january to december, for example Month.MARCH
     * @return
     */
    public List<Timesheet> generateTimesheet(Integer year, Integer month) {
        return getWorkingDays(year, month).stream().map(this::createTimesheet).collect(Collectors.toList());
    }

    public List<InvoiceSummary> buildInvoiceSummaryByWeek(Integer year, Integer month) {
        Map<Integer, List<Timesheet>> weekMap = new HashMap<>();
        generateTimesheet(year, month).forEach( t -> {
            int week = getWeek(t.getWorkdayDate());
            if (!weekMap.containsKey(week)) {
                weekMap.put(week, new ArrayList<>());
            }
            Objects.requireNonNull(weekMap.get(week)).add(t);
        });
        List<InvoiceSummary> invoiceSummaryByWeek = new ArrayList<>();

        weekMap.forEach( (k,e) -> {
            InvoiceSummary invoiceSummary = new InvoiceSummary();
            invoiceSummary.setWeekInYear(k);
            //invoiceSummary.setFromDate(e.get(0).getFromDate().toLocalDate());
            //invoiceSummary.setToDate(e.get(e.size() -1).getToDate().toLocalDate());
            invoiceSummary.setSumWorkedDays(e.size());
            invoiceSummaryByWeek.add(invoiceSummary);
            Objects.requireNonNull(weekMap.get(k)).forEach(t -> {
                invoiceSummary.setSumBilledWork(invoiceSummary.getSumBilledWork() + (t.getHourlyRate()*((double)t.getWorkedMinutes()/60)));
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
        Timesheet timesheet = Timesheet.createDefault(null, null, DEFAULT_STATUS, DEFAULT_DAILY_BREAK_IN_MINUTES, day, DEFAULT_DAILY_WORKING_HOURS_IN_MINUTES, DEFAULT_HOURLY_RATE);
        // start ar am 08:00
        timesheet.setWorkdayDate(day);
        return timesheet;
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
