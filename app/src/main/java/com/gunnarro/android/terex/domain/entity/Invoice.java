package com.gunnarro.android.terex.domain.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.gunnarro.android.terex.domain.converter.LocalDateConverter;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
@TypeConverters(LocalDateConverter.class)
@Entity(tableName = "invoice", indices = {@Index(value = {"timesheet_id", "client_id"},
        unique = true)})
public class Invoice {

    @PrimaryKey(autoGenerate = true)
    public Long id;
    @ColumnInfo(name = "invoice_id")
    private Long invoiceId;
    @ColumnInfo(name = "timesheet_id")
    private Long timesheetId;
    @ColumnInfo(name = "client_id")
    private Long clientId;
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

    private transient List<InvoiceSummary> invoiceSummaryList;

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

    public Long getTimesheetId() {
        return timesheetId;
    }

    public void setTimesheetId(Long timesheetId) {
        this.timesheetId = timesheetId;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
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

    public List<InvoiceSummary> getInvoiceSummaryList() {
        return invoiceSummaryList;
    }

    public void setInvoiceSummaryList(List<InvoiceSummary> invoiceSummaryList) {
        this.invoiceSummaryList = invoiceSummaryList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Invoice invoice = (Invoice) o;
        return Objects.equals(invoiceId, invoice.invoiceId) && Objects.equals(timesheetId, invoice.timesheetId) && Objects.equals(clientId, invoice.clientId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(invoiceId, timesheetId, clientId);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Invoice{");
        sb.append("id=").append(id);
        sb.append(", invoiceId=").append(invoiceId);
        sb.append(", timesheetId=").append(timesheetId);
        sb.append(", clientId=").append(clientId);
        sb.append(", status='").append(status).append('\'');
        sb.append(", billingDate=").append(billingDate);
        sb.append(", dueDate=").append(dueDate);
        sb.append(", vat=").append(vat);
        sb.append(", amount=").append(amount);
        sb.append(", invoiceSummaryList=").append(invoiceSummaryList);
        sb.append('}');
        return sb.toString();
    }
}
