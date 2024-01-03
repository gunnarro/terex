package com.gunnarro.android.terex.domain.entity;

import androidx.room.Embedded;
import androidx.room.Relation;

public class CompanyDetails {

    @Embedded
    private Company company;

    @Relation(parentColumn = "address_id", entityColumn = "id")
    private Address companyAddress;

    @Relation(parentColumn = "contact_info_id", entityColumn = "id")
    private ContactInfo companyContactInfo;

    @Relation(parentColumn = "contact_person_id", entityColumn = "id")
    private Person contactPerson;
}
