package com.gunnarro.android.terex.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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
        assertTrue(personId.intValue() > 0);

        PersonDto personDto = personService.getPerson(personId);
        assertTrue(personDto.getId().intValue() > 0);

        assertEquals("ole gunnar hansen", personDto.getFullName());

        // update and save person
        personDto.setFullName("Ole Gunnar Hansen");
        personId = personService.save(personDto);

        PersonDto updatedPersonDto = personService.getPerson(personId);
        assertEquals(personId, updatedPersonDto.getId());
        assertEquals("Ole Gunnar Hansen", personDto.getFullName());
        assertNull(personDto.getContactInfo());
    }

    private PersonDto createPersonDto() {
        PersonDto personDto = new PersonDto();
        personDto.setFullName("ole gunnar hansen");
        return personDto;
    }
}
