package com.gunnarro.android.terex.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.gunnarro.android.terex.IntegrationTestSetup;
import com.gunnarro.android.terex.domain.dto.IntegrationDto;
import com.gunnarro.android.terex.repository.IntegrationRepository;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class IntegrationServiceTest extends IntegrationTestSetup {

    private IntegrationService integrationService;

    @Before
    public void setup() {
        super.setupDatabase();
        integrationService = new IntegrationService(new IntegrationRepository());
    }

    @After
    public void cleanUp() {
        super.cleanUpDatabase();
    }

    @Ignore
    @Test
    public void integration_CRUD_manual() {

        IntegrationDto integrationDto = new IntegrationDto();
        integrationDto.setId(44L);
        integrationDto.setSystem("BREG");
        integrationDto.setBaseUrl("https://breg.no");
        integrationDto.setServiceType("REST");
        integrationDto.setUserName("guro");
        integrationDto.setPassword("change-me");
        integrationDto.setSchemaUrl("https://breg.no/swagger");
        integrationDto.setAuthenticationType("PASSWORD");
        integrationDto.setAccessToken(null);

        // get
        assertNull(integrationService.getIntegration(1L));
        assertNull(integrationService.getIntegrationBySystem("BREG"));
        assertEquals(0, integrationService.getIntegrations().size());

        // create new
        Long id = integrationService.saveIntegration(integrationDto);
        IntegrationDto newIntegrationDto = integrationService.getIntegration(id);
        assertEquals("BREG", newIntegrationDto.getSystem());
        assertEquals("https://breg.no", newIntegrationDto.getBaseUrl());
        assertEquals("REST", newIntegrationDto.getServiceType());
        assertEquals("guro", newIntegrationDto.getUserName());
        assertEquals("change-me", newIntegrationDto.getPassword());
        assertEquals("https://breg.no/swagger", newIntegrationDto.getSchemaUrl());
        assertEquals("PASSWORD", newIntegrationDto.getAuthenticationType());
        assertNull(newIntegrationDto.getAccessToken());

        // update
        newIntegrationDto.setBaseUrl("http://breg.no/org");
        integrationService.saveIntegration(newIntegrationDto);
        IntegrationDto updatedIntegrationDto = integrationService.getIntegration(id);
        assertEquals("http://breg.no/org", updatedIntegrationDto.getBaseUrl());

        // delete
        integrationService.delete(id);

        assertEquals(0, integrationService.getIntegrations().size());
    }
}
