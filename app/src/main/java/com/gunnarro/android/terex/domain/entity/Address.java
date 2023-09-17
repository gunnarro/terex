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
@Getter
@Setter
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
