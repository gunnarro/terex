package com.gunnarro.android.terex.repository;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.gunnarro.android.terex.domain.entity.Person;

@Dao
public interface PersonDao {

    @Query("SELECT * FROM person p WHERE p.full_name = :fullName")
    Person findPerson(String fullName);

    @Query("SELECT * FROM person p WHERE p.id = :personId")
    Person getPerson(Long personId);

    /**
     * @param person invoice file to be inserted. Abort if conflict, i.e. silently drop the insert
     * @return the id of the inserted invoice row
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insert(Person person);

    /**
     * @param person updated invoice file. Replace on conflict, i.e, replace old data with the new one
     * @return number of updated row(S), should only be one for this method.
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    int update(Person person);

    /**
     * @param person to be deleted
     */
    @Delete
    void delete(Person person);

}
