package com.gunnarro.android.terex.domain.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.gunnarro.android.terex.domain.converter.LocalDateConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@TypeConverters(LocalDateConverter.class)
@Entity(tableName = "invoice_summary")
public class InvoiceSummary {

    @PrimaryKey(autoGenerate = true)
    public int id;
    @ColumnInfo(name = "invoice_id")
    public int invoiceId;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(int invoiceId) {
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

    public String getFromDateDDMM() {
        return fromDate.format(DateTimeFormatter.ofPattern("dd.MM"));
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public String getToDateDDMM() {
        return toDate.format(DateTimeFormatter.ofPattern("dd.MM"));
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }

    public Integer getSumWorkedDays() {
        return sumWorkedDays;
    }

    public double getSumWorkedHours() {
        return sumWorkedHours;
    }

    public void setSumWorkedHours(double sumWorkedHours) {
        this.sumWorkedHours = sumWorkedHours;
    }

    public void setSumWorkedDays(Integer sumWorkedDays) {
        this.sumWorkedDays = sumWorkedDays;
    }


    public double getSumBilledWork() {
        return sumBilledWork;
    }

    public void setSumBilledWork(double sumBilledWork) {
        this.sumBilledWork = sumBilledWork;
    }

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
