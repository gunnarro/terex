package com.gunnarro.android.terex.repository;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.gunnarro.android.terex.domain.entity.Company;

import java.util.List;

@Dao
public interface CompanyDao {

    @Query("SELECT * FROM invoice ORDER BY billing_date ASC")
    LiveData<List<Company>> getAll();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Company company);

    @Update
    void update(Company company);

    @Delete
    void delete(Company company);
}
