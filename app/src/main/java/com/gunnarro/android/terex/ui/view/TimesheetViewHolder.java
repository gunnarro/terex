package com.gunnarro.android.terex.ui.view;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.gunnarro.android.terex.R;
import com.gunnarro.android.terex.domain.entity.Timesheet;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class TimesheetViewHolder extends RecyclerView.ViewHolder {
    private int TITLE_MAX_LENGTH = 35;
    private final TextView timesheetLineHeaderView;
    private final TextView timesheetLine1StatusView;
    private final TextView timesheetLine1LabelView;
    private final TextView timesheetLine1ValueView;
    private final TextView timesheetLine2LabelView;
    private final TextView timesheetLine2ValueView;


    private TimesheetViewHolder(View itemView) {
        super(itemView);
        timesheetLineHeaderView = itemView.findViewById(R.id.timesheet_line_header);
        timesheetLine1StatusView = itemView.findViewById(R.id.timesheet_line_1_status);
        timesheetLine1LabelView = itemView.findViewById(R.id.timesheet_line_1_label);
        timesheetLine1ValueView = itemView.findViewById(R.id.timesheet_line_1_value);
        timesheetLine2LabelView = itemView.findViewById(R.id.timesheet_line_2_label);
        timesheetLine2ValueView = itemView.findViewById(R.id.timesheet_line_2_value);
    }

    public static TimesheetViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_timesheet_item, parent, false);
        return new TimesheetViewHolder(view);
    }


    public void bindListLine(Timesheet timesheet) {
        String title = String.format("%s:%s:%s", timesheet.getId(), timesheet.getClientName(), timesheet.getProjectCode());
        if (title.length() > TITLE_MAX_LENGTH) {
            title = title.substring(0, TITLE_MAX_LENGTH) + "...";
        }
        timesheetLineHeaderView.setText(title);
        timesheetLine1StatusView.setText(timesheet.getFromDate().format(DateTimeFormatter.ofPattern("MMM", Locale.getDefault())));

        if (timesheet.isOpen()) {
            timesheetLine1StatusView.setTextColor(timesheetLine1StatusView.getResources().getColor(R.color.timesheet_status_open, null));
        } else if (timesheet.isActive()) {
            timesheetLine1StatusView.setTextColor(timesheetLine1StatusView.getResources().getColor(R.color.timesheet_status_active, null));
        } else if (timesheet.isCompleted()) {
            timesheetLine1StatusView.setTextColor(timesheetLine1StatusView.getResources().getColor(R.color.timesheet_status_completed, null));
        } else if (timesheet.isBilled()) {
            timesheetLine1StatusView.setTextColor(timesheetLine1StatusView.getResources().getColor(R.color.timesheet_status_billed, null));
        }
        timesheetLine1LabelView.setText(R.string.lbl_worked_days);
        timesheetLine1ValueView.setText(String.format("%s of %s days", "x", timesheet.getWorkingDaysInMonth()));
        timesheetLine2LabelView.setText(R.string.lbl_worked_hours);
        timesheetLine2ValueView.setText(String.format("%s of %s hours", "x", timesheet.getWorkingHoursInMonth()));
    }
}
