package com.gunnarro.android.terex.repository;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.gunnarro.android.terex.domain.entity.Address;

@Dao
public interface AddressDao {

    @Query("SELECT * FROM address a WHERE a.street_address = :streetAddress")
    Address findAddress(String streetAddress);

    @Query("SELECT * FROM address a WHERE a.id = :addressId")
    Address getAddress(Long addressId);

    /**
     * @param address invoice file to be inserted. Abort if conflict, i.e. silently drop the insert
     * @return the id of the inserted invoice row
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insert(Address address);

    /**
     * @param address updated invoice file. Replace on conflict, i.e, replace old data with the new one
     * @return number of updated row(S), should only be one for this method.
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    int update(Address address);

    /**
     * @param address to be deleted
     */
    @Delete
    void delete(Address address);

}
