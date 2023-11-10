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
import com.gunnarro.android.terex.ui.adapter.TimesheetListAdapter;
import com.gunnarro.android.terex.ui.view.TimesheetViewModel;
import com.gunnarro.android.terex.utility.Utility;

import org.jetbrains.annotations.NotNull;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TimesheetListFragment extends Fragment {
    public static final String TIMESHEET_REQUEST_KEY = "100";
    public static final String TIMESHEET_JSON_KEY = "timesheet_as_json";
    public static final String TIMESHEET_ID_KEY = "timesheet_id";
    public static final String TIMESHEET_ACTION_KEY = "111";
    public static final String TIMESHEET_ACTION_SAVE = "timesheet_save";
    public static final String TIMESHEET_ACTION_DELETE = "timesheet_delete";
    public static final String TIMESHEET_ACTION_VIEW = "timesheet_view";

    private TimesheetViewModel timesheetViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requireActivity().setTitle(R.string.title_timesheets);
        // Get a new or existing ViewModel from the ViewModelProvider.
        try {
            timesheetViewModel = new ViewModelProvider(this).get(TimesheetViewModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // register listener for add, update and delete timesheet events
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
        RecyclerView recyclerView = view.findViewById(R.id.timesheet_list_recyclerview);
        final TimesheetListAdapter adapter = new TimesheetListAdapter(getParentFragmentManager(), new TimesheetListAdapter.TimesheetDiff());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Update the cached copy of the timesheet entries in the adapter.
        timesheetViewModel.getTimesheetLiveData().observe(requireActivity(), adapter::submitList);

        FloatingActionButton addButton = view.findViewById(R.id.timesheet_add_btn);
        addButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, TimesheetNewFragment.class, createTimesheetBundle(adapter.getItemId(0)))
                    .setReorderingAllowed(true)
                    .commit();
        });

        Log.d(Utility.buildTag(getClass(), "onCreateView"), "");
        return view;
    }

    private Bundle createTimesheetBundle(Long timesheetId) {
        Timesheet timesheet = timesheetViewModel.getTimesheet(timesheetId);
        if (timesheet == null) {
            timesheet = createDefaultTimesheet(timesheetId);
        }
        String timesheetJson = Utility.gsonMapper().toJson(timesheet, Timesheet.class);
        Bundle bundle = new Bundle();
        bundle.putString(TimesheetListFragment.TIMESHEET_JSON_KEY, timesheetJson);
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
            handleTimesheetActions(bundle.getString(TIMESHEET_JSON_KEY), bundle.getString(TIMESHEET_ACTION_KEY));
        } else {
            Log.w("unknown action!", "unknown action: " + bundle);
        }
    }

    private void handleTimesheetActions(String timesheetJson, String action) {
        Log.d(Utility.buildTag(getClass(), "handleTimesheetActions"), String.format("action: %s, timesheet: %s", action, timesheetJson));
        try {
            Timesheet timesheet = Utility.gsonMapper().fromJson(timesheetJson, Timesheet.class);
            if (TIMESHEET_ACTION_SAVE.equals(action)) {
                if (timesheet.getId() == null) {
                    timesheetViewModel.saveTimesheet(timesheet);
                    showSnackbar(String.format(getResources().getString(R.string.info_timesheet_list_add_msg_format), timesheet.getTimesheetRef()), R.color.color_snackbar_text_add);
                } else {
                    showSnackbar(String.format(getResources().getString(R.string.info_timesheet_list_update_msg_format), timesheet.getTimesheetRef()), R.color.color_snackbar_text_update);
                }
            } else if (TIMESHEET_ACTION_DELETE.equals(action)) {
                timesheetViewModel.deleteTimesheet(timesheet);
                showSnackbar(String.format(getResources().getString(R.string.info_timesheet_list_delete_msg_format), timesheet.getTimesheetRef()), R.color.color_snackbar_text_delete);
            } else if (TIMESHEET_ACTION_VIEW.equals(action)) {
                // redirect to timesheet entry list fragment
                Bundle bundle = new Bundle();
                bundle.putLong(TimesheetListFragment.TIMESHEET_ID_KEY, timesheet.getId());
                goToTimesheetEntryView(bundle);
            } else {
                Log.w(Utility.buildTag(getClass(), "handleTimesheetActions"), "unknown action: " + action);
                showInfoDialog(String.format("Application error!%s Unknown action: %s%s Please report.", action, System.lineSeparator(), System.lineSeparator()), getActivity());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showInfoDialog(String.format("Application error!%sError: %s%s Please report.", ex.getMessage(), System.lineSeparator(), System.lineSeparator()), getActivity());
        }
    }

    private Timesheet createDefaultTimesheet(Long timesheetId) {
        return Timesheet.createDefault(null, null);
    }

    private void goToTimesheetEntryView(Bundle bundle) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, TimesheetEntryListFragment.class, bundle)
                .setReorderingAllowed(true)
                .commit();
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
}
