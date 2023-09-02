package com.gunnarro.android.terex.domain.entity;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import dagger.Component;
import lombok.Builder;
import lombok.NoArgsConstructor;

//@Builder
@NoArgsConstructor
@Entity(tableName = "recruitment_company")
public class RecruitmentCompany {

    @PrimaryKey(autoGenerate = true)
    private int id;
    @Embedded
    private Company company;

}
