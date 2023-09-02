package com.gunnarro.android.terex.domain.entity;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity(tableName = "assignment")
public class Assignment {

    @PrimaryKey(autoGenerate = true)
    private int id;
    @Embedded
    private Company company;
}
