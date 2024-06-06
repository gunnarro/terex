package com.gunnarro.android.terex.ui.fragment;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
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
public class TimesheetEntryAddFragment extends BaseFragment implements View.OnClickListener {

    @Inject
    public TimesheetEntryAddFragment() {
        // Needed by dagger framework
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requireActivity().setTitle(R.string.title_register_work);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_timesheet_entry_add, container, false);
        // workday date picker
        TextInputEditText workdayDay = view.findViewById(R.id.timesheet_entry_workday_day);
        // turn off keyboard popup when clicked
        workdayDay.setShowSoftInputOnFocus(false);
        workdayDay.setOnClickListener(v -> {
            DatePickerDialog workdayDatePicker = new DatePickerDialog(
                    requireContext(),
                    R.style.DialogTheme,
                    (dayView, year, monthOfYear, dayOfMonth) -> workdayDay.setText(dayOfMonth),
                    LocalDate.now().getYear(),
                    LocalDate.now().getMonthValue(),
                    LocalDate.now().getDayOfMonth());

            // FIXME Set dates
//datePickerDialog.getDatePicker().setMaxDate(maxTime);
//datePickerDialog.getDatePicker().setMinDate(minTime);

            workdayDatePicker.setTitle(getResources().getString(R.string.title_select_workday_date));
            workdayDatePicker.getDatePicker().setMinDate(Utility.getFirstDayOfMonth(LocalDate.now()).toEpochDay());
            workdayDatePicker.getDatePicker().setMaxDate(Utility.getLastDayOfMonth(LocalDate.now()).toEpochDay());
            workdayDatePicker.show();
        });

