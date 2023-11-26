package com.gunnarro.android.terex.utility;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Assertions;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import org.junit.jupiter.api.Test;

class UtilityTest {

    @Test
    void getFirstDayOfWeek() {
        LocalDate firstDayOfWeekDate = Utility.getFirstDayOfWeek(2023, 5, 18);
        assertEquals("2023-05-01", firstDayOfWeekDate.toString());
        assertEquals("2023-05-07", Utility.getLastDayOfWeek(firstDayOfWeekDate, 18).toString());
        assertEquals(18, Utility.getWeek(firstDayOfWeekDate).intValue());

    }

    @Test
    void formatDateAndTime() {
        Date d1 = Utility.toDate("20-03-2020 09:35");
        assertEquals("20-03-2020 09:35", Utility.formatTime(d1.getTime()));
    }

    @Test
    void dateDiffHours() {
        LocalDateTime d1 = LocalDateTime.now(ZoneId.systemDefault()).withHour(8).withMinute(0).withSecond(0).withNano(0);
        assertEquals("08:00", d1.toLocalTime().toString());
        LocalDateTime d2 = d1.plusHours(7).plusMinutes(30);
        assertEquals("7.5", Utility.getDateDiffInHours(d1.toLocalTime(), d2.toLocalTime()));
    }

    @Test
    void dateDiffHoursParseTimeString() {
        LocalTime d1 = LocalTime.parse("08:00");
        assertEquals("08:00", d1.toString());
        LocalTime d2 = d1.plusHours(7).plusMinutes(30);
        assertEquals("7.5", Utility.getDateDiffInHours(d1, d2));
    }

    @Test
    void formatToHHMM() {
        assertEquals("02:08", Utility.formatToHHMM(2, 8));
        assertEquals("00:00", Utility.formatToHHMM(0, 0));
        assertEquals("24:59", Utility.formatToHHMM(24, 59));
    }

    @Test
    void formatToDDMMYYYY() {
        assertEquals("01-01-2022", Utility.formatToDDMMYYYY(2022, 1, 1));
        assertEquals("15-12-2022", Utility.formatToDDMMYYYY(2022, 12, 15));
        assertEquals("30-10-2022", Utility.formatToDDMMYYYY(2022, 10, 30));
    }

    @Test
    void mapToMonthName() {
        assertEquals(13, Utility.getMonthNames().length);
        assertEquals("December", Utility.getMonthNames()[Utility.getMonthNames().length - 2]);
        assertEquals("January", Utility.mapMonthNumberToName(0));
        assertEquals(LocalDate.of(2023, 1, 1).getMonth().name(), Utility.getMonthNames()[0].toUpperCase());
        assertEquals(LocalDate.of(2023, 1, 1).getMonth().getValue(), Utility.mapMonthNameToNumber("January").intValue());
    }

    @Test
    void numberOfBusinessDaysInMonth() {
        Assertions.assertEquals(19, (int) Utility.countBusinessDaysInMonth(LocalDate.of(2023, 2, 1)));
        Assertions.assertEquals(21, (int) Utility.countBusinessDaysInMonth(LocalDate.of(2023, 10, 1)));
        Assertions.assertEquals(21, (int) Utility.countBusinessDaysInMonth(LocalDate.of(2023, 11, 1)));
    }
}
