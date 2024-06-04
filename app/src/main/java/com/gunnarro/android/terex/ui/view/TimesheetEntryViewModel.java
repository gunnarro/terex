package com.gunnarro.android.terex.ui.view;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.gunnarro.android.terex.domain.dto.TimesheetDto;
import com.gunnarro.android.terex.domain.entity.Timesheet;
import com.gunnarro.android.terex.domain.entity.TimesheetEntry;
import com.gunnarro.android.terex.service.TimesheetService;

import java.util.List;
import java.util.Map;

/**
 * Repository is completely separated from the UI through the ViewModel.
 * We use AndroidViewModel because we can pass the application context in the constructor.
 */
public class TimesheetEntryViewModel extends AndroidViewModel {

    private final TimesheetService timesheetService;

    private final MutableLiveData<List<TimesheetEntry>> timesheetEntryListLiveData;

    public TimesheetEntryViewModel(@NonNull Application application) {
        super(application);
        timesheetEntryListLiveData = new MutableLiveData<>();
        timesheetService = new TimesheetService();
        //timesheetEntryListLiveData.setValue(timesheetService.getTimesheetEntryListLiveData(timesheetId));
    }

    public LiveData<List<TimesheetEntry>> getTimesheetEntryLiveData(Long timesheetId) {
        LiveData<List<TimesheetEntry>> listLiveData = timesheetService.getTimesheetEntryListLiveData(timesheetId);
        Log.d("getTimesheetLiveData", String.format("timesheetId=%s, timesheetEntries=%s", timesheetId, listLiveData.getValue()));
        return listLiveData;
    }

    public LiveData<Map<Timesheet, List<TimesheetEntry>>> getTimesheetLiveData(Long timesheetId) {
        LiveData<Map<Timesheet, List<TimesheetEntry>>> listLiveData = timesheetService.getTimesheetLiveData(timesheetId);
        Log.d("getTimesheetLiveData", String.format("timesheetId=%s, timesheetEntries=%s", timesheetId, listLiveData.getValue()));
        return listLiveData;
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
}
