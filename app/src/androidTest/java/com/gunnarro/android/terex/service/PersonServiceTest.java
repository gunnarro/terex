package com.gunnarro.android.terex.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import com.gunnarro.android.terex.DbHelper;
import com.gunnarro.android.terex.config.AppDatabase;
import com.gunnarro.android.terex.domain.dto.PersonDto;
import com.gunnarro.android.terex.repository.PersonRepository;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class PersonServiceTest {

    private PersonService personService;

    @Before
    public void setup() {
        Context appContext = ApplicationProvider.getApplicationContext();
        AppDatabase appDatabase = Room.inMemoryDatabaseBuilder(appContext, AppDatabase.class).build();
        AppDatabase.init(appContext);
        personService = new PersonService(new PersonRepository(), new ContactInfoService());
        // load test data
        List<String> sqlQueryList = DbHelper.readMigrationSqlQueryFile(appContext, "database/test_data.sql");
        sqlQueryList.forEach(query -> {
            System.out.println(query);
            // appDatabase.getOpenHelper().getWritableDatabase().execSQL(query);
        });
        assertTrue(appDatabase.getOpenHelper().getWritableDatabase().isDatabaseIntegrityOk());
    }

    /**
     * some problem with sql test data
     *
     * @Test public void getPerson() {
     * PersonDto personDto = personService.getPerson(2L);
     * assertEquals("", personDto.getFullName());
     * }
     */
    @Test
    public void getPerson_not_found() {
        assertEquals(null, personService.getPerson(1000L));
    }

    @Test
    public void savePerson() {
        PersonDto newPersonDto = createPersonDto();

        Long personId = personService.save(newPersonDto);
        assertEquals(1, personId.intValue());

        PersonDto personDto = personService.getPerson(personId);
        assertEquals(1, personDto.getId().intValue());

        assertEquals("ole gunnar hansen", personDto.getFullName());

        // update and save person
        personDto.setFullName("Ole Gunnar Hansen");
        personId = personService.save(personDto);

        PersonDto updatedPersonDto = personService.getPerson(personId);
        assertEquals(1, updatedPersonDto.getId().intValue());
        assertEquals("Ole Gunnar Hansen", personDto.getFullName());
    }

    private PersonDto createPersonDto() {
        PersonDto personDto = new PersonDto();
        personDto.setFullName("ole gunnar hansen");
        return personDto;
    }
}
