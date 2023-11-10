package com.gunnarro.android.terex.ui.view;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.gunnarro.android.terex.domain.entity.TimesheetEntry;
import com.gunnarro.android.terex.domain.entity.TimesheetWithEntries;
import com.gunnarro.android.terex.service.TimesheetService;

import java.util.List;

/**
 * Repository is completely separated from the UI through the ViewModel.
 * We use AndroidViewModel because we can pass the application context in the constructor.
 */
public class TimesheetEntryViewModel extends AndroidViewModel {

    private final TimesheetService timesheetService;

    public TimesheetEntryViewModel(@NonNull Application application) {
        super(application);
        timesheetService = new TimesheetService(application);
    }

    public LiveData<List<TimesheetEntry>> getTimesheetLiveData(Long timesheetId) {
        LiveData<List<TimesheetEntry>> listLiveData = timesheetService.getTimesheetEntryListLiveData(timesheetId);
        Log.d("getTimesheetLiveData", String.format("timesheetId=%s, timesheetEntries=%s", timesheetId, listLiveData.getValue()));
        return listLiveData;
    }

    public TimesheetEntry getMostRecentTimesheetEntry(Long timesheetId) {
        return timesheetService.getMostRecentTimeSheetEntry(timesheetId);
    }

    public TimesheetWithEntries getTimesheetWithEntries(Long timesheetId) {
        if (timesheetId == null) { return null; }
        return timesheetService.getTimesheetWithEntries(timesheetId);
    }

    public void saveTimesheetEntry(TimesheetEntry timesheet) {
        Log.d("TimesheetViewModel.saveTimesheetEntry", "save: " + timesheet);
        timesheetService.saveTimesheetEntry(timesheet);
    }

    public void deleteTimesheetEntry(TimesheetEntry timesheet) {
        Log.d("TimesheetViewModel.deleteTimesheetEntry", "delete: " + timesheet);
        timesheetService.deleteTimesheetEntry(timesheet);
    }
}
