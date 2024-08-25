package com.gunnarro.android.terex.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.gunnarro.android.terex.R;
import com.gunnarro.android.terex.domain.dto.AccountingDto;
import com.gunnarro.android.terex.domain.dto.InvoiceDto;
import com.gunnarro.android.terex.service.InvoiceService;
import com.gunnarro.android.terex.utility.Utility;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

public class AccountingFragment extends Fragment {

    private InvoiceService invoiceService;

    @Inject
    public AccountingFragment() {
        // Needed by dagger framework
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        invoiceService = new InvoiceService();
        Log.d(Utility.buildTag(getClass(), "onCreate"), "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        requireActivity().setTitle(R.string.title_accounting);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_accounting, container, false);
        AccountingDto accountingDto = calculate();
        ((TextView) view.findViewById(R.id.accounting_table_header)).setText(accountingDto.getAccountingPeriod());
        ((TextInputEditText) view.findViewById(R.id.accounting_total_billed_amount)).setText(formatAmount(accountingDto.getTotalBilledAmount()));
        ((TextInputEditText) view.findViewById(R.id.accounting_total_billed_amount_vat)).setText(formatAmount(accountingDto.getTotalBilledAmountVat()));
        ((TextInputEditText) view.findViewById(R.id.accounting_total_employees_salary)).setText(formatAmount(accountingDto.getTotalEmployeeSalary()));
        ((TextInputEditText) view.findViewById(R.id.accounting_total_employees_salary_tax)).setText(formatAmount(accountingDto.getTotalEmployeeSalaryTax()));
        ((TextInputEditText) view.findViewById(R.id.accounting_total_employers_salary_tax)).setText(formatAmount(accountingDto.getTotalEmployersSalaryTax()));
        ((TextInputEditText) view.findViewById(R.id.accounting_total_employers_company_tax)).setText(formatAmount(accountingDto.getTotalEmployersCompanyTax()));
        ((TextInputEditText) view.findViewById(R.id.accounting_result_before_taxes)).setText(formatAmount(accountingDto.getResultBeforeTaxes()));
        ((TextInputEditText) view.findViewById(R.id.accounting_employees_max_salary)).setText(formatAmount(accountingDto.getEmployeesMaxSalary()));

        Log.d(Utility.buildTag(getClass(), "onCreateView"), "");
        return view;
    }

    private AccountingDto calculate() {
        List<InvoiceDto> invoiceList = invoiceService.getInvoiceDtoList();
        AccountingDto accountingDto = new AccountingDto();

        invoiceList.forEach(i -> {
            accountingDto.setTotalBilledAmount(accountingDto.getTotalBilledAmount() + i.getAmount());
            accountingDto.setTotalBilledAmountVat(accountingDto.getTotalBilledAmountVat() + i.getVatAmount());
            accountingDto.setTotalEmployeeSalary(accountingDto.getTotalEmployeeSalary() + 90000 + 10000 + 10000);
            accountingDto.setTotalEmployeeSalaryTax(accountingDto.getTotalEmployersSalaryTax() + 90000 * 0.46);
            accountingDto.setTotalEmployersCompanyTax(accountingDto.getTotalEmployersCompanyTax() + 90000 * 0.14);
        });

        // calculate max salary for the last invoice
        if (!invoiceList.isEmpty()) {
            accountingDto.setEmployeesMaxSalary(invoiceList.get(invoiceList.size() - 1).getAmount() * 0.80);
            accountingDto.setEmployeesMaxSalaryPeriod(invoiceList.get(invoiceList.size() - 1).getBillingPeriodStartDate().format(DateTimeFormatter.ofPattern("MMMM yyyy")));


            LocalDate startDate = invoiceList.get(0).getBillingPeriodStartDate();
            LocalDate endDate = invoiceList.get(invoiceList.size() - 1).getBillingPeriodStartDate();
            accountingDto.setAccountingPeriod(
                    String.format("Accounting period %s to %s",
                            startDate.format(DateTimeFormatter.ofPattern("MMMM")),
                            endDate.format(DateTimeFormatter.ofPattern("MMMM yyyy"))
                    )
            );
        }
        return accountingDto;
    }

    private String formatAmount(double amount) {
        return String.format(Locale.getDefault(), "%.2f", amount);
    }
}