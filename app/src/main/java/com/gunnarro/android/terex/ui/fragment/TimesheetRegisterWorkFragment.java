package com.gunnarro.android.terex.ui.fragment;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;
import com.gunnarro.android.terex.R;
import com.gunnarro.android.terex.domain.entity.RegisterWork;
import com.gunnarro.android.terex.domain.entity.Timesheet;
import com.gunnarro.android.terex.utility.Utility;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;


@AndroidEntryPoint
public class TimesheetRegisterWorkFragment extends Fragment implements View.OnClickListener {

    @Inject
    public TimesheetRegisterWorkFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Log.d(Utility.buildTag(getClass(), "onCreate"), "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_timesheet_register_work, container, false);
        EditText workdayDate = view.findViewById(R.id.timesheet_workday_date);
        // turn off keyboard popup when clicked
        workdayDate.setShowSoftInputOnFocus(false);
        workdayDate.setOnClickListener(v -> {
            DatePickerDialog workdayDatePicker = new DatePickerDialog(
                    getContext(),
                    R.style.DialogTheme,
                    (view1, year, monthOfYear, dayOfMonth) -> workdayDate.setText(Utility.formatToDDMMYYYY(year, monthOfYear, dayOfMonth)),
                    LocalDate.now().getYear(),
                    LocalDate.now().getMonthValue(),
                    LocalDate.now().getDayOfMonth());
            workdayDatePicker.setTitle("Select workday date");
            workdayDatePicker.show();
        });

        EditText fromTime = view.findViewById(R.id.timesheet_from_time);
        // turn off keyboard popup when clicked
        fromTime.setShowSoftInputOnFocus(false);
        fromTime.setOnClickListener(v -> {
            TimePickerDialog fromTimePicker = new TimePickerDialog(
                    getContext(),
                    R.style.DialogTheme,
                    (timePicker, selectedHour, selectedMinute) -> { fromTime.setText(Utility.formatToHHMM(selectedHour, selectedMinute)); updateWorkedHours();},
                    LocalTime.now().getHour(),
                    LocalTime.now().getMinute(),
                    true);
            fromTimePicker.setTitle("Select worked from time");
            fromTimePicker.show();
        });

        EditText toTime = view.findViewById(R.id.timesheet_to_time);
        // turn off keyboard popup when clicked
        toTime.setShowSoftInputOnFocus(false);
        toTime.setOnClickListener(v -> {
            TimePickerDialog toTimePicker = new TimePickerDialog(
                    getContext(),
                    R.style.DialogTheme,
                    (timePicker, selectedHour, selectedMinute) -> { toTime.setText(Utility.formatToHHMM(selectedHour, selectedMinute)); updateWorkedHours();},
                    LocalTime.now().getHour(),
                    LocalTime.now().getMinute(),
                    true);
            toTimePicker.setTitle("Select worked to time");
            toTimePicker.show();
        });

        view.findViewById(R.id.btn_timesheet_register_save).setOnClickListener(v -> {
            Bundle result = new Bundle();
            result.putString(TimesheetListFragment.TIMESHEET_JSON_INTENT_KEY, getTimesheetAsJson());
            result.putString(TimesheetListFragment.TIMESHEET_ACTION_KEY, TimesheetListFragment.TIMESHEET_ACTION_SAVE);
            getParentFragmentManager().setFragmentResult(TimesheetListFragment.TIMESHEET_REQUEST_KEY, result);
            Log.d(Utility.buildTag(getClass(), "onCreateView"), "add new item intent");
            returnToTimesheetList();
        });

        view.findViewById(R.id.btn_timesheet_register_delete).setOnClickListener(v -> {
            Bundle result = new Bundle();
            result.putString(TimesheetListFragment.TIMESHEET_JSON_INTENT_KEY, getTimesheetAsJson());
            result.putString(TimesheetListFragment.TIMESHEET_ACTION_KEY, TimesheetListFragment.TIMESHEET_ACTION_DELETE);
            getParentFragmentManager().setFragmentResult(TimesheetListFragment.TIMESHEET_REQUEST_KEY, result);
            Log.d(Utility.buildTag(getClass(), "onCreateView"), "add new item intent");
            returnToTimesheetList();
        });

        view.findViewById(R.id.btn_timesheet_register_cancel).setOnClickListener(v -> {
            // Simply return back to timesheet list
            NavigationView navigationView  = requireActivity().findViewById(R.id.navigationView);
            requireActivity().onOptionsItemSelected(navigationView.getMenu().findItem(R.id.nav_timesheet_list));
            returnToTimesheetList();
        });

        Spinner statusSpinner = view.findViewById(R.id.timesheet_statuses_spinner);
        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(getContext(), R.array.timesheet_statuses, android.R.layout.simple_spinner_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(statusAdapter);

        Spinner clientSpinner = view.findViewById(R.id.timesheet_client_spinner);
        String[] clients = new String[]{"Technogarden", "IT-Verket"};
        ArrayAdapter<String> clientAdapter = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_spinner_item, clients);
        clientAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        clientSpinner.setAdapter(clientAdapter);

        Spinner projectSpinner = view.findViewById(R.id.timesheet_project_spinner);
        String[] projects = new String[]{"MasterCard"};
        ArrayAdapter projectAdapter = new ArrayAdapter(this.getContext(), android.R.layout.simple_spinner_item, projects);
        projectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        projectSpinner.setAdapter(projectAdapter);
        Log.d(Utility.buildTag(getClass(), "onCreateView"), "");
        return view;
    }

