package com.gunnarro.android.terex.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.gunnarro.android.terex.config.AppDatabase;
import com.gunnarro.android.terex.domain.entity.Invoice;
import com.gunnarro.android.terex.domain.entity.InvoiceSummary;
import com.gunnarro.android.terex.exception.TerexApplicationException;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;

import javax.inject.Singleton;

@Singleton
public class InvoiceRepository {

    /**
     * OPEN: not added any billing information or assigned to a timesheet
     * CREATED: fulfilled and assigned to a timesheet. Possible to edit and delete.
     * SENT: sent to customer, tha invoice is then closed and assigned timesheet is set to status CLOSED. Not possible to edit or delete. Can only be viewed.
     * CANCELLED: the invoice have been cancelled.
     */
    public enum InvoiceStatusEnum {
        OPEN, CREATED, SENT, CANCELLED;
    }

    private final InvoiceDao invoiceDao;
    private final InvoiceSummaryDao invoiceSummaryDao;
    private final LiveData<List<Invoice>> allInvoices;

    // Note that in order to unit test the WordRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    public InvoiceRepository(Context applicationContext) {
        invoiceDao = AppDatabase.getDatabase(applicationContext).invoiceDao();
        invoiceSummaryDao = AppDatabase.getDatabase(applicationContext).invoiceSummaryDao();
        allInvoices = invoiceDao.getAll();
    }

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

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    public LiveData<List<Invoice>> getAllInvoices() {
        return allInvoices;
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

    public Invoice findInvoice(String reference) {
        try {
            CompletionService<Invoice> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
            service.submit(() -> invoiceDao.getInvoiceByRef(reference));
            Future<Invoice> future = service.take();
            return future != null ? future.get() : null;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Long insertInvoiceSummary(InvoiceSummary invoiceSummary) {
        Log.d("updateInvoice", "invoiceSummery: " + invoiceSummary);
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

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public Long saveInvoice(@NotNull final Invoice invoice) {
        try {
            Invoice invoiceExisting = findInvoice(invoice.getReference());
            Log.d("TimesheetRepository.saveTimesheetEntry", String.format("%s", invoiceExisting));
            Long id = null;
            if (invoiceExisting == null) {
                invoice.setCreatedDate(LocalDateTime.now());
                invoice.setLastModifiedDate(LocalDateTime.now());
                id = insertInvoice(invoice);
                Log.d("", "inserted new invoice: " + id + " - " + invoice.getReference());
            } else {
                invoice.setId(invoiceExisting.getId());
                invoice.setCreatedDate(invoiceExisting.getCreatedDate());
                invoice.setLastModifiedDate(LocalDateTime.now());
                updateInvoice(invoice);
                id = invoice.getId();
                Log.d("", "updated invoice: " + id + " - " + invoice.getReference());
            }
            return id;
        } catch (Exception e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            e.printStackTrace();
            throw new TerexApplicationException("Error saving invoice!", e.getMessage(), e.getCause());
        }
    }


    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    private Long insertInvoice(Invoice invoice) throws InterruptedException, ExecutionException {
        Log.d("inertInvoice", "invoice: " + invoice);
        CompletionService<Long> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
        service.submit(() -> invoiceDao.insert(invoice));
        Future<Long> future = service.take();
        return future != null ? future.get() : null;
    }

    private Integer updateInvoice(Invoice invoice) throws InterruptedException, ExecutionException {
        Log.d("updateInvoice", "invoice: " + invoice);
        CompletionService<Integer> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
        service.submit(() -> invoiceDao.update(invoice));
        Future<Integer> future = service.take();
        return future != null ? future.get() : null;
    }
}
