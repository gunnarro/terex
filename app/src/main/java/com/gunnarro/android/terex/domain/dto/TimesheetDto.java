package com.gunnarro.android.terex.domain.dto;

import java.time.LocalDate;
import java.util.List;

public class TimesheetDto {
    private String clientName;
    private String projectCode;
    private LocalDate fromDate;

    private LocalDate toDate;
    private String sumBilledHours;

    private String sunWorkDays;

    private List<TimesheetEntryDto> timesheetEntryDtoList;

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }

    public String getSumBilledHours() {
        return sumBilledHours;
    }

    public void setSumBilledHours(String sumBilledHours) {
        this.sumBilledHours = sumBilledHours;
    }

    public String getSunWorkDays() {
        return sunWorkDays;
    }

    public void setSunWorkDays(String sunWorkDays) {
        this.sunWorkDays = sunWorkDays;
    }

    public List<TimesheetEntryDto> getTimesheetEntryDtoList() {
        return timesheetEntryDtoList;
    }

    public void setTimesheetEntryDtoList(List<TimesheetEntryDto> timesheetEntryDtoList) {
        this.timesheetEntryDtoList = timesheetEntryDtoList;
    }


}
