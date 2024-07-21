package com.gunnarro.android.terex.domain.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TimesheetDtoTest {

    @Test
    void equal() {
        UserAccountDto userAccountDto = new UserAccountDto();
        userAccountDto.setId(555L);
        ProjectDto projectDto = new ProjectDto();
        projectDto.setId(22L);
        TimesheetDto timesheetDtoMay = new TimesheetDto(1L);
        timesheetDtoMay.setUserAccountDto(userAccountDto);
        timesheetDtoMay.setYear(2024);
        timesheetDtoMay.setMonth(5);

        TimesheetDto timesheetDtoMay2 = new TimesheetDto(2L);
        timesheetDtoMay2.setUserAccountDto(userAccountDto);
        timesheetDtoMay2.setYear(2024);
        timesheetDtoMay2.setMonth(5);
        assertEquals(timesheetDtoMay, timesheetDtoMay2);
    }

    @Test
    void notEqual() {
        UserAccountDto userAccountDto = new UserAccountDto();
        userAccountDto.setId(555L);
        ProjectDto projectDto = new ProjectDto();
        projectDto.setId(22L);
        TimesheetDto timesheetDtoMay = new TimesheetDto(1L);
        timesheetDtoMay.setUserAccountDto(userAccountDto);
        timesheetDtoMay.setYear(2024);
        timesheetDtoMay.setMonth(5);

        TimesheetDto timesheetDtoJune = new TimesheetDto(2L);
        timesheetDtoJune.setUserAccountDto(userAccountDto);
        timesheetDtoJune.setYear(2024);
        timesheetDtoJune.setMonth(6);
        assertNotEquals(timesheetDtoMay, timesheetDtoJune);
    }


}
