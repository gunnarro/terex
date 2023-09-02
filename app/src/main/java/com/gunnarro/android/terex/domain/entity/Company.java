package com.gunnarro.android.terex.domain.entity;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;

import dagger.Component;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Used as embedded
 */
//@Builder
@NoArgsConstructor
@Getter
public class Company {
    @ColumnInfo(name = "name")
    String name;
    @ColumnInfo(name = "organization_number")
    Integer organizationNumber;
    @ColumnInfo(name = "bank_account_number")
    Integer bankAccountNumber;
    @Embedded
    Address businessAddress;
    @Embedded
    Contact contact;
}
