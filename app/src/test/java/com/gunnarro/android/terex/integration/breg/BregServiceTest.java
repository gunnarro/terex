package com.gunnarro.android.terex.integration.breg;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.gunnarro.android.terex.domain.dto.OrganizationDto;

import org.junit.jupiter.api.Test;

class BregServiceTest {

    @Test
    void findOrganization() {
        BregService bregService = new BregService();
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
