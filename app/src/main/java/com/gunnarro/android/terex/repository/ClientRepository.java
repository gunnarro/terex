package com.gunnarro.android.terex.repository;

import android.util.Log;

import com.gunnarro.android.terex.config.AppDatabase;
import com.gunnarro.android.terex.domain.entity.Client;
import com.gunnarro.android.terex.exception.TerexApplicationException;

import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;

import javax.inject.Singleton;

@Singleton
public class ClientRepository {

    private final ClientDao clientDao;

    public ClientRepository() {
        clientDao = AppDatabase.getDatabase().clientDao();
    }

    public List<Client> getClients() {
        try {
            CompletionService<List<Client>> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
            service.submit(clientDao::getClients);
            Future<List<Client>> future = service.take();
            return future != null ? future.get() : null;
        } catch (InterruptedException | ExecutionException e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("error getting clients!", "50050", e);
        }
    }

    public Client getClient(Long clientId) {
        try {
            CompletionService<Client> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
            service.submit(() -> clientDao.getClient(clientId));
            Future<Client> future = service.take();
            return future != null ? future.get() : null;
        } catch (InterruptedException | ExecutionException e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("error getting clients!", "50050", e);
        }
    }

    public Client findClient(String name) {
        try {
            CompletionService<Client> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
            service.submit(() -> clientDao.findClient(name));
            Future<Client> future = service.take();
            return future != null ? future.get() : null;
        } catch (InterruptedException | ExecutionException e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("error find client!", "50050", e);
        }
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public Long insert(Client client) throws InterruptedException, ExecutionException {
        Log.d("insert", "client: " + client);
        CompletionService<Long> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
        service.submit(() -> clientDao.insert(client));
        Future<Long> future = service.take();
        return future != null ? future.get() : null;
    }

    public Integer update(Client client) throws InterruptedException, ExecutionException {
        Log.d("update", "client: " + client);
        CompletionService<Integer> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
        service.submit(() -> clientDao.update(client));
        Future<Integer> future = service.take();
        return future != null ? future.get() : null;
    }
}
