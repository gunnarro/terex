package com.gunnarro.android.terex.domain.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
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
@Entity(tableName = "timesheet")
public class Timesheet {

    @PrimaryKey(autoGenerate = true)
    private Long id;

    @NonNull
    @ColumnInfo(name = "created_date")
    private LocalDateTime createdDate;

    @NonNull
    @ColumnInfo(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

    @NonNull
    @ColumnInfo(name = "client_name", index = true)
    private String clientName;

    @NonNull
    @ColumnInfo(name = "project_code", index = true)
    private String projectCode;

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
    @ColumnInfo(name = "status", defaultValue = "Open")
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

    public void setId(Long id) {
        this.id = id;
    }

    @NonNull
    public String getClientName() {
        return clientName;
    }

    public void setClientName(@NonNull String clientName) {
        this.clientName = clientName;
    }

    @NonNull
    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(@NonNull String projectCode) {
        this.projectCode = projectCode;
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

    public static Timesheet createDefault(String clientName, String projectCode, String status, Integer dailyBreakMin, LocalDate workDayDate, Long workingHoursMin, Integer hourlyRate) {
        Timesheet timesheet = new Timesheet();
        timesheet.setClientName(clientName);
        timesheet.setProjectCode(projectCode);
        timesheet.setStatus(status);
        timesheet.setWorkdayDate(workDayDate);
        timesheet.setFromTime(LocalTime.now(ZoneId.systemDefault()).withHour(8).withMinute(0).withSecond(0).withSecond(0).withNano(0));
        // add 7,5 hours
        timesheet.setToTime(timesheet.getFromTime().plusMinutes(workingHoursMin));
        timesheet.setWorkedMinutes(Long.valueOf(ChronoUnit.MINUTES.between(timesheet.getFromTime(), timesheet.getToTime())).intValue());
        timesheet.setBreakInMin(dailyBreakMin);
        timesheet.setHourlyRate(hourlyRate);
        return timesheet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Timesheet timesheet = (Timesheet) o;
        return clientName.equals(timesheet.clientName) && projectCode.equals(timesheet.projectCode) && workdayDate.isEqual(timesheet.workdayDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientName, projectCode, workdayDate);
    }

    @NonNull
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Timesheet{");
        sb.append("id=").append(id);
        sb.append(", createdDate=").append(createdDate);
        sb.append(", lastModifiedDate=").append(lastModifiedDate);
        sb.append(", clientName='").append(clientName).append('\'');
        sb.append(", projectCode='").append(projectCode).append('\'');
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

    public static Timesheet clone(Timesheet timesheet) {
        if (timesheet == null) {
            return null;
        }
        Timesheet clone = new Timesheet();
        clone.setProjectCode(timesheet.getProjectCode());
        clone.setClientName(timesheet.getClientName());
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
}
