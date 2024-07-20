package com.gunnarro.android.terex.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gunnarro.android.terex.TestData;
import com.gunnarro.android.terex.domain.dto.OrganizationDto;
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
    @Mock
    private AddressService addressServiceMock;

    @Mock
    private ContactInfoService contactInfoServiceMock;

    @Mock
    private PersonService personServiceMock;

    @BeforeEach
    public void setup() {
        organizationService = new OrganizationService(organizationRepositoryMock, addressServiceMock, contactInfoServiceMock, personServiceMock);
    }


    @Test
    void saveOrganization_new() throws ExecutionException, InterruptedException {
        OrganizationDto organizationDto = TestData.createOrganizationDto(null, "gunnarro as", "822 707 922");

        when(organizationRepositoryMock.find(anyString())).thenReturn(null);
        when(organizationRepositoryMock.insert(any())).thenReturn(1L);

        Long organizationId = organizationService.save(organizationDto);
        assertEquals(1L, organizationId);

        verify(organizationRepositoryMock, times(1)).find(organizationDto.getName());
        verify(organizationRepositoryMock, times(1)).insert(any());
    }

    @Test
    void saveOrganization_update() throws ExecutionException, InterruptedException {
        OrganizationDto organizationDto = TestData.createOrganizationDto(100L, "gunnarro as", "822 707 922");
        Organization existingOrganization = TimesheetMapper.fromOrganizationDto(organizationDto);
        when(organizationRepositoryMock.getOrganization(anyLong())).thenReturn(existingOrganization);
        when(organizationRepositoryMock.update(any())).thenReturn(1);

        Long organizationId = organizationService.save(organizationDto);
        assertEquals(100L, organizationId);

        verify(organizationRepositoryMock, times(1)).getOrganization(organizationDto.getId());
        verify(organizationRepositoryMock, times(1)).update(any());
    }

}
