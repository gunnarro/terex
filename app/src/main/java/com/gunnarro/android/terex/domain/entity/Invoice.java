package com.gunnarro.android.terex.domain.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.gunnarro.android.terex.domain.converter.LocalDateConverter;
import com.gunnarro.android.terex.domain.converter.LocalDateTimeConverter;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
@TypeConverters({LocalDateConverter.class, LocalDateTimeConverter.class})
@Entity(tableName = "invoice", indices = {@Index(value = {"client_id", "timesheet_id"},
        unique = true)})
public class Invoice extends BaseEntity {

    @PrimaryKey(autoGenerate = true)
    private Long id;
    @NotNull
    @ColumnInfo(name = "timesheet_id")
    private Long timesheetId;
    @ColumnInfo(name = "invoice_file_id")
    private Long invoiceFileId;
    @NotNull
    @ColumnInfo(name = "invoice_number")
    private Integer invoiceNumber;
    @NotNull
    @ColumnInfo(name = "client_id")
    private Long clientId;
    @ColumnInfo(name = "reference")
    private String reference;
    @ColumnInfo(name = "invoice_status")
    private String status;
    @ColumnInfo(name = "billing_date")
    private LocalDate billingDate;
    @ColumnInfo(name = "due_date")
    private LocalDate dueDate;
    @ColumnInfo(name = "vat")
    private double vat;
    @ColumnInfo(name = "amount")
    private double amount;

    private transient List<TimesheetSummary> timesheetSummaryList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTimesheetId() {
        return timesheetId;
    }

    public void setTimesheetId(Long timesheetId) {
        this.timesheetId = timesheetId;
    }

    @NotNull
    public Long getInvoiceFileId() {
        return invoiceFileId;
    }

    public void setInvoiceFileId(@NotNull Long invoiceFileId) {
        this.invoiceFileId = invoiceFileId;
    }

    public Integer getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(Integer invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getBillingDate() {
        return billingDate;
    }

    public void setBillingDate(LocalDate billingDate) {
        this.billingDate = billingDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public double getVat() {
        return vat;
    }

    public void setVat(double vat) {
        this.vat = vat;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public List<TimesheetSummary> getTimesheetSummaryList() {
        return timesheetSummaryList;
    }

    public void setTimesheetSummaryList(List<TimesheetSummary> timesheetSummaryList) {
        this.timesheetSummaryList = timesheetSummaryList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Invoice invoice = (Invoice) o;
        return timesheetId.equals(invoice.timesheetId) && clientId.equals(invoice.clientId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timesheetId, clientId);
    }
}
