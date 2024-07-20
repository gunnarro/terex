package com.gunnarro.android.terex.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gunnarro.android.terex.domain.dto.AddressDto;
import com.gunnarro.android.terex.domain.dto.ContactInfoDto;
import com.gunnarro.android.terex.domain.dto.PersonDto;
import com.gunnarro.android.terex.domain.entity.Person;
import com.gunnarro.android.terex.domain.mapper.TimesheetMapper;
import com.gunnarro.android.terex.repository.PersonRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.concurrent.ExecutionException;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    private PersonService personService;

    @Mock
    private PersonRepository personRepositoryMock;
    @Mock
    private ContactInfoService contactInfoServiceMock;

    @BeforeEach
    public void setup() {
        personService = new PersonService(personRepositoryMock, contactInfoServiceMock);
    }

    @Test
    void getPerson() {
        Person person = createPerson();
        person.setId(233L);
        person.setContactInfoId(78L);

        ContactInfoDto contactInfoDto = new ContactInfoDto();
        contactInfoDto.setId(person.getContactInfoId());
        contactInfoDto.setEmailAddress("my@gmail.com");
        contactInfoDto.setMobileNumberCountryCode("+47");
        contactInfoDto.setMobileNumber("22334455");

        when(personRepositoryMock.getPerson(anyLong())).thenReturn(person);
        when(contactInfoServiceMock.getContactInfo(anyLong())).thenReturn(contactInfoDto);

        PersonDto personDto = personService.getPerson(233L);
        assertEquals(person.getId(), personDto.getId());
        assertEquals("gunnar astor rønneberg", personDto.getFullName());
        assertEquals(person.getContactInfoId(), personDto.getContactInfo().getId());
        assertEquals("my@gmail.com", personDto.getContactInfo().getEmailAddress());
        assertEquals("+47", personDto.getContactInfo().getMobileNumberCountryCode());
        assertEquals("22334455", personDto.getContactInfo().getMobileNumber());
        assertNull(personDto.getAddress());
    }

    @Test
    void savePerson_new() throws ExecutionException, InterruptedException {
        PersonDto personDto = createPersonDto();

        when(personRepositoryMock.find(anyString())).thenReturn(null);
        when(personRepositoryMock.insert(any())).thenReturn(1000L);

        Long clientId = personService.save(personDto);

        assertEquals(1000L, clientId);

        verify(personRepositoryMock, times(1)).find(anyString());
        verify(personRepositoryMock, times(1)).insert(any());
    }

    @Test
    void savePerson_update() throws ExecutionException, InterruptedException {
        PersonDto personDto = createPersonDto();
        personDto.setId(101L);

        when(personRepositoryMock.getPerson(101L)).thenReturn(TimesheetMapper.fromPersonDto(personDto));

        Long personId = personService.save(personDto);
        assertEquals(101L, personId);

        verify(personRepositoryMock, times(1)).getPerson(101L);
        verify(personRepositoryMock, times(1)).update(any());
    }

    private PersonDto createPersonDto() {
        PersonDto personDto = new PersonDto();
        personDto.setFullName("gunnar astor ronneberg");
        ContactInfoDto contactInfoDto = new ContactInfoDto();
        contactInfoDto.setEmailAddress("my@gmail.com");
        contactInfoDto.setMobileNumberCountryCode("+47");
        contactInfoDto.setMobileNumber("34343434");
        personDto.setContactInfo(contactInfoDto);

        AddressDto addressDto = new AddressDto();
        addressDto.setStreetAddress("my-street 45");
        personDto.setAddress(addressDto);
        return personDto;
    }

    private Person createPerson() {
        Person person = new Person();
        person.setGender("M");
        person.setDateOfBirth(LocalDate.of(1970, 11, 10));
        person.setFullName("gunnar astor rønneberg");
        person.setContactInfoId(233L);
        person.setAddressId(55L);
        return person;
    }
}
