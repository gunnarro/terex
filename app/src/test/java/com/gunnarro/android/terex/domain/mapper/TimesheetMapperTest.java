package com.gunnarro.android.terex.domain.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.gunnarro.android.terex.TestData;
import com.gunnarro.android.terex.domain.dto.AddressDto;
import com.gunnarro.android.terex.domain.dto.BusinessAddressDto;
import com.gunnarro.android.terex.domain.dto.ClientDto;
import com.gunnarro.android.terex.domain.dto.ContactInfoDto;
import com.gunnarro.android.terex.domain.dto.OrganizationDto;
import com.gunnarro.android.terex.domain.dto.PersonDto;
import com.gunnarro.android.terex.domain.dto.PostalAddressDto;
import com.gunnarro.android.terex.domain.dto.ProjectDto;
import com.gunnarro.android.terex.domain.dto.TimesheetDto;
import com.gunnarro.android.terex.domain.dto.TimesheetEntryDto;
import com.gunnarro.android.terex.domain.dto.TimesheetSummaryDto;
import com.gunnarro.android.terex.domain.dto.UserAccountDto;
import com.gunnarro.android.terex.domain.entity.Address;
import com.gunnarro.android.terex.domain.entity.Client;
import com.gunnarro.android.terex.domain.entity.ContactInfo;
import com.gunnarro.android.terex.domain.entity.Organization;
import com.gunnarro.android.terex.domain.entity.Person;
import com.gunnarro.android.terex.domain.entity.Project;
import com.gunnarro.android.terex.domain.entity.Timesheet;
import com.gunnarro.android.terex.domain.entity.TimesheetEntry;
import com.gunnarro.android.terex.domain.entity.TimesheetSummary;
import com.gunnarro.android.terex.domain.entity.UserAccount;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

class TimesheetMapperTest {

    @Test
    void toTimesheetDto() {
        Timesheet timesheet = Timesheet.createDefault(100L, 200L, 2024, 5);
        TimesheetDto timesheetDto = TimesheetMapper.toTimesheetDto(timesheet);
        assertEquals(timesheet.getId(), timesheetDto.getId());
        assertEquals(timesheet.getProjectId(), timesheetDto.getProjectDto().getId());
        assertEquals(timesheet.getUserId(), timesheetDto.getUserAccountDto().getId());
        assertEquals(timesheet.getYear(), timesheetDto.getYear());
        assertEquals(timesheet.getMonth(), timesheetDto.getMonth());
        assertEquals(timesheet.getStatus(), timesheetDto.getStatus());
    }

    @Test
    void fromTimesheetDto() {
        TimesheetDto timesheetDto = new TimesheetDto();
        timesheetDto.setId(22L);
        ProjectDto projectDto = new ProjectDto();
        projectDto.setId(666L);
        timesheetDto.setProjectDto(projectDto);
        UserAccountDto userAccountDto = new UserAccountDto();
        userAccountDto.setId(11L);
        timesheetDto.setUserAccountDto(userAccountDto);
        timesheetDto.setStatus("COMPLETED");
        timesheetDto.setYear(2024);
        timesheetDto.setMonth(7);
        Timesheet timesheet = TimesheetMapper.fromTimesheetDto(timesheetDto);
        assertEquals(timesheetDto.getId(), timesheet.getId());
        assertEquals(timesheetDto.getProjectDto().getId(), timesheet.getProjectId());
        assertEquals(timesheetDto.getUserAccountDto().getId(), timesheet.getUserId());
        assertEquals(timesheetDto.getYear(), timesheet.getYear());
        assertEquals(timesheetDto.getMonth(), timesheet.getMonth());
        assertEquals(timesheetDto.getStatus(), timesheet.getStatus());
    }

    @Test
    void toTimesheetEntryDto() {
        LocalDate localDate = LocalDate.of(2023, 12, 1);
        List<TimesheetEntry> timesheetEntryList = TestData.generateTimesheetEntries(localDate.getYear(), localDate.getMonthValue(), List.of(), List.of());

        TimesheetEntryDto timesheetEntryDto = TimesheetMapper.toTimesheetEntryDto(timesheetEntryList.get(0));
        assertEquals("2023-12-01", timesheetEntryDto.getWorkdayDate().toString());
        assertEquals("REGULAR", timesheetEntryDto.getType());
        assertEquals("08:00", timesheetEntryDto.getFromTime().toString());
        assertEquals("15:30", timesheetEntryDto.getToTime().toString());
        assertEquals("7.5", timesheetEntryDto.getWorkedHours());
        assertNull(timesheetEntryDto.getComments());
    }

