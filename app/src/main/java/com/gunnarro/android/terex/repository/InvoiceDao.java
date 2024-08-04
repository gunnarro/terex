package com.gunnarro.android.terex.repository;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.gunnarro.android.terex.domain.entity.Invoice;

import java.util.List;

@Dao
public interface InvoiceDao {

    @Query("SELECT * FROM invoice i where i.id = :invoiceId")//" JOIN TimesheetSummary s ON i.id = s.invoice_id WHERE i.id = :invoiceId")
    Invoice getInvoiceById(long invoiceId);

    @Query("SELECT * FROM invoice ORDER BY billing_date ASC")
    List<Invoice> getAll();

    @Query("SELECT * FROM invoice i WHERE i.id = :invoiceId")
    Invoice getInvoice(long invoiceId);

    @Query("SELECT id FROM invoice ORDER BY billing_period_start_date ASC")
    List<Long> getAllInvoiceIds();

    /**
     * @param invoice timesheet to be inserted. Abort if conflict, i.e. silently drop the insert
     * @return the id of the inserted invoice row
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insert(Invoice invoice);

    /**
     * @param invoice updated invoice. Replace on conflict, i.e, replace old data with the new one
     * @return number of updated row(S), should only be one for this method.
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    int update(Invoice invoice);

    /**
     * @param invoice to be deleted
     */
    @Delete
    void delete(Invoice invoice);

    @Query("SELECT * FROM invoice i WHERE i.reference = :reference")
    Invoice getInvoiceByRef(String reference);
}
