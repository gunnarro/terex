package com.gunnarro.android.terex.repository;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.gunnarro.android.terex.domain.entity.Integration;

import java.util.List;

@Dao
public interface IntegrationDao {

    @Query("SELECT * FROM integration i WHERE i.system = :system")
    Integration find(String system);

    @Query("SELECT * FROM integration i WHERE i.id = :id")
    Integration getIntegration(Long id);

    @Query("SELECT * FROM integration")
    List<Integration> getIntegrations();

    @Query("SELECT * FROM integration")
    LiveData<List<Integration>> getIntegrationsLiveData();

    /**
     * @param integration integration to be inserted. Abort if conflict, i.e. silently drop the insert
     * @return the id of the inserted integration row
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insert(Integration integration);

    /**
     * @param integration updated integration. Replace on conflict, i.e, replace old data with the new one
     * @return number of updated row(S), should only be one for this method.
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    int update(Integration integration);

    /**
     * @param integration to be deleted
     */
    @Delete
    void delete(Integration integration);

}
