package com.gunnarro.android.terex.service;


import android.util.Log;

import com.gunnarro.android.terex.domain.dto.BusinessAddressDto;
import com.gunnarro.android.terex.domain.dto.OrganizationDto;
import com.gunnarro.android.terex.domain.dto.PersonDto;
import com.gunnarro.android.terex.domain.entity.Organization;
import com.gunnarro.android.terex.domain.mapper.TimesheetMapper;
import com.gunnarro.android.terex.exception.TerexApplicationException;
import com.gunnarro.android.terex.repository.OrganizationRepository;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final AddressService addressService;
    private final ContactInfoService contactInfoService;
    private final PersonService personService;

    /**
     * For unit test onlu
     */
    @Inject
    public OrganizationService(OrganizationRepository organizationRepository, AddressService addressService, ContactInfoService contactInfoService, PersonService personService) {
        this.organizationRepository = organizationRepository;
        this.addressService = addressService;
        this.contactInfoService = contactInfoService;
        this.personService = personService;
    }

    @Inject
    public OrganizationService() {
        this(new OrganizationRepository(), new AddressService(), new ContactInfoService(), new PersonService());
    }

    public OrganizationDto getOrganization(Long organizationId) {
        Organization organization = organizationRepository.getOrganization(organizationId);
        OrganizationDto organizationDto = null;
        if (organization != null) {
            organizationDto = TimesheetMapper.toOrganizationDto(organizationRepository.getOrganization(organizationId));
            BusinessAddressDto businessAddressDto = addressService.getBusinessAddress(organization.getBusinessAddressId());
            organizationDto.setBusinessAddress(businessAddressDto);
            PersonDto contactPersonDto = personService.getPerson(organization.getContactInfoId());
            organizationDto.setContactPerson(contactPersonDto);
        }
        return organizationDto;
    }

    public Long save(@NotNull final OrganizationDto organizationDto) {
        if (organizationDto.getName() == null || organizationDto.getOrganizationNumber() == null) {
            throw new TerexApplicationException("Save org error", "org missing name and/or org.nr!", null);
        }
        Organization organization = TimesheetMapper.fromOrganizationDto(organizationDto);
        if (organizationDto.getBusinessAddress() != null) {
            Long addressId = addressService.saveBusinessAddress(organizationDto.getBusinessAddress());
            organization.setBusinessAddressId(addressId);
        }

        if (organization.getContactInfoId() != null) {
            Long contactInfoId = contactInfoService.save(organizationDto.getContactPerson().getContactInfo());
            organization.setContactInfoId(contactInfoId);
        }
        Log.d("organizationService", "save: " + organization);
        return save(organization);
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    private Long save(@NotNull final Organization organization) {
        try {
            Organization organizationExisting;
            if (organization.getId() == null) {
                organizationExisting = organizationRepository.find(organization.getName());
            } else {
                organizationExisting = organizationRepository.getOrganization(organization.getId());
            }
            Log.d("saveOrganization", String.format("existing org: %s", organizationExisting));
            Long orgId;
            if (organizationExisting == null) {
                organization.setCreatedDate(LocalDateTime.now());
                organization.setLastModifiedDate(LocalDateTime.now());
                orgId = organizationRepository.insert(organization);
                Log.d("saveOrganization", String.format("inserted new organization: %s - %s ", orgId, organization.getName()));
            } else {
                organization.setId(organizationExisting.getId());
                organization.setCreatedDate(organizationExisting.getCreatedDate());
                organization.setLastModifiedDate(LocalDateTime.now());
                organizationRepository.update(organization);
                orgId = organization.getId();
                Log.d("saveOrganization", String.format("updated organization: %s - %s", orgId, organization.getName()));
            }
            return orgId;
        } catch (Exception e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("Error saving organization! " + e.getMessage(), "50050", e.getCause());
        }
    }
}
