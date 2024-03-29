package com.gunnarro.android.terex.repository;

import android.util.Log;

import com.gunnarro.android.terex.config.AppDatabase;
import com.gunnarro.android.terex.domain.entity.Address;
import com.gunnarro.android.terex.exception.TerexApplicationException;

import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;

import javax.inject.Singleton;

@Singleton
public class AddressRepository {


    private final AddressDao addressDao;

    // Note that in order to unit test the WordRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    public AddressRepository() {
        addressDao = AppDatabase.getDatabase().addressDao();
    }


    public Address getAddress(Long addressId) {
        try {
            CompletionService<Address> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
            service.submit(() -> addressDao.getAddress(addressId));
            Future<Address> future = service.take();
            return future != null ? future.get() : null;
        } catch (InterruptedException | ExecutionException e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("error getting address!", "50050", e);
        }
    }

    public Address findAddress(String streetName, String streetNumber) {
        try {
            CompletionService<Address> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
            service.submit(() -> addressDao.findAddress(streetName, streetNumber));
            Future<Address> future = service.take();
            return future != null ? future.get() : null;
        } catch (InterruptedException | ExecutionException e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("error find address!", "50050", e);
        }
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public Long insert(Address address) throws InterruptedException, ExecutionException {
        Log.d("insert", "address: " + address);
        CompletionService<Long> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
        service.submit(() -> addressDao.insert(address));
        Future<Long> future = service.take();
        return future != null ? future.get() : null;
    }

    public Integer update(Address address) throws InterruptedException, ExecutionException {
        Log.d("insert", "address: " + address);
        CompletionService<Integer> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
        service.submit(() -> addressDao.update(address));
        Future<Integer> future = service.take();
        return future != null ? future.get() : null;
    }
}
