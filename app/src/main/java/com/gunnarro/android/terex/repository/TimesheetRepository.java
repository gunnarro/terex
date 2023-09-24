package com.gunnarro.android.terex.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.gunnarro.android.terex.config.AppDatabase;
import com.gunnarro.android.terex.domain.entity.Timesheet;
import com.gunnarro.android.terex.domain.entity.TimesheetEntry;
import com.gunnarro.android.terex.exception.TerexApplicationException;
import com.gunnarro.android.terex.utility.Utility;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;

import javax.inject.Singleton;

@Singleton
public class TimesheetRepository {

    private final TimesheetDao timesheetDao;
    private final TimesheetEntryDao timesheetEntryDao;
    private final LiveData<List<TimesheetEntry>> timesheetEntryList;

    // Note that in order to unit test the WordRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    public TimesheetRepository(Context applicationContext) {
        timesheetDao = AppDatabase.getDatabase(applicationContext).timesheetDao();
        timesheetEntryDao = AppDatabase.getDatabase(applicationContext).timesheetEntryDao();
        timesheetEntryList = timesheetEntryDao.getTimesheetEntryListLiveData(1L);
    }

    public Timesheet createTimesheet(@NotNull String clientName, @NotNull String projectCode, @NotNull LocalDate timesheetDate) {
        Timesheet timesheet = new Timesheet();
        timesheet.setStatus("open");
        timesheet.setClientName(clientName);
        timesheet.setProjectCode(projectCode);
        timesheet.setFromDate(Utility.getFirstDayOfMonth(timesheetDate));
        timesheet.setToDate(Utility.getLastDayOfMonth(timesheetDate));
        timesheet.setMonth(timesheetDate.getMonthValue());
        timesheet.setYear(timesheetDate.getYear());
        Long id = timesheetDao.insert(timesheet);
        return timesheetDao.getTimesheetById(id);
    }

    public Timesheet getTimesheet(Long id) {
        return timesheetDao.getTimesheetById(id);
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
            e.printStackTrace();
            throw new TerexApplicationException("Error getting timesheet entry list", e.getMessage(), e.getCause());
        }
    }

    /**
     * Get timesheet for current month
     *
     * @return timesheet for current month
     */
    public Timesheet getCurrentTimesheet() {
        return timesheetDao.getTimesheet("*", "*", LocalDate.now().getYear(), LocalDate.now().getMonthValue());
    }

    public List<Timesheet> getAllTimesheets() {
        try {
            CompletionService<List<Timesheet>> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
            service.submit(timesheetDao::getAllTimesheets);
            Future<List<Timesheet>> future = service.take();
            return future != null ? future.get() : null;
        } catch (InterruptedException | ExecutionException e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("Error getting timesheet list", e.getMessage(), e.getCause());
        }
    }

    public Long getCurrentTimesheetId(String clientName, String projectCode) {
        Timesheet timesheet = timesheetDao.getTimesheet(clientName, projectCode, LocalDate.now().getYear(), LocalDate.now().getMonthValue());
        return timesheet != null ? timesheet.getId() : null;
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
            e.printStackTrace();
            throw new TerexApplicationException("Error getting timesheet entry list", e.getMessage(), e.getCause());
        }
    }

    /**
     * Return a clone of data for the last added timesheet or from the timesheet marked as uses as default.
     * Note! all data is not returned, such as id, created and last modified date, for example.
     */
    public TimesheetEntry getMostRecent() {
        try {
            CompletionService<TimesheetEntry> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
            service.submit(timesheetEntryDao::getMostRecent);
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
        Log.d("TimesheetRepository.getTimesheetEntryListLiveData", "refresh live data in fragment");
        try {
            CompletionService<LiveData<List<TimesheetEntry>>> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
            service.submit(() -> timesheetEntryDao.getTimesheetEntryListLiveData(timesheetId));
            Future<LiveData<List<TimesheetEntry>>> future = service.take();
            return future != null ? future.get() : null;
        } catch (InterruptedException | ExecutionException e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            e.printStackTrace();
            throw new TerexApplicationException("Error getting timesheet entry list", e.getMessage(), e.getCause());
        }
    }


    public Long saveTimesheet(Timesheet timesheet) {
        try {
            Timesheet timesheetExisting = getTimesheet(timesheet.getClientName(), timesheet.getProjectCode(), timesheet.getYear(), timesheet.getMonth());
            Log.d("TimesheetRepository.saveTimesheet", String.format("%s", timesheetExisting));
            Long id;
            if (timesheetExisting == null) {
                timesheet.setCreatedDate(LocalDateTime.now());
                timesheet.setLastModifiedDate(LocalDateTime.now());
                id = insertTimesheet(timesheet);
                Log.d("", "insert new timesheet: " + id + " - " + timesheetExisting);
            } else {
                timesheet.setId(timesheetExisting.getId());
                timesheet.setCreatedDate(timesheetExisting.getCreatedDate());
                timesheet.setLastModifiedDate(LocalDateTime.now());
                Integer i = updateTimesheet(timesheet);
                id = Long.valueOf(i);
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

    private Integer updateTimesheet(Timesheet timesheet) throws InterruptedException, ExecutionException {
        CompletionService<Integer> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
        service.submit(() -> timesheetDao.update(timesheet));
        Future<Integer> future = service.take();
        return future != null ? future.get() : null;
    }

    private Long insertTimesheet(Timesheet timesheet) throws InterruptedException, ExecutionException {
        CompletionService<Long> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
        service.submit(() -> timesheetDao.insert(timesheet));
        Future<Long> future = service.take();
        return future != null ? future.get() : null;
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public Long saveTimesheetEntry(@NotNull final TimesheetEntry timesheetEntry) {
        try {
            TimesheetEntry timesheetEntryExisting = getTimesheetEntry(timesheetEntry.getTimesheetId(), timesheetEntry.getWorkdayDate());
            Log.d("TimesheetRepository.saveTimesheetEntry", String.format("%s", timesheetEntryExisting));
            // set the timesheet work date week number here, this is only used to simplify accumulate timesheet by week by the invoice service
            timesheetEntry.setWorkdayWeek(timesheetEntry.getWorkdayDate().get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear()));
            Long id;
            if (timesheetEntryExisting == null) {
                timesheetEntry.setCreatedDate(LocalDateTime.now());
                timesheetEntry.setLastModifiedDate(LocalDateTime.now());
                id = insertTimesheetEntry(timesheetEntry);
                Log.d("", "insert new timesheet entry: " + id + " - " + timesheetEntry.getWorkdayDate());
            } else {
                timesheetEntry.setId(timesheetEntryExisting.getId());
                timesheetEntry.setCreatedDate(timesheetEntryExisting.getCreatedDate());
                timesheetEntry.setLastModifiedDate(LocalDateTime.now());
                Integer i = updateTimesheetEntry(timesheetEntry);
                id = Long.valueOf(i);
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

    private TimesheetEntry getTimesheetEntry(Long timesheetId, LocalDate workDayDate) throws InterruptedException, ExecutionException {
        CompletionService<TimesheetEntry> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
        service.submit(() -> timesheetEntryDao.getTimesheet(timesheetId, workDayDate));
        Future<TimesheetEntry> future = service.take();
        return future != null ? future.get() : null;
    }

    private Long insertTimesheetEntry(TimesheetEntry timesheetEntry) throws InterruptedException, ExecutionException {
        CompletionService<Long> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
        service.submit(() -> timesheetEntryDao.insert(timesheetEntry));
        Future<Long> future = service.take();
        return future != null ? future.get() : null;
    }

    private Integer updateTimesheetEntry(TimesheetEntry timesheetEntry) throws InterruptedException, ExecutionException {
        CompletionService<Integer> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
        service.submit(() -> timesheetEntryDao.update(timesheetEntry));
        Future<Integer> future = service.take();
        return future != null ? future.get() : null;
    }

    public void deleteTimesheetEntry(TimesheetEntry timesheetEntry) {
        AppDatabase.databaseExecutor.execute(() -> {
            timesheetEntryDao.delete(timesheetEntry);
            Log.d("TimesheetRepository.delete", "deleted, id=" + timesheetEntry.getId());
        });
    }
}
