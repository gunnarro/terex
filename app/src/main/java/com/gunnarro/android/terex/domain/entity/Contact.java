package com.gunnarro.android.terex.domain.entity;

import androidx.room.ColumnInfo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Used as embedded
 */

@NoArgsConstructor
@Getter
@Setter
public class Contact {
    @ColumnInfo(name = "mobile_number")
    String mobileNumber;
    @ColumnInfo(name = "email_address")
    String emailAddress;

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}
