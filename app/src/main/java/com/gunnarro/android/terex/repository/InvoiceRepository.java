package com.gunnarro.android.terex.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.gunnarro.android.terex.config.AppDatabase;
import com.gunnarro.android.terex.domain.entity.Invoice;
import com.gunnarro.android.terex.domain.entity.InvoiceAttachment;
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
public class InvoiceRepository {

    /**
     * OPEN: not added any billing information or assigned to a timesheet
     * CREATED: fulfilled and assigned to a timesheet. Possible to edit and delete.
     * SENT: sent to customer, tha invoice is then closed and assigned timesheet is set to status CLOSED. Not possible to edit or delete. Can only be viewed.
     * CANCELLED: the invoice have been cancelled.
     */
    public enum InvoiceStatusEnum {
        NEW, COMPLETED, SENT
    }

    private final InvoiceDao invoiceDao;

    private final InvoiceAttachmentDao invoiceAttachmentDao;
    private final TimesheetSummaryDao timesheetSummaryDao;
    private final LiveData<List<Invoice>> allInvoices;

    // Note that in order to unit test the WordRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    public InvoiceRepository(Context applicationContext) {
        invoiceDao = AppDatabase.getDatabase(applicationContext).invoiceDao();
        invoiceAttachmentDao = AppDatabase.getDatabase(applicationContext).invoiceAttachmentDao();
        timesheetSummaryDao = AppDatabase.getDatabase(applicationContext).timesheetSummaryDao();
        allInvoices = invoiceDao.getAll();
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
                return invoiceDao.getInvoice(invoiceId);
            });
            Future<Invoice> future = service.take();
            return future != null ? future.get() : null;
        } catch (InterruptedException | ExecutionException e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("Error get invoice", e.getMessage(), e.getCause());
        }
    }

    public Invoice findInvoice(String reference) {
        try {
            CompletionService<Invoice> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
            service.submit(() -> invoiceDao.getInvoiceByRef(reference));
            Future<Invoice> future = service.take();
            return future != null ? future.get() : null;
        } catch (InterruptedException | ExecutionException e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("Error find invoice", e.getMessage(), e.getCause());
        }
    }


    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public Long saveInvoice(@NotNull final Invoice invoice) {
        try {
            Invoice invoiceExisting = findInvoice(invoice.getReference());
            if (invoiceExisting != null && invoiceExisting.isCompleted()) {
                throw new TerexApplicationException("Invoice is COMPLETED, not allowed to delete or update", "40045", null);
            }
            Log.d("InvoiceRepository.saveInvoice", String.format("%s", invoice));
            Long id;
            if (invoiceExisting == null) {
                // this is a new invoice
                invoice.setCreatedDate(LocalDateTime.now());
                invoice.setLastModifiedDate(LocalDateTime.now());
                id = insertInvoice(invoice);
                Log.d("", "inserted new invoice: " + id + " - " + invoice.getReference());
            } else {
                invoice.setId(invoiceExisting.getId());
                invoice.setLastModifiedDate(LocalDateTime.now());
                updateInvoice(invoice);
                id = invoice.getId();
                Log.d("", "updated invoice: " + id + " - " + invoice.getReference());
            }
            return id;
        } catch (Exception e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
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

    // ----------------------------------------------------------
    // invoice attachment
    // ----------------------------------------------------------

    public InvoiceAttachment getInvoiceAttachment(Long invoiceId, String attachmentType, String attachmentFileType) {
        try {
            CompletionService<InvoiceAttachment> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
            service.submit(() -> invoiceAttachmentDao.getInvoiceAttachment(invoiceId, attachmentType, attachmentFileType));
            Future<InvoiceAttachment> future = service.take();
            return future != null ? future.get() : null;
        } catch (InterruptedException | ExecutionException e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("Error get invoice attachment", e.getMessage(), e.getCause());
        }
    }

    public InvoiceAttachment findInvoiceAttachment(Long invoiceId, String attachmentType, String attachmentFileName, String attachmentFileType) {
        try {
            CompletionService<InvoiceAttachment> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
            service.submit(() -> invoiceAttachmentDao.findInvoiceAttachment(invoiceId, attachmentType, attachmentFileName, attachmentFileType));
            Future<InvoiceAttachment> future = service.take();
            return future != null ? future.get() : null;
        } catch (InterruptedException | ExecutionException e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("Error find invoice attachment", e.getMessage(), e.getCause());
        }
    }

    public Long saveInvoiceAttachment(@NotNull final InvoiceAttachment invoiceAttachment) {
        try {
            Invoice invoiceAttachmentExisting = null;//findInvoice(invoice.getReference());
            Log.d("InvoiceRepository.saveInvoiceFile", String.format("%s", invoiceAttachmentExisting));
            Long id = null;
            if (invoiceAttachmentExisting == null) {
                invoiceAttachment.setCreatedDate(LocalDateTime.now());
                invoiceAttachment.setLastModifiedDate(LocalDateTime.now());
                id = insertInvoiceAttachment(invoiceAttachment);
                Log.d("", "inserted new invoice file: " + id + " - " + invoiceAttachment.getAttachmentFileName());
            } else {
                invoiceAttachment.setId(invoiceAttachmentExisting.getId());
                invoiceAttachment.setLastModifiedDate(LocalDateTime.now());
                updateInvoiceAttachment(invoiceAttachment);
                id = invoiceAttachment.getId();
                Log.d("", "updated invoice file: " + id + " - " + invoiceAttachment.getAttachmentFileName());
            }
            return id;
        } catch (Exception e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("Error saving invoice!", e.getMessage(), e.getCause());
        }
    }


    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    private Long insertInvoiceAttachment(InvoiceAttachment invoiceAttachment) throws InterruptedException, ExecutionException {
        Log.d("insertInvoiceAttachment", "invoiceFile: " + invoiceAttachment);
        CompletionService<Long> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
        service.submit(() -> invoiceAttachmentDao.insert(invoiceAttachment));
        Future<Long> future = service.take();
        return future != null ? future.get() : null;
    }

    private Integer updateInvoiceAttachment(InvoiceAttachment invoiceAttachment) throws InterruptedException, ExecutionException {
        Log.d("updateInvoiceAttachment", ": " + invoiceAttachment);
        CompletionService<Integer> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
        service.submit(() -> invoiceAttachmentDao.update(invoiceAttachment));
        Future<Integer> future = service.take();
        return future != null ? future.get() : null;
    }

}
