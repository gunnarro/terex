package com.gunnarro.android.terex.domain.entity;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Relation;
import androidx.room.TypeConverters;

import com.gunnarro.android.terex.domain.converter.LocalDateConverter;
import com.gunnarro.android.terex.domain.converter.LocalDateTimeConverter;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TypeConverters({LocalDateConverter.class, LocalDateTimeConverter.class})
@Entity(tableName = "consultant_broker")
public class ConsultantBrokerWithProject {

    @Embedded
    private ConsultantBroker consultantBroker;

    public ConsultantBrokerWithProject() {
    }

    @Ignore
    public ConsultantBrokerWithProject(ConsultantBroker consultantBroker, List<Project> projectList) {
        this.consultantBroker = consultantBroker;
        this.projectList = projectList;
    }

    @Relation(
            parentColumn = "id",
            entityColumn = "client_id"
    )
    private List<Project> projectList;

    public ConsultantBroker getConsultantBroker() {
        return consultantBroker;
    }

    public void setConsultantBroker(ConsultantBroker consultantBroker) {
        this.consultantBroker = consultantBroker;
    }

    public List<Project> getProjectList() {
        return projectList;
    }

    public void setProjectList(List<Project> projectList) {
        this.projectList = projectList;
    }
}
