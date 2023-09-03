package com.gunnarro.android.terex.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.gunnarro.android.terex.config.AppDatabase;
import com.gunnarro.android.terex.domain.entity.Timesheet;
import com.gunnarro.android.terex.exception.TerexApplicationException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class TimesheetRepository {

    private final TimesheetDao timesheetDao;
    private final LiveData<List<Timesheet>> timesheetList;

    // Note that in order to unit test the WordRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    public TimesheetRepository(Application application) {
        timesheetDao = AppDatabase.getDatabase(application).timesheetDao();
        timesheetList = timesheetDao.getAll();
        //  Calendar cal = Calendar.getInstance();
        // allTimesheets.getValue().stream().filter(t -> cal.setTime(t.getFromDate()) == cal.get(Calendar.WEEK_OF_YEAR))

    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    public LiveData<List<Timesheet>> getAllTimesheet() {
        Log.d("TimesheetRepository.getAllTimesheet", "refresh live data in fragment");
        return timesheetDao.getAll();
    }

    public List<Timesheet> getTimesheetByProject(String projectName, int month) {
        return Objects.requireNonNull(timesheetList.getValue()).stream().filter(t -> t.getProjectName().equals(projectName) && t.getWorkdayDate().getMonthValue() == month).collect(Collectors.toList());
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public Long save(final Timesheet timesheet) {
        try {
            Timesheet timesheetExisting = getTimesheet(timesheet.getClientName(), timesheet.getProjectName(), timesheet.getWorkdayDate());
            Log.d("TimesheetRepository.save", String.format("%s", timesheetExisting));
            Long id;
            if (timesheetExisting == null) {
                timesheet.setCreatedDate(LocalDateTime.now());
                timesheet.setLastModifiedDate(LocalDateTime.now());
                id = insertTimesheet(timesheet);
            } else {
                timesheet.setLastModifiedDate(LocalDateTime.now());
                Integer i = updateTimesheet(timesheet);
                id = Long.valueOf(i);
            }
            return id;
        } catch (InterruptedException | ExecutionException e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            e.printStackTrace();
            throw new TerexApplicationException("Error saving credential!", e.getMessage(), e.getCause());
        }
    }

    private Timesheet getTimesheet(String clientName, String projectName, LocalDate workDayDate) throws InterruptedException, ExecutionException {
        CompletionService<Timesheet> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
        service.submit(() -> timesheetDao.getTimesheet(clientName, projectName, workDayDate));
        Future<Timesheet> future = service.take();
        return future.get();
    }

    private Long insertTimesheet(Timesheet timesheet) throws InterruptedException, ExecutionException {
        CompletionService<Long> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
        service.submit(() -> timesheetDao.insert(timesheet));
        Future<Long> future = service.take();
        return future.get();
    }

    private Integer updateTimesheet(Timesheet timesheet) throws InterruptedException, ExecutionException {
        CompletionService<Integer> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
        service.submit(() -> timesheetDao.update(timesheet));
        Future<Integer> future = service.take();
        return future.get();
    }

    public void delete(Timesheet timesheet) {
        AppDatabase.databaseExecutor.execute(() -> {
            timesheetDao.delete(timesheet);
            Log.d("TimesheetRepository.save", "deleted, id=" + timesheet.getId());
        });
    }
}
