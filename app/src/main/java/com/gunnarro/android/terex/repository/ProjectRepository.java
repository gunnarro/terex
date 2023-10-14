package com.gunnarro.android.terex.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.gunnarro.android.terex.config.AppDatabase;
import com.gunnarro.android.terex.domain.entity.Project;
import com.gunnarro.android.terex.exception.TerexApplicationException;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
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
    private final LiveData<List<Project>> allProjects;

    // Note that in order to unit test the WordRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    public ProjectRepository(Context applicationContext) {
        projectDao = AppDatabase.getDatabase(applicationContext).projectDao();
        allProjects = projectDao.getAllProjects();
    }


    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    public LiveData<List<Project>> getAllProjects() {
        return allProjects;
    }

    public List<Project> getProjects(String status) {
        try {
            CompletionService<List<Project>> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
            service.submit(() -> projectDao.getProjects(status));
            Future<List<Project>> future = service.take();
            return future != null ? future.get() : null;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }


    public Project getProject(Long projectId) {
        try {
            CompletionService<Project> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
            service.submit(() -> projectDao.getProject(projectId));
            Future<Project> future = service.take();
            return future != null ? future.get() : null;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Project findProject(String companyName, String projectName) {
        try {
            CompletionService<Project> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
            service.submit(() -> projectDao.findProject(companyName, projectName));
            Future<Project> future = service.take();
            return future != null ? future.get() : null;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }


    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public Long saveProject(@NotNull final Project project) {
        try {
            Project projectExisting = findProject(project.getCompanyName(), project.getName());
            Log.d("ProjectRepository.saveProject", String.format("%s", projectExisting));
            Long id = null;
            if (projectExisting == null) {
                project.setCreatedDate(LocalDateTime.now());
                project.setLastModifiedDate(LocalDateTime.now());
                id = insertProject(project);
                Log.d("", "inserted new project: " + id + " - " + project.getName());
            } else {
                project.setId(projectExisting.getId());
                project.setCreatedDate(projectExisting.getCreatedDate());
                project.setLastModifiedDate(LocalDateTime.now());
                updateProject(project);
                id = project.getId();
                Log.d("", "updated project: " + id + " - " + project.getName());
            }
            return id;
        } catch (Exception e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            e.printStackTrace();
            throw new TerexApplicationException("Error saving invoice!", e.getMessage(), e.getCause());
        }
    }


    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    private Long insertProject(Project project) throws InterruptedException, ExecutionException {
        Log.d("insertProject", "Project: " + project);
        CompletionService<Long> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
        service.submit(() -> projectDao.insert(project));
        Future<Long> future = service.take();
        return future != null ? future.get() : null;
    }

    private Integer updateProject(Project project) throws InterruptedException, ExecutionException {
        Log.d("updateInvoice", "invoice: " + project);
        CompletionService<Integer> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
        service.submit(() -> projectDao.update(project));
        Future<Integer> future = service.take();
        return future != null ? future.get() : null;
    }
}
