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
@Entity(tableName = "client")
public class ClientWithProject {

    @Embedded
    private Client client;

    public ClientWithProject() {
    }

    @Ignore
    public ClientWithProject(Client client, List<Project> projectList) {
        this.client = client;
        this.projectList = projectList;
    }

    @Relation(parentColumn = "company_id", entityColumn = "id")
    private Company company;

    @Relation(parentColumn = "id", entityColumn = "client_id")
    private List<Project> projectList;

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public List<Project> getProjectList() {
        return projectList;
    }

    public void setProjectList(List<Project> projectList) {
        this.projectList = projectList;
    }
}
