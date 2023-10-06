package com.gunnarro.android.terex.repository;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.gunnarro.android.terex.domain.entity.Invoice;
import com.gunnarro.android.terex.domain.entity.InvoiceSummary;

import java.util.List;
import java.util.Map;

@Dao
public interface InvoiceDao {

    @Query("SELECT * FROM invoice i JOIN invoice_summary s ON i.id = s.invoice_id WHERE i.id = :invoiceId")
    Map<Invoice, List<InvoiceSummary>> getInvoiceById(long invoiceId);

    @Query("SELECT * FROM invoice ORDER BY billing_date ASC")
    LiveData<List<Invoice>> getAll();

    @Query("SELECT * FROM invoice i WHERE i.id = :invoiceId")
    Invoice getInvoice(long invoiceId);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Invoice invoice);

    @Update
    void update(Invoice invoice);

    @Delete
    void delete(Invoice invoice);

}
