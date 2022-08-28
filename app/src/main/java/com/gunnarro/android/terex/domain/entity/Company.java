package com.gunnarro.android.terex.domain.entity;

import androidx.room.Embedded;
import androidx.room.PrimaryKey;

/**
 * Used as embedded
 */
public class Company {
    String name;
    Integer organizationNumber;
    Integer bankAccountNumber;

    @Embedded
    Address businessAddress;

    @Embedded
    Contact contact;
}
