package com.gunnarro.android.terex.service;


import com.gunnarro.android.terex.config.AppDatabase;
import com.gunnarro.android.terex.domain.dto.OrganizationDto;
import com.gunnarro.android.terex.domain.dto.PersonDto;
import com.gunnarro.android.terex.domain.entity.Address;
import com.gunnarro.android.terex.domain.entity.Organization;
import com.gunnarro.android.terex.domain.mapper.TimesheetMapper;
import com.gunnarro.android.terex.repository.AddressRepository;
import com.gunnarro.android.terex.repository.OrganizationRepository;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final AddressRepository addressRepository;
    private ContactInfoService contactInfoService;
    private PersonService personService;

    /**
     * For unit test onlu
     */
    @Inject
    public OrganizationService(OrganizationRepository organizationRepository, AddressRepository addressRepository, ContactInfoService contactInfoService, PersonService personService) {
        this.organizationRepository = organizationRepository;
        this.addressRepository = addressRepository;
        this.contactInfoService = contactInfoService;
        this.personService = personService;
    }

    @Inject
    public OrganizationService() {
        this(new OrganizationRepository(AppDatabase.getDatabase().organizationDao()), new AddressRepository(), new ContactInfoService(), new PersonService());
    }

    public OrganizationDto getOrganization(Long organizationId) {
        Organization organization = organizationRepository.getOrganization(organizationId);
        OrganizationDto organizationDto = null;
        if (organization != null ) {
            Address businessAddress = addressRepository.getAddress(organization.getBusinessAddressId());
            organizationDto = TimesheetMapper.toOrganizationDto(organizationRepository.getOrganization(organizationId));
            PersonDto contactPersonDto = personService.getPerson(organization.getContactInfoId());

            organizationDto.setBusinessAddress(TimesheetMapper.toABusinessAddressDto(businessAddress));
            organizationDto.setContactPerson(contactPersonDto);
        }
        return organizationDto;
    }

    public List<OrganizationDto> getOrganizations() {
        return TimesheetMapper.toOrganizationDtoList(organizationRepository.getOrganizations());
    }

    public Long save(@NotNull final OrganizationDto organizationDto) {
        Organization organization = TimesheetMapper.fromOrganizationDto(organizationDto);
        if (organizationDto.getBusinessAddress() != null) {
            Long addressId = addressRepository.save(TimesheetMapper.fromBusinessAddressDto(organizationDto.getBusinessAddress()));
            organization.setBusinessAddressId(addressId);
        }

        if (organization.getContactInfoId() != null) {
            Long contactInfoId = contactInfoService.save(organizationDto.getContactPerson().getContactInfo());
            organization.setContactInfoId(contactInfoId);
        }
        return organizationRepository.save(organization);
    }


}
