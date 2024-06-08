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
import com.gunnarro.android.terex.domain.entity.TimesheetEntry;
import com.gunnarro.android.terex.exception.InputValidationException;
import com.gunnarro.android.terex.exception.TerexApplicationException;
import com.gunnarro.android.terex.ui.adapter.TimesheetEntryListAdapter;
import com.gunnarro.android.terex.ui.listener.ListOnItemClickListener;
import com.gunnarro.android.terex.ui.swipe.SwipeCallback;
import com.gunnarro.android.terex.ui.view.TimesheetEntryViewModel;
import com.gunnarro.android.terex.utility.Utility;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TimesheetEntryListFragment extends BaseFragment implements ListOnItemClickListener {
    public static final String TIMESHEET_ENTRY_REQUEST_KEY = "timesheet_entry_request";
    public static final String TIMESHEET_ENTRY_JSON_KEY = "timesheet_entry_as_json";
    public static final String TIMESHEET_ENTRY_ID_KEY = "timesheet_entry_id";
    public static final String TIMESHEET_ENTRY_ACTION_KEY = "211";
    public static final String TIMESHEET_ENTRY_ACTION_SAVE = "timesheet_entry_save";
    public static final String TIMESHEET_ENTRY_ACTION_DELETE = "timesheet_entry_delete";
    private Long timesheetId;
    private boolean isTimesheetReadOnly;
    private TimesheetEntryViewModel timesheetEntryViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setHasOptionsMenu(false);
        requireActivity().setTitle(R.string.title_timesheet_entries);
        // save the timesheet id, needed for view and delete timesheet entry actions.
        timesheetId = getArguments().getLong(TimesheetListFragment.TIMESHEET_ID_KEY);
        isTimesheetReadOnly = getArguments().getBoolean(TimesheetListFragment.TIMESHEET_READ_ONLY_KEY);
        // Get a new or existing ViewModel from the ViewModelProvider.
        try {
            timesheetEntryViewModel = new TimesheetEntryViewModel(requireActivity().getApplication(), timesheetId);
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recycler_timesheet_entry_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.timesheet_entry_list_recyclerview);
        final TimesheetEntryListAdapter adapter = new TimesheetEntryListAdapter(this, new TimesheetEntryListAdapter.TimesheetEntryDiff(), isTimesheetReadOnly);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (getArguments() == null || getArguments().getLong(TimesheetListFragment.TIMESHEET_ID_KEY) == 0) {
            // timesheet id must be provided, if not, return
            throw new TerexApplicationException("Missing timesheet id!", "50023", null);
        }
        // save the timesheet id, needed for view and delete timesheet entry actions.
        //timesheetId = getArguments().getLong(TimesheetListFragment.TIMESHEET_ID_KEY);
        //isTimesheetReadOnly = getArguments().getBoolean(TimesheetListFragment.TIMESHEET_READ_ONLY_KEY);

        // Update the cached copy of the timesheet entries in the adapter.
        timesheetEntryViewModel.getTimesheetEntryLiveData(timesheetId).observe(requireActivity(), adapter::submitList);

        FloatingActionButton addButton = view.findViewById(R.id.timesheet_entry_add_btn);
        addButton.setOnClickListener(v ->
                navigateTo(R.id.nav_from_timesheet_entry_list_to_timesheet_entry_details, createTimesheetEntryBundle(timesheetId))
        );

        FloatingActionButton calendarButton = view.findViewById(R.id.timesheet_entry_calendar_btn);
        calendarButton.setOnClickListener(v ->
                navigateTo(R.id.nav_from_timesheet_entry_list_to_timesheet_entry_calendar_add, createTimesheetEntryBundle(timesheetId))
        );

        // flip gui based on timesheet status, hide buttons if timesheet has status BILLED
        Log.d("isTimesheetReadOnly", String.format("timesheetId=%s, isTimesheetReadOnly=%s", timesheetId, isTimesheetReadOnly));
        if (isTimesheetReadOnly) {
            addButton.setVisibility(View.GONE);
            calendarButton.setVisibility(View.GONE);
        }
        // enable swipe
        enableSwipeToLeftAndDeleteItem(recyclerView, isTimesheetReadOnly);
        enableSwipeToRightAndViewItem(recyclerView);
        return view;
    }

    private Bundle createTimesheetEntryBundle(Long timesheetId) {
        TimesheetEntry mostRecentTimesheetEntry = timesheetEntryViewModel.getMostRecentTimesheetEntry(timesheetId);
        String timesheetJson = Utility.gsonMapper().toJson(mostRecentTimesheetEntry, TimesheetEntry.class);
        Bundle bundle = new Bundle();
        bundle.putLong(TimesheetListFragment.TIMESHEET_ID_KEY, timesheetId);
        bundle.putString(TimesheetEntryListFragment.TIMESHEET_ENTRY_JSON_KEY, timesheetJson);
        return bundle;
    }

    private void handleFragmentResult(Bundle bundle) {
        if (bundle == null) {
            return;
        }
        Log.w("handleFragmentResult", String.format("received action: %s", bundle));
        if (bundle.getString(TIMESHEET_ENTRY_ACTION_KEY) != null) {
            handleTimesheetEntryActions(bundle.getString(TIMESHEET_ENTRY_JSON_KEY), bundle.getString(TIMESHEET_ENTRY_ACTION_KEY));
        } else {
            Log.w("handleFragmentResult", String.format("unknown action: %s", bundle));
        }
    }

    private void handleTimesheetEntryActions(String timesheetEntryJson, String action) {
        Log.d(Utility.buildTag(getClass(), "handleTimesheetEntryActions"), String.format("action: %s, timesheetEntryJson: %s", action, timesheetEntryJson));
        try {
            TimesheetEntry timesheetEntry = Utility.gsonMapper().fromJson(timesheetEntryJson, TimesheetEntry.class);
            if (TIMESHEET_ENTRY_ACTION_SAVE.equals(action)) {
                timesheetEntryViewModel.saveTimesheetEntry(timesheetEntry);
                if (timesheetEntry.getId() == null) {
                    showSnackbar(String.format(getResources().getString(R.string.info_timesheet_list_add_msg_format), timesheetEntry.getWorkdayDate(), timesheetEntry.getWorkedHours()), R.color.color_snackbar_text_add);
                } else {
                    showSnackbar(String.format(getResources().getString(R.string.info_timesheet_list_update_msg_format), timesheetEntry.getWorkdayDate(), timesheetEntry.getWorkedHours()), R.color.color_snackbar_text_update);
                }
            } else if (TIMESHEET_ENTRY_ACTION_DELETE.equals(action)) {
                deleteTimesheetEntry(timesheetEntry.getId());
                showSnackbar(String.format(getResources().getString(R.string.info_timesheet_list_delete_msg_format), "timesheet entry, workday=" + timesheetEntry.getWorkdayDate()), R.color.color_snackbar_text_delete);
            } else {
                Log.w(Utility.buildTag(getClass(), "handleTimesheetEntryActions"), "unknown action: " + action);
                showInfoDialog("Error", String.format("Application error!%s Unknown action: %s%s Please report.", action, System.lineSeparator(), System.lineSeparator()));
            }
        } catch (InputValidationException ie) {
            showInfoDialog("Info", ie.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            showInfoDialog("Error", String.format("Application error!%s Error: %s%s Please report.", ex.getMessage(), System.lineSeparator(), System.lineSeparator()));
        }
    }

    private void enableSwipeToRightAndViewItem(RecyclerView recyclerView) {
        SwipeCallback swipeToDeleteCallback = new SwipeCallback(requireContext(), ItemTouchHelper.RIGHT, getResources().getColor(R.color.color_bg_swipe_right, null), R.drawable.ic_add_black_24dp) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final int selectedTimesheetEntryPos = viewHolder.getAbsoluteAdapterPosition();
                TimesheetEntry timesheetEntry = timesheetEntryViewModel.getTimesheetEntryLiveData(timesheetId).getValue().get(selectedTimesheetEntryPos);
                Bundle bundle = new Bundle();
                bundle.putLong(TimesheetEntryListFragment.TIMESHEET_ENTRY_ID_KEY, timesheetEntry.getId());
                // goToTimesheetEntryView(bundle);
            }
        };
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(recyclerView);
        Log.i(Utility.buildTag(getClass(), "enableSwipeToRightAndAdd"), "enabled swipe handler for timesheet entry list item");
    }

    private void enableSwipeToLeftAndDeleteItem(RecyclerView recyclerView, boolean isTimesheetReadOnly) {
        if (isTimesheetReadOnly) {
            // do not enable delete if timesheet is read only.
            return;
        }
        SwipeCallback swipeToDeleteCallback = new SwipeCallback(requireContext(), ItemTouchHelper.LEFT, getResources().getColor(R.color.color_bg_swipe_left, null), R.drawable.ic_delete_black_24dp) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final int selectedTimesheetEntryPos = viewHolder.getAbsoluteAdapterPosition();
                TimesheetEntry timesheetEntry = timesheetEntryViewModel.getTimesheetEntryLiveData(timesheetId).getValue().get(selectedTimesheetEntryPos);
                confirmDeleteTimesheetEntryDialog(getString(R.string.msg_delete_timesheet_entry), getString(R.string.msg_confirm_delete_timesheet_entry), timesheetEntry.getId());
            }
        };
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(recyclerView);
        Log.i(Utility.buildTag(getClass(), "enableSwipeToLeftAndDeleteItem"), "enabled swipe handler for delete timesheet entry list item");
    }

    /**
     * Only called for delete a timesheet entry item, by click om the garbage icon
     */
    @Override
    public void onItemClick(Bundle bundle) {
        TimesheetEntry timesheetEntry = new TimesheetEntry();
        timesheetEntry.setId(bundle.getLong(TIMESHEET_ENTRY_ID_KEY));
        handleTimesheetEntryActions(Utility.gsonMapper().toJson(timesheetEntry, TimesheetEntry.class), bundle.getString(TIMESHEET_ENTRY_ACTION_KEY));
    }

    private void deleteTimesheetEntry(Long timesheetEntryId) {
        try {
            TimesheetEntry timesheetEntry = timesheetEntryViewModel.getTimesheetEntry(timesheetEntryId);
            timesheetEntryViewModel.deleteTimesheetEntry(timesheetEntry);
            RecyclerView recyclerView = requireView().findViewById(R.id.timesheet_entry_list_recyclerview);
            recyclerView.getAdapter().notifyDataSetChanged();
            showSnackbar(String.format(getResources().getString(R.string.info_timesheet_list_delete_msg_format), "timesheet entry, workday=" + timesheetEntry.getWorkdayDate()), R.color.color_snackbar_text_delete);
        } catch (TerexApplicationException | InputValidationException e) {
            showInfoDialog("Info", e.getMessage());
        }
    }

    private void confirmDeleteTimesheetEntryDialog(final String title, final String message, final Long timesheetEntryId) {
        new MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.btn_ok, (dialogInterface, i) -> deleteTimesheetEntry(timesheetEntryId))
                .setNeutralButton(R.string.btn_cancel, (dialogInterface, i) -> {
                    // nothing to do
                })
                .show();
    }

    private void notifyItemRemoved() {
        RecyclerView recyclerView = requireView().findViewById(R.id.timesheet_entry_list_recyclerview);
        recyclerView.getAdapter().notifyDataSetChanged();
        Log.d("notifyItemRemoved", "notify deleted timesheet entry ...");
    }
}
