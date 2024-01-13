package com.gunnarro.android.terex.repository;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.gunnarro.android.terex.domain.entity.ContactInfo;

@Dao
public interface ContactInfoDao {

    @Query("SELECT * FROM contact_info c WHERE c.id = :contactId")
    ContactInfo findContactInfo(Long contactId);

    @Query("SELECT * FROM contact_info c WHERE c.id = :contactId")
    ContactInfo getContactInfo(Long contactId);

    /**
     * @param contactInfo invoice file to be inserted. Abort if conflict, i.e. silently drop the insert
     * @return the id of the inserted invoice row
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insert(ContactInfo contactInfo);

    /**
     * @param contactInfo updated invoice file. Replace on conflict, i.e, replace old data with the new one
     * @return number of updated row(S), should only be one for this method.
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    int update(ContactInfo contactInfo);

    /**
     * @param contactInfo to be deleted
     */
    @Delete
    void delete(ContactInfo contactInfo);

}
