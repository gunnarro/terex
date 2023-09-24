package com.gunnarro.android.terex.domain.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
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
@Entity(tableName = "invoice")
public class Invoice {

    @PrimaryKey(autoGenerate = true)
    public int id;
    @ColumnInfo(name = "invoice_id")
    private Integer invoiceId;
    @ColumnInfo(name = "client_id")
    private Integer clientId;
    @ColumnInfo(name = "invoice_date")
    private LocalDate invoiceDate;
    @ColumnInfo(name = "invoice_status")
    private String status;
    @ColumnInfo(name = "due_date")
    private LocalDate dueDate;
    @ColumnInfo(name = "vat")
    private double vat;
    @ColumnInfo(name = "amount")
    private double amount;

    private transient List<InvoiceSummary> invoiceSummaryList;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Integer invoiceId) {
        this.invoiceId = invoiceId;
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(LocalDate invoiceDate) {
        this.invoiceDate = invoiceDate;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Invoice invoice = (Invoice) o;
        return id == invoice.id && Double.compare(invoice.vat, vat) == 0 && Double.compare(invoice.amount, amount) == 0 && invoiceId.equals(invoice.invoiceId) && Objects.equals(clientId, invoice.clientId) && Objects.equals(invoiceDate, invoice.invoiceDate) && Objects.equals(dueDate, invoice.dueDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, invoiceId);
    }
}
