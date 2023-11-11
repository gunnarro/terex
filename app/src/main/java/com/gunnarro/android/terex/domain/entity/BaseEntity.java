package com.gunnarro.android.terex.domain.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;

import java.time.LocalDateTime;

public abstract class BaseEntity {

    // used in ForeignKey onDelete attribute
    public static final int NO_ACTION = 1;
    public static final int RESTRICT = 2;
    public static final int SET_NULL = 3;
    public static final int SET_DEFAULT = 4;
    public static final int CASCADE = 5;

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
