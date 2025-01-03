package com.gunnarro.android.terex.ui.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.gunnarro.android.terex.R;
import com.gunnarro.android.terex.domain.dto.InvoiceDto;
import com.gunnarro.android.terex.domain.entity.Invoice;
import com.gunnarro.android.terex.utility.Utility;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class InvoiceViewHolder extends RecyclerView.ViewHolder {
    private final TextView invoiceLineHeaderView;
    private final TextView invoiceLine1StatusView;
    private final TextView invoiceLine1LabelView;
    private final TextView invoiceLine1ValueView;
    private final TextView invoiceLine2LabelView;
    private final TextView invoiceLine2ValueView;

    private InvoiceViewHolder(View itemView) {
        super(itemView);
        invoiceLineHeaderView = itemView.findViewById(R.id.invoice_line_header);
        invoiceLine1StatusView = itemView.findViewById(R.id.invoice_line_1_status);
        invoiceLine1LabelView = itemView.findViewById(R.id.invoice_line_1_label);
        invoiceLine1ValueView = itemView.findViewById(R.id.invoice_line_1_value);
        invoiceLine2LabelView = itemView.findViewById(R.id.invoice_line_2_label);
        invoiceLine2ValueView = itemView.findViewById(R.id.invoice_line_2_value);
    }

    public static InvoiceViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_invoice_item, parent, false);
        return new InvoiceViewHolder(view);
    }

    public void bindListLine(InvoiceDto invoiceDto) {
        invoiceLineHeaderView.setText(String.format("%s", invoiceDto.getInvoiceRecipient().getName()));
        invoiceLine1StatusView.setText(invoiceDto.getBillingPeriodStartDate().format(DateTimeFormatter.ofPattern("MMM", Locale.getDefault())));

        if (invoiceDto.getStatus().equals(Invoice.InvoiceStatusEnum.NEW.name())) {
            invoiceLine1StatusView.setTextColor(invoiceLine1StatusView.getResources().getColor(R.color.invoice_status_open, null));
        } else if (invoiceDto.getStatus().equals(Invoice.InvoiceStatusEnum.COMPLETED.name())) {
            invoiceLine1StatusView.setTextColor(invoiceLine1StatusView.getResources().getColor(R.color.invoice_status_completed, null));
        } else if (invoiceDto.getStatus().equals(Invoice.InvoiceStatusEnum.SENT.name())) {
            invoiceLine1StatusView.setTextColor(invoiceLine1StatusView.getResources().getColor(R.color.invoice_status_sent, null));
        }
        invoiceLine1LabelView.setText(R.string.lbl_billing_date);
        invoiceLine1ValueView.setText(String.format("%s", invoiceDto.getBillingDate()));
        invoiceLine2LabelView.setText(R.string.lbl_billed_amount);
        invoiceLine2ValueView.setText(String.format("%s", Utility.formatAmountToNOK(invoiceDto.getAmount() + invoiceDto.getVatAmount())));
    }
}
