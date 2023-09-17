package com.gunnarro.android.terex.domain.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity(tableName = "contract")
public class Contract {

    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "created_date")
    private LocalDateTime createdDate;

    @NonNull
    @ColumnInfo(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

    @ColumnInfo(name = "project_start_date")
    private LocalDateTime projectStartDate;

    @NonNull
    @ColumnInfo(name = "project_end_date")
    private LocalDateTime projectEndDate;

    @ColumnInfo(name = "project_hourly_rate")
    private Integer hourly_rate;
}
