package com.gunnarro.android.terex.ui.fragment;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListPopupWindow;

import com.applandeo.materialcalendarview.CalendarDay;
import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.google.android.material.textfield.TextInputEditText;
import com.gunnarro.android.terex.R;
import com.gunnarro.android.terex.domain.dto.ProjectDto;
import com.gunnarro.android.terex.domain.dto.SpinnerItem;
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
import java.util.stream.Collectors;

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
        timesheetService = new TimesheetService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        requireActivity().setTitle(R.string.title_timesheet_calendar);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_timesheet_entry_custom_calendar, container, false);
        CalendarView calendarView = view.findViewById(R.id.view_timesheet_custom_calendar);
        Long timesheetId = getArguments().getLong(TimesheetListFragment.TIMESHEET_ID_KEY);
        // calendarView.setSelectedDates(selectedDates);
        //@Deprecated("Use setCalendarDays(List<CalendarDay>) with isEnabled = false")
        calendarView.setDisabledDays(createSelectedCalendarDates(timesheetId));
        //calendarView.setCalendarDays(createSelectedDates(timesheetId));
        //calendarView.setSelectedDates(createSelectedCalendarDates(timesheetId));
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

        // create client projects spinner
        AutoCompleteTextView projectSpinner = view.findViewById(R.id.timesheet_calendar_project_spinner);
        List<ProjectDto> projectDtoList = timesheetService.getTimesheetDto(timesheetId).getClientDto().getProjectList();
        List<SpinnerItem> projectItems = projectDtoList.stream().map(p -> new SpinnerItem(p.getId(), p.getName())).collect(Collectors.toList());
        ArrayAdapter<SpinnerItem> projectAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, projectItems);
        projectSpinner.setAdapter(projectAdapter);
        projectSpinner.setListSelection(0);
        projectSpinner.setText(projectSpinner.getAdapter().getItem(0).toString());
        projectSpinner.setInputMethodMode(ListPopupWindow.INPUT_METHOD_NOT_NEEDED);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            projectSpinner.setAutoHandwritingEnabled(false);
            projectSpinner.setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO);
        }


        view.findViewById(R.id.btn_timesheet_calendar_save_regular).setEnabled(true);
        view.findViewById(R.id.btn_timesheet_calendar_save_regular).setOnClickListener(v -> {
            v.setEnabled(false);
            addTimesheetEntry(TimesheetEntry.TimesheetEntryTypeEnum.REGULAR);
            v.setEnabled(true);
        });

        view.findViewById(R.id.btn_timesheet_calendar_save_vacation).setEnabled(true);
        view.findViewById(R.id.btn_timesheet_calendar_save_vacation).setOnClickListener(v -> {
            v.setEnabled(false);
            addTimesheetEntry(TimesheetEntry.TimesheetEntryTypeEnum.VACATION);
            v.setEnabled(true);
        });

        view.findViewById(R.id.btn_timesheet_calendar_save_sick).setEnabled(true);
        view.findViewById(R.id.btn_timesheet_calendar_save_sick).setOnClickListener(v -> {
            v.setEnabled(false);
            addTimesheetEntry(TimesheetEntry.TimesheetEntryTypeEnum.SICK);
            v.setEnabled(true);
        });

        view.findViewById(R.id.btn_timesheet_calendar_cancel).setOnClickListener(v -> {
            view.findViewById(R.id.btn_timesheet_calendar_cancel).setBackgroundColor(getResources().getColor(R.color.color_btn_bg_default, view.getContext().getTheme()));
            // Simply return back to the timesheet list
            Bundle bundle = new Bundle();
            bundle.putLong(TimesheetListFragment.TIMESHEET_ID_KEY, timesheetId);
            navigateTo(R.id.nav_from_timesheet_entry_calendar_to_timesheet_entry_list, bundle);
        });

        updateView(view, readTimesheetEntryFromBundle());
        Log.d(Utility.buildTag(getClass(), "onCreateView"), "");
        return view;
    }

    private TimesheetEntry readTimesheetEntryFromBundle() {
        String timesheetEntryJson = getArguments() != null ? getArguments().getString(TimesheetEntryListFragment.TIMESHEET_ENTRY_JSON_KEY) : null;
        Log.d("receives timesheet", String.format("%s", timesheetEntryJson));
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
            calendarDay.setSelectedBackgroundResource(getResources().getColor(R.color.color_btn_bg_default, null));
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
            eventDays.add(new EventDay(cal, R.drawable.timesheet_day_ok_24, getResources().getColor(R.color.color_btn_bg_default, null)));
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

    private List<Calendar> createSelectedCalendarDates(Long timesheetId) {
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

    private void updateView(View view, @NotNull TimesheetEntry timesheetEntry) {
        ((EditText) view.findViewById(R.id.timesheet_calendar_worked_hours)).setText(Utility.fromSecondsToHours(timesheetEntry.getWorkedSeconds()));
    }

    private TimesheetEntry getTimesheetEntry(TimesheetEntry.TimesheetEntryTypeEnum type) {
        TimesheetEntry timesheetEntry = readTimesheetEntryFromBundle();
        // need only update the work day date because we all other data is read from the last added timesheet.
        timesheetEntry.setType(type.name());
        timesheetEntry.setWorkdayDate(selectedWorkDayDate);
        AutoCompleteTextView projectSpinner = requireView().findViewById(R.id.timesheet_calendar_project_spinner);
        // some trouble to get the project id, so this mey be to complicated
        int count = projectSpinner.getAdapter().getCount();
        for (int i = 0; i < count; i++) {
            SpinnerItem item = (SpinnerItem) projectSpinner.getAdapter().getItem(i);
            if (item.name().equals(projectSpinner.getText().toString())) {
                timesheetEntry.setProjectId(item.id());
            }
        }

        TextInputEditText workedHoursInput =  requireView().findViewById(R.id.timesheet_calendar_worked_hours);
        timesheetEntry.setWorkedSeconds(Utility.fromHoursToSeconds(workedHoursInput.getText().toString()));

        if (timesheetEntry.isVacationDay() || timesheetEntry.isSickDay()) {
            // set worked time to 0.
            timesheetEntry.setStartTime(null);
            timesheetEntry.setWorkedSeconds(null);
            timesheetEntry.setBreakSeconds(null);
        }
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
    private void addTimesheetEntry(TimesheetEntry.TimesheetEntryTypeEnum type) {
        if (selectedWorkDayDate == null) {
            showInfoDialog("Info", "You must select a workday date!");
            return;
        }
        try {
            TimesheetEntry timesheetEntry = getTimesheetEntry(type);
            timesheetService.saveTimesheetEntry(timesheetEntry);
            showSnackbar(String.format("Added %s %s %s %s", timesheetEntry.getProjectId(), timesheetEntry.getWorkdayDate(), timesheetEntry.getType(), timesheetEntry.getWorkedHours()), R.color.color_snackbar_text_add);
        } catch (TerexApplicationException | InputValidationException e) {
            Log.e("handleButtonSaveClick", e.getMessage());
            showInfoDialog("Error", e.getMessage());
        }
    }
}
