package com.gunnarro.android.terex.domain.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity(tableName = "project")
public class Project {

    @PrimaryKey(autoGenerate = true)
    public Long id;

    @ColumnInfo(name = "project_contract_id")
    private Integer project_contract_id;

    @ColumnInfo(name = "company_name")
    private String company_name;

    @ColumnInfo(name = "project_name")
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getProject_contract_id() {
        return project_contract_id;
    }

    public void setProject_contract_id(Integer project_contract_id) {
        this.project_contract_id = project_contract_id;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @ColumnInfo(name = "project_description")
    private String description;

    @ColumnInfo(name = "project_status")
    private String status;



}
