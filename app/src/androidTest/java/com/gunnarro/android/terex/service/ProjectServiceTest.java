package com.gunnarro.android.terex.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import com.gunnarro.android.terex.DbHelper;
import com.gunnarro.android.terex.config.AppDatabase;
import com.gunnarro.android.terex.domain.dto.ProjectDto;
import com.gunnarro.android.terex.domain.entity.Project;
import com.gunnarro.android.terex.repository.ProjectRepository;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class ProjectServiceTest {

    private ProjectService projectService;

    @Before
    public void setup() {
        Context appContext = ApplicationProvider.getApplicationContext();
        AppDatabase appDatabase = Room.inMemoryDatabaseBuilder(appContext, AppDatabase.class).build();
        AppDatabase.init(appContext);
        projectService = new ProjectService(new ProjectRepository());
        // load test data
        List<String> sqlQueryList = DbHelper.readMigrationSqlQueryFile(appContext,"database/test_data.sql");
        sqlQueryList.forEach( query -> {
            System.out.println(query);
            appDatabase.getOpenHelper().getWritableDatabase().execSQL(query);
        });
        assertTrue(appDatabase.getOpenHelper().getWritableDatabase().isDatabaseIntegrityOk());
    }

    @Test
    public void saveProject() {
        assertNull(projectService.getProject(1L));
        assertNull(projectService.getProjectWithTimesheet(1L));

        ProjectDto newProjectDto = new ProjectDto();
        newProjectDto.setName("gunnarro timesheet project");
        newProjectDto.setId(null);// for new projects id is not set
        newProjectDto.setDescription("develop a timesheet app");
        newProjectDto.setStatus(Project.ProjectStatusEnum.ACTIVE.name());

        Long projectId = projectService.saveProject(newProjectDto);
        assertEquals(1, projectId.intValue());

        ProjectDto projectDto = projectService.getProjectWithTimesheet(projectId);
        assertEquals(1, projectDto.getId().intValue());
        assertEquals("gunnarro timesheet project", projectDto.getName());
        assertEquals("develop a timesheet app", projectDto.getDescription());
        assertEquals(Project.ProjectStatusEnum.ACTIVE.name(), projectDto.getStatus());
        assertEquals(0, projectDto.getTimesheetDto().size());

        projectDto = projectService.getProject(projectId);
        assertEquals(1, projectDto.getId().intValue());
        assertEquals("gunnarro timesheet project", projectDto.getName());
        assertEquals("develop a timesheet app", projectDto.getDescription());
        assertEquals(Project.ProjectStatusEnum.ACTIVE.name(), projectDto.getStatus());
        assertNull(projectDto.getTimesheetDto());
    }
}
