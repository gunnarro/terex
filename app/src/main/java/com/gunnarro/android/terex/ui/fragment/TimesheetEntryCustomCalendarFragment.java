package com.gunnarro.android.terex.ui.fragment;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorRes;
import androidx.fragment.app.Fragment;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.gunnarro.android.terex.R;
import com.gunnarro.android.terex.domain.entity.TimesheetEntry;
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
public class TimesheetEntryCustomCalendarFragment extends Fragment {

    private LocalDate selectedWorkDayDate;

    private TimesheetService timesheetService;

    @Inject
    public TimesheetEntryCustomCalendarFragment() {
        Log.d("TimesheetCalendarFragment", "");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requireActivity().setTitle(R.string.title_timesheet_calendar);
        timesheetService = new TimesheetService(getContext());
        Log.d(Utility.buildTag(getClass(), "onCreate"), "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_timesheet_entry_custom_calendar, container, false);
        CalendarView calendarView = view.findViewById(R.id.view_timesheet_custom_calendar);
        Long timesheetId = getArguments().getLong(TimesheetFragment.TIMESHEET_ID_KEY);
        // calendarView.setSelectedDates(selectedDates);
        calendarView.setDisabledDays(createSelectedDates(timesheetId));
        calendarView.setEvents(createEventDays(timesheetId));

        calendarView.setOnDayClickListener(eventDay -> {
            selectedWorkDayDate = LocalDate.of(
                    eventDay.getCalendar().get(Calendar.YEAR),
                    eventDay.getCalendar().get(Calendar.MONTH) + 1,
                    eventDay.getCalendar().get(Calendar.DAY_OF_MONTH));
            Log.d("clicked on date:", selectedWorkDayDate.toString());
        });

        calendarView.setSelected(true);
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(System.currentTimeMillis());
        try {
            calendarView.setDate(now);
        } catch (OutOfDateRangeException e) {
            throw new RuntimeException(e);
        }

        view.findViewById(R.id.btn_timesheet_calendar_save).setEnabled(true);
        view.findViewById(R.id.btn_timesheet_calendar_save).setOnClickListener(v -> {
            //   view.findViewById(R.id.btn_timesheet_calendar_save).setBackgroundColor(getResources().getColor(R.color.color_btn_bg_cancel, view.getContext().getTheme()));
            v.setEnabled(false);
            handleButtonSaveClick(calendarView);
            v.setEnabled(true);
        });


        view.findViewById(R.id.btn_timesheet_calendar_cancel).setOnClickListener(v -> {
            view.findViewById(R.id.btn_timesheet_calendar_cancel).setBackgroundColor(getResources().getColor(R.color.color_btn_bg_cancel, view.getContext().getTheme()));
            // Simply return back to credential list
            NavigationView navigationView = requireActivity().findViewById(R.id.navigationView);
            requireActivity().onOptionsItemSelected(navigationView.getMenu().findItem(R.id.nav_timesheet_list));
            returnToTimesheetEntryList(getArguments().getLong(TimesheetFragment.TIMESHEET_ID_KEY));
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
        String timesheetJson = getArguments() != null ? getArguments().getString(TimesheetEntryListFragment.TIMESHEET_ENTRY_JSON_KEY) : null;
        Log.d("receives timesheet", "" + timesheetJson);
        if (timesheetJson != null && !timesheetJson.isEmpty()) {
            try {
                TimesheetEntry timesheetEntry = Utility.gsonMapper().fromJson(timesheetJson, TimesheetEntry.class);
                Log.d(Utility.buildTag(getClass(), "onFragmentResult"), String.format("timesheet: %s", timesheetEntry));
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

    private List<Calendar> createSelectedDates(Long timesheetId) {
        List<TimesheetEntry> timesheetEntryList = timesheetService.getTimesheetEntryList(timesheetId);
        List<Calendar> selectedDates = new ArrayList<>();
        timesheetEntryList.forEach(t -> {
            Calendar cal = Calendar.getInstance();
            cal.set(t.getWorkdayDate().getYear(), t.getWorkdayDate().getMonth().getValue() - 1, t.getWorkdayDate().getDayOfMonth());
            selectedDates.add(cal);
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
        bundle.putLong(TimesheetFragment.TIMESHEET_ID_KEY, timesheetId);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, TimesheetEntryListFragment.class, bundle)
                .setReorderingAllowed(true)
                .commit();
    }

    /**
     * Check that selected workday date is not before or after the date range of the timesheet.
     */
    private boolean isWorkdayDateValid() {
      return false; //selectedWorkDayDate.isBefore() || selectedWorkDayDate.isAfter();
    };

    /**
     * When button save is click and new timesheet entry event is sent in order to insert it into the database
     */
    private void handleButtonSaveClick(CalendarView calendarView) {
        timesheetService.saveTimesheetEntry(getTimesheetEntry());
        showSnackbar(String.format(getResources().getString(R.string.info_timesheet_list_add_msg_format), selectedWorkDayDate), R.color.color_snackbar_text_add);
    }

    private void showSnackbar(String msg, @ColorRes int bgColor) {
        Resources.Theme theme = getResources().newTheme();
        Snackbar snackbar = Snackbar.make(requireView().findViewById(R.id.timesheet_calendar_layout), msg, BaseTransientBottomBar.LENGTH_LONG);
        snackbar.setTextColor(getResources().getColor(bgColor, theme));
        snackbar.show();
    }
}
