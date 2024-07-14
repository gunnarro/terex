package com.gunnarro.android.terex.service;


import android.util.Log;

import androidx.room.Transaction;

import com.gunnarro.android.terex.domain.dto.ProjectDto;
import com.gunnarro.android.terex.domain.entity.Project;
import com.gunnarro.android.terex.domain.entity.ProjectWithTimesheet;
import com.gunnarro.android.terex.domain.mapper.TimesheetMapper;
import com.gunnarro.android.terex.exception.InputValidationException;
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
        this(new ProjectRepository());
    }

    public ProjectDto getProject(Long projectId) {
        ProjectDto projectDto = TimesheetMapper.toProjectDto(projectRepository.getProject(projectId));
        if (projectDto != null) {
            projectDto.getClientDto().setName(projectRepository.getClientName(projectDto.getId()));
            return projectDto;
        }
        return null;
    }

    public Integer getProjectHourlyRate(Long timesheetId) {
        return projectRepository.getProjectHourlyRateByTimesheetId(timesheetId);
    }
/*
    public ProjectDto getProjectWithTimesheet(Long projectId) {
        ProjectWithTimesheet project = projectRepository.getProjectWithTimesheet(projectId);
        if (project != null) {
            return TimesheetMapper.toProjectDto(project.getProject(), project.getTimesheetList());
        } else {
            return null;
        }
    }
*/
    public List<ProjectDto> getProjects(Long clientId) {
        return TimesheetMapper.toProjectDtoList(projectRepository.getProjects(clientId));
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public Long saveProject(@NotNull final ProjectDto projectDto) {
        Project project = TimesheetMapper.fromProjectDto(projectDto);
        try {
            Project projectExisting = projectRepository.find(project.getClientId(), project.getName());
            Log.d("ProjectRepository.saveProject", String.format("projectExisting: %s", projectExisting));
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

    @Transaction
    public void deleteProject(Long projectId) {
        Project project = projectRepository.getProject(projectId);
        if (project == null) {
            throw new InputValidationException(String.format("Project not found! timesheetEntryId=%s", projectId), "40044", null);
        }
        if (project.isActive()) {
            throw new InputValidationException("Project is active, not allowed to be delete.", "40045", null);
        }
        projectRepository.delete(project);
    }
}
