package com.gunnarro.android.terex.domain.entity;

import dagger.Component;
import lombok.Builder;
import lombok.Getter;

/**
 * used as embedded
 */
@Builder
@Getter
public class Address {
    String streetName;
    String streetNumber;
    Integer postCode;
    String country;
}
