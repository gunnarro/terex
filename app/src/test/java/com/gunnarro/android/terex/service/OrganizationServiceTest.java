package com.gunnarro.android.terex.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.gunnarro.android.terex.TestData;
import com.gunnarro.android.terex.domain.dto.OrganizationDto;
import com.gunnarro.android.terex.domain.entity.Organization;
import com.gunnarro.android.terex.domain.mapper.TimesheetMapper;
import com.gunnarro.android.terex.repository.AddressRepository;
import com.gunnarro.android.terex.repository.OrganizationRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.ExecutionException;

@Disabled
@ExtendWith(MockitoExtension.class)
class OrganizationServiceTest {

    private OrganizationService organizationService;

    @Mock
    private OrganizationRepository organizationRepositoryMock;
    @Mock
    private AddressRepository addressRepositoryMock;

    @Mock
    private ContactInfoService contactInfoServiceMock;

    @Mock
    private PersonService personServiceMock;

    @BeforeEach
    public void setup() {
        organizationService = new OrganizationService(organizationRepositoryMock, addressRepositoryMock, contactInfoServiceMock, personServiceMock);
    }


    @Test
    void saveOrganization_new() throws ExecutionException, InterruptedException {
        OrganizationDto organizationDto = TestData.createOrganizationDto(null, "gunnarro as", "822 707 922");

        when(organizationRepositoryMock.findOrganization(anyString())).thenReturn(null);
        when(organizationRepositoryMock.insert(any())).thenReturn(1L);

        Long organizationId = organizationService.save(organizationDto);
        assertEquals(1L, organizationId);
    }

    @Test
    void saveOrganization_update() {
        OrganizationDto organizationDto = TestData.createOrganizationDto(100L, "gunnarro as", "822 707 922");
        Organization existingOrganization = TimesheetMapper.fromOrganizationDto(organizationDto);
        when(organizationRepositoryMock.findOrganization(anyString())).thenReturn(existingOrganization);

        Long organizationId = organizationService.save(organizationDto);
        assertEquals(100L, organizationId);
    }

}
