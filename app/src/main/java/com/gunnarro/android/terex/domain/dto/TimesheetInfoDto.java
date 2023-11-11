package com.gunnarro.android.terex.domain.dto;

public class TimesheetInfoDto {
    private Long timesheetId;
    private Long invoiceNumber;
    private String clientName;
    private String projectCode;
    private String status;
    private Integer year;
    private Integer month;
    private Integer workingDaysInMonth;
    private Integer workingHoursInMonth;

    private Integer registeredWorkedDays;
    private Integer registeredWorkedHours;

    private Integer registeredSickLeaveDays;
    private Integer registeredSickLeaveHours;

    private Integer registeredVacationDays;
    private Integer registeredVacationHours;

    public Long getTimesheetId() {
        return timesheetId;
    }

    public void setTimesheetId(Long timesheetId) {
        this.timesheetId = timesheetId;
    }

    public Long getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(Long invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getWorkingDaysInMonth() {
        return workingDaysInMonth;
    }

    public void setWorkingDaysInMonth(Integer workingDaysInMonth) {
        this.workingDaysInMonth = workingDaysInMonth;
    }

    public Integer getWorkingHoursInMonth() {
        return workingHoursInMonth;
    }

    public void setWorkingHoursInMonth(Integer workingHoursInMonth) {
        this.workingHoursInMonth = workingHoursInMonth;
    }

    public Integer getRegisteredWorkedDays() {
        return registeredWorkedDays;
    }

    public void setRegisteredWorkedDays(Integer registeredWorkedDays) {
        this.registeredWorkedDays = registeredWorkedDays;
    }

    public Integer getRegisteredWorkedHours() {
        return registeredWorkedHours;
    }

    public void setRegisteredWorkedHours(Integer registeredWorkedHours) {
        this.registeredWorkedHours = registeredWorkedHours;
    }

    public Integer getRegisteredSickLeaveDays() {
        return registeredSickLeaveDays;
    }

    public void setRegisteredSickLeaveDays(Integer registeredSickLeaveDays) {
        this.registeredSickLeaveDays = registeredSickLeaveDays;
    }

    public Integer getRegisteredSickLeaveHours() {
        return registeredSickLeaveHours;
    }

    public void setRegisteredSickLeaveHours(Integer registeredSickLeaveHours) {
        this.registeredSickLeaveHours = registeredSickLeaveHours;
    }

    public Integer getRegisteredVacationDays() {
        return registeredVacationDays;
    }

    public void setRegisteredVacationDays(Integer registeredVacationDays) {
        this.registeredVacationDays = registeredVacationDays;
    }

    public Integer getRegisteredVacationHours() {
        return registeredVacationHours;
    }

    public void setRegisteredVacationHours(Integer registeredVacationHours) {
        this.registeredVacationHours = registeredVacationHours;
    }
}
