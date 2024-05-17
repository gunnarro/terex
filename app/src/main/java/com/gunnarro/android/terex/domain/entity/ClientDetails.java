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
    private Person contactPerson;
    @Relation(parentColumn = "id", entityColumn = "client_id")
    private List<Project> projectList;

    @Ignore
    private OrganizationDetails organizationDetails;

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

    public OrganizationDetails getOrganizationDetails() {
        return organizationDetails;
    }

    public void setOrganizationDetails(OrganizationDetails organizationDetails) {
        this.organizationDetails = organizationDetails;
    }

    public Person getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(Person contactPerson) {
        this.contactPerson = contactPerson;
    }
}
