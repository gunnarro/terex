package com.gunnarro.android.terex.domain.entity;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

import lombok.NoArgsConstructor;

//@Builder
@NoArgsConstructor
@Entity(tableName = "recruitment_company")
public class RecruitmentCompany {

    @PrimaryKey(autoGenerate = true)
    private int id;
    @Embedded
    private Company company;

    @ColumnInfo(name = "projects")
    private List<Project> projects;

}
