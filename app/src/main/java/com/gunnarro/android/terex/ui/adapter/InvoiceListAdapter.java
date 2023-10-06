package com.gunnarro.android.terex.ui.adapter;

import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.gunnarro.android.terex.domain.entity.Invoice;
import com.gunnarro.android.terex.ui.view.InvoiceViewHolder;

public class InvoiceListAdapter extends ListAdapter<Invoice, InvoiceViewHolder>{

    public InvoiceListAdapter(@NonNull DiffUtil.ItemCallback<Invoice> diffCallback) {
        super(diffCallback);
        Log.d("InvoiceListAdapter","init");
    }

    @NonNull
    @Override
    public InvoiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return InvoiceViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(InvoiceViewHolder holder, int position) {
        Invoice current = getItem(position);
        holder.bindListItemHeader(current.getBillingDate().toString() + " - " + current.getDueDate());
        holder.bindListItemBody(current.getClientId() + ", " + current.getInvoiceId() + "," + current.getAmount());
    }

    public static class InvoiceDiff extends DiffUtil.ItemCallback<Invoice> {

        @Override
        public boolean areItemsTheSame(@NonNull Invoice oldItem, @NonNull Invoice newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Invoice oldItem, @NonNull Invoice newItem) {
            return oldItem.equals(newItem);
        }
    }
}
