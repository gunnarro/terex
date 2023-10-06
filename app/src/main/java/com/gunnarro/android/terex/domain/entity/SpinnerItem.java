package com.gunnarro.android.terex.domain.entity;

import androidx.annotation.NonNull;

public record SpinnerItem(Long id, String name) {

    @NonNull
    @Override
    public String toString() {
        return name;
    }

}
