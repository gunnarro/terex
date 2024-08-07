package com.gunnarro.android.terex.utility;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

class UtilityTest {

    @Test
    void fromSecondsToHours() {
        // 7:30
        long seconds = 7 * 60 * 60 + 30*60;
        Assertions.assertEquals(27000, seconds);
        Assertions.assertEquals("7.5", Utility.fromSecondsToHours(seconds));
        // 7:00
        seconds = 7 * 60 * 60;
        Assertions.assertEquals("7.0", Utility.fromSecondsToHours(seconds));

        Assertions.assertEquals("0.0", Utility.fromSecondsToHours(0L));
    }

    @Test
    void fromHoursToSeconds() {
        String hours = "7.5";
        long seconds = Utility.fromHoursToSeconds(hours);
        Assertions.assertEquals(27000, seconds);
        Assertions.assertEquals("7.5", Utility.fromSecondsToHours(seconds));

        Assertions.assertEquals(25200, Utility.fromHoursToSeconds("7"));
        Assertions.assertEquals(25200, Utility.fromHoursToSeconds("7.0"));
        Assertions.assertEquals(25200, Utility.fromHoursToSeconds("7.00"));
        Assertions.assertEquals(25200, Utility.fromHoursToSeconds("07.00"));
        Assertions.assertEquals(0, Utility.fromHoursToSeconds("0.0"));
        Assertions.assertNull(Utility.fromHoursToSeconds(""));
        Assertions.assertNull(Utility.fromHoursToSeconds(null));
    }

    @Test
    void getFirstDayOfMonth() {
        LocalDate date = LocalDate.of(2023, 11, 1);
        LocalDate firstDayOfMonth = Utility.getFirstDayOfMonth(date);
        Assertions.assertEquals("2023-11-01", firstDayOfMonth.toString());
        Assertions.assertEquals("2023-11-30", Utility.getLastDayOfMonth(date).toString());
    }


    @Test
    void dateTimeDiffHours() {
        LocalDateTime d1 = LocalDateTime.now(ZoneId.systemDefault()).withHour(8).withMinute(0).withSecond(0).withNano(0);
        Assertions.assertEquals("08:00", d1.toLocalTime().toString());
        LocalDateTime d2 = d1.plusHours(7).plusMinutes(30);
        Assertions.assertEquals("7.5", Utility.getDateDiffInHours(d1.toLocalTime(), d2.toLocalTime()));
    }

    @Test
    void timeDiffHours() {
        LocalTime d1 = LocalTime.now(ZoneId.systemDefault()).withHour(8).withMinute(0).withSecond(0).withNano(0);
        Assertions.assertEquals("08:00", d1.toString());
        LocalTime d2 = d1.plusHours(7).plusMinutes(30);
        Assertions.assertEquals("7.30", Utility.getTimeDiffInHours(d1, d2));
    }

    @Test
    void dateDiffHoursParseTimeString() {
        LocalTime d1 = LocalTime.parse("08:00");
        Assertions.assertEquals("08:00", d1.toString());
        LocalTime d2 = d1.plusHours(7).plusMinutes(30);
        Assertions.assertEquals("7.5", Utility.getDateDiffInHours(d1, d2));
    }

    @Test
    void formatToHHMM() {
        Assertions.assertEquals("02:08", Utility.formatToHHMM(2, 8));
        Assertions.assertEquals("00:00", Utility.formatToHHMM(0, 0));
        Assertions.assertEquals("24:59", Utility.formatToHHMM(24, 59));
    }

    @Test
    void mapToMonthName() {
        Assertions.assertEquals(1, LocalDate.of(2023, 1, 1).getMonth().getValue());
        Assertions.assertEquals(12, LocalDate.of(2023, 12, 1).getMonth().getValue());
        Assertions.assertEquals(12, Utility.getMonthNames().length);
        Assertions.assertEquals("December", Utility.getMonthNames()[Utility.getMonthNames().length - 1]);
        Assertions.assertEquals("January", Utility.mapMonthNumberToName(0));
        Assertions.assertEquals(LocalDate.of(2023, 1, 1).getMonth().name(), Utility.getMonthNames()[0].toUpperCase());
        Assertions.assertEquals(LocalDate.of(2023, 1, 1).getMonth().getValue(), Utility.mapMonthNameToNumber("January").intValue());
    }

    @Test
    void numberOfBusinessDaysInMonth() {
        Assertions.assertEquals(22, (int) Utility.countBusinessDaysInMonth(LocalDate.of(2023, 1, 1)));
        Assertions.assertEquals(20, (int) Utility.countBusinessDaysInMonth(LocalDate.of(2023, 2, 1)));
        Assertions.assertEquals(22, (int) Utility.countBusinessDaysInMonth(LocalDate.of(2023, 10, 1)));
        Assertions.assertEquals(22, (int) Utility.countBusinessDaysInMonth(LocalDate.of(2023, 11, 1)));
        Assertions.assertEquals(21, (int) Utility.countBusinessDaysInMonth(LocalDate.of(2023, 12, 1)));
    }

    @Test
    void formatAmountNOK() {
        Assertions.assertEquals("206 250,00", Utility.formatAmountToNOK(206250.00));
        Assertions.assertEquals("101,35", Utility.formatAmountToNOK(101.35));
        Assertions.assertEquals("101,00", Utility.formatAmountToNOK(101));
        Assertions.assertEquals("1 101,50", Utility.formatAmountToNOK(1101.5));
    }
}
