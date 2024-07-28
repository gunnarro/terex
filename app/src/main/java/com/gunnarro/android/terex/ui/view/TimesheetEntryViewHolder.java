package com.gunnarro.android.terex.ui.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.gunnarro.android.terex.R;
import com.gunnarro.android.terex.domain.dto.TimesheetEntryDto;
import com.gunnarro.android.terex.utility.Utility;

import java.time.format.DateTimeFormatter;
import java.util.Locale;


public class TimesheetEntryViewHolder extends RecyclerView.ViewHolder {
    private final ImageView timesheetEntryDeleteIconView;
    private final TextView timesheetEntryLineHeaderView;
    private final TextView timesheetEntryLine1StatusView;
    private final TextView timesheetEntryLine1LabelView;
    private final TextView timesheetEntryLine1ValueView;
    private final TextView timesheetEntryLine2LabelView;
    private final TextView timesheetEntryLine2ValueView;

    private TimesheetEntryViewHolder(View itemView) {
        super(itemView);
        timesheetEntryDeleteIconView = itemView.findViewById(R.id.ic_timesheet_entry_row_delete);
        timesheetEntryLineHeaderView = itemView.findViewById(R.id.timesheet_entry_line_header);
        timesheetEntryLine1StatusView = itemView.findViewById(R.id.timesheet_entry_line_1_status);
        timesheetEntryLine1LabelView = itemView.findViewById(R.id.timesheet_entry_line_1_label);
        timesheetEntryLine1ValueView = itemView.findViewById(R.id.timesheet_entry_line_1_value);
        timesheetEntryLine2LabelView = itemView.findViewById(R.id.timesheet_entry_line_2_label);
        timesheetEntryLine2ValueView = itemView.findViewById(R.id.timesheet_entry_line_2_value);
    }

    public static TimesheetEntryViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_timesheet_entry_item, parent, false);
        return new TimesheetEntryViewHolder(view);
    }

    public void bindListLine(TimesheetEntryDto timesheetEntryDto) {
        if (timesheetEntryDto.isClosed()) {
            // hide the delete icon
            timesheetEntryDeleteIconView.setVisibility(View.GONE);
        }
        timesheetEntryLineHeaderView.setText(timesheetEntryDto.getProjectDto().getName());
        timesheetEntryLine1StatusView.setText(timesheetEntryDto.getWorkdayDate().format(DateTimeFormatter.ofPattern("dd", Locale.getDefault())));
        // can have status OPEN or CLOSED
        if (timesheetEntryDto.isOpen()) {
            timesheetEntryLine1StatusView.setTextColor(timesheetEntryLine1StatusView.getResources().getColor(R.color.timesheet_entry_status_open, null));
        } else if (timesheetEntryDto.isClosed()) {
            timesheetEntryLine1StatusView.setTextColor(timesheetEntryLine1StatusView.getResources().getColor(R.color.timesheet_entry_status_closed, null));
            itemView.findViewById(R.id.ic_timesheet_entry_row_delete).setVisibility(View.GONE);
        }
        timesheetEntryLine1LabelView.setText(timesheetEntryDto.getType());
        if (timesheetEntryDto.isRegularWorkDay()) {
            timesheetEntryLine1LabelView.setTextColor(timesheetEntryLine1StatusView.getResources().getColor(R.color.timesheet_entry_type_regular, null));
        } else if (timesheetEntryDto.isVacationDay()) {
            timesheetEntryLine1LabelView.setTextColor(timesheetEntryLine1StatusView.getResources().getColor(R.color.timesheet_entry_type_vacation, null));
        } else if (timesheetEntryDto.isSickDay()) {
            timesheetEntryLine1LabelView.setTextColor(timesheetEntryLine1StatusView.getResources().getColor(R.color.timesheet_entry_type_sick, null));
        }

        timesheetEntryLine1ValueView.setText("");
        timesheetEntryLine2LabelView.setText(String.format("%s - %s", Utility.formatTime(timesheetEntryDto.getStartTime()), Utility.formatTime(timesheetEntryDto.getEndTime())));
        timesheetEntryLine2ValueView.setText(timesheetEntryDto.getWorkedHours());
    }
}
