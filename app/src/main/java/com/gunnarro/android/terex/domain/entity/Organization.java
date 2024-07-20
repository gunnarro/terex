package com.gunnarro.android.terex.domain.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.TypeConverters;

import com.gunnarro.android.terex.domain.converter.LocalDateConverter;
import com.gunnarro.android.terex.domain.converter.LocalDateTimeConverter;

import org.jetbrains.annotations.NotNull;

import java.util.StringJoiner;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TypeConverters({LocalDateConverter.class, LocalDateTimeConverter.class})
@Entity(tableName = "organization", indices = {@Index(value = {"organization_number"},
        unique = true)})
public class Organization extends BaseEntity {

    /**
     * The foreign key in the child table will generally reference a primary key in the parent table.
     */
    @ColumnInfo(name = "business_address_id")
    private Long businessAddressId;

    @ColumnInfo(name = "postal_address_id")
    private Long postalAddressId;

    @ColumnInfo(name = "invoice_address_id")
    private Long invoiceAddressId;

    @ColumnInfo(name = "contact_info_id")
    private Long contactInfoId;

    @NotNull
    @ColumnInfo(name = "organization_name")
    private String name;

    @NotNull
    @ColumnInfo(name = "organization_number")
    private String organizationNumber;

    /**
     * Type of industry, for example consultant, broker, bank, telecom, etc
     * I.e, what kind of services the company offers
     */
    @ColumnInfo(name = "organization_industry_type")
    private String organizationIndustryType;

    @ColumnInfo(name = "bank_account_number")
    private String bankAccountNumber;

    public Long getBusinessAddressId() {
        return businessAddressId;
    }

    public void setBusinessAddressId(Long businessAddressId) {
        this.businessAddressId = businessAddressId;
    }

    public Long getPostalAddressId() {
        return postalAddressId;
    }

    public void setPostalAddressId(Long postalAddressId) {
        this.postalAddressId = postalAddressId;
    }

    public Long getInvoiceAddressId() {
        return invoiceAddressId;
    }

    public void setInvoiceAddressId(Long invoiceAddressId) {
        this.invoiceAddressId = invoiceAddressId;
    }

    public Long getContactInfoId() {
        return contactInfoId;
    }

    public void setContactInfoId(Long contactInfoId) {
        this.contactInfoId = contactInfoId;
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

    @NonNull
    @Override
    public String toString() {
        return new StringJoiner(", ", Organization.class.getSimpleName() + "[", "]")
                .add("id='" + getId() + "'")
                .add("name='" + name + "'")
                .add("organizationNumber='" + organizationNumber + "'")
                .add("organizationIndustryType='" + organizationIndustryType + "'")
                .toString();
    }
}
