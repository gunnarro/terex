package com.gunnarro.android.terex.domain.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.gunnarro.android.terex.domain.converter.LocalDateConverter;
import com.gunnarro.android.terex.domain.converter.LocalDateTimeConverter;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import lombok.NoArgsConstructor;

@NoArgsConstructor
@TypeConverters({LocalDateConverter.class, LocalDateTimeConverter.class})
@Entity(tableName = "project")
public class Project extends BaseEntity {

    @PrimaryKey(autoGenerate = true)
    public Long id;

    @ColumnInfo(name = "project_contract_id")
    private Integer projectContractId;

    @NotNull
    @ColumnInfo(name = "company_name")
    private String companyName;

    @NotNull
    @ColumnInfo(name = "project_name")
    private String name;

    @ColumnInfo(name = "project_description")
    private String description;

    @ColumnInfo(name = "project_status")
    private String status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getProjectContractId() {
        return projectContractId;
    }

    public void setProjectContractId(Integer projectContractId) {
        this.projectContractId = projectContractId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return companyName.equals(project.companyName) && name.equals(project.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(companyName, name);
    }
}
