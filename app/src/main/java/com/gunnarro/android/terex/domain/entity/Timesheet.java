package com.gunnarro.android.terex.domain.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;
import java.util.List;

import lombok.Getter;

/**
 * not in use
 */
@Getter
@Entity(tableName = "timesheet")
public class Timesheet {
    @PrimaryKey(autoGenerate = true)
    public int uid;
    @ColumnInfo(name = "title")
    private String title;
    @ColumnInfo(name = "from_date_time")
    private Date fromDate;
    @ColumnInfo(name = "to_date_time")
    private Date toDate;
    @ColumnInfo(name = "status")
    private String status;
    @ColumnInfo(name = "comments")
    private String comments;

    private List<TimesheetEntry> timesheetEntryList;
}
