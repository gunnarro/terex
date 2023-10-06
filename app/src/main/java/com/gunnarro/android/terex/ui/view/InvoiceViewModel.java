package com.gunnarro.android.terex.ui.view;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.gunnarro.android.terex.domain.entity.Invoice;
import com.gunnarro.android.terex.repository.InvoiceRepository;

import java.util.List;

/**
 * Repository is completely separated from the UI through the ViewModel.
 */
public class InvoiceViewModel extends AndroidViewModel {

    private final InvoiceRepository repository;
    // Using LiveData and caching what getAlphabetizedWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    private final LiveData<List<Invoice>> invoices;

    public InvoiceViewModel(Application application) {
        super(application);
        repository = new InvoiceRepository(application);
        invoices = repository.getAllInvoices();
    }

    public LiveData<List<Invoice>> getAllInvoices() {
        return invoices;
    }

    public void insert(Invoice invoice) {
        Log.d("InvoiceViewModel.insert" , "insert: " + invoice);
        repository.insertInvoice(invoice);
    }
}
