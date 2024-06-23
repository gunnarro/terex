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

import com.gunnarro.android.terex.R;
import com.gunnarro.android.terex.domain.entity.Integration;
import com.gunnarro.android.terex.exception.InputValidationException;
import com.gunnarro.android.terex.exception.TerexApplicationException;
import com.gunnarro.android.terex.ui.adapter.IntegrationListAdapter;
import com.gunnarro.android.terex.ui.listener.ListOnItemClickListener;
import com.gunnarro.android.terex.ui.view.IntegrationViewModel;
import com.gunnarro.android.terex.utility.Utility;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class IntegrationListFragment extends BaseFragment implements ListOnItemClickListener {
    public static final String INTEGRATION_REQUEST_KEY = "integration_request";
    public static final String INTEGRATION_ID_KEY = "integration_id";
    public static final String INTEGRATION_ACTION_KEY = "integration_action";
    public static final String INTEGRATION_ACTION_VIEW = "integration_view";
    public static final String INTEGRATION_ACTION_EDIT = "integration_edit";
    public static final String INTEGRATION_ACTION_SAVE = "integration_save";
    public static final String INTEGRATION_JSON_KEY = "integration_json";
    private IntegrationViewModel integrationViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get a new or existing ViewModel from the ViewModelProvider.
        integrationViewModel = new ViewModelProvider(this).get(IntegrationViewModel.class);

        getParentFragmentManager().setFragmentResultListener(INTEGRATION_REQUEST_KEY, this, new FragmentResultListener() {
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
        requireActivity().setTitle(R.string.title_integrations);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recycler_integration_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.integration_list_recyclerview);
        final IntegrationListAdapter adapter = new IntegrationListAdapter(this, new IntegrationListAdapter.IntegrationDiff());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        integrationViewModel.getIntegrationsLiveData().observe(requireActivity(), adapter::submitList);
        Log.d(Utility.buildTag(getClass(), "onCreateView"), "");
        return view;
    }

    private void handleFragmentResult(Bundle bundle) {
        if (bundle == null) {
            return;
        }
        if (bundle.getString(INTEGRATION_ACTION_KEY) != null) {
            handleIntegrationActions(bundle.getString(INTEGRATION_JSON_KEY), bundle.getString(INTEGRATION_ACTION_KEY));
        } else {
            Log.w("unknown action!", "unknown action: " + bundle);
        }
    }

    private void handleIntegrationActions(String integrationJson, String action) {
        Log.d(Utility.buildTag(getClass(), "handleProjectActions"), String.format("action: %s, integration: %s", action, integrationJson));
        try {
            Integration integration = Utility.gsonMapper().fromJson(integrationJson, Integration.class);
            if (INTEGRATION_ACTION_SAVE.equals(action)) {
                integrationViewModel.save(integration);
                showSnackbar(String.format("saved %s", integration.getSystem()), R.color.color_snackbar_text_add);
            } else if (INTEGRATION_ACTION_VIEW.equals(action)) {
                // redirect to timesheet entry list fragment
                Bundle bundle = new Bundle();
                bundle.putLong(INTEGRATION_ID_KEY, integration.getId());

            } else if (INTEGRATION_ACTION_EDIT.equals(action)) {
                Bundle bundle = new Bundle();
                bundle.putString(INTEGRATION_JSON_KEY, integrationJson);
            } else {
                Log.w(Utility.buildTag(getClass(), "handleIntegrationActions"), "unknown action: " + action);
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
        navigateTo(R.id.nav_from_integration_list_to_integration_details, bundle);
    }
}
