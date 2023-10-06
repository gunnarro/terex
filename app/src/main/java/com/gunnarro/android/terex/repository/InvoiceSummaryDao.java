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

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(InvoiceSummary invoiceSummary);

    @Update
    void update(InvoiceSummary invoiceSummary);

    @Delete
    void delete(InvoiceSummary invoiceSummary);

}
