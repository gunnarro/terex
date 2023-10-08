package com.gunnarro.android.terex.repository;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.gunnarro.android.terex.domain.entity.InvoiceSummary;

import java.util.List;

@Dao
public interface InvoiceSummaryDao {

    @Query("select * from invoice_summary i where i.invoice_id = :invoiceId")
    List<InvoiceSummary> getInvoiceSummaries(Long invoiceId);

    /**
     * @param invoiceSummary timesheet to be inserted. Abort if conflict, i.e. silently drop the insert
     * @return the id of the inserted invoice summary row
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insert(InvoiceSummary invoiceSummary);

    /**
     * @param invoiceSummary updated invoice summary. Replace on conflict, i.e, replace old data with the new one
     * @return number of updated row(S), should only be one for this method.
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    int update(InvoiceSummary invoiceSummary);

    /**
     * @param invoiceSummary to be deleted
     */
    @Delete
    void delete(InvoiceSummary invoiceSummary);

}
