package com.gunnarro.android.terex.ui.view;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.gunnarro.android.terex.domain.entity.Timesheet;
import com.gunnarro.android.terex.service.TimesheetService;

import java.util.List;

/**
 * Repository is completely separated from the UI through the ViewModel.
 * We use AndroidViewModel because we can pass the application context in the constructor.
 */
public class TimesheetViewModel extends AndroidViewModel {

    private final TimesheetService timesheetService;
    // Using LiveData and caching what getTimesheetListLiveData returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    private LiveData<List<Timesheet>> timesheetList;

    public TimesheetViewModel(@NonNull Application application) {
        super(application);
        timesheetService = new TimesheetService(application);
        // timesheetList = timesheetService.getTimesheetListLiveData();
    }

    public LiveData<List<Timesheet>> getTimesheetLiveData(Integer year) {
        return timesheetService.getTimesheetListLiveData(year);
    }

    public void saveTimesheet(Timesheet timesheet) {
        timesheetService.saveTimesheet(timesheet);
    }

    public Timesheet getTimesheet(Long timesheetId) {
        return timesheetService.getTimesheet(timesheetId);
    }

    public void deleteTimesheet(Timesheet timesheet) {
        timesheetService.deleteTimesheet(timesheet);
    }
}
