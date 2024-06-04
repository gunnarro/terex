package com.gunnarro.android.terex.ui.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
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
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.gunnarro.android.terex.R;
import com.gunnarro.android.terex.domain.dto.ClientDto;
import com.gunnarro.android.terex.domain.dto.ProjectDto;
import com.gunnarro.android.terex.domain.dto.SpinnerItem;
import com.gunnarro.android.terex.domain.entity.Timesheet;
import com.gunnarro.android.terex.exception.InputValidationException;
import com.gunnarro.android.terex.exception.TerexApplicationException;
import com.gunnarro.android.terex.service.ClientService;
import com.gunnarro.android.terex.service.TimesheetService;
import com.gunnarro.android.terex.utility.Utility;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;


@AndroidEntryPoint
public class TimesheetNewFragment extends BaseFragment implements View.OnClickListener {

    private TimesheetService timesheetService;
    private ClientService clientService;

    @Inject
    public TimesheetNewFragment() {
        // Needed by dagger framework
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requireActivity().setTitle(R.string.title_timesheet);
        timesheetService = new TimesheetService();
        clientService = new ClientService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_timesheet_new, container, false);

        ClientDto clientDto = clientService.getClient(1L);
        if (clientDto == null) {
            // no clients found, display info dialog and return timesheet list
            return view;
        }

        if (clientDto.getProjectList() == null || clientDto.getProjectList().isEmpty()) {
            // client have no projects, display info dialog and return timesheet list
            return view;
        }

        Long userId = 1L; // fixme
        String[] clients = new String[]{clientDto.getName()};
        Long[] projects = clientDto.getProjectList().stream().map(ProjectDto::getId).toArray(Long[]::new);

        Timesheet timesheet = Timesheet.createDefault(userId, projects[0], LocalDate.now().getYear(), LocalDate.now().getMonthValue());
        // check if this is an existing or a new timesheet
        Long timesheetId = getArguments() != null ? getArguments().getLong(TimesheetListFragment.TIMESHEET_ID_KEY) : null;
        if (timesheetId != null && timesheetId > 0) {
            try {
                timesheet = timesheetService.getTimesheet(timesheetId);
                Log.d(Utility.buildTag(getClass(), "onCreateView"), String.format("timesheet=%s", timesheet));
            } catch (Exception e) {
                Log.e("Timesheet new error!", e.toString());
                throw new TerexApplicationException("Application Error!", "5000", e);
            }
        }

