package com.gunnarro.android.terex.ui.adapter;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.gunnarro.android.terex.R;
import com.gunnarro.android.terex.domain.entity.Integration;
import com.gunnarro.android.terex.ui.fragment.IntegrationListFragment;
import com.gunnarro.android.terex.ui.listener.ListOnItemClickListener;
import com.gunnarro.android.terex.ui.view.IntegrationViewHolder;

public class IntegrationListAdapter extends ListAdapter<Integration, IntegrationViewHolder> implements AdapterView.OnItemClickListener {
    private final ListOnItemClickListener listOnItemClickListener;

    public IntegrationListAdapter(@NonNull ListOnItemClickListener listOnItemClickListener, @NonNull DiffUtil.ItemCallback<Integration> diffCallback) {
        super(diffCallback);
        this.setHasStableIds(true);
        this.listOnItemClickListener = listOnItemClickListener;
    }

    @NonNull
    @Override
    public IntegrationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        IntegrationViewHolder viewHolder = IntegrationViewHolder.create(parent);
        viewHolder.itemView.findViewById(R.id.ic_integration_row_view).setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putLong(IntegrationListFragment.INTEGRATION_ID_KEY, getItem(viewHolder.getBindingAdapterPosition()).getId());
            bundle.putString(IntegrationListFragment.INTEGRATION_ACTION_KEY, IntegrationListFragment.INTEGRATION_ACTION_EDIT);
            listOnItemClickListener.onItemClick(bundle);
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(IntegrationViewHolder holder, int position) {
        holder.bindListLine(getItem(position));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        notifyItemRangeRemoved(position, 1);
    }

    public static class IntegrationDiff extends DiffUtil.ItemCallback<Integration> {
        @Override
        public boolean areItemsTheSame(@NonNull Integration oldItem, @NonNull Integration newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Integration oldItem, @NonNull Integration newItem) {
            return oldItem.equals(newItem);
        }
    }
}
