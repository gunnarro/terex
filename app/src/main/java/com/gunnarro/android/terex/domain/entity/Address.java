package com.gunnarro.android.terex.domain.entity;

import androidx.room.ColumnInfo;

import lombok.Builder;
import lombok.Getter;

/**
 * used as embedded
 */
@Builder
@Getter
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

}
