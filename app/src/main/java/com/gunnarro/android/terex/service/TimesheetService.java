package com.gunnarro.android.terex.service;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.gunnarro.android.terex.domain.entity.Timesheet;
import com.gunnarro.android.terex.domain.entity.TimesheetEntry;
import com.gunnarro.android.terex.domain.entity.TimesheetWithEntries;
import com.gunnarro.android.terex.exception.TerexApplicationException;
import com.gunnarro.android.terex.repository.TimesheetRepository;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TimesheetService {

    private final TimesheetRepository timesheetRepository;

    @Inject
    public TimesheetService(Context applicationContext) {
        timesheetRepository = new TimesheetRepository(applicationContext);
    }

    public Long saveTimesheet(Timesheet timesheet) {
        // first of all, check status
        if (timesheet.getStatus().equals(Timesheet.TimesheetStatusEnum.BILLED.name())) {
            Log.d("", "timesheet is closed, no changes is allowed. timesheetId=" + timesheet.getId());
            return null;
        }
        try {
            Timesheet timesheetExisting = timesheetRepository.getTimesheet(timesheet.getClientName(), timesheet.getProjectCode(), timesheet.getYear(), timesheet.getMonth());
            Log.d("TimesheetRepository.saveTimesheet", String.format("%s", timesheetExisting));
            Long id = null;
            if (timesheetExisting == null) {
                timesheet.setCreatedDate(LocalDateTime.now());
                timesheet.setLastModifiedDate(LocalDateTime.now());
                id = timesheetRepository.insertTimesheet(timesheet);
                Log.d("", "insert new timesheet: " + id + " - " + timesheet);
            } else {
                timesheet.setId(timesheetExisting.getId());
                timesheet.setCreatedDate(timesheetExisting.getCreatedDate());
                timesheet.setLastModifiedDate(LocalDateTime.now());
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


    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
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

    public TimesheetWithEntries getCurrentTimesheetWithEntries() {
        return timesheetRepository.getCurrentTimesheetWithEntries(LocalDate.now().getYear(), LocalDate.now().getMonthValue());
    }

    public LiveData<List<TimesheetEntry>> getTimesheetEntryListLiveData(Long timesheetId) {
        return timesheetRepository.getTimesheetEntryListLiveData(timesheetId);
    }

    public List<Timesheet> getTimesheets(String status) {
        return timesheetRepository.getTimesheets(status);
    }

    public List<TimesheetEntry> getTimesheetEntryList(Long timesheetId) {
        return timesheetRepository.getTimesheetEntryList(timesheetId);
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
}
