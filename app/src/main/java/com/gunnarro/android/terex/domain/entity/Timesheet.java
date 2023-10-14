package com.gunnarro.android.terex.domain.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.gunnarro.android.terex.domain.converter.LocalDateConverter;
import com.gunnarro.android.terex.domain.converter.LocalDateTimeConverter;
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
 * <p>
 * A timesheet is unique based on client name, project code, year and mount.
 * This model is basically based upon one timesheet per month.
 * <p>
 * When status is set equal to BILLED, the is is no possible to change the timesheet or any assigned timesheet entry.
 */
@Getter
@Setter
@TypeConverters({LocalDateConverter.class, LocalDateTimeConverter.class})
@Entity(tableName = "timesheet", indices = {@Index(value = {"client_name", "project_code", "year", "month"},
        unique = true)})
public class Timesheet {

    /**
     * OPEN: Created but not in use, i.e, the timesheet is empty. When first timesheet entry is added the status changes to ACTIVE.
     * ACTIVE: Open and have work registered, i.e, timesheet entries. When attached to a invoice the status changes to BILLED.
     * BILLED: Added as attachment to a invoice. Not possible to change or delete the timesheet or any assigned timesheet entries.
     */
    public enum TimesheetStatusEnum {
        OPEN, ACTIVE, BILLED;
    }

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

    /**
     * Must be unique, typically one timesheet per month.
     * the ref is composed with: clientName_projectCode_year_month
     */
    @NotNull
    @ColumnInfo(name = "timesheet_ref")
    private String timesheetRef;

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

    public Long getId() {
        return id;
    }

    public Long getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(Long invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public void setId(Long id) {
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

    /**
     * @return 1 - 12
     */
    public Integer getMonth() {
        return month;
    }

    /**
     * @param month 1 to 12
     */
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

    @NotNull
    public String getTimesheetRef() {
        return timesheetRef;
    }

    /**
     * unique reference for this timesheet
     */
    public void createTimesheetRef() {
        timesheetRef = year + ":" + month + ":" + clientName + ":" + projectCode;
    }

    public static Timesheet createDefault(String clientName, String projectCode) {
        LocalDate now = LocalDate.now();
        Timesheet timesheet = new Timesheet();
        timesheet.setClientName(clientName);
        timesheet.setProjectCode(projectCode);
        timesheet.setStatus(Timesheet.TimesheetStatusEnum.OPEN.name());
        timesheet.setYear(now.getYear());
        timesheet.setMonth(now.getMonthValue());
        timesheet.setFromDate(Utility.getFirstDayOfMonth(now));
        timesheet.setToDate(Utility.getLastDayOfMonth(now));
        timesheet.createTimesheetRef();
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
