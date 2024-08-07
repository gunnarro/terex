package com.gunnarro.android.terex.service;


import android.util.Log;

import com.gunnarro.android.terex.domain.dto.ContactInfoDto;
import com.gunnarro.android.terex.domain.dto.PersonDto;
import com.gunnarro.android.terex.domain.entity.Person;
import com.gunnarro.android.terex.domain.mapper.TimesheetMapper;
import com.gunnarro.android.terex.exception.TerexApplicationException;
import com.gunnarro.android.terex.repository.PersonRepository;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PersonService {

    private final PersonRepository personRepository;
    private final ContactInfoService contactInfoService;

    /**
     * For unit test only
     */
    @Inject
    public PersonService(PersonRepository personRepository, ContactInfoService contactInfoService) {
        this.personRepository = personRepository;
        this.contactInfoService = contactInfoService;
    }

    @Inject
    public PersonService() {
        this(new PersonRepository(), new ContactInfoService());
    }

    public PersonDto getPerson(Long personId) {
        Person person = personRepository.getPerson(personId);
        PersonDto personDto = TimesheetMapper.toPersonDto(person);
        if (person != null && person.getContactInfoId() != null) {
            ContactInfoDto contactInfoDto = contactInfoService.getContactInfo(person.getContactInfoId());
            personDto.setContactInfo(contactInfoDto);
        }
        return personDto;
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public Long save(@NotNull final PersonDto personDto) {
        if (personDto.hasContactInformation()) {
            Long contactInformationId =  contactInfoService.save(personDto.getContactInfo());
            personDto.getContactInfo().setId(contactInformationId);
        }

        Person person = TimesheetMapper.fromPersonDto(personDto);
        try {
            Person personExisting;
            if (person.getId() == null) {
                personExisting = personRepository.find(person.getFullName());
            } else {
                personExisting = personRepository.getPerson(person.getId());
            }
            Log.d("savePerson", String.format("existingPerson=%s", personExisting));
            Long id;
            if (personExisting == null) {
                person.setCreatedDate(LocalDateTime.now());
                person.setLastModifiedDate(LocalDateTime.now());
                id = personRepository.insert(person);
                Log.d("savePerson", String.format("inserted new person, id= %s - %s", id, person));
            } else {
                person.setId(personExisting.getId());
                person.setCreatedDate(personExisting.getCreatedDate());
                person.setLastModifiedDate(LocalDateTime.now());
                personRepository.update(person);
                id = person.getId();
                Log.d("savePerson", String.format("updated person, Id= %s - %s", id, person));
            }
            return id;
        } catch (Exception e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("Error saving organization! " + e.getMessage(), "50050", e.getCause());
        }
    }
}
