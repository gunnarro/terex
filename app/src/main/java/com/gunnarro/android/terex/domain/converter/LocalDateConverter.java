package com.gunnarro.android.terex.domain.converter;

import androidx.annotation.Nullable;
import androidx.room.TypeConverter;

import java.time.LocalDate;

public class LocalDateConverter {

    private LocalDateConverter() {
        throw new AssertionError();
    }

    @TypeConverter
    public static LocalDate toLocalDate(@Nullable Long epoch) {
        return epoch == null ? null : LocalDate.ofEpochDay(epoch);
    }

    @TypeConverter
    public static Long fromLocalDate(@Nullable LocalDate localDate) {
        return localDate == null ? null : localDate.toEpochDay();
    }
}
