package com.gunnarro.android.terex.domain.entity;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Relation;
import androidx.room.TypeConverters;

import com.gunnarro.android.terex.domain.converter.LocalDateConverter;
import com.gunnarro.android.terex.domain.converter.LocalDateTimeConverter;

import java.util.List;

import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity(tableName = "project")
public class ProjectWithTimesheet {

    @Embedded
    private Project project;

    @Relation(
            parentColumn = "id",
            entityColumn = "project_id"
    )
    private List<Timesheet> timesheetList;

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public List<Timesheet> getTimesheetList() {
        return timesheetList;
    }

    public void setTimesheetList(List<Timesheet> timesheetList) {
        this.timesheetList = timesheetList;
    }
}
