package com.gunnarro.android.terex.ui.view;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.gunnarro.android.terex.domain.dto.ProjectDto;
import com.gunnarro.android.terex.service.ProjectService;

import java.util.List;

/**
 * Repository is completely separated from the UI through the ViewModel.
 */
public class ProjectViewModel extends AndroidViewModel {

    private final ProjectService projectService;

    private final MutableLiveData<List<ProjectDto>> projectListLiveData;

    public ProjectViewModel(@NonNull Application application, Long clientId) {
        super(application);
        projectService = new ProjectService();
        projectListLiveData = new MutableLiveData<>();
        projectListLiveData.setValue(projectService.getProjects(clientId, null));
    }

    public LiveData<List<ProjectDto>> getProjectsLiveData(Long clientId) {
        projectListLiveData.setValue(projectService.getProjects(clientId, null));
        return projectListLiveData;
    }

    public void saveProject(ProjectDto projectDto) {
        projectService.saveProject(projectDto);
    }

    public void deleteProject(Long projectId) {
        projectService.deleteProject(projectId);
    }
}
