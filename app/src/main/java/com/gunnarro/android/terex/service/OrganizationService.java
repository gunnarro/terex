package com.gunnarro.android.terex.service;


import com.gunnarro.android.terex.config.AppDatabase;
import com.gunnarro.android.terex.domain.dto.OrganizationDto;
import com.gunnarro.android.terex.domain.entity.Address;
import com.gunnarro.android.terex.domain.entity.ContactInfo;
import com.gunnarro.android.terex.domain.entity.Organization;
import com.gunnarro.android.terex.domain.entity.Person;
import com.gunnarro.android.terex.domain.mapper.TimesheetMapper;
import com.gunnarro.android.terex.repository.AddressRepository;
import com.gunnarro.android.terex.repository.ContactInfoRepository;
import com.gunnarro.android.terex.repository.OrganizationRepository;
import com.gunnarro.android.terex.repository.PersonRepository;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final AddressRepository addressRepository;
    private ContactInfoRepository contactInfoRepository;
    private PersonRepository personRepository;
    /**
     * For unit test onlu
     */
    @Inject
    public OrganizationService(OrganizationRepository organizationRepository, AddressRepository addressRepository) {
        this.organizationRepository = organizationRepository;
        this.addressRepository = addressRepository;
    }

    @Inject
    public OrganizationService() {
        this.organizationRepository = new OrganizationRepository(AppDatabase.getDatabase().organizationDao());
        this.addressRepository = new AddressRepository();
    }

    public OrganizationDto getOrganization(Long organizationId) {
        Organization organization = organizationRepository.getOrganization(organizationId);
        Address businessAddress = addressRepository.getAddress(organization.getBusinessAddressId());
        OrganizationDto organizationDto = TimesheetMapper.toOrganizationDto(organizationRepository.getOrganization(organizationId));
        ContactInfo orgContactInfo = contactInfoRepository.getContactInfo(organization.getContactInfoId());
        Person contactPerson = personRepository.getPerson(organization.getContactPersonId());

        organizationDto.setBusinessAddress(TimesheetMapper.toABusinessAddressDto(businessAddress));
        organizationDto.setContactPerson(TimesheetMapper.toPersonDto(contactPerson));
        organizationDto.setContactInfo(TimesheetMapper.toContactInfoDto(orgContactInfo));
        return organizationDto;
    }

    public List<OrganizationDto> getOrganizations() {
        return TimesheetMapper.toOrganizationDtoList(organizationRepository.getOrganizations());
    }

    public Long save(@NotNull final OrganizationDto organizationDto) {
        Organization organization = TimesheetMapper.fromOrganizationDto(organizationDto);
        Long addressId = addressRepository.save(TimesheetMapper.fromBusinessAddressDto(organizationDto.getBusinessAddress()));
        organization.setBusinessAddressId(addressId);
        return organizationRepository.save(organization);
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    /*
    public Long saveDeprecated(@NotNull final OrganizationDto organizationDto) {
        Organization organization = TimesheetMapper.fromOrganizationDto(organizationDto);
        try {
            Organization organizationExisting = organizationRepository.findOrganization(organization.getName());
            Log.d("save organization", String.format("%s", organizationExisting));
            Long id;
            if (organizationExisting == null) {
                organization.setCreatedDate(LocalDateTime.now());
                organization.setLastModifiedDate(LocalDateTime.now());
                id = organizationRepository.insert(organization);
                Log.d("", "inserted new organization: " + id + " - " + organization.getName());
            } else {
                organization.setId(organizationExisting.getId());
                organization.setCreatedDate(organizationExisting.getCreatedDate());
                organization.setLastModifiedDate(LocalDateTime.now());
                organizationRepository.update(organization);
                id = organization.getId();
                Log.d("", "updated organization: " + id + " - " + organization.getName());
            }
            return id;
        } catch (Exception e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("Error saving organization! " + e.getMessage(), "50050", e.getCause());
        }
    }*/
}
