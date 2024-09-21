package com.gunnarro.android.terex.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentResultListener;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.gunnarro.android.terex.R;
import com.gunnarro.android.terex.domain.dto.ClientDto;
import com.gunnarro.android.terex.domain.dto.ProjectDto;
import com.gunnarro.android.terex.exception.InputValidationException;
import com.gunnarro.android.terex.exception.TerexApplicationException;
import com.gunnarro.android.terex.ui.adapter.ProjectListAdapter;
import com.gunnarro.android.terex.ui.listener.ListOnItemClickListener;
import com.gunnarro.android.terex.ui.swipe.SwipeCallback;
import com.gunnarro.android.terex.ui.view.ProjectViewModel;
import com.gunnarro.android.terex.utility.Utility;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProjectListFragment extends BaseFragment implements ListOnItemClickListener {
    public static final String PROJECT_REQUEST_KEY = "project_request";
    public static final String PROJECT_ID_KEY = "project_id";
    public static final String PROJECT_ACTION_KEY = "project_action";
    public static final String PROJECT_ACTION_VIEW = "project_view";
    public static final String PROJECT_ACTION_EDIT = "project_edit";
    public static final String PROJECT_ACTION_SAVE = "project_save";
    public static final String PROJECT_ACTION_DELETE = "project_delete";
    public static final String PROJECT_JSON_KEY = "project_json";
    public static final String PROJECT_READ_ONLY_KEY = "project_read_only";
    private ProjectViewModel projectViewModel;
    private Long clientId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        clientId = getArguments().getLong(ClientListFragment.CLIENT_ID_KEY);
        if (clientId == null) {
            throw new TerexApplicationException("project list error, client id is not set", "50555", null);
        }
        Log.d("ProjectListFragment", "clientId=" + clientId);
        // Get a new or existing ViewModel from the ViewModelProvider.
        projectViewModel = new ProjectViewModel(requireActivity().getApplication(), clientId);

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
        requireActivity().setTitle(R.string.title_projects);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recycler_project_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.project_list_recyclerview);
        final ProjectListAdapter adapter = new ProjectListAdapter(this, new ProjectListAdapter.ProjectDtoDiff());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (getArguments() == null || getArguments().getLong(ClientListFragment.CLIENT_ID_KEY) == 0) {
            // client id must be provided, if not, return
            throw new TerexApplicationException("Missing client id!", "50023", null);
        }

        projectViewModel.getProjectsLiveData(clientId).observe(requireActivity(), adapter::submitList);

        FloatingActionButton addButton = view.findViewById(R.id.project_list_add_btn);
        addButton.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            ProjectDto projectDto = new ProjectDto();
            projectDto.setClientDto(new ClientDto(clientId));
            bundle.putString(PROJECT_JSON_KEY, Utility.gsonMapper().toJson(projectDto, ProjectDto.class));
            bundle.putLong(ClientListFragment.CLIENT_ID_KEY, clientId);
            navigateTo(R.id.nav_from_project_list_to_project_new, bundle);
        });

        enableSwipeToLeftAndDeleteItem(recyclerView);
        enableSwipeToRightAndViewItem(recyclerView);
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

    private void handleProjectActions(String projectJsonDtoJson, String action) {
        Log.d(Utility.buildTag(getClass(), "handleProjectActions"), String.format("action: %s, project: %s", action, projectJsonDtoJson));
        try {
            ProjectDto projectDto = Utility.gsonMapper().fromJson(projectJsonDtoJson, ProjectDto.class);
            if (PROJECT_ACTION_SAVE.equals(action)) {
                projectViewModel.saveProject(projectDto);
                showSnackbar(String.format("saved %s", projectDto.getName()), R.color.color_snackbar_text_add);
            } else if (PROJECT_ACTION_DELETE.equals(action)) {
                if (projectDto.getStatus().equals("ACTIVE")) {
                    showInfoDialog("Info", "Can not delete project with status ACTIVE");
                } else {
                    confirmDeleteProjectDialog(getString(R.string.msg_delete_timesheet), getString(R.string.msg_confirm_delete), projectDto.getId());
                }
            } else if (PROJECT_ACTION_VIEW.equals(action)) {
                // redirect to timesheet entry list fragment
                Bundle bundle = new Bundle();
                bundle.putLong(PROJECT_ID_KEY, projectDto.getId());
                bundle.putBoolean(PROJECT_READ_ONLY_KEY, projectDto.isClosed());
                // openClientProjectListView(bundle);
            } else if (PROJECT_ACTION_EDIT.equals(action)) {
                // redirect to timesheet entry list fragment
                Bundle bundle = new Bundle();
                bundle.putString(PROJECT_JSON_KEY, projectJsonDtoJson);
                bundle.putBoolean(PROJECT_READ_ONLY_KEY, projectDto.isClosed());
                //openClientDetailsView(bundle);
            } else {
                Log.w(Utility.buildTag(getClass(), "handleProjectActions"), "unknown action: " + action);
                showInfoDialog("Info", String.format("Application error!%s Unknown action: %s%s Please report.", action, System.lineSeparator(), System.lineSeparator()));
            }
        } catch (TerexApplicationException | InputValidationException ex) {
            showInfoDialog("Info", String.format("Error handling project action! action=%s, error=%s", action, ex.getMessage()));
        } catch (Exception e) {
            showInfoDialog("Error", String.format("Error handling project action! action=%s, error= %s", action, e.getCause()));
        }
    }


    private void enableSwipeToRightAndViewItem(RecyclerView recyclerView) {
        SwipeCallback swipeToViewCallback = new SwipeCallback(requireContext(), ItemTouchHelper.RIGHT, getResources().getColor(R.color.color_bg_swipe_right, null), R.drawable.ic_add_black_24dp) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final int selectedProjectPos = viewHolder.getAbsoluteAdapterPosition();
                ProjectDto projectDto = projectViewModel.getProjectsLiveData(clientId).getValue().get(selectedProjectPos);
                Bundle bundle = new Bundle();
                bundle.putLong(PROJECT_ID_KEY, projectDto.getId());
                bundle.putBoolean(PROJECT_READ_ONLY_KEY, projectDto.isClosed());
                navigateTo(R.id.project_new_fragment, bundle);
            }
        };
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToViewCallback);
        itemTouchhelper.attachToRecyclerView(recyclerView);
        Log.i(Utility.buildTag(getClass(), "enableSwipeToRightAndAdd"), "enabled swipe handler for timesheet list item");
    }

    private void enableSwipeToLeftAndDeleteItem(RecyclerView recyclerView) {
        SwipeCallback swipeToDeleteCallback = new SwipeCallback(requireContext(), ItemTouchHelper.LEFT, getResources().getColor(R.color.color_bg_swipe_left, null), R.drawable.ic_delete_black_24dp) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final int selectedProjectPos = viewHolder.getAbsoluteAdapterPosition();
                ProjectDto projectDto = projectViewModel.getProjectsLiveData(clientId).getValue().get(selectedProjectPos);
                confirmDeleteProjectDialog(getString(R.string.msg_delete_timesheet), getString(R.string.msg_confirm_delete), projectDto.getId());
            }
        };
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(recyclerView);
        Log.i(Utility.buildTag(getClass(), "enableSwipeToLeftAndDeleteItem"), "enabled swipe handler for delete timesheet list item");
    }

    private void deleteProject(Long projectId) {
        try {
            projectViewModel.deleteProject(projectId);
            RecyclerView recyclerView = requireView().findViewById(R.id.timesheet_entry_list_recyclerview);
            recyclerView.getAdapter().notifyDataSetChanged();
            showSnackbar(String.format(getResources().getString(R.string.info_timesheet_list_delete_msg_format), "project"), R.color.color_snackbar_text_delete);
        } catch (TerexApplicationException | InputValidationException e) {
            showInfoDialog("Info", e.getMessage());
        }
    }

    private void confirmDeleteProjectDialog(final String title, final String message, final Long projectId) {
        new MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.btn_ok, (dialogInterface, i) -> deleteProject(projectId))
                .setNeutralButton(R.string.btn_cancel, (dialogInterface, i) -> {
                    // nothing to do
                })
                .show();
    }

    @Override
    public void onItemClick(Bundle bundle) {
        //navigateTo(R.id.nav_from_client_list_to_client_details, bundle);
    }
}
