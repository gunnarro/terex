package com.gunnarro.android.terex.service;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.room.Transaction;

import com.gunnarro.android.terex.domain.dto.TimesheetEntryDto;
import com.gunnarro.android.terex.domain.entity.Timesheet;
import com.gunnarro.android.terex.domain.entity.TimesheetEntry;
import com.gunnarro.android.terex.domain.entity.TimesheetSummary;
import com.gunnarro.android.terex.domain.entity.TimesheetWithEntries;
import com.gunnarro.android.terex.exception.TerexApplicationException;
import com.gunnarro.android.terex.repository.TimesheetRepository;
import com.gunnarro.android.terex.utility.Utility;

import org.jetbrains.annotations.NotNull;

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
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TimesheetService {

    private final TimesheetRepository timesheetRepository;

    @Inject
    public TimesheetService(Context applicationContext) {
        timesheetRepository = new TimesheetRepository(applicationContext);
    }


    // ----------------------------------------
    // timesheet
    // ----------------------------------------

    public List<Timesheet> getTimesheets(String status) {
        return timesheetRepository.getTimesheets(status);
    }

    public Timesheet getTimesheet(Long timesheetId) {
        return timesheetRepository.getTimesheet(timesheetId);
    }

    public LiveData<List<Timesheet>> getTimesheetListLiveData() {
        return timesheetRepository.getAllTimesheets();
    }

    public void deleteTimesheet(Timesheet timesheet) {
        timesheetRepository.deleteTimesheet(timesheet);
    }

    public Long saveTimesheet(Timesheet timesheet) {
        Timesheet timesheetExisting = timesheetRepository.getTimesheet(timesheet.getClientName(), timesheet.getProjectCode(), timesheet.getYear(), timesheet.getMonth());

        // first of all, check status
        if (timesheetExisting != null && timesheetExisting.isBilled()) {
            Log.d("", "timesheet is already billed, no changes is allowed. timesheetId=" + timesheetExisting.getId() + " " + timesheetExisting.getStatus());
            return null;
        }
        try {
            Log.d("TimesheetRepository.saveTimesheet", String.format("%s", timesheetExisting));
            Long id;
            if (timesheetExisting == null) {
                timesheet.setCreatedDate(LocalDateTime.now());
                timesheet.setLastModifiedDate(LocalDateTime.now());
                timesheet.setWorkingDaysInMonth(Utility.countBusinessDaysInMonth(timesheet.getFromDate()));
                timesheet.setWorkingHoursInMonth((int) (timesheet.getWorkingDaysInMonth() * 7.5));
                id = timesheetRepository.insertTimesheet(timesheet);
                Log.d("", "insert new timesheet: " + id + " - " + timesheet);
            } else {
                timesheet.setId(timesheetExisting.getId());
                timesheet.setCreatedDate(timesheetExisting.getCreatedDate());
                timesheet.setLastModifiedDate(LocalDateTime.now());
                timesheet.setWorkingDaysInMonth(Utility.countBusinessDaysInMonth(timesheet.getFromDate()));
                timesheet.setWorkingHoursInMonth((int) (timesheet.getWorkingDaysInMonth() * 7.5));
                timesheetRepository.updateTimesheet(timesheet);
                id = timesheet.getId();
                Log.d("", "update timesheet: " + id + " - " + timesheet);
            }
            return id;
        } catch (InterruptedException | ExecutionException e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            e.printStackTrace();
            throw new TerexApplicationException("Error saving timesheet!", e.getMessage(), e.getCause());
        }
    }

    // ----------------------------------------
    // timesheet entry
    // ----------------------------------------


    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    @Transaction
    public Long saveTimesheetEntry(@NotNull final TimesheetEntry timesheetEntry) {
        try {
            TimesheetEntry timesheetEntryExisting = timesheetRepository.getTimesheetEntry(timesheetEntry.getTimesheetId(), timesheetEntry.getWorkdayDate());
            Log.d("TimesheetRepository.saveTimesheetEntry", String.format("%s", timesheetEntryExisting));
            // set the timesheet work date week number here, this is only used to simplify accumulate timesheet by week by the invoice service
            timesheetEntry.setWorkdayWeek(timesheetEntry.getWorkdayDate().get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear()));
            Long id = null;
            if (timesheetEntryExisting == null) {
                timesheetEntry.setCreatedDate(LocalDateTime.now());
                timesheetEntry.setLastModifiedDate(LocalDateTime.now());
                id = timesheetRepository.insertTimesheetEntry(timesheetEntry);
                Log.d("", "insert new timesheet entry: " + id + " - " + timesheetEntry.getWorkdayDate());
            } else {
                timesheetEntry.setId(timesheetEntryExisting.getId());
                timesheetEntry.setCreatedDate(timesheetEntryExisting.getCreatedDate());
                timesheetEntry.setLastModifiedDate(LocalDateTime.now());
                timesheetRepository.updateTimesheetEntry(timesheetEntry);
                id = timesheetEntry.getId();
                Log.d("", "update timesheet entry: " + id + " - " + timesheetEntry.getWorkdayDate());
            }
            return id;
        } catch (InterruptedException | ExecutionException e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            e.printStackTrace();
            throw new TerexApplicationException("Error saving timesheet entry!", e.getMessage(), e.getCause());
        }
    }

    public void deleteTimesheetEntry(TimesheetEntry timesheetEntry) {
        timesheetRepository.deleteTimesheetEntry(timesheetEntry);
    }

    public TimesheetEntry getMostRecentTimeSheetEntry(Long timesheetId) {
        return timesheetRepository.getMostRecentTimeSheetEntry(timesheetId);
    }

    public TimesheetWithEntries getTimesheetWithEntries(Long timesheetId) {
        return timesheetRepository.getTimesheetWithEntries(timesheetId);
    }

    public LiveData<List<TimesheetEntry>> getTimesheetEntryListLiveData(Long timesheetId) {
        return timesheetRepository.getTimesheetEntryListLiveData(timesheetId);
    }

    public List<TimesheetEntry> getTimesheetEntryList(Long timesheetId) {
        return timesheetRepository.getTimesheetEntryList(timesheetId);
    }

    // ----------------------------------------
    // timesheet summary
    // ----------------------------------------

    public Long saveTimesheetSummary(TimesheetSummary timesheetSummary) {
        return timesheetRepository.saveTimesheetSummary(timesheetSummary);
    }

    /**
     * Used as invoice attachment
     */
    public List<TimesheetSummary> createTimesheetSummary(@NotNull Long timesheetId) {
        // check timesheet status
        Timesheet timesheet = getTimesheet(timesheetId);
        if (!timesheet.isCompleted()) {
            throw new TerexApplicationException("Application error, timesheet have status COMPLETED", "50023", null);
        }
        Log.d("createInvoiceSummary", String.format("timesheetId=%s", timesheetId));
        List<TimesheetEntry> timesheetEntryList = getTimesheetEntryList(timesheetId);
        if (timesheetEntryList == null || timesheetEntryList.isEmpty()) {
            throw new TerexApplicationException("Application error, timesheet not ready for billing, no entries found!", "50023", null);
        }
        Log.d("createInvoiceSummary", "timesheet entries: " + timesheetEntryList);
        // accumulate timesheet by week for the mount
        Map<Integer, List<TimesheetEntry>> timesheetWeekMap = timesheetEntryList.stream().collect(Collectors.groupingBy(TimesheetEntry::getWorkdayWeek));
        List<TimesheetSummary> invoiceSummaryByWeek = new ArrayList<>();
        timesheetWeekMap.forEach((k, e) -> {
            invoiceSummaryByWeek.add(buildTimesheetSummaryForWeek(timesheetId, k, e));
        });
        Log.d("createInvoiceSummary", "timesheet summary by week: " + invoiceSummaryByWeek);
        // close the timesheet after invoice have been generated, is not possible to do any form of changes on the time list.
        timesheet.setStatus(Timesheet.TimesheetStatusEnum.BILLED.name());
        saveTimesheet(timesheet);

        // then close all timesheet entries
        timesheetEntryList.forEach(e -> {
            timesheetRepository.closeTimesheetEntry(e.getId());
        });

        invoiceSummaryByWeek.sort(Comparator.comparing(TimesheetSummary::getWeekInYear));
        return invoiceSummaryByWeek;
    }

    private TimesheetSummary buildTimesheetSummaryForWeek(@NotNull Long timesheetId, @NotNull Integer week, @NotNull List<TimesheetEntry> timesheetEntryList) {
        TimesheetSummary timesheetSummary = new TimesheetSummary();
        timesheetSummary.setCreatedDate(LocalDateTime.now());
        timesheetSummary.setLastModifiedDate(LocalDateTime.now());
        timesheetSummary.setTimesheetId(timesheetId);
        timesheetSummary.setWeekInYear(week);
        timesheetSummary.setYear(timesheetEntryList.get(0).getWorkdayDate().getYear());
        timesheetSummary.setFromDate(Utility.getFirstDayOfWeek(timesheetEntryList.get(0).getWorkdayDate(), week));
        timesheetSummary.setToDate(Utility.getLastDayOfWeek(timesheetEntryList.get(0).getWorkdayDate(), week));
        timesheetSummary.setTotalWorkedDays(timesheetEntryList.size());
        timesheetEntryList.forEach(t -> {
            timesheetSummary.setTotalBilledAmount(timesheetSummary.getTotalBilledAmount() + (t.getHourlyRate() * ((double) t.getWorkedMinutes() / 60)));
            timesheetSummary.setTotalWorkedHours(timesheetSummary.getTotalWorkedHours() + (double) t.getWorkedMinutes() / 60);
        });
        return timesheetSummary;
    }

    public List<TimesheetSummary> buildTimesheetSummaryByWeek(Integer year, Integer month) {
        Map<Integer, List<TimesheetEntry>> weekMap = new HashMap<>();
        generateTimesheet(year, month).forEach(t -> {
            int week = getWeek(t.getWorkdayDate());
            if (!weekMap.containsKey(week)) {
                weekMap.put(week, new ArrayList<>());
            }
            Objects.requireNonNull(weekMap.get(week)).add(t);
        });
        List<TimesheetSummary> timesheetSummaryByWeek = new ArrayList<>();

        weekMap.forEach((k, e) -> {
            TimesheetSummary timesheetSummary = new TimesheetSummary();
            timesheetSummary.setWeekInYear(k);
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
     * @return
     */
    public List<TimesheetEntry> generateTimesheet(Integer year, Integer month) {
        return getWorkingDays(year, month).stream().map(this::createTimesheet).collect(Collectors.toList());
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


    private TimesheetEntryDto mapToTimesheetEntryDto(TimesheetEntry timesheetEntry) {
        TimesheetEntryDto timesheetEntryDto = new TimesheetEntryDto();
        timesheetEntryDto.setComments(timesheetEntry.getComment());
        timesheetEntryDto.setFromTime(timesheetEntry.getFromTime().toString());
        timesheetEntryDto.setToTime(timesheetEntry.getToTime().toString());
        timesheetEntryDto.setWorkdayDate(timesheetEntry.getWorkdayDate());
        return timesheetEntryDto;
    }
}
