package com.gunnarro.android.terex.repository;

import android.util.Log;

import com.gunnarro.android.terex.config.AppDatabase;
import com.gunnarro.android.terex.domain.entity.Address;
import com.gunnarro.android.terex.exception.TerexApplicationException;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;

import javax.inject.Singleton;

@Singleton
public class AddressRepository {

    private final AddressDao addressDao;

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

    public Address find(String streetAddress) {
        try {
            CompletionService<Address> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
            service.submit(() -> addressDao.findAddress(streetAddress));
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
        Log.d("insert", String.format("address: %s", address));
        CompletionService<Long> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
        service.submit(() -> addressDao.insert(address));
        Future<Long> future = service.take();
        return future != null ? future.get() : null;
    }

    public Integer update(Address address) throws InterruptedException, ExecutionException {
        Log.d("update", String.format("address: %s", address));
        CompletionService<Integer> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
        service.submit(() -> addressDao.update(address));
        Future<Integer> future = service.take();
        return future != null ? future.get() : null;
    }

    public Long save(@NotNull final Address address) {
        try {
            Address addressExisting;
            if (address.getId() == null) {
                addressExisting = find(address.getStreetAddress());
            } else {
                addressExisting = getAddress(address.getId());
            }
            Log.d("saveAddress", String.format("streetAddress=%s, isExisting=%s", address.getStreetAddress(), addressExisting != null));
            Long addressId;
            if (addressExisting == null) {
                address.setCreatedDate(LocalDateTime.now());
                address.setLastModifiedDate(LocalDateTime.now());
                addressId = insert(address);
                Log.d("saveAddress", String.format("inserted new address: %s - %s", addressId, address.getStreetAddress()));
            } else {
                address.setId(addressExisting.getId());
                address.setCreatedDate(addressExisting.getCreatedDate());
                address.setLastModifiedDate(LocalDateTime.now());
                update(address);
                addressId = address.getId();
                Log.d("saveAddress", String.format("updated address: %s - %s", addressId, address.getStreetAddress()));
            }
            return addressId;
        } catch (Exception e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("Error saving address! " + e.getMessage(), "50050", e.getCause());
        }
    }
}
