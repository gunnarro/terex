package com.gunnarro.android.terex.repository;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.gunnarro.android.terex.domain.entity.Organization;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrganizationRepositoryTest {

    private OrganizationRepository organizationRepository;

    @Mock
    private OrganizationDao organizationDaoMock;

    @BeforeEach
    public void setup() {
        organizationRepository = new OrganizationRepository(organizationDaoMock);
    }


    @Test
    void saveOrganization_new() {
        Organization organization = createOrganization(null);

        when(organizationDaoMock.findOrganization(anyString())).thenReturn(null);
        when(organizationDaoMock.insert(any())).thenReturn(1L);

        Long organizationId = organizationRepository.save(organization);
        assertEquals(1L, organizationId);
    }

    @Test
    void saveOrganization_update() {
        Organization organization = createOrganization(100L);
        Organization existingOrganization = createOrganization(100L);
        when(organizationDaoMock.findOrganization(anyString())).thenReturn(existingOrganization);

        Long organizationId = organizationRepository.save(organization);
        assertEquals(100L, organizationId);
    }

    private Organization createOrganization(Long id) {
        Organization organization = new Organization();
        if (id != null) {
            organization.setId(id);
        }
        organization.setName("gunnarro");
        organization.setOrganizationNumber("123456789");
        organization.setBankAccountNumber("23232323");
        organization.setBusinessAddressId(10L);
        organization.setPostalAddressId(11L);
        organization.setContactInfoId(22L);
        organization.setContactPersonId(44L);
        //organization.setIndustryType("SOFTWARE");
        return organization;
    }
}
