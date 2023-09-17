package com.gunnarro.android.terex.domain.entity;

import androidx.room.ColumnInfo;

import dagger.Component;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
    @ColumnInfo(name = "private_mobile_number")
    String privateMobileNumber;
    @ColumnInfo(name = "work_mobile_number")
    String workMobileNumber;
    @ColumnInfo(name = "email_address")
    String emailAddress;
}
