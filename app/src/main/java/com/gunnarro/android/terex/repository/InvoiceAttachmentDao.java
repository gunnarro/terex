package com.gunnarro.android.terex.repository;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.gunnarro.android.terex.domain.entity.InvoiceAttachment;

@Dao
public interface InvoiceAttachmentDao {

    @Query("SELECT * FROM invoice_attachment a WHERE a.invoice_id = :invoiceId AND a.type = :attachmentType AND a.file_name = :attachmentFileName AND a.file_type = :attachmentFileType")
    InvoiceAttachment findInvoiceAttachment(Long invoiceId, String attachmentType, String attachmentFileName, String attachmentFileType);

    @Query("SELECT * FROM invoice_attachment a WHERE a.invoice_id = :invoiceId AND a.type = :attachmentType AND a.file_type = :attachmentFileType")
    InvoiceAttachment getInvoiceAttachment(Long invoiceId, String attachmentType, String attachmentFileType);

    @Query("DELETE FROM invoice_attachment WHERE invoice_id = :invoiceId")
    void deleteAttachments(Long invoiceId);

    /**
     * @param invoiceAttachment invoice file to be inserted. Abort if conflict, i.e. silently drop the insert
     * @return the id of the inserted invoice row
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insert(InvoiceAttachment invoiceAttachment);

    /**
     * @param invoiceAttachment updated invoice file. Replace on conflict, i.e, replace old data with the new one
     * @return number of updated row(S), should only be one for this method.
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    int update(InvoiceAttachment invoiceAttachment);

    /**
     * @param invoiceAttachment to be deleted
     */
    @Delete
    void delete(InvoiceAttachment invoiceAttachment);

}
