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

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.gunnarro.android.terex.R;
import com.gunnarro.android.terex.domain.entity.Client;
import com.gunnarro.android.terex.exception.InputValidationException;
import com.gunnarro.android.terex.exception.TerexApplicationException;
import com.gunnarro.android.terex.ui.adapter.ClientListAdapter;
import com.gunnarro.android.terex.ui.listener.ListOnItemClickListener;
import com.gunnarro.android.terex.ui.view.ClientViewModel;
import com.gunnarro.android.terex.utility.Utility;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ClientListFragment extends BaseFragment implements ListOnItemClickListener {
    public static final String CLIENT_REQUEST_KEY = "400";
    public static final String CLIENT_ID_KEY = "client_id";
    public static final String CLIENT_JSON_KEY = "client_json";
    public static final String CLIENT_ACTION_KEY = "client_action";
    public static final String CLIENT_ACTION_VIEW = "client_view";
    public static final String CLIENT_ACTION_SAVE = "client_save";
    public static final String CLIENT_ACTION_EDIT = "client_edit";
    public static final String CLIENT_ACTION_DELETE = "client_delete";
    private ClientViewModel clientViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requireActivity().setTitle(R.string.title_clients);
        // Get a new or existing ViewModel from the ViewModelProvider.
        clientViewModel = new ViewModelProvider(this).get(ClientViewModel.class);

        getParentFragmentManager().setFragmentResultListener(CLIENT_REQUEST_KEY, this, new FragmentResultListener() {
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
        View view = inflater.inflate(R.layout.fragment_recycler_client_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.client_list_recyclerview);
        final ClientListAdapter adapter = new ClientListAdapter(this, new ClientListAdapter.ClientDtoDiff());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // Add an observer on the LiveData returned by getAlphabetizedWords.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        // Update the cached copy of the words in the adapter.
        clientViewModel.getAllClients().observe(requireActivity(), adapter::submitList);

        FloatingActionButton addButton = view.findViewById(R.id.client_list_add_btn);
        addButton.setOnClickListener(v -> navigateTo(R.id.nav_from_client_list_to_client_new, null));

        Log.d(Utility.buildTag(getClass(), "onCreateView"), "");
        return view;
    }

    private void handleFragmentResult(Bundle bundle) {
        if (bundle == null) {
            return;
        }
        if (bundle.getString(CLIENT_ACTION_KEY) != null) {
            handleClientActions(bundle.getString(CLIENT_JSON_KEY), bundle.getString(CLIENT_ACTION_KEY));
        } else {
            Log.w("unknown action!", "unknown action: " + bundle);
        }
    }

    private void handleClientActions(String clientJson, String action) {
        Log.d(Utility.buildTag(getClass(), "handleClientActions"), String.format("action: %s, client: %s", action, clientJson));
        try {
            Client client = Utility.gsonMapper().fromJson(clientJson, Client.class);
            if (CLIENT_ACTION_SAVE.equals(action)) {
                //clientViewModel.saveClient(client);
                showSnackbar(String.format("saved %s", client.getName()), R.color.color_snackbar_text_add);
            } else if (CLIENT_ACTION_DELETE.equals(action)) {
                if (client.getStatus().equals("ACTIVE")) {
                    showInfoDialog("Info", "Can not delete client with status ACTIVE");
                } else {
                    confirmDeleteClientDialog(getString(R.string.msg_delete_timesheet), getString(R.string.msg_confirm_delete), client.getId());
                }
            } else if (CLIENT_ACTION_VIEW.equals(action)) {
                // redirect to timesheet entry list fragment
                Bundle bundle = new Bundle();
                bundle.putLong(CLIENT_ID_KEY, client.getId());
                //bundle.putBoolean(CLIENT_READ_ONLY_KEY, timesheet.isBilled());
                openClientProjectListView(bundle);
            } else if (CLIENT_ACTION_EDIT.equals(action)) {
                // redirect to timesheet entry list fragment
                Bundle bundle = new Bundle();
                bundle.putString(CLIENT_JSON_KEY, clientJson);
                //bundle.putBoolean(CLIENT_READ_ONLY_KEY, timesheet.isBilled());
                openClientDetailsView(bundle);
            } else {
                Log.w(Utility.buildTag(getClass(), "handleClientActions"), "unknown action: " + action);
                showInfoDialog("Info", String.format("Application error!%s Unknown action: %s%s Please report.", action, System.lineSeparator(), System.lineSeparator()));
            }
        } catch (TerexApplicationException | InputValidationException ex) {
            showInfoDialog("Info", String.format("%s", ex.getMessage()));
        } catch (Exception e) {
            showInfoDialog("Error", String.format("%s", e.getCause()));
        }
    }

    private void openClientDetailsView(Bundle bundle) {
        navigateTo(R.id.nav_from_client_list_to_client_details, bundle);
    }

    private void openClientProjectListView(Bundle bundle) {
        navigateTo(R.id.nav_from_client_list_to_project_list, bundle);
    }

    /*
    * catch table item click
     */
    @Override
    public void onItemClick(Bundle bundle) {
       navigateTo(R.id.nav_from_client_list_to_project_list, bundle);
    }

    private void deleteClient(Long clientId) {
        try {
           // Client client = clientViewModel.getClient(clientId);
           // clientViewModel.deleteClient(client);
            showSnackbar(String.format(getResources().getString(R.string.info_timesheet_list_delete_msg_format), "not implemented"), R.color.color_snackbar_text_delete);
        } catch (TerexApplicationException | InputValidationException e) {
            showInfoDialog("Info", e.getMessage());
        }
    }

    private void confirmDeleteClientDialog(final String title, final String message, final Long clientId) {
        new MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.btn_ok, (dialogInterface, i) -> deleteClient(clientId))
                .setNeutralButton(R.string.btn_cancel, (dialogInterface, i) -> {
                    // nothing to do
                })
                .show();
    }
}
