package com.gunnarro.android.terex.repository;

import android.util.Log;

import com.gunnarro.android.terex.config.AppDatabase;
import com.gunnarro.android.terex.domain.entity.ContactInfo;
import com.gunnarro.android.terex.exception.TerexApplicationException;

import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;

import javax.inject.Singleton;

@Singleton
public class ContactInfoRepository {

    private final ContactInfoDao contactInfoDao;

    public ContactInfoRepository() {
        contactInfoDao = AppDatabase.getDatabase().contactInfoDao();
    }


    public ContactInfo getContactInfo(Long addressId) {
        try {
            CompletionService<ContactInfo> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
            service.submit(() -> contactInfoDao.getContactInfo(addressId));
            Future<ContactInfo> future = service.take();
            return future != null ? future.get() : null;
        } catch (InterruptedException | ExecutionException e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("error getting contactInfo!", "50050", e);
        }
    }

    public ContactInfo find(String mobileNumber, String emailAddress) {
        try {
            CompletionService<ContactInfo> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
            service.submit(() -> contactInfoDao.findContactInfo(mobileNumber, emailAddress));
            Future<ContactInfo> future = service.take();
            return future != null ? future.get() : null;
        } catch (InterruptedException | ExecutionException e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("error find contactInfo!", "50050", e);
        }
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public Long insert(ContactInfo contactInfo) throws InterruptedException, ExecutionException {
        Log.d("insert", "contactInfo: " + contactInfo);
        CompletionService<Long> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
        service.submit(() -> contactInfoDao.insert(contactInfo));
        Future<Long> future = service.take();
        return future != null ? future.get() : null;
    }

    public Integer update(ContactInfo contactInfo) throws InterruptedException, ExecutionException {
        Log.d("update", "contactInfo: " + contactInfo);
        CompletionService<Integer> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
        service.submit(() -> contactInfoDao.update(contactInfo));
        Future<Integer> future = service.take();
        return future != null ? future.get() : null;
    }
}
