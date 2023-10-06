package com.gunnarro.android.terex.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.gunnarro.android.terex.config.AppDatabase;
import com.gunnarro.android.terex.domain.entity.Invoice;
import com.gunnarro.android.terex.domain.entity.InvoiceSummary;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;

import javax.inject.Singleton;

@Singleton
public class InvoiceRepository {
    private InvoiceDao invoiceDao;
    private InvoiceSummaryDao invoiceSummaryDao;
    private LiveData<List<Invoice>> allInvoices;

    public Map<Invoice, List<InvoiceSummary>> getInvoiceAndSummaryById(Long invoiceId) {
        try {
            CompletionService<Map<Invoice, List<InvoiceSummary>>> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
            service.submit(() -> invoiceDao.getInvoiceById(invoiceId));
            Future<Map<Invoice, List<InvoiceSummary>>> future = service.take();
            return future != null ? future.get() : null;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Note that in order to unit test the WordRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    public InvoiceRepository(Context applicationContext) {
        invoiceDao = AppDatabase.getDatabase(applicationContext).invoiceDao();
        invoiceSummaryDao = AppDatabase.getDatabase(applicationContext).invoiceSummaryDao();
        allInvoices = invoiceDao.getAll();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    public LiveData<List<Invoice>> getAllInvoices() {
        return allInvoices;
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public Long insertInvoice(Invoice invoice) {
        try {
            CompletionService<Long> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
            service.submit(() -> invoiceDao.insert(invoice));
            Future<Long> future = service.take();
            return future != null ? future.get() : null;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Invoice getInvoice(Long invoiceId) {
        try {
            CompletionService<Invoice> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
            service.submit(() -> {
                Invoice invoice = invoiceDao.getInvoice(invoiceId);
                invoice.setInvoiceSummaryList(invoiceSummaryDao.getInvoiceSummaries(invoiceId));
                return invoice;
            });
            Future<Invoice> future = service.take();
            return future != null ? future.get() : null;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Long insertInvoiceSummary(InvoiceSummary invoiceSummary) {
        try {
            CompletionService<Long> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
            service.submit(() -> invoiceSummaryDao.insert(invoiceSummary));
            Future<Long> future = service.take();
            return future != null ? future.get() : null;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }
}
