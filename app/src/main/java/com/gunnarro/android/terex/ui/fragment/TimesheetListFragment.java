package com.gunnarro.android.terex.ui.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.gunnarro.android.terex.R;
import com.gunnarro.android.terex.domain.entity.Timesheet;
import com.gunnarro.android.terex.domain.entity.TimesheetEntry;
import com.gunnarro.android.terex.domain.entity.TimesheetWithEntries;
import com.gunnarro.android.terex.observable.RxBus;
import com.gunnarro.android.terex.observable.event.TimesheetEvent;
import com.gunnarro.android.terex.ui.adapter.TimesheetListAdapter;
import com.gunnarro.android.terex.ui.view.TimesheetEntryViewModel;
import com.gunnarro.android.terex.utility.Utility;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

@AndroidEntryPoint
public class TimesheetListFragment extends Fragment {
    public static final String TIMESHEET_REQUEST_KEY = "100";
    public static final String TIMESHEET_ENTRY_JSON_INTENT_KEY = "timesheet_entry_as_json";
    public static final String TIMESHEET_ENTRY_ACTION_KEY = "11";
    public static final String TIMESHEET_ENTRY_ACTION_SAVE = "timesheet_entry_save";
    public static final String TIMESHEET_ENTRY_ACTION_DELETE = "timesheet_entry_delete";

    public static final String TIMESHEET_JSON_INTENT_KEY = "timesheet_as_json";
    public static final String TIMESHEET_ACTION_KEY = "22";
    public static final String TIMESHEET_ACTION_SAVE = "timesheet_save";
    public static final String TIMESHEET_ACTION_DELETE = "timesheet_delete";

    private TimesheetEntryViewModel timesheetViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        // Get a new or existing ViewModel from the ViewModelProvider.
        timesheetViewModel = new ViewModelProvider(this).get(TimesheetEntryViewModel.class);

        getParentFragmentManager().setFragmentResultListener(TIMESHEET_REQUEST_KEY, this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                Log.d(Utility.buildTag(getClass(), "onFragmentResult"), "requestKey: " + requestKey + ", bundle:" + bundle);
                handleFragmentResult(bundle);
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

        // Update the cached copy of the timesheet entries in the adapter.
        timesheetViewModel.getTimesheetLiveData().observe(requireActivity(), adapter::submitList);

        List<Timesheet> timesheets = timesheetViewModel.getAllTimesheets();
        Log.d("all timesheets", "timesheets: " + timesheets);
        TimesheetWithEntries timesheetWithEntries = timesheetViewModel.getTimesheetWithEntries(1L);
        Log.d("all timesheets", "timesheet with entries: " + timesheetViewModel.getTimesheetWithEntries(1L));

        TextView listHeaderView = view.findViewById(R.id.timesheet_list_header);
        listHeaderView.setText(String.format("[%s-%s] %s - %s", timesheetWithEntries.getTimesheet().getMonth(), timesheetWithEntries.getTimesheet().getYear(), timesheetWithEntries.getTimesheet().getClientName(), timesheetWithEntries.getTimesheet().getProjectCode()));

        FloatingActionButton addButton = view.findViewById(R.id.add_timesheet);
        addButton.setOnClickListener(v -> {
            String timesheetJson = Utility.gsonMapper().toJson(timesheetViewModel.getMostRecent(), TimesheetEntry.class);
            Bundle bundle = new Bundle();
            bundle.putString(TimesheetListFragment.TIMESHEET_ENTRY_JSON_INTENT_KEY, timesheetJson);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, TimesheetAddEntryFragment.class, bundle)
                    .setReorderingAllowed(true)
                    .commit();
        });

