package com.gunnarro.android.terex.domain.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.TypeConverters;

import com.gunnarro.android.terex.domain.converter.LocalDateConverter;
import com.gunnarro.android.terex.domain.converter.LocalDateTimeConverter;
import com.gunnarro.android.terex.repository.InvoiceRepository;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
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

    /*
     * Invoicing Period means monthly or annually as specified in the confirmation invoice from the Company.
     */
    public enum InvoicePeriodEnum {
        MONTH
    }

    /**
     * Hold a unique reference to the timesheet that is used as the basis for the invoice
     */
    @NotNull
    @ColumnInfo(name = "timesheet_id")
    private Long timesheetId;
    @NotNull
    @ColumnInfo(name = "invoice_number")
    private Integer invoiceNumber;
    @NotNull
    @ColumnInfo(name = "client_id")
    private Long clientId;
    /**
     * invoice recipient : fakturamottaker
     */
    @NotNull
    @ColumnInfo(name = "invoice_recipient_id")
    private Long invoiceRecipientId;

    /**
     * invoice issuer : fakturautsteder
     */
    @NotNull
    @ColumnInfo(name = "invoice_issuer_id")
    private Long invoiceIssuerId;
    @ColumnInfo(name = "reference")
    private String reference;
    @NotNull
    @ColumnInfo(name = "invoice_status", defaultValue = "OPEN")
    private String status;
    @NotNull
    @ColumnInfo(name = "billing_date")
    private LocalDate billingDate;
    @ColumnInfo(name = "billing_period_start_date")
    private LocalDate billingPeriodStartDate;
    @ColumnInfo(name = "billing_period_end_date")
    private LocalDate billingPeriodEndDate;
    @NotNull
    @ColumnInfo(name = "due_date")
    private LocalDate dueDate;
    @NotNull
    @ColumnInfo(name = "vat")
    private double vat;
    @NotNull
    @ColumnInfo(name = "amount", defaultValue = "0")
    private double amount;
    /**
     * ISO 427 currency code
     */
    @NotNull
    @ColumnInfo(name = "currency", defaultValue = "NOK")
    private String currency;

    /**
     * invoicing Period means weekly, every 14-day, monthly or annually as specified in the confirmation invoice from the Company.
     * Fakturaperioden er alltid fra 01. til siste dag av måneden, og gjelder forbruk for måneden før du mottar faktura.
     */
    @NotNull
    @ColumnInfo(name = "invoice_period", defaultValue = "monthly")
    private String invoicePeriod;

    public Long getTimesheetId() {
        return timesheetId;
    }

    public void setTimesheetId(Long timesheetId) {
        this.timesheetId = timesheetId;
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

    public Long getInvoiceRecipientId() {
        return invoiceRecipientId;
    }

    public void setInvoiceRecipientId(Long invoiceRecipientId) {
        this.invoiceRecipientId = invoiceRecipientId;
    }

    public Long getInvoiceIssuerId() {
        return invoiceIssuerId;
    }

    public void setInvoiceIssuerId(Long invoiceIssuerId) {
        this.invoiceIssuerId = invoiceIssuerId;
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

    public LocalDate getBillingPeriodStartDate() {
        return billingPeriodStartDate;
    }

    public void setBillingPeriodStartDate(LocalDate billingPeriodStartDate) {
        this.billingPeriodStartDate = billingPeriodStartDate;
    }

    public LocalDate getBillingPeriodEndDate() {
        return billingPeriodEndDate;
    }

    public void setBillingPeriodEndDate(LocalDate billingPeriodEndDate) {
        this.billingPeriodEndDate = billingPeriodEndDate;
    }

    @NotNull
    public String getInvoicePeriod() {
        return invoicePeriod;
    }

    public void setInvoicePeriod(@NotNull String invoicePeriod) {
        this.invoicePeriod = invoicePeriod;
    }

    @NotNull
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(@NotNull String currency) {
        this.currency = currency;
    }

    public boolean isCompleted() {
        return status.equals(InvoiceRepository.InvoiceStatusEnum.COMPLETED.name());
    }

    public boolean isNew() {
        return status.equals(InvoiceRepository.InvoiceStatusEnum.NEW.name());
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
