package com.gunnarro.android.terex.ui.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.gunnarro.android.terex.R;
import com.gunnarro.android.terex.domain.entity.TimesheetEntry;
import com.gunnarro.android.terex.utility.Utility;

import java.time.format.DateTimeFormatter;
import java.util.Locale;


public class TimesheetEntryViewHolder extends RecyclerView.ViewHolder {
    private final ImageView timesheetEntryDeleteIconView;
    private final TextView timesheetEntryLineHeaderView;
    private final TextView timesheetEntryLine1StatusView;
    private final TextView timesheetEntryLine1LabelView;
    private final TextView timesheetEntryLine1ValueView;

    private TimesheetEntryViewHolder(View itemView) {
        super(itemView);
        timesheetEntryDeleteIconView = itemView.findViewById(R.id.ic_timesheet_entry_row_delete);
        timesheetEntryLineHeaderView = itemView.findViewById(R.id.timesheet_entry_line_header);
        timesheetEntryLine1StatusView = itemView.findViewById(R.id.timesheet_entry_line_1_status);
        timesheetEntryLine1LabelView = itemView.findViewById(R.id.timesheet_entry_line_1_label);
        timesheetEntryLine1ValueView = itemView.findViewById(R.id.timesheet_entry_line_1_value);
    }

    public static TimesheetEntryViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_timesheet_entry_item, parent, false);
        return new TimesheetEntryViewHolder(view);
    }

    public void bindListLine(TimesheetEntry timesheetEntry) {
        if (timesheetEntry.isClosed()) {
            // hide the delete icon
            timesheetEntryDeleteIconView.setVisibility(View.GONE);
        }
        timesheetEntryLineHeaderView.setText(timesheetEntry.getType().toLowerCase());
        if (timesheetEntry.isRegularWorkDay()) {
            timesheetEntryLineHeaderView.setTextColor(timesheetEntryLine1StatusView.getResources().getColor(R.color.timesheet_entry_type_regular, null));
        } else if (timesheetEntry.isVacationDay()) {
            timesheetEntryLineHeaderView.setTextColor(timesheetEntryLine1StatusView.getResources().getColor(R.color.timesheet_entry_type_vacation, null));
        } else if (timesheetEntry.isSickDay()) {
            timesheetEntryLineHeaderView.setTextColor(timesheetEntryLine1StatusView.getResources().getColor(R.color.timesheet_entry_type_sick, null));
        }

        timesheetEntryLine1StatusView.setText(timesheetEntry.getWorkdayDate().format(DateTimeFormatter.ofPattern("dd", Locale.getDefault())));
        // can have status OPEN or CLOSED
        if (timesheetEntry.isOpen()) {
            timesheetEntryLine1StatusView.setTextColor(timesheetEntryLine1StatusView.getResources().getColor(R.color.timesheet_entry_status_open, null));
        } else if (timesheetEntry.isClosed()) {
            timesheetEntryLine1StatusView.setTextColor(timesheetEntryLine1StatusView.getResources().getColor(R.color.timesheet_entry_status_closed, null));
            itemView.findViewById(R.id.ic_timesheet_entry_row_delete).setVisibility(View.GONE);
        }
        timesheetEntryLine1LabelView.setText(String.format("%s - %s", Utility.formatTime(timesheetEntry.getStartTime()), Utility.formatTime(timesheetEntry.getEndTime())));
        timesheetEntryLine1ValueView.setText(Utility.getTimeDiffInHours(timesheetEntry.getStartTime(), timesheetEntry.getEndTime()));
    }
}
