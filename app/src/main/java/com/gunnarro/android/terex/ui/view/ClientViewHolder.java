package com.gunnarro.android.terex.ui.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.gunnarro.android.terex.R;
import com.gunnarro.android.terex.domain.dto.ClientDto;

public class ClientViewHolder extends RecyclerView.ViewHolder {
    private final ImageView lineHeaderStatusImgView;
    private final TextView lineHeaderView;
    private final TextView line1ValueView;
    private final TextView line2ValueView;

    private ClientViewHolder(View itemView) {
        super(itemView);
        lineHeaderStatusImgView = itemView.findViewById(R.id.client_line_1_status);
        lineHeaderView = itemView.findViewById(R.id.client_line_header);
        line1ValueView = itemView.findViewById(R.id.client_line_1_value);
        line2ValueView = itemView.findViewById(R.id.client_line_2_value);
    }

    public static ClientViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_client_item, parent, false);
        return new ClientViewHolder(view);
    }

    public void bindListLine(ClientDto clientDto) {
        lineHeaderView.setText(String.format("%s", clientDto.getName()));
        if (clientDto.isActive()) {
            lineHeaderStatusImgView.setImageResource(R.drawable.ic_status_active_24);
        } else {
            lineHeaderStatusImgView.setImageResource(R.drawable.ic_status_closed_24);
        }

        int numberOfProjects = clientDto.getProjectList() != null ? clientDto.getProjectList().size() : 0;
        line1ValueView.setText(String.format("Number of projects: %s", numberOfProjects));
        if (clientDto.hasContactPersonDto()) {
            line2ValueView.setText(String.format("%s %s", clientDto.getContactPersonDto().getFullName(),
                    clientDto.getContactPersonDto().getContactInfo() != null ? clientDto.getContactPersonDto().getContactInfo().getMobileNumber() : ""));
        }
    }
}
