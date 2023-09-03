package com.gunnarro.android.terex.domain.entity;

import androidx.room.ColumnInfo;

public class Person {
    @ColumnInfo(name = "first_name")
    String firstName;
    @ColumnInfo(name = "middle_name")
    String middleName;
    @ColumnInfo(name = "last_name")
    String lastName;
    @ColumnInfo(name = "date_of_birth")
    String dateOfBirth;
    @ColumnInfo(name = "social_security_number")
    String socialSecurityNumber;
    @ColumnInfo(name = "gender")
    String gender;
    @ColumnInfo(name = "marital_status")
    String materialStatus;
}
