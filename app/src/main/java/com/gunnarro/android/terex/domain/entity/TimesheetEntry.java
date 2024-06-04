package com.gunnarro.android.terex.domain.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.TypeConverters;

import com.gunnarro.android.terex.domain.converter.LocalDateConverter;
import com.gunnarro.android.terex.domain.converter.LocalDateTimeConverter;
import com.gunnarro.android.terex.domain.converter.LocalTimeConverter;
import com.gunnarro.android.terex.utility.Utility;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.Objects;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Timesheet entry is the time entries that holds your actual working hours.
 * And is added to a specific timesheet.
 */
//@Builder
// default constructor, Room accepts only one
@NoArgsConstructor
//@AllArgsConstructor
@Getter
@Setter
@TypeConverters({LocalDateConverter.class, LocalTimeConverter.class, LocalDateTimeConverter.class})
@Entity(tableName = "timesheet_entry",
        foreignKeys = {@ForeignKey(entity = Timesheet.class, parentColumns = "id", childColumns = "timesheet_id", onDelete = BaseEntity.CASCADE)},
        indices = {@Index(value = {"timesheet_id", "workday_date"}, unique = true)})
public class TimesheetEntry extends BaseEntity {

    public enum TimesheetEntryTypeEnum {
        REGULAR, VACATION, SICK
    }

    public enum TimesheetEntryStatusEnum {
        OPEN, CLOSED
    }

    /**
     * Hold a unique reference to the timesheet that is entry is assigned to.
     */
    @NonNull
    @ColumnInfo(name = "timesheet_id")
    private Long timesheetId;
    @NonNull
    @ColumnInfo(name = "workday_week")
    private Integer workdayWeek;

    @NonNull
    @ColumnInfo(name = "workday_date")
    private LocalDate workdayDate;

    /**
     * type of entry, SICK_LEAVE, VACATION, HOLIDAY, REGULAR.
     * Default to REGULAR, which means a ordinary workday.
     */
    @NonNull
    @ColumnInfo(name = "type", defaultValue = "REGULAR")
    private String type;

    @NonNull
    @ColumnInfo(name = "start_time")
    private LocalTime fromTime;

    @NonNull
    @ColumnInfo(name = "end_time")
    private LocalTime toTime;

    @NotNull
    @ColumnInfo(name = "worked_in_min")
    private Integer workedMinutes;

    @ColumnInfo(name = "break_in_min")
    private Integer breakInMin;

    /**
     * Status can only be OPEN and CLOSED. When CLOSED is not possible to change or delete the entry.
     * The status is automatically set equal to CLOSED when a timesheet have been completed and billed.
     */
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

    @NonNull
    public Long getTimesheetId() {
        return timesheetId;
    }

    public void setTimesheetId(@NonNull Long timesheetId) {
        this.timesheetId = timesheetId;
    }

    @NonNull
    public Integer getWorkdayWeek() {
        return workdayWeek;
    }

    public Integer getWorkdayMonth() {
        return workdayDate.getMonthValue();
    }

    public Integer getWorkdayYear() {
        return workdayDate.getYear();
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

    @NonNull
    public Integer getWorkedMinutes() {
        return workedMinutes;
    }

    public String getWorkedHours() {
        return Double.toString((double) workedMinutes / 60);
    }

    public void setWorkedMinutes(@NonNull Integer workedMinutes) {
        this.workedMinutes = workedMinutes;
    }

    public Integer getBreakInMin() {
        return breakInMin;
    }

    public void setBreakInMin(Integer breakInMin) {
        this.breakInMin = breakInMin;
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

    public Integer getUseAsDefault() {
        return useAsDefault;
    }

    public void setUseAsDefault(Integer useAsDefault) {
        this.useAsDefault = useAsDefault;
    }

    @NonNull
    public String getType() {
        return type;
    }

    public void setType(@NonNull String type) {
        this.type = type;
    }

    public boolean isRegularWorkDay() {
        return type.equals(TimesheetEntryTypeEnum.REGULAR.name());
    }

    public boolean isVacationDay() {
        return type.equals(TimesheetEntryTypeEnum.VACATION.name());
    }

    public boolean isSickDay() {
        return type.equals(TimesheetEntryTypeEnum.SICK.name());
    }

    public boolean isOpen() {
        return status.equals(TimesheetEntryStatusEnum.OPEN.name());
    }

    public boolean isBilled() {
        return status.equals(TimesheetEntryStatusEnum.CLOSED.name());
    }

    public static TimesheetEntry createDefault(Long timesheetId, LocalDate workDayDate) {
        TimesheetEntry timesheetEntry = new TimesheetEntry();
        timesheetEntry.setTimesheetId(timesheetId);
        timesheetEntry.setStatus(TimesheetEntryStatusEnum.OPEN.name());
        timesheetEntry.setType(TimesheetEntryTypeEnum.REGULAR.name());
        timesheetEntry.setWorkdayDate(workDayDate);
        timesheetEntry.setWorkdayWeek(timesheetEntry.getWorkdayDate().get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear()));
        timesheetEntry.setFromTime(LocalTime.now(ZoneId.systemDefault()).withHour(8).withMinute(0).withSecond(0).withSecond(0).withNano(0));
        // add 7,5 hours
        timesheetEntry.setToTime(timesheetEntry.getFromTime().plusMinutes(Utility.DEFAULT_DAILY_WORKING_HOURS_IN_MINUTES));
        timesheetEntry.setWorkedMinutes((int) ChronoUnit.MINUTES.between(timesheetEntry.getFromTime(), timesheetEntry.getToTime()));
        timesheetEntry.setBreakInMin(Utility.DEFAULT_DAILY_BREAK_IN_MINUTES);
        return timesheetEntry;
    }

    public static TimesheetEntry clone(TimesheetEntry timesheetEntry) {
        if (timesheetEntry == null) {
            return null;
        }
        TimesheetEntry clone = new TimesheetEntry();
        clone.setTimesheetId(timesheetEntry.getTimesheetId());
        clone.setBreakInMin(timesheetEntry.getBreakInMin());
        clone.setWorkedMinutes(timesheetEntry.getWorkedMinutes());
        clone.setStatus(timesheetEntry.getStatus());
        clone.setType(timesheetEntry.getType());
        clone.setWorkdayWeek(timesheetEntry.getWorkdayWeek());
        clone.setBreakInMin(timesheetEntry.getBreakInMin());
        clone.setFromTime(timesheetEntry.getFromTime());
        clone.setToTime(timesheetEntry.getToTime());
        clone.setComment(timesheetEntry.getComment());
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
        final StringBuilder sb = new StringBuilder("TimesheetEntry{");
        sb.append("id=").append(getId());
        sb.append(", uuid=").append(getUuid());
        sb.append(", timesheetId=").append(timesheetId);
        sb.append(", createdDate=").append(getCreatedDate());
        sb.append(", lastModifiedDate=").append(getLastModifiedDate());
        sb.append(", workdayWeek=").append(workdayWeek);
        sb.append(", workdayDate=").append(workdayDate);
        sb.append(", fromTime=").append(fromTime);
        sb.append(", toTime=").append(toTime);
        sb.append(", workedMinutes=").append(workedMinutes);
        sb.append(", breakInMin=").append(breakInMin);
        sb.append(", status='").append(status);
        sb.append(", type=").append(type);
        sb.append(", comment='").append(comment);
        sb.append(", useAsDefault='").append(useAsDefault);
        sb.append('}');
        return sb.toString();
    }
}
