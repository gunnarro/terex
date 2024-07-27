package com.gunnarro.android.terex.domain.dto;

import androidx.annotation.NonNull;

import com.gunnarro.android.terex.domain.entity.TimesheetEntry;
import com.gunnarro.android.terex.utility.Utility;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import java.util.StringJoiner;

public class TimesheetEntryDto {

    private Long id;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private Long timesheetId;
    private Long projectId;
    @NonNull
    private LocalDate workdayDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Long workedSeconds;
    private Integer breakSeconds;
    private String comments;
    @NonNull
    private String type;
    private String status;
    private ProjectDto projectDto;

    public TimesheetEntryDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public Long getTimesheetId() {
        return timesheetId;
    }

    public void setTimesheetId(Long timesheetId) {
        this.timesheetId = timesheetId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public LocalDate getWorkdayDate() {
        return workdayDate;
    }

    public void setWorkdayDate(LocalDate workdayDate) {
        this.workdayDate = workdayDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Long getWorkedSeconds() {
        return workedSeconds;
    }

    public void setWorkedSeconds(Long workedSeconds) {
        this.workedSeconds = workedSeconds;
    }

    public Integer getBreakSeconds() {
        return breakSeconds;
    }

    public void setBreakSeconds(Integer breakSeconds) {
        this.breakSeconds = breakSeconds;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ProjectDto getProjectDto() {
        return projectDto;
    }

    public void setProjectDto(ProjectDto projectDto) {
        this.projectDto = projectDto;
    }

    // helper methods

    public String getWorkedHours() {
        return Utility.fromSecondsToHours(workedSeconds);
    }

    public String getWorkdayDateDay() {
        if (workdayDate.getDayOfMonth() < 10) {
            return workdayDate.format(DateTimeFormatter.ofPattern("d"));
        } else {
            return workdayDate.format(DateTimeFormatter.ofPattern("dd"));
        }
    }

    public String getWorkdayDateDayName() {
        return workdayDate.format(DateTimeFormatter.ofPattern("EEEE", new Locale("no","NO")));
    }

    public boolean isWeekend() {
        return workdayDate.format(DateTimeFormatter.ofPattern("EEE")).equalsIgnoreCase("sat") || workdayDate.format(DateTimeFormatter.ofPattern("EEE")).equalsIgnoreCase("sun");
    }

    public boolean isRegularWorkDay() {
        return type.equals(TimesheetEntry.TimesheetEntryTypeEnum.REGULAR.name());
    }

    public boolean isRegularWorkDayWithHours() {
        return type.equals(TimesheetEntry.TimesheetEntryTypeEnum.REGULAR.name()) && workedSeconds > 0;
    }

    public boolean isVacationDay() {
        return type.equals(TimesheetEntry.TimesheetEntryTypeEnum.VACATION.name());
    }

    public boolean isSickDay() {
        return type.equals(TimesheetEntry.TimesheetEntryTypeEnum.SICK.name());
    }

    public boolean hasWorkHours() {
        return workedSeconds != null && workedSeconds > 0;
    }

    public boolean isOpen() {
        return status.equals(TimesheetEntry.TimesheetEntryStatusEnum.OPEN.name());
    }

    public boolean isClosed() {
        return status.equals(TimesheetEntry.TimesheetEntryStatusEnum.CLOSED.name());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimesheetEntryDto that = (TimesheetEntryDto) o;
        return Objects.equals(timesheetId, that.timesheetId) && Objects.equals(projectId, that.projectId) && Objects.equals(workdayDate, that.workdayDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timesheetId, projectId, workdayDate);
    }


    @Override
    public String toString() {
        return new StringJoiner(", ", TimesheetEntryDto.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("timesheetId=" + timesheetId)
                .add("projectId=" + projectId)
                .add("workdayDate=" + workdayDate)
                .add("workedSeconds=" + workedSeconds)
                .add("type='" + type + "'")
                .add("status='" + status + "'")
                .toString();
    }
}
