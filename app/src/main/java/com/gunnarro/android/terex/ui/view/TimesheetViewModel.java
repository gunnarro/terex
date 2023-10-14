package com.gunnarro.android.terex.ui.view;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.gunnarro.android.terex.domain.entity.Timesheet;
import com.gunnarro.android.terex.domain.entity.TimesheetEntry;
import com.gunnarro.android.terex.domain.entity.TimesheetWithEntries;
import com.gunnarro.android.terex.service.TimesheetService;

import java.util.List;

/**
 * Repository is completely separated from the UI through the ViewModel.
 * We use AndroidViewModel because we can pass the application context in the constructor.
 */
public class TimesheetViewModel extends AndroidViewModel {

    private final TimesheetService timesheetService;
    // Using LiveData and caching what getAlphabetizedWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    private final LiveData<List<TimesheetEntry>> timesheetEntryList;

    public TimesheetViewModel(@NonNull Application application) {
        super(application);
        timesheetService = new TimesheetService(application);
        timesheetEntryList = timesheetService.getTimesheetEntryListLiveData(0L);
    }

    public LiveData<List<TimesheetEntry>> getTimesheetLiveData() {
        return timesheetEntryList;
    }

    public TimesheetEntry getMostRecentTimesheetEntry(Long timesheetId) {
        return timesheetService.getMostRecentTimeSheetEntry(timesheetId);
    }

    public TimesheetWithEntries getCurrentTimesheetWithEntries() {
        return timesheetService.getCurrentTimesheetWithEntries();
    }

    public void saveTimesheet(Timesheet timesheet) {
        timesheetService.saveTimesheet(timesheet);
    }

    public void saveTimesheetEntry(TimesheetEntry timesheet) {
        Log.d("TimesheetViewModel.saveTimesheetEntry", "save: " + timesheet);
        timesheetService.saveTimesheetEntry(timesheet);
    }

    public void deleteTimesheetEntry(TimesheetEntry timesheet) {
        Log.d("TimesheetViewModel.deleteTimesheetEntry", "save: " + timesheet);
        timesheetService.deleteTimesheetEntry(timesheet);
    }
}
