package com.gunnarro.android.terex.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import com.gunnarro.android.terex.DbHelper;
import com.gunnarro.android.terex.config.AppDatabase;
import com.gunnarro.android.terex.domain.dto.BusinessAddressDto;
import com.gunnarro.android.terex.domain.dto.ContactInfoDto;
import com.gunnarro.android.terex.domain.dto.OrganizationDto;
import com.gunnarro.android.terex.domain.dto.PersonDto;
import com.gunnarro.android.terex.repository.AddressRepository;
import com.gunnarro.android.terex.repository.OrganizationRepository;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class OrganizationServiceTest {

    private OrganizationService organizationService;

    @Before
    public void setup() {
        Context appContext = ApplicationProvider.getApplicationContext();
        AppDatabase appDatabase = Room.inMemoryDatabaseBuilder(appContext, AppDatabase.class).build();
        AppDatabase.init(appContext);
        organizationService = new OrganizationService(new OrganizationRepository(appDatabase.organizationDao()), new AddressRepository(), new ContactInfoService(), new PersonService());
        // load test data
        List<String> sqlQueryList = DbHelper.readMigrationSqlQueryFile(appContext, "database/test_data.sql");
        sqlQueryList.forEach(query -> {
            System.out.println(query);
            appDatabase.getOpenHelper().getWritableDatabase().execSQL(query);
        });
        assertTrue(appDatabase.getOpenHelper().getWritableDatabase().isDatabaseIntegrityOk());
    }

    @Test
    public void getOrganization_not_found() {
        assertNull(organizationService.getOrganization(23L));
    }

    @Test
    public void saveOrganization_new() {
        OrganizationDto newOrganizationDto = createOrganizationDto();

        // create new organization
        Long orgId = organizationService.save(newOrganizationDto);
        assertEquals(2, orgId.intValue());

        // get organization
        OrganizationDto organizationDto = organizationService.getOrganization(orgId);
        assertEquals(orgId, organizationDto.getId());
        assertEquals("123456789", organizationDto.getOrganizationNumber());
        assertEquals("gunnarro sandbox as", organizationDto.getName());
        assertEquals("SOFTWARE", organizationDto.getIndustryType());
        assertEquals("23232323", organizationDto.getBankAccountNumber());
  /*      assertEquals("23232323", organizationDto.getContactInfo());
        assertEquals("23232323", organizationDto.getBusinessAddress());
        assertEquals("23232323", organizationDto.getContactPerson());
*/

        // update organisation, ie save same org twice without any changes and ensure that nor a duplicate is created
        orgId = organizationService.save(organizationDto);

        OrganizationDto updatedOrganizationDto = organizationService.getOrganization(orgId);
        assertEquals("123456789", updatedOrganizationDto.getOrganizationNumber());
        assertEquals("gunnarro sandbox as", organizationDto.getName());
        assertEquals("SOFTWARE", updatedOrganizationDto.getIndustryType());
        assertEquals("23232323", updatedOrganizationDto.getBankAccountNumber());
  /*      assertEquals("1", updatedOrganizationDto.getContactInfo().getId().toString());
        assertEquals("2", updatedOrganizationDto.getBusinessAddress().getId());
        assertEquals("2", updatedOrganizationDto.getContactPerson().getId());
*/
    }

    private OrganizationDto createOrganizationDto() {
        OrganizationDto organizationDto = new OrganizationDto();
        organizationDto.setName("gunnarro sandbox as");
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

        BusinessAddressDto organizationAddress = new BusinessAddressDto();
        organizationAddress.setStreetAddress("my-street 34");
        organizationAddress.setCity("oslo");
        organizationAddress.setCountry("norway");
        organizationAddress.setPostalCode("0467");

        organizationDto.setBusinessAddress(organizationAddress);
        organizationDto.setContactPerson(contectPersonDto);
        organizationDto.setContactInfo(contactInfoDto);
        return organizationDto;
    }
}
