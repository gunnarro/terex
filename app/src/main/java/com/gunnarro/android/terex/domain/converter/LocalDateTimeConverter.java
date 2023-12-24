package com.gunnarro.android.terex.domain.converter;

import androidx.annotation.Nullable;
import androidx.room.TypeConverter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class LocalDateTimeConverter {

    private LocalDateTimeConverter() {
        throw new AssertionError();
    }

    @TypeConverter
    public static LocalDateTime toLocalDateTime(@Nullable Long epoch) {
        return epoch == null ? null : LocalDateTime.ofInstant(Instant.ofEpochMilli(epoch), ZoneId.systemDefault());
    }

    @TypeConverter
    public static Long fromLocalDateTime(@Nullable LocalDateTime localDateTime) {
        return localDateTime == null ? null : localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}
