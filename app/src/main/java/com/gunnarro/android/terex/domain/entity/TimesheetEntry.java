package com.gunnarro.android.terex.domain.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.gunnarro.android.terex.domain.converter.LocalDateConverter;
import com.gunnarro.android.terex.domain.converter.LocalDateTimeConverter;
import com.gunnarro.android.terex.domain.converter.LocalTimeConverter;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//@Builder
// default constructor, Room accepts only one
@NoArgsConstructor
//@AllArgsConstructor
@Getter
@Setter
@TypeConverters({LocalDateConverter.class, LocalTimeConverter.class, LocalDateTimeConverter.class})
@Entity(tableName = "timesheet_entry", indices = {@Index(value = {"timesheet_id", "workday_date"},
        unique = true)})
public class TimesheetEntry {

    @PrimaryKey(autoGenerate = true)
    private Long id;

    @NonNull
    @ColumnInfo(name = "timesheet_id")
    private Long timesheetId;

    @NonNull
    @ColumnInfo(name = "created_date")
    private LocalDateTime createdDate;

    @NonNull
    @ColumnInfo(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

    @NonNull
    @ColumnInfo(name = "workday_week")
    private Integer workdayWeek;

    @NonNull
    @ColumnInfo(name = "workday_date")
    private LocalDate workdayDate;

    @NonNull
    @ColumnInfo(name = "from_time")
    private LocalTime fromTime;

    @NonNull
    @ColumnInfo(name = "to_time")
    private LocalTime toTime;

    @NotNull
    @ColumnInfo(name = "worked_in_min")
    private Integer workedMinutes;

    @ColumnInfo(name = "break_in_min")
    private Integer breakInMin;

    @NotNull
    @ColumnInfo(name = "hourly_rate")
    private Integer hourlyRate;

    @NonNull
    @ColumnInfo(name = "status", defaultValue = "OPEN")
    private String status;

    @ColumnInfo(name = "comment")
    private String comment;

    /**
     * Indicate if it data in this entry should be used as default setting for new entries. Set to not default as default.
     */
    @ColumnInfo(name = "use_as_default", defaultValue = "0")
    private Integer useAsDefault;


    public Long getId() {
        return id;
    }

    @NonNull
    public Long getTimesheetId() {
        return timesheetId;
    }

    public void setTimesheetId(@NonNull Long timesheetId) {
        this.timesheetId = timesheetId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NonNull
    public Integer getWorkdayWeek() {
        return workdayWeek;
    }

    public void setWorkdayWeek(@NonNull Integer workdayWeek) {
        this.workdayWeek = workdayWeek;
    }

    @NonNull
    public LocalDate getWorkdayDate() {
        return workdayDate;
    }

    public void setWorkdayDate(@NonNull LocalDate workdayDate) {
        this.workdayDate = workdayDate;
    }

    @NonNull
    public LocalTime getFromTime() {
        return fromTime;
    }

    public void setFromTime(@NonNull LocalTime fromTime) {
        this.fromTime = fromTime;
    }

    @NonNull
    public LocalTime getToTime() {
        return toTime;
    }

    public void setToTime(@NonNull LocalTime toTime) {
        this.toTime = toTime;
    }

    public Integer getWorkedMinutes() {
        return workedMinutes;
    }

    public void setWorkedMinutes(Integer workedMinutes) {
        this.workedMinutes = workedMinutes;
    }

    public Integer getBreakInMin() {
        return breakInMin;
    }

    public void setBreakInMin(Integer breakInMin) {
        this.breakInMin = breakInMin;
    }

    public Integer getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(Integer hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    /**
     * Valid statues: OPEN, BILLED
     */
    @NonNull
    public String getStatus() {
        return status;
    }

    public void setStatus(@NonNull String status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }


    @NonNull
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(@NonNull LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    @NonNull
    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public Integer getUseAsDefault() {
        return useAsDefault;
    }

    public void setUseAsDefault(Integer useAsDefault) {
        this.useAsDefault = useAsDefault;
    }

    public void setLastModifiedDate(@NonNull LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }


    public static TimesheetEntry createDefault(Long timesheetId, String status, Integer dailyBreakMin, LocalDate workDayDate, Long workingHoursMin, Integer hourlyRate) {
        TimesheetEntry timesheetEntry = new TimesheetEntry();
        timesheetEntry.setTimesheetId(timesheetId);
        timesheetEntry.setStatus(status);
        timesheetEntry.setWorkdayDate(workDayDate);
        timesheetEntry.setFromTime(LocalTime.now(ZoneId.systemDefault()).withHour(8).withMinute(0).withSecond(0).withSecond(0).withNano(0));
        // add 7,5 hours
        timesheetEntry.setToTime(timesheetEntry.getFromTime().plusMinutes(workingHoursMin));
        timesheetEntry.setWorkedMinutes(Long.valueOf(ChronoUnit.MINUTES.between(timesheetEntry.getFromTime(), timesheetEntry.getToTime())).intValue());
        timesheetEntry.setBreakInMin(dailyBreakMin);
        timesheetEntry.setHourlyRate(hourlyRate);
        return timesheetEntry;
    }

    public static TimesheetEntry clone(TimesheetEntry timesheet) {
        if (timesheet == null) {
            return null;
        }
        TimesheetEntry clone = new TimesheetEntry();
        clone.setTimesheetId(timesheet.getTimesheetId());
        clone.setBreakInMin(timesheet.getBreakInMin());
        clone.setWorkedMinutes(timesheet.getWorkedMinutes());
        clone.setStatus(timesheet.getStatus());
        clone.setHourlyRate(timesheet.getHourlyRate());
        clone.setWorkdayWeek(timesheet.getWorkdayWeek());
        clone.setBreakInMin(timesheet.getBreakInMin());
        clone.setFromTime(timesheet.getFromTime());
        clone.setToTime(timesheet.getToTime());
        clone.setComment(timesheet.getComment());
        return clone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimesheetEntry that = (TimesheetEntry) o;
        return timesheetId.equals(that.timesheetId) && workdayDate.equals(that.workdayDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timesheetId, workdayDate);
    }

    @NonNull
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Timesheet{");
        sb.append("id=").append(id);
        sb.append(", timesheetId=").append(timesheetId);
        sb.append(", createdDate=").append(createdDate);
        sb.append(", lastModifiedDate=").append(lastModifiedDate);
        sb.append(", workdayWeek=").append(workdayWeek);
        sb.append(", workdayDate=").append(workdayDate);
        sb.append(", fromTime=").append(fromTime);
        sb.append(", toTime=").append(toTime);
        sb.append(", workedMinutes=").append(workedMinutes);
        sb.append(", breakInMin=").append(breakInMin);
        sb.append(", hourlyRate=").append(hourlyRate);
        sb.append(", status='").append(status).append('\'');
        sb.append(", comment='").append(comment).append('\'');
        sb.append(", useAsDefault='").append(useAsDefault).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
