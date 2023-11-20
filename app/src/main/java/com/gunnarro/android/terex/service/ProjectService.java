package com.gunnarro.android.terex.service;

import android.content.Context;

import androidx.room.Transaction;

import com.gunnarro.android.terex.domain.entity.Project;
import com.gunnarro.android.terex.repository.ProjectRepository;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ProjectService {

    private final ProjectRepository projectRepository;

    /**
     * default constructor
     */

    @Inject
    public ProjectService(Context applicationContext) {
        projectRepository = new ProjectRepository(applicationContext);
    }

    @Transaction
    public Long createProject(String companyName, String projectName, String description) {
        Project project = new Project();
        project.setCompanyName(companyName);
        project.setName(projectName);
        project.setDescription(description);
        return projectRepository.saveProject(project);
    }


    public List<Project> getProjects(String status) {
        return projectRepository.getProjects(status);
    }

}
