package com.gunnarro.android.terex.domain.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "project")
public class Project {

    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "project_name")
    private String projectName;
    @ColumnInfo(name = "comments")
    private List<Timesheet> timesheets;
}
