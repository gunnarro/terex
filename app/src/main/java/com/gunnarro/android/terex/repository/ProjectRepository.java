package com.gunnarro.android.terex.repository;

import android.content.Context;
import android.util.Log;

import androidx.room.Transaction;

import com.gunnarro.android.terex.config.AppDatabase;
import com.gunnarro.android.terex.domain.entity.Project;
import com.gunnarro.android.terex.domain.entity.ProjectWithTimesheet;
import com.gunnarro.android.terex.exception.TerexApplicationException;

import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;

import javax.inject.Singleton;

@Singleton
public class ProjectRepository {

    /**
     * OPEN: not added any billing information or assigned to a timesheet
     * CREATED: fulfilled and assigned to a timesheet. Possible to edit and delete.
     * SENT: sent to customer, tha invoice is then closed and assigned timesheet is set to status CLOSED. Not possible to edit or delete. Can only be viewed.
     * CANCELLED: the invoice have been cancelled.
     */
    public enum ProjectStatusEnum {
        ACTIVE, FINISHED;
    }

    private final ProjectDao projectDao;

    // Note that in order to unit test the WordRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    public ProjectRepository() {
        projectDao = AppDatabase.getDatabase().projectDao();
    }


    @Transaction
    public ProjectWithTimesheet getProjectWithTimesheet(Long projectId) {
        try {
            CompletionService<ProjectWithTimesheet> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
            service.submit(() -> projectDao.getProjectWithTimesheet(projectId));
            Future<ProjectWithTimesheet> future = service.take();
            return future != null ? future.get() : null;
        } catch (InterruptedException | ExecutionException e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("Error getting project with timesheet list", e.getMessage(), e.getCause());
        }
    }

    public List<Project> getProjects(Long consultantCompanyId, String status) {
        try {
            CompletionService<List<Project>> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
            service.submit(() -> projectDao.getProjects(consultantCompanyId, status));
            Future<List<Project>> future = service.take();
            return future != null ? future.get() : null;
        } catch (InterruptedException | ExecutionException e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("error getting projects!", "50050", e);
        }
    }


    public Project getProject(Long projectId) {
        try {
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

    public Project findProject(Long consultantCompanyId, String projectName) {
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
    public Long insertProject(Project project) throws InterruptedException, ExecutionException {
        Log.d("insertProject", "project: " + project);
        CompletionService<Long> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
        service.submit(() -> projectDao.insert(project));
        Future<Long> future = service.take();
        return future != null ? future.get() : null;
    }

    public Integer updateProject(Project project) throws InterruptedException, ExecutionException {
        Log.d("updateProject", "project: " + project);
        CompletionService<Integer> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
        service.submit(() -> projectDao.update(project));
        Future<Integer> future = service.take();
        return future != null ? future.get() : null;
    }
}
