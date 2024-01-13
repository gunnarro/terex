package com.gunnarro.android.terex.repository;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.gunnarro.android.terex.domain.entity.Organization;

import java.util.List;

@Dao
public interface OrganizationDao {

    @Query("SELECT * FROM organization o order by o.organization_name desc")
    List<Organization> getOrganizations();

    @Query("SELECT * FROM organization c WHERE c.id = :organizationId")
    Organization getOrganization(long organizationId);

    @Query("SELECT * FROM organization o WHERE  o.organization_name = :organizationName")
    Organization findOrganization(String organizationName);
    /**
     * @param organization to be inserted. Abort if conflict, i.e. silently drop the insert
     * @return the id of the inserted invoice row
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insert(Organization organization);

    /**
     * @param organization Replace on conflict, i.e, replace old data with the new one
     * @return number of updated row(S), should only be one for this method.
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    int update(Organization organization);

    /**
     * @param organization to be deleted
     */
    @Delete
    void delete(Organization organization);
}
