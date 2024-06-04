package com.gunnarro.android.terex.ui.view;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.gunnarro.android.terex.domain.dto.TimesheetDto;
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
    private final MutableLiveData<List<TimesheetDto>> timesheetListLiveData;

    public TimesheetViewModel(@NonNull Application application, Integer year) {
        super(application);
        timesheetListLiveData = new MutableLiveData<>();
        timesheetService = new TimesheetService();
        timesheetListLiveData.setValue(timesheetService.getTimesheetDtoList(year));
    }

    public LiveData<List<TimesheetDto>> getTimesheetLiveData(Integer year) {
        Log.d("getTimesheetLiveData", "selected year=" + year);
        timesheetListLiveData.setValue(timesheetService.getTimesheetDtoList(year));
        return timesheetListLiveData;
    }

    public void saveTimesheet(Timesheet timesheet) {
        timesheetService.saveTimesheet(timesheet);
    }

    public Timesheet getTimesheet(Long timesheetId) {
        return timesheetService.getTimesheet(timesheetId);
    }

    public TimesheetDto getTimesheetDto(Long timesheetId) {
        return timesheetService.getTimesheetDto(timesheetId);
    }

    public void deleteTimesheet(Timesheet timesheet) {
        timesheetService.deleteTimesheet(timesheet.getId());
    }
}
