package com.gunnarro.android.terex.domain.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.gunnarro.android.terex.domain.converter.LocalDateConverter;
import com.gunnarro.android.terex.domain.converter.LocalDateTimeConverter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TypeConverters({LocalDateConverter.class, LocalDateTimeConverter.class})
@Entity(tableName = "organization", indices = {@Index(value = {"organization_number"},
        unique = true)})
public class Organization extends BaseEntity {
    @PrimaryKey(autoGenerate = true)
    private Long id;

    /**
     * The foreign key in the child table will generally reference a primary key in the parent table.
     */
    @ColumnInfo(name = "address_id")
    private Long addressId;

    @ColumnInfo(name = "contact_info_id")
    private Long contactInfoId;

    @ColumnInfo(name = "contact_person_id")
    private Long contactPersonId;

    @ColumnInfo(name = "organization_name")
    private String name;
    @ColumnInfo(name = "organization_number")
    private String organizationNumber;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public Long getContactInfoId() {
        return contactInfoId;
    }

    public void setContactInfoId(Long contactInfoId) {
        this.contactInfoId = contactInfoId;
    }

    public Long getContactPersonId() {
        return contactPersonId;
    }

    public void setContactPersonId(Long contactPersonId) {
        this.contactPersonId = contactPersonId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrganizationNumber() {
        return organizationNumber;
    }

    public void setOrganizationNumber(String organizationNumber) {
        this.organizationNumber = organizationNumber;
    }

    public String getOrganizationIndustryType() {
        return organizationIndustryType;
    }

    public void setOrganizationIndustryType(String organizationIndustryType) {
        this.organizationIndustryType = organizationIndustryType;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

    /**
     * Type of industry, for example consultant, broker, bank, telecom, etc
     * I.e, what kind of services the company offers
     */
    @ColumnInfo(name = "organization_industry_type")
    private String organizationIndustryType;

    @ColumnInfo(name = "bank_account_number")
    private String bankAccountNumber;



}
