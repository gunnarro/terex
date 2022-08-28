package com.gunnarro.android.terex.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.gunnarro.android.terex.config.AppDatabase;
import com.gunnarro.android.terex.domain.entity.Invoice;
import com.gunnarro.android.terex.domain.entity.InvoiceSummary;
import com.gunnarro.android.terex.domain.entity.Timesheet;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class InvoiceRepository {
    private InvoiceDao invoiceDao;
    private LiveData<List<Invoice>> allInvoices;

    public Map<Invoice, List<InvoiceSummary>> getInvoiceAndSummaryById(Integer invoiceId) {
        return invoiceDao.getInvoiceById(invoiceId);
    }

    // Note that in order to unit test the WordRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    public InvoiceRepository(Application application) {
        invoiceDao = AppDatabase.getDatabase(application).invoiceDao();
        allInvoices = invoiceDao.getAll();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    public LiveData<List<Invoice>> getAllInvoices() {
        return allInvoices;
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public void insert(Invoice invoice) {
        AppDatabase.databaseWriteExecutor.execute(() -> invoiceDao.insert(invoice));
        Log.d("InvoiceRepository.insert" , "saved: " + invoice);
    }
}
