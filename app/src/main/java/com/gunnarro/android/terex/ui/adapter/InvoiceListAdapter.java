package com.gunnarro.android.terex.ui.adapter;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.gunnarro.android.terex.R;
import com.gunnarro.android.terex.domain.dto.InvoiceDto;
import com.gunnarro.android.terex.domain.entity.Invoice;
import com.gunnarro.android.terex.ui.fragment.InvoiceListFragment;
import com.gunnarro.android.terex.ui.listener.ListOnItemClickListener;
import com.gunnarro.android.terex.ui.view.InvoiceViewHolder;

public class InvoiceListAdapter extends ListAdapter<InvoiceDto, InvoiceViewHolder> implements AdapterView.OnItemClickListener {
    private final ListOnItemClickListener listOnItemClickListener;

    public InvoiceListAdapter(@NonNull ListOnItemClickListener listOnItemClickListener, @NonNull DiffUtil.ItemCallback<InvoiceDto> diffCallback) {
        super(diffCallback);
        this.setHasStableIds(true);
        this.listOnItemClickListener = listOnItemClickListener;
    }

    @NonNull
    @Override
    public InvoiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        InvoiceViewHolder viewHolder = InvoiceViewHolder.create(parent);
        viewHolder.itemView.findViewById(R.id.ic_invoice_row_view).setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString(InvoiceListFragment.INVOICE_ACTION_VIEW, "true");
            bundle.putLong(InvoiceListFragment.INVOICE_ID_KEY, getItem(viewHolder.getBindingAdapterPosition()).getId());
            listOnItemClickListener.onItemClick(bundle);
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
