package com.gunnarro.android.terex.repository;

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

    @Query("SELECT * FROM company c order by c.id desc")
    List<Company> getCompanies();

    @Query("SELECT * FROM company c WHERE c.id = :companyId")
    Company getCompany(long companyId);

    @Query("SELECT * FROM company c WHERE c.company_name = :companyName")
    Company findCompany(String companyName);
    /**
     * @param company company to be inserted. Abort if conflict, i.e. silently drop the insert
     * @return the id of the inserted invoice row
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insert(Company company);

    /**
     * @param company company project. Replace on conflict, i.e, replace old data with the new one
     * @return number of updated row(S), should only be one for this method.
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    int update(Company company);

    /**
     * @param company to be deleted
     */
    @Delete
    void delete(Company company);
}
