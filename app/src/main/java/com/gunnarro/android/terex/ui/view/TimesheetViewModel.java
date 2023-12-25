package com.gunnarro.android.terex.ui.view;

import android.app.Application;
import android.app.KeyguardManager;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.gunnarro.android.terex.domain.dto.TimesheetDto;
import com.gunnarro.android.terex.domain.entity.Timesheet;
import com.gunnarro.android.terex.domain.mapper.TimesheetMapper;
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
    private final LiveData<List<Timesheet>> timesheetList;

    public TimesheetViewModel(@NonNull Application application, Integer year) {
        super(application);
        timesheetService = new TimesheetService(application);
        timesheetList = timesheetService.getTimesheetListLiveData(year);
    }

    public LiveData<List<Timesheet>> getTimesheetLiveData(Integer year) {
        //return Transformations.map(timesheetService.getTimesheetListLiveData(year) {
        //    t -> TimesheetMapper.toTimesheetDtoList(timesheetList.getValue())
        //};
      //  timesheetList = timesheetService.getTimesheetListLiveData(year);
        return timesheetList;
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
        timesheetService.deleteTimesheet(timesheet);
    }
}
