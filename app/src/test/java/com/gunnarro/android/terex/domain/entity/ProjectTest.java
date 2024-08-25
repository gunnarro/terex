package com.gunnarro.android.terex.domain.entity;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

class ProjectTest {
    @Test
    void isClosed() {
        Project project = new Project();
        project.setId(233L);
        project.setStatus(Project.ProjectStatusEnum.CLOSED.name());
        assertTrue(project.isClosed());
        assertFalse(project.isActive());
        assertFalse(project.isNew());
    }

    @Test
    void isNew() {
        Project project = new Project();
        project.setId(null);
        project.setStatus(null);
        assertTrue(project.isNew());
        assertFalse(project.isClosed());
        assertFalse(project.isActive());
    }

    @Test
    void isActive() {
        Project project = new Project();
        project.setId(2333L);
        project.setStatus(Project.ProjectStatusEnum.ACTIVE.name());
        assertTrue(project.isActive());
        assertFalse(project.isClosed());
        assertFalse(project.isNew());
    }

    @Test
    void areEqual() {
        Project project1 = new Project();
        Project project2 = new Project();
        assertTrue(project1.equals(project2));

        project1.setClientId(33L);
        project1.setName("test-project");

        project2.setClientId(33L);
        project2.setName("test-project");

        assertTrue(project1.equals(project2));
    }

    @Test
    void areNotEqual() {
        Project project1 = new Project();
        Project project2 = new Project();

        project1.setClientId(33L);
        project1.setName("test-project");

        project2.setClientId(33L);
        project2.setName("test-project-1");

        assertFalse(project1.equals(project2));

        project2.setClientId(34L);
        project2.setName("test-project");

        assertFalse(project1.equals(project2));
    }
}
