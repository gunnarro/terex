package com.gunnarro.android.terex.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.gunnarro.android.terex.domain.dto.ProjectDto;
import com.gunnarro.android.terex.domain.entity.Project;
import com.gunnarro.android.terex.domain.mapper.TimesheetMapper;
import com.gunnarro.android.terex.repository.ProjectRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.ExecutionException;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    private ProjectService projectService;

    @Mock
    private ProjectRepository projectRepositoryMock;

    @BeforeEach
    public void setup() {
        projectService = new ProjectService(projectRepositoryMock);
    }


    @Test
    void saveProject_new() throws ExecutionException, InterruptedException {
        ProjectDto projectDto = new ProjectDto();
        projectDto.setClientId(1002L);
        projectDto.setName("timerex");
        projectDto.setStatus(Project.ProjectStatusEnum.ACTIVE.name());
        projectDto.setDescription("develop a timesheet android app");

        when(projectRepositoryMock.findProject(anyLong(), anyString())).thenReturn(null);
        when(projectRepositoryMock.insert(any())).thenReturn(1L);

        Long projectId = projectService.saveProject(projectDto);
        assertEquals(1L, projectId);
    }

    @Test
    void saveProject_update() throws ExecutionException, InterruptedException {
        ProjectDto projectDto = new ProjectDto();
        projectDto.setId(23L);
        projectDto.setClientId(1002L);
        projectDto.setName("timerex");
        projectDto.setStatus(Project.ProjectStatusEnum.ACTIVE.name());
        projectDto.setDescription("develop a timesheet android app");
        projectDto.setHourlyRate(1250);

        Project existingProject = TimesheetMapper.fromProjectDto(projectDto);
        when(projectRepositoryMock.findProject(anyLong(), anyString())).thenReturn(existingProject);
        when(projectRepositoryMock.update(any())).thenReturn(1);

        Long projectId = projectService.saveProject(projectDto);
        assertEquals(23L, projectId);
    }
}
