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
 * A timesheet is unique based on user account id, client id, year and mont.
 * This model is basically based upon one timesheet per month.
 * <p>
 * When status is set equal to BILLED, the is is no possible to change the timesheet or any assigned timesheet entry.
 */
@Getter
@Setter
@TypeConverters({LocalDateConverter.class, LocalDateTimeConverter.class})
@Entity(tableName = "timesheet", indices = {@Index(value = {"user_account_id", "client_id", "year", "month"},
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
        ACTIVE, COMPLETED, BILLED;
    }

    @ColumnInfo(name = "status")
    private String status;
    @NotNull
    @ColumnInfo(name = "user_account_id")
    private Long userId;
    @NotNull
    @ColumnInfo(name = "client_id")
    private Long clientId;
    @NotNull
    @ColumnInfo(name = "year")
    private Integer year;
    @NotNull
    @ColumnInfo(name = "month")
    private Integer month;
    @NotNull
    @ColumnInfo(name = "from_date")
    private LocalDate fromDate;
    @NotNull
    @ColumnInfo(name = "to_date")
    private LocalDate toDate;
    @ColumnInfo(name = "description")
    private String description;
    @ColumnInfo(name = "working_days_in_month", defaultValue = "0")
    private Integer workingDaysInMonth = 0;
    @ColumnInfo(name = "working_hours_in_month", defaultValue = "0")
    private Integer workingHoursInMonth = 0;

    public Timesheet() {
        this.status = TimesheetStatusEnum.ACTIVE.name();
    }

    @NonNull
    public Long getUserId() {
        return userId;
    }

    public void setUserId(@NonNull Long userId) {
        this.userId = userId;
    }

    @NonNull
    public Long getClientId() {
        return clientId;
    }

    public void setClientId(@NonNull Long clientId) {
        this.clientId = clientId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return status.equals(TimesheetStatusEnum.ACTIVE.name());
    }

    public boolean isCompleted() {
        return status.equals(TimesheetStatusEnum.COMPLETED.name());
    }

    public boolean isBilled() {
        return status.equals(TimesheetStatusEnum.BILLED.name());
    }

    public static Timesheet createDefault(Long userId, Long clientId, Integer year, Integer month) {
        LocalDate timesheetDate = LocalDate.of(year, month, 1);
        Timesheet timesheet = new Timesheet();
        timesheet.setUserId(userId);
        timesheet.setClientId(clientId);
        timesheet.setStatus(Timesheet.TimesheetStatusEnum.ACTIVE.name());
        timesheet.setYear(timesheetDate.getYear());
        timesheet.setMonth(timesheetDate.getMonthValue());
        timesheet.setFromDate(Utility.getFirstDayOfMonth(timesheetDate));
        timesheet.setToDate(Utility.getLastDayOfMonth(timesheetDate));
        timesheet.setWorkingDaysInMonth(Utility.countBusinessDaysInMonth(timesheet.getFromDate()));
        timesheet.setWorkingHoursInMonth((int) (timesheet.getWorkingDaysInMonth() * 7.5));
        return timesheet;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Timesheet timesheet = (Timesheet) o;
        return Objects.equals(userId, timesheet.userId) && Objects.equals(clientId, timesheet.clientId) && Objects.equals(year, timesheet.year) && Objects.equals(month, timesheet.month);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, clientId, year, month);
    }

    /**
     * @return the unique key for a timesheet
     */
    @NonNull
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Timesheet{");
        sb.append("id=").append(getId());
        sb.append(", userId=").append(userId);
        sb.append(", clientId=").append(clientId);
        sb.append(", year=").append(year);
        sb.append(", month=").append(month);
        sb.append(", status=").append(status);
        sb.append('}');
        return sb.toString();
    }
}
