package com.gunnarro.android.terex.domain.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TimesheetEntryDto {

    private LocalDate workdayDate;
    private LocalTime fromTime;
    private LocalTime toTime;
    private Integer workedMinutes;
    private String comments;

    public TimesheetEntryDto() {
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

    public Integer getWorkedMinutes() {
        return workedMinutes;
    }

    public void setWorkedMinutes(Integer workedMinutes) {
        this.workedMinutes = workedMinutes;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    // helper methods

    public String getWorkedHours() {
        if (workedMinutes != null && workedMinutes > 0) {
            return Double.toString((double) workedMinutes / 60);
        }
        return null;
    }

    public String getWorkdayDateDay() {
        return workdayDate.format(DateTimeFormatter.ofPattern("dd"));
    }
}
