package com.gunnarro.android.terex.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.gunnarro.android.terex.config.AppDatabase;
import com.gunnarro.android.terex.domain.entity.Timesheet;

import java.util.List;
import java.util.Objects;
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

    public void insert(Timesheet timesheet) {
        AppDatabase.databaseWriteExecutor.execute(() -> timesheetDao.insert(timesheet));
    }

    public void delete(Timesheet timesheet) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            timesheetDao.delete(timesheet);
            Log.d("TimesheetRepository.save", "deleted, id=" + timesheet.getId());
        });
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public void save(Timesheet timesheet) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            Timesheet timesheetExisting = timesheetDao.getTimesheet(timesheet.getClientName(), timesheet.getProjectName(), timesheet.getWorkdayDate());
            if (timesheetExisting == null) {
                Long id = timesheetDao.insert(timesheet);
                Log.d("TimesheetRepository.save", "inserted (new), id=" + timesheetDao.getById(id));
            } else {
                timesheet.setId(timesheetExisting.getId()); // FIXME hack
                Log.d("TimesheetRepository.save", "update: " + timesheet);
                int rows = timesheetDao.update(timesheet);
                Log.d("TimesheetRepository.save", "updated: " + timesheetDao.getTimesheet(timesheet.getClientName(), timesheet.getProjectName(), timesheet.getWorkdayDate()));
            }
        });
    }
}
