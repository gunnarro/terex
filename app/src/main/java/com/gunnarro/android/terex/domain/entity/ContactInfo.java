package com.gunnarro.android.terex.domain.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.TypeConverters;

import com.gunnarro.android.terex.domain.converter.LocalDateConverter;
import com.gunnarro.android.terex.domain.converter.LocalDateTimeConverter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TypeConverters({LocalDateConverter.class, LocalDateTimeConverter.class})
@Entity(tableName = "contact_info")
public class ContactInfo extends BaseEntity {

    @ColumnInfo(name = "mobile_number")
    private String mobileNumber;
    @ColumnInfo(name = "mobile_number_country_code")
    private String mobileNumberCountryCode;
    @ColumnInfo(name = "email_address")
    private String emailAddress;

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getMobileNumberCountryCode() {
        return mobileNumberCountryCode;
    }

    public void setMobileNumberCountryCode(String mobileNumberCountryCode) {
        this.mobileNumberCountryCode = mobileNumberCountryCode;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}
