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
import com.gunnarro.android.terex.domain.entity.Timesheet;
import com.gunnarro.android.terex.ui.fragment.TimesheetFragment;
import com.gunnarro.android.terex.ui.view.TimesheetViewHolder;
import com.gunnarro.android.terex.utility.Utility;

public class TimesheetListAdapter extends ListAdapter<Timesheet, TimesheetViewHolder> implements AdapterView.OnItemClickListener {

    private final FragmentManager fragmentManager;

    public TimesheetListAdapter(@NonNull FragmentManager fragmentManager, @NonNull DiffUtil.ItemCallback<Timesheet> diffCallback) {
        super(diffCallback);
        this.setHasStableIds(true);
        this.fragmentManager = fragmentManager;
        Log.d("TimesheetListAdapter", "init");
    }

    @NonNull
    @Override
    public TimesheetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TimesheetViewHolder viewHolder = TimesheetViewHolder.create(parent);
        /**
         viewHolder.itemView.findViewById(R.id.ic_timesheet_row_delete).setOnClickListener(v -> {
            Bundle actionBundle = new Bundle();
            actionBundle.putString(TimesheetFragment.TIMESHEET_JSON_INTENT_KEY, toJson(getItem(viewHolder.getBindingAdapterPosition())));
            actionBundle.putString(TimesheetFragment.TIMESHEET_ACTION_KEY, TimesheetFragment.TIMESHEET_ACTION_DELETE);
            fragmentManager.setFragmentResult(TimesheetFragment.TIMESHEET_REQUEST_KEY, actionBundle);
        });
        */
        viewHolder.itemView.findViewById(R.id.ic_timesheet_row_view).setOnClickListener(v -> {
            Bundle actionBundle = new Bundle();
            actionBundle.putString(TimesheetFragment.TIMESHEET_JSON_KEY, toJson(getItem(viewHolder.getBindingAdapterPosition())));
            actionBundle.putString(TimesheetFragment.TIMESHEET_ACTION_KEY, TimesheetFragment.TIMESHEET_ACTION_VIEW);
            fragmentManager.setFragmentResult(TimesheetFragment.TIMESHEET_REQUEST_KEY, actionBundle);
        });
        return viewHolder;
    }

    private String toJson(Timesheet timesheet) {
        try {
            return Utility.gsonMapper().toJson(timesheet);
        } catch (Exception e) {
            Log.e("getTimesheetAsJson", e.toString());
            throw new RuntimeException("unable to parse object to json! " + e);
        }
    }

    @Override
    public void onBindViewHolder(TimesheetViewHolder holder, int position) {
        holder.bindListLine(getItem(position));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(Utility.buildTag(getClass(), "onItemClick"), "position: " + position + ", id: " + id);
        notifyItemRangeRemoved(position, 1);
    }

    /**
     *
     */
    public static class TimesheetDiff extends DiffUtil.ItemCallback<Timesheet> {
        @Override
        public boolean areItemsTheSame(@NonNull Timesheet oldItem, @NonNull Timesheet newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Timesheet oldItem, @NonNull Timesheet newItem) {
            return oldItem.equals(newItem);
        }
    }
}
