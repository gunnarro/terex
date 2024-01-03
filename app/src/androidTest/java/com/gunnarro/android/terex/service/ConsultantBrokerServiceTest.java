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
import com.gunnarro.android.terex.domain.dto.ConsultantBrokerDto;
import com.gunnarro.android.terex.repository.ConsultantBrokerRepository;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class ConsultantBrokerServiceTest {

    private ConsultantBrokerService consultantBrokerService;

    @Before
    public void setup() {
        Context appContext = ApplicationProvider.getApplicationContext();
        AppDatabase appDatabase = Room.inMemoryDatabaseBuilder(appContext, AppDatabase.class).build();
        AppDatabase.init(appContext);
        consultantBrokerService = new ConsultantBrokerService(new ConsultantBrokerRepository());
        // load test data
        List<String> sqlQueryList = DbHelper.readMigrationSqlQueryFile(appContext, "database/test_data.sql");
        sqlQueryList.forEach(query -> {
            System.out.println(query);
            appDatabase.getOpenHelper().getWritableDatabase().execSQL(query);
        });
        assertTrue(appDatabase.getOpenHelper().getWritableDatabase().isDatabaseIntegrityOk());
    }

    @Test
    public void getConsultantBroker() {
        ConsultantBrokerDto consultantBrokerDto = consultantBrokerService.getConsultantBroker(23L);
        assertNull(consultantBrokerDto);
    }

    @Test
    public void newAndUpdateConsultantBroker() {
        ConsultantBrokerDto newConsultantBrokerDto = new ConsultantBrokerDto();
        newConsultantBrokerDto.setName("gunnarro consulting broker");
        newConsultantBrokerDto.setStatus("ACTIVE");
        Long id = consultantBrokerService.saveConsultantBroker(newConsultantBrokerDto);
        assertNotNull(id);
        // check new
        ConsultantBrokerDto consultantBrokerDto = consultantBrokerService.getConsultantBroker(id);
        assertEquals(id, consultantBrokerDto.getId());
        assertEquals("gunnarro consulting broker", consultantBrokerDto.getName());
        assertEquals("ACTIVE", consultantBrokerDto.getStatus());
        // update
        ConsultantBrokerDto updateConsultantBrokerDto = new ConsultantBrokerDto();
        updateConsultantBrokerDto.setId(consultantBrokerDto.getId());
        updateConsultantBrokerDto.setName("updated gunnarro consulting broker");
        updateConsultantBrokerDto.setStatus("DEACTIVATED");
        id = consultantBrokerService.saveConsultantBroker(updateConsultantBrokerDto);
        // check updated
        ConsultantBrokerDto updatedConsultantBrokerDto = consultantBrokerService.getConsultantBroker(id);
        assertEquals("updated gunnarro consulting broker", updatedConsultantBrokerDto.getName());
        assertEquals("ACTIVE", updatedConsultantBrokerDto.getStatus());

    }
}
