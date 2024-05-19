package com.gunnarro.android.terex.domain.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.TypeConverters;

import com.gunnarro.android.terex.domain.converter.LocalDateConverter;
import com.gunnarro.android.terex.domain.converter.LocalDateTimeConverter;
import com.gunnarro.android.terex.utility.Utility;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

/**
 * Timesheet is the top level class used to track the time a particular employee has worked during a certain period.
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
public class Timesheet extends BaseEntity {

    /**
     * <ul>
     *  <li>NEW      : Created but not in use, i.e, the timesheet is empty. When first timesheet entry is added the status changes to ACTIVE.</li>
     *  <li>ACTIVE   : Open and have work registered, i.e, timesheet entries. When attached to a invoice the status changes to BILLED.</li>
     *  <li>COMPLETED: Ready for billing, i.e, to be used as attachment for the invoice. In this state you are still able to edit the timesheet by flip the status back to ACTIVE.</li>
     *  <li>BILLED   : Have been added as attachment to a invoice. Not possible to change or delete the timesheet or any assigned timesheet entries.</li>
     * </u>
     */
    public enum TimesheetStatusEnum {
        NEW, ACTIVE, COMPLETED, BILLED;

        public static String[] names() {
            return new String[]{NEW.name(), ACTIVE.name(), COMPLETED.name(), BILLED.name()};
        }
    }

    @ColumnInfo(name = "project_id")
    private Long projectId;

    /**
     * Must be unique, typically one timesheet per month.
     * the ref is composed with: clientName_projectCode_year_month
     */
//    @NotNull
    @ColumnInfo(name = "timesheet_ref")
    private String timesheetRef;

    @NotNull
    @ColumnInfo(name = "client_name")
    private String clientName;

    /**
     * use projectId
     */
    @Deprecated
    @NotNull
    @ColumnInfo(name = "project_code")
    private String projectCode;
    @NotNull
    @ColumnInfo(name = "year")
    private Integer year;
    @NotNull
    @ColumnInfo(name = "month")
    private Integer month;
    @NotNull
    @ColumnInfo(name = "from_date")
    private LocalDate fromDate;
    @ColumnInfo(name = "working_days_in_month", defaultValue = "0")
    private Integer workingDaysInMonth = 0;
    @ColumnInfo(name = "working_hours_in_month", defaultValue = "0")
    private Integer workingHoursInMonth = 0;
    @NotNull
    @ColumnInfo(name = "to_date")
    private LocalDate toDate;
    @NotNull
    @ColumnInfo(name = "status", defaultValue = "OPEN")
    private String status;
    @ColumnInfo(name = "description")
    private String description;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getTimesheetRef() {
        return timesheetRef;
    }

    public void setTimesheetRef(String timesheetRef) {
        this.timesheetRef = timesheetRef;
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

    @NotNull
    public Integer getYear() {
        return year;
    }

    public void setYear(@NotNull Integer year) {
        this.year = year;
    }

    @NotNull
    public Integer getMonth() {
        return month;
    }

    public void setMonth(@NotNull Integer month) {
        this.month = month;
    }

    @NotNull
    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(@NotNull LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public Integer getWorkingDaysInMonth() {
        return workingDaysInMonth;
    }

    public void setWorkingDaysInMonth(Integer workingDaysInMonth) {
        this.workingDaysInMonth = workingDaysInMonth;
    }

    public Integer getWorkingHoursInMonth() {
        return workingHoursInMonth;
    }

    public void setWorkingHoursInMonth(Integer workingHoursInMonth) {
        this.workingHoursInMonth = workingHoursInMonth;
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


    public boolean isNew() {
        return status.equals(TimesheetStatusEnum.NEW.name());
    }

    public boolean isActive() {
        return status.equals(TimesheetStatusEnum.ACTIVE.name());
    }

    public boolean isClosed() {
        return status.equals(TimesheetStatusEnum.BILLED.name());
    }

    public boolean isCompleted() {
        return status.equals(TimesheetStatusEnum.COMPLETED.name());
    }


    public boolean isBilled() {
        return status.equals(TimesheetStatusEnum.BILLED.name());
    }

    /**
     * unique reference for this timesheet
     */
    public void createTimesheetRef() {
        timesheetRef = year + ":" + month + ":" + clientName + ":" + projectCode;
    }

    public static Timesheet createDefault(String clientName, String projectCode, Integer year, Integer month) {
        LocalDate timesheetDate = LocalDate.of(year, month, 1);
        Timesheet timesheet = new Timesheet();
        timesheet.setClientName(clientName);
        timesheet.setProjectCode(projectCode);
        timesheet.setProjectId(1L);
        timesheet.setStatus(Timesheet.TimesheetStatusEnum.NEW.name());
        timesheet.setYear(timesheetDate.getYear());
        timesheet.setMonth(timesheetDate.getMonthValue());
        timesheet.setFromDate(Utility.getFirstDayOfMonth(timesheetDate));
        timesheet.setToDate(Utility.getLastDayOfMonth(timesheetDate));
        timesheet.createTimesheetRef();
        timesheet.setWorkingDaysInMonth(Utility.countBusinessDaysInMonth(timesheet.getFromDate()));
        timesheet.setWorkingHoursInMonth((int) (timesheet.getWorkingDaysInMonth() * 7.5));
        return timesheet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Timesheet timesheet = (Timesheet) o;
        return clientName.equals(timesheet.clientName) && projectCode.equals(timesheet.projectCode) && year.equals(timesheet.year) && month.equals(timesheet.month);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientName, projectCode, year, month);
    }

    @NonNull
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Timesheet{");
        sb.append("id=").append(getId());
        sb.append(", uuid='").append(getUuid()).append('\'');
        sb.append(", timesheetRef='").append(timesheetRef).append('\'');
        sb.append(", clientName='").append(clientName).append('\'');
        sb.append(", projectCode='").append(projectCode).append('\'');
        sb.append(", year=").append(year);
        sb.append(", month=").append(month);
        sb.append(", fromDate=").append(fromDate);
        sb.append(", workingDaysInMonth=").append(workingDaysInMonth);
        sb.append(", workingHoursInMonth=").append(workingHoursInMonth);
        sb.append(", toDate=").append(toDate);
        sb.append(", status='").append(status).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
