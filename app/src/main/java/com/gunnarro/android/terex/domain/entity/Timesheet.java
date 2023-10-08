package com.gunnarro.android.terex.domain.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.gunnarro.android.terex.domain.converter.LocalDateConverter;
import com.gunnarro.android.terex.domain.converter.LocalDateTimeConverter;
import com.gunnarro.android.terex.repository.TimesheetRepository;
import com.gunnarro.android.terex.utility.Utility;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

/**
 * typically there is one timesheet per month.
 * A timesheet contains time sheet entries.
 */
@Getter
@Setter
@TypeConverters({LocalDateConverter.class, LocalDateTimeConverter.class})
@Entity(tableName = "timesheet", indices = {@Index(value = {"client_name", "project_code", "year", "month"},
        unique = true)})
public class Timesheet {
    @NotNull
    @PrimaryKey(autoGenerate = true)
    public Long id;
    @ColumnInfo(name = "invoice_number")
    public Long invoiceNumber;
    @NonNull
    @ColumnInfo(name = "created_date")
    private LocalDateTime createdDate;
    @NonNull
    @ColumnInfo(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;
    @NotNull
    @ColumnInfo(name = "client_name")
    private String clientName;
    @NotNull
    @ColumnInfo(name = "project_code")
    private String projectCode;
    @ColumnInfo(name = "year")
    private Integer year;
    @ColumnInfo(name = "month")
    private Integer month;
    @NotNull
    @ColumnInfo(name = "from_date")
    private LocalDate fromDate;
    @NotNull
    @ColumnInfo(name = "to_date")
    private LocalDate toDate;
    @NotNull
    @ColumnInfo(name = "status")
    private String status;
    @ColumnInfo(name = "description")
    private String description;

    @NotNull
    public Long getId() {
        return id;
    }

    public Long getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(Long invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public void setId(@NotNull Long id) {
        this.id = id;
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

    public void setLastModifiedDate(@NonNull LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    @NotNull
    public String getClientName() {
        return clientName;
    }

    public void setClientName(@NotNull String clientName) {
        this.clientName = clientName;
    }

    @NotNull
    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(@NotNull String projectCode) {
        this.projectCode = projectCode;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    @NotNull
    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(@NotNull LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    @NotNull
    public LocalDate getToDate() {
        return toDate;
    }

    public void setToDate(@NotNull LocalDate toDate) {
        this.toDate = toDate;
    }

    @NotNull
    public String getStatus() {
        return status;
    }

    public void setStatus(@NotNull String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static Timesheet createDefault(String clientName, String projectCode) {
        LocalDate now = LocalDate.now();
        Timesheet timesheet = new Timesheet();
        timesheet.setClientName(clientName);
        timesheet.setProjectCode(projectCode);
        timesheet.setStatus(TimesheetRepository.TimesheetStatusEnum.OPEN.name());
        timesheet.setYear(now.getYear());
        timesheet.setMonth(now.getMonthValue());
        timesheet.setFromDate(Utility.getFirstDayOfMonth(now));
        timesheet.setToDate(Utility.getLastDayOfMonth(now));
        return timesheet;
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
        Timesheet timesheet = (Timesheet) o;
        return clientName.equals(timesheet.clientName) && projectCode.equals(timesheet.projectCode) && Objects.equals(year, timesheet.year) && Objects.equals(month, timesheet.month);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientName, projectCode, year, month);
    }

    @NonNull
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Timesheet{");
        sb.append("id=").append(id);
        sb.append(", invoiceNumber=").append(invoiceNumber);
        sb.append(", createdDate=").append(createdDate);
        sb.append(", lastModifiedDate=").append(lastModifiedDate);
        sb.append(", clientName='").append(clientName).append('\'');
        sb.append(", projectCode='").append(projectCode).append('\'');
        sb.append(", year=").append(year);
        sb.append(", month=").append(month);
        sb.append(", fromDate=").append(fromDate);
        sb.append(", toDate=").append(toDate);
        sb.append(", status='").append(status).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
