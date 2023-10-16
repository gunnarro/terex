package com.gunnarro.android.terex.domain.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.gunnarro.android.terex.domain.converter.LocalDateConverter;
import com.gunnarro.android.terex.domain.converter.LocalDateTimeConverter;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Used to hold weekly summary for a timesheet.
 */
@TypeConverters({LocalDateConverter.class, LocalDateTimeConverter.class})
@Entity(tableName = "timesheet_summary", indices = {@Index(value = {"timesheet_id", "year", "week_in_year"},
        unique = true)})
public class TimesheetSummary extends BaseEntity {

    @NotNull
    @PrimaryKey(autoGenerate = true)
    public Long id;
    @NotNull
    @ColumnInfo(name = "timesheet_id")
    public Long timesheetId;
    @NotNull
    @ColumnInfo(name = "year")
    private Integer year;
    @NotNull
    @ColumnInfo(name = "week_in_year")
    private Integer weekInYear;
    @ColumnInfo(name = "from_date")
    private LocalDate fromDate;
    @ColumnInfo(name = "to_date")
    private LocalDate toDate;
    @ColumnInfo(name = "total_worked_days")
    private Integer totalWorkedDays = 0;
    @ColumnInfo(name = "total_days_off")
    private Integer totalDaysOff = 0;
    @ColumnInfo(name = "total_sick_leave_days")
    private Integer totalSickLeaveDays = 0;
    @ColumnInfo(name = "total_worked_hours")
    private double totalWorkedHours = 0;
    @ColumnInfo(name = "sum_billed_work")
    private double sumBilledWork = 0;

    public TimesheetSummary() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public double getTotalWorkedHours() {
        return totalWorkedHours;
    }

    public void setTotalWorkedHours(double totalWorkedHours) {
        this.totalWorkedHours = totalWorkedHours;
    }

    public double getSumBilledWork() {
        return sumBilledWork;
    }

    public void setSumBilledWork(double sumBilledWork) {
        this.sumBilledWork = sumBilledWork;
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
