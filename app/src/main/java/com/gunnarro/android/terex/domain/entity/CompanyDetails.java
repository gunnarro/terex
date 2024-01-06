package com.gunnarro.android.terex.domain.entity;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.Relation;
import androidx.room.TypeConverters;

import com.gunnarro.android.terex.domain.converter.LocalDateConverter;
import com.gunnarro.android.terex.domain.converter.LocalDateTimeConverter;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Entity(tableName = "company")
public class CompanyDetails {

    @Embedded
    private Company company;

    @Relation(parentColumn = "address_id", entityColumn = "id")
    private Address companyAddress;

    @Relation(parentColumn = "contact_info_id", entityColumn = "id")
    private ContactInfo companyContactInfo;

    @Relation(parentColumn = "contact_person_id", entityColumn = "id")
    private Person contactPerson;

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
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
