package com.gunnarro.android.terex;

import com.gunnarro.android.terex.domain.entity.TimesheetEntry;
import com.gunnarro.android.terex.domain.entity.TimesheetSummary;

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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestData {

    public static List<TimesheetSummary> buildTimesheetSummaryByWeek(Integer year, Integer month) {
        Map<Integer, List<TimesheetEntry>> weekMap = new HashMap<>();
        generateTimesheetEntries(year, month).forEach(t -> {
            int week = getWeek(t.getWorkdayDate());
            if (!weekMap.containsKey(week)) {
                weekMap.put(week, new ArrayList<>());
            }
            Objects.requireNonNull(weekMap.get(week)).add(t);
        });
        List<TimesheetSummary> timesheetSummaryByWeek = new ArrayList<>();

        weekMap.forEach((k, e) -> {
            TimesheetSummary timesheetSummary = new TimesheetSummary();
            timesheetSummary.setCreatedDate(LocalDateTime.now());
            timesheetSummary.setLastModifiedDate(LocalDateTime.now());
            timesheetSummary.setWeekInYear(k);
            timesheetSummary.setCurrency("NOK");
            timesheetSummary.setTotalWorkedDays(e.size());
            timesheetSummaryByWeek.add(timesheetSummary);
            Objects.requireNonNull(weekMap.get(k)).forEach(t -> {
                timesheetSummary.setTotalBilledAmount(timesheetSummary.getTotalBilledAmount() + (t.getHourlyRate() * ((double) t.getWorkedMinutes() / 60)));
                timesheetSummary.setTotalWorkedHours(timesheetSummary.getTotalWorkedHours() + (double) t.getWorkedMinutes() / 60);
            });
        });
        return timesheetSummaryByWeek;
    }

    /**
     * @param year  current year
     * @param month from january to december, for example Month.MARCH
     */
    public static List<TimesheetEntry> generateTimesheetEntries(Integer year, Integer month) {
        return getWorkingDays(year, month).stream().map(TestData::createTimesheet).collect(Collectors.toList());
    }

    private static TimesheetEntry createTimesheet(LocalDate day) {
        return TimesheetEntry.createDefault(new java.util.Random().nextLong(), day);
    }

    private static List<LocalDate> getWorkingDays(Integer year, Integer month) {
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

    private static int getWeek(LocalDate date) {
        TemporalField woy = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
        return date.get(woy);
    }
}
