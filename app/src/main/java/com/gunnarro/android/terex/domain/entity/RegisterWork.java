package com.gunnarro.android.terex.domain.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class RegisterWork {

    public static final Integer DEFAULT_DAILY_BREAK_IN_MINUTES = 30;
    public static final Long DEFAULT_DAILY_WORKING_HOURS_IN_MINUTES = 8 * 60L - DEFAULT_DAILY_BREAK_IN_MINUTES;
    public static final Integer DEFAULT_HOURLY_RATE = 1075;
    public static final String DEFAULT_STATUS = "Open";
    public static final String TIMESHEET_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";

    private String clientName;

    private String projectName;

    private Integer hourlyRate;

    private LocalDate workdayDate;

    private LocalTime fromTime;

    private LocalTime toTime;

    private double workedHours;

    private Integer breakInMin;

    private String status;

    private String comment;


    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Integer getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(Integer hourlyRate) {
        this.hourlyRate = hourlyRate;
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

    public double getWorkedHours() {
        return workedHours;
    }

    public void setWorkedHours(double workedHours) {
        this.workedHours = workedHours;
    }

    public Integer getBreakInMin() {
        return breakInMin;
    }

    public void setBreakInMin(Integer breakInMin) {
        this.breakInMin = breakInMin;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public static RegisterWork buildDefault(String client) {
        RegisterWork w = new RegisterWork();
        w.setClientName(client);
        w.setStatus(DEFAULT_STATUS);
        w.setWorkdayDate(LocalDate.now());
        w.setFromTime(LocalTime.now(ZoneId.systemDefault()).withHour(8).withMinute(0).withSecond(0).withSecond(0));
        // add 7,5 hours
        w.setToTime(w.getFromTime().plusMinutes(DEFAULT_DAILY_WORKING_HOURS_IN_MINUTES));
        long minutes = ChronoUnit.MINUTES.between(w.getFromTime(), w.getToTime());
        w.setWorkedHours((double) minutes /60);
        w.setBreakInMin(DEFAULT_DAILY_BREAK_IN_MINUTES);
        w.setHourlyRate(DEFAULT_HOURLY_RATE);
        return w;
    }
}