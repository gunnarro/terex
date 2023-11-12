package com.gunnarro.android.terex.domain.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.gunnarro.android.terex.domain.converter.LocalDateConverter;
import com.gunnarro.android.terex.domain.converter.LocalDateTimeConverter;
import com.gunnarro.android.terex.domain.converter.LocalTimeConverter;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
//@AllArgsConstructor
@Getter
@Setter
@TypeConverters({LocalDateConverter.class, LocalTimeConverter.class, LocalDateTimeConverter.class})
@Entity(tableName = "employment",
        foreignKeys = {@ForeignKey(entity = Timesheet.class, parentColumns = "id", childColumns = "timesheet_id", onDelete = BaseEntity.CASCADE)},
        indices = {@Index(value = {"id"}, unique = true)})
public class Employment extends BaseEntity {

    @PrimaryKey(autoGenerate = true)
    private Long id;

    private String employmentType;
    private String employmentStatus;

}