        // timesheet entity type
        MaterialButtonToggleGroup typeBtnGrp = view.findViewById(R.id.timesheet_entry_type_btn_group_layout);
        typeBtnGrp.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
                    MaterialButton button = group.findViewById(checkedId);
                    button.setChecked(isChecked);
                    Log.d("typeBtnGrp.addOnButtonCheckedListener", String.format("checkedButton=%s, isChecked=%s", button.getText(), isChecked));
                }
        );

        // from time time picker
        EditText fromTime = view.findViewById(R.id.timesheet_entry_from_time);
        // turn off keyboard popup when clicked
        fromTime.setShowSoftInputOnFocus(false);
        fromTime.setOnClickListener(v -> {
            TimePickerDialog fromTimePicker = new TimePickerDialog(
                    requireContext(),
                    R.style.DialogTheme,
                    (timePicker, selectedHour, selectedMinute) -> fromTime.setText(Utility.formatToHHMM(selectedHour, selectedMinute)),
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
                    (timePicker, selectedHour, selectedMinute) -> toTime.setText(Utility.formatToHHMM(selectedHour, selectedMinute)),
                    LocalTime.now().getHour(),
                    LocalTime.now().getMinute(),
                    true);
            toTimePicker.setTitle(getResources().getString(R.string.title_select_to_time));
            toTimePicker.show();
        });

        // disable save button as default
        view.findViewById(R.id.timesheet_entry_save_btn).setEnabled(true);
        view.findViewById(R.id.timesheet_entry_save_btn).setOnClickListener(v -> {
            view.findViewById(R.id.timesheet_entry_save_btn).setBackgroundColor(getResources().getColor(R.color.color_btn_bg_cancel, view.getContext().getTheme()));
            Bundle result = new Bundle();
            result.putString(TimesheetEntryListFragment.TIMESHEET_ENTRY_JSON_KEY, getTimesheetEntryAsJson());
            result.putString(TimesheetEntryListFragment.TIMESHEET_ENTRY_ACTION_KEY, TimesheetEntryListFragment.TIMESHEET_ENTRY_ACTION_SAVE);
            getParentFragmentManager().setFragmentResult(TimesheetEntryListFragment.TIMESHEET_ENTRY_REQUEST_KEY, result);
            Log.d(Utility.buildTag(getClass(), "onCreateView"), "add new timesheet entry intent: " + getTimesheetEntryAsJson());
            returnToTimesheetEntryList(getArguments().getLong(TimesheetListFragment.TIMESHEET_ID_KEY));
        });

        view.findViewById(R.id.timesheet_entry_delete_btn).setOnClickListener(v -> {
            view.findViewById(R.id.timesheet_entry_delete_btn).setBackgroundColor(getResources().getColor(R.color.color_btn_bg_cancel, view.getContext().getTheme()));
            Bundle result = new Bundle();
            result.putString(TimesheetEntryListFragment.TIMESHEET_ENTRY_JSON_KEY, getTimesheetEntryAsJson());
            result.putString(TimesheetEntryListFragment.TIMESHEET_ENTRY_ACTION_KEY, TimesheetEntryListFragment.TIMESHEET_ENTRY_ACTION_DELETE);
            getParentFragmentManager().setFragmentResult(TimesheetEntryListFragment.TIMESHEET_ENTRY_REQUEST_KEY, result);
            Log.d(Utility.buildTag(getClass(), "onCreateView"), "add new delete item intent");
            returnToTimesheetEntryList(getArguments().getLong(TimesheetListFragment.TIMESHEET_ID_KEY));
        });

        view.findViewById(R.id.timesheet_entry_cancel_btn).setOnClickListener(v -> {
            view.findViewById(R.id.timesheet_entry_cancel_btn).setBackgroundColor(getResources().getColor(R.color.color_btn_bg_cancel, view.getContext().getTheme()));
            returnToTimesheetEntryList(getArguments().getLong(TimesheetListFragment.TIMESHEET_ID_KEY));
        });

        updateTimesheetEntryAddView(view, readTimesheetEntryFromBundle());
        return view;
    }

    private TimesheetEntry readTimesheetEntryFromBundle() {
        String timesheetEntryJson = getArguments() != null ? getArguments().getString(TimesheetEntryListFragment.TIMESHEET_ENTRY_JSON_KEY) : null;
        Log.d("readTimesheetEntryFromBundle", "received timesheet entry, timesheetEntryJson=" + timesheetEntryJson);
        if (timesheetEntryJson != null && !timesheetEntryJson.isEmpty()) {
            try {
                TimesheetEntry timesheetEntry = Utility.gsonMapper().fromJson(timesheetEntryJson, TimesheetEntry.class);
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

    private void returnToTimesheetEntryList(Long timesheetId) {
        Bundle bundle = new Bundle();
        bundle.putLong(TimesheetListFragment.TIMESHEET_ID_KEY, timesheetId);
        navigateTo(R.id.nav_from_timesheet_entry_details_to_timesheet_entry_list, bundle);
    }

    private void updateTimesheetEntryAddView(View view, @NotNull TimesheetEntry timesheetEntry) {
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
        timesheetNameView.setText("timesheetId: " + timesheetEntry.getTimesheetId());

        MaterialButtonToggleGroup typeBtnGrp = view.findViewById(R.id.timesheet_entry_type_btn_group_layout);
        if (timesheetEntry.isRegularWorkDay()) {
            ((MaterialButton) typeBtnGrp.findViewById(R.id.timesheet_entry_type_regular)).setChecked(true);
        } else if (timesheetEntry.isVacationDay()) {
            ((MaterialButton) typeBtnGrp.findViewById(R.id.timesheet_entry_type_vacation)).setChecked(true);
        } else if (timesheetEntry.isSickDay()) {
            ((MaterialButton) typeBtnGrp.findViewById(R.id.timesheet_entry_type_sick)).setChecked(true);
        }

        EditText workdayYearView = view.findViewById(R.id.timesheet_entry_workday_year);
        workdayYearView.setText(String.format("%s", timesheetEntry.getWorkdayDate().getYear()));

        EditText workdayMonthView = view.findViewById(R.id.timesheet_entry_workday_month);
        workdayMonthView.setText(String.format("%s", timesheetEntry.getWorkdayDate().getMonthValue()));

        EditText workdayDayView = view.findViewById(R.id.timesheet_entry_workday_day);
        workdayDayView.setText(String.format("%s", timesheetEntry.getWorkdayDate().getDayOfMonth()));

        EditText fromTimeView = view.findViewById(R.id.timesheet_entry_from_time);
        fromTimeView.setText(Utility.formatTime(timesheetEntry.getFromTime()));

        EditText toTimeView = view.findViewById(R.id.timesheet_entry_to_time);
        toTimeView.setText(Utility.formatTime(timesheetEntry.getToTime()));

        EditText breakView = view.findViewById(R.id.timesheet_entry_break);
        breakView.setText(String.format("%s", timesheetEntry.getBreakInMin()));

        EditText commentView = view.findViewById(R.id.timesheet_entry_comment);
        commentView.setText(timesheetEntry.getComment());

        // hide fields if this is a new
        if (timesheetEntry.getId() == null) {
            timesheetNameView.setEnabled(false);
            view.findViewById(R.id.timesheet_entry_date_layout).setVisibility(View.GONE);
            view.findViewById(R.id.timesheet_entry_delete_btn).setVisibility(View.GONE);
        } else if (timesheetEntry.isClosed()) {
            // timesheet entry is locked
            createdDateView.setEnabled(false);
            lastModifiedDateView.setEnabled(false);
            // disable input that is not allowed to edit
            timesheetNameView.setEnabled(false);
            workdayYearView.setEnabled(false);
            workdayMonthView.setEnabled(false);
            view.findViewById(R.id.timesheet_entry_date_layout).setVisibility(View.GONE);
            view.findViewById(R.id.timesheet_entry_delete_btn).setVisibility(View.GONE);
            view.findViewById(R.id.timesheet_entry_add_btn).setVisibility(View.GONE);
            view.findViewById(R.id.timesheet_entry_cancel_btn).setVisibility(View.GONE);
        } else {
            // change button icon to from add new to save
            ((MaterialButton) view.findViewById(R.id.timesheet_entry_save_btn)).setText(getResources().getString(R.string.btn_save));
            if (timesheetEntry.isClosed()) {
                view.findViewById(R.id.btn_timesheet_new_delete).setVisibility(View.GONE);
                view.findViewById(R.id.btn_timesheet_new_save).setVisibility(View.GONE);
            }
            createdDateView.setEnabled(false);
            lastModifiedDateView.setEnabled(false);
            // disable input that is not allowed to edit
            timesheetNameView.setEnabled(false);
            workdayYearView.setEnabled(false);
            workdayMonthView.setEnabled(false);
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
        if (id == R.id.timesheet_entry_save_btn) {
            Log.d(Utility.buildTag(getClass(), "onClick"), "save button, save entry");
        } else if (id == R.id.timesheet_entry_cancel_btn) {
            // return back to main view
            Log.d(Utility.buildTag(getClass(), "onClick"), "cancel button, return back to timesheet list view");
        }
    }

    private String getTimesheetEntryAsJson() {
        TimesheetEntry timesheetEntry = new TimesheetEntry();

        TextView timesheetIdView = requireView().findViewById(R.id.timesheet_entry_timesheet_id);
        timesheetEntry.setTimesheetId(Utility.isInteger(timesheetIdView.getText().toString()) ? Long.parseLong(timesheetIdView.getText().toString()) : null);

        TextView idView = requireView().findViewById(R.id.timesheet_entry_id);
        timesheetEntry.setId(Utility.isInteger(idView.getText().toString()) ? Long.parseLong(idView.getText().toString()) : null);

        EditText createdDateView = requireView().findViewById(R.id.timesheet_entry_created_date);
        LocalDateTime createdDateTime = Utility.toLocalDateTime(createdDateView.getText().toString());
        if (createdDateTime != null) {
            timesheetEntry.setCreatedDate(createdDateTime);
        }

        EditText lastModifiedDateView = requireView().findViewById(R.id.timesheet_entry_last_modified_date);
        LocalDateTime lastModifiedDateTime = Utility.toLocalDateTime(lastModifiedDateView.getText().toString());
        if (lastModifiedDateTime != null) {
            timesheetEntry.setLastModifiedDate(lastModifiedDateTime);
        }

        MaterialButtonToggleGroup typeBtnGrp = requireView().findViewById(R.id.timesheet_entry_type_btn_group_layout);
        MaterialButton checkedTypeButton = typeBtnGrp.findViewById(typeBtnGrp.getCheckedButtonId());
        timesheetEntry.setType(TimesheetEntry.TimesheetEntryTypeEnum.valueOf(checkedTypeButton.getText().toString().toUpperCase()).name());

        // always set to OPEN, not possible to change manually.
        timesheetEntry.setStatus(TimesheetEntry.TimesheetEntryStatusEnum.OPEN.name());

        TextView workdayYearView = requireView().findViewById(R.id.timesheet_entry_workday_year);
        TextView workdayMonthView = requireView().findViewById(R.id.timesheet_entry_workday_month);
        TextView workdayDayView = requireView().findViewById(R.id.timesheet_entry_workday_day);
        timesheetEntry.setWorkdayDate(LocalDate.of(Integer.parseInt(workdayYearView.getText().toString()), Integer.parseInt(workdayMonthView.getText().toString()), Integer.parseInt(workdayDayView.getText().toString())));

        TextView fromTimeView = requireView().findViewById(R.id.timesheet_entry_from_time);
        timesheetEntry.setFromTime(Utility.toLocalTime(fromTimeView.getText().toString()));

        TextView toTimeView = requireView().findViewById(R.id.timesheet_entry_to_time);
        timesheetEntry.setToTime(Utility.toLocalTime(toTimeView.getText().toString()));

        TextView breakView = requireView().findViewById(R.id.timesheet_entry_break);
        timesheetEntry.setBreakInMin(Integer.parseInt(breakView.getText().toString()));

        TextView commentView = requireView().findViewById(R.id.timesheet_entry_comment);
        timesheetEntry.setComment(commentView.getText().toString());
        // determine and set worked minutes
        timesheetEntry.setWorkedMinutes(Long.valueOf(ChronoUnit.MINUTES.between(timesheetEntry.getFromTime(), timesheetEntry.getToTime())).intValue());
        try {
            return Utility.gsonMapper().toJson(timesheetEntry);
        } catch (Exception e) {
            Log.e("getTimesheetAsJson", e.toString());
            throw new TerexApplicationException("unable to parse object to json!", "50050", e);
        }
    }
}
