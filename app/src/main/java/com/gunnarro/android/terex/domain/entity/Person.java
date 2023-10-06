package com.gunnarro.android.terex.domain.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.gunnarro.android.terex.domain.converter.LocalDateConverter;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TypeConverters({LocalDateConverter.class})
@Entity(tableName = "person", indices = {@Index(value = {"client_name", "project_code", "year", "month"},
        unique = true)})
public class Person {
    @NotNull
    @PrimaryKey(autoGenerate = true)
    public Long id;
    @ColumnInfo(name = "first_name")
    String firstName;
    @ColumnInfo(name = "middle_name")
    String middleName;
    @ColumnInfo(name = "last_name")
    String lastName;
    @ColumnInfo(name = "date_of_birth")
    LocalDate dateOfBirth;
    @ColumnInfo(name = "social_security_number")
    String socialSecurityNumber;

    @NotNull
    public Long getId() {
        return id;
    }

    public void setId(@NotNull Long id) {
        this.id = id;
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

    @ColumnInfo(name = "gender")
    String gender;
    @ColumnInfo(name = "marital_status")
    String maritalStatus;



}
