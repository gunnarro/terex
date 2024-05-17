package com.gunnarro.android.terex.domain.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.TypeConverters;

import com.gunnarro.android.terex.domain.converter.LocalDateConverter;
import com.gunnarro.android.terex.domain.converter.LocalDateTimeConverter;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TypeConverters({LocalDateConverter.class, LocalDateTimeConverter.class})
@Entity(tableName = "person", indices = {@Index(value = {"first_name"}, unique = true)})
public class Person extends BaseEntity {

    @ColumnInfo(name = "address_id")
    private Long addressId;
    @ColumnInfo(name = "contact_info_id")
    private Long contactInfoId;
    /**
     * More suite for internalization, not all countries have first and last name
     */
    @NotNull
    @ColumnInfo(name = "full_name")
    private String fullName;
    @ColumnInfo(name = "first_name")
    private String firstName;
    @ColumnInfo(name = "middle_name")
    private String middleName;
    @ColumnInfo(name = "last_name")
    private String lastName;
    @ColumnInfo(name = "date_of_birth")
    private LocalDate dateOfBirth;
    @ColumnInfo(name = "social_security_number")
    private String socialSecurityNumber;
    @ColumnInfo(name = "gender")
    private String gender;
    @ColumnInfo(name = "marital_status")
    private String maritalStatus;

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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getSocialSecurityNumber() {
        return socialSecurityNumber;
    }

    public void setSocialSecurityNumber(String socialSecurityNumber) {
        this.socialSecurityNumber = socialSecurityNumber;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    @NonNull
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Person{");
        sb.append("id=").append(getId());
        sb.append(", addressId=").append(addressId);
        sb.append(", contactInfoId=").append(contactInfoId);
        sb.append(", fullName='").append(fullName).append('\'');
        sb.append(", firstName='").append(firstName).append('\'');
        sb.append(", middleName='").append(middleName).append('\'');
        sb.append(", lastName='").append(lastName).append('\'');
        sb.append(", dateOfBirth=").append(dateOfBirth);
        sb.append(", socialSecurityNumber='").append(socialSecurityNumber).append('\'');
        sb.append(", gender='").append(gender).append('\'');
        sb.append(", maritalStatus='").append(maritalStatus).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
