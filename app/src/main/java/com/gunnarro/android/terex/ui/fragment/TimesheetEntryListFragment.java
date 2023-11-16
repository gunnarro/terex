package com.gunnarro.android.terex.ui.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.gunnarro.android.terex.R;
import com.gunnarro.android.terex.domain.entity.Timesheet;
import com.gunnarro.android.terex.domain.entity.TimesheetEntry;
import com.gunnarro.android.terex.exception.TerexApplicationException;
import com.gunnarro.android.terex.ui.adapter.TimesheetEntryListAdapter;
import com.gunnarro.android.terex.ui.swipe.SwipeCallback;
import com.gunnarro.android.terex.ui.view.TimesheetEntryViewModel;
import com.gunnarro.android.terex.utility.Utility;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TimesheetEntryListFragment extends Fragment {
    public static final String TIMESHEET_ENTRY_REQUEST_KEY = "200";
    public static final String TIMESHEET_ENTRY_JSON_KEY = "timesheet_entry_as_json";
    public static final String TIMESHEET_ENTRY_ACTION_KEY = "211";
    public static final String TIMESHEET_ENTRY_ACTION_SAVE = "timesheet_entry_save";
    public static final String TIMESHEET_ENTRY_ACTION_DELETE = "timesheet_entry_delete";

    private TimesheetEntryViewModel timesheetEntryViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setHasOptionsMenu(false);
        requireActivity().setTitle(R.string.title_timesheet_entries);
        // Get a new or existing ViewModel from the ViewModelProvider.
        try {
            timesheetEntryViewModel = new ViewModelProvider(this).get(TimesheetEntryViewModel.class);
        } catch (Exception e) {
            throw new TerexApplicationException("Error creating view!", "50051", e);
        }
        getParentFragmentManager().setFragmentResultListener(TIMESHEET_ENTRY_REQUEST_KEY, this, new FragmentResultListener() {
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
        View view = inflater.inflate(R.layout.fragment_recycler_timesheet_entry_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.timesheet_entry_list_recyclerview);
        final TimesheetEntryListAdapter adapter = new TimesheetEntryListAdapter(getParentFragmentManager(), new TimesheetEntryListAdapter.TimesheetEntryDiff());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (getArguments() == null || getArguments().getLong(TimesheetListFragment.TIMESHEET_ID_KEY) == 0) {
            // timesheet id must be provided, if not, return
            throw new TerexApplicationException("Missing timesheet id!", "50023", null);
        }
        Long timesheetId = getArguments().getLong(TimesheetListFragment.TIMESHEET_ID_KEY);

        // Update the cached copy of the timesheet entries in the adapter.
        timesheetEntryViewModel.getTimesheetEntryLiveData(timesheetId).observe(requireActivity(), adapter::submitList);

        // TimesheetWithEntries timesheetWithEntries = timesheetEntryViewModel.getTimesheetWithEntries(timesheetId);
        // Log.d("all timesheets", "timesheet with entries: " + timesheetWithEntries);

        TextView listHeaderView = view.findViewById(R.id.timesheet_entry_list_header);
        // if (timesheetWithEntries != null && timesheetWithEntries.getTimesheet() != null) {
        //     listHeaderView.setText(String.format("[%s-%s] %s - %s %S", timesheetWithEntries.getTimesheet().getMonth(), timesheetWithEntries.getTimesheet().getYear(), timesheetWithEntries.getTimesheet().getClientName(), timesheetWithEntries.getTimesheet().getProjectCode(),
        //             timesheetWithEntries.getTimesheet().getStatus()));
        // }
        listHeaderView.setText(String.format("timesheetId=%s", timesheetId));

        FloatingActionButton addButton = view.findViewById(R.id.timesheet_entry_add_btn);
        addButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, TimesheetEntryAddFragment.class, createTimesheetEntryBundle(timesheetId))
                    .setReorderingAllowed(true)
                    .commit();
        });

        FloatingActionButton calendarButton = view.findViewById(R.id.timesheet_entry_calendar_btn);
        calendarButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, TimesheetEntryCustomCalendarFragment.class, createTimesheetEntryBundle(timesheetId))
                    .setReorderingAllowed(true)
                    .commit();
        });

        // if timesheet has status closed, it is not possible to do any kind of changes
        // fixme
        /*if (timesheetWithEntries != null && timesheetWithEntries.getTimesheet().isBilled()) {
            addButton.setVisibility(View.INVISIBLE);
            calendarButton.setVisibility(View.INVISIBLE);
        }*/
        // listen after timesheet add and delete events
        //RxBus.getInstance().listen().subscribe(getInputObserver());
        // enable swipe
        enableSwipeToLeftAndDeleteItem(recyclerView);
        enableSwipeToRightAndViewItem(recyclerView);
        Log.d(Utility.buildTag(getClass(), "onCreateView"), "");
        return view;
    }

    private Bundle createTimesheetEntryBundle(Long timesheetId) {
        TimesheetEntry mostRecentTimesheetEntry = timesheetEntryViewModel.getMostRecentTimesheetEntry(timesheetId);
        if (mostRecentTimesheetEntry == null) {
            mostRecentTimesheetEntry = createDefaultTimesheetEntry(timesheetId);
        }
        String timesheetJson = Utility.gsonMapper().toJson(mostRecentTimesheetEntry, TimesheetEntry.class);
        Bundle bundle = new Bundle();
        bundle.putLong(TimesheetListFragment.TIMESHEET_ID_KEY, timesheetId);
        bundle.putString(TimesheetEntryListFragment.TIMESHEET_ENTRY_JSON_KEY, timesheetJson);
        return bundle;
    }

    /**
     * Update backup info after view is successfully create
     */
    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void handleFragmentResult(Bundle bundle) {
        if (bundle == null) {
            return;
        }
        if (bundle.getString(TIMESHEET_ENTRY_ACTION_KEY) != null) {
            handleTimesheetEntryActions(bundle.getString(TIMESHEET_ENTRY_JSON_KEY), bundle.getString(TIMESHEET_ENTRY_ACTION_KEY));
        } else {
            Log.w("unknown action!", "unknown action: " + bundle);
        }
    }

    private void handleTimesheetEntryActions(String timesheetEntryJson, String action) {
        Log.d(Utility.buildTag(getClass(), "handleTimesheetEntryActions"), String.format("action: %s, timesheet: %s", action, timesheetEntryJson));
        try {
            TimesheetEntry timesheetEntry = Utility.gsonMapper().fromJson(timesheetEntryJson, TimesheetEntry.class);
            if (TIMESHEET_ENTRY_ACTION_SAVE.equals(action)) {
                timesheetEntryViewModel.saveTimesheetEntry(timesheetEntry);
                if (timesheetEntry.getId() == null) {
                    showSnackbar(String.format(getResources().getString(R.string.info_timesheet_list_add_msg_format), timesheetEntry.getWorkdayDate()), R.color.color_snackbar_text_add);
                } else {
                    showSnackbar(String.format(getResources().getString(R.string.info_timesheet_list_update_msg_format), timesheetEntry.getWorkdayDate()), R.color.color_snackbar_text_update);
                }
            } else if (TIMESHEET_ENTRY_ACTION_DELETE.equals(action)) {
                timesheetEntryViewModel.deleteTimesheetEntry(timesheetEntry);
                showSnackbar(String.format(getResources().getString(R.string.info_timesheet_list_delete_msg_format), timesheetEntry.getWorkdayDate()), R.color.color_snackbar_text_delete);
            } else {
                Log.w(Utility.buildTag(getClass(), "handleTimesheetEntryActions"), "unknown action: " + action);
                showInfoDialog(String.format("Application error!%s Unknown action: %s%s Please report.", action, System.lineSeparator(), System.lineSeparator()), getActivity());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showInfoDialog(String.format("Application error!%sError: %s%s Please report.", ex.getMessage(), System.lineSeparator(), System.lineSeparator()), getActivity());
        }
    }

    private TimesheetEntry createDefaultTimesheetEntry(Long timesheetId) {
        return TimesheetEntry.createDefault(timesheetId, Timesheet.TimesheetStatusEnum.OPEN.name(), Utility.DEFAULT_DAILY_BREAK_IN_MINUTES, LocalDate.now(), Utility.DEFAULT_DAILY_WORKING_HOURS_IN_MINUTES, Utility.DEFAULT_HOURLY_RATE);
    }

    private void enableSwipeToRightAndViewItem(RecyclerView recyclerView) {
        SwipeCallback swipeToDeleteCallback = new SwipeCallback(requireContext(), ItemTouchHelper.RIGHT, getResources().getColor(R.color.color_bg_swipe_right, null), R.drawable.ic_add_black_24dp) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                LiveData<List<TimesheetEntry>> listLiveData = timesheetEntryViewModel.getTimesheetEntryLiveData(1L);
                int pos = viewHolder.getAbsoluteAdapterPosition();
                List<TimesheetEntry> list = listLiveData.getValue();
                //openTimesheetView(list.get(pos));

                Bundle bundle = new Bundle();
                bundle.putLong(TimesheetListFragment.TIMESHEET_ID_KEY, list.get(pos).getId());
               // goToTimesheetEntryView(bundle);
            }
        };
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(recyclerView);
        Log.i(Utility.buildTag(getClass(), "enableSwipeToRightAndAdd"), "enabled swipe handler for timesheet entry list item");
    }

    private void enableSwipeToLeftAndDeleteItem(RecyclerView recyclerView) {
        SwipeCallback swipeToDeleteCallback = new SwipeCallback(requireContext(), ItemTouchHelper.LEFT, getResources().getColor(R.color.color_bg_swipe_left, null), R.drawable.ic_delete_black_24dp) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final int position = viewHolder.getAbsoluteAdapterPosition();// FIXME
                timesheetEntryViewModel.deleteTimesheetEntry(timesheetEntryViewModel.getTimesheetEntryLiveData(1L).getValue().get(position));
                showSnackbar("Deleted timesheet", R.color.color_snackbar_text_delete);
            }
        };
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(recyclerView);
        Log.i(Utility.buildTag(getClass(), "enableSwipeToLeftAndDeleteItem"), "enabled swipe handler for delete timesheet entry list item");
    }

    private void showSnackbar(String msg, @ColorRes int bgColor) {
        Resources.Theme theme = getResources().newTheme();
        Snackbar snackbar = Snackbar.make(requireView().findViewById(R.id.timesheet_entry_list_layout), msg, BaseTransientBottomBar.LENGTH_LONG);
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
}
