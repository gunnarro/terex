package com.gunnarro.android.terex.ui.adapter;

import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.gunnarro.android.terex.domain.entity.Invoice;
import com.gunnarro.android.terex.domain.entity.Timesheet;
import com.gunnarro.android.terex.ui.view.InvoiceViewHolder;
import com.gunnarro.android.terex.ui.view.TimesheetViewHolder;
import com.gunnarro.android.terex.utility.Utility;

public class InvoiceListAdapter extends ListAdapter<Invoice, InvoiceViewHolder>{

    public InvoiceListAdapter(@NonNull DiffUtil.ItemCallback<Invoice> diffCallback) {
        super(diffCallback);
        Log.d("TimesheetListAdapter","init");
    }

    @NonNull
    @Override
    public InvoiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return InvoiceViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(InvoiceViewHolder holder, int position) {
        Invoice current = getItem(position);
        holder.bindListItemHeader(current.getInvoiceDate().toString() + " - " + current.getDueDate());
        holder.bindListItemBody(current.getClientId() + ", " + current.getInvoiceId() + "," + current.getAmountDue());
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
