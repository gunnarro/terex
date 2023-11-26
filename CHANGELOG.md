# Change log

## v1.0.0 (02.09.2023)
 ### New Features

 ### Bug Fixes

 ### Dependency upgrades

 ### Documentation


[![Android CI](https://github.com/gunnarro/terex/actions/workflows/android.yml/badge.svg)](https://github.com/gunnarro/terex/actions/workflows/android.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=gunnarro_terex&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=gunnarro_terex)
[![Android Release](https://github.com/gunnarro/terex/actions/workflows/android-release.yml/badge.svg)](https://github.com/gunnarro/terex/actions/workflows/android-release.yml)

# TeRex
Android time tracking app.
This is a simple application for time registration for consultants.

## Resources
- [calendar](https://github.com/kizitonwose/Calendar)


POST git-upload-pack (305 bytes) From https://github.com/gunnarro/terex * branch main -> FETCH_HEAD = [up to date] main -> origin/main hint:
You have divergent branches and need to specify how to reconcile them. hint:
You can do so by running one of the following commands sometime before hint:
your next pull: hint: hint: git config pull.rebase false
# merge hint: git config pull.rebase true
# rebase hint: git config pull.ff only
# fast-forward only hint: hint: You can replace "git config" with "git config --global" to set a default hint: preference for all repositories. You can also pass --rebase, --no-rebase, hint: or --ff-only on the command line to override the configured default per hint: invocation. Need to specify how to reconcile divergent branches.



<resources>
    <string name="app_name">TeRex</string>
    <!-- titles -->
    <string name="title_admin">Administration</string>
    <string name="title_timesheets">Timesheets</string>
    <string name="title_timesheet">Timesheet</string>
    <string name="title_timesheet_entries">Timesheet entries</string>
    <string name="title_register_work">Register Work</string>
    <string name="title_invoices">Invoices</string>
    <string name="title_invoice">Invoice</string>
    <string name="title_project">Project</string>
    <string name="title_credential">Credential Store</string>
    <string name="title_credential_register">Credential Store</string>
    <string name="title_timesheet_calendar">Timesheet calendar</string>
    <string name="title_timesheet_new">New timesheet</string>
    <string name="title_invoice_attachment">Invoice attachment</string>
    <string name="title_settings">Settings</string>

    <!-- labels -->
    <string name="lbl_description">Description</string>
    <string name="lbl_timesheet">Timesheet</string>
    <string name="lbl_invoice">Invoice</string>
    <string name="lbl_client">Client</string>
    <string name="lbl_title">Title</string>
    <string name="lbl_project">Project</string>
    <string name="lbl_hourly_rate">Hourly rate</string>
    <string name="lbl_from_date_time">From</string>
    <string name="lbl_to_date_time">To</string>
    <string name="lbl_break">Break</string>
    <string name="lbl_status">Status</string>
    <string name="lbl_comment">Comment</string>
    <string name="lbl_system">System</string>
    <string name="lbl_username">Username</string>
    <string name="lbl_password">Password</string>
    <string name="lbl_url">Url</string>
    <string name="lbl_project_name">Project name</string>
    <string name="lbl_timesheet_name">Timesheet name</string>
    <string name="lbl_worked_days">Worked days</string>
    <string name="lbl_worked_hours">Worked hours</string>
    <string name="lbl_of">of</string>
    <string name="lbl_export_to_pdf">Exprot to pdf</string>

    <!-- button names -->
    <string name="btn_save">Save</string>
    <string name="btn_cancel">Cancel</string>
    <string name="btn_add">Add</string>
    <string name="btn_back">Back</string>
    <string name="btn_create">Create</string>
    <string name="btn_calendar">Calendar</string>
    <string name="btn_email">Send email</string>
    <string name="btn_view">View</string>
    <string name="btn_ok">Ok</string>

    <!-- dialog messages -->
    <string name="msg_delete_timesheet">Delete timesheet. Note that all registered hours will also be deleted. It is not possible to revert the deletion.</string>
    <string name="msg_confirm_delete">Confirm delete timesheet.</string>

    <!-- invoice -->
    <string name="lbl_company_name">Firma</string>
    <string name="lbl_business_address">Forretningsadresse</string>
    <string name="lbl_organization_number">Organisasjonsnummer</string>
    <string name="lbl_bank_account_number">Bankonto</string>
    <string name="lbl_invoice_reference">Fakturareferanse</string>
    <string name="lbl_invoice_number">Fakturanummer</string>

    <string name="lbl_client_name">Kunde</string>
    <string name="lbl_client_address">Adresse</string>
    <string name="lbl_client_phone_number">Telefon</string>
    <string name="lbl_client_organization_number">Organisasjonsnummer</string>
    <string name="lbl_invoice_project_code">Prosjektkode</string>
    <string name="lbl_client_contact_person">Kontaktperson</string>

    <string name="lbl_billing_date">Billing date</string>
    <string name="lbl_billed_amount">Amount</string>

    <string name="txt_colon">:</string>
    <string name="txt_empty"></string>
    <string name="lbl_workday_date">Date</string>
    <string name="lbl_to_time">To time</string>
    <string name="lbl_from_time">From time</string>
    <string name="lbl_from_date">From date</string>
    <string name="lbl_to_date">To date</string>
    <string name="lbl_year">Year</string>
    <string name="lbl_month">Month</string>
    <string name="lbl_day">Day</string>
    <string name="hh_mm">HH:mm</string>
    <string name="dd_mm_yyyy">dd-MM-yyyy</string>
    <string name="btn_delete">Delete</string>
    <string name="title_activity_item_detail_host">ItemDetailHostActivity</string>
    <string name="title_item_list">Items</string>
    <string name="title_item_detail">Item Detail</string>
    <string name="title_select_from_time">Select from time</string>
    <string name="title_select_to_time">Select to time</string>
    <string name="title_select_workday_date">Select workday date</string>

    <string name="prompt_created_date">Created date</string>
    <string name="prompt_last_modified_date">Last modified date</string>
    <string name="prompt_client">Client</string>
    <string name="prompt_project">Project</string>

    <string name="info_timesheet_list_added_entry">Added new entry to timesheet</string>
    <string name="info_timesheet_list_updated_entry">Updated timesheet entry</string>
    <string name="info_timesheet_list_deleted_entry">Deleted entry from timesheet</string>

    <string name="info_timesheet_list_add_msg_format">Added %s</string>
    <string name="info_timesheet_list_saved_msg_format">Saved %s</string>
    <string name="info_timesheet_list_update_msg_format">Updated %s</string>
    <string name="info_timesheet_list_delete_msg_format">Deleted %s</string>

    <!-- CalendarView values -->
    <string name="material_calendar_monday">Mon</string>
    <string name="material_calendar_tuesday">Tue</string>
    <string name="material_calendar_wednesday">Wen</string>
    <string name="material_calendar_thursday">Thu</string>
    <string name="material_calendar_friday">Fri</string>
    <string name="material_calendar_saturday">Sat</string>
    <string name="material_calendar_sunday">Sun</string>
    <string name="lbl_invoice_attachment">Invoice attachment</string>

    <array name="material_calendar_months_array">
        <item>January</item>
        <item>February</item>
        <item>March</item>
        <item>April</item>
        <item>May</item>
        <item>June</item>
        <item>July</item>
        <item>August</item>
        <item>September</item>
        <item>October</item>
        <item>November</item>
        <item>December</item>
    </array>


</resources>




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
// datePickerDialog.getDatePicker().setMaxDate(maxTime);
// datePickerDialog.getDatePicker().setMinDate(minTime);


            workdayDatePicker.setTitle(getResources().getString(R.string.title_select_workday_date));
            workdayDatePicker.getDatePicker().setMinDate(Utility.getFirstDayOfMonth(LocalDate.now()).toEpochDay());
            workdayDatePicker.getDatePicker().setMaxDate(Utility.getLastDayOfMonth(LocalDate.now()).toEpochDay());
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
            result.putString(TimesheetEntryListFragment.TIMESHEET_ENTRY_JSON_KEY, getTimesheetEntryAsJson());
            result.putString(TimesheetEntryListFragment.TIMESHEET_ENTRY_ACTION_KEY, TimesheetEntryListFragment.TIMESHEET_ENTRY_ACTION_SAVE);
            getParentFragmentManager().setFragmentResult(TimesheetEntryListFragment.TIMESHEET_ENTRY_REQUEST_KEY, result);
            Log.d(Utility.buildTag(getClass(), "onCreateView"), "add new timesheet entry intent: " + getTimesheetEntryAsJson());
            returnToTimesheetEntryList(getArguments().getLong(TimesheetListFragment.TIMESHEET_ID_KEY));
        });

        view.findViewById(R.id.btn_timesheet_entry_delete).setOnClickListener(v -> {
            view.findViewById(R.id.btn_timesheet_entry_delete).setBackgroundColor(getResources().getColor(R.color.color_btn_bg_cancel, view.getContext().getTheme()));
            Bundle result = new Bundle();
            result.putString(TimesheetEntryListFragment.TIMESHEET_ENTRY_JSON_KEY, getTimesheetEntryAsJson());
            result.putString(TimesheetEntryListFragment.TIMESHEET_ENTRY_ACTION_KEY, TimesheetEntryListFragment.TIMESHEET_ENTRY_ACTION_DELETE);
            getParentFragmentManager().setFragmentResult(TimesheetEntryListFragment.TIMESHEET_ENTRY_REQUEST_KEY, result);
            Log.d(Utility.buildTag(getClass(), "onCreateView"), "add new delete item intent");
            returnToTimesheetEntryList(getArguments().getLong(TimesheetListFragment.TIMESHEET_ID_KEY));
        });

        view.findViewById(R.id.btn_timesheet_entry_cancel).setOnClickListener(v -> {
            view.findViewById(R.id.btn_timesheet_entry_cancel).setBackgroundColor(getResources().getColor(R.color.color_btn_bg_cancel, view.getContext().getTheme()));
            // Simply return back to credential list
            NavigationView navigationView = requireActivity().findViewById(R.id.navigationView);
            requireActivity().onOptionsItemSelected(navigationView.getMenu().findItem(R.id.nav_timesheet_list));
            returnToTimesheetEntryList(getArguments().getLong(TimesheetListFragment.TIMESHEET_ID_KEY));
        });

        updateTimesheetEntryAddView(view, readTimesheetEntryFromBundle());
        return view;
    }

    private TimesheetEntry readTimesheetEntryFromBundle() {
        String timesheetEntryJson = getArguments() != null ? getArguments().getString(TimesheetEntryListFragment.TIMESHEET_ENTRY_JSON_KEY) : null;
        Log.d("receives timesheet", "" + timesheetEntryJson);
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
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, TimesheetEntryListFragment.class, bundle)
                .setReorderingAllowed(true)
                .commit();
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

        EditText timesheetNameView = view.findViewById(R.id.timesheet_entry_timesheet_spinner);
        timesheetNameView.setText(timesheetEntry.getTimesheetId().toString());

        AutoCompleteTextView statusSpinner = view.findViewById(R.id.timesheet_entry_status_spinner);
        statusSpinner.setText(timesheetEntry.getStatus());

        EditText hourlyRateView = view.findViewById(R.id.timesheet_entry_hourly_rate);
        hourlyRateView.setText(String.format("%s", timesheetEntry.getHourlyRate()));

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
            view.findViewById(R.id.timesheet_entry_date_layout).setVisibility(View.GONE);
            view.findViewById(R.id.btn_timesheet_entry_delete).setVisibility(View.GONE);
        } else {
            // change button icon to from add new to save
            ((MaterialButton) view.findViewById(R.id.btn_timesheet_entry_save)).setText(getResources().getString(R.string.btn_save));
            if (timesheetEntry.isBilled()) {
                view.findViewById(R.id.btn_timesheet_new_delete).setVisibility(View.GONE);
                view.findViewById(R.id.btn_timesheet_new_save).setVisibility(View.GONE);
            }
            createdDateView.setEnabled(true);
            lastModifiedDateView.setEnabled(true);
            // disable input that is not allowed to edit
            timesheetNameView.setEnabled(false);
            hourlyRateView.setEnabled(false);
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
        if (id == R.id.btn_timesheet_entry_save) {
            Log.d(Utility.buildTag(getClass(), "onClick"), "save button, save entry");
        } else if (id == R.id.btn_timesheet_entry_cancel) {
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

        AutoCompleteTextView statusSpinner = requireView().findViewById(R.id.timesheet_entry_status_spinner);
        timesheetEntry.setStatus(statusSpinner.getText().toString());

        TextView hourlyRateView = requireView().findViewById(R.id.timesheet_entry_hourly_rate);
        timesheetEntry.setHourlyRate(Integer.parseInt(hourlyRateView.getText().toString()));

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



