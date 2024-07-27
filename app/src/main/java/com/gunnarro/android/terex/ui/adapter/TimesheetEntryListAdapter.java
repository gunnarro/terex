package com.gunnarro.android.terex.ui.adapter;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.gunnarro.android.terex.R;
import com.gunnarro.android.terex.domain.dto.TimesheetEntryDto;
import com.gunnarro.android.terex.ui.fragment.TimesheetEntryListFragment;
import com.gunnarro.android.terex.ui.listener.ListOnItemClickListener;
import com.gunnarro.android.terex.ui.view.TimesheetEntryViewHolder;

public class TimesheetEntryListAdapter extends ListAdapter<TimesheetEntryDto, TimesheetEntryViewHolder> implements AdapterView.OnItemClickListener {

    private final ListOnItemClickListener listOnItemClickListener;

    public TimesheetEntryListAdapter(@NonNull ListOnItemClickListener listOnItemClickListener, @NonNull DiffUtil.ItemCallback<TimesheetEntryDto> diffCallback) {
        super(diffCallback);
        // do not have stable id's, since items can be deleted and inserted into the list.
        // Can be with the same timesheet id and and workday date, but the id have changed.
        this.setHasStableIds(true);
        this.listOnItemClickListener = listOnItemClickListener;
    }

    @NonNull
    @Override
    public TimesheetEntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TimesheetEntryViewHolder viewHolder = TimesheetEntryViewHolder.create(parent);
        viewHolder.itemView.findViewById(R.id.ic_timesheet_entry_row_delete).setOnClickListener(v -> {
            TimesheetEntryDto timesheetEntryDto = getItem(viewHolder.getBindingAdapterPosition());
            if (timesheetEntryDto.isOpen()) {
                Bundle actionBundle = new Bundle();
                actionBundle.putLong(TimesheetEntryListFragment.TIMESHEET_ENTRY_ID_KEY, timesheetEntryDto.getId());
                actionBundle.putString(TimesheetEntryListFragment.TIMESHEET_ENTRY_ACTION_KEY, TimesheetEntryListFragment.TIMESHEET_ENTRY_ACTION_DELETE);
                listOnItemClickListener.onItemClick(actionBundle);
            }
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
    public static class TimesheetEntryDiff extends DiffUtil.ItemCallback<TimesheetEntryDto> {
        @Override
        public boolean areItemsTheSame(@NonNull TimesheetEntryDto oldItem, @NonNull TimesheetEntryDto newItem) {
            // User properties may have changed if reloaded from the DB, but ID is fixed
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull TimesheetEntryDto oldItem, @NonNull TimesheetEntryDto newItem) {
            // NOTE: if you use equals, your object must properly override Object#equals()
            // Incorrectly returning false here will result in too many animations.
            return oldItem.equals(newItem);
        }
    }

}
