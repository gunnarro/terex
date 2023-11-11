package com.gunnarro.android.terex.domain;
import com.gunnarro.android.terex.domain.dto.TimesheetInfoDto;
import com.gunnarro.android.terex.domain.entity.Timesheet;

public class TimesheetMapper {

    public static TimesheetInfoDto toTimesheetInfoDto(Timesheet timesheet, Integer sumDays, Integer sumHours) {
        TimesheetInfoDto timesheetInfoDto = new TimesheetInfoDto();
        timesheetInfoDto.setTimesheetId(timesheet.getId());
        timesheetInfoDto.setInvoiceNumber(timesheet.getInvoiceNumber());
        timesheetInfoDto.setClientName(timesheet.getClientName());
        timesheetInfoDto.setProjectCode(timesheet.getProjectCode());
        timesheetInfoDto.setYear(timesheet.getYear());
        timesheetInfoDto.setMonth(timesheet.getMonth());
        timesheetInfoDto.setStatus(timesheet.getStatus());
        timesheetInfoDto.setWorkingDaysInMonth(timesheet.getWorkingDaysInMonth());
        timesheetInfoDto.setWorkingHoursInMonth(timesheet.getWorkingHoursInMonth());
        timesheetInfoDto.setRegisteredWorkedDays(sumDays);
        timesheetInfoDto.setRegisteredWorkedHours(sumHours);
        return timesheetInfoDto;
    }
}
