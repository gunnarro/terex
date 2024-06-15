package com.gunnarro.android.terex.domain.dto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Used to hold weekly summary for a timesheet.
 */
public class TimesheetSummaryDto {

    /**
     * Hold a unique reference to the timesheet that is used as the basis for the summary
     */
    private Long timesheetId;

    private Integer year;

    private Integer weekInYear;

    private String summedByPeriod;

    private LocalDate fromDate;

    private LocalDate toDate;
    private Integer totalWorkedDays = 0;
    private Integer totalVacationDays = 0;
    private Integer totalSickLeaveDays = 0;
    private double totalWorkedHours = 0;
    private double totalBilledAmount = 0;
    /**
     * ISO 427 currency code
     */
    private String currency;

    public TimesheetSummaryDto() {
    }

    public Long getTimesheetId() {
        return timesheetId;
    }

    public void setTimesheetId(Long timesheetId) {
        this.timesheetId = timesheetId;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getWeekInYear() {
        return weekInYear;
    }

    public void setWeekInYear(Integer weekInYear) {
        this.weekInYear = weekInYear;
    }

    public String getSummedByPeriod() {
        return summedByPeriod;
    }

    public void setSummedByPeriod(String summedByPeriod) {
        this.summedByPeriod = summedByPeriod;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }

    public Integer getTotalWorkedDays() {
        return totalWorkedDays;
    }

    public void setTotalWorkedDays(Integer totalWorkedDays) {
        this.totalWorkedDays = totalWorkedDays;
    }

    public Integer getTotalVacationDays() {
        return totalVacationDays;
    }

    public void setTotalVacationDays(Integer totalVacationDays) {
        this.totalVacationDays = totalVacationDays;
    }

    public Integer getTotalSickLeaveDays() {
        return totalSickLeaveDays;
    }

    public void setTotalSickLeaveDays(Integer totalSickLeaveDays) {
        this.totalSickLeaveDays = totalSickLeaveDays;
    }

    public String getTotalWorkedHours() {
        return String.format(Locale.getDefault(),"%.1f", totalWorkedHours);
    }

    public void setTotalWorkedHours(double totalWorkedHours) {
        this.totalWorkedHours = totalWorkedHours;
    }

    public double getBilledAmount() {
        return totalBilledAmount;
    }
    public String getTotalBilledAmount() {
        return String.format(Locale.getDefault(),"%.2f", totalBilledAmount);
    }

    public void setTotalBilledAmount(double totalBilledAmount) {
        this.totalBilledAmount = totalBilledAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getFromDateMM() {
        return fromDate.format(DateTimeFormatter.ofPattern("MM"));
    }

    public String getFromDateDDMM() {
        return fromDate.format(DateTimeFormatter.ofPattern("dd.MM"));
    }

    public String getToDateDDMM() {
        return toDate.format(DateTimeFormatter.ofPattern("dd.MM"));
    }

}
