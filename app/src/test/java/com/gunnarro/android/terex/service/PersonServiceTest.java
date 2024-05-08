package com.gunnarro.android.terex.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.gunnarro.android.terex.domain.dto.AddressDto;
import com.gunnarro.android.terex.domain.dto.ContactInfoDto;
import com.gunnarro.android.terex.domain.dto.PersonDto;
import com.gunnarro.android.terex.domain.mapper.TimesheetMapper;
import com.gunnarro.android.terex.repository.PersonRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.ExecutionException;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    private PersonService personService;

    @Mock
    private PersonRepository personRepositoryMock;

    @BeforeEach
    public void setup() {
        personService = new PersonService(personRepositoryMock);
    }

    @Test
    void savePerson_new() throws ExecutionException, InterruptedException {
        PersonDto personDto = createPersonDto();

        when(personRepositoryMock.findPerson(anyString(), anyString(), anyString())).thenReturn(null);
        when(personRepositoryMock.insert(any())).thenReturn(1000L);

        Long clientId = personService.save(personDto);

        assertEquals(1000L, clientId);
    }

    @Test
    void savePerson_update() {
        PersonDto personDto = createPersonDto();
        personDto.setId(101L);

        when(personRepositoryMock.getPerson(101L)).thenReturn(TimesheetMapper.fromPersonDto(personDto));

        Long personId = personService.save(personDto);
        assertEquals(101L, personId);
    }

    private PersonDto createPersonDto() {
        PersonDto personDto = new PersonDto();
        personDto.setFirstName("gunnar");
        personDto.setMiddleName("astor");
        personDto.setLastName("ronneberg");
        ContactInfoDto contactInfoDto = new ContactInfoDto();
        contactInfoDto.setEmailAddress("my@gmail.com");
        contactInfoDto.setMobileNumberCountryCode("+47");
        contactInfoDto.setMobileNumber("34343434");
        personDto.setContactInfo(contactInfoDto);

        AddressDto addressDto = new AddressDto();
        addressDto.setStreetName("my-street 45");
        personDto.setAddress(addressDto);
        return personDto;
    }

}
