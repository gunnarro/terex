package com.gunnarro.android.terex.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.gunnarro.android.terex.domain.dto.AddressDto;
import com.gunnarro.android.terex.domain.dto.ContactInfoDto;
import com.gunnarro.android.terex.domain.dto.OrganizationDto;
import com.gunnarro.android.terex.domain.dto.PersonDto;
import com.gunnarro.android.terex.domain.entity.Organization;
import com.gunnarro.android.terex.domain.mapper.TimesheetMapper;
import com.gunnarro.android.terex.repository.OrganizationRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.ExecutionException;

@ExtendWith(MockitoExtension.class)
class OrganizationServiceTest {

    private OrganizationService organizationService;

    @Mock
    private OrganizationRepository organizationRepositoryMock;

    @BeforeEach
    public void setup() {
        organizationService = new OrganizationService(organizationRepositoryMock);
    }


    @Test
    void saveOrganization_new() throws ExecutionException, InterruptedException {
        OrganizationDto organizationDto = createOrganizationDto();

        when(organizationRepositoryMock.findOrganization(anyString())).thenReturn(null);
        when(organizationRepositoryMock.insert(any())).thenReturn(1L);

        Long projectId = organizationService.save(organizationDto);
        assertEquals(1L, projectId);
    }

    @Test
    void saveOrganization_update() throws ExecutionException, InterruptedException {
        OrganizationDto organizationDto = createOrganizationDto();
        organizationDto.setId(100L);
        Organization existingOrganization = TimesheetMapper.fromOrganizationDto(organizationDto);
        when(organizationRepositoryMock.findOrganization(anyString())).thenReturn(existingOrganization);

        Long projectId = organizationService.save(organizationDto);
        assertEquals(100L, projectId);
    }

    private OrganizationDto createOrganizationDto() {
        OrganizationDto organizationDto = new OrganizationDto();
        organizationDto.setName("gunnarro");
        organizationDto.setOrganizationNumber("123456789");
        organizationDto.setBankAccountNumber("23232323");
        organizationDto.setIndustryType("SOFTWARE");

        ContactInfoDto contactInfoDto = new ContactInfoDto();
        contactInfoDto.setEmailAddress("my@org.com");
        contactInfoDto.setMobileNumberCountryCode("+47");
        contactInfoDto.setMobileNumber("22334455");

        PersonDto contectPersonDto = new PersonDto();
        contectPersonDto.setFirstName("ole");
        contectPersonDto.setLastName("hansen");

        AddressDto organizationAddress = new AddressDto();
        organizationAddress.setStreetName("my-street");
        organizationAddress.setStreetNumber("23");
        organizationAddress.setStreetNumberPrefix("b");
        organizationAddress.setCity("oslo");
        organizationAddress.setCountry("norway");
        organizationAddress.setPostCode("0467");

        organizationDto.setAddress(organizationAddress);
        organizationDto.setContactPerson(contectPersonDto);
        organizationDto.setContactInfo(contactInfoDto);
        return organizationDto;
    }
}
