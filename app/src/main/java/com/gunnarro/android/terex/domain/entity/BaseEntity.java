package com.gunnarro.android.terex.domain.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.TypeConverters;

import com.gunnarro.android.terex.domain.converter.LocalDateTimeConverter;

import java.time.LocalDateTime;

public abstract class BaseEntity {

    @NonNull
    @ColumnInfo(name = "created_date")
    private LocalDateTime createdDate;
    @NonNull
    @ColumnInfo(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;


    @NonNull
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(@NonNull LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    @NonNull
    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(@NonNull LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

}
