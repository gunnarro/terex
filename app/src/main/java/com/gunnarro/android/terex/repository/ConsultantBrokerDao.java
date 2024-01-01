package com.gunnarro.android.terex.repository;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.gunnarro.android.terex.domain.entity.ConsultantBroker;
import com.gunnarro.android.terex.domain.entity.ConsultantBrokerWithProject;
import com.gunnarro.android.terex.domain.entity.Project;

import java.util.List;
import java.util.Optional;

@Dao
public interface ConsultantBrokerDao {


    /**
     * use transactions since this method return a aggregate object
     */
    @Transaction
    @Query("SELECT * FROM consultant_broker WHERE id = :consultantBrokerId")
    ConsultantBrokerWithProject getConsultantBrokerWithProjects(Long consultantBrokerId);

    @Transaction
    @Query("SELECT * FROM consultant_broker")
    List<ConsultantBrokerWithProject> getConsultantBrokerWithProjects();

    @Query("SELECT * FROM consultant_broker")
    List<ConsultantBroker> getConsultantBrokers();


    @Query("SELECT * FROM consultant_broker b WHERE b.id = :consultantBrokerId")
    ConsultantBroker getConsultantBroker(long consultantBrokerId);

    @Query("SELECT * FROM consultant_broker b WHERE b.name = :name")
    ConsultantBroker findConsultantBroker(String name);

    /**
     * @param consultantBroker consultantBroker to be inserted. Abort if conflict, i.e. silently drop the insert
     * @return the id of the inserted invoice row
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insert(ConsultantBroker consultantBroker);

    /**
     * @param consultantBroker updated consultantBroker. Replace on conflict, i.e, replace old data with the new one
     * @return number of updated row(S), should only be one for this method.
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    int update(ConsultantBroker consultantBroker);

    /**
     * @param consultantBroker to be deleted
     */
    @Delete
    void delete(ConsultantBroker consultantBroker);
}
