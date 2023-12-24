package com.gunnarro.android.terex.domain.converter;

import androidx.annotation.Nullable;
import androidx.room.TypeConverter;

import java.time.LocalTime;

public class LocalTimeConverter {

    private LocalTimeConverter() {
        throw new AssertionError();
    }

    @TypeConverter
    public static LocalTime toLocalTime(@Nullable Long nanoOfDay) {
        return nanoOfDay == null ? null : LocalTime.ofNanoOfDay(nanoOfDay);
    }

    @TypeConverter
    public static Long fromLocalTime(@Nullable LocalTime localTime) {
        return localTime == null ? null : localTime.toNanoOfDay();
    }
}
