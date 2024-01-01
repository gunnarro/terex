package com.gunnarro.android.terex.repository;

import android.util.Log;

import com.gunnarro.android.terex.config.AppDatabase;
import com.gunnarro.android.terex.domain.entity.ConsultantBroker;
import com.gunnarro.android.terex.domain.entity.ConsultantBrokerWithProject;
import com.gunnarro.android.terex.exception.TerexApplicationException;

import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;

import javax.inject.Singleton;

@Singleton
public class ConsultantBrokerRepository {

    private final ConsultantBrokerDao consultantBrokerDao;

    public ConsultantBrokerRepository() {
        consultantBrokerDao = AppDatabase.getDatabase().consultantBrokerDao();
    }

    public List<ConsultantBrokerWithProject> getConsultantBrokers() {
        try {
            CompletionService<List<ConsultantBrokerWithProject>> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
            service.submit(consultantBrokerDao::getConsultantBrokerWithProjects);
            Future<List<ConsultantBrokerWithProject>> future = service.take();
            return future != null ? future.get() : null;
        } catch (InterruptedException | ExecutionException e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("error getting companies!", "50050", e);
        }
    }

    public ConsultantBrokerWithProject getConsultantBroker(Long consultantBrokerId) {
        try {
            CompletionService<ConsultantBrokerWithProject> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
            service.submit(() -> consultantBrokerDao.getConsultantBrokerWithProjects(consultantBrokerId));
            Future<ConsultantBrokerWithProject> future = service.take();
            return future != null ? future.get() : null;
        } catch (InterruptedException | ExecutionException e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("error getting companies!", "50050", e);
        }
    }

    public ConsultantBroker findConsultantBroker(String name) {
        try {
            CompletionService<ConsultantBroker> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
            service.submit(() -> consultantBrokerDao.findConsultantBroker(name));
            Future<ConsultantBroker> future = service.take();
            return future != null ? future.get() : null;
        } catch (InterruptedException | ExecutionException e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("error find project!", "50050", e);
        }
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public Long insertConsultantBroker(ConsultantBroker consultantBroker) throws InterruptedException, ExecutionException {
        Log.d("insertConsultantBroker", "consultantBroker: " + consultantBroker);
        CompletionService<Long> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
        service.submit(() -> consultantBrokerDao.insert(consultantBroker));
        Future<Long> future = service.take();
        return future != null ? future.get() : null;
    }

    public Integer updateConsultantBroker(ConsultantBroker consultantBroker) throws InterruptedException, ExecutionException {
        Log.d("updateConsultantBroker", "consultantBroker: " + consultantBroker);
        CompletionService<Integer> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
        service.submit(() -> consultantBrokerDao.update(consultantBroker));
        Future<Integer> future = service.take();
        return future != null ? future.get() : null;
    }
}
