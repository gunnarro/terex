package com.gunnarro.android.terex.domain.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.TypeConverters;

import com.gunnarro.android.terex.domain.converter.LocalDateConverter;
import com.gunnarro.android.terex.domain.converter.LocalDateTimeConverter;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Objects;

/**
 * Used to hold weekly summary for a timesheet.
 */
@TypeConverters({LocalDateConverter.class, LocalDateTimeConverter.class})
@Entity(tableName = "timesheet_summary", indices = {@Index(value = {"timesheet_id", "year", "week_in_year"},
        unique = true)})
public class TimesheetSummary extends BaseEntity {

    /**
     * Hold a unique reference to the timesheet that is used as the basis for the summary
     */
    @ColumnInfo(name = "timesheet_id")
    private Long timesheetId;
    @ColumnInfo(name = "year")
    private Integer year;
    @ColumnInfo(name = "week_in_year")
    private Integer weekInYear;
    @ColumnInfo(name = "from_date")
    private LocalDate fromDate;
    @ColumnInfo(name = "to_date")
    private LocalDate toDate;
    @ColumnInfo(name = "total_worked_days", defaultValue = "0")
    private Integer totalWorkedDays = 0;
    @ColumnInfo(name = "total_days_off", defaultValue = "0")
    private Integer totalDaysOff = 0;
    @ColumnInfo(name = "total_sick_leave_days", defaultValue = "0")
    private Integer totalSickLeaveDays = 0;
    @ColumnInfo(name = "total_worked_hours", defaultValue = "0")
    private Double totalWorkedHours = 0d;
    @ColumnInfo(name = "total_billed_amount", defaultValue = "0")
    private Double totalBilledAmount = 0d;
    /**
     * ISO 427 currency code
     */
    @ColumnInfo(name = "currency", defaultValue = "NOK")
    private String currency;

    public TimesheetSummary() {
    }

    public Long getTimesheetId() {
        return timesheetId;
    }

    public void setTimesheetId(@NotNull Long timesheetId) {
        this.timesheetId = timesheetId;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(@NotNull Integer year) {
        this.year = year;
    }

    public Integer getWeekInYear() {
        return weekInYear;
    }

    public void setWeekInYear(@NotNull Integer weekInYear) {
        this.weekInYear = weekInYear;
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

    public Integer getTotalWorkedDays() {
        return totalWorkedDays;
    }

    public void setTotalWorkedDays(Integer totalWorkedDays) {
        this.totalWorkedDays = totalWorkedDays;
    }

    public Integer getTotalDaysOff() {
        return totalDaysOff;
    }

    public void setTotalDaysOff(Integer totalDaysOff) {
        this.totalDaysOff = totalDaysOff;
    }

    public Integer getTotalSickLeaveDays() {
        return totalSickLeaveDays;
    }

    public void setTotalSickLeaveDays(Integer totalSickLeaveDays) {
        this.totalSickLeaveDays = totalSickLeaveDays;
    }

    public Double getTotalWorkedHours() {
        return totalWorkedHours;
    }

    public void setTotalWorkedHours(Double totalWorkedHours) {
        this.totalWorkedHours = totalWorkedHours;
    }

    public Double getTotalBilledAmount() {
        return totalBilledAmount;
    }

    public String getTotalBilledAmountFormatted() {
        return String.format(Locale.getDefault(), "%.2f", totalBilledAmount);
    }

    public void setTotalBilledAmount(Double totalBilledAmount) {
        this.totalBilledAmount = totalBilledAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimesheetSummary that = (TimesheetSummary) o;
        return timesheetId.equals(that.timesheetId) && year.equals(that.year) && weekInYear.equals(that.weekInYear);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timesheetId, year, weekInYear);
    }
}
