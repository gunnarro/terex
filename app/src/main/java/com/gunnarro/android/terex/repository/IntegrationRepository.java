package com.gunnarro.android.terex.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.gunnarro.android.terex.config.AppDatabase;
import com.gunnarro.android.terex.domain.entity.Integration;
import com.gunnarro.android.terex.exception.TerexApplicationException;

import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;

import javax.inject.Singleton;

@Singleton
public class IntegrationRepository {

    private final IntegrationDao integrationDao;

    public IntegrationRepository() {
        this.integrationDao = AppDatabase.getDatabase().integrationDao();
    }

    public LiveData<List<Integration>> getIntegrationsLiveData() {
        return integrationDao.getIntegrationsLiveData();
    }

    public List<Integration> getIntegrations() {
        try {
            CompletionService<List<Integration>> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
            service.submit(integrationDao::getIntegrations);
            Future<List<Integration>> future = service.take();
            return future != null ? future.get() : null;
        } catch (InterruptedException | ExecutionException e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("error getting integration!", "50050", e);
        }
    }

    public Integration getIntegration(Long integrationId) {
        try {
            CompletionService<Integration> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
            service.submit(() -> integrationDao.getIntegration(integrationId));
            Future<Integration> future = service.take();
            return future != null ? future.get() : null;
        } catch (InterruptedException | ExecutionException e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("error getting integration!", "50050", e);
        }
    }

    public Integration find(String system) {
        try {
            CompletionService<Integration> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
            service.submit(() -> integrationDao.find(system));
            Future<Integration> future = service.take();
            return future != null ? future.get() : null;
        } catch (InterruptedException | ExecutionException e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("error find project!", "50050", e);
        }
    }

    public Long insert(Integration integration) throws InterruptedException, ExecutionException {
        Log.d("insertIntegration", "Integration: " + integration);
        CompletionService<Long> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
        service.submit(() -> integrationDao.insert(integration));
        Future<Long> future = service.take();
        return future != null ? future.get() : null;
    }

    public Integer update(Integration integration) throws InterruptedException, ExecutionException {
        Log.d("updateProject", "Integration: " + integration);
        CompletionService<Integer> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
        service.submit(() -> integrationDao.update(integration));
        Future<Integer> future = service.take();
        return future != null ? future.get() : null;
    }

    public void delete(Integration integration) {
        AppDatabase.databaseExecutor.execute(() -> {
            integrationDao.delete(integration);
            Log.d("IntegrationRepository.delete", "deleted, integrationId=" + integration.getId());
        });
    }

}
