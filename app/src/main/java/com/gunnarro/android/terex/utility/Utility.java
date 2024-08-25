package com.gunnarro.android.terex.utility;

import android.content.Context;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.gunnarro.android.terex.exception.TerexApplicationException;
import com.gunnarro.android.terex.service.InvoiceService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Utility {

    public static final Integer DEFAULT_DAILY_BREAK_IN_SECONDS = 30 * 60;
    public static final Long DEFAULT_DAILY_WORKING_HOURS_IN_SECONDS = (long) ((8 * 60 * 60) - DEFAULT_DAILY_BREAK_IN_SECONDS);
    private static final Pattern POSITIVE_INTEGER_PATTERN = Pattern.compile("\\d+");
    private static final String DATE_TIME_PATTERN = "dd-MM-yyyy HH:mm";

    private static final String DATE_PATTERN = "dd-MM-yyyy";
    public static final String MONTH_YEAR_DATE_PATTERN = "MMMM yyyy";
    private static final String TIME_PATTERN = "HH:mm";

    private static final String HOUR_FORMAT = "%s.%s";
    private static String currentUUID;

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
            .registerTypeAdapter(LocalTime.class, new LocalTimeTypeAdapter())
            .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
            .setPrettyPrinting()
            .create();

    public static Gson gsonMapper() {
        return gson;
    }

    public static DateTimeFormatter getDateTimeFormatter() {
        return dateTimeFormatter;
    }

    private Utility() {
        genNewUUID();
    }

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN, Locale.getDefault());


    public static void genNewUUID() {
        currentUUID = java.util.UUID.randomUUID().toString();
    }

    public static String buildTag(Class<?> clazz, String tagName) {
        return new StringBuilder(clazz.getSimpleName())
                .append(".").append(tagName)
                .append(", UUID=").append(currentUUID)
                .append(", thread=").append(Thread.currentThread().getName())
                .toString();
    }

    /**
     * Convert 7:30 to 7.5 hours format
     * Return hours on the 7.5 format.
     */
    public static String fromSecondsToHours(Long seconds) {
        if (seconds == null) {
            return null;
        }
        LocalTime hours = LocalTime.ofSecondOfDay(seconds);
        return String.format(HOUR_FORMAT, hours.getHour(), (hours.getMinute() * 10 / 60));
    }

    /**
     * The hours format is hh.m, i.e 7.5.
     */
    public static Long fromHoursToSeconds(String hours) {
        if (hours == null || hours.isBlank()) {
            return null;
        }
        String tmpHours = hours;
        if (!hours.contains(".")) {
            // missing minutes, pad with .0
            tmpHours = hours + ".0";
        }
        String[] hhmm = tmpHours.split("\\.");
        if (hhmm.length != 2) {
            return null;
        }

        // may be non integers, if so, log error and return null
        try {
            int hh = Integer.parseInt(hhmm[0]);
            int mm = Integer.parseInt(hhmm[1]);

            return (long) LocalTime.of(hh, mm * 60 / 10).toSecondOfDay();
        } catch (Exception e) {
            Log.e("", e.getMessage());
            return null;
        }
    }

    public static String formatDateTime(LocalDateTime localDateTime) {
        if (localDateTime != null) {
            return localDateTime.format(dateTimeFormatter);
        }
        return null;
    }

    public static DateTimeFormatter getDateFormatter() {
        return DateTimeFormatter.ofPattern(DATE_PATTERN, Locale.getDefault());
    }

    public static DateTimeFormatter getTimeFormatter() {
        return DateTimeFormatter.ofPattern(TIME_PATTERN, Locale.getDefault());
    }

    public static LocalDate toLocalDate(String year, String mountName, Integer dayOfMount) {
        return LocalDate.of(Integer.parseInt(year), mapMonthNameToNumber(mountName), dayOfMount);
    }

    public static LocalDate toLocalDate(String dateStr) {
        if (dateStr != null && !dateStr.trim().isEmpty()) {
            return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(DATE_PATTERN, Locale.getDefault()));
        }
        return null;
    }

    public static LocalTime toLocalTime(String timeStr) {
        return LocalTime.parse(timeStr, DateTimeFormatter.ofPattern(TIME_PATTERN, Locale.getDefault()));
    }

    public static String formatTime(LocalTime localTime) {
        return localTime != null ? localTime.format(DateTimeFormatter.ofPattern(TIME_PATTERN, Locale.getDefault())) : null;
    }

    public static String formatDate(LocalDate localDate) {
        if (localDate != null) {
            return localDate.format(DateTimeFormatter.ofPattern(DATE_PATTERN));
        }
        return null;
    }

    public static String getDateDiffInHours(LocalTime d1, LocalTime d2) {
        if (d1 != null && d2 != null) {
            int diffHour = d2.getHour() - d1.getHour();
            int diffMinute = d2.getMinute() - d1.getMinute();
            return String.format(HOUR_FORMAT, diffHour, new DecimalFormat("#").format((double) diffMinute / 60 * 10));
        }
        return null;
    }

    public static String getTimeDiffInHours(LocalTime t1, LocalTime t2) {
        if (t1 == null || t2 == null) {
            return "0";
        }
        Duration diff = Duration.between(t1, t2);
        return String.format(HOUR_FORMAT, diff.toHours(), diff.toMinutes() % 60);
    }

    public static String formatToHHMM(int hour, int minute) {
        String hh = String.format("%s", hour);
        String mm = String.format("%s", minute);
        if (hour >= 0 && hour < 10) {
            hh = String.format("0%s", hour);
        }
        if (minute >= 0 && minute < 10) {
            mm = String.format("0%s", minute);
        }
        return String.format("%s:%s", hh, mm);
    }

    public static boolean isInteger(String value) {
        if (value == null) {
            return false;
        }
        return POSITIVE_INTEGER_PATTERN.matcher(value).matches();
    }

    public static LocalDateTime toLocalDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.length() != DATE_TIME_PATTERN.length()) {
            return null;
        }
        return LocalDateTime.parse(dateTimeStr, dateTimeFormatter);
    }

    public static LocalDate getFirstDayOfWeek(LocalDate date, Integer weekOfYear) {
        return LocalDate.of(date.getYear(), date.getMonthValue(), 1)
                .with(DayOfWeek.MONDAY)
                .with(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear(), weekOfYear);
    }

    /**
     * Use Monday as start of week
     */
    public static LocalDate getFirstDayOfWeek(Integer year, Integer month, Integer weekOfYear) {
        return LocalDate.of(year, month, 1)
                .with(DayOfWeek.MONDAY)
                .with(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear(), weekOfYear);
    }

    /**
     * use sunday as last day of week
     */
    public static LocalDate getLastDayOfWeek(LocalDate date, Integer weekOfYear) {
        LocalDate firstDayOfWeek = getFirstDayOfWeek(date.getYear(), date.getMonthValue(), weekOfYear);
        while (firstDayOfWeek.getDayOfWeek() != DayOfWeek.SUNDAY) {
            firstDayOfWeek = firstDayOfWeek.plusDays(1);
        }
        return firstDayOfWeek;
    }

    public static LocalDate getFirstDayOfMonth(LocalDate date) {
        return LocalDate.of(date.getYear(), date.getMonth(), 1);
    }

    public static LocalDate getLastDayOfMonth(LocalDate date) {
        return date.with(TemporalAdjusters.lastDayOfMonth());
    }

    public static String[] getYears() {
        return new String[]{"2023", "2024"};
    }

    public static String[] getMonthNames() {
        return new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    }

    /**
     * go from 0 - 11
     */
    public static String mapMonthNumberToName(Integer monthNumber) {
        return getMonthNames()[monthNumber];
    }

    public static Integer mapMonthNameToNumber(String monthName) {
        for (int i = 1; i <= 12; i++) {
            if (LocalDate.of(2023, i, 1).getMonth().name().equalsIgnoreCase(monthName)) {
                return i;
            }
        }
        return null;
    }

    public static Integer countBusinessDaysInMonth(LocalDate month) {
        LocalDate startDate = getFirstDayOfMonth(month);
        // Predicate 2: Is a given date is a weekday
        Predicate<LocalDate> isWeekend = date -> date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY;
        // Get all days between two dates
        int daysInMonth = YearMonth.of(month.getYear(), month.getMonthValue()).lengthOfMonth();
        // Iterate over stream of all dates and check each day against any weekday or
        return Stream.iterate(startDate, date -> date.plusDays(1))
                .limit(daysInMonth)
                .filter((isWeekend).negate())
                .collect(Collectors.toList()).size();
    }

    public static String loadMustacheTemplate(Context applicationContext, InvoiceService.InvoiceAttachmentTypesEnum template) {
        StringBuilder mustacheTemplateStr = new StringBuilder();
        try (InputStream fis = applicationContext.getAssets().open(template.getTemplate()); InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8); BufferedReader br = new BufferedReader(isr)) {
            br.lines().forEach(mustacheTemplateStr::append);
            return mustacheTemplateStr.toString();
        } catch (IOException e) {
            throw new TerexApplicationException("error reading mustache template", "50050", e);
        }
    }

    public static String formatAmountToNOK(double amount) {
        DecimalFormat formatter
                = new DecimalFormat("#,##0.00");
        return formatter.format(BigDecimal.valueOf(amount)).replace(",", " ").replace(".", ",");
    }
}