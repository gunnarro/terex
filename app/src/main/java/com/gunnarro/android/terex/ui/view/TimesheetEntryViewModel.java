package com.gunnarro.android.terex.ui.view;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.gunnarro.android.terex.domain.dto.TimesheetEntryDto;
import com.gunnarro.android.terex.service.TimesheetService;

import java.util.List;

/**
 * Repository is completely separated from the UI through the ViewModel.
 * We use AndroidViewModel because we can pass the application context in the constructor.
 */
public class TimesheetEntryViewModel extends AndroidViewModel {

    private final TimesheetService timesheetService;

    private final MutableLiveData<List<TimesheetEntryDto>> timesheetEntryListLiveData;

    public TimesheetEntryViewModel(@NonNull Application application, @NonNull Long timesheetId) {
        super(application);
        timesheetService = new TimesheetService();
        timesheetEntryListLiveData = new MutableLiveData<>();
        timesheetEntryListLiveData.setValue(timesheetService.getTimesheetEntryListDto(timesheetId));
    }

    public LiveData<List<TimesheetEntryDto>> getTimesheetEntryLiveData(Long timesheetId) {
        timesheetEntryListLiveData.setValue(timesheetService.getTimesheetEntryListDto(timesheetId));
        return timesheetEntryListLiveData;
    }

    public TimesheetEntryDto getMostRecentTimesheetEntryDto(Long timesheetId) {
        return timesheetService.getMostRecentTimeSheetEntryDto(timesheetId);
    }

    public void saveTimesheetEntryDto(TimesheetEntryDto timesheetEntryDto) {
        timesheetService.saveTimesheetEntryDto(timesheetEntryDto);
    }

    public void deleteTimesheetEntryDto(TimesheetEntryDto timesheetEntryDto) {
        timesheetService.deleteTimesheetEntryDto(timesheetEntryDto);
    }

    public TimesheetEntryDto getTimesheetEntryDto(Long timesheetEntryId) {
        return timesheetService.getTimesheetEntryDto(timesheetEntryId);
    }

    public String getTimesheetTitle(Long timesheetId) {
        return timesheetService.getTimesheetTitle(timesheetId);
    }
}
