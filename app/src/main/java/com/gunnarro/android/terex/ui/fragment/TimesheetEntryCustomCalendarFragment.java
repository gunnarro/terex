package com.gunnarro.android.terex.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.applandeo.materialcalendarview.CalendarDay;
import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.gunnarro.android.terex.R;
import com.gunnarro.android.terex.domain.entity.TimesheetEntry;
import com.gunnarro.android.terex.exception.InputValidationException;
import com.gunnarro.android.terex.exception.TerexApplicationException;
import com.gunnarro.android.terex.service.TimesheetService;
import com.gunnarro.android.terex.utility.Utility;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

/**
 * <a href="https://github.com/Applandeo/Material-Calendar-View">Material-Calendar-View</a>
 */
public class TimesheetEntryCustomCalendarFragment extends BaseFragment {

    private LocalDate selectedWorkDayDate;

    private TimesheetService timesheetService;

    @Inject
    public TimesheetEntryCustomCalendarFragment() {
        // Needed by dagger framework
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requireActivity().setTitle(R.string.title_timesheet_calendar);
        timesheetService = new TimesheetService();
        Log.d(Utility.buildTag(getClass(), "onCreate"), "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_timesheet_entry_custom_calendar, container, false);
        CalendarView calendarView = view.findViewById(R.id.view_timesheet_custom_calendar);
        Long timesheetId = getArguments().getLong(TimesheetListFragment.TIMESHEET_ID_KEY);
        // calendarView.setSelectedDates(selectedDates);
        //@Deprecated("Use setCalendarDays(List<CalendarDay>) with isEnabled = false")
        //calendarView.setDisabledDays(createSelectedDates(timesheetId));
        calendarView.setCalendarDays(createSelectedDates(timesheetId));
        //  @Deprecated("Use setCalendarDays() instead")
      //  calendarView.setEvents(createEventDays(timesheetId));
        //  @Deprecated("Use setOnCalendarDayClickListener instead")
        calendarView.setOnCalendarDayClickListener(day -> {
            selectedWorkDayDate = LocalDate.of(
                    day.getCalendar().get(Calendar.YEAR),
                    day.getCalendar().get(Calendar.MONTH) + 1,
                    day.getCalendar().get(Calendar.DAY_OF_MONTH));
            Log.d("clicked on date:", selectedWorkDayDate.toString());
        });
        /*
        calendarView.setOnDayClickListener(eventDay -> {
            selectedWorkDayDate = LocalDate.of(
                    eventDay.getCalendar().get(Calendar.YEAR),
                    eventDay.getCalendar().get(Calendar.MONTH) + 1,
                    eventDay.getCalendar().get(Calendar.DAY_OF_MONTH));
            Log.d("clicked on date:", selectedWorkDayDate.toString());
        });
*/
        calendarView.setSelected(true);
        // should be set equal to the timesheet month, so get the timesheet from date
        LocalDate timesheetFromDate = timesheetService.getTimesheet(timesheetId).getFromDate();
        Calendar timesheetDateCal = Calendar.getInstance();
        timesheetDateCal.set(timesheetFromDate.getYear(), timesheetFromDate.getMonthValue() - 1, timesheetFromDate.getDayOfMonth());
        try {
            calendarView.setDate(timesheetDateCal);
            Calendar firstDayOfMonth = Calendar.getInstance();
            firstDayOfMonth.setTimeInMillis(Utility.getFirstDayOfMonth(LocalDate.now()).toEpochDay());
            Calendar lastDayOfMonth = Calendar.getInstance();
            lastDayOfMonth.setTimeInMillis(Utility.getLastDayOfMonth(LocalDate.now()).toEpochDay());
            // fixme
            //   calendarView.setMinimumDate(firstDayOfMonth);
            //   calendarView.setMaximumDate(lastDayOfMonth);
        } catch (OutOfDateRangeException e) {
            throw new TerexApplicationException("Error creating view!", "50051", e);
        }

        view.findViewById(R.id.btn_timesheet_calendar_save).setEnabled(true);
        view.findViewById(R.id.btn_timesheet_calendar_save).setOnClickListener(v -> {
            v.setEnabled(false);
            handleButtonSaveClick();
            v.setEnabled(true);
        });


        view.findViewById(R.id.btn_timesheet_calendar_cancel).setOnClickListener(v -> {
            view.findViewById(R.id.btn_timesheet_calendar_cancel).setBackgroundColor(getResources().getColor(R.color.color_btn_bg_cancel, view.getContext().getTheme()));
            // Simply return back to the timesheet list
            Bundle bundle = new Bundle();
            bundle.putLong(TimesheetListFragment.TIMESHEET_ID_KEY, timesheetId);
            navigateTo(R.id.nav_from_timesheet_entry_calendar_to_timesheet_entry_list, bundle);
        });

        Log.d(Utility.buildTag(getClass(), "onCreateView"), "");
        return view;
    }

    /**
     * Update backup info after view is successfully create
     */
    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private TimesheetEntry readTimesheetEntryFromBundle() {
        String timesheetEntryJson = getArguments() != null ? getArguments().getString(TimesheetEntryListFragment.TIMESHEET_ENTRY_JSON_KEY) : null;
        Log.d("receives timesheet", "" + timesheetEntryJson);
        if (timesheetEntryJson != null && !timesheetEntryJson.isEmpty()) {
            try {
                TimesheetEntry timesheetEntry = Utility.gsonMapper().fromJson(timesheetEntryJson, TimesheetEntry.class);
                Log.d(Utility.buildTag(getClass(), "onFragmentResult"), String.format("timesheetEntryJson: %s", timesheetEntry));
                return timesheetEntry;
            } catch (Exception e) {
                Log.e("", e.toString());
                throw new TerexApplicationException("Application Error!", "5000", e);
            }
        } else {
            // no recent timesheet entry found, should not happen
            throw new TerexApplicationException("Timesheet entry not found!", "55023", null);
        }
    }

    private List<CalendarDay> createCalendarDays(Long timesheetId) {
        List<CalendarDay> calendarDays = new ArrayList<>();
        calendarDays.addAll(createSelectedDates(timesheetId));
        calendarDays.addAll(createAvailableCalendarDays(timesheetId));
        return calendarDays;
    }

    private List<CalendarDay> createAvailableCalendarDays(Long timesheetId) {
        List<CalendarDay> calendarDays = new ArrayList<>();
        List<TimesheetEntry> timesheetEntryList = timesheetService.getTimesheetEntryList(timesheetId);
        timesheetEntryList.forEach(t -> {
            Calendar cal = Calendar.getInstance();
            cal.set(t.getWorkdayDate().getYear(), t.getWorkdayDate().getMonth().getValue() - 1, t.getWorkdayDate().getDayOfMonth());
           // calendarDays.add(new CalendarDay(cal, R.drawable.timesheet_day_ok_24, getResources().getColor(R.color.color_btn_bg_delete, null)));
            CalendarDay calendarDay = new CalendarDay(cal);
            calendarDay.setLabelColor(R.drawable.timesheet_day_ok_24);
            calendarDay.setSelectedBackgroundResource(getResources().getColor(R.color.color_btn_bg_delete, null));
            calendarDays.add(calendarDay);
            Log.d("TimesheetCustomCalendarFragment", "ADD SELECTED DATE: " + t.getWorkdayDate().toString());
        });
        return calendarDays;
    }


    private List<EventDay> createEventDays(Long timesheetId) {
        List<EventDay> eventDays = new ArrayList<>();
        List<TimesheetEntry> timesheetEntryList = timesheetService.getTimesheetEntryList(timesheetId);
        timesheetEntryList.forEach(t -> {
            Calendar cal = Calendar.getInstance();
            cal.set(t.getWorkdayDate().getYear(), t.getWorkdayDate().getMonth().getValue() - 1, t.getWorkdayDate().getDayOfMonth());
            eventDays.add(new EventDay(cal, R.drawable.timesheet_day_ok_24, getResources().getColor(R.color.color_btn_bg_delete, null)));
            Log.d("TimesheetCustomCalendarFragment", "ADD SELECTED DATE: " + t.getWorkdayDate().toString());
        });
        return eventDays;
    }

    private List<CalendarDay> createSelectedDates(Long timesheetId) {
        List<TimesheetEntry> timesheetEntryList = timesheetService.getTimesheetEntryList(timesheetId);
        List<CalendarDay> selectedDates = new ArrayList<>();
        timesheetEntryList.forEach(t -> {
            Calendar cal = Calendar.getInstance();
            cal.set(t.getWorkdayDate().getYear(), t.getWorkdayDate().getMonth().getValue() - 1, t.getWorkdayDate().getDayOfMonth());
            selectedDates.add(new CalendarDay(cal));
            Log.d("TimesheetCustomCalendarFragment", "ADD SELECTED DATE: " + t.getWorkdayDate());
        });
        return selectedDates;
    }

    private TimesheetEntry getTimesheetEntry() {
        TimesheetEntry timesheetEntry = readTimesheetEntryFromBundle();
        // need only update the work day date because we all other data is read from the last added timesheet.
        timesheetEntry.setWorkdayDate(selectedWorkDayDate);
        return timesheetEntry;
    }

    private void returnToTimesheetEntryList(Long timesheetId) {
        Bundle bundle = new Bundle();
        bundle.putLong(TimesheetListFragment.TIMESHEET_ID_KEY, timesheetId);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_content_frame, TimesheetEntryListFragment.class, bundle)
                .setReorderingAllowed(true)
                .commit();
    }

    /**
     * When button save is click and new timesheet entry event is sent in order to insert it into the database
     */
    private void handleButtonSaveClick() {
        if (selectedWorkDayDate == null) {
            showInfoDialog("Info", "You must select a workday date!");
            return;
        }
        try {
            TimesheetEntry timesheetEntry = getTimesheetEntry();
            timesheetService.saveTimesheetEntry(timesheetEntry);
            showSnackbar(String.format(getResources().getString(R.string.info_timesheet_list_add_msg_format), timesheetEntry.getWorkdayDate(), timesheetEntry.getWorkedHours()), R.color.color_snackbar_text_add);
        } catch (TerexApplicationException | InputValidationException e) {
           showInfoDialog("Info", e.getMessage());
        }
    }
}
