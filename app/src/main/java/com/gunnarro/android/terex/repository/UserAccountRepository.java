package com.gunnarro.android.terex.repository;

import android.util.Log;

import com.gunnarro.android.terex.config.AppDatabase;
import com.gunnarro.android.terex.domain.entity.UserAccount;
import com.gunnarro.android.terex.exception.TerexApplicationException;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;

import javax.inject.Singleton;

@Singleton
public class UserAccountRepository {

    private final UserAccountDao userAccountDao;

    /**
     * For unit testing only
     *
     * @param userAccountDao user account dao mock
     */
    public UserAccountRepository(UserAccountDao userAccountDao) {
        this.userAccountDao = userAccountDao;
    }

    public UserAccountRepository() {
        userAccountDao = AppDatabase.getDatabase().userAccountDao();
    }

    public UserAccount getUserAccount(Long userAccountId) {
        try {
            CompletionService<UserAccount> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
            service.submit(() -> userAccountDao.getUserAccount(userAccountId));
            Future<UserAccount> future = service.take();
            return future != null ? future.get() : null;
        } catch (InterruptedException | ExecutionException e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("error getting user account!", "50050", e);
        }
    }

    public Long getDefaultUserAccountId() {
        try {
            CompletionService<Long> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
            service.submit(userAccountDao::getDefaultUserAccountId);
            Future<Long> future = service.take();
            return future != null ? future.get() : null;
        } catch (InterruptedException | ExecutionException e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("error getting user account!", "50050", e);
        }
    }

    public UserAccount getDefaultUserAccount() {
        try {
            CompletionService<UserAccount> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
            service.submit(userAccountDao::getDefaultUserAccount);
            Future<UserAccount> future = service.take();
            return future != null ? future.get() : null;
        } catch (InterruptedException | ExecutionException e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("error getting user account!", "50050", e);
        }
    }

    public UserAccount find(String userName) {
        try {
            CompletionService<UserAccount> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
            service.submit(() -> userAccountDao.findUserAccount(userName));
            Future<UserAccount> future = service.take();
            return future != null ? future.get() : null;
        } catch (InterruptedException | ExecutionException e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("error find user account!", "50050", e);
        }
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public Long insert(UserAccount userAccount) throws InterruptedException, ExecutionException {
        Log.d("insert", String.format("userAccount: %s", userAccount));
        CompletionService<Long> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
        service.submit(() -> userAccountDao.insert(userAccount));
        Future<Long> future = service.take();
        return future != null ? future.get() : null;
    }

    public Integer update(UserAccount userAccount) throws InterruptedException, ExecutionException {
        Log.d("update", String.format("userAccount: %s", userAccount));
        CompletionService<Integer> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
        service.submit(() -> userAccountDao.update(userAccount));
        Future<Integer> future = service.take();
        return future != null ? future.get() : null;
    }

    public void delete(UserAccount userAccount) {
        AppDatabase.databaseExecutor.execute(() -> {
            userAccountDao.delete(userAccount);
            Log.d("UserAccountRepository.delete", "deleted, userAccountId=" + userAccount.getId());
        });
    }

    public Long save(@NotNull final UserAccount userAccount) {
        try {
            UserAccount userAccountExisting;
            if (userAccount.getId() == null) {
                userAccountExisting = find(userAccount.getUserName());
            } else {
                userAccountExisting = getUserAccount(userAccount.getId());
            }
            Log.d("saveUserAccount", String.format("streetAddress=%s, isExisting=%s", userAccount, userAccountExisting != null));
            Long userAccountId;
            if (userAccountExisting == null) {
                userAccount.setCreatedDate(LocalDateTime.now());
                userAccount.setLastModifiedDate(LocalDateTime.now());
                userAccountId = insert(userAccount);
                Log.d("saveAddress", String.format("inserted new user account: %s - %s", userAccountId, userAccount));
            } else {
                userAccount.setId(userAccountExisting.getId());
                userAccount.setCreatedDate(userAccountExisting.getCreatedDate());
                userAccount.setLastModifiedDate(LocalDateTime.now());
                update(userAccount);
                userAccountId = userAccount.getId();
                Log.d("saveAddress", String.format("updated user account: %s - %s", userAccountId, userAccount));
            }
            return userAccountId;
        } catch (Exception e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("Error saving user account! " + e.getMessage(), "50050", e.getCause());
        }
    }
}
