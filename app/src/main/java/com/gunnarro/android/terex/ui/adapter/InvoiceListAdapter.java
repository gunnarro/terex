package com.gunnarro.android.terex.ui.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.gunnarro.android.terex.domain.dto.InvoiceDto;
import com.gunnarro.android.terex.ui.view.InvoiceViewHolder;

public class InvoiceListAdapter extends ListAdapter<InvoiceDto, InvoiceViewHolder> {

    public InvoiceListAdapter(@NonNull DiffUtil.ItemCallback<InvoiceDto> diffCallback) {
        super(diffCallback);
        this.setHasStableIds(true);
    }

    @NonNull
    @Override
    public InvoiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return InvoiceViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(InvoiceViewHolder holder, int position) {
        holder.bindListLine(getItem(position));
    }

    public static class InvoiceDtoDiff extends DiffUtil.ItemCallback<InvoiceDto> {

        @Override
        public boolean areItemsTheSame(@NonNull InvoiceDto oldItem, @NonNull InvoiceDto newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull InvoiceDto oldItem, @NonNull InvoiceDto newItem) {
            return oldItem.equals(newItem);
        }
    }
}
