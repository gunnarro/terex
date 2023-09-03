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
import com.gunnarro.android.terex.domain.entity.Timesheet;
import com.gunnarro.android.terex.exception.TerexApplicationException;
import com.gunnarro.android.terex.utility.Utility;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;


@AndroidEntryPoint
public class TimesheetAddFragment extends Fragment implements View.OnClickListener {

    // Match a not-empty string. A string with only spaces or no characters is an empty-string.
    public static final String HAS_TEXT_REGEX = "\\w.*+";
    // match one or more withe space
    public static final String EMPTY_TEXT_REGEX = "\\s+";
    // private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private static final Integer DEFAULT_DAILY_BREAK_IN_MINUTES = 30;
    private static final Long DEFAULT_DAILY_WORKING_HOURS_IN_MINUTES = 8 * 60L - DEFAULT_DAILY_BREAK_IN_MINUTES;
    private static final Integer DEFAULT_HOURLY_RATE = 1075;
    private static final String DEFAULT_STATUS = "Open";
    private static final String TIMESHEET_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";

    String[] clients = new String[]{"Norway Consulting", "Technogarden", "IT-Verket"};
    String[] projects = new String[]{"CatalystOne Solution AS", "MasterCard"};

    @Inject
    public TimesheetAddFragment() {
        // Needed by dagger framework
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        requireActivity().setTitle(R.string.title_register_work);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_timesheet_add_grid, container, false);
        Timesheet timesheet = Timesheet.createDefault(clients[0], projects[0], DEFAULT_STATUS, DEFAULT_DAILY_BREAK_IN_MINUTES, LocalDate.now(), DEFAULT_DAILY_WORKING_HOURS_IN_MINUTES, DEFAULT_HOURLY_RATE);
        // check if this is an existing or a new timesheet
        String timesheetJson = getArguments() != null ? getArguments().getString(TimesheetListFragment.TIMESHEET_JSON_INTENT_KEY) : null;
        if (timesheetJson != null) {
            try {
                timesheet = Utility.gsonMapper().fromJson(timesheetJson, Timesheet.class);
                Log.d(Utility.buildTag(getClass(), "onFragmentResult"), String.format("action: %s, timesheet: %s", timesheetJson, timesheet));
            } catch (Exception e) {
                Log.e("", e.toString());
                throw new TerexApplicationException("Application Error!", "5000", e);
            }
        }

