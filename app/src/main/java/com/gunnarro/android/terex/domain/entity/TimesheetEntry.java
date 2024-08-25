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
@Getter
@Setter
@TypeConverters({LocalDateConverter.class, LocalTimeConverter.class, LocalDateTimeConverter.class})
@Entity(tableName = "timesheet_entry",
        foreignKeys = {@ForeignKey(entity = Timesheet.class, parentColumns = "id", childColumns = "timesheet_id", onDelete = BaseEntity.CASCADE)},
        indices = {@Index(value = {"timesheet_id", "project_id", "workday_date"}, unique = true)})
public class TimesheetEntry extends BaseEntity {

    public enum TimesheetEntryTypeEnum {
        REGULAR, VACATION, SICK
    }

    /**
     * A timesheet entry status is set to OPEN as default. The status is set equal to CLOSED when the timesheet is billed.
     * It is not allowed to edit a timesheet og a timesheet entry when the timesheet has status BILLED.
     */
    public enum TimesheetEntryStatusEnum {
        OPEN, CLOSED
    }

    /**
     * Status can only be OPEN and CLOSED. When CLOSED is not possible to change or delete the entry.
     * The status is automatically set equal to CLOSED when a timesheet have been completed and billed.
     */
    @ColumnInfo(name = "status")
    private String status;

    /**
     * Hold a unique reference to the timesheet that is entry is assigned to.
     */
    @NonNull
    @ColumnInfo(name = "timesheet_id")
    private Long timesheetId;

    @NonNull
    @ColumnInfo(name = "project_id")
    private Long projectId;

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
    private String type = TimesheetEntryTypeEnum.REGULAR.name();

    @ColumnInfo(name = "start_time")
    private LocalTime startTime;

    @Deprecated
    @ColumnInfo(name = "end_time")
    private LocalTime endTime;

    @NonNull
    @ColumnInfo(name = "worked_seconds")
    private Long workedSeconds;

    @ColumnInfo(name = "comments")
    private String comments;

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

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    @NonNull
    public Integer getWorkdayWeek() {
        return workdayWeek;
    }

    public Integer getWorkdayMonth() {
        return workdayDate != null ? workdayDate.getMonthValue() : null;
    }

    public Integer getWorkdayYear() {
        return workdayDate != null ? workdayDate.getYear() : null;
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

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        if (startTime != null && workedSeconds != null) {
            return startTime.plusSeconds(workedSeconds);
        }
        return null;
    }

    public String getWorkedHours() {
        if (workedSeconds != null) {
            return Double.toString((double) workedSeconds / (60 * 60));
        }
        return null;
    }

    public Long getWorkedSeconds() {
        return workedSeconds;
    }

    public void setWorkedSeconds(Long workedSeconds) {
        this.workedSeconds = workedSeconds;
    }

    /**
     * Valid statues: OPEN, BILLED
     */
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
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
        return TimesheetEntryTypeEnum.REGULAR.name().equals(type);
    }

    public boolean isVacationDay() {
        return TimesheetEntryTypeEnum.VACATION.name().equals(type);
    }

    public boolean isSickDay() {
        return TimesheetEntryTypeEnum.SICK.name().equals(type);
    }

    public boolean isOpen() {
        return TimesheetEntryStatusEnum.OPEN.name().equals(status);
    }

    public boolean isClosed() {
        return TimesheetEntryStatusEnum.CLOSED.name().equals(status);
    }

    public static TimesheetEntry createNotWorked(@NotNull Long timesheetId, Long projectId, @NotNull LocalDate workDayDate, String type) {
        TimesheetEntry timesheetEntry = new TimesheetEntry();
        timesheetEntry.setTimesheetId(timesheetId);
        timesheetEntry.setProjectId(projectId);
        timesheetEntry.setType(type);
        timesheetEntry.setWorkdayDate(workDayDate);
        timesheetEntry.setWorkedSeconds(0L);
        timesheetEntry.setStatus(TimesheetEntryStatusEnum.CLOSED.name());
        timesheetEntry.setWorkdayWeek(timesheetEntry.getWorkdayDate().get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear()));
        return timesheetEntry;
    }

    public static TimesheetEntry create(@NotNull Long timesheetId, Long projectId, @NotNull LocalDate workDayDate, LocalTime startTime, Long workedSeconds, String comments) {
        TimesheetEntry timesheetEntry = new TimesheetEntry();
        timesheetEntry.setTimesheetId(timesheetId);
        timesheetEntry.setProjectId(projectId);
        timesheetEntry.setStatus(TimesheetEntryStatusEnum.OPEN.name());
        timesheetEntry.setType(TimesheetEntryTypeEnum.REGULAR.name());
        timesheetEntry.setWorkdayDate(workDayDate);
        timesheetEntry.setWorkdayWeek(timesheetEntry.getWorkdayDate().get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear()));
        // add 7,5 hours
        timesheetEntry.setStartTime(startTime);
        timesheetEntry.setWorkedSeconds((Utility.DEFAULT_DAILY_WORKING_HOURS_IN_SECONDS));
        timesheetEntry.setComments(comments);
        return timesheetEntry;
    }

    public static TimesheetEntry createDefault(@NotNull Long timesheetId, Long projectId, @NotNull LocalDate workDayDate) {
        return create(timesheetId, projectId, workDayDate, LocalTime.now(ZoneId.systemDefault()).withHour(8).withMinute(0).withSecond(0).withSecond(0).withNano(0), Utility.DEFAULT_DAILY_WORKING_HOURS_IN_SECONDS, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimesheetEntry that = (TimesheetEntry) o;
        return Objects.equals(timesheetId, that.timesheetId) && Objects.equals(projectId, that.projectId) && Objects.equals(workdayDate, that.workdayDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timesheetId, projectId, workdayDate);
    }

    @NonNull
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TimesheetEntry{");
        sb.append("id=").append(getId());
        sb.append(", uuid=").append(getUuid());
        sb.append(", timesheetId=").append(timesheetId);
        sb.append(", projectId=").append(projectId);
        sb.append(", createdDate=").append(getCreatedDate());
        sb.append(", lastModifiedDate=").append(getLastModifiedDate());
        sb.append(", workdayWeek=").append(workdayWeek);
        sb.append(", workdayDate=").append(workdayDate);
        sb.append(", startTime=").append(startTime);
        sb.append(", endTime=").append(endTime);
        sb.append(", workedSeconds=").append(workedSeconds);
        sb.append(", status='").append(status);
        sb.append(", type=").append(type);
        sb.append(", comment='").append(comments);
        sb.append(", useAsDefault='").append(useAsDefault);
        sb.append('}');
        return sb.toString();
    }
}
