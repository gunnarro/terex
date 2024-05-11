package com.gunnarro.android.terex.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.gunnarro.android.terex.R;
import com.gunnarro.android.terex.domain.entity.Project;
import com.gunnarro.android.terex.exception.InputValidationException;
import com.gunnarro.android.terex.exception.TerexApplicationException;
import com.gunnarro.android.terex.repository.ProjectRepository;
import com.gunnarro.android.terex.ui.adapter.ProjectListAdapter;
import com.gunnarro.android.terex.ui.listener.ListOnItemClickListener;
import com.gunnarro.android.terex.ui.view.ProjectViewModel;
import com.gunnarro.android.terex.utility.Utility;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProjectListFragment extends BaseFragment implements ListOnItemClickListener {
    public static final String PROJECT_REQUEST_KEY = "800";
    public static final String PROJECT_ID_KEY = "project_id";
    public static final String PROJECT_ACTION_KEY = "project_action";
    public static final String PROJECT_ACTION_VIEW = "project_view";
    public static final String PROJECT_ACTION_EDIT = "project_edit";
    public static final String PROJECT_ACTION_SAVE = "project_save";
    public static final String PROJECT_ACTION_DELETE = "project_delete";
    public static final String PROJECT_JSON_KEY = "project_json";
    private ProjectViewModel projectViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requireActivity().setTitle(R.string.title_projects);
        // Get a new or existing ViewModel from the ViewModelProvider.
        projectViewModel = new ViewModelProvider(this).get(ProjectViewModel.class);

        getParentFragmentManager().setFragmentResultListener(PROJECT_REQUEST_KEY, this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                Log.d(Utility.buildTag(getClass(), "onFragmentResult"), "requestKey: " + requestKey + ", bundle: " + bundle);
                handleFragmentResult(bundle);
            }
        });
        Log.d(Utility.buildTag(getClass(), "onCreate"), "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recycler_project_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.project_list_recyclerview);
        final ProjectListAdapter adapter = new ProjectListAdapter(this, new ProjectListAdapter.ProjectDiff());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (getArguments() == null || getArguments().getLong(ClientListFragment.CLIENT_ID_KEY) == 0) {
            // client id must be provided, if not, return
            throw new TerexApplicationException("Missing client id!", "50023", null);
        }

        Long clientId = getArguments().getLong(ClientListFragment.CLIENT_ID_KEY);
        projectViewModel.getProjectLiveData(clientId, ProjectRepository.ProjectStatusEnum.ACTIVE).observe(requireActivity(), adapter::submitList);

        FloatingActionButton addButton = view.findViewById(R.id.project_list_add_btn);
        addButton.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            Project project = new Project();
            project.setClientId(clientId);
            bundle.putString(PROJECT_JSON_KEY, Utility.gsonMapper().toJson(project, Project.class));
            bundle.putLong(ClientListFragment.CLIENT_ID_KEY, clientId);
            navigateTo(R.id.nav_from_project_list_to_project_new, bundle);
        });

        Log.d(Utility.buildTag(getClass(), "onCreateView"), "");
        return view;
    }

    private void handleFragmentResult(Bundle bundle) {
        if (bundle == null) {
            return;
        }
        if (bundle.getString(PROJECT_ACTION_KEY) != null) {
            handleProjectActions(bundle.getString(PROJECT_JSON_KEY), bundle.getString(PROJECT_ACTION_KEY));
        } else {
            Log.w("unknown action!", "unknown action: " + bundle);
        }
    }

    private void handleProjectActions(String projectJson, String action) {
        Log.d(Utility.buildTag(getClass(), "handleProjectActions"), String.format("action: %s, project: %s", action, projectJson));
        try {
            Project project = Utility.gsonMapper().fromJson(projectJson, Project.class);
            if (PROJECT_ACTION_SAVE.equals(action)) {
               // projectViewModel.saveProject(project);
                showSnackbar(String.format("saved %s", project.getName()), R.color.color_snackbar_text_add);
            } else if (PROJECT_ACTION_DELETE.equals(action)) {
                if (project.getStatus().equals("ACTIVE")) {
                    showInfoDialog("Info", "Can not delete client with status ACTIVE");
                } else {
                    //FIXME confirmDeleteClientDialog(getString(R.string.msg_delete_timesheet), getString(R.string.msg_confirm_delete), client.getId());
                }
            } else if (PROJECT_ACTION_VIEW.equals(action)) {
                // redirect to timesheet entry list fragment
                Bundle bundle = new Bundle();
                bundle.putLong(PROJECT_ID_KEY, project.getId());
                //bundle.putBoolean(CLIENT_READ_ONLY_KEY, timesheet.isBilled());
                // FIXME openClientProjectListView(bundle);
            } else if (PROJECT_ACTION_EDIT.equals(action)) {
                // redirect to timesheet entry list fragment
                Bundle bundle = new Bundle();
                bundle.putString(PROJECT_JSON_KEY, projectJson);
                //bundle.putBoolean(CLIENT_READ_ONLY_KEY, timesheet.isBilled());
                // FIXME openClientDetailsView(bundle);
            } else {
                Log.w(Utility.buildTag(getClass(), "handleProjectActions"), "unknown action: " + action);
                showInfoDialog("Info", String.format("Application error!%s Unknown action: %s%s Please report.", action, System.lineSeparator(), System.lineSeparator()));
            }
        } catch (TerexApplicationException | InputValidationException ex) {
            showInfoDialog("Info", String.format("%s", ex.getMessage()));
        } catch (Exception e) {
            showInfoDialog("Error", String.format("%s", e.getCause()));
        }
    }

    @Override
    public void onItemClick(Bundle bundle) {
        //  getNavController().navigate(R.id.nav_from_client_list_to_client_details, bundle);
    }
}
