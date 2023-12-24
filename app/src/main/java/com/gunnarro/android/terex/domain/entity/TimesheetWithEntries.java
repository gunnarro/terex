package com.gunnarro.android.terex.domain.entity;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Relation;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Holder class for timesheet with timesheet entries
 */
public class TimesheetWithEntries {

    @Embedded
    private Timesheet timesheet;

    @Relation(
            parentColumn = "id",
            entityColumn = "timesheet_id"
    )
    private List<TimesheetEntry> timesheetEntryList;

    @NotNull
    public Timesheet getTimesheet() {
        return timesheet;
    }

    public void setTimesheet(Timesheet timesheet) {
        this.timesheet = timesheet;
    }

    public List<TimesheetEntry> getTimesheetEntryList() {
        return timesheetEntryList;
    }

    public void setTimesheetEntryList(List<TimesheetEntry> timesheetEntryList) {
        this.timesheetEntryList = timesheetEntryList;
    }

    @NonNull
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TimesheetWithEntries{");
        sb.append("timesheet=").append(timesheet);
        sb.append(", timesheetEntryList=").append(timesheetEntryList);
        sb.append('}');
        return sb.toString();
    }

}
