package com.gunnarro.android.terex.ui.fragment;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.gunnarro.android.terex.R;
import com.gunnarro.android.terex.domain.dto.ClientDto;
import com.gunnarro.android.terex.domain.dto.ProjectDto;
import com.gunnarro.android.terex.domain.entity.Project;
import com.gunnarro.android.terex.exception.InputValidationException;
import com.gunnarro.android.terex.exception.TerexApplicationException;
import com.gunnarro.android.terex.service.ProjectService;
import com.gunnarro.android.terex.utility.Utility;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProjectNewFragment extends BaseFragment implements View.OnClickListener {

    private final ProjectService projectService;

    @Inject
    public ProjectNewFragment() {
        // Needed by dagger framework
        projectService = new ProjectService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        requireActivity().setTitle(R.string.title_project_new);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_project_new, container, false);
        final ProjectDto projectDto = readProjectFromBundle();

        view.findViewById(R.id.project_save_btn).setEnabled(true);
        view.findViewById(R.id.project_save_btn).setOnClickListener(v -> {
            view.findViewById(R.id.project_save_btn).setBackgroundColor(getResources().getColor(R.color.color_btn_bg_cancel, view.getContext().getTheme()));
            saveProject();
        });

        view.findViewById(R.id.project_delete_btn).setOnClickListener(v -> {
            view.findViewById(R.id.project_delete_btn).setBackgroundColor(getResources().getColor(R.color.color_btn_bg_default, view.getContext().getTheme()));
            deleteProject();
        });

        view.findViewById(R.id.project_cancel_btn).setOnClickListener(v -> {
            view.findViewById(R.id.project_cancel_btn).setBackgroundColor(getResources().getColor(R.color.color_btn_bg_default, view.getContext().getTheme()));
            returnToProjectList(projectDto.getClientDto().getId());
        });

        view.findViewById(R.id.project_start_date).setOnClickListener(l ->
                new DatePickerFragment(view.findViewById(R.id.project_start_date)).show(requireActivity().getSupportFragmentManager(), "datePicker"));
        ((TextInputEditText) view.findViewById(R.id.project_start_date)).setShowSoftInputOnFocus(false);

        view.findViewById(R.id.project_end_date).setOnClickListener(l ->
                new DatePickerFragment(view.findViewById(R.id.project_end_date)).show(requireActivity().getSupportFragmentManager(), "datePicker"));
        ((TextInputEditText) view.findViewById(R.id.project_end_date)).setShowSoftInputOnFocus(false);

        updateProjectNewView(view, projectDto);
        return view;
    }

    private ProjectDto readProjectFromBundle() {
        Long projectId = getArguments().getLong(ProjectListFragment.PROJECT_ID_KEY);
        boolean isProjectReadOnly = getArguments().getBoolean(ProjectListFragment.PROJECT_READ_ONLY_KEY);
        ProjectDto projectDto = projectService.getProject(projectId);
        if (projectDto == null) {
            // this was a project new request, return a empty project object where only client id is set
            projectDto = new ProjectDto(getArguments().getLong(ClientListFragment.CLIENT_ID_KEY));
        }
        return projectDto;
    }

    private void returnToProjectList(Long clientId) {
        Bundle bundle = new Bundle();
        bundle.putLong(ClientListFragment.CLIENT_ID_KEY, clientId);
        navigateTo(R.id.nav_from_project_new_to_project_list, bundle);
    }

    private void updateProjectNewView(View view, @NotNull ProjectDto projectDto) {
        Log.d("update project add view", projectDto.toString());
        if (projectDto.getClientDto() == null) {
            throw new TerexApplicationException("client is null!", "client is null!", null);
        }
        // hidden fields
        ((TextView) view.findViewById(R.id.project_client_id)).setText(String.valueOf(projectDto.getClientDto().getId()));
        ((TextView) view.findViewById(R.id.project_project_id)).setText(String.valueOf(projectDto.getId()));

        // none editable field, i.e, primary key or part of primary key
        ((TextView) view.findViewById(R.id.project_name)).setText(projectDto.getName());

        ((TextView) view.findViewById(R.id.project_created_date)).setText(Utility.formatDateTime(projectDto.getCreatedDate()));
        ((TextView) view.findViewById(R.id.project_last_modified_date)).setText(Utility.formatDateTime(projectDto.getLastModifiedDate()));

        ((TextView) view.findViewById(R.id.project_name)).setText(projectDto.getName());
        ((TextView) view.findViewById(R.id.project_description)).setText(projectDto.getDescription());

        ((TextView) view.findViewById(R.id.project_start_date)).setText(Utility.formatDate(projectDto.getStartDate()));
        ((TextView) view.findViewById(R.id.project_end_date)).setText(Utility.formatDate(projectDto.getEndDate()));

        ((TextView) view.findViewById(R.id.project_hourly_rate)).setText(String.format("%s", projectDto.getHourlyRate() != null ? projectDto.getHourlyRate() : ""));

        ((MaterialButton) view.findViewById(R.id.project_new_status_btn_active)).setChecked(projectDto.isActive());
        ((MaterialButton) view.findViewById(R.id.project_new_status_btn_closed)).setChecked(projectDto.isClosed());

        // hide fields if this is a new
        if (projectDto.isNew()) {
            view.findViewById(R.id.project_name).setEnabled(true);
            view.findViewById(R.id.project_date_layout).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.project_created_date_layout).setVisibility(View.GONE);
            view.findViewById(R.id.project_last_modified_date_layout).setVisibility(View.GONE);
            view.findViewById(R.id.project_delete_btn).setVisibility(View.GONE);
        } else if (projectDto.isClosed()) {
            // project locked, not able to edit anymore

            view.findViewById(R.id.project_created_date).setEnabled(false);
            view.findViewById(R.id.project_last_modified_date).setEnabled(false);
            view.findViewById(R.id.project_name).setEnabled(false);
            view.findViewById(R.id.project_description).setEnabled(false);
            view.findViewById(R.id.project_hourly_rate).setEnabled(false);
            view.findViewById(R.id.project_start_date).setEnabled(false);
            view.findViewById(R.id.project_end_date).setEnabled(false);
            //((MaterialButton) view.findViewById(R.id.project_new_status_btn_active)).setCheckable(false);
            //((MaterialButton) view.findViewById(R.id.project_new_status_btn_closed)).setCheckable(false);

            // hide buttons
            //view.findViewById(R.id.project_delete_btn).setVisibility(View.GONE);
            //view.findViewById(R.id.project_save_btn).setVisibility(View.GONE);
        } else {
            // never allowed to edit project name
            view.findViewById(R.id.project_name).setEnabled(false);
        }
        Log.d(Utility.buildTag(getClass(), "updateProjectAddView"), String.format("updated %s ", projectDto));
    }

    @Override
    public void onClick(View view) {
        // ask every time
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_MEDIA_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // You have not been granted access, ask for permission now, no need for nay permissions
            Log.d(Utility.buildTag(getClass(), "onClick"), "save button, permission not granted");
        }

        int id = view.getId();
        if (id == R.id.project_save_btn) {
            Log.d(Utility.buildTag(getClass(), "onClick"), "save button, save entry");
        } else if (id == R.id.project_cancel_btn) {
            // return back to main view
            Log.d(Utility.buildTag(getClass(), "onClick"), "cancel button, return back to client list view");
        }
    }

    private ProjectDto getProjectDtoData() {
        ProjectDto projectDto = new ProjectDto();

        TextView clientIdView = requireView().findViewById(R.id.project_client_id);
        projectDto.setClientDto(new ClientDto(Utility.isInteger(clientIdView.getText().toString()) ? Long.parseLong(clientIdView.getText().toString()) : null));

        TextView idView = requireView().findViewById(R.id.project_project_id);
        projectDto.setId(Utility.isInteger(idView.getText().toString()) ? Long.parseLong(idView.getText().toString()) : null);

        EditText createdDateView = requireView().findViewById(R.id.project_created_date);
        LocalDateTime createdDateTime = Utility.toLocalDateTime(createdDateView.getText().toString());
        if (createdDateTime != null) {
            projectDto.setCreatedDate(createdDateTime);
        }

        EditText lastModifiedDateView = requireView().findViewById(R.id.project_last_modified_date);
        LocalDateTime lastModifiedDate = Utility.toLocalDateTime(lastModifiedDateView.getText().toString());
        if (lastModifiedDate != null) {
            projectDto.setLastModifiedDate(lastModifiedDate);
        }

        EditText startDateDateView = requireView().findViewById(R.id.project_start_date);
        LocalDate startDateTime = Utility.toLocalDate(startDateDateView.getText().toString());
        if (startDateTime != null) {
            projectDto.setStartDate(startDateTime);
        }

        EditText endDateDateView = requireView().findViewById(R.id.project_end_date);
        LocalDate endDateTime = Utility.toLocalDate(endDateDateView.getText().toString());
        if (endDateTime != null) {
            projectDto.setEndDate(endDateTime);
        }

        projectDto.setName(((TextView) requireView().findViewById(R.id.project_name)).getText().toString());
        projectDto.setDescription(((TextView) requireView().findViewById(R.id.project_description)).getText().toString());
        projectDto.setHourlyRate(Integer.valueOf(((TextView) requireView().findViewById(R.id.project_hourly_rate)).getText().toString()));

        if (((MaterialButton) requireView().findViewById(R.id.project_new_status_btn_active)).isChecked()) {
            projectDto.setStatus(Project.ProjectStatusEnum.ACTIVE.name());
        } else if (((MaterialButton) requireView().findViewById(R.id.project_new_status_btn_closed)).isChecked()) {
            projectDto.setStatus(Project.ProjectStatusEnum.CLOSED.name());
        }
        return projectDto;
    }

    private void saveProject() {
        try {
            ProjectDto projectDto = getProjectDtoData();
            projectService.saveProject(projectDto);
            showSnackbar(String.format("Added new project! %s", projectDto.getName()), R.color.color_snackbar_text_add);
            returnToProjectList(projectDto.getClientDto().getId());
        } catch (TerexApplicationException | InputValidationException ex) {
            showInfoDialog("Error", String.format("Failed save project! error=%s", ex.getMessage()));
        } catch (Exception e) {
            showInfoDialog("Error", String.format("Failed save project! error=%s", e.getCause()));
        }
    }

    private void deleteProject() {
        try {
            ProjectDto projectDto = getProjectDtoData();
            projectService.deleteProject(projectDto.getId());
            showSnackbar(String.format("Deleted new project! %s", projectDto.getName()), R.color.color_snackbar_text_delete);
            returnToProjectList(projectDto.getClientDto().getId());
        } catch (TerexApplicationException | InputValidationException ex) {
            showInfoDialog("Error", String.format("Failed delete project! error=%s", ex.getMessage()));
        } catch (Exception e) {
            showInfoDialog("Error", String.format("Failed delete project! error=%s", e.getCause()));
        }
    }


    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        TextInputEditText dateTextView;

        public DatePickerFragment(TextInputEditText view) {
            this.dateTextView = view;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker.
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it.
            return new DatePickerDialog(requireContext(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date the user picks.
            Log.d("", "");
            dateTextView.setText(Utility.formatDate(LocalDate.of(year, month + 1, day)));
        }
    }
}
