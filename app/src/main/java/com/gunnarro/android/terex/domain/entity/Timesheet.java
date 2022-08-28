package com.gunnarro.android.terex.domain.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.gunnarro.android.terex.domain.converter.LocalDateConverter;
import com.gunnarro.android.terex.domain.converter.LocalTimeConverter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

@TypeConverters({LocalDateConverter.class, LocalTimeConverter.class})
@Entity(tableName = "timesheet")
public class Timesheet {

    @PrimaryKey(autoGenerate = true)
    private Long id;

    @NonNull
    @ColumnInfo(name = "client_name", index = true)
    private String clientName;

    @NonNull
    @ColumnInfo(name = "project_name", index = true)
    private String projectName;

    @NonNull
    @ColumnInfo(name = "workday_date")
    private LocalDate workdayDate;

    @NonNull
    @ColumnInfo(name = "from_time")
    private LocalTime fromTime;

    @NonNull
    @ColumnInfo(name = "to_time")
    private LocalTime toTime;

    @ColumnInfo(name = "worked_in_min")
    private Integer workedMinutes;

    @ColumnInfo(name = "break_in_min")
    private Integer breakInMin;

    @ColumnInfo(name = "hourly_rate")
    private Integer hourlyRate;

    @NonNull
    @ColumnInfo(name = "status", defaultValue = "Open")
    private String status;

    @ColumnInfo(name = "comment")
    private String comment;

    /**
     * default constructor, Room accepts only one
     */
    public Timesheet() {
    }

    public Long getId() {
        return id;
    }

    public void setId(@NonNull Long id) {
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
    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(@NonNull String projectName) {
        this.projectName = projectName;
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

    public void setWorkedMinutes(@NonNull Integer workedMinutes) {
        this.workedMinutes = workedMinutes;
    }

    public Integer getBreakInMin() {
        return breakInMin;
    }

    public void setBreakInMin(@NonNull Integer breakInMin) {
        this.breakInMin = breakInMin;
    }

    public Integer getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(@NonNull Integer hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Timesheet timesheet = (Timesheet) o;
        return clientName.equals(timesheet.clientName) && projectName.equals(timesheet.projectName) && workdayDate.equals(timesheet.workdayDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientName, projectName, workdayDate);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Timesheet{");
        sb.append("id=").append(id);
        sb.append(", clientName='").append(clientName).append('\'');
        sb.append(", projectName='").append(projectName).append('\'');
        sb.append(", workdayDate=").append(workdayDate);
        sb.append(", fromTime=").append(fromTime);
        sb.append(", toTime=").append(toTime);
        sb.append(", status=").append(status);
        sb.append('}');
        return sb.toString();
    }
}