        FloatingActionButton calendarButton = view.findViewById(R.id.view_timesheet_calendar);
        calendarButton.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, TimesheetCustomCalendarFragment.class, bundle)
                    .setReorderingAllowed(true)
                    .commit();
        });

        // listen after timesheet add and delete events
        RxBus.getInstance().listen().subscribe(getInputObserver());
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

    private void handleFragmentResult(Bundle bundle) {
        if (bundle == null) {
            return;
        }
        if (bundle.getString(TIMESHEET_ACTION_KEY) != null) {
            handleTimesheetActions(bundle.getString(TIMESHEET_JSON_INTENT_KEY), bundle.getString(TIMESHEET_ACTION_KEY));
        } else if (bundle.getString(TIMESHEET_ENTRY_ACTION_KEY) != null) {
            handleTimesheetEntryActions(bundle.getString(TIMESHEET_ENTRY_JSON_INTENT_KEY), bundle.getString(TIMESHEET_ENTRY_ACTION_KEY));
        } else {
            Log.w("unknown action!", "unknown action: " + bundle);
        }
    }

    private void handleTimesheetEntryActions(String timesheetEntryJson, String action) {
        Log.d(Utility.buildTag(getClass(), "handleTimesheetEntryActions"), String.format("action: %s, timesheet: %s", action, timesheetEntryJson));
        try {
            TimesheetEntry timesheetEntry = Utility.gsonMapper().fromJson(timesheetEntryJson, TimesheetEntry.class);
            if (TIMESHEET_ENTRY_ACTION_SAVE.equals(action)) {
                timesheetViewModel.saveTimesheetEntry(timesheetEntry);
                if (timesheetEntry.getId() == null) {
                    showSnackbar(String.format(getResources().getString(R.string.info_timesheet_list_add_msg_format), timesheetEntry.getWorkdayDate()), R.color.color_snackbar_text_add);
                } else {
                    showSnackbar(String.format(getResources().getString(R.string.info_timesheet_list_update_msg_format), timesheetEntry.getWorkdayDate()), R.color.color_snackbar_text_update);
                }

            } else if (TIMESHEET_ENTRY_ACTION_DELETE.equals(action)) {
                timesheetViewModel.deleteTimesheetEntry(timesheetEntry);
                showSnackbar(String.format(getResources().getString(R.string.info_timesheet_list_delete_msg_format), timesheetEntry.getWorkdayDate()), R.color.color_snackbar_text_delete);
            } else {
                Log.w(Utility.buildTag(getClass(), "handleTimesheetEntryActions"), "unknown action: " + action);
                showInfoDialog(String.format("Application error!%s Unknown action: %s%s Please report.", action, System.lineSeparator(), System.lineSeparator()), getActivity());
            }
        } catch (Exception ex) {
            showInfoDialog(String.format("Application error!%sError: %s%s Please report.", ex.getMessage(), System.lineSeparator(), System.lineSeparator()), getActivity());
        }
    }

    private void handleTimesheetActions(String timesheetJson, String action) {
        Log.d(Utility.buildTag(getClass(), "handleTimesheetActions"), String.format("action: %s, timesheet: %s", action, timesheetJson));
        try {
            Timesheet timesheet = Utility.gsonMapper().fromJson(timesheetJson, Timesheet.class);
            if (TIMESHEET_ACTION_SAVE.equals(action)) {
                if (timesheet.getId() == null) {
                    timesheetViewModel.saveTimesheet(timesheet);
                    showSnackbar(String.format(getResources().getString(R.string.info_timesheet_list_add_msg_format), timesheet.toString()), R.color.color_snackbar_text_add);
                } else {
                    showSnackbar(String.format(getResources().getString(R.string.info_timesheet_list_update_msg_format), timesheet), R.color.color_snackbar_text_update);
                }
            } else if (TIMESHEET_ACTION_DELETE.equals(action)) {

                showSnackbar(String.format(getResources().getString(R.string.info_timesheet_list_delete_msg_format), timesheet.toString()), R.color.color_snackbar_text_delete);
            } else {
                Log.w(Utility.buildTag(getClass(), "handleTimesheetActions"), "unknown action: " + action);
                showInfoDialog(String.format("Application error!%s Unknown action: %s%s Please report.", action, System.lineSeparator(), System.lineSeparator()), getActivity());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showInfoDialog(String.format("Application error!%sError: %s%s Please report.", ex.getMessage(), System.lineSeparator(), System.lineSeparator()), getActivity());
        }
    }

    private void showSnackbar(String msg, @ColorRes int bgColor) {
        Resources.Theme theme = getResources().newTheme();
        Snackbar snackbar = Snackbar.make(requireView().findViewById(R.id.timesheet_list_layout), msg, BaseTransientBottomBar.LENGTH_LONG);
        snackbar.setTextColor(getResources().getColor(bgColor, theme));
        snackbar.show();
    }

    private void showInfoDialog(String infoMessage, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Info");
        builder.setMessage(infoMessage);
        // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
        builder.setCancelable(false);
        // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
        builder.setPositiveButton("Ok", (dialog, which) -> dialog.cancel());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // listen for timesheet add and delete events
    private Observer<Object> getInputObserver() {
        return new Observer<Object>() {
            @Override
            public void onSubscribe(@NotNull Disposable d) {
                Log.d(Utility.buildTag(getClass(), "getInputObserver.onSubscribe"), "");
            }

            @Override
            public void onNext(@NotNull Object obj) {
                if (obj instanceof TimesheetEvent event) {
                    Log.d(Utility.buildTag(getClass(), "getInputObserver.onNext"), String.format("handle event: %s", event));
                    if (event.isAdd()) {
                        timesheetViewModel.saveTimesheetEntry(event.getTimesheetEntry());
                    } else if (event.isDelete()) {
                        // not implemented
                    }
                }
            }

            @Override
            public void onError(@NotNull Throwable e) {
                Log.e(Utility.buildTag(getClass(), "getInputObserver.onError"), String.format("%s", e.getMessage()));
            }

            @Override
            public void onComplete() {
                Log.d(Utility.buildTag(getClass(), "getInputObserver.onComplete"), "");
            }
        };
    }
}