    @Test
    void toTimesheetEntryDtoList() {
        LocalDate localDate = LocalDate.of(2023, 12, 1);
        List<TimesheetEntry> timesheetEntryList = TestData.generateTimesheetEntries(localDate.getYear(), localDate.getMonthValue(), List.of(), List.of());

        List<TimesheetEntryDto> timesheetEntryDtoList = TimesheetMapper.toTimesheetEntryDtoList(timesheetEntryList);
        assertEquals(21, timesheetEntryDtoList.size());
    }

    @Test
    void toTimesheetSummaryDto() {
        LocalDate localDate = LocalDate.of(2023, 12, 1);
        List<TimesheetSummary> timesheetSummaryList = TestData.buildTimesheetSummaryByWeek(23L, localDate.getYear(), localDate.getMonthValue(), 1250);

        TimesheetSummaryDto timesheetWeekSummaryDto = TimesheetMapper.toTimesheetSummaryDto(timesheetSummaryList.get(0));
        assertEquals(23, timesheetWeekSummaryDto.getTimesheetId());
        assertEquals("WEEK", timesheetWeekSummaryDto.getSummedByPeriod());
        assertEquals("01.12", timesheetWeekSummaryDto.getFromDateDDMM());
        assertEquals("9375.00", timesheetWeekSummaryDto.getTotalBilledAmount());
        assertEquals("12", timesheetWeekSummaryDto.getFromDateMM());
        assertEquals("48", timesheetWeekSummaryDto.getWeekInYear().toString());
        assertEquals("7.5", timesheetWeekSummaryDto.getTotalWorkedHours());
        assertEquals(1, timesheetWeekSummaryDto.getTotalWorkedDays());
        assertEquals(0, timesheetWeekSummaryDto.getTotalSickLeaveDays());
        assertEquals(0, timesheetWeekSummaryDto.getTotalVacationDays());
    }

    @Test
    void toTimesheetSummaryDtoList() {
        LocalDate localDate = LocalDate.of(2023, 12, 1);
        List<TimesheetSummary> timesheetSummaryList = TestData.buildTimesheetSummaryByWeek(23L, localDate.getYear(), localDate.getMonthValue(), 1250);

        List<TimesheetSummaryDto> timesheetSummaryDtoList = TimesheetMapper.toTimesheetSummaryDtoList(timesheetSummaryList);
        assertEquals(5, timesheetSummaryDtoList.size());
    }

    @Test
    void toProjectDto() {
        Project project = new Project();
        project.setName("gunnarro timesheet project");
        project.setId(1L);
        project.setClientId(1001L);
        project.setDescription("develop a timesheet app");
        project.setStatus(Project.ProjectStatusEnum.ACTIVE.name());
        project.setHourlyRate(1200);

        ProjectDto projectDto = TimesheetMapper.toProjectDto(project);

        assertEquals(1, projectDto.getId().intValue());
        assertEquals(1001, projectDto.getClientId().intValue());
        assertEquals("gunnarro timesheet project", projectDto.getName());
        assertEquals("develop a timesheet app", projectDto.getDescription());
        assertEquals(1200, projectDto.getHourlyRate());
        assertEquals(Project.ProjectStatusEnum.ACTIVE.name(), projectDto.getStatus());
        assertNull(projectDto.getTimesheetDto());
    }

    @Test
    void fromProjectDto() {
        ProjectDto projectDto = new ProjectDto();
        projectDto.setClientId(1001L);
        projectDto.setName("gunnarro timesheet project");
        projectDto.setId(1L);
        projectDto.setDescription("develop a timesheet app");
        projectDto.setStatus(Project.ProjectStatusEnum.ACTIVE.name());
        projectDto.setHourlyRate(1200);

        Project project = TimesheetMapper.fromProjectDto(projectDto);

        assertEquals(projectDto.getId(), project.getId().intValue());
        assertEquals(projectDto.getClientId(), project.getClientId().intValue());
        assertEquals(projectDto.getName(), project.getName());
        assertEquals(projectDto.getDescription(), project.getDescription());
        assertEquals(projectDto.getStatus(), project.getStatus());
        assertEquals(projectDto.getHourlyRate(), project.getHourlyRate());
    }

    @Test
    void toClientDtoList() {
        List<Client> clients = new ArrayList<Client>();
        clients.add(createClient(33L, "gunnarro as"));
        List<ClientDto> clientDtos = TimesheetMapper.toClientDtoList(clients);
        assertEquals(1, clientDtos.size());
        assertEquals(33, clientDtos.get(0).getId());
        assertEquals("ACTIVE", clientDtos.get(0).getStatus());
        assertEquals("gunnarro as", clientDtos.get(0).getName());
        assertEquals(100, clientDtos.get(0).getOrganizationDto().getId());
        assertEquals(900L, clientDtos.get(0).getContactPersonDto().getId());
    }

