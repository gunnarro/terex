package com.gunnarro.android.terex.ui.adapter;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.gunnarro.android.terex.R;
import com.gunnarro.android.terex.domain.dto.ProjectDto;
import com.gunnarro.android.terex.ui.fragment.ProjectListFragment;
import com.gunnarro.android.terex.ui.listener.ListOnItemClickListener;
import com.gunnarro.android.terex.ui.view.ProjectViewHolder;

public class ProjectListAdapter extends ListAdapter<ProjectDto, ProjectViewHolder> implements AdapterView.OnItemClickListener {
    private final ListOnItemClickListener listOnItemClickListener;

    public ProjectListAdapter(@NonNull ListOnItemClickListener listOnItemClickListener, @NonNull DiffUtil.ItemCallback<ProjectDto> diffCallback) {
        super(diffCallback);
        this.setHasStableIds(true);
        this.listOnItemClickListener = listOnItemClickListener;
    }

    @NonNull
    @Override
    public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ProjectViewHolder viewHolder = ProjectViewHolder.create(parent);
        viewHolder.itemView.findViewById(R.id.ic_project_row_view).setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putLong(ProjectListFragment.PROJECT_ID_KEY, getItem(viewHolder.getBindingAdapterPosition()).getId());
            bundle.putString(ProjectListFragment.PROJECT_ACTION_KEY, ProjectListFragment.PROJECT_ACTION_EDIT);
            listOnItemClickListener.onItemClick(bundle);
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ProjectViewHolder holder, int position) {
        holder.bindListLine(getItem(position));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        notifyItemRangeRemoved(position, 1);
    }

    public static class ProjectDtoDiff extends DiffUtil.ItemCallback<ProjectDto> {

        @Override
        public boolean areItemsTheSame(@NonNull ProjectDto oldItem, @NonNull ProjectDto newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull ProjectDto oldItem, @NonNull ProjectDto newItem) {
            return oldItem.equals(newItem);
        }
    }
}
