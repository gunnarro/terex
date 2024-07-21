package com.gunnarro.android.terex.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.gunnarro.android.terex.IntegrationTestSetup;
import com.gunnarro.android.terex.domain.dto.ClientDto;
import com.gunnarro.android.terex.domain.dto.ProjectDto;
import com.gunnarro.android.terex.domain.entity.Project;
import com.gunnarro.android.terex.repository.ProjectRepository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class ProjectServiceTest extends IntegrationTestSetup {

    private ProjectService projectService;

    @Before
    public void setup() {
        super.setupDatabase();
        projectService = new ProjectService(new ProjectRepository());
    }

    @After
    public void cleanUp() {
        super.cleanUpDatabase();
    }

    @Test
    public void getHourlyRate() {
        assertNull(projectService.getProjectHourlyRate(23L));
    }

    @Test
    public void saveProject() {
        assertNull(projectService.getProject(1L));

        ProjectDto newProjectDto = new ProjectDto();
        newProjectDto.setClientDto(new ClientDto(200L));
        newProjectDto.setName("gunnarro timesheet project");
        newProjectDto.setId(null);// for new projects id is not set
        newProjectDto.setDescription("develop a timesheet app");
        newProjectDto.setStatus(Project.ProjectStatusEnum.ACTIVE.name());
        newProjectDto.setHourlyRate(1250);

        Long projectId = projectService.saveProject(newProjectDto);
        assertTrue(projectId.intValue() > 0);

        ProjectDto projectDto = projectService.getProject(projectId);
        assertEquals(projectId, projectDto.getId());
        assertEquals("gunnarro timesheet project", projectDto.getName());
        assertEquals("develop a timesheet app", projectDto.getDescription());
        assertEquals(Project.ProjectStatusEnum.ACTIVE.name(), projectDto.getStatus());

        // update project hourly rate
        projectDto.setHourlyRate(1055);
        projectService.saveProject(projectDto);
        List<ProjectDto> projectDtoList = projectService.getProjects(projectDto.getClientDto().getId(), ProjectRepository.ProjectStatusEnum.ACTIVE);
        assertEquals(1, projectDtoList.size());
        assertEquals(projectId, projectDtoList.get(0).getId());
        assertEquals(projectDto.getClientDto().getId(), projectDtoList.get(0).getClientDto().getId());
        assertEquals(1055, projectDtoList.get(0).getHourlyRate().intValue());
    }

    @Test
    public void getProjects() {
        assertEquals(0, projectService.getProjects(23L, null).size());
        assertEquals(0, projectService.getProjects(23L, ProjectRepository.ProjectStatusEnum.ACTIVE).size());
    }
}
