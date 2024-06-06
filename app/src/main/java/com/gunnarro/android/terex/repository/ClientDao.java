package com.gunnarro.android.terex.repository;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.gunnarro.android.terex.domain.entity.Client;

import java.util.List;

/**
 * Room query example:
 * <a hraf="https://developer.android.com/training/data-storage/room/accessing-data">room dao query examples</a>
 * <p>
 * NB! Use transactions if method return a aggregate object
 */
@Dao
public interface ClientDao {

    @Query("SELECT client.id FROM client"
            + " INNER JOIN project ON project.client_id = client.id "
            + " INNER JOIN timesheet ON timesheet.project_id = project.id"
            + " WHERE timesheet.id = :timesheetId")
    Long getClientIdByTimesheetId(Long timesheetId);

    @Query("SELECT client.* FROM client"
            + " INNER JOIN project ON project.client_id = client.id "
            + " INNER JOIN timesheet ON timesheet.project_id = project.id"
            + " WHERE timesheet.id = :timesheetId")
    Client getClientByTimesheetId(Long timesheetId);

    @Query("SELECT id FROM client ORDER BY id ASC")
    List<Long> getAllClientIds();

    @Query("SELECT * FROM client ORDER BY name ASC")
    LiveData<List<Client>> getAllClients();

    @Query("SELECT * FROM client WHERE status = 'ACTIVE' limit 1")
    Client getActiveClient();

    @Transaction
    @Query("SELECT * FROM client WHERE id = :clientId")
    Client getClient(Long clientId);

    @Transaction
    @Query("SELECT * FROM client")
    List<Client> getClientList();

    @Query("SELECT * FROM client")
    List<Client> getClients();

    @Query("SELECT * FROM client c WHERE c.id = :clientId")
    Client getClient(long clientId);

    @Query("SELECT * FROM client c WHERE c.name = :name")
    Client findClientByName(String name);

    /**
     * @param client Client to be inserted. Abort if conflict, i.e. silently drop the insert
     * @return the id of the inserted invoice row
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insert(Client client);

    /**
     * @param client updated client. Replace on conflict, i.e, replace old data with the new one
     * @return number of updated row(S), should only be one for this method.
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    int update(Client client);

    /**
     * @param client to be deleted
     */
    @Delete
    void delete(Client client);

}
