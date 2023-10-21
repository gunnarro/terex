package com.gunnarro.android.terex.domain.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.gunnarro.android.terex.domain.converter.LocalDateConverter;
import com.gunnarro.android.terex.domain.converter.LocalDateTimeConverter;
import com.gunnarro.android.terex.domain.converter.LocalTimeConverter;

import java.time.LocalTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Holds name and a short description of the piece of work to be done and the period of time the work have been done.
 */
@NoArgsConstructor
@Getter
@Setter
@TypeConverters({LocalDateConverter.class, LocalTimeConverter.class, LocalDateTimeConverter.class})
@Entity(tableName = "work_task", indices = {@Index(value = {"timesheet_entry_id", "from_time"},
        unique = true)})
public class Task extends BaseEntity {

    @PrimaryKey(autoGenerate = true)
    private Long id;

    @NonNull
    @ColumnInfo(name = "timesheet_entry_id")
    private Long timesheetEntryId;

    @NonNull
    @ColumnInfo(name = "from_time")
    private LocalTime fromTime;

    @NonNull
    @ColumnInfo(name = "to_time")
    private LocalTime toTime;

    @NonNull
    @ColumnInfo(name = "description")
    private LocalTime description;
}
