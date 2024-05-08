package com.gunnarro.android.terex.repository;

import android.util.Log;

import com.gunnarro.android.terex.config.AppDatabase;
import com.gunnarro.android.terex.domain.dto.OrganizationDto;
import com.gunnarro.android.terex.domain.entity.Organization;
import com.gunnarro.android.terex.domain.mapper.TimesheetMapper;
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
public class OrganizationRepository {

    private final OrganizationDao organizationDao;

    public OrganizationRepository(OrganizationDao organizationDao) {
        this.organizationDao = organizationDao;
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

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public Long save(@NotNull final Organization organization) {
        try {
            Organization organizationExisting = findOrganization(organization.getName());
            Log.d("save organization", String.format("%s", organizationExisting));
            Long orgId;
            if (organizationExisting == null) {
                organization.setCreatedDate(LocalDateTime.now());
                organization.setLastModifiedDate(LocalDateTime.now());
                orgId = insert(organization);
                Log.d("", "inserted new organization: " + orgId + " - " + organization.getName());
            } else {
                organization.setId(organizationExisting.getId());
                organization.setCreatedDate(organizationExisting.getCreatedDate());
                organization.setLastModifiedDate(LocalDateTime.now());
                update(organization);
                orgId = organization.getId();
                Log.d("", "updated organization: " + orgId + " - " + organization.getName());
            }
            return orgId;
        } catch (Exception e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("Error saving organization! " + e.getMessage(), "50050", e.getCause());
        }
    }
}
