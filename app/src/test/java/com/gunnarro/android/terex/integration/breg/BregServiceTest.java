package com.gunnarro.android.terex.integration.breg;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.gunnarro.android.terex.domain.dto.IntegrationDto;
import com.gunnarro.android.terex.domain.dto.OrganizationDto;
import com.gunnarro.android.terex.service.IntegrationService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BregServiceTest {

    @Mock
    private IntegrationService integrationServiceMock;

    @Test
    void findOrganization() {
        IntegrationDto integrationDto = new IntegrationDto();
        integrationDto.setBaseUrl("https://data.brreg.no/enhetsregisteret/api/enheter/");
        integrationDto.setReadTimeoutMs(5000L);
        integrationDto.setConnectionTimeoutMs(5000L);
        integrationDto.setHttpHeaderContentType("application/vnd.brreg.enhetsregisteret.enhet.v2+json;charset=UTF-8");
        when(integrationServiceMock.getIntegrationBySystem(anyString())).thenReturn(integrationDto);

        BregService bregService = new BregService(integrationServiceMock);
        OrganizationDto organizationDto = bregService.findOrganization("828707922");
        assertEquals("GUNNARRO AS", organizationDto.getName());
        assertFalse(organizationDto.getOrganizationStatusDto().isKonkurs());
        assertEquals("Norge", organizationDto.getBusinessAddress().getCountry());
        assertEquals("NO", organizationDto.getBusinessAddress().getCountryCode());
        assertEquals("OSLO", organizationDto.getBusinessAddress().getCity());
        assertEquals("Stavangergata 35", organizationDto.getBusinessAddress().getStreetAddress());
        //assertEquals("2023", organizationDto.getOrganizationStatusDto().getSisteInnsendteAarsregnskap());
    }
}
