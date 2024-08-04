package com.gunnarro.android.terex.domain.dto;

public class AccountingDto {
    private double totalBilledAmount;
    private double totalBilledAmountVat;
    private double totalEmployeeSalary;
    private double totalEmployeeSalaryTax;
    private double totalEmployersSalaryTax;
    private double totalEmployersCompanyTax;
    private double resultBeforeTaxes;
    private double employeesMaxSalary;
    private String accountingPeriod;
    private String employeesMaxSalaryPeriod;

    public double getTotalBilledAmount() {
        return totalBilledAmount;
    }

    public void setTotalBilledAmount(double totalBilledAmount) {
        this.totalBilledAmount = totalBilledAmount;
    }

    public double getTotalBilledAmountVat() {
        return totalBilledAmountVat;
    }

    public void setTotalBilledAmountVat(double totalBilledAmountVat) {
        this.totalBilledAmountVat = totalBilledAmountVat;
    }

    public double getTotalEmployeeSalary() {
        return totalEmployeeSalary;
    }

    public void setTotalEmployeeSalary(double totalEmployeeSalary) {
        this.totalEmployeeSalary = totalEmployeeSalary;
    }

    public double getTotalEmployeeSalaryTax() {
        return totalEmployeeSalaryTax;
    }

    public void setTotalEmployeeSalaryTax(double totalEmployeeSalaryTax) {
        this.totalEmployeeSalaryTax = totalEmployeeSalaryTax;
    }

    public double getTotalEmployersSalaryTax() {
        return totalEmployersSalaryTax;
    }

    public void setTotalEmployersSalaryTax(double totalEmployersSalaryTax) {
        this.totalEmployersSalaryTax = totalEmployersSalaryTax;
    }

    public double getTotalEmployersCompanyTax() {
        return totalEmployersCompanyTax;
    }

    public void setTotalEmployersCompanyTax(double totalEmployersCompanyTax) {
        this.totalEmployersCompanyTax = totalEmployersCompanyTax;
    }

    public double getResultBeforeTaxes() {
        return resultBeforeTaxes;
    }

    public void setResultBeforeTaxes(double resultBeforeTaxes) {
        this.resultBeforeTaxes = resultBeforeTaxes;
    }

    public double getEmployeesMaxSalary() {
        return employeesMaxSalary;
    }

    public void setEmployeesMaxSalary(double employeesMaxSalary) {
        this.employeesMaxSalary = employeesMaxSalary;
    }

    public String getEmployeesMaxSalaryPeriod() {
        return employeesMaxSalaryPeriod;
    }

    public void setEmployeesMaxSalaryPeriod(String employeesMaxSalaryPeriod) {
        this.employeesMaxSalaryPeriod = employeesMaxSalaryPeriod;
    }

    public String getAccountingPeriod() {
        return accountingPeriod;
    }

    public void setAccountingPeriod(String accountingPeriod) {
        this.accountingPeriod = accountingPeriod;
    }
}
