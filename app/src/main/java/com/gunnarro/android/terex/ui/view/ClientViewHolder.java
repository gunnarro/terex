package com.gunnarro.android.terex.ui.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.gunnarro.android.terex.R;
import com.gunnarro.android.terex.domain.entity.Client;

public class ClientViewHolder extends RecyclerView.ViewHolder {
    private final TextView lineHeaderView;
    private final TextView line1LabelView;
    private final TextView line1ValueView;

    private ClientViewHolder(View itemView) {
        super(itemView);
        lineHeaderView = itemView.findViewById(R.id.client_line_header);
        line1LabelView = itemView.findViewById(R.id.client_line_1_label);
        line1ValueView = itemView.findViewById(R.id.client_line_1_value);
    }

    public static ClientViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_client_item, parent, false);
        return new ClientViewHolder(view);
    }

    public void bindListLine(Client client) {
        lineHeaderView.setText(String.format("%s %s", client.getName(), client.getStatus()));
     /*   line1StatusView.setText(client.getStatus());
        if (client.getStatus().equals("ACTIVE")) {
            line1StatusView.setTextColor(line1StatusView.getResources().getColor(R.color.invoice_status_open, null));
        } else
            line1StatusView.setTextColor(line1StatusView.getResources().getColor(R.color.invoice_status_completed, null));
    }*/
        line1LabelView.setText("");
        line1ValueView.setText("");
    }
}
