package com.gunnarro.android.terex.domain.entity;

import androidx.room.ColumnInfo;

import java.time.LocalDate;

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
    @ColumnInfo(name = "customer_company_id")
    Integer customerCompanyId;
    @ColumnInfo(name = "consultant_company_id")
    Integer consultantCompanyId;
    @ColumnInfo(name = "contract_signing_date")
    LocalDate contractSigningDate;
    @ColumnInfo(name = "start_date")
    LocalDate startDate;
    @ColumnInfo(name = "end_date")
    LocalDate endDate;
}
