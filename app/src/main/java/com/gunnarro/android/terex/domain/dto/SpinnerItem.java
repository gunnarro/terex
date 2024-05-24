package com.gunnarro.android.terex.domain.dto;

import androidx.annotation.NonNull;

public record SpinnerItem(Long id, String name) {

    /**l
     * Makes that the spinner only shows the name in the drop down list.
     */
    @NonNull
    @Override
    public String toString() {
        return name;
    }

}
