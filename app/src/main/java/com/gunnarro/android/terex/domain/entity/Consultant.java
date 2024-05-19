package com.gunnarro.android.terex.domain.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.gunnarro.android.terex.domain.converter.LocalDateConverter;
import com.gunnarro.android.terex.domain.converter.LocalDateTimeConverter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TypeConverters({LocalDateConverter.class, LocalDateTimeConverter.class})
@Entity(tableName = "consultant")
public class Consultant extends BaseEntity {

    @PrimaryKey(autoGenerate = true)
    private Long id;

    @ColumnInfo(name = "company_id")
    private Long companyId;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "status")
    private String status;
}
