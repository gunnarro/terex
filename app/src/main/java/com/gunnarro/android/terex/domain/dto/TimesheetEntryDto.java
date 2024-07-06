package com.gunnarro.android.terex.domain.dto;

import androidx.annotation.NonNull;

import com.gunnarro.android.terex.domain.entity.TimesheetEntry;
import com.gunnarro.android.terex.utility.Utility;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TimesheetEntryDto {

    private Long projectId;
    @NonNull
    private LocalDate workdayDate;
    private LocalTime fromTime;
    private LocalTime toTime;
    private Long workedSeconds;
    private String comments;
    @NonNull
    private String type;

    public TimesheetEntryDto() {
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

    public LocalTime getFromTime() {
        return fromTime;
    }

    public void setFromTime(LocalTime fromTime) {
        this.fromTime = fromTime;
    }

    public LocalTime getToTime() {
        return toTime;
    }

    public void setToTime(LocalTime toTime) {
        this.toTime = toTime;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
        return workdayDate.format(DateTimeFormatter.ofPattern("EEEE"));
    }

    public boolean isWeekend() {
        return workdayDate.format(DateTimeFormatter.ofPattern("EEE")).equalsIgnoreCase("sat") || workdayDate.format(DateTimeFormatter.ofPattern("EEE")).equalsIgnoreCase("sun");
    }

    public boolean isRegularWorkDay() {
        return type.equals(TimesheetEntry.TimesheetEntryTypeEnum.REGULAR.name());
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
}
