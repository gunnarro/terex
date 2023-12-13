package com.gunnarro.android.terex.ui.adapter;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.gunnarro.android.terex.R;
import com.gunnarro.android.terex.domain.entity.Timesheet;
import com.gunnarro.android.terex.exception.TerexApplicationException;
import com.gunnarro.android.terex.ui.fragment.TimesheetListFragment;
import com.gunnarro.android.terex.ui.view.TimesheetViewHolder;
import com.gunnarro.android.terex.utility.Utility;

public class TimesheetListAdapter extends ListAdapter<Timesheet, TimesheetViewHolder> implements AdapterView.OnItemClickListener {

    private final NavController navController;

    public TimesheetListAdapter(@NonNull NavController navController, @NonNull DiffUtil.ItemCallback<Timesheet> diffCallback) {
        super(diffCallback);
        this.setHasStableIds(true);
        this.navController = navController;
    }

    @NonNull
    @Override
    public TimesheetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TimesheetViewHolder viewHolder = TimesheetViewHolder.create(parent);
        viewHolder.itemView.findViewById(R.id.ic_timesheet_row_view).setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString(TimesheetListFragment.TIMESHEET_JSON_KEY, toJson(getItem(viewHolder.getBindingAdapterPosition())));
            bundle.putString(TimesheetListFragment.TIMESHEET_ACTION_KEY, TimesheetListFragment.TIMESHEET_ACTION_EDIT);
            navController.navigate(R.id.nav_from_timesheet_list_to_timesheet_entry_list, bundle);
        });
        return viewHolder;
    }

    private String toJson(Timesheet timesheet) {
        try {
            return Utility.gsonMapper().toJson(timesheet);
        } catch (Exception e) {
            Log.e("getTimesheetAsJson", e.toString());
            throw new TerexApplicationException("unable to parse object to json!", "505500", e);
        }
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
