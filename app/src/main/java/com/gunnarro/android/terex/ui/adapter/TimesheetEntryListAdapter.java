package com.gunnarro.android.terex.ui.adapter;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.gunnarro.android.terex.R;
import com.gunnarro.android.terex.domain.entity.TimesheetEntry;
import com.gunnarro.android.terex.ui.fragment.TimesheetEntryListFragment;
import com.gunnarro.android.terex.ui.view.TimesheetEntryViewHolder;
import com.gunnarro.android.terex.utility.Utility;

public class TimesheetEntryListAdapter extends ListAdapter<TimesheetEntry, TimesheetEntryViewHolder> implements AdapterView.OnItemClickListener {

    private final FragmentManager fragmentManager;

    public TimesheetEntryListAdapter(@NonNull FragmentManager fragmentManager, @NonNull DiffUtil.ItemCallback<TimesheetEntry> diffCallback) {
        super(diffCallback);
        // do not have stable id's, since items can be deleted and inserted into the list. Can be with the same timesheet id and and workday date, but the id have changed.
        this.setHasStableIds(false);
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public TimesheetEntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TimesheetEntryViewHolder viewHolder = TimesheetEntryViewHolder.create(parent);
        viewHolder.itemView.findViewById(R.id.ic_timesheet_entry_row_delete).setOnClickListener(v -> {
            Bundle actionBundle = new Bundle();
            actionBundle.putString(TimesheetEntryListFragment.TIMESHEET_ENTRY_JSON_KEY, toJson(getItem(viewHolder.getBindingAdapterPosition())));
            actionBundle.putString(TimesheetEntryListFragment.TIMESHEET_ENTRY_ACTION_KEY, TimesheetEntryListFragment.TIMESHEET_ENTRY_ACTION_DELETE);
            fragmentManager.setFragmentResult(TimesheetEntryListFragment.TIMESHEET_ENTRY_REQUEST_KEY, actionBundle);
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TimesheetEntryViewHolder holder, int position) {
        holder.bindListLine(getItem(position));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        notifyItemRangeRemoved(position, 1);
    }

    private String toJson(TimesheetEntry timesheet) {
        try {
            return Utility.gsonMapper().toJson(timesheet);
        } catch (Exception e) {
            Log.e("getTimesheetAsJson", e.toString());
            throw new RuntimeException("unable to parse timesheetEntry to json! " + e);
        }
    }

    /**
     * User to check is list items are equal or not
     */
    public static class TimesheetEntryDiff extends DiffUtil.ItemCallback<TimesheetEntry> {
        @Override
        public boolean areItemsTheSame(@NonNull TimesheetEntry oldItem, @NonNull TimesheetEntry newItem) {
            // User properties may have changed if reloaded from the DB, but ID is fixed
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull TimesheetEntry oldItem, @NonNull TimesheetEntry newItem) {
            // NOTE: if you use equals, your object must properly override Object#equals()
            // Incorrectly returning false here will result in too many animations.
            return oldItem.equals(newItem);
        }
    }

}
