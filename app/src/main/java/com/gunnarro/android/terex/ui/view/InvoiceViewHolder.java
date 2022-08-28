package com.gunnarro.android.terex.ui.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.gunnarro.android.terex.R;

public class InvoiceViewHolder extends RecyclerView.ViewHolder {
    private final TextView invoiceItemHeaderView;
    private final TextView invoiceItemBodyView;

    private InvoiceViewHolder(View itemView) {
        super(itemView);
        invoiceItemHeaderView = itemView.findViewById(R.id.invoice_item_header);
        invoiceItemBodyView = itemView.findViewById(R.id.invoice_item_body);
    }

    public void bindListItemHeader(String text) {
        invoiceItemHeaderView.setText(text);
    }

    public void bindListItemBody(String text) {
        invoiceItemBodyView.setText(text);
    }

    public static InvoiceViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_invoice_item, parent, false);
        return new InvoiceViewHolder(view);
    }
}
