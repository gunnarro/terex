package com.gunnarro.android.terex.ui.fragment;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.gunnarro.android.terex.R;
import com.gunnarro.android.terex.domain.entity.TimesheetEntry;
import com.gunnarro.android.terex.exception.TerexApplicationException;
import com.gunnarro.android.terex.utility.Utility;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;


@AndroidEntryPoint
public class TimesheetEntryAddFragment extends Fragment implements View.OnClickListener {

    @Inject
    public TimesheetEntryAddFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requireActivity().setTitle(R.string.title_register_work);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_timesheet_entry_add, container, false);
        // create status spinner
        final AutoCompleteTextView statusSpinner = view.findViewById(R.id.timesheet_entry_status_spinner);
        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.timesheet_statuses, android.R.layout.simple_spinner_item);
        statusSpinner.setAdapter(statusAdapter);
        statusSpinner.setListSelection(0);

        // workday date picker
        TextInputEditText workdayDate = view.findViewById(R.id.timesheet_entry_workday_date);
        // turn off keyboard popup when clicked
        workdayDate.setShowSoftInputOnFocus(false);
        workdayDate.setOnClickListener(v -> {
            DatePickerDialog workdayDatePicker = new DatePickerDialog(
                    requireContext(),
                    R.style.DialogTheme,
                    (view1, year, monthOfYear, dayOfMonth) -> workdayDate.setText(Utility.formatToDDMMYYYY(year, monthOfYear, dayOfMonth)),
                    LocalDate.now().getYear(),
                    LocalDate.now().getMonthValue(),
                    LocalDate.now().getDayOfMonth());
            workdayDatePicker.setTitle(getResources().getString(R.string.title_select_workday_date));
            workdayDatePicker.show();
        });

        // from time time picker
        EditText fromTime = view.findViewById(R.id.timesheet_entry_from_time);
        // turn off keyboard popup when clicked
        fromTime.setShowSoftInputOnFocus(false);
        fromTime.setOnClickListener(v -> {
            TimePickerDialog fromTimePicker = new TimePickerDialog(
                    requireContext(),
                    R.style.DialogTheme,
                    (timePicker, selectedHour, selectedMinute) -> {
                        fromTime.setText(Utility.formatToHHMM(selectedHour, selectedMinute));
                    },
                    LocalTime.now().getHour(),
                    LocalTime.now().getMinute(),
                    true);
            fromTimePicker.setTitle(getResources().getString(R.string.title_select_from_time));
            fromTimePicker.show();
        });

        // to time time picker
        EditText toTime = view.findViewById(R.id.timesheet_entry_to_time);
        // turn off keyboard popup when clicked
        toTime.setShowSoftInputOnFocus(false);
        toTime.setOnClickListener(v -> {
            TimePickerDialog toTimePicker = new TimePickerDialog(
                    requireContext(),
                    R.style.DialogTheme,
                    (timePicker, selectedHour, selectedMinute) -> {
                        toTime.setText(Utility.formatToHHMM(selectedHour, selectedMinute));
                    },
                    LocalTime.now().getHour(),
                    LocalTime.now().getMinute(),
                    true);
            toTimePicker.setTitle(getResources().getString(R.string.title_select_to_time));
            toTimePicker.show();
        });

        // disable save button as default
        view.findViewById(R.id.btn_timesheet_entry_save).setEnabled(true);
        view.findViewById(R.id.btn_timesheet_entry_save).setOnClickListener(v -> {
            view.findViewById(R.id.btn_timesheet_entry_save).setBackgroundColor(getResources().getColor(R.color.color_btn_bg_cancel, view.getContext().getTheme()));
            Bundle result = new Bundle();
            result.putString(TimesheetEntryListFragment.TIMESHEET_ENTRY_JSON_INTENT_KEY, getTimesheetAsJson());
            result.putString(TimesheetEntryListFragment.TIMESHEET_ENTRY_ACTION_KEY, TimesheetEntryListFragment.TIMESHEET_ENTRY_ACTION_SAVE);
            getParentFragmentManager().setFragmentResult(TimesheetEntryListFragment.TIMESHEET_ENTRY_REQUEST_KEY, result);
            Log.d(Utility.buildTag(getClass(), "onCreateView"), "add new timesheet entry intent: " + getTimesheetAsJson());
            returnToTimesheetList();
        });

        view.findViewById(R.id.btn_timesheet_entry_delete).setOnClickListener(v -> {
            view.findViewById(R.id.btn_timesheet_entry_delete).setBackgroundColor(getResources().getColor(R.color.color_btn_bg_cancel, view.getContext().getTheme()));
            Bundle result = new Bundle();
            result.putString(TimesheetEntryListFragment.TIMESHEET_ENTRY_JSON_INTENT_KEY, getTimesheetAsJson());
            result.putString(TimesheetEntryListFragment.TIMESHEET_ENTRY_ACTION_KEY, TimesheetEntryListFragment.TIMESHEET_ENTRY_ACTION_DELETE);
            getParentFragmentManager().setFragmentResult(TimesheetEntryListFragment.TIMESHEET_ENTRY_REQUEST_KEY, result);
            Log.d(Utility.buildTag(getClass(), "onCreateView"), "add new delete item intent");
            returnToTimesheetList();
        });

        view.findViewById(R.id.btn_timesheet_entry_cancel).setOnClickListener(v -> {
            view.findViewById(R.id.btn_timesheet_entry_cancel).setBackgroundColor(getResources().getColor(R.color.color_btn_bg_cancel, view.getContext().getTheme()));
            // Simply return back to credential list
            NavigationView navigationView = requireActivity().findViewById(R.id.navigationView);
            requireActivity().onOptionsItemSelected(navigationView.getMenu().findItem(R.id.nav_timesheet_list));
            returnToTimesheetList();
        });

        updateTimesheetAddView(view, readTimesheetEntryFromBundle());
        return view;
    }

    private TimesheetEntry readTimesheetEntryFromBundle() {
        String timesheetJson = getArguments() != null ? getArguments().getString(TimesheetEntryListFragment.TIMESHEET_ENTRY_JSON_INTENT_KEY) : null;
        Log.d("receives timesheet", "" + timesheetJson);
        if (timesheetJson != null && !timesheetJson.isEmpty()) {
            try {
                TimesheetEntry timesheetEntry = Utility.gsonMapper().fromJson(timesheetJson, TimesheetEntry.class);
                // set workday date always to current date if not set
                if (timesheetEntry.getWorkdayDate() == null) {
                    timesheetEntry.setWorkdayDate(LocalDate.now());
                }
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

    private void returnToTimesheetList() {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, TimesheetEntryListFragment.class, null)
                .setReorderingAllowed(true)
                .commit();
    }

    private void updateTimesheetAddView(View view, @NotNull TimesheetEntry timesheetEntry) {
        Log.d("update timesheet add view", timesheetEntry.toString());
        if (timesheetEntry.getTimesheetId() == null) {
            throw new TerexApplicationException("timesheetId is null!", "timesheetId is null!", null);
        }

        TextView timesheetId = view.findViewById(R.id.timesheet_entry_timesheet_id);
        timesheetId.setText(String.valueOf(timesheetEntry.getTimesheetId()));

        TextView id = view.findViewById(R.id.timesheet_entry_id);
        id.setText(String.valueOf(timesheetEntry.getId()));

        EditText createdDateView = view.findViewById(R.id.timesheet_entry_created_date);
        createdDateView.setText(Utility.formatDateTime(timesheetEntry.getCreatedDate()));

        EditText lastModifiedDateView = view.findViewById(R.id.timesheet_entry_last_modified_date);
        lastModifiedDateView.setText(Utility.formatDateTime(timesheetEntry.getLastModifiedDate()));

        EditText timesheetNameView = view.findViewById(R.id.timesheet_entry_timesheet_name);
        timesheetNameView.setText(timesheetEntry.getTimesheetId().toString());

        AutoCompleteTextView statusSpinner = view.findViewById(R.id.timesheet_entry_status_spinner);
        statusSpinner.setText(timesheetEntry.getStatus());

        EditText hourlyRateView = view.findViewById(R.id.timesheet_entry_hourly_rate);
        hourlyRateView.setText(String.format("%s", timesheetEntry.getHourlyRate()));

        EditText workdayDateView = view.findViewById(R.id.timesheet_entry_workday_date);
        workdayDateView.setText(Utility.formatDate(timesheetEntry.getWorkdayDate()));

        EditText fromTimeView = view.findViewById(R.id.timesheet_entry_from_time);
        fromTimeView.setText(Utility.formatTime(timesheetEntry.getFromTime()));

        EditText toTimeView = view.findViewById(R.id.timesheet_entry_to_time);
        toTimeView.setText(Utility.formatTime(timesheetEntry.getToTime()));

        EditText breakView = view.findViewById(R.id.timesheet_entry_break);
        breakView.setText(String.format("%s", timesheetEntry.getBreakInMin()));

        //TextView workedHoursView = view.findViewById(R.id.timesheet_worked_hours);
        //workedHoursView.setText(String.format("%s", "0"));

        EditText commentView = view.findViewById(R.id.timesheet_entry_comment);
        commentView.setText(timesheetEntry.getComment());

        // hide fields if this is a new
        if (timesheetEntry.getId() == null) {
            view.findViewById(R.id.timesheet_entry_date_layout).setVisibility(View.GONE);
            view.findViewById(R.id.btn_timesheet_entry_delete).setVisibility(View.GONE);
        } else {
            // change button icon to from add new to save
            ((MaterialButton) view.findViewById(R.id.btn_timesheet_entry_save)).setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_check_black_24dp));
        }
        Log.d(Utility.buildTag(getClass(), "updateTimesheetAddView"), String.format("updated %s ", timesheetEntry));
    }

    @Override
    public void onClick(View view) {
        // ask every time
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_MEDIA_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // You have not been granted access, ask for permission now, no need for nay permissions
            Log.d(Utility.buildTag(getClass(), "onClick"), "save button, permission not granted");
        }

        int id = view.getId();
        if (id == R.id.btn_timesheet_entry_save) {
            Log.d(Utility.buildTag(getClass(), "onClick"), "save button, save entry");
        } else if (id == R.id.btn_timesheet_entry_cancel) {
            // return back to main view
            Log.d(Utility.buildTag(getClass(), "onClick"), "cancel button, return back to timesheet list view");
        }
    }

    private String getTimesheetAsJson() {
        TimesheetEntry timesheet = new TimesheetEntry();

        TextView timesheetIdView = requireView().findViewById(R.id.timesheet_entry_timesheet_id);
        timesheet.setTimesheetId(Utility.isInteger(timesheetIdView.getText().toString()) ? Long.parseLong(timesheetIdView.getText().toString()) : null);

        TextView idView = requireView().findViewById(R.id.timesheet_entry_id);
        timesheet.setId(Utility.isInteger(idView.getText().toString()) ? Long.parseLong(idView.getText().toString()) : null);

        EditText createdDateView = requireView().findViewById(R.id.timesheet_entry_created_date);
        LocalDateTime createdDateTime = Utility.toLocalDateTime(createdDateView.getText().toString());
        if (createdDateTime != null) {
            timesheet.setCreatedDate(createdDateTime);
        }

        EditText lastModifiedDateView = requireView().findViewById(R.id.timesheet_entry_last_modified_date);
        LocalDateTime lastModifiedDateTime = Utility.toLocalDateTime(lastModifiedDateView.getText().toString());
        if (lastModifiedDateTime != null) {
            timesheet.setLastModifiedDate(lastModifiedDateTime);
        }

        AutoCompleteTextView timesheetSpinner = requireView().findViewById(R.id.timesheet_entry_timesheet_spinner);
        //timesheet.setStatus(timesheetSpinner.getText().toString());

        AutoCompleteTextView statusSpinner = requireView().findViewById(R.id.timesheet_entry_status_spinner);
        timesheet.setStatus(statusSpinner.getText().toString());

        TextView hourlyRateView = requireView().findViewById(R.id.timesheet_entry_hourly_rate);
        timesheet.setHourlyRate(Integer.parseInt(hourlyRateView.getText().toString()));

        TextView workdayDateView = requireView().findViewById(R.id.timesheet_entry_workday_date);
        timesheet.setWorkdayDate(Utility.toLocalDate(workdayDateView.getText().toString()));

        TextView fromTimeView = requireView().findViewById(R.id.timesheet_entry_from_time);
        timesheet.setFromTime(Utility.toLocalTime(fromTimeView.getText().toString()));

        TextView toTimeView = requireView().findViewById(R.id.timesheet_entry_to_time);
        timesheet.setToTime(Utility.toLocalTime(toTimeView.getText().toString()));

        TextView breakView = requireView().findViewById(R.id.timesheet_entry_break);
        timesheet.setBreakInMin(Integer.parseInt(breakView.getText().toString()));

        // TextView workedHoursView = requireView().findViewById(R.id.timesheet_worked_hours);
        // timesheet.setWorkedMinutes((int) (Double.parseDouble(workedHoursView.getText().toString())*60));

        TextView commentView = requireView().findViewById(R.id.timesheet_entry_comment);
        timesheet.setComment(commentView.getText().toString());
        // determine and set worked minutes
        timesheet.setWorkedMinutes(Long.valueOf(ChronoUnit.MINUTES.between(timesheet.getFromTime(), timesheet.getToTime())).intValue());
        try {
            return Utility.gsonMapper().toJson(timesheet);
        } catch (Exception e) {
            Log.e("getTimesheetAsJson", e.toString());
            throw new RuntimeException("unable to parse object to json! " + e);
        }
    }

    /**
     *
     */
    private TextWatcher createEmptyTextValidator(EditText editText, String regexp, String validationErrorMsg) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!editText.getText().toString().matches(regexp)) {
                    editText.setError(validationErrorMsg);
                }
            }
        };
    }
}
