package com.gunnarro.android.terex.utility;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

public class UtilityTest {

    @Test
    public void formatDateAndTime() {
        Date d1 = Utility.toDate("20-03-2020 09:35");
        assertEquals("20-03-2020 09:35", Utility.formatTime(d1.getTime()));
    }

    @Test
    public void dateDiffHours() {
        LocalDateTime d1 = LocalDateTime.now(ZoneId.systemDefault()).withHour(8).withMinute(0).withSecond(0).withNano(0);
        assertEquals("08:00", d1.toLocalTime().toString());
        LocalDateTime d2 = d1.plusHours(7).plusMinutes(30);
        assertEquals("7.5", Utility.getDateDiffInHours(d1.toLocalTime(),d2.toLocalTime()));
    }

    @Test
    public void dateDiffHoursParseTimeString() {
        LocalTime d1 = LocalTime.parse("08:00");
        assertEquals("08:00", d1.toString());
        LocalTime d2 = d1.plusHours(7).plusMinutes(30);
        assertEquals("7.5", Utility.getDateDiffInHours(d1, d2));
    }

    @Test
    public void formatToHHMM() {
        assertEquals("02:08", Utility.formatToHHMM(2, 8));
        assertEquals("00:00", Utility.formatToHHMM(0, 0));
        assertEquals("24:59", Utility.formatToHHMM(24, 59));
    }

    @Test
    public void formatToDDMMYYYY() {
        assertEquals("01-01-2022", Utility.formatToDDMMYYYY(2022, 1, 1));
        assertEquals("15-12-2022", Utility.formatToDDMMYYYY(2022, 12, 15));
        assertEquals("30-10-2022", Utility.formatToDDMMYYYY(2022, 10, 30));
    }
}
