package com.gunnarro.android.terex.ui.view;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.gunnarro.android.terex.R;
import com.gunnarro.android.terex.domain.entity.Invoice;
import com.gunnarro.android.terex.repository.InvoiceRepository;

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


    public void bindListLine(Invoice invoice) {
        invoiceLineHeaderView.setText(String.format("%s", invoice.getReference()));
        invoiceLine1StatusView.setText(invoice.getBillingDate().format(DateTimeFormatter.ofPattern("MMM", Locale.getDefault())));

        if (invoice.getStatus().equals(InvoiceRepository.InvoiceStatusEnum.OPEN.name())) {
            invoiceLine1StatusView.setTextColor(Color.parseColor("#0100f6"));
        } else if (invoice.getStatus().equals(InvoiceRepository.InvoiceStatusEnum.SENT.name())) {
            invoiceLine1StatusView.setTextColor(Color.parseColor("#54aa00"));
        } else {
            invoiceLine1StatusView.setTextColor(Color.parseColor("#f5f600"));
        }
        invoiceLine1LabelView.setText(R.string.lbl_billing_date);
        invoiceLine1ValueView.setText(String.format("%s", invoice.getBillingDate()));
        invoiceLine2LabelView.setText(R.string.lbl_billed_amount);
        invoiceLine2ValueView.setText(String.format("%s", invoice.getAmount()));
    }
}
