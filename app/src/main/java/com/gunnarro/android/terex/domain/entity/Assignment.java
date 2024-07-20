package com.gunnarro.android.terex.domain.entity;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.TypeConverters;

import com.gunnarro.android.terex.domain.converter.LocalDateConverter;
import com.gunnarro.android.terex.domain.converter.LocalDateTimeConverter;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

/**
 * TODO supported in nest version
 */
@Getter
@Setter
@TypeConverters({LocalDateConverter.class, LocalDateTimeConverter.class})
@Entity(tableName = "assignment", indices = {@Index(value = {""},
        unique = true)})
public class Assignment extends BaseEntity {
    @ColumnInfo(name = "description")
    private String description;
    @ColumnInfo(name = "start_date")
    private LocalDate startDate;
    @ColumnInfo(name = "end_date")
    private LocalDate endDate;
    @ColumnInfo(name = "hourlyRate")
    private Integer hourlyRate;
}