    @Test
    void toClientDtoList_empty() {
        List<ClientDto> clientDtos = TimesheetMapper.toClientDtoList(new ArrayList<>());
        assertTrue(clientDtos.isEmpty());

        clientDtos = TimesheetMapper.toClientDtoList(null);
        assertTrue(clientDtos.isEmpty());
    }

    @Test
    void fromClientDto() {
        ClientDto clientDto = new ClientDto();
        clientDto.setId(23L);
        OrganizationDto orgDto = new OrganizationDto();
        orgDto.setOrganizationNumber("828707933");
        clientDto.setOrganizationDto(orgDto);
        clientDto.setName("gunnarro as");
        clientDto.setStatus(Client.ClientStatusEnum.ACTIVE.name());
        PersonDto contactPersonDto = new PersonDto();
        contactPersonDto.setId(666L);
        contactPersonDto.setFullName("gunnar ronneberg");
        clientDto.setContactPersonDto(contactPersonDto);
        Client client = TimesheetMapper.fromClientDto(clientDto);
        assertEquals(clientDto.getName(), client.getName());
        assertEquals(clientDto.getStatus(), client.getStatus());
        assertEquals(clientDto.getOrganizationDto().getId(), client.getOrganizationId());
        assertEquals(clientDto.getContactPersonDto().getId(), client.getContactPersonId());
    }

    @Test
    void toOrganizationDto() {
        Organization organization = new Organization();
        organization.setOrganizationNumber("123456789");
        organization.setName("gunnarro:as");
        organization.setBankAccountNumber("2323232323");
        organization.setOrganizationIndustryType("SOFTWARE");

        OrganizationDto organizationDto = TimesheetMapper.toOrganizationDto(organization);
        assertEquals(organization.getName(), organizationDto.getName());
        assertEquals(organization.getOrganizationNumber(), organizationDto.getOrganizationNumber());
        assertEquals(organization.getBankAccountNumber(), organizationDto.getBankAccountNumber());
        assertEquals(organization.getOrganizationIndustryType(), organizationDto.getIndustryType());
    }

    @Test
    void fromOrganizationDto() {
        OrganizationDto organizationDto = new OrganizationDto();
        organizationDto.setOrganizationNumber("123456789");
        organizationDto.setName("gunnarro:as");
        organizationDto.setBankAccountNumber("2323232323");
        organizationDto.setIndustryType("SOFTWARE");
        PersonDto personDto = new PersonDto();
        personDto.setId(333L);
        organizationDto.setContactPerson(personDto);
        PostalAddressDto postalAddressDto = new PostalAddressDto();
        postalAddressDto.setId(444L);
        organizationDto.setPostalAddressDto(postalAddressDto);
        BusinessAddressDto businessAddressDto = new BusinessAddressDto();
        businessAddressDto.setId(555L);
        organizationDto.setBusinessAddress(businessAddressDto);

        Organization organization = TimesheetMapper.fromOrganizationDto(organizationDto);
        assertEquals(organizationDto.getName(), organization.getName());
        assertEquals(organizationDto.getOrganizationNumber(), organization.getOrganizationNumber());
        assertEquals(organizationDto.getBankAccountNumber(), organization.getBankAccountNumber());
        assertEquals(organizationDto.getIndustryType(), organization.getOrganizationIndustryType());
        assertEquals(organizationDto.getContactPerson().getId(), organization.getContactInfoId());
        assertEquals(organizationDto.getBusinessAddress().getId(), organization.getBusinessAddressId());
        assertEquals(organizationDto.getPostalAddressDto().getId(), organization.getPostalAddressId());
        assertEquals(organizationDto.getBankAccountNumber(), organization.getBankAccountNumber());
    }

    @Test
    void toPersonDto() {
        Person person = new Person();
        person.setFullName("gunnar astor rønneberg");
        person.setSocialSecurityNumber("123456 12345");
        person.setDateOfBirth(LocalDate.of(1990, 3, 23));
        person.setMaritalStatus("U");
        person.setGender("M");
        person.setContactInfoId(45L);
        person.setAddressId(88L);

        PersonDto personDto = TimesheetMapper.toPersonDto(person);
        assertEquals(person.getFullName(), personDto.getFullName());
        assertNull(personDto.getContactInfo());
        assertNull(personDto.getAddress());
    }

    @Test
    void fromPersonDto() {
        PersonDto personDto = new PersonDto();
        personDto.setFullName("gunnar astor rønneberg");

        Person person = TimesheetMapper.fromPersonDto(personDto);
        assertEquals(personDto.getFullName(), person.getFullName());
        assertNull(personDto.getContactInfo());
        assertNull(personDto.getAddress());
    }

