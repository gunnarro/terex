package com.gunnarro.android.terex.repository;

import android.util.Log;

import com.gunnarro.android.terex.config.AppDatabase;
import com.gunnarro.android.terex.domain.entity.Company;
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
public class CompanyRepository {

    private final CompanyDao companyDao;

    public CompanyRepository() {
        companyDao = AppDatabase.getDatabase().companyDao();
    }


    public List<Company> getCompanies() {
        try {
            CompletionService<List<Company>> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
            service.submit(companyDao::getCompanies);
            Future<List<Company>> future = service.take();
            return future != null ? future.get() : null;
        } catch (InterruptedException | ExecutionException e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("error getting companies!", "50050", e);
        }
    }


    public Company getCompany(Long companyId) {
        try {
            CompletionService<Company> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
            service.submit(() -> companyDao.getCompany(companyId));
            Future<Company> future = service.take();
            return future != null ? future.get() : null;
        } catch (InterruptedException | ExecutionException e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("error getting company!", "50050", e);
        }
    }

    public Company findProject(String companyName) {
        try {
            CompletionService<Company> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
            service.submit(() -> companyDao.findCompany(companyName));
            Future<Company> future = service.take();
            return future != null ? future.get() : null;
        } catch (InterruptedException | ExecutionException e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("error find project!", "50050", e);
        }
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public Long saveCompany(@NotNull final Company company) {
        try {
            Company companyExisting = companyDao.findCompany(company.getName());
            Log.d("CompanyRepository.saveCompany", String.format("%s", companyExisting));
            Long id = null;
            if (companyExisting == null) {
                company.setCreatedDate(LocalDateTime.now());
                company.setLastModifiedDate(LocalDateTime.now());
                id = insertCompany(company);
                Log.d("", "inserted new company: " + id + " - " + company.getName());
            } else {
                company.setId(companyExisting.getId());
                company.setCreatedDate(companyExisting.getCreatedDate());
                company.setLastModifiedDate(LocalDateTime.now());
                updateCompany(company);
                id = company.getId();
                Log.d("", "updated project: " + id + " - " + company.getName());
            }
            return id;
        } catch (Exception e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("Error saving company!", e.getMessage(), e.getCause());
        }
    }


    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    private Long insertCompany(Company company) throws InterruptedException, ExecutionException {
        Log.d("insertProject", "company: " + company);
        CompletionService<Long> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
        service.submit(() -> companyDao.insert(company));
        Future<Long> future = service.take();
        return future != null ? future.get() : null;
    }

    private Integer updateCompany(Company company) throws InterruptedException, ExecutionException {
        Log.d("updateInvoice", "company: " + company);
        CompletionService<Integer> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
        service.submit(() -> companyDao.update(company));
        Future<Integer> future = service.take();
        return future != null ? future.get() : null;
    }
}