    private void returnToTimesheetList() {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, TimesheetListFragment.class, null)
                .setReorderingAllowed(true)
                .commit();
    }

    /**
     * Update backup info after view is successfully create
     */
    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateRegisterWorkView(requireView(), RegisterWork.buildDefault("MasterCard"));
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void updateRegisterWorkView(View view, RegisterWork registerWork) {
        TextView id = requireView().findViewById(R.id.timesheet_entity_id);
        id.setText(null);

        Spinner clientSpinner = view.findViewById(R.id.timesheet_client_spinner);
        clientSpinner.setSelection(1);

        Spinner projectSpinner = view.findViewById(R.id.timesheet_project_spinner);
        projectSpinner.setSelection(0);

        TextView hourlyRateView = view.findViewById(R.id.timesheet_hourly_rate);
        hourlyRateView.setText(String.format("%s", registerWork.getHourlyRate()));

        TextView workdayDateView = view.findViewById(R.id.timesheet_workday_date);
        workdayDateView.setText(Utility.formatDate(registerWork.getWorkdayDate()));

        EditText fromTimeView = view.findViewById(R.id.timesheet_from_time);
        fromTimeView.setText(Utility.formatTime(registerWork.getFromTime()));

        EditText toTimeView = view.findViewById(R.id.timesheet_to_time);
        toTimeView.setText(Utility.formatTime(registerWork.getToTime()));

        TextView breakView = view.findViewById(R.id.timesheet_break);
        breakView.setText(String.format("%s", registerWork.getBreakInMin()));

        Spinner statusSpinner = view.findViewById(R.id.timesheet_statuses_spinner);
        statusSpinner.setSelection(0);

        TextView workedHoursView = view.findViewById(R.id.timesheet_worked_hours);
        workedHoursView.setText(String.format("%s", registerWork.getWorkedHours()));

        TextView commentView = view.findViewById(R.id.timesheet_comment);
        commentView.setText(registerWork.getComment());

        Log.d(Utility.buildTag(getClass(), "saveRegisterWork"), String.format("updated %s ", registerWork));
    }

    @Override
    public void onClick(View view) {
        // ask every time
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_MEDIA_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // You have not been granted access, ask for permission now.
            //requestPermissionLauncher.launch(Manifest.permission.ACCESS_MEDIA_LOCATION);
        } else {
            Log.d(Utility.buildTag(getClass(), "onClick"), "save button, permission granted");
        }

        int id = view.getId();
        if (id == R.id.btn_timesheet_register_save) {
            Log.d(Utility.buildTag(getClass(), "onClick"), "save button, save entry: ");
        } else if (id == R.id.btn_timesheet_register_cancel) {
            // return back to main view
        }
    }

    private void updateWorkedHours() {
        EditText fromTime = requireView().findViewById(R.id.timesheet_from_time);
        EditText toTime = requireView().findViewById(R.id.timesheet_to_time);
        TextView workedHoursView = requireView().findViewById(R.id.timesheet_worked_hours);
        workedHoursView.setText(Utility.getDateDiffInHours(LocalTime.parse(fromTime.getText()), LocalTime.parse(toTime.getText())));
    }

    private String getTimesheetAsJson() {
        Timesheet timesheet = new Timesheet();

        TextView idView = requireView().findViewById(R.id.timesheet_entity_id);
        timesheet.setId(Utility.isInteger(idView.getText().toString()) ? Long.parseLong(idView.getText().toString()) : null);

        Spinner clientSpinner = requireView().findViewById(R.id.timesheet_client_spinner);
        timesheet.setClientName(clientSpinner.getSelectedItem().toString());

        Spinner projectSpinner = requireView().findViewById(R.id.timesheet_project_spinner);
        timesheet.setProjectName(projectSpinner.getSelectedItem().toString());

        TextView hourlyRateView = requireView().findViewById(R.id.timesheet_hourly_rate);
        timesheet.setHourlyRate(Integer.parseInt(hourlyRateView.getText().toString()));

        TextView workdayDateView = requireView().findViewById(R.id.timesheet_workday_date);
        timesheet.setWorkdayDate(Utility.toLocalDate(workdayDateView.getText().toString()));

        TextView fromTimeView = requireView().findViewById(R.id.timesheet_from_time);
        timesheet.setFromTime(Utility.toLocalTime(fromTimeView.getText().toString()));

        TextView toTimeView = requireView().findViewById(R.id.timesheet_to_time);
        timesheet.setToTime(Utility.toLocalTime(toTimeView.getText().toString()));

        TextView breakView = requireView().findViewById(R.id.timesheet_break);
        timesheet.setBreakInMin(Integer.parseInt(breakView.getText().toString()));

        Spinner statusSpinner = requireView().findViewById(R.id.timesheet_statuses_spinner);
        timesheet.setStatus(statusSpinner.getSelectedItem().toString());

        TextView workedHoursView = requireView().findViewById(R.id.timesheet_worked_hours);
        timesheet.setWorkedMinutes((int) (Double.parseDouble(workedHoursView.getText().toString())*60));

        TextView commentView = requireView().findViewById(R.id.timesheet_comment);
        timesheet.setComment(commentView.getText().toString());

        try {
            return Utility.gsonMapper().toJson(timesheet);
        } catch (Exception e) {
            Log.e("getTimesheetAsJson", e.toString());
            throw new RuntimeException("unable to parse object to json! " + e);
        }
    }
}
