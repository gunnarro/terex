package com.gunnarro.android.terex.ui.view;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.gunnarro.android.terex.domain.entity.Timesheet;
import com.gunnarro.android.terex.repository.TimesheetRepository;

import java.util.List;

/**
 * Repository is completely separated from the UI through the ViewModel.
 * We use AndroidViewModel because we can pass the application context in the constructor.
 */
public class TimesheetViewModel extends AndroidViewModel {

    private final TimesheetRepository repository;
    // Using LiveData and caching what getAlphabetizedWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    private final LiveData<List<Timesheet>> timesheets;

    public TimesheetViewModel(@NonNull Application application) {
        super(application);
        repository = new TimesheetRepository(application);
        timesheets = repository.getAllTimesheet();
    }

    public LiveData<List<Timesheet>> getTimesheetLiveData() {
        return timesheets;
    }

    public void save(Timesheet timesheet) {
        Log.d("TimesheetViewModel.save" , "save: " + timesheet);
        repository.save(timesheet);
    }

    public void delete(Timesheet timesheet) {
        Log.d("TimesheetViewModel.delete" , "save: " + timesheet);
        repository.delete(timesheet);
    }
}
