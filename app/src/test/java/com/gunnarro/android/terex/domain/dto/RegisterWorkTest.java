package com.gunnarro.android.terex.domain.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.gunnarro.android.terex.domain.dto.RegisterWork;
import com.gunnarro.android.terex.domain.entity.Timesheet;

import org.junit.jupiter.api.Test;
class RegisterWorkTest {

    @Test
    void registerWork() {
        RegisterWork work = RegisterWork.buildDefault("MasterCard");
        assertEquals("30", work.getBreakInMin().toString());
        assertEquals("1075", work.getHourlyRate().toString());
        assertEquals(Timesheet.TimesheetStatusEnum.NEW.name(), work.getStatus());
        assertEquals("7.5", work.getWorkedHours().toString());
        assertNotNull(work.getFromTime());
        assertNotNull(work.getToTime());
        assertNull(work.getComment());

      //  long diffMs = work.getToDate().toEpochSecond(ZoneOffset.UTC) - work.getFromDate().toEpochSecond(ZoneOffset.UTC);
      //  long minutes = TimeUnit.MILLISECONDS.toMinutes(diffMs);
      //   assertEquals(7*60 + 30, minutes);
    }
}