package com.gunnarro.android.terex.domain.entity;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Relation;

import java.util.List;


@Entity(tableName = "client")
public class ClientDetails {

    @Embedded
    private Client client;
    @Relation(parentColumn = "id", entityColumn = "client_id")
    private List<Project> projectList;

    @Ignore
    private CompanyDetails company;

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public CompanyDetails getCompany() {
        return company;
    }

    public void setCompany(CompanyDetails company) {
        this.company = company;
    }

    public List<Project> getProjectList() {
        return projectList;
    }

    public void setProjectList(List<Project> projectList) {
        this.projectList = projectList;
    }
}
