package com.gunnarro.android.terex.domain.dto;

import androidx.annotation.NonNull;

import com.gunnarro.android.terex.domain.entity.TimesheetEntry;
import com.gunnarro.android.terex.utility.Utility;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import java.util.StringJoiner;

public class TimesheetEntryDto extends BaseDto {

    private Long timesheetId;
    private Long projectId;
    @NonNull
    private LocalDate workdayDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Long workedSeconds;
    private String comments;
    @NonNull
    private String type;
    private String status;
    private ProjectDto projectDto;

    public TimesheetEntryDto() {
        workdayDate = LocalDate.now();
        type = TimesheetEntry.TimesheetEntryTypeEnum.REGULAR.name();
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

    @NonNull
    public LocalDate getWorkdayDate() {
        return workdayDate;
    }

    public void setWorkdayDate(@NonNull LocalDate workdayDate) {
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

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    @NonNull
    public String getType() {
        return type;
    }

    public void setType(@NonNull String type) {
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
        return workdayDate.format(DateTimeFormatter.ofPattern("EEEE", new Locale("no", "NO")));
    }

    public boolean isWeekend() {
        return workdayDate.format(DateTimeFormatter.ofPattern("EEE")).equalsIgnoreCase("sat") || workdayDate.format(DateTimeFormatter.ofPattern("EEE")).equalsIgnoreCase("sun");
    }

    public boolean isRegularWorkDay() {
        return TimesheetEntry.TimesheetEntryTypeEnum.REGULAR.name().equals(type);
    }

    public boolean isRegularWorkDayWithHours() {
        return TimesheetEntry.TimesheetEntryTypeEnum.REGULAR.name().equals(type) && workedSeconds > 0;
    }

    public boolean isVacationDay() {
        return TimesheetEntry.TimesheetEntryTypeEnum.VACATION.name().equals(type);
    }

    public boolean isSickDay() {
        return TimesheetEntry.TimesheetEntryTypeEnum.SICK.name().equals(type);
    }

    public boolean isOpen() {
        return TimesheetEntry.TimesheetEntryStatusEnum.OPEN.name().equals(status);
    }

    public boolean isClosed() {
        return TimesheetEntry.TimesheetEntryStatusEnum.CLOSED.name().equals(status);
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

    @NonNull
    @Override
    public String toString() {
        return new StringJoiner(", ", TimesheetEntryDto.class.getSimpleName() + "[", "]")
                .add("id=" + getId())
                .add("timesheetId=" + timesheetId)
                .add("projectId=" + projectId)
                .add("workdayDate=" + workdayDate)
                .add("workedSeconds=" + workedSeconds)
                .add("type='" + type + "'")
                .add("status='" + status + "'")
                .toString();
    }
}
