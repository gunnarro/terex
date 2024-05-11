package com.gunnarro.android.terex.ui.view;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.gunnarro.android.terex.domain.entity.Project;
import com.gunnarro.android.terex.repository.ProjectRepository;
import com.gunnarro.android.terex.service.ProjectService;

import java.util.List;

/**
 * Repository is completely separated from the UI through the ViewModel.
 */
public class ProjectViewModel extends AndroidViewModel {

    private final ProjectService projectService;

    public ProjectViewModel(@NonNull Application application) {
        super(application);
        projectService = new ProjectService();
    }

    public LiveData<List<Project>> getProjectLiveData(Long clientId, ProjectRepository.ProjectStatusEnum status) {
        return projectService.getProjects(clientId, status);
    }

}