        // status button group - some styling problem - buttons are disabled as default
        MaterialButtonToggleGroup statusBtnGrp = view.findViewById(R.id.timesheet_new_status_btn_group_layout);
        statusBtnGrp.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
                    ((MaterialButton) group.findViewById(checkedId)).setChecked(isChecked);
                    /*
                    group.check(R.id.timesheet_new_status_billed);
                    if (checkedId == R.id.timesheet_new_status_new) {
                        ((MaterialButton) group.findViewById(checkedId)).setChecked(isChecked);
                    } else if (checkedId == R.id.timesheet_new_status_active) {
                        group.findViewById(R.id.timesheet_new_status_active).setSelected(isChecked);
                    } else if (checkedId == R.id.timesheet_new_status_completed) {
                        group.findViewById(R.id.timesheet_new_status_completed).setSelected(isChecked);
                    } else if (checkedId == R.id.timesheet_new_status_billed) {
                        group.findViewById(R.id.timesheet_new_status_billed).setSelected(isChecked);
                    }*/
                    Log.d("statusBtnGrp.addOnButtonCheckedListener", String.format("checkedId=%s, isChecked=%s", checkedId, isChecked));
                }
        );

        // create timesheet clients spinner
        final AutoCompleteTextView clientSpinner = view.findViewById(R.id.timesheet_new_client_spinner);
        ArrayAdapter<String> clientAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, clients);
        clientSpinner.setAdapter(clientAdapter);
        clientSpinner.setListSelection(0);
        clientSpinner.setInputMethodMode(ListPopupWindow.INPUT_METHOD_NOT_NEEDED);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            clientSpinner.setAutoHandwritingEnabled(false);
            clientSpinner.setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO);
        }

        // create client projects spinner
        final AutoCompleteTextView projectSpinner = view.findViewById(R.id.timesheet_new_project_spinner);
        List<SpinnerItem> projectItems = clientDto.getProjectList().stream().map(p -> new SpinnerItem(p.getId(), p.getName())).collect(Collectors.toList());
        ArrayAdapter<SpinnerItem> projectAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, projectItems);
        projectSpinner.setAdapter(projectAdapter);
        projectSpinner.setListSelection(0);

        // create timesheet statuses spinner
        final AutoCompleteTextView statusSpinner = view.findViewById(R.id.timesheet_new_status_spinner);
        ArrayAdapter<CharSequence> statusAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, Timesheet.TimesheetStatusEnum.names());
        statusSpinner.setAdapter(statusAdapter);
        statusSpinner.setListSelection(0);
        statusSpinner.setValidator(new AutoCompleteTextView.Validator() {
            @Override
            public boolean isValid(CharSequence text) {
                try {
                    Timesheet.TimesheetStatusEnum.valueOf(text.toString());
                    return true;
                } catch (Exception e) {
                    // ((AutoCompleteTextView)requireView().findViewById(R.id.timesheet_new_status_spinner)).setError("Invalid status");
                    return false;
                }
            }

            @Override
            public CharSequence fixText(CharSequence invalidText) {
                return null;
            }
        });

        // create timesheet year spinner
        final AutoCompleteTextView yearSpinner = view.findViewById(R.id.timesheet_new_year_spinner);
        ArrayAdapter<CharSequence> yearAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, Utility.getYears());
        yearSpinner.setAdapter(yearAdapter);
        yearSpinner.setListSelection(0);
        yearSpinner.setOnItemClickListener((parent, view12, position, id) -> Log.d("yearSpinner", "selected: " + yearAdapter.getItem(position)));

        // create timesheet month spinner
        final AutoCompleteTextView monthSpinner = view.findViewById(R.id.timesheet_new_month_spinner);
        ArrayAdapter<CharSequence> monthAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, Utility.getMonthNames());
        monthSpinner.setAdapter(monthAdapter);
        monthSpinner.setListSelection(0);
        monthSpinner.setValidator(new AutoCompleteTextView.Validator() {
            @Override
            public boolean isValid(CharSequence text) {
                try {
                    if (Utility.mapMonthNameToNumber(text.toString()) == null) {
                        //   ((AutoCompleteTextView)requireView().findViewById(R.id.timesheet_new_month_spinner)).setError("Invalid month name");
                        return false;
                    }
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }

            @Override
            public CharSequence fixText(CharSequence invalidText) {
                return null;
            }
        });

        monthSpinner.setOnItemClickListener((parent, view1, position, id) -> {
            Log.d("monthSpinner", "selected: " + monthAdapter.getItem(position));
            AutoCompleteTextView year = requireView().findViewById(R.id.timesheet_new_year_spinner);
            Log.d("monthSpinner", String.format("selected, year=%s, mount=%s", year.getText(), monthAdapter.getItem(position)));
            updateFromToDate(Utility.toLocalDate(year.getText().toString(), monthAdapter.getItem(position).toString(), 1));
        });

        // disable save button as default
        view.findViewById(R.id.btn_timesheet_new_save).setEnabled(true);
        view.findViewById(R.id.btn_timesheet_new_save).setOnClickListener(v -> {
            if (!isInputDataValid()) {
                return;
            }
            view.findViewById(R.id.btn_timesheet_new_save).setBackgroundColor(getResources().getColor(R.color.color_btn_bg_cancel, view.getContext().getTheme()));
            saveTimesheet();
        });

        view.findViewById(R.id.btn_timesheet_new_delete).setOnClickListener(v -> {
            view.findViewById(R.id.btn_timesheet_new_delete).setBackgroundColor(getResources().getColor(R.color.color_btn_bg_cancel, view.getContext().getTheme()));
            confirmDeleteTimesheetDialog(getString(R.string.msg_delete_timesheet), getString(R.string.msg_confirm_delete));
        });

        view.findViewById(R.id.btn_timesheet_new_cancel).setOnClickListener(v -> {
            view.findViewById(R.id.btn_timesheet_new_cancel).setBackgroundColor(getResources().getColor(R.color.color_btn_bg_cancel, view.getContext().getTheme()));
            // Simply return back to timesheet list
            navigateTo(R.id.nav_from_timesheet_details_to_timesheet_list, null);
        });

        updateTimesheetInputData(view, timesheet);
        Log.d(Utility.buildTag(getClass(), "onCreateView"), String.format("view timesheet=%s", timesheet));
        return view;
    }

    /**
     * Check data here
     */
    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final AutoCompleteTextView timesheetSpinner = view.findViewById(R.id.timesheet_new_client_spinner);
        if (timesheetSpinner.getAdapter() == null || timesheetSpinner.getAdapter().isEmpty()) {
            showInfoDialog("Info", "No clients found. Please add a client with least one project.");
            // navigate back to timesheet list
            navigateTo(R.id.nav_to_timesheet_list, savedInstanceState, true);
        } else {
            final AutoCompleteTextView projectSpinner = view.findViewById(R.id.timesheet_new_project_spinner);
            if (projectSpinner.getAdapter() == null || projectSpinner.getAdapter().isEmpty()) {
                showInfoDialog("Info", "No projects found! Please add a project to the client.");
                // navigate back to timesheet list
                navigateTo(R.id.nav_to_timesheet_list, savedInstanceState, true);
            }
        }
    }

    private void saveTimesheet() {
        try {
            Timesheet timesheet = readTimesheetInputData();
            timesheetService.saveTimesheet(timesheet);
            showSnackbar(String.format(getResources().getString(R.string.info_timesheet_list_saved_msg_format), timesheet.toString(), timesheet.getYear() + "-" + timesheet.getMonth()), R.color.color_snackbar_text_add);
            navigateTo(R.id.nav_from_timesheet_details_to_timesheet_list, null);
        } catch (TerexApplicationException | InputValidationException ex) {
            showInfoDialog("Error", String.format("%s", ex.getMessage()));
        } catch (Exception e) {
            showInfoDialog("Error", String.format("%s", e.getCause()));
        }
    }

    private void deleteTimesheet() {
        Timesheet timesheet = readTimesheetInputData();
        timesheetService.deleteTimesheet(timesheet.getId());
        showSnackbar(String.format(getResources().getString(R.string.info_timesheet_list_delete_msg_format), timesheet.toString()), R.color.color_snackbar_text_delete);
        navigateTo(R.id.nav_from_timesheet_details_to_timesheet_list, null);
    }

    private void confirmDeleteTimesheetDialog(final String title, final String message) {
        new MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme).setTitle(title).setMessage(message).setPositiveButton(R.string.btn_ok, (dialogInterface, i) -> deleteTimesheet()).setNeutralButton(R.string.btn_cancel, (dialogInterface, i) -> {
            // nothing to do
        }).show();
    }

    private void updateTimesheetInputData(@NotNull View view, @NotNull Timesheet timesheet) {
        // new timesheet do not have id yet, the id is generated upon save to database
        if (!timesheet.isNew()) {
            TextView timesheetIdView = view.findViewById(R.id.timesheet_new_id);
            timesheetIdView.setText(String.valueOf(timesheet.getId()));
        }

        TextView userAccountIdView = view.findViewById(R.id.timesheet_new_user_account_id);
        userAccountIdView.setText(String.valueOf(timesheet.getUserId()));

        EditText createdDateView = view.findViewById(R.id.timesheet_new_created_date);
        createdDateView.setText(Utility.formatDateTime(timesheet.getCreatedDate()));

        EditText lastModifiedDateView = view.findViewById(R.id.timesheet_new_last_modified_date);
        lastModifiedDateView.setText(Utility.formatDateTime(timesheet.getLastModifiedDate()));

        AutoCompleteTextView clientSpinner = view.findViewById(R.id.timesheet_new_client_spinner);
        clientSpinner.setText(clientSpinner.getAdapter().getItem(0).toString());

        AutoCompleteTextView projectSpinner = view.findViewById(R.id.timesheet_new_project_spinner);
        projectSpinner.setText(projectSpinner.getAdapter().getItem(0).toString());

        // set status button group
        if (timesheet.isNew()) {
            ((MaterialButton) view.findViewById(R.id.timesheet_new_status_btn_new)).setChecked(true);
        } else if (timesheet.isActive()) {
            ((MaterialButton) view.findViewById(R.id.timesheet_new_status_btn_active)).setChecked(true);
        } else if (timesheet.isCompleted()) {
            ((MaterialButton) view.findViewById(R.id.timesheet_new_status_btn_completed)).setChecked(true);
        } else if (timesheet.isBilled()) {
            ((MaterialButton) view.findViewById(R.id.timesheet_new_status_btn_billed)).setChecked(true);
        }

        AutoCompleteTextView statusSpinner = view.findViewById(R.id.timesheet_new_status_spinner);
        statusSpinner.setText(timesheet.getStatus());

        AutoCompleteTextView yearSpinner = view.findViewById(R.id.timesheet_new_year_spinner);
        yearSpinner.setText(String.format("%s", timesheet.getYear()));

        AutoCompleteTextView monthSpinner = view.findViewById(R.id.timesheet_new_month_spinner);
        monthSpinner.setText(Utility.mapMonthNumberToName(timesheet.getMonth() - 1));

        EditText fromTimeView = view.findViewById(R.id.timesheet_new_from_date);
        fromTimeView.setText(Utility.formatDate(timesheet.getFromDate()));

        EditText toTimeView = view.findViewById(R.id.timesheet_new_to_date);
        toTimeView.setText(Utility.formatDate(timesheet.getToDate()));

        EditText descriptionView = view.findViewById(R.id.timesheet_new_description);
        descriptionView.setText(timesheet.getDescription());

        // hidden as default
        view.findViewById(R.id.timesheet_new_id).setVisibility(View.GONE);
        view.findViewById(R.id.timesheet_new_created_date_layout).setVisibility(View.GONE);
        view.findViewById(R.id.timesheet_new_last_modified_date_layout).setVisibility(View.GONE);

        // hide fields if this is a new
        if (timesheet.isNew()) {
            view.findViewById(R.id.timesheet_new_created_date_layout).setVisibility(View.GONE);
            view.findViewById(R.id.timesheet_new_last_modified_date_layout).setVisibility(View.GONE);
            view.findViewById(R.id.btn_timesheet_new_delete).setVisibility(View.GONE);
        } else if (timesheet.isActive() || timesheet.isCompleted()) {
            // Status and description are the only values that is allowed to change.
            // change button icon to from add new to save
            view.findViewById(R.id.timesheet_new_created_date_layout).setVisibility(View.VISIBLE);
            view.findViewById(R.id.timesheet_new_last_modified_date_layout).setVisibility(View.VISIBLE);
            ((MaterialButton) view.findViewById(R.id.btn_timesheet_new_save)).setText(getResources().getString(R.string.btn_save));
            // only allowed to update status
            createdDateView.setEnabled(false);
            lastModifiedDateView.setEnabled(false);
            clientSpinner.setEnabled(false);
            view.findViewById(R.id.timesheet_new_client_spinner_layout).setEnabled(false);
            projectSpinner.setEnabled(false);
            view.findViewById(R.id.timesheet_new_project_spinner_layout).setEnabled(false);
            statusSpinner.setEnabled(true);
            view.findViewById(R.id.timesheet_new_status_btn_group_layout).setEnabled(true);
            yearSpinner.setEnabled(false);
            monthSpinner.setEnabled(false);
            fromTimeView.setEnabled(false);
            toTimeView.setEnabled(false);
            descriptionView.setEnabled(true);
        } else if (timesheet.isBilled()) {
            // if timesheet is billed, disable all fields and actions, not allowed to change.
            view.findViewById(R.id.timesheet_new_created_date_layout).setVisibility(View.VISIBLE);
            view.findViewById(R.id.timesheet_new_last_modified_date_layout).setVisibility(View.VISIBLE);
            view.findViewById(R.id.timesheet_new_client_spinner_layout).setEnabled(false);
            view.findViewById(R.id.timesheet_new_project_spinner_layout).setEnabled(false);
            view.findViewById(R.id.timesheet_new_status_spinner_layout).setEnabled(false);
            view.findViewById(R.id.timesheet_new_status_btn_group_layout).setEnabled(false);
            //view.findViewById(R.id.timesheet_new_status_group_layout).setEnabled(false);
            view.findViewById(R.id.timesheet_new_year_spinner_layout).setEnabled(false);
            view.findViewById(R.id.timesheet_new_month_spinner_layout).setEnabled(false);
            view.findViewById(R.id.timesheet_new_created_description_layout).setEnabled(false);
            // disable all fields, timesheet is locked.
            createdDateView.setEnabled(false);
            lastModifiedDateView.setEnabled(false);
            clientSpinner.setEnabled(false);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                clientSpinner.setAllowClickWhenDisabled(false);
            }
            projectSpinner.setEnabled(false);
            statusSpinner.setEnabled(false);
            yearSpinner.setEnabled(false);
            monthSpinner.setEnabled(false);
            fromTimeView.setEnabled(false);
            toTimeView.setEnabled(false);
            descriptionView.setEnabled(false);
            view.findViewById(R.id.btn_timesheet_new_delete).setVisibility(View.GONE);
            view.findViewById(R.id.btn_timesheet_new_save).setVisibility(View.GONE);
            ((MaterialButton) view.findViewById(R.id.btn_timesheet_new_cancel)).setText(getResources().getString(R.string.btn_back));
        }
        Log.d(Utility.buildTag(getClass(), "updateTimesheetNewView"), String.format("updated timesheetId=%s ", timesheet.getId()));
    }

    @Override
    public void onClick(View view) {
        // ask every time
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_MEDIA_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // You have not been granted access, ask for permission now, no need for nay permissions
            Log.d(Utility.buildTag(getClass(), "onClick"), "save button, permission not granted");
        }

        int id = view.getId();
        if (id == R.id.btn_timesheet_new_save) {
            Log.d(Utility.buildTag(getClass(), "onClick"), "save button, save timesheet");
        } else if (id == R.id.btn_timesheet_new_cancel) {
            // return back to main view
            Log.d(Utility.buildTag(getClass(), "onClick"), "cancel button, return back to timesheet list view");
        }
    }

    private Timesheet readTimesheetInputData() {
        Timesheet timesheet = new Timesheet();

        TextView timesheetIdView = requireView().findViewById(R.id.timesheet_new_id);
        if (timesheetIdView.getText() != null && !timesheetIdView.getText().toString().isBlank()) {
            timesheet.setId(Long.parseLong(timesheetIdView.getText().toString()));
        }

        TextView userAccountIdView = requireView().findViewById(R.id.timesheet_new_user_account_id);
        timesheet.setUserId(Long.parseLong(userAccountIdView.getText().toString()));
        EditText createdDateView = requireView().findViewById(R.id.timesheet_new_created_date);
        LocalDateTime createdDateTime = Utility.toLocalDateTime(createdDateView.getText().toString());
        if (createdDateTime != null) {
            timesheet.setCreatedDate(createdDateTime);
        }

        EditText lastModifiedDateView = requireView().findViewById(R.id.timesheet_new_last_modified_date);
        LocalDateTime lastModifiedDateTime = Utility.toLocalDateTime(lastModifiedDateView.getText().toString());
        if (lastModifiedDateTime != null) {
            timesheet.setLastModifiedDate(lastModifiedDateTime);
        }

        //fixme
        AutoCompleteTextView clientSpinner = requireView().findViewById(R.id.timesheet_new_client_spinner);
        //  timesheet.setClientName(clientSpinner.getText().toString());

        AutoCompleteTextView projectSpinner = requireView().findViewById(R.id.timesheet_new_project_spinner);
        // some trouble to get the project id, so this mey be to complicated
        int count = projectSpinner.getAdapter().getCount();
        for (int i = 0; i < count; i++) {
            SpinnerItem item = (SpinnerItem) projectSpinner.getAdapter().getItem(i);
            if (item.name().equals(projectSpinner.getText().toString())) {
                timesheet.setProjectId(item.id());
            }
        }

        //MaterialButtonToggleGroup statusBtnGrp = requireView().findViewById(R.id.timesheet_new_status_group_layout);
        //MaterialButton selectedStatusBtn = statusBtnGrp.findViewById(statusBtnGrp.getCheckedButtonId());
        //timesheet.setStatus(selectedStatusBtn.getText().toString().toUpperCase());
       // AutoCompleteTextView statusSpinner = requireView().findViewById(R.id.timesheet_new_status_spinner);
       // timesheet.setStatus(statusSpinner.getText().toString());

        if ( ((MaterialButton) requireView().findViewById(R.id.timesheet_new_status_btn_new)).isChecked()) {
           timesheet.setStatus(Timesheet.TimesheetStatusEnum.NEW.name());
        } else if ( ((MaterialButton) requireView().findViewById(R.id.timesheet_new_status_btn_active)).isChecked()) {
            timesheet.setStatus(Timesheet.TimesheetStatusEnum.ACTIVE.name());
        } else if ( ((MaterialButton) requireView().findViewById(R.id.timesheet_new_status_btn_completed)).isChecked()) {
            timesheet.setStatus(Timesheet.TimesheetStatusEnum.COMPLETED.name());
        } else if ( ((MaterialButton) requireView().findViewById(R.id.timesheet_new_status_btn_billed)).isChecked()) {
            timesheet.setStatus(Timesheet.TimesheetStatusEnum.BILLED.name());
        }

        AutoCompleteTextView yearSpinner = requireView().findViewById(R.id.timesheet_new_year_spinner);
        timesheet.setYear(Integer.parseInt(yearSpinner.getText().toString()));

        AutoCompleteTextView monthSpinner = requireView().findViewById(R.id.timesheet_new_month_spinner);
        timesheet.setMonth(Utility.mapMonthNameToNumber(monthSpinner.getText().toString()));

        TextView fromDateView = requireView().findViewById(R.id.timesheet_new_from_date);
        timesheet.setFromDate(Utility.toLocalDate(fromDateView.getText().toString()));

        TextView toDateView = requireView().findViewById(R.id.timesheet_new_to_date);
        timesheet.setToDate(Utility.toLocalDate(toDateView.getText().toString()));

        TextView descriptionView = requireView().findViewById(R.id.timesheet_new_description);
        timesheet.setDescription(descriptionView.getText().toString());
        return timesheet;
    }

    private boolean isInputDataValid() {
        boolean hasValidationError = true;
        AutoCompleteTextView clientSpinner = requireView().findViewById(R.id.timesheet_new_client_spinner);
        if (!hasText(clientSpinner.getText())) {
            clientSpinner.setError(getString(R.string.lbl_required));
            hasValidationError = false;
        }
        AutoCompleteTextView projectSpinner = requireView().findViewById(R.id.timesheet_new_project_spinner);
        if (!hasText(projectSpinner.getText())) {
            projectSpinner.setError(getString(R.string.lbl_required));
            hasValidationError = false;
        }
        AutoCompleteTextView statusSpinner = requireView().findViewById(R.id.timesheet_new_status_spinner);
        if (!hasText(statusSpinner.getText())) {
            statusSpinner.setError(getString(R.string.lbl_required));
            hasValidationError = false;
        }
        AutoCompleteTextView yearSpinner = requireView().findViewById(R.id.timesheet_new_year_spinner);
        if (!hasText(yearSpinner.getText())) {
            yearSpinner.setError(getString(R.string.lbl_required));
            hasValidationError = false;
        }
        AutoCompleteTextView monthSpinner = requireView().findViewById(R.id.timesheet_new_month_spinner);
        if (!hasText(monthSpinner.getText())) {
            monthSpinner.setError(getString(R.string.lbl_required));
            hasValidationError = false;
        }
        // simply check if the timesheet already exist
        return hasValidationError;
    }

    private void updateFromToDate(LocalDate date) {
        TextView fromDateView = requireView().findViewById(R.id.timesheet_new_from_date);
        fromDateView.setText(Utility.formatDate(Utility.getFirstDayOfMonth(date)));

        TextView toDateView = requireView().findViewById(R.id.timesheet_new_to_date);
        toDateView.setText(Utility.formatDate(Utility.getLastDayOfMonth(date)));
    }
}
