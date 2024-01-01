package com.gunnarro.android.terex.domain.entity;

import androidx.room.ColumnInfo;

import org.jetbrains.annotations.Nullable;

public class ConsultantAssignment {
    /**
     * The contract between the consultant broker and the consultant
     * The contract holds that startup and end date, conditions, etc
     * Before the end date is reached, the contract must be renewal or terminates.
     */
    @Nullable
    @ColumnInfo(name = "project_contract_id")
    private Long projectContractId;
}
