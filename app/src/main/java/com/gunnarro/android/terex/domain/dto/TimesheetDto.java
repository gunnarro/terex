package com.gunnarro.android.terex.domain.dto;

import androidx.annotation.NonNull;

import com.gunnarro.android.terex.domain.entity.Timesheet;

import java.time.LocalDate;
import java.util.Objects;

public class TimesheetDto {
    private Long id;
    private UserAccountDto userAccountDto;
    private ProjectDto projectDto;
    private String status;
    private LocalDate fromDate;
    private LocalDate toDate;
    private Integer year;
    private Integer month;
    private Integer workingDaysInMonth;
    private Integer workingHoursInMonth;
    private Integer registeredWorkedDays;
    private Integer registeredWorkedHours;
    private Integer sickDays;
    private Integer vacationDays;
    private String description;

    public TimesheetDto() {}

    public TimesheetDto(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public Integer getSickDays() {
        return sickDays;
    }

    public void setSickDays(Integer sickDays) {
        this.sickDays = sickDays;
    }

    public Integer getVacationDays() {
        return vacationDays;
    }

    public void setVacationDays(Integer vacationDays) {
        this.vacationDays = vacationDays;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UserAccountDto getUserAccountDto() {
        return userAccountDto;
    }

    public void setUserAccountDto(UserAccountDto userAccountDto) {
        this.userAccountDto = userAccountDto;
    }

    public ProjectDto getProjectDto() {
        return projectDto;
    }

    public void setProjectDto(ProjectDto projectDto) {
        this.projectDto = projectDto;
    }

    public boolean isNew() {
        return status.equals(Timesheet.TimesheetStatusEnum.NEW.name());
    }

    public boolean isActive() {
        return status.equals(Timesheet.TimesheetStatusEnum.ACTIVE.name());
    }

    public boolean isClosed() {
        return status.equals(Timesheet.TimesheetStatusEnum.BILLED.name());
    }

    public boolean isCompleted() {
        return status.equals(Timesheet.TimesheetStatusEnum.COMPLETED.name());
    }

    public boolean isBilled() {
        return status.equals(Timesheet.TimesheetStatusEnum.BILLED.name());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimesheetDto that = (TimesheetDto) o;
        return Objects.equals(userAccountDto, that.userAccountDto) && Objects.equals(projectDto, that.projectDto) && Objects.equals(year, that.year) && Objects.equals(month, that.month);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userAccountDto, projectDto, year, month);
    }


    @NonNull
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TimesheetDto{");
        sb.append("id=").append(id);
        sb.append(", status='").append(status).append('\'');
        sb.append(", year=").append(year);
        sb.append(", month=").append(month);
        sb.append('}');
        return sb.toString();
    }
}
