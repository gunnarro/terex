package com.gunnarro.android.terex.domain.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

/**
 * not in use
 */
@Entity(tableName = "timesheet_entry")
public class TimesheetEntry {
    @PrimaryKey(autoGenerate = true)
    public int uid;
    @ColumnInfo(name = "from_date_time")
    private Date fromDate;
    @ColumnInfo(name = "to_date_time")
    private Date toDate;
    @ColumnInfo(name = "break_in_min")
    private Integer breakInMin;
    @ColumnInfo(name = "status")
    private String status;
    @ColumnInfo(name = "comments")
    private String comments;
}
