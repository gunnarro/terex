package com.gunnarro.android.terex.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.gunnarro.android.terex.R;
import com.gunnarro.android.terex.domain.dto.IntegrationDto;
import com.gunnarro.android.terex.exception.TerexApplicationException;
import com.gunnarro.android.terex.service.IntegrationService;
import com.gunnarro.android.terex.ui.MainActivity;
import com.gunnarro.android.terex.utility.Utility;

import org.jetbrains.annotations.NotNull;

import java.io.File;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class IntegrationDetailsFragment extends BaseFragment {

    private final IntegrationService integrationService;

    @Inject
    public IntegrationDetailsFragment() {
        // Needed by dagger framework
        integrationService = new IntegrationService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        requireActivity().setTitle(R.string.title_integration_details);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_integration_details, container, false);
        final IntegrationDto integrationDto = readIntegrationFromBundle();

        view.findViewById(R.id.integration_details_save_btn).setEnabled(true);
        view.findViewById(R.id.integration_details_save_btn).setOnClickListener(v -> {
            view.findViewById(R.id.integration_details_save_btn).setBackgroundColor(getResources().getColor(R.color.color_btn_bg_cancel, view.getContext().getTheme()));
            //saveIntegration();
            returnToIntegrationList();
        });

        view.findViewById(R.id.integration_details_delete_btn).setOnClickListener(v -> {
            view.findViewById(R.id.integration_details_delete_btn).setBackgroundColor(getResources().getColor(R.color.color_btn_bg_default, view.getContext().getTheme()));
            Bundle bundle = new Bundle();
            bundle.putLong(IntegrationListFragment.INTEGRATION_ID_KEY, integrationDto.getId());
            bundle.putString(IntegrationListFragment.INTEGRATION_ACTION_KEY, IntegrationListFragment.INTEGRATION_ACTION_DELETE);
            getParentFragmentManager().setFragmentResult(IntegrationListFragment.INTEGRATION_REQUEST_KEY, bundle);
            returnToIntegrationList();
        });

        view.findViewById(R.id.integration_details_cancel_btn).setOnClickListener(v -> {
            view.findViewById(R.id.integration_details_cancel_btn).setBackgroundColor(getResources().getColor(R.color.color_btn_bg_default, view.getContext().getTheme()));
            returnToIntegrationList();
        });

        updateView(view, integrationDto);
        return view;
    }

    private void updateView(View view, IntegrationDto integrationDto) {
        Log.d("update integration details view", integrationDto.toString());
        // hidden fields
        ((TextView) view.findViewById(R.id.integration_details_integration_id)).setText(String.valueOf(integrationDto.getId()));

        // none editable field, i.e, primary key or part of primary key
        ((TextView) view.findViewById(R.id.integration_details_system)).setText(integrationDto.getSystem());
        // check if this is a new integration
        if (integrationDto.getId() != null) {
            view.findViewById(R.id.integration_details_system).setEnabled(false);
        }

        if (integrationDto.getCreatedDate() != null) {
            ((TextView) view.findViewById(R.id.project_created_date)).setText(Utility.formatDateTime(integrationDto.getCreatedDate()));
        }
        if (integrationDto.getLastModifiedDate() != null) {
            ((TextView) view.findViewById(R.id.project_last_modified_date)).setText(Utility.formatDateTime(integrationDto.getLastModifiedDate()));
        }

        if (integrationDto.getBaseUrl() != null) {
            ((TextView) view.findViewById(R.id.integration_base_url)).setText(integrationDto.getBaseUrl());
        }

        ((MaterialButton) view.findViewById(R.id.integration_details_status_btn_active)).setChecked(integrationDto.isActive());
        ((MaterialButton) view.findViewById(R.id.integration_details_status_btn_deactivate)).setChecked(integrationDto.isDeActivated());

        // hide fields if this is a new
        if (integrationDto.getId() == null) {
            view.findViewById(R.id.integration_details_date_layout).setVisibility(View.GONE);
            view.findViewById(R.id.integration_details_delete_btn).setVisibility(View.GONE);
        } else if (!integrationDto.isDeActivated()) {
            // project locked, not able to edit anymore
            view.findViewById(R.id.integration_details_created_date).setEnabled(false);
            view.findViewById(R.id.integration_details_last_modified_date).setEnabled(false);
        }
        Log.d(Utility.buildTag(getClass(), "updateView"), String.format("updated %s ", integrationDto));
    }

    private void returnToIntegrationList() {
        navigateTo(R.id.nav_from_integration_details_to_integration_list, null);
    }

    private IntegrationDto readIntegrationFromBundle() {
        Long integrationId = getArguments().getLong(IntegrationListFragment.INTEGRATION_ID_KEY);
        boolean isProjectReadOnly = getArguments().getBoolean(IntegrationListFragment.INTEGRATION_READ_ONLY_KEY);
        IntegrationDto integrationDto = integrationService.getIntegration(integrationId);
        if (integrationDto == null) {
            // this was a integration new request, create a default integration object
            integrationDto = new IntegrationDto();
        }
        return integrationDto;
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }


    private File readFile(String fileName) {
        try {
            return new File(MainActivity.getInternalAppDir(), fileName);
        } catch (Exception e) {
            throw new TerexApplicationException(e.getMessage(), "50500", e);
        }
    }
}
