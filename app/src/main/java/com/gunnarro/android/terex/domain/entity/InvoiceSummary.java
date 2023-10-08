package com.gunnarro.android.terex.domain.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.gunnarro.android.terex.domain.converter.LocalDateConverter;
import com.gunnarro.android.terex.domain.converter.LocalDateTimeConverter;

import java.time.LocalDate;
import java.util.Objects;

@TypeConverters({LocalDateConverter.class, LocalDateTimeConverter.class})
@Entity(tableName = "invoice_summary", indices = {@Index(value = {"invoice_id", "year", "week_in_year"},
        unique = true)})
public class InvoiceSummary extends BaseEntity {

    @PrimaryKey(autoGenerate = true)
    public Long id;
    @ColumnInfo(name = "invoice_id")
    public Long invoiceId;
    @ColumnInfo(name = "year")
    private Integer year;
    @ColumnInfo(name = "week_in_year")
    private Integer weekInYear;
    @ColumnInfo(name = "from_date")
    private LocalDate fromDate;
    @ColumnInfo(name = "to_date")
    private LocalDate toDate;
    @ColumnInfo(name = "sum_worked_days")
    private Integer sumWorkedDays = 0;
    @ColumnInfo(name = "sum_worked_hours")
    private double sumWorkedHours = 0;
    @ColumnInfo(name = "sum_billed_work")
    private double sumBilledWork = 0;

    public InvoiceSummary() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
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

    public Integer getSumWorkedDays() {
        return sumWorkedDays;
    }

    public void setSumWorkedDays(Integer sumWorkedDays) {
        this.sumWorkedDays = sumWorkedDays;
    }

    public double getSumWorkedHours() {
        return sumWorkedHours;
    }

    public void setSumWorkedHours(double sumWorkedHours) {
        this.sumWorkedHours = sumWorkedHours;
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
        InvoiceSummary that = (InvoiceSummary) o;
        return invoiceId.equals(that.invoiceId) && year.equals(that.year) && weekInYear.equals(that.weekInYear);
    }

    @Override
    public int hashCode() {
        return Objects.hash(invoiceId, year, weekInYear);
    }

    @NonNull
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("InvoiceSummary{");
        sb.append("id=").append(id);
        sb.append(", invoiceId=").append(invoiceId);
        sb.append(", year=").append(year);
        sb.append(", weekInYear=").append(weekInYear);
        sb.append(", fromDate=").append(fromDate);
        sb.append(", toDate=").append(toDate);
        sb.append(", sumWorkedDays=").append(sumWorkedDays);
        sb.append(", sumWorkedHours=").append(sumWorkedHours);
        sb.append(", sumBilledWork=").append(sumBilledWork);
        sb.append('}');
        return sb.toString();
    }
}
