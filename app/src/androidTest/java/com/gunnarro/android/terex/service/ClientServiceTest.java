package com.gunnarro.android.terex.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import com.gunnarro.android.terex.DbHelper;
import com.gunnarro.android.terex.config.AppDatabase;
import com.gunnarro.android.terex.domain.dto.ClientDto;
import com.gunnarro.android.terex.repository.ClientRepository;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

public class ClientServiceTest {

    private ClientService clientService;

    @Before
    public void setup() {
        Context appContext = ApplicationProvider.getApplicationContext();
        AppDatabase appDatabase = Room.inMemoryDatabaseBuilder(appContext, AppDatabase.class).build();
        AppDatabase.init(appContext);
        clientService = new ClientService(new ClientRepository());
        // load test data
        List<String> sqlQueryList = DbHelper.readMigrationSqlQueryFile(appContext, "database/test_data.sql");
        sqlQueryList.forEach(query -> {
            System.out.println(query);
            appDatabase.getOpenHelper().getWritableDatabase().execSQL(query);
        });
        assertTrue(appDatabase.getOpenHelper().getWritableDatabase().isDatabaseIntegrityOk());
    }

    @Test
    public void getClient() {
        ClientDto clientDto = clientService.getClient(23L);
        assertNull(clientDto);
    }

    @Ignore
    @Test
    public void newAndUpdateClient() {
        ClientDto newClientDto = new ClientDto();
        newClientDto.getOrganizationDto().setName("gunnarro:as");
        newClientDto.setStatus("ACTIVE");
        Long id = clientService.saveClient(newClientDto);
        assertNotNull(id);
        // check new
        ClientDto clientDto = clientService.getClient(id);
        assertEquals(id, clientDto.getId());
       // assertEquals("gunnarro:as", clientDto.getName());
        assertEquals("ACTIVE", clientDto.getStatus());
        // update
        ClientDto updateClientDto = new ClientDto();
        updateClientDto.setId(clientDto.getId());
        //updateClientDto.setName("updated gunnarro:as");
        updateClientDto.setStatus("DEACTIVATED");
        id = clientService.saveClient(updateClientDto);
        // check updated
        ClientDto updatedClientDto = clientService.getClient(id);
        //assertEquals("updated gunnarro:as", updatedClientDto.getName());
        assertEquals("ACTIVE", updatedClientDto.getStatus());
    }
}
