package com.gunnarro.android.terex.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.gunnarro.android.terex.R;
import com.gunnarro.android.terex.domain.entity.Timesheet;
import com.gunnarro.android.terex.ui.adapter.TimesheetListAdapter;
import com.gunnarro.android.terex.ui.view.TimesheetViewModel;
import com.gunnarro.android.terex.utility.Utility;

import org.jetbrains.annotations.NotNull;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TimesheetListFragment extends Fragment {
    public static final String TIMESHEET_JSON_INTENT_KEY = "timesheet_as_json";
    public static final String TIMESHEET_REQUEST_KEY = "1";
    public static final String TIMESHEET_ACTION_KEY = "11";
    public static final String TIMESHEET_ACTION_SAVE = "timesheet_save";
    public static final String TIMESHEET_ACTION_DELETE = "timesheet_delete";
    private TimesheetViewModel timesheetViewModel;

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        // Get a new or existing ViewModel from the ViewModelProvider.
        timesheetViewModel = new ViewModelProvider(this).get(TimesheetViewModel.class);

        getParentFragmentManager().setFragmentResultListener(TIMESHEET_REQUEST_KEY, this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                Log.d(Utility.buildTag(getClass(), "onFragmentResult"), "intent: " + requestKey + "json:  + " + bundle.getString(TIMESHEET_JSON_INTENT_KEY));
                try {
                    Timesheet timesheet = mapper.readValue(bundle.getString(TIMESHEET_JSON_INTENT_KEY), Timesheet.class);
                    if (bundle.getString(TIMESHEET_ACTION_KEY).equals(TIMESHEET_ACTION_SAVE)) {
                        timesheetViewModel.save(timesheet);
                        Toast.makeText(getContext(), "saved timesheet " + timesheet.getWorkdayDate(), Toast.LENGTH_SHORT).show();
                    } else if (bundle.getString(TIMESHEET_ACTION_KEY).equals(TIMESHEET_ACTION_DELETE)) {
                        timesheetViewModel.delete(timesheet);
                        Toast.makeText(getContext(), "deleted timesheet " + timesheet.getWorkdayDate(), Toast.LENGTH_SHORT).show();
                    } else {
                        Log.w(Utility.buildTag(getClass(), "onFragmentResult"), "unkown action: " + (bundle.getString(TIMESHEET_ACTION_KEY)));
                        Toast.makeText(getContext(), "unknown action " + bundle.getString(TIMESHEET_ACTION_KEY), Toast.LENGTH_SHORT).show();
                    }
                    Log.d(Utility.buildTag(getClass(), "onFragmentResult"), String.format("action: %s, timesheet: %s %s", bundle.getString(TIMESHEET_ACTION_KEY), timesheet.getClientName(), timesheet.getProjectName()));
                } catch (JsonProcessingException e) {
                    Log.e("", e.toString());
                    throw new RuntimeException("Application Error: " + e);
                }
            }
        });
        Log.d(Utility.buildTag(getClass(), "onCreate"), "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recycler_timesheet_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.timesheet_recyclerview);
        final TimesheetListAdapter adapter = new TimesheetListAdapter(getParentFragmentManager(), new TimesheetListAdapter.TimesheetDiff());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //recyclerView.setOnClickListener(v -> Log.d("", "clicked on list item...."));

        // Add an observer on the LiveData returned by getAlphabetizedWords.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        // Update the cached copy of the words in the adapter.
        timesheetViewModel.getTimesheetLiveData().observe(requireActivity(), adapter::submitList);

        FloatingActionButton addButton = view.findViewById(R.id.add_timesheet);
        addButton.setOnClickListener(v -> {
                Bundle arguments = new Bundle();
            try {
                arguments.putString(TIMESHEET_JSON_INTENT_KEY, Utility.getJsonMapper().writeValueAsString(new Timesheet()));
            } catch (JsonProcessingException e) {
                Log.e(Utility.buildTag(getClass(), "addBtn.setOnClickListener"), e.toString());
            }

            requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, TimesheetRegisterWorkFragment.class, arguments)
                .setReorderingAllowed(true)
                .commit();
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

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
