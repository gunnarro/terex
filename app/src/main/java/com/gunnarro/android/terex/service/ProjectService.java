package com.gunnarro.android.terex.service;


import android.util.Log;

import androidx.lifecycle.LiveData;

import com.gunnarro.android.terex.domain.dto.ProjectDto;
import com.gunnarro.android.terex.domain.entity.Project;
import com.gunnarro.android.terex.domain.entity.ProjectWithTimesheet;
import com.gunnarro.android.terex.domain.mapper.TimesheetMapper;
import com.gunnarro.android.terex.exception.TerexApplicationException;
import com.gunnarro.android.terex.repository.ProjectRepository;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ProjectService {

    private final ProjectRepository projectRepository;

    /**
     * For unit test onlu
     */
    @Inject
    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Inject
    public ProjectService() {
        this.projectRepository = new ProjectRepository();
    }

    public ProjectDto getProject(Long projectId) {
        return TimesheetMapper.toProjectDto(projectRepository.getProject(projectId));
    }

    public ProjectDto getProjectWithTimesheet(Long projectId) {
        ProjectWithTimesheet project = projectRepository.getProjectWithTimesheet(projectId);
        if (project != null) {
            return TimesheetMapper.toProjectDto(project.getProject(), project.getTimesheetList());
        } else {
            return null;
        }
    }

    public LiveData<List<Project>> getProjectsLiveData(Long clientId, ProjectRepository.ProjectStatusEnum status) {
        return projectRepository.getProjectsLiveData(clientId, status.name());
    }

    public List<ProjectDto> getProjects(Long clientId, ProjectRepository.ProjectStatusEnum status) {
        return TimesheetMapper.toProjectDtoList(projectRepository.getProjects(clientId, status.name()));
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public Long saveProject(@NotNull final ProjectDto projectDto) {
        Project project = TimesheetMapper.fromProjectDto(projectDto);
        try {
            Project projectExisting = projectRepository.findProject(project.getClientId(), project.getName());
            Log.d("ProjectRepository.saveProject", String.format("%s", projectExisting));
            Long id;
            if (projectExisting == null) {
                project.setCreatedDate(LocalDateTime.now());
                project.setLastModifiedDate(LocalDateTime.now());
                id = projectRepository.insert(project);
                Log.d("", "inserted new project: " + id + " - " + project.getName());
            } else {
                project.setId(projectExisting.getId());
                project.setCreatedDate(projectExisting.getCreatedDate());
                project.setLastModifiedDate(LocalDateTime.now());
                projectRepository.update(project);
                id = project.getId();
                Log.d("", "updated project: " + id + " - " + project.getName());
            }
            return id;
        } catch (Exception e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("Error saving project! " + e.getMessage(), "50050", e.getCause());
        }
    }
}