        // create client spinner
        final AutoCompleteTextView clientSpinner = view.findViewById(R.id.timesheet_client_spinner);
        ArrayAdapter<String> clientAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, clients);
        clientSpinner.setAdapter(clientAdapter);
        // create project spinner
        final AutoCompleteTextView projectSpinner = view.findViewById(R.id.timesheet_project_spinner);
        ArrayAdapter<String> projectAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, projects);
        projectSpinner.setAdapter(projectAdapter);

        // create status spinner
        final AutoCompleteTextView statusSpinner = view.findViewById(R.id.timesheet_status_spinner);
        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.timesheet_statuses, android.R.layout.simple_spinner_item);
        statusSpinner.setAdapter(statusAdapter);

        // workday date picker
        TextInputEditText workdayDate = view.findViewById(R.id.timesheet_workday_date);
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
        EditText fromTime = view.findViewById(R.id.timesheet_from_time);
        // turn off keyboard popup when clicked
        fromTime.setShowSoftInputOnFocus(false);
        fromTime.setOnClickListener(v -> {
            TimePickerDialog fromTimePicker = new TimePickerDialog(
                    requireContext(),
                    R.style.DialogTheme,
                    (timePicker, selectedHour, selectedMinute) -> {
                        fromTime.setText(Utility.formatToHHMM(selectedHour, selectedMinute));
                        updateWorkedHours();
                    },
                    LocalTime.now().getHour(),
                    LocalTime.now().getMinute(),
                    true);
            fromTimePicker.setTitle(getResources().getString(R.string.title_select_from_time));
            fromTimePicker.show();
        });

        // to time time picker
        EditText toTime = view.findViewById(R.id.timesheet_to_time);
        // turn off keyboard popup when clicked
        toTime.setShowSoftInputOnFocus(false);
        toTime.setOnClickListener(v -> {
            TimePickerDialog toTimePicker = new TimePickerDialog(
                    requireContext(),
                    R.style.DialogTheme,
                    (timePicker, selectedHour, selectedMinute) -> {
                        toTime.setText(Utility.formatToHHMM(selectedHour, selectedMinute));
                        updateWorkedHours();
                    },
                    LocalTime.now().getHour(),
                    LocalTime.now().getMinute(),
                    true);
            toTimePicker.setTitle(getResources().getString(R.string.title_select_to_time));
            toTimePicker.show();
        });

        // disable save button as default
        view.findViewById(R.id.btn_timesheet_add_save).setEnabled(true);
        view.findViewById(R.id.btn_timesheet_add_save).setOnClickListener(v -> {
            view.findViewById(R.id.btn_timesheet_add_save).setBackgroundColor(getResources().getColor(R.color.color_btn_bg_cancel, view.getContext().getTheme()));
            Bundle result = new Bundle();
            result.putString(TimesheetListFragment.TIMESHEET_JSON_INTENT_KEY, getTimesheetAsJson());
            result.putString(TimesheetListFragment.TIMESHEET_ACTION_KEY, TimesheetListFragment.TIMESHEET_ACTION_SAVE);
            getParentFragmentManager().setFragmentResult(TimesheetListFragment.TIMESHEET_REQUEST_KEY, result);
            Log.d(Utility.buildTag(getClass(), "onCreateView"), "add new add item intent: " + getTimesheetAsJson());
            returnToTimesheetList();
        });

        view.findViewById(R.id.btn_timesheet_add_delete).setOnClickListener(v -> {
            view.findViewById(R.id.btn_timesheet_add_delete).setBackgroundColor(getResources().getColor(R.color.color_btn_bg_cancel, view.getContext().getTheme()));
            Bundle result = new Bundle();
            result.putString(TimesheetListFragment.TIMESHEET_JSON_INTENT_KEY, getTimesheetAsJson());
            result.putString(TimesheetListFragment.TIMESHEET_ACTION_KEY, TimesheetListFragment.TIMESHEET_ACTION_DELETE);
            getParentFragmentManager().setFragmentResult(TimesheetListFragment.TIMESHEET_REQUEST_KEY, result);
            Log.d(Utility.buildTag(getClass(), "onCreateView"), "add new delete item intent");
            returnToTimesheetList();
        });

        view.findViewById(R.id.btn_timesheet_add_cancel).setOnClickListener(v -> {
            view.findViewById(R.id.btn_timesheet_add_cancel).setBackgroundColor(getResources().getColor(R.color.color_btn_bg_cancel, view.getContext().getTheme()));
            // Simply return back to credential list
            NavigationView navigationView = requireActivity().findViewById(R.id.navigationView);
            requireActivity().onOptionsItemSelected(navigationView.getMenu().findItem(R.id.nav_timesheet_list));
            returnToTimesheetList();
        });

        updateTimesheetAddView(view, timesheet);
        Log.d(Utility.buildTag(getClass(), "onCreateView"), timesheet.toString());
        return view;
    }

    private void returnToTimesheetList() {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, TimesheetListFragment.class, null)
                .setReorderingAllowed(true)
                .commit();
    }

    private void updateTimesheetAddView(View view, Timesheet timesheet) {
        TextView id = view.findViewById(R.id.timesheet_entity_id);
        id.setText(String.valueOf(timesheet.getId()));

        EditText createdDateView = view.findViewById(R.id.timesheet_created_date);
        createdDateView.setText(Utility.formatDateTime(timesheet.getCreatedDate()));

        EditText lastModifiedDateView = view.findViewById(R.id.timesheet_last_modified_date);
        lastModifiedDateView.setText(Utility.formatDateTime(timesheet.getLastModifiedDate()));

        AutoCompleteTextView clientSpinner = view.findViewById(R.id.timesheet_client_spinner);
        clientSpinner.setText(timesheet.getClientName());

        AutoCompleteTextView projectSpinner = view.findViewById(R.id.timesheet_project_spinner);
        projectSpinner.setText(timesheet.getProjectName());

        AutoCompleteTextView statusSpinner = view.findViewById(R.id.timesheet_status_spinner);
        statusSpinner.setText(timesheet.getStatus());

        EditText hourlyRateView = view.findViewById(R.id.timesheet_hourly_rate);
        hourlyRateView.setText(String.format("%s", timesheet.getHourlyRate()));

        EditText workdayDateView = view.findViewById(R.id.timesheet_workday_date);
        workdayDateView.setText(Utility.formatDate(timesheet.getWorkdayDate()));

        EditText fromTimeView = view.findViewById(R.id.timesheet_from_time);
        fromTimeView.setText(Utility.formatTime(timesheet.getFromTime()));

        EditText toTimeView = view.findViewById(R.id.timesheet_to_time);
        toTimeView.setText(Utility.formatTime(timesheet.getToTime()));

        EditText breakView = view.findViewById(R.id.timesheet_break);
        breakView.setText(String.format("%s", timesheet.getBreakInMin()));

        //TextView workedHoursView = view.findViewById(R.id.timesheet_worked_hours);
        //workedHoursView.setText(String.format("%s", "0"));

        EditText commentView = view.findViewById(R.id.timesheet_comment);
        commentView.setText(timesheet.getComment());

        // hide fields if this is a new
        if (timesheet.getId() == null) {
            view.findViewById(R.id.timesheet_created_date_layout).setVisibility(View.GONE);
            view.findViewById(R.id.timesheet_last_modified_date_layout).setVisibility(View.GONE);
            view.findViewById(R.id.btn_timesheet_add_delete).setVisibility(View.GONE);
        } else {
            // change button icon to from add new to save
            ((MaterialButton) view.findViewById(R.id.btn_timesheet_add_save)).setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_check_black_24dp));
        }
        Log.d(Utility.buildTag(getClass(), "updateTimesheetAddView"), String.format("updated %s ", timesheet));
    }

    @Override
    public void onClick(View view) {
        // ask every time
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_MEDIA_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // You have not been granted access, ask for permission now, no need for nay permissions
            Log.d(Utility.buildTag(getClass(), "onClick"), "save button, permission not granted");
        }

        int id = view.getId();
        if (id == R.id.btn_timesheet_add_save) {
            Log.d(Utility.buildTag(getClass(), "onClick"), "save button, save entry");
        } else if (id == R.id.btn_timesheet_add_cancel) {
            // return back to main view
            Log.d(Utility.buildTag(getClass(), "onClick"), "cancel button, return back to timesheet list view");
        }
    }

    private String getTimesheetAsJson() {
        Timesheet timesheet = new Timesheet();

        TextView idView = requireView().findViewById(R.id.timesheet_entity_id);
        timesheet.setId(Utility.isInteger(idView.getText().toString()) ? Long.parseLong(idView.getText().toString()) : null);

        EditText createdDateView = requireView().findViewById(R.id.timesheet_created_date);
        LocalDateTime createdDateTime = Utility.toLocalDateTime(createdDateView.getText().toString());
        if (createdDateTime != null) {
            timesheet.setCreatedDate(createdDateTime);
        }

        EditText lastModifiedDateView = requireView().findViewById(R.id.timesheet_last_modified_date);
        LocalDateTime lastModifiedDateTime = Utility.toLocalDateTime(lastModifiedDateView.getText().toString());
        if (lastModifiedDateTime != null) {
            timesheet.setLastModifiedDate(lastModifiedDateTime);
        }

        AutoCompleteTextView clientSpinner = requireView().findViewById(R.id.timesheet_client_spinner);
        timesheet.setClientName(clientSpinner.getText().toString());

        AutoCompleteTextView projectSpinner = requireView().findViewById(R.id.timesheet_project_spinner);
        timesheet.setProjectName(projectSpinner.getText().toString());

        AutoCompleteTextView statusSpinner = requireView().findViewById(R.id.timesheet_status_spinner);
        timesheet.setStatus(statusSpinner.getText().toString());

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

        // TextView workedHoursView = requireView().findViewById(R.id.timesheet_worked_hours);
        // timesheet.setWorkedMinutes((int) (Double.parseDouble(workedHoursView.getText().toString())*60));

        TextView commentView = requireView().findViewById(R.id.timesheet_comment);
        timesheet.setComment(commentView.getText().toString());

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

    private boolean isInputFormDataValid() {
        return true;
    }

    // FIXME
    private void updateWorkedHours() {
        /*
        EditText fromTime = requireView().findViewById(R.id.timesheet_from_time);
        EditText toTime = requireView().findViewById(R.id.timesheet_to_time);
        TextView workedHoursView = requireView().findViewById(R.id.timesheet_worked_hours);
        workedHoursView.setText(Utility.getDateDiffInHours(LocalTime.parse(fromTime.getText()), LocalTime.parse(toTime.getText())));

         */
    }
}
