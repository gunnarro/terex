package com.gunnarro.android.terex.ui.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;
import com.gunnarro.android.terex.R;
import com.gunnarro.android.terex.domain.entity.Timesheet;
import com.gunnarro.android.terex.repository.TimesheetRepository;
import com.gunnarro.android.terex.utility.Utility;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.Calendar;

import javax.inject.Inject;

public class TimesheetCalendarFragment extends Fragment {

    private Timesheet lastAddedTimesheet;
    private LocalDate selectedLocalDate;

    private TimesheetRepository timesheetRepository;

    @Inject
    public TimesheetCalendarFragment() {
        Log.d("TimesheetCalendarFragment", "");
        // repository = new TimesheetRepository(getContext());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        timesheetRepository = new TimesheetRepository(getContext());
        lastAddedTimesheet = timesheetRepository.getMostRecent();
        Log.d(Utility.buildTag(getClass(), "onCreate"), "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_timesheet_calendar, container, false);
        CalendarView calendarView = view.findViewById(R.id.view_timesheet_calendar);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                selectedLocalDate = LocalDate.of(year, month + 1, dayOfMonth);
                Log.d(Utility.buildTag(getClass(), "onSelectedDayChange"), "" + selectedLocalDate);
            }
        });

        calendarView.setOnClickListener(new CalendarView.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(Utility.buildTag(getClass(), "OnClickListener"), "" + view.isClickable());
            }
        });

        calendarView.setDate(Calendar.getInstance().getTimeInMillis());
        calendarView.setSelected(true);
        calendarView.setLayoutMode(1);
        calendarView.setFirstDayOfWeek(1);
        calendarView.setShowWeekNumber(true);

        view.findViewById(R.id.btn_timesheet_calendar_save).setEnabled(true);
        view.findViewById(R.id.btn_timesheet_calendar_save).setOnClickListener(v -> {
            view.findViewById(R.id.btn_timesheet_calendar_save).setBackgroundColor(getResources().getColor(R.color.color_btn_bg_cancel, view.getContext().getTheme()));

            String timesheetJsonStr = getTimesheetAsJson();
            if (timesheetJsonStr == null) {
                showInfoDialog("You are directed back to register timesheet page, because there are no timesheet entry to copy! The must at least exist one timesheet entry before you can use the timesheet calendar view.", getContext());
                goToAddTimesheet();
            }
            Bundle result = new Bundle();
            result.putString(TimesheetListFragment.TIMESHEET_JSON_INTENT_KEY, timesheetJsonStr);
            result.putString(TimesheetListFragment.TIMESHEET_ACTION_KEY, TimesheetListFragment.TIMESHEET_ACTION_SAVE);
            getParentFragmentManager().setFragmentResult(TimesheetListFragment.TIMESHEET_REQUEST_KEY, result);
            Log.d(Utility.buildTag(getClass(), "onCreateView"), "add new add item intent: " + getTimesheetAsJson());
        });

        view.findViewById(R.id.btn_timesheet_calendar_cancel).setOnClickListener(v -> {
            view.findViewById(R.id.btn_timesheet_calendar_cancel).setBackgroundColor(getResources().getColor(R.color.color_btn_bg_cancel, view.getContext().getTheme()));
            // Simply return back to credential list
            NavigationView navigationView = requireActivity().findViewById(R.id.navigationView);
            requireActivity().onOptionsItemSelected(navigationView.getMenu().findItem(R.id.nav_timesheet_list));
            returnToTimesheetList();
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

    private String getTimesheetAsJson() {
        if (lastAddedTimesheet == null) {
            return null;
        }
        // need only update the work day date because we all other data is read from the last added timesheet.
        lastAddedTimesheet.setWorkdayDate(selectedLocalDate);
        try {
            return Utility.gsonMapper().toJson(lastAddedTimesheet);
        } catch (Exception e) {
            Log.e("getTimesheetAsJson", e.toString());
            throw new RuntimeException("unable to parse object to json! " + e);
        }
    }

    private void returnToTimesheetList() {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, TimesheetListFragment.class, null)
                .setReorderingAllowed(true)
                .commit();
    }

    private void goToAddTimesheet() {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, TimesheetAddFragment.class, null)
                .setReorderingAllowed(true)
                .commit();
    }

    private void showInfoDialog(String infoMessage, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Info");
        builder.setMessage(infoMessage);
        // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
        builder.setCancelable(false);
        // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
        builder.setPositiveButton("Ok", (dialog, which) -> dialog.cancel());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
