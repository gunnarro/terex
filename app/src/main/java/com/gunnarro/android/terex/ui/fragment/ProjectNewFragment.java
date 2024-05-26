package com.gunnarro.android.terex.ui.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.gunnarro.android.terex.R;
import com.gunnarro.android.terex.domain.dto.ProjectDto;
import com.gunnarro.android.terex.domain.entity.Project;
import com.gunnarro.android.terex.exception.InputValidationException;
import com.gunnarro.android.terex.exception.TerexApplicationException;
import com.gunnarro.android.terex.service.ProjectService;
import com.gunnarro.android.terex.utility.Utility;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProjectNewFragment extends BaseFragment implements View.OnClickListener {

    private ProjectService projectService;

    @Inject
    public ProjectNewFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requireActivity().setTitle(R.string.title_project_new);
        projectService = new ProjectService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requireActivity().setTitle(R.string.title_register_work);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_project_new, container, false);

        // create status spinner
        final AutoCompleteTextView statusSpinner = view.findViewById(R.id.project_status_spinner);
        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.timesheet_statuses, android.R.layout.simple_spinner_item);
        statusSpinner.setAdapter(statusAdapter);
        statusSpinner.setListSelection(0);

        // disable save button as default
        view.findViewById(R.id.project_save_btn).setEnabled(true);
        view.findViewById(R.id.project_save_btn).setOnClickListener(v -> {

          /*  view.findViewById(R.id.client_project_save_btn).setBackgroundColor(getResources().getColor(R.color.color_btn_bg_cancel, view.getContext().getTheme()));
            Bundle result = new Bundle();
            result.putString(ProjectListFragment.PROJECT_JSON_KEY, getProjectAsJson());
            result.putString(ProjectListFragment.PROJECT_ACTION_KEY, ProjectListFragment.PROJECT_ACTION_SAVE);
            getParentFragmentManager().setFragmentResult(ProjectListFragment.PROJECT_REQUEST_KEY, result);
            Log.d(Utility.buildTag(getClass(), "onCreateView"), "add new project intent: " + getProjectAsJson());

           */
            saveProject();
            returnToProjectList(getArguments().getLong(ClientListFragment.CLIENT_ID_KEY));
        });

        view.findViewById(R.id.project_delete_btn).setOnClickListener(v -> {
            view.findViewById(R.id.project_delete_btn).setBackgroundColor(getResources().getColor(R.color.color_btn_bg_cancel, view.getContext().getTheme()));
            Bundle result = new Bundle();
            result.putString(ProjectListFragment.PROJECT_JSON_KEY, getProjectAsJson());
            result.putString(ProjectListFragment.PROJECT_ACTION_KEY, ProjectListFragment.PROJECT_ACTION_DELETE);
            getParentFragmentManager().setFragmentResult(ProjectListFragment.PROJECT_REQUEST_KEY, result);
            Log.d(Utility.buildTag(getClass(), "onCreateView"), "add new delete item intent");
            returnToProjectList(getArguments().getLong(ClientListFragment.CLIENT_ID_KEY));
        });

        view.findViewById(R.id.project_cancel_btn).setOnClickListener(v -> {
            view.findViewById(R.id.project_cancel_btn).setBackgroundColor(getResources().getColor(R.color.color_btn_bg_cancel, view.getContext().getTheme()));
            returnToProjectList(getArguments().getLong(ClientListFragment.CLIENT_ID_KEY));
        });

        updateProjectNewView(view, readProjectFromBundle());
        return view;
    }

    private Project readProjectFromBundle() {
        String projectJson = getArguments() != null ? getArguments().getString(ProjectListFragment.PROJECT_JSON_KEY) : null;
        Log.d("received project", "" + projectJson);
        if (projectJson != null && !projectJson.isEmpty()) {
            try {
                Project project = Utility.gsonMapper().fromJson(projectJson, Project.class);
                Log.d(Utility.buildTag(getClass(), "onFragmentResult"), String.format("project: %s", project));
                return project;
            } catch (Exception e) {
                Log.e("", e.toString());
                throw new TerexApplicationException("Application Error!", "5000", e);
            }
        } else {
            // no recent timesheet entry found, should not happen
            throw new TerexApplicationException("Project not found!", "55023", null);
        }
    }

    private void returnToProjectList(Long clientId) {
        Bundle bundle = new Bundle();
        bundle.putLong(ClientListFragment.CLIENT_ID_KEY, clientId);
        navigateTo(R.id.nav_from_project_new_to_project_list, bundle);
    }

    private void updateProjectNewView(View view, @NotNull Project project) {
        Log.d("update project add view", project.toString());
        if (project.getClientId() == null) {
            throw new TerexApplicationException("clientId is null!", "clientId is null!", null);
        }

        TextView clientId = view.findViewById(R.id.project_client_id);
        clientId.setText(String.valueOf(project.getClientId()));

        TextView id = view.findViewById(R.id.project_project_id);
        id.setText(String.valueOf(project.getId()));

        EditText createdDateView = view.findViewById(R.id.project_created_date);
        createdDateView.setText(Utility.formatDateTime(project.getCreatedDate()));

        EditText lastModifiedDateView = view.findViewById(R.id.project_last_modified_date);
        lastModifiedDateView.setText(Utility.formatDateTime(project.getLastModifiedDate()));

        AutoCompleteTextView statusSpinner = view.findViewById(R.id.project_status_spinner);
        statusSpinner.setText(Project.ProjectStatusEnum.ACTIVE.name()); //FIXME

        EditText hourlyRateView = view.findViewById(R.id.project_hourly_rate);
        hourlyRateView.setText(String.format("%s", project.getHourlyRate()));

        EditText workdayYearView = view.findViewById(R.id.project_description);
        workdayYearView.setText(String.format("%s", project.getDescription()));

        // hide fields if this is a new
        if (project.getId() == null) {
            view.findViewById(R.id.project_date_layout).setVisibility(View.GONE);
            view.findViewById(R.id.project_delete_btn).setVisibility(View.GONE);
        } else if (!project.getStatus().equals(Project.ProjectStatusEnum.ACTIVE.name())) {
            // project locked, not able to edit anymore
            createdDateView.setEnabled(false);
            lastModifiedDateView.setEnabled(false);
        }
        Log.d(Utility.buildTag(getClass(), "updateProjectAddView"), String.format("updated %s ", project));
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

    private ProjectDto getProjectData() {
        ProjectDto projectDto = new ProjectDto();

        TextView clientIdView = requireView().findViewById(R.id.project_client_id);
        projectDto.setClientId(Utility.isInteger(clientIdView.getText().toString()) ? Long.parseLong(clientIdView.getText().toString()) : null);

        TextView idView = requireView().findViewById(R.id.project_project_id);
        projectDto.setId(Utility.isInteger(idView.getText().toString()) ? Long.parseLong(idView.getText().toString()) : null);

        EditText createdDateView = requireView().findViewById(R.id.project_created_date);
        LocalDateTime createdDateTime = Utility.toLocalDateTime(createdDateView.getText().toString());
        if (createdDateTime != null) {
            //projectDto.setCreatedDate(createdDateTime);
        }

        EditText lastModifiedDateView = requireView().findViewById(R.id.project_last_modified_date);
        LocalDateTime lastModifiedDateTime = Utility.toLocalDateTime(lastModifiedDateView.getText().toString());
        if (lastModifiedDateTime != null) {
            //projectDto.setLastModifiedDate(lastModifiedDateTime);
        }
        projectDto.setName(((TextView) requireView().findViewById(R.id.project_name)).getText().toString());
        projectDto.setDescription(((TextView) requireView().findViewById(R.id.project_description)).getText().toString());
        projectDto.setHourlyRate( Integer.valueOf(((TextView) requireView().findViewById(R.id.project_hourly_rate)).getText().toString()));
        projectDto.setStatus(((AutoCompleteTextView) requireView().findViewById(R.id.project_status_spinner)).getText().toString());
        return projectDto;
    }

    private String getProjectAsJson() {
        try {
            return Utility.gsonMapper().toJson(getProjectData());
        } catch (Exception e) {
            Log.e("getProjectAsJson", e.toString());
            throw new RuntimeException("unable to parse object to json! " + e);
        }
    }

    private void saveProject() {
        try {
            ProjectDto projectDto = getProjectData();
            projectService.saveProject(projectDto);
            showSnackbar(String.format("dded new project! %s", projectDto.getName()), R.color.color_snackbar_text_add);
        } catch (TerexApplicationException | InputValidationException ex) {
            ex.printStackTrace();
            showInfoDialog("Error", String.format("%s", ex.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            showInfoDialog("Error", String.format("%s", e.getCause()));
        }
    }
}
