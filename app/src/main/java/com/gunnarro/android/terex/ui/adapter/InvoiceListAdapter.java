package com.gunnarro.android.terex.ui.adapter;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.gunnarro.android.terex.R;
import com.gunnarro.android.terex.domain.entity.Invoice;
import com.gunnarro.android.terex.ui.fragment.InvoiceListFragment;
import com.gunnarro.android.terex.ui.view.InvoiceViewHolder;

public class InvoiceListAdapter extends ListAdapter<Invoice, InvoiceViewHolder> implements AdapterView.OnItemClickListener {

    private final FragmentManager fragmentManager;

    public InvoiceListAdapter(@NonNull FragmentManager fragmentManager, @NonNull DiffUtil.ItemCallback<Invoice> diffCallback) {
        super(diffCallback);
        this.setHasStableIds(true);
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public InvoiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        InvoiceViewHolder viewHolder = InvoiceViewHolder.create(parent);
        viewHolder.itemView.findViewById(R.id.ic_invoice_row_view).setOnClickListener(v -> {
            Bundle actionBundle = new Bundle();
            actionBundle.putString(InvoiceListFragment.INVOICE_ACTION_VIEW, "true");
            actionBundle.putString(InvoiceListFragment.INVOICE_ID_KEY, getItem(viewHolder.getBindingAdapterPosition()).getId().toString());
            fragmentManager.setFragmentResult(InvoiceListFragment.INVOICE_REQUEST_KEY, actionBundle);
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(InvoiceViewHolder holder, int position) {
        holder.bindListLine(getItem(position));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        notifyItemRangeRemoved(position, 1);
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
