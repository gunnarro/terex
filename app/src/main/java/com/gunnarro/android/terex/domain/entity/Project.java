package com.gunnarro.android.terex.domain.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "project")
public class Project {

    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "client")
    private Integer clientId;

    @ColumnInfo(name = "project_name")
    private String name;

    @ColumnInfo(name = "project_description")
    private String description;

    @ColumnInfo(name = "project_hourly_rate")
    private Integer hourly_rate;

}
