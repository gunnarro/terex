package com.gunnarro.android.terex.domain.entity;

import androidx.room.ColumnInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * used as embedded
 */

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Address {
    @ColumnInfo(name = "street_name")
    String streetName;
    @ColumnInfo(name = "street_number")
    String streetNumber;
    @ColumnInfo(name = "street_number_prefix")
    String streetNumberPrefix;
    @ColumnInfo(name = "post_code")
    String postCode;
    @ColumnInfo(name = "city")
    String city;
    @ColumnInfo(name = "country_code")
    String countryCode;


    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getStreetNumberPrefix() {
        return streetNumberPrefix;
    }

    public void setStreetNumberPrefix(String streetNumberPrefix) {
        this.streetNumberPrefix = streetNumberPrefix;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

}
