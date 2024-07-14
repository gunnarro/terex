package com.gunnarro.android.terex.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.gunnarro.android.terex.IntegrationTestSetup;
import com.gunnarro.android.terex.domain.dto.BusinessAddressDto;
import com.gunnarro.android.terex.domain.dto.ClientDto;
import com.gunnarro.android.terex.domain.dto.ContactInfoDto;
import com.gunnarro.android.terex.domain.dto.OrganizationDto;
import com.gunnarro.android.terex.domain.dto.PersonDto;
import com.gunnarro.android.terex.domain.entity.Client;
import com.gunnarro.android.terex.repository.ClientRepository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;


public class ClientServiceTest extends IntegrationTestSetup {

    private ClientService clientService;

    @Before
    public void setup() {
        super.setupDatabase();
        clientService = new ClientService(new ClientRepository(), new OrganizationService(), new ProjectService(), new PersonService());
    }

    @After
    public void cleanUp() {
        super.cleanUpDatabase();
    }

    @Test
    public void getClientByTimesheetId_not_found() {
        assertNull(clientService.getClientByTimesheetId(100L));
    }

    @Test
    public void getClient_not_found() {
        ClientDto clientDto = clientService.getClient(9876L);
        assertNull(clientDto);
    }

    @Test
    public void newAndUpdateClient() {
        ClientDto newClientDto = new ClientDto(null);
        OrganizationDto organizationDto = new OrganizationDto();
        organizationDto.setName("gunnarro unittest as");
        organizationDto.setOrganizationNumber("822707922");
        newClientDto.setOrganizationDto(organizationDto);
        newClientDto.setName(organizationDto.getName());
        newClientDto.setStatus(Client.ClientStatusEnum.ACTIVE.name());
        newClientDto.setContactPersonDto(createContactPerson());

        Long id = clientService.saveClient(newClientDto);
        assertNotNull(id);
        // check new
        ClientDto clientDto = clientService.getClient(id);
        assertEquals(id, clientDto.getId());
        assertEquals("gunnarro unittest as", clientDto.getName());
        assertEquals("ACTIVE", clientDto.getStatus());
        assertTrue(clientDto.getOrganizationDto().getId() > 0);
        assertEquals("822707922", clientDto.getOrganizationDto().getOrganizationNumber());
        assertTrue(clientDto.getContactPersonDto().getId() > 0);
        assertEquals("gunnar ronneberg", clientDto.getContactPersonDto().getFullName());
        assertTrue(clientDto.getContactPersonDto().getContactInfo().getId() > 0);
        assertEquals("gr@yahoo.org", clientDto.getContactPersonDto().getContactInfo().getEmailAddress());
        assertEquals("44556677", clientDto.getContactPersonDto().getContactInfo().getMobileNumber());
        assertEquals("+47", clientDto.getContactPersonDto().getContactInfo().getMobileNumberCountryCode());
        // update client status
        clientDto.setStatus(Client.ClientStatusEnum.DEACTIVATED.name());
        id = clientService.saveClient(clientDto);
        // check updated
        ClientDto updatedClientDto = clientService.getClient(id);
        assertEquals("gunnarro unittest as", updatedClientDto.getName());
        assertEquals("DEACTIVATED", updatedClientDto.getStatus());
    }

    // -------------------------
    // TEST DATA
    // ------------------------

    private Client createClient() {
        Client client = new Client();
        client.setId(1001L);
        client.setCreatedDate(LocalDateTime.now());
        client.setLastModifiedDate(LocalDateTime.now());
        client.setName("gunnarro as");
        client.setStatus("ACTIVE");
        return client;
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
        contectPersonDto.setFullName("ole hansen");

        BusinessAddressDto organizationAddress = new BusinessAddressDto();
        organizationAddress.setStreetAddress("my-street 120 e");

        organizationAddress.setCity("oslo");
        organizationAddress.setCountry("norway");
        organizationAddress.setPostalCode("0467");

        organizationDto.setBusinessAddress(organizationAddress);
        organizationDto.setContactPerson(contectPersonDto);
        organizationDto.setContactInfo(contactInfoDto);
        return organizationDto;
    }

    private PersonDto createContactPerson() {
        PersonDto contactPersonDto = new PersonDto();
        contactPersonDto.setFullName("gunnar ronneberg");
        ContactInfoDto contactInfoDto = new ContactInfoDto();
        contactInfoDto.setEmailAddress("gr@yahoo.org");
        contactInfoDto.setMobileNumberCountryCode("+47");
        contactInfoDto.setMobileNumber("44556677");
        contactPersonDto.setContactInfo(contactInfoDto);
        return contactPersonDto;
    }
}