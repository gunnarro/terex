package com.gunnarro.android.terex.repository;

import android.util.Log;

import com.gunnarro.android.terex.config.AppDatabase;
import com.gunnarro.android.terex.domain.entity.Project;
import com.gunnarro.android.terex.exception.TerexApplicationException;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import javax.inject.Singleton;

@Singleton
public class ProjectRepository {

    private final ProjectDao projectDao;

    public ProjectRepository() {
        projectDao = AppDatabase.getDatabase().projectDao();
    }

    public List<Project> getProjects(Long clientId, Project.ProjectStatusEnum projectStatus) {
        List<String> projectStatuses;
        if (projectStatus != null) {
            projectStatuses = List.of(projectStatus.name());
        } else {
            projectStatuses = Arrays.stream(Project.ProjectStatusEnum.values()).map(Enum::name).collect(Collectors.toList());
        }
        try {
            CompletionService<List<Project>> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
            service.submit(() -> projectDao.getProjects(clientId, projectStatuses));
            Future<List<Project>> future = service.take();
            return future != null ? future.get() : null;
        } catch (InterruptedException | ExecutionException e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("error getting projects!", "50050", e);
        }
    }

    public Integer getProjectHourlyRateByTimesheetId(Long timesheetId) {
        try {
            CompletionService<Integer> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
            service.submit(() -> projectDao.getProjectHourlyRate(timesheetId));
            Future<Integer> future = service.take();
            return future != null ? future.get() : null;
        } catch (InterruptedException | ExecutionException e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("error getting project!", "50050", e);
        }
    }

    public String getClientName(Long projectId) {
        try {
            CompletionService<String> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
            service.submit(() -> projectDao.getClientName(projectId));
            Future<String> future = service.take();
            return future != null ? future.get() : null;
        } catch (InterruptedException | ExecutionException e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("error getting project!", "50050", e);
        }
    }

    public Project getProject(Long projectId) {
        try {
            Log.d("getProject, projectId", projectId.toString());
            CompletionService<Project> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
            service.submit(() -> projectDao.getProject(projectId));
            Future<Project> future = service.take();
            return future != null ? future.get() : null;
        } catch (InterruptedException | ExecutionException e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("error getting project!", "50050", e);
        }
    }

    public Project find(Long consultantCompanyId, String projectName) {
        try {
            CompletionService<Project> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
            service.submit(() -> projectDao.findProject(consultantCompanyId, projectName));
            Future<Project> future = service.take();
            return future != null ? future.get() : null;
        } catch (InterruptedException | ExecutionException e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("error find project!", "50050", e);
        }
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public Long insert(Project project) throws InterruptedException, ExecutionException {
        Log.d("insertProject", project.toString());
        CompletionService<Long> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
        service.submit(() -> projectDao.insert(project));
        Future<Long> future = service.take();
        return future != null ? future.get() : null;
    }

    public Integer update(Project project) throws InterruptedException, ExecutionException {
        Log.d("updateProject", project.toString());
        CompletionService<Integer> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
        service.submit(() -> projectDao.update(project));
        Future<Integer> future = service.take();
        return future != null ? future.get() : null;
    }

    public void delete(Project project) {
        AppDatabase.databaseExecutor.execute(() -> {
            projectDao.delete(project);
            Log.d("ProjectRepository.delete", "deleted, projectId=" + project.getId());
        });
    }
}
