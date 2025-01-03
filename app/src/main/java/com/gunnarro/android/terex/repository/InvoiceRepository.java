package com.gunnarro.android.terex.repository;

import android.util.Log;

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

    private final InvoiceDao invoiceDao;

    private final InvoiceAttachmentDao invoiceAttachmentDao;
    private final TimesheetSummaryDao timesheetSummaryDao;

    // Note that in order to unit test the WordRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    public InvoiceRepository() {
        invoiceDao = AppDatabase.getDatabase().invoiceDao();
        invoiceAttachmentDao = AppDatabase.getDatabase().invoiceAttachmentDao();
        timesheetSummaryDao = AppDatabase.getDatabase().timesheetSummaryDao();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    public List<Invoice> getAllInvoices() {
        try {
            CompletionService<List<Invoice>> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
            service.submit(invoiceDao::getAll);
            Future<List<Invoice>> future = service.take();
            return future != null ? future.get() : null;
        } catch (InterruptedException | ExecutionException e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("Error get invoice's", e.getMessage(), e.getCause());
        }
    }

    public List<Long> getAllInvoiceIds() {
        try {
            CompletionService<List<Long>> service = new ExecutorCompletionService<>(AppDatabase.databaseExecutor);
            service.submit(invoiceDao::getAllInvoiceIds);
            Future<List<Long>> future = service.take();
            return future != null ? future.get() : null;
        } catch (InterruptedException | ExecutionException e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("Error get invoice id's", e.getMessage(), e.getCause());
        }
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
                Log.d("InvoiceRepository.saveInvoice", String.format("inserted new invoice, invoiceId=%s, timesheetId=%s", id, invoice.getTimesheetId()));
            } else {
                invoice.setId(invoiceExisting.getId());
                invoice.setLastModifiedDate(LocalDateTime.now());
                updateInvoice(invoice);
                id = invoice.getId();
                Log.d("InvoiceRepository.saveInvoice", String.format("updated invoice, invoiceId=%s, timesheetId=%s ", id, invoice.getTimesheetId()));
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

    public void updateInvoiceStatus(Long invoiceId, String status) {
        AppDatabase.databaseExecutor.execute(() -> {
            invoiceDao.updateInvoiceStatus(invoiceId, status);
            Log.d("InvoiceRepository.updateInvoiceStatus", "invoiceId=" + invoiceId + ", status=" + status);
        });
    }

    public void deleteInvoice(Invoice invoice) {
        AppDatabase.databaseExecutor.execute(() -> {
            invoiceDao.delete(invoice);
            Log.d("InvoiceRepository.delete", "deleted, invoiceId=" + invoice.getId());
        });
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
            Log.d("saveInvoiceAttachment", String.format("%s", invoiceAttachmentExisting));
            Long id = null;
            if (invoiceAttachmentExisting == null) {
                invoiceAttachment.setCreatedDate(LocalDateTime.now());
                invoiceAttachment.setLastModifiedDate(LocalDateTime.now());
                id = insertInvoiceAttachment(invoiceAttachment);
                Log.d("saveInvoiceAttachment", "inserted new invoice file: " + id + " - " + invoiceAttachment.getFileName());
            } else {
                invoiceAttachment.setId(invoiceAttachmentExisting.getId());
                invoiceAttachment.setLastModifiedDate(LocalDateTime.now());
                updateInvoiceAttachment(invoiceAttachment);
                id = invoiceAttachment.getId();
                Log.d("saveInvoiceAttachment", "updated invoice file: " + id + " - " + invoiceAttachment.getFileName());
            }
            return id;
        } catch (Exception e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("Error saving invoice attachment!", e.getMessage(), e.getCause());
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

    public void deleteInvoiceAttachments(Long invoiceId) {
        AppDatabase.databaseExecutor.execute(() -> {
            invoiceAttachmentDao.deleteAttachments(invoiceId);
            Log.d("InvoiceRepository.delete", "deleted all attachments, invoiceId=" + invoiceId);
        });
    }
}
