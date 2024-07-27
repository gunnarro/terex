package com.gunnarro.android.terex.ui.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.gunnarro.android.terex.R;
import com.gunnarro.android.terex.domain.dto.ProjectDto;

public class ProjectViewHolder extends RecyclerView.ViewHolder {
    private final TextView lineHeaderView;
    private final TextView line1LabelView;
    private final TextView line1ValueView;

    private ProjectViewHolder(View itemView) {
        super(itemView);
        lineHeaderView = itemView.findViewById(R.id.project_line_header);
        line1LabelView = itemView.findViewById(R.id.project_line_1_label);
        line1ValueView = itemView.findViewById(R.id.project_line_1_value);
    }

    public static ProjectViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_project_item, parent, false);
        return new ProjectViewHolder(view);
    }

    public void bindListLine(ProjectDto projectDto) {
        lineHeaderView.setText(String.format("%s %s", projectDto.getName(), projectDto.getStatus()));
        if (projectDto.isActive()) {
            lineHeaderView.setEnabled(false);
            line1LabelView.setEnabled(false);
            line1ValueView.setEnabled(false);
        }
        line1LabelView.setText("Hourly rate");
        line1ValueView.setText(String.format("%s", projectDto.getHourlyRate().toString()));
    }
}
