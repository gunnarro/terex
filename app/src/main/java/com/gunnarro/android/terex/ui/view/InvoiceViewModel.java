package com.gunnarro.android.terex.ui.view;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.gunnarro.android.terex.domain.entity.Invoice;
import com.gunnarro.android.terex.repository.InvoiceRepository;

import java.util.List;

/**
 * Repository is completely separated from the UI through the ViewModel.
 */
public class InvoiceViewModel extends AndroidViewModel {

    private final InvoiceRepository invoiceRepository;
    // Using LiveData and caching what getAlphabetizedWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    private final LiveData<List<Invoice>> invoices;

    public InvoiceViewModel(Application application) {
        super(application);
        invoiceRepository = new InvoiceRepository();
        invoices = invoiceRepository.getAllInvoices();
    }

    public LiveData<List<Invoice>> getAllInvoices() {
        return invoices;
    }

}
