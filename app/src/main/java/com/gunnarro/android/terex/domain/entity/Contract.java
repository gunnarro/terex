package com.gunnarro.android.terex.domain.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.gunnarro.android.terex.domain.converter.LocalDateConverter;
import com.gunnarro.android.terex.domain.converter.LocalDateTimeConverter;

import java.time.LocalDateTime;

import lombok.NoArgsConstructor;

@NoArgsConstructor
@TypeConverters({LocalDateConverter.class, LocalDateTimeConverter.class})
@Entity(tableName = "contract")
public class Contract extends BaseEntity {

    @PrimaryKey(autoGenerate = true)
    private Long id;

    @NonNull
    @ColumnInfo(name = "start_date")
    private LocalDateTime startDate;
    @NonNull
    @ColumnInfo(name = "end_date")
    private LocalDateTime endDate;

    @ColumnInfo(name = "hired_date")
    private LocalDateTime hiredDate;

    @ColumnInfo(name = "hourly_rate")
    private Integer hourlyRate;

    @ColumnInfo(name = "assignment_type")
    private String assignmentType;

    @ColumnInfo(name = "assignment_description")
    private String assignmentDescription;
}
