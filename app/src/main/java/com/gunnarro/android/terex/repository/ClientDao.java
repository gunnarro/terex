package com.gunnarro.android.terex.repository;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.gunnarro.android.terex.domain.entity.Client;
import com.gunnarro.android.terex.domain.entity.ClientWithProject;

import java.util.List;

@Dao
public interface ClientDao {


    /**
     * use transactions since this method return a aggregate object
     */
    @Transaction
    @Query("SELECT * FROM client WHERE id = :clientId")
    ClientWithProject getClientWithProjects(Long clientId);

    @Transaction
    @Query("SELECT * FROM client")
    List<ClientWithProject> getClientsWithProjects();

    @Query("SELECT * FROM client")
    List<Client> getClients();


    @Query("SELECT * FROM client c WHERE c.id = :clientId")
    Client getClient(long clientId);

    @Query("SELECT * FROM client c INNER JOIN company cmp ON c.company_id = cmp.id WHERE cmp.company_name = :name")
    Client findClient(String name);

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
