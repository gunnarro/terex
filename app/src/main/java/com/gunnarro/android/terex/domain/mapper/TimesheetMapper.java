package com.gunnarro.android.terex.domain.mapper;

import com.gunnarro.android.terex.domain.dto.TimesheetDto;
import com.gunnarro.android.terex.domain.dto.TimesheetEntryDto;
import com.gunnarro.android.terex.domain.entity.Timesheet;
import com.gunnarro.android.terex.domain.entity.TimesheetEntry;

import java.util.List;
import java.util.stream.Collectors;

public class TimesheetMapper {

    private TimesheetMapper() {
    }

    public static TimesheetDto toTimesheetDto(Timesheet timesheet, Integer sumDays, Integer sumHours) {
        TimesheetDto timesheetDto = new TimesheetDto();
        timesheetDto.setTimesheetId(timesheet.getId());
        timesheetDto.setClientName(timesheet.getClientName());
        timesheetDto.setProjectCode(timesheet.getProjectCode());
        timesheetDto.setDescription(timesheet.getDescription());
        timesheetDto.setYear(timesheet.getYear());
        timesheetDto.setMonth(timesheet.getMonth());
        timesheetDto.setStatus(timesheet.getStatus());
        timesheetDto.setWorkingDaysInMonth(timesheet.getWorkingDaysInMonth());
        timesheetDto.setWorkingHoursInMonth(timesheet.getWorkingHoursInMonth());
        timesheetDto.setRegisteredWorkedDays(sumDays);
        timesheetDto.setRegisteredWorkedHours(sumHours);
        return timesheetDto;
    }


    public static Timesheet fromTimesheetDto(TimesheetDto timesheetDto) {
        Timesheet timesheet = new Timesheet();
        timesheet.setId(timesheetDto.getTimesheetId());
        timesheet.setClientName(timesheetDto.getClientName());
        timesheet.setProjectCode(timesheetDto.getProjectCode());
        timesheet.setDescription(timesheetDto.getDescription());
        timesheet.setYear(timesheetDto.getYear());
        timesheet.setMonth(timesheetDto.getMonth());
        timesheet.setStatus(timesheetDto.getStatus());
        timesheet.setFromDate(timesheet.getFromDate());
        timesheet.setToDate(timesheet.getToDate());
        timesheet.setWorkingDaysInMonth(timesheetDto.getWorkingDaysInMonth());
        timesheet.setWorkingHoursInMonth(timesheetDto.getWorkingHoursInMonth());
        return timesheet;
    }

    public static List<TimesheetEntryDto> toTimesheetEntryDtoList(List<TimesheetEntry> timesheetEntryList) {
        return timesheetEntryList.stream().map(TimesheetMapper::toTimesheetEntryDto).collect(Collectors.toList());
    }

    public static TimesheetEntryDto toTimesheetEntryDto(TimesheetEntry timesheetEntry) {
        TimesheetEntryDto timesheetEntryDto = new TimesheetEntryDto();
        timesheetEntryDto.setWorkdayDate(timesheetEntry.getWorkdayDate());
        timesheetEntryDto.setFromTime(timesheetEntry.getFromTime());
        timesheetEntryDto.setToTime(timesheetEntry.getToTime());
        timesheetEntryDto.setWorkedMinutes(timesheetEntry.getWorkedMinutes());
        timesheetEntryDto.setComments(timesheetEntry.getComment());
        return timesheetEntryDto;
    }
}
