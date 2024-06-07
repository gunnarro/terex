package com.gunnarro.android.terex.domain.dto;

import com.gunnarro.android.terex.domain.entity.Timesheet;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.Objects;

public class InvoiceDto {

    private Long id;
    @NotNull
    private Timesheet timesheet;
    /**
     * invoice recipient : fakturamottaker
     */
    @NotNull
    private ClientDto invoiceRecipient;
    /**
     * invoice issuer : fakturautsteder
     */
    @NotNull
    private UserAccountDto invoiceIssuer;
    @NotNull
    private Integer invoiceNumber;
    private String reference;
    @NotNull
    private String status;
    @NotNull
    private LocalDate billingDate;

    private LocalDate billingPeriodStartDate;

    private LocalDate billingPeriodEndDate;
    @NotNull
    private LocalDate dueDate;
    @NotNull
    private double vat;
    @NotNull
    private double amount;
    /**
     * ISO 427 currency code
     */
    @NotNull
    private String currency;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Timesheet getTimesheet() {
        return timesheet;
    }

    public void setTimesheet(Timesheet timesheet) {
        this.timesheet = timesheet;
    }

    public ClientDto getInvoiceRecipient() {
        return invoiceRecipient;
    }

    public void setInvoiceRecipient(ClientDto invoiceRecipient) {
        this.invoiceRecipient = invoiceRecipient;
    }

    public UserAccountDto getInvoiceIssuer() {
        return invoiceIssuer;
    }

    public void setInvoiceIssuer(UserAccountDto invoiceIssuer) {
        this.invoiceIssuer = invoiceIssuer;
    }

    public Integer getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(Integer invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
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

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InvoiceDto that = (InvoiceDto) o;
        return Objects.equals(timesheet, that.timesheet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timesheet);
    }
}
