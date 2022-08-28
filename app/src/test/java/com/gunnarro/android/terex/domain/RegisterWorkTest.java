package com.gunnarro.android.terex.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.gunnarro.android.terex.domain.entity.RegisterWork;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.Duration;
import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;
public class RegisterWorkTest {

    @Test
    public void registerWork() {
        RegisterWork work = RegisterWork.buildDefault("MasterCard");
        assertEquals("30", work.getBreakInMin().toString());
        assertEquals("1075", work.getHourlyRate().toString());
        assertEquals("Open", work.getStatus());
        assertEquals("7.5", work.getWorkedHours().toString());
        assertNotNull(work.getFromTime());
        assertNotNull(work.getToTime());
        assertNull(work.getComment());

      //  long diffMs = work.getToDate().toEpochSecond(ZoneOffset.UTC) - work.getFromDate().toEpochSecond(ZoneOffset.UTC);
      //  long minutes = TimeUnit.MILLISECONDS.toMinutes(diffMs);
      //   assertEquals(7*60 + 30, minutes);
    }
}