    @Test
    void toAddressDto() {
        Address address = new Address();
        address.setStreetAddress("bergslia 34 A");
        address.setCity("oslo");
        address.setCountry("norway");
        address.setPostalCode("0467");

        AddressDto addressDto = TimesheetMapper.toAddressDto(address);
        assertEquals(address.getCity(), addressDto.getCity());
        assertEquals(address.getPostalCode(), addressDto.getPostCode());
        assertEquals(address.getCountry(), addressDto.getCountry());
    }

    @Test
    void toContactInfoDto() {
        ContactInfo contactInfo = new ContactInfo();
        contactInfo.setEmailAddress("my@gmail.com");
        contactInfo.setMobileNumberCountryCode("+47");
        contactInfo.setMobileNumber("11223344");
        ContactInfoDto contactInfoDto = TimesheetMapper.toContactInfoDto(contactInfo);
        assertEquals(contactInfo.getEmailAddress(), contactInfoDto.getEmailAddress());
        assertEquals(contactInfo.getMobileNumberCountryCode(), contactInfoDto.getMobileNumberCountryCode());
        assertEquals(contactInfo.getMobileNumber(), contactInfoDto.getMobileNumber());
    }

    @Test
    void fromContactInfoDto() {
        ContactInfoDto contactInfoDto = new ContactInfoDto();
        contactInfoDto.setEmailAddress("my@gmail.com");
        contactInfoDto.setMobileNumberCountryCode("+47");
        contactInfoDto.setMobileNumber("11223344");
        ContactInfo contactInfo = TimesheetMapper.fromContactInfoDto(contactInfoDto);
        assertEquals(contactInfoDto.getEmailAddress(), contactInfo.getEmailAddress());
        assertEquals(contactInfoDto.getMobileNumberCountryCode(), contactInfo.getMobileNumberCountryCode());
        assertEquals(contactInfoDto.getMobileNumber(), contactInfo.getMobileNumber());
    }

    @Test
    void fromUserAccountDto() {
        UserAccountDto userAccountDto = new UserAccountDto();
        userAccountDto.setId(66L);
        userAccountDto.setUserAccountType(UserAccount.UserAccountTypeEnum.BUSINESS.name());
        userAccountDto.setDefaultUser(true);
        userAccountDto.setUserName("guro");
        userAccountDto.setPassword("change-me");
        OrganizationDto organizationDto = new OrganizationDto();
        organizationDto.setId(44L);
        userAccountDto.setOrganizationDto(organizationDto);
        userAccountDto.setPersonDto(null);

        UserAccount userAccount = TimesheetMapper.fromUserAccountDto(userAccountDto);

        assertEquals(userAccountDto.getId(), userAccount.getId());
        assertEquals(userAccountDto.getUserAccountType(), userAccount.getUserAccountType());
        assertEquals(userAccountDto.isDefaultUser(), userAccount.isDefaultUser());
        assertEquals(userAccountDto.getUserName(), userAccount.getUserName());
        assertEquals(userAccountDto.getPassword(), userAccount.getPassword());
        assertEquals(userAccountDto.getOrganizationDto().getId(), userAccount.getOrganizationId());
        assertNull(userAccount.getPersonId());
    }

    @Test
    void toUserAccountDto() {
        UserAccount userAccount = new UserAccount();
        userAccount.setId(12L);
        userAccount.setUserAccountType(UserAccount.UserAccountTypeEnum.BUSINESS.name());
        userAccount.setDefaultUser(1);
        userAccount.setUserName("guro");
        userAccount.setPassword("change-me");
        userAccount.setOrganizationId(55L);
        userAccount.setPersonId(null);

        UserAccountDto userAccountDto = TimesheetMapper.toUserAccountDto(userAccount);
        assertEquals(userAccount.getId(), userAccountDto.getId());
        assertEquals(userAccount.getUserAccountType(), userAccountDto.getUserAccountType());
        assertEquals(userAccount.isDefaultUser(), userAccountDto.isDefaultUser());
        assertEquals(userAccount.getUserName(), userAccountDto.getUserName());
        assertEquals(userAccount.getPassword(), userAccountDto.getPassword());
        assertEquals(userAccount.getOrganizationId(), userAccountDto.getOrganizationDto().getId());
        assertNull(userAccountDto.getPersonDto());
    }

    private Client createClient(Long id, String name) {
        Client client = new Client();
        client.setId(id);
        client.setName(name);
        client.setOrganizationId(100L);
        client.setContactPersonId(900L);
        client.setStatus(Client.ClientStatusEnum.ACTIVE.name());
        return client;
    }
}
