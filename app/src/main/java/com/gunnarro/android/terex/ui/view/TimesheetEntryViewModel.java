package com.gunnarro.android.terex.ui.view;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.gunnarro.android.terex.domain.entity.Timesheet;
import com.gunnarro.android.terex.domain.entity.TimesheetEntry;
import com.gunnarro.android.terex.repository.TimesheetRepository;

import java.util.List;

/**
 * Repository is completely separated from the UI through the ViewModel.
 * We use AndroidViewModel because we can pass the application context in the constructor.
 */
public class TimesheetEntryViewModel extends AndroidViewModel {

    private final TimesheetRepository timesheetRepository;
    // Using LiveData and caching what getAlphabetizedWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    private final LiveData<List<TimesheetEntry>> timesheetEntryList;

    public TimesheetEntryViewModel(@NonNull Application application) {
        super(application);
        timesheetRepository = new TimesheetRepository(application);
        timesheetEntryList = timesheetRepository.getTimesheetEntryListLiveData(1L);
    }

    public LiveData<List<TimesheetEntry>> getTimesheetLiveData() {
        return timesheetEntryList;
    }

    public TimesheetEntry getMostRecent() {
        return timesheetRepository.getMostRecent();
    }

    public List<Timesheet> getAllTimesheets() {
        return timesheetRepository.getAllTimesheets();
    }

    public void saveTimesheet(Timesheet timesheet) {
        timesheetRepository.saveTimesheet(timesheet);
    }

    public void saveTimesheetEntry(TimesheetEntry timesheet) {
        Log.d("TimesheetViewModel.save", "save: " + timesheet);
        timesheetRepository.saveTimesheetEntry(timesheet);
    }

    public void deleteTimesheetEntry(TimesheetEntry timesheet) {
        Log.d("TimesheetViewModel.delete", "save: " + timesheet);
        timesheetRepository.deleteTimesheetEntry(timesheet);
    }
}
