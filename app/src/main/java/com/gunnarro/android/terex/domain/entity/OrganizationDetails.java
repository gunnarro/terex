package com.gunnarro.android.terex.domain.entity;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Relation;

import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity(tableName = "organization")
public class OrganizationDetails {

    @Embedded
    private Organization organization;

    @Relation(parentColumn = "address_id", entityColumn = "id")
    private Address companyAddress;

    @Relation(parentColumn = "contact_info_id", entityColumn = "id")
    private ContactInfo companyContactInfo;

    @Relation(parentColumn = "contact_person_id", entityColumn = "id")
    private Person contactPerson;

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public Address getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(Address companyAddress) {
        this.companyAddress = companyAddress;
    }

    public ContactInfo getCompanyContactInfo() {
        return companyContactInfo;
    }

    public void setCompanyContactInfo(ContactInfo companyContactInfo) {
        this.companyContactInfo = companyContactInfo;
    }

    public Person getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(Person contactPerson) {
        this.contactPerson = contactPerson;
    }
}
