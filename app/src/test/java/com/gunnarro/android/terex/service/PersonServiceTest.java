package com.gunnarro.android.terex.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.gunnarro.android.terex.domain.dto.PersonDto;
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
    void savePerson_update() throws ExecutionException, InterruptedException {
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
        personDto.setSocialSecurityNumber("123456 12345");
        personDto.setDateOfBirth(LocalDate.of(1990, 3, 23));
        personDto.setMaritalStatus("U");
        personDto.setGender("M");
        return personDto;
    }

}
