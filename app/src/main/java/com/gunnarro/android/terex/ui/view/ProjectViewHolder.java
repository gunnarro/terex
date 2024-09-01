package com.gunnarro.android.terex.ui.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.gunnarro.android.terex.R;
import com.gunnarro.android.terex.domain.dto.ProjectDto;
import com.gunnarro.android.terex.utility.Utility;

import java.util.Locale;

public class ProjectViewHolder extends RecyclerView.ViewHolder {
    private final ImageView lineHeaderStatusImgView;
    private final TextView lineHeaderView;
    private final TextView line1LabelView;
    private final TextView line1ValueView;
    private final TextView line2LabelView;
    private final TextView line2ValueView;

    private ProjectViewHolder(View itemView) {
        super(itemView);
        lineHeaderStatusImgView = itemView.findViewById(R.id.project_line_1_status);
        lineHeaderView = itemView.findViewById(R.id.project_line_header);
        line1LabelView = itemView.findViewById(R.id.project_line_1_label);
        line1ValueView = itemView.findViewById(R.id.project_line_1_value);
        line2LabelView = itemView.findViewById(R.id.project_line_2_label);
        line2ValueView = itemView.findViewById(R.id.project_line_2_value);
    }

    public static ProjectViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_project_item, parent, false);
        return new ProjectViewHolder(view);
    }

    public void bindListLine(ProjectDto projectDto) {
        lineHeaderView.setText(projectDto.getName());
        if (projectDto.isActive()) {
            lineHeaderView.setEnabled(false);
            line1LabelView.setEnabled(false);
            line1ValueView.setEnabled(false);
        }
        if (projectDto.isActive()) {
            lineHeaderStatusImgView.setImageResource(R.drawable.ic_status_active_24);
        } else if (projectDto.isClosed()) {
            lineHeaderStatusImgView.setImageResource(R.drawable.ic_status_closed_24);
        }
        line1LabelView.setText("Hourly rate");
        line1ValueView.setText(String.format("%s", String.format(Locale.getDefault(), "%.2f Kr", Double.valueOf(projectDto.getHourlyRate().toString()))));
        if (projectDto.getStartDate() != null && projectDto.getEndDate() != null) {
            line2LabelView.setText("Periode");
            line2ValueView.setText(String.format("%s til %s", Utility.formatDate(projectDto.getStartDate()), Utility.formatDate(projectDto.getEndDate())));
        }
    }
}
