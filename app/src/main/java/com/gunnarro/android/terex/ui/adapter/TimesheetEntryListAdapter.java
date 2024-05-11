package com.gunnarro.android.terex.ui.adapter;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.gunnarro.android.terex.R;
import com.gunnarro.android.terex.domain.entity.TimesheetEntry;
import com.gunnarro.android.terex.ui.fragment.TimesheetEntryListFragment;
import com.gunnarro.android.terex.ui.listener.ListOnItemClickListener;
import com.gunnarro.android.terex.ui.view.TimesheetEntryViewHolder;

public class TimesheetEntryListAdapter extends ListAdapter<TimesheetEntry, TimesheetEntryViewHolder> implements AdapterView.OnItemClickListener {

    private final ListOnItemClickListener listOnItemClickListener;

    public TimesheetEntryListAdapter(@NonNull ListOnItemClickListener listOnItemClickListener, @NonNull DiffUtil.ItemCallback<TimesheetEntry> diffCallback) {
        super(diffCallback);
        // do not have stable id's, since items can be deleted and inserted into the list. Can be with the same timesheet id and and workday date, but the id have changed.
        this.setHasStableIds(false);
        this.listOnItemClickListener = listOnItemClickListener;
    }

    @NonNull
    @Override
    public TimesheetEntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TimesheetEntryViewHolder viewHolder = TimesheetEntryViewHolder.create(parent);
        viewHolder.itemView.findViewById(R.id.ic_timesheet_entry_row_delete).setOnClickListener(v -> {
            Bundle actionBundle = new Bundle();
            actionBundle.putLong(TimesheetEntryListFragment.TIMESHEET_ENTRY_ID_KEY, getItem(viewHolder.getBindingAdapterPosition()).getId());
            actionBundle.putString(TimesheetEntryListFragment.TIMESHEET_ENTRY_ACTION_KEY, TimesheetEntryListFragment.TIMESHEET_ENTRY_ACTION_DELETE);
            listOnItemClickListener.onItemClick(actionBundle);
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
