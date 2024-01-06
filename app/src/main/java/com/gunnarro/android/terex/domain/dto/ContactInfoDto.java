package com.gunnarro.android.terex.domain.dto;

public class ContactInfoDto {
    private Long id;

    private String mobileNumber;

    private String mobileNumberCountryCode;

    private Integer emailAddress;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Integer getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(Integer emailAddress) {
        this.emailAddress = emailAddress;
    }
}
