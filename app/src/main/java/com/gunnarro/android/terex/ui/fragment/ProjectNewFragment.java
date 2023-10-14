package com.gunnarro.android.terex.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;
import com.gunnarro.android.terex.R;
import com.gunnarro.android.terex.domain.entity.Project;
import com.gunnarro.android.terex.domain.entity.SpinnerItem;
import com.gunnarro.android.terex.exception.TerexApplicationException;
import com.gunnarro.android.terex.repository.ProjectRepository;
import com.gunnarro.android.terex.service.ProjectService;
import com.gunnarro.android.terex.utility.Utility;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProjectNewFragment extends Fragment {

    // @Inject
    private ProjectService projectService;

    @Inject
    public ProjectNewFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requireActivity().setTitle(R.string.title_project);
        setHasOptionsMenu(true);
        projectService = new ProjectService(requireActivity().getApplicationContext());
        Log.d(Utility.buildTag(getClass(), "onCreate"), "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_project_new, container, false);
        List<Project> projectList = projectService.getProjects(ProjectRepository.ProjectStatusEnum.ACTIVE.name());
        List<SpinnerItem> projectItems = projectList.stream().map(p -> new SpinnerItem(p.id, p.getName())).collect(Collectors.toList());
        final AutoCompleteTextView projectSpinner = view.findViewById(R.id.project_new_project_name_spinner);
        ArrayAdapter<SpinnerItem> projectAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, projectItems);
        projectSpinner.setAdapter(projectAdapter);
        projectSpinner.setListSelection(0);

        view.findViewById(R.id.btn_project_new_create).setOnClickListener(v -> {
            try {
                String selectedProject = projectSpinner.getText().toString();
                SpinnerItem item = projectItems.stream().filter(i -> i.name().equals(selectedProject)).findFirst().orElse(null);
                if (item == null) {
                   // showInfoDialog("Please select a project!", requireContext());
                } else {
                   // Invoice invoice = createInvoice(item.id());
                }
            } catch (TerexApplicationException e) {
                //showInfoDialog("Error creating project!", requireContext());
            }
        });

        view.findViewById(R.id.btn_project_new_delete).setOnClickListener(v -> {
            try {

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        view.findViewById(R.id.btn_project_new_cancel).setOnClickListener(v -> {
            view.findViewById(R.id.btn_project_new_cancel).setBackgroundColor(getResources().getColor(R.color.color_btn_bg_cancel, view.getContext().getTheme()));
            // Simply return back to project list
            NavigationView navigationView = requireActivity().findViewById(R.id.navigationView);
            requireActivity().onOptionsItemSelected(navigationView.getMenu().findItem(R.id.nav_invoice_list));
            returnToProjectList();
        });

        Log.d(Utility.buildTag(getClass(), "onCreateView"), "");
        return view;
    }

    /**
     * Update backup info after view is successfully create
     */
    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * Create a new invoice for the given timesheet.
     *
     * @param timesheetId to be created invoice for
     * @return id of the invoice
     */
    private void createProject(Long timesheetId) {

    }


    private void returnToProjectList() {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, InvoiceListFragment.class, null)
                .setReorderingAllowed(true)
                .commit();
    }

}
