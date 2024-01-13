package com.gunnarro.android.terex.repository;

import android.util.Log;

import com.gunnarro.android.terex.config.AppDatabase;
import com.gunnarro.android.terex.domain.entity.Organization;
import com.gunnarro.android.terex.exception.TerexApplicationException;

import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;

import javax.inject.Singleton;

@Singleton
public class OrganizationRepository {

    private final OrganizationDao organizationDao;

    public OrganizationRepository() {
        organizationDao = AppDatabase.getDatabase().organizationDao();
    }

    public List<Organization> getOrganizations() {
        try {
            CompletionService<List<Organization>> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
            service.submit(organizationDao::getOrganizations);
            Future<List<Organization>> future = service.take();
            return future != null ? future.get() : null;
        } catch (InterruptedException | ExecutionException e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("error getting organizations!", "50050", e);
        }
    }


    public Organization getOrganization(Long organizationId) {
        try {
            CompletionService<Organization> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
            service.submit(() -> organizationDao.getOrganization(organizationId));
            Future<Organization> future = service.take();
            return future != null ? future.get() : null;
        } catch (InterruptedException | ExecutionException e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("error getting organization!", "50050", e);
        }
    }

    public Organization findOrganization(String organizationName) {
        try {
            CompletionService<Organization> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
            service.submit(() -> organizationDao.findOrganization(organizationName));
            Future<Organization> future = service.take();
            return future != null ? future.get() : null;
        } catch (InterruptedException | ExecutionException e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("error find organization!", "50050", e);
        }
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public Long insert(Organization organization) throws InterruptedException, ExecutionException {
        Log.d("insert", "organization: " + organization);
        CompletionService<Long> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
        service.submit(() -> organizationDao.insert(organization));
        Future<Long> future = service.take();
        return future != null ? future.get() : null;
    }

    public Integer update(Organization organization) throws InterruptedException, ExecutionException {
        Log.d("update", "organization: " + organization);
        CompletionService<Integer> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
        service.submit(() -> organizationDao.update(organization));
        Future<Integer> future = service.take();
        return future != null ? future.get() : null;
    }
}
