package com.gunnarro.android.terex.ui.adapter;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.gunnarro.android.terex.R;
import com.gunnarro.android.terex.domain.dto.ClientDto;
import com.gunnarro.android.terex.domain.entity.Client;
import com.gunnarro.android.terex.ui.fragment.ClientListFragment;
import com.gunnarro.android.terex.ui.listener.ListOnItemClickListener;
import com.gunnarro.android.terex.ui.view.ClientViewHolder;

public class ClientListAdapter extends ListAdapter<ClientDto, ClientViewHolder> implements AdapterView.OnItemClickListener {
    private final ListOnItemClickListener listOnItemClickListener;

    public ClientListAdapter(@NonNull ListOnItemClickListener listOnItemClickListener, @NonNull DiffUtil.ItemCallback<ClientDto> diffCallback) {
        super(diffCallback);
        this.setHasStableIds(true);
        this.listOnItemClickListener = listOnItemClickListener;
    }

    @NonNull
    @Override
    public ClientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ClientViewHolder viewHolder = ClientViewHolder.create(parent);
        viewHolder.itemView.findViewById(R.id.ic_client_row_view).setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putLong(ClientListFragment.CLIENT_ID_KEY, getItem(viewHolder.getBindingAdapterPosition()).getId());
            bundle.putString(ClientListFragment.CLIENT_ACTION_KEY, ClientListFragment.CLIENT_ACTION_EDIT);
            listOnItemClickListener.onItemClick(bundle);
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ClientViewHolder holder, int position) {
        holder.bindListLine(getItem(position));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        notifyItemRangeRemoved(position, 1);
    }

    public static class ClientDiff extends DiffUtil.ItemCallback<Client> {

        @Override
        public boolean areItemsTheSame(@NonNull Client oldItem, @NonNull Client newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Client oldItem, @NonNull Client newItem) {
            return oldItem.equals(newItem);
        }
    }

    public static class ClientDtoDiff extends DiffUtil.ItemCallback<ClientDto> {

        @Override
        public boolean areItemsTheSame(@NonNull ClientDto oldItem, @NonNull ClientDto newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull ClientDto oldItem, @NonNull ClientDto newItem) {
            return oldItem.equals(newItem);
        }
    }
}
