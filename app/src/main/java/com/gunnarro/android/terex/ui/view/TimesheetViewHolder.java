package com.gunnarro.android.terex.ui.view;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.gunnarro.android.terex.R;
import com.gunnarro.android.terex.domain.entity.Timesheet;
import com.gunnarro.android.terex.utility.Utility;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class TimesheetViewHolder extends RecyclerView.ViewHolder {
    private final TextView timesheetLineHeaderView;
    private final View timesheetLine1StatusView;
    private final TextView timesheetLine1LabelView;
    private final TextView timesheetLine1ValueView;
    private final View timesheetLine2StatusView;
    private final TextView timesheetLine2LabelView;
    private final TextView timesheetLine2ValueView;


    private TimesheetViewHolder(View itemView) {
        super(itemView);
        timesheetLineHeaderView = itemView.findViewById(R.id.timesheet_line_header);
        timesheetLine1StatusView = itemView.findViewById(R.id.timesheet_line_1_status);
        timesheetLine1LabelView = itemView.findViewById(R.id.timesheet_line_1_label);
        timesheetLine1ValueView = itemView.findViewById(R.id.timesheet_line_1_value);
        timesheetLine2StatusView = itemView.findViewById(R.id.timesheet_line_2_status);
        timesheetLine2LabelView = itemView.findViewById(R.id.timesheet_line_2_label);
        timesheetLine2ValueView = itemView.findViewById(R.id.timesheet_line_2_value);
    }

    public static TimesheetViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_timesheet_item, parent, false);
        return new TimesheetViewHolder(view);
    }


    public void bindListLine(Timesheet timesheet) {
        timesheetLineHeaderView.setText(timesheet.getFromDate().format(DateTimeFormatter.ofPattern(Utility.INVOICE_DATE_PATTERN, Locale.getDefault())));
        if (timesheet.getStatus().equals(Timesheet.TimesheetStatusEnum.OPEN.name())) {
            timesheetLine1StatusView.setBackgroundColor(Color.parseColor("#0100f6"));
            timesheetLine2StatusView.setBackgroundColor(Color.parseColor("#0100f6"));
        } else if (timesheet.getStatus().equals(Timesheet.TimesheetStatusEnum.BILLED.name())) {
            timesheetLine1StatusView.setBackgroundColor(Color.parseColor("#54aa00"));
            timesheetLine2StatusView.setBackgroundColor(Color.parseColor("#54aa00"));
        } else {
            timesheetLine1StatusView.setBackgroundColor(Color.parseColor("#f5f600"));
            timesheetLine2StatusView.setBackgroundColor(Color.parseColor("#f5f600"));
        }
        timesheetLine1LabelView.setText(R.string.lbl_worked_days);
        timesheetLine1ValueView.setText(String.format("%s of %s days", "x", timesheet.getWorkingDaysInMonth()));
        timesheetLine2LabelView.setText(R.string.lbl_worked_hours);
        timesheetLine2ValueView.setText(String.format("%s of %s hours", "x", timesheet.getWorkingHoursInMonth()));
    }
}
