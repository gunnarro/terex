package com.gunnarro.android.terex.domain.entity;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.util.List;

import lombok.NoArgsConstructor;

@NoArgsConstructor
/*@Entity(tableName = "recruitment_company",
        foreignKeys = {@ForeignKey(entity = Project.class,
                parentColumns = "id",
                childColumns = "project_id"
        )}
)*/
public class RecruitmentCompany {

   // @PrimaryKey(autoGenerate = true)
    private int id;
    //@Embedded
    private Company company;

   // @ColumnInfo(name = "project_id")
   // private List<Project> projects;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }


}
