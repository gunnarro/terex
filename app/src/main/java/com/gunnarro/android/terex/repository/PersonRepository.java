package com.gunnarro.android.terex.repository;

import android.util.Log;

import com.gunnarro.android.terex.config.AppDatabase;
import com.gunnarro.android.terex.domain.entity.Person;
import com.gunnarro.android.terex.exception.TerexApplicationException;

import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;

import javax.inject.Singleton;

@Singleton
public class PersonRepository {

    private final PersonDao personDao;

    public PersonRepository() {
        personDao = AppDatabase.getDatabase().personDao();
    }


    public Person getPerson(Long personId) {
        try {
            CompletionService<Person> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
            service.submit(() -> personDao.getPerson(personId));
            Future<Person> future = service.take();
            return future != null ? future.get() : null;
        } catch (InterruptedException | ExecutionException e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("error getting person!", "50050", e);
        }
    }

    public Person findPerson(String fullName) {
        try {
            CompletionService<Person> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
            service.submit(() -> personDao.findPerson(fullName));
            Future<Person> future = service.take();
            return future != null ? future.get() : null;
        } catch (InterruptedException | ExecutionException e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("error find person!", "50050", e);
        }
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public Long insert(Person person) throws InterruptedException, ExecutionException {
        Log.d("insert", "person: " + person);
        CompletionService<Long> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
        service.submit(() -> personDao.insert(person));
        Future<Long> future = service.take();
        return future != null ? future.get() : null;
    }

    public Integer update(Person person) throws InterruptedException, ExecutionException {
        Log.d("insert", "person: " + person);
        CompletionService<Integer> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
        service.submit(() -> personDao.update(person));
        Future<Integer> future = service.take();
        return future != null ? future.get() : null;
    }
}
