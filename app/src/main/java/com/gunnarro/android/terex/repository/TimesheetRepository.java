package com.gunnarro.android.terex.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.room.Transaction;

import com.gunnarro.android.terex.config.AppDatabase;
import com.gunnarro.android.terex.domain.entity.Timesheet;
import com.gunnarro.android.terex.domain.entity.TimesheetEntry;
import com.gunnarro.android.terex.domain.entity.TimesheetSummary;
import com.gunnarro.android.terex.domain.entity.TimesheetWithEntries;
import com.gunnarro.android.terex.exception.TerexApplicationException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;

import javax.inject.Singleton;

@Singleton
public class TimesheetRepository {

    private final TimesheetDao timesheetDao;
    private final TimesheetEntryDao timesheetEntryDao;
    private final TimesheetSummaryDao timesheetSummaryDao;

    // Note that in order to unit test the WordRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    public TimesheetRepository() {
        timesheetDao = AppDatabase.getDatabase().timesheetDao();
        timesheetEntryDao = AppDatabase.getDatabase().timesheetEntryDao();
        timesheetSummaryDao = AppDatabase.getDatabase().timesheetSummaryDao();
    }


    public LiveData<Map<Timesheet, List<TimesheetEntry>>> getTimesheetLiveData(Long timesheetId) {
        return timesheetDao.getTimesheetLiveData(timesheetId);
    }

    public Integer getRegisteredWorkedDays(Long timesheetId) {
        return timesheetEntryDao.getRegisteredWorkedDays(timesheetId);
    }

    public Integer getRegisteredWorkedHours(Long timesheetId) {
        return timesheetEntryDao.getRegisteredWorkedHours(timesheetId);
    }

    public List<TimesheetSummary> getTimesheetSummary(Long timesheetId) {
        try {
            CompletionService<List<TimesheetSummary>> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
            service.submit(() -> timesheetSummaryDao.getTimesheetSummaries(timesheetId));
            Future<List<TimesheetSummary>> future = service.take();
            return future != null ? future.get() : null;
        } catch (InterruptedException | ExecutionException e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("Error getting timesheet with entries list", e.getMessage(), e.getCause());
        }
    }

    public Long saveTimesheetSummary(TimesheetSummary timesheetSummary) {
        try {
            CompletionService<Long> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
            service.submit(() -> timesheetSummaryDao.insert(timesheetSummary));
            Future<Long> future = service.take();
            return future != null ? future.get() : null;
        } catch (InterruptedException | ExecutionException e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("Error saving timesheet summary", e.getMessage(), e.getCause());
        }
    }

    @Transaction
    public TimesheetWithEntries getTimesheetWithEntries(Long timesheetId) {
        try {
            CompletionService<TimesheetWithEntries> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
            service.submit(() -> timesheetDao.getTimesheetWithEntries(timesheetId));
            Future<TimesheetWithEntries> future = service.take();
            return future != null ? future.get() : null;
        } catch (InterruptedException | ExecutionException e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("Error getting timesheet with entries list", e.getMessage(), e.getCause());
        }
    }

    public Timesheet getTimesheet(Long id) {
        try {
            CompletionService<Timesheet> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
            service.submit(() -> timesheetDao.getTimesheetById(id));
            Future<Timesheet> future = service.take();
            return future != null ? future.get() : null;
        } catch (InterruptedException | ExecutionException e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("Error getting timesheet!", e.getMessage(), e.getCause());
        }
    }

    public Timesheet getTimesheet(String clientName, String projectCode, Integer year, Integer mount) {
        try {
            CompletionService<Timesheet> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
            service.submit(() -> timesheetDao.getTimesheet(clientName, projectCode, year, mount));
            Future<Timesheet> future = service.take();
            return future != null ? future.get() : null;
        } catch (InterruptedException | ExecutionException e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("Error getting timesheet!", e.getMessage(), e.getCause());
        }
    }

    public List<Timesheet> getTimesheets(String status) {
        try {
            CompletionService<List<Timesheet>> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
            service.submit(() -> timesheetDao.getTimesheets(status));
            Future<List<Timesheet>> future = service.take();
            return future != null ? future.get() : null;
        } catch (InterruptedException | ExecutionException e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("Error getting timesheet list", e.getMessage(), e.getCause());
        }
    }

    public LiveData<List<Timesheet>> getTimesheetByYear(final Integer year) {
        try {
            CompletionService<LiveData<List<Timesheet>>> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
            service.submit(() -> timesheetDao.getTimesheetByYear(year));
            Future<LiveData<List<Timesheet>>> future = service.take();
            return future != null ? future.get() : null;
        } catch (InterruptedException | ExecutionException e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("Error getting timesheet list", e.getMessage(), e.getCause());
        }
    }

    public List<TimesheetEntry> getTimesheetEntryList(Long timesheetId) {
        try {
            CompletionService<List<TimesheetEntry>> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
            service.submit(() -> timesheetEntryDao.getTimesheetEntryList(timesheetId));
            Future<List<TimesheetEntry>> future = service.take();
            return future != null ? future.get() : null;
        } catch (InterruptedException | ExecutionException e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("Error getting timesheet entry list", e.getMessage(), e.getCause());
        }
    }

    /**
     * Return a clone of data for the last added timesheet or from the timesheet marked as uses as default.
     * Note! all data is not returned, such as id, created and last modified date, for example.
     */
    public TimesheetEntry getMostRecentTimeSheetEntry(Long timesheetId) {
        try {
            CompletionService<TimesheetEntry> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
            service.submit(() -> timesheetEntryDao.getMostRecent(timesheetId));
            Future<TimesheetEntry> future = service.take();
            return TimesheetEntry.clone(future.get());
        } catch (InterruptedException | ExecutionException e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("Error getting most recent timesheet", e.getMessage(), e.getCause());
        }
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    public LiveData<List<TimesheetEntry>> getTimesheetEntryListLiveData(Long timesheetId) {
        try {
            CompletionService<LiveData<List<TimesheetEntry>>> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
            service.submit(() -> timesheetEntryDao.getTimesheetEntryListLiveData(timesheetId));
            Future<LiveData<List<TimesheetEntry>>> future = service.take();
            LiveData<List<TimesheetEntry>> liveDate = future != null ? future.get() : null;
            Log.d("TimesheetRepository.getTimesheetEntryListLiveData", String.format("timesheetId=%s, data=%s", timesheetId, liveDate.getValue()));
            return liveDate;
        } catch (InterruptedException | ExecutionException e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("Error getting timesheet entry list", e.getMessage(), e.getCause());
        }
    }

    public Integer updateTimesheet(Timesheet timesheet) throws InterruptedException, ExecutionException {
        Log.d("TimesheetRepository.updateTimesheet", String.format("%s", timesheet));
        CompletionService<Integer> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
        service.submit(() -> timesheetDao.update(timesheet));
        Future<Integer> future = service.take();
        return future != null ? future.get() : null;
    }

    public Long insertTimesheet(Timesheet timesheet) throws InterruptedException, ExecutionException {
        Log.d("TimesheetRepository.insertTimesheet", String.format("%s", timesheet));
        CompletionService<Long> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
        service.submit(() -> timesheetDao.insert(timesheet));
        Future<Long> future = service.take();
        return future != null ? future.get() : null;
    }

    public void deleteTimesheet(Timesheet timesheet) {
        AppDatabase.databaseExecutor.execute(() -> {
            timesheetDao.delete(timesheet);
            Log.d("TimesheetRepository.delete", "deleted, id=" + timesheet.getId());
        });
    }

    public TimesheetEntry getTimesheetEntry(Long timesheetId, LocalDate workDayDate) throws InterruptedException, ExecutionException {
        CompletionService<TimesheetEntry> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
        service.submit(() -> timesheetEntryDao.getTimesheet(timesheetId, workDayDate));
        Future<TimesheetEntry> future = service.take();
        return future != null ? future.get() : null;
    }

    public TimesheetEntry getTimesheetEntry(Long timesheetId, LocalDate workDayDate, String status) throws InterruptedException, ExecutionException {
        CompletionService<TimesheetEntry> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
        service.submit(() -> timesheetEntryDao.getTimesheet(timesheetId, workDayDate, status));
        Future<TimesheetEntry> future = service.take();
        return future != null ? future.get() : null;
    }

    public Long insertTimesheetEntry(TimesheetEntry timesheetEntry) throws InterruptedException, ExecutionException {
        CompletionService<Long> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
        service.submit(() -> timesheetEntryDao.insert(timesheetEntry));
        Future<Long> future = service.take();
        return future != null ? future.get() : null;
    }

    public Integer updateTimesheetEntry(TimesheetEntry timesheetEntry) throws InterruptedException, ExecutionException {
        if (timesheetEntry.isOpen()) {
            CompletionService<Integer> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
            service.submit(() -> timesheetEntryDao.update(timesheetEntry));
            Future<Integer> future = service.take();
            return future != null ? future.get() : null;
        } else {
            Log.e("", String.format("not allowed to update timesheet entry in this status! status=%s", timesheetEntry.getStatus()));
            return 0;
        }
    }

    public boolean closeTimesheetEntry(Long timesheetEntryId) {
        AppDatabase.databaseExecutor.execute(() -> {
            timesheetEntryDao.closeTimesheetEntry(timesheetEntryId);
        });
        return true;
    }

    /**
     * Not allowed to delete if timesheet entry status is equal to BILLED
     */
    public int deleteTimesheetEntry(TimesheetEntry timesheetEntry) {
        if (timesheetEntry.isOpen()) {
            AppDatabase.databaseExecutor.execute(() -> {
                timesheetEntryDao.delete(timesheetEntry);
            });
        } else {
            Log.d("", "not allowed to delete in this status!");
            return 0;
        }
        return 1;
    }
}
