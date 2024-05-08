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
import com.gunnarro.android.terex.ui.adapter.ClientListAdapter;
import com.gunnarro.android.terex.ui.listener.ListOnItemClickListener;
import com.gunnarro.android.terex.ui.view.ClientViewModel;
import com.gunnarro.android.terex.utility.Utility;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ClientListFragment extends BaseFragment implements ListOnItemClickListener {
    public static final String CLIENT_REQUEST_KEY = "300";
    public static final String CLIENT_ID_KEY = "client_id";
    public static final String CLIENT_ACTION_KEY = "client_action";
    public static final String CLIENT_ACTION_VIEW = "client_view";
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
        final ClientListAdapter adapter = new ClientListAdapter(this, new ClientListAdapter.ClientDiff());
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
        if (bundle.getLong(CLIENT_ID_KEY) > 0) {
          //  navigateTo(R.id.nav_from_client_list_to_client_details, bundle);
        }
    }

    @Override
    public void onItemClick(Bundle bundle) {
      //  getNavController().navigate(R.id.nav_from_client_list_to_client_details, bundle);
    }
}
