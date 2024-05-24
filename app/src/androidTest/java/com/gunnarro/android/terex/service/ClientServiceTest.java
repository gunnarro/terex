package com.gunnarro.android.terex.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import com.gunnarro.android.terex.DbHelper;
import com.gunnarro.android.terex.config.AppDatabase;
import com.gunnarro.android.terex.domain.dto.BusinessAddressDto;
import com.gunnarro.android.terex.domain.dto.ClientDto;
import com.gunnarro.android.terex.domain.dto.ContactInfoDto;
import com.gunnarro.android.terex.domain.dto.OrganizationDto;
import com.gunnarro.android.terex.domain.dto.PersonDto;
import com.gunnarro.android.terex.domain.entity.Client;
import com.gunnarro.android.terex.exception.TerexApplicationException;
import com.gunnarro.android.terex.repository.ClientRepository;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;

public class ClientServiceTest {

    private ClientService clientService;

    @Before
    public void setup() {
        Context appContext = ApplicationProvider.getApplicationContext();
        AppDatabase appDatabase = Room.inMemoryDatabaseBuilder(appContext, AppDatabase.class).build();
        AppDatabase.init(appContext);
        clientService = new ClientService(new ClientRepository(), new OrganizationService(), new ProjectService(), new PersonService());
        // load test data
        List<String> sqlQueryList = DbHelper.readMigrationSqlQueryFile(appContext, "database/test_data.sql");
        sqlQueryList.forEach(query -> {
            System.out.println("DB test data sql query: : " + query);
            appDatabase.getOpenHelper().getWritableDatabase().execSQL(query);
        });
        Client client = new Client();
        client.setId(23L);
        client.setCreatedDate(LocalDateTime.now());
        client.setLastModifiedDate(LocalDateTime.now());
        client.setName("unit test");
        appDatabase.clientDao().insert(client);
        assertTrue(appDatabase.getOpenHelper().getWritableDatabase().isDatabaseIntegrityOk());
    }

    @Test(expected = TerexApplicationException.class)
    public void getClientByTimesheetId_not_found() {
        clientService.getClientByTimesheetId(100L);
    }
/*
    @Test
    public void getClientByTimesheetId() {
        ClientDto clientDto = clientService.getClientByTimesheetId(99L);
        assertEquals("", clientDto.getName());
    }
*/
    /**
     * fixme do not find the client in the test_data.sql file
     *
    @Test
    public void getClient() {
        ClientDto clientDto = clientService.getClient(23L);
        assertNotNull(clientDto);
        assertEquals("GUNNARRO AS", clientDto.getName());
    }
*/
    @Test
    public void getClient_not_found() {
        ClientDto clientDto = clientService.getClient(9876L);
        assertNull(clientDto);
    }

    @Test
    public void newAndUpdateClient() {
        ClientDto newClientDto = new ClientDto();
        OrganizationDto organizationDto = new OrganizationDto();
        organizationDto.setName("gunnarro unittest as");
        organizationDto.setOrganizationNumber("822707922");
        newClientDto.setOrganizationDto(organizationDto);
        newClientDto.setName(organizationDto.getName());
        newClientDto.setStatus(Client.ClientStatusEnum.ACTIVE.name());
        newClientDto.setCntactPersonDto(createContactPerson());

        Long id = clientService.saveClient(newClientDto);
        assertNotNull(id);
        // check new
        ClientDto clientDto = clientService.getClient(id);
        assertEquals(id, clientDto.getId());
        assertEquals("gunnarro unittest as", clientDto.getName());
        assertEquals("ACTIVE", clientDto.getStatus());
        assertEquals("1", clientDto.getOrganizationDto().getId().toString());
        assertEquals("822707922", clientDto.getOrganizationDto().getOrganizationNumber());
        assertEquals(1L, clientDto.getCntactPersonDto().getId().longValue());
        assertEquals("gunnar ronneberg", clientDto.getCntactPersonDto().getFullName());
        assertEquals(1L, clientDto.getCntactPersonDto().getContactInfo().getId().longValue());
        assertEquals("gr@yahoo.org", clientDto.getCntactPersonDto().getContactInfo().getEmailAddress());
        assertEquals("44556677", clientDto.getCntactPersonDto().getContactInfo().getMobileNumber());
        assertEquals("+47", clientDto.getCntactPersonDto().getContactInfo().getMobileNumberCountryCode());
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

    private ClientDto ClientDto() {
        ClientDto clientDto = new ClientDto();
        clientDto.setId(100L);
        clientDto.setName("gunnarro as");
        clientDto.setStatus("ACTIVE");
        clientDto.setOrganizationDto(createOrganizationDto());
        clientDto.setCntactPersonDto(createContactPerson());
        return clientDto;
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
