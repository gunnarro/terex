package com.gunnarro.android.terex.ui.view;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.gunnarro.android.terex.domain.entity.TimesheetEntry;
import com.gunnarro.android.terex.service.TimesheetService;

import java.util.List;

/**
 * Repository is completely separated from the UI through the ViewModel.
 * We use AndroidViewModel because we can pass the application context in the constructor.
 */
public class TimesheetEntryViewModel extends AndroidViewModel {

    private final TimesheetService timesheetService;

    private final MutableLiveData<List<TimesheetEntry>> timesheetEntryListLiveData;

    public TimesheetEntryViewModel(@NonNull Application application, @NonNull Long timesheetId) {
        super(application);
        timesheetEntryListLiveData = new MutableLiveData<>();
        timesheetService = new TimesheetService();
        timesheetEntryListLiveData.setValue(timesheetService.getTimesheetEntryList(timesheetId));
    }

    public LiveData<List<TimesheetEntry>> getTimesheetEntryLiveData(Long timesheetId) {
        Log.d("getTimesheetEntryLiveData", String.format("timesheetId=%s, entries=%s", timesheetId, timesheetEntryListLiveData.getValue().size()));
        //timesheetEntryListLiveData.setValue(timesheetService.getTimesheetEntryList(timesheetId));
        return timesheetEntryListLiveData;
    }

    public TimesheetEntry getMostRecentTimesheetEntry(Long timesheetId) {
        return timesheetService.getMostRecentTimeSheetEntry(timesheetId);
    }

    public void saveTimesheetEntry(TimesheetEntry timesheetEntry) {
        timesheetService.saveTimesheetEntry(timesheetEntry);
    }

    public void deleteTimesheetEntry(TimesheetEntry timesheetEntry) {
        timesheetService.deleteTimesheetEntry(timesheetEntry);
    }

    public TimesheetEntry getTimesheetEntry(Long timesheetEntryId) {
        return timesheetService.getTimesheetEntry(timesheetEntryId);
    }

    public String getTimesheetTitle(Long timesheetId) {
        return timesheetService.getTimesheetTitle(timesheetId);
    }
}
