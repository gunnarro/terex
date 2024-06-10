package com.gunnarro.android.terex.service;

import static org.junit.Assert.assertEquals;

import com.gunnarro.android.terex.IntegrationTestSetup;
import com.gunnarro.android.terex.domain.dto.PersonDto;
import com.gunnarro.android.terex.repository.PersonRepository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PersonServiceTest extends IntegrationTestSetup {

    private PersonService personService;

    @Before
    public void setup() {
        super.setupDatabase();
        personService = new PersonService(new PersonRepository(), new ContactInfoService());
    }

    @After
    public void cleanUp() {
        super.cleanUpDatabase();
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
