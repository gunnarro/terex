package com.gunnarro.android.terex.ui.view;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.gunnarro.android.terex.R;
import com.gunnarro.android.terex.domain.entity.TimesheetEntry;
import com.gunnarro.android.terex.exception.TerexApplicationException;
import com.gunnarro.android.terex.utility.Utility;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class TimesheetEntryViewHolder extends RecyclerView.ViewHolder {
    private final ImageView timesheetEntryDeleteIconBtn;
    private final TextView timesheetEntryLineHeaderView;
    private final View timesheetEntryLine1StatusView;
    private final TextView timesheetEntryLine1LabelView;
    private final TextView timesheetEntryLine1ValueView;
    private final View timesheetEntryLine2StatusView;
    private final TextView timesheetEntryLine2LabelView;
    private final TextView timesheetEntryLine2ValueView;


    private TimesheetEntryViewHolder(View itemView) {
        super(itemView);
        timesheetEntryDeleteIconBtn = itemView.findViewById(R.id.ic_timesheet_entry_row_delete);
        timesheetEntryLineHeaderView = itemView.findViewById(R.id.timesheet_entry_line_header);
        timesheetEntryLine1StatusView = itemView.findViewById(R.id.timesheet_entry_line_1_status);
        timesheetEntryLine1LabelView = itemView.findViewById(R.id.timesheet_entry_line_1_label);
        timesheetEntryLine1ValueView = itemView.findViewById(R.id.timesheet_entry_line_1_value);
        timesheetEntryLine2StatusView = itemView.findViewById(R.id.timesheet_entry_line_2_status);
        timesheetEntryLine2LabelView = itemView.findViewById(R.id.timesheet_entry_line_2_label);
        timesheetEntryLine2ValueView = itemView.findViewById(R.id.timesheet_entry_line_2_value);
    }

    public static TimesheetEntryViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_timesheet_entry_item, parent, false);
        return new TimesheetEntryViewHolder(view);
    }


    public void bindListLine(TimesheetEntry timesheetEntry) {
        timesheetEntryLineHeaderView.setText(timesheetEntry.getWorkdayDate().format(DateTimeFormatter.ofPattern(Utility.WORKDAY_DATE_PATTERN, Locale.getDefault())));
        // can have status OPEN or BILLED
        if (timesheetEntry.isOpen()) {
            timesheetEntryLine1StatusView.setBackgroundColor(Color.parseColor("#0100f6"));
            timesheetEntryLine2StatusView.setBackgroundColor(Color.parseColor("#0100f6"));
        } else if (timesheetEntry.isBilled()) {
            timesheetEntryLine1StatusView.setBackgroundColor(Color.parseColor("#54aa00"));
            timesheetEntryLine2StatusView.setBackgroundColor(Color.parseColor("#54aa00"));
            timesheetEntryDeleteIconBtn.setVisibility(View.INVISIBLE);
        } else {
            throw new TerexApplicationException(String.format("Application error, unknown timesheet entry status! status=%s, timesheetEntryId=%s", timesheetEntry.getStatus(),timesheetEntry.getId()), "50034", null);
        }
        timesheetEntryLine1LabelView.setText(String.format("%s - %s", Utility.formatTime(timesheetEntry.getFromTime()), Utility.formatTime(timesheetEntry.getToTime())));
        timesheetEntryLine1ValueView.setText(Utility.getDateDiffInHours(timesheetEntry.getFromTime(), timesheetEntry.getToTime()));
        timesheetEntryLine2LabelView.setText(String.format("%s", timesheetEntry.getHourlyRate()));
        timesheetEntryLine2ValueView.setText(String.format("%s", timesheetEntry.getHourlyRate()*(timesheetEntry.getWorkedMinutes()/60)));
    }
}
