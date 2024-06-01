package com.gunnarro.android.terex.ui.adapter;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.gunnarro.android.terex.R;
import com.gunnarro.android.terex.domain.dto.TimesheetDto;
import com.gunnarro.android.terex.ui.fragment.TimesheetListFragment;
import com.gunnarro.android.terex.ui.listener.ListOnItemClickListener;
import com.gunnarro.android.terex.ui.view.TimesheetViewHolder;

public class TimesheetListAdapter extends ListAdapter<TimesheetDto, TimesheetViewHolder> implements AdapterView.OnItemClickListener {

    private final ListOnItemClickListener listOnItemClickListener;

    public TimesheetListAdapter(@NonNull ListOnItemClickListener listOnItemClickListener, @NonNull DiffUtil.ItemCallback<TimesheetDto> diffCallback) {
        super(diffCallback);
        this.setHasStableIds(true);
        this.listOnItemClickListener = listOnItemClickListener;
    }

    @NonNull
    @Override
    public TimesheetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TimesheetViewHolder viewHolder = TimesheetViewHolder.create(parent);
        viewHolder.itemView.findViewById(R.id.ic_timesheet_row_view).setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putLong(TimesheetListFragment.TIMESHEET_ID_KEY, getItem(viewHolder.getBindingAdapterPosition()).getTimesheetId());
            bundle.putString(TimesheetListFragment.TIMESHEET_ACTION_KEY, TimesheetListFragment.TIMESHEET_ACTION_EDIT);
            listOnItemClickListener.onItemClick(bundle);
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TimesheetViewHolder holder, int position) {
        Log.d("onBindViewHolder", String.format("%s", getItem(position)));
        holder.bindListLine(getItem(position));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d("onItemClick", "pos=" + position + ", id=" + id);
        notifyItemRangeRemoved(position, 1);
    }

    /**
     * Check if timesheet are equal or not
     */
    public static class TimesheetDiff extends DiffUtil.ItemCallback<TimesheetDto> {
        @Override
        public boolean areItemsTheSame(@NonNull TimesheetDto oldItem, @NonNull TimesheetDto newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull TimesheetDto oldItem, @NonNull TimesheetDto newItem) {
            return oldItem.equals(newItem);
        }
    }
}
