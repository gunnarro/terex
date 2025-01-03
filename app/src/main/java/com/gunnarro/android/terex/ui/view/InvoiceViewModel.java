package com.gunnarro.android.terex.ui.view;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.gunnarro.android.terex.domain.dto.InvoiceDto;
import com.gunnarro.android.terex.domain.dto.TimesheetDto;
import com.gunnarro.android.terex.domain.entity.Invoice;
import com.gunnarro.android.terex.repository.InvoiceRepository;
import com.gunnarro.android.terex.service.InvoiceService;

import java.util.List;

/**
 * Repository is completely separated from the UI through the ViewModel.
 */
public class InvoiceViewModel extends AndroidViewModel {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceService invoiceService;
    // Using LiveData and caching what getAlphabetizedWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    //private final LiveData<List<Invoice>> invoices;

    private final MutableLiveData<List<InvoiceDto>> invoiceListLiveData;

    public InvoiceViewModel(Application application) {
        super(application);
        invoiceRepository = new InvoiceRepository();
        invoiceService = new InvoiceService();
        invoiceListLiveData = new MutableLiveData<>();
        invoiceListLiveData.setValue(invoiceService.getInvoiceDtoList());
        //invoices = invoiceRepository.getAllInvoices();
    }

    public LiveData<List<InvoiceDto>> getAllInvoices() {
        return invoiceListLiveData;
    }

    public void deleteInvoice(Long invoiceId) {
        invoiceService.deleteInvoice(invoiceId);
    }
}
