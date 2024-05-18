package com.gunnarro.android.terex.domain.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.gunnarro.android.terex.TestData;
import com.gunnarro.android.terex.domain.dto.AddressDto;
import com.gunnarro.android.terex.domain.dto.BusinessAddressDto;
import com.gunnarro.android.terex.domain.dto.ClientDto;
import com.gunnarro.android.terex.domain.dto.ConsultantBrokerDto;
import com.gunnarro.android.terex.domain.dto.ContactInfoDto;
import com.gunnarro.android.terex.domain.dto.OrganizationDto;
import com.gunnarro.android.terex.domain.dto.PersonDto;
import com.gunnarro.android.terex.domain.dto.PostalAddressDto;
import com.gunnarro.android.terex.domain.dto.ProjectDto;
import com.gunnarro.android.terex.domain.dto.TimesheetEntryDto;
import com.gunnarro.android.terex.domain.dto.TimesheetSummaryDto;
import com.gunnarro.android.terex.domain.entity.Address;
import com.gunnarro.android.terex.domain.entity.Client;
import com.gunnarro.android.terex.domain.entity.ClientDetails;
import com.gunnarro.android.terex.domain.entity.ConsultantBroker;
import com.gunnarro.android.terex.domain.entity.ConsultantBrokerWithProject;
import com.gunnarro.android.terex.domain.entity.ContactInfo;
import com.gunnarro.android.terex.domain.entity.Organization;
import com.gunnarro.android.terex.domain.entity.Person;
import com.gunnarro.android.terex.domain.entity.Project;
import com.gunnarro.android.terex.domain.entity.TimesheetEntry;
import com.gunnarro.android.terex.domain.entity.TimesheetSummary;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

class TimesheetMapperTest {

    @Test
    void toTimesheetEntryDto() {
        LocalDate localDate = LocalDate.of(2023, 12, 1);
        List<TimesheetEntry> timesheetEntryList = TestData.generateTimesheetEntries(localDate.getYear(), localDate.getMonthValue());

        TimesheetEntryDto timesheetEntryDto = TimesheetMapper.toTimesheetEntryDto(timesheetEntryList.get(0));
        assertEquals("2023-12-01", timesheetEntryDto.getWorkdayDate().toString());
        assertEquals("08:00", timesheetEntryDto.getFromTime().toString());
        assertEquals("15:30", timesheetEntryDto.getToTime().toString());
        assertEquals("7.5", timesheetEntryDto.getWorkedHours());
        assertNull(timesheetEntryDto.getComments());
    }

    @Test
    void toTimesheetEntryDtoList() {
        LocalDate localDate = LocalDate.of(2023, 12, 1);
        List<TimesheetEntry> timesheetEntryList = TestData.generateTimesheetEntries(localDate.getYear(), localDate.getMonthValue());

        List<TimesheetEntryDto> timesheetEntryDtoList = TimesheetMapper.toTimesheetEntryDtoList(timesheetEntryList);
        assertEquals(21, timesheetEntryDtoList.size());
    }

    @Test
    void toTimesheetSummaryDto() {
        LocalDate localDate = LocalDate.of(2023, 12, 1);
        List<TimesheetSummary> timesheetSummaryList = TestData.buildTimesheetSummaryByWeek(23L, localDate.getYear(), localDate.getMonthValue());

        TimesheetSummaryDto timesheetSummaryDto = TimesheetMapper.toTimesheetSummaryDto(timesheetSummaryList.get(0));
        assertEquals(23, timesheetSummaryDto.getTimesheetId());
        assertEquals("01.12", timesheetSummaryDto.getFromDateDDMM());
        assertEquals("9375.00", timesheetSummaryDto.getTotalBilledAmount());
        assertEquals("12", timesheetSummaryDto.getFromDateMM());
        assertEquals("48", timesheetSummaryDto.getWeekInYear().toString());
        assertEquals("7.5", timesheetSummaryDto.getTotalWorkedHours());
    }

    @Test
    void toTimesheetSummaryDtoList() {
        LocalDate localDate = LocalDate.of(2023, 12, 1);
        List<TimesheetSummary> timesheetSummaryList = TestData.buildTimesheetSummaryByWeek(23L, localDate.getYear(), localDate.getMonthValue());

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

        ProjectDto projectDto = TimesheetMapper.toProjectDto(project);

        assertEquals(1, projectDto.getId().intValue());
        assertEquals(1001, projectDto.getClientId().intValue());
        assertEquals("gunnarro timesheet project", projectDto.getName());
        assertEquals("develop a timesheet app", projectDto.getDescription());
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

        Project project = TimesheetMapper.fromProjectDto(projectDto);

        assertEquals(projectDto.getId(), project.getId().intValue());
        assertEquals(projectDto.getClientId(), project.getClientId().intValue());
        assertEquals(projectDto.getName(), project.getName());
        assertEquals(projectDto.getDescription(), project.getDescription());
        assertEquals(projectDto.getStatus(), project.getStatus());
    }

    @Test
    void toConsultantBrokerDto() {
        ConsultantBroker consultantBroker = new ConsultantBroker();
        consultantBroker.setId(12L);
        consultantBroker.setName("gunnarro consultant broker");
        consultantBroker.setStatus("ACTIVE");

        Project project = new Project();
        project.setName("gunnarro timesheet project");
        project.setId(1L);
        project.setClientId(1L);
        project.setDescription("develop a timesheet app");
        project.setStatus(Project.ProjectStatusEnum.ACTIVE.name());

        ConsultantBrokerWithProject consultantBrokerWithProject = new ConsultantBrokerWithProject();
        consultantBrokerWithProject.setConsultantBroker(consultantBroker);
        consultantBrokerWithProject.setProjectList(List.of(project));

        ConsultantBrokerDto consultantBrokerDto = TimesheetMapper.toConsultantBrokerDto(consultantBrokerWithProject);
        assertEquals(12, consultantBrokerDto.getId().intValue());
        assertEquals("gunnarro consultant broker", consultantBrokerDto.getName());
        assertEquals(1, consultantBrokerDto.getProjects().size());
        assertEquals("gunnarro timesheet project", consultantBrokerDto.getProjects().get(0).getName());
    }

    @Test
    void fromConsultantBrokerDto() {
        ConsultantBrokerDto consultantBrokerDto = new ConsultantBrokerDto();
        consultantBrokerDto.setId(12L);
        consultantBrokerDto.setName("gunnarro consultant broker");
        consultantBrokerDto.setStatus("ACTIVE");

        ConsultantBroker consultantBroker = TimesheetMapper.fromConsultantBrokerDto(consultantBrokerDto);
        assertEquals(12, consultantBroker.getId().intValue());
        assertEquals("gunnarro consultant broker", consultantBroker.getName());
        assertEquals("ACTIVE", consultantBroker.getStatus());

    }

    @Test
    void toClientDtoList() {
        List<Client> clients = new ArrayList<Client>();
        Client client = new Client();
        client.setId(24L);
        client.setName("gunnarro as");
        client.setOrganizationId(100L);
        clients.add(client);
        List<ClientDto> clientDtos = TimesheetMapper.toClientDtoList(clients);
        assertEquals(1, clientDtos.size());
        //  assertNull(clientDto.getCompanyDto());
        //    assertNull(clientDto.getCompanyDto().getName());
        //    assertNull(clientDto.getCompanyDto().getOrganizationNumber());
        //    assertNull(clientDto.getCompanyDto().getBankAccountNumber());
    }

    @Test
    void toClientDtoList_empty() {
        List<ClientDto> clientDtos = TimesheetMapper.toClientDtoList(new ArrayList<>());
        assertTrue(clientDtos.isEmpty());

        clientDtos = TimesheetMapper.toClientDtoList(null);
        assertTrue(clientDtos.isEmpty());
    }

    @Test
    void toClientDto() {
        ClientDetails clientDetails = new ClientDetails();
        Client client = new Client();
        client.setId(2344L);
        client.setName("gunnarro");
        client.setOrganizationId(2222222L);
        client.setStatus("ACTIVE");
        clientDetails.setClient(client);

        Project project1 = new Project();
        project1.setClientId(client.getId());
        project1.setName("terex app developing");
        project1.setStatus(Project.ProjectStatusEnum.ACTIVE.name());
        project1.setDescription("description");

        Project project2 = new Project();
        project2.setClientId(client.getId());
        project2.setName("web developing");
        project2.setStatus(Project.ProjectStatusEnum.ACTIVE.name());
        project2.setDescription("description");

        clientDetails.setProjectList(List.of(project1, project2));
        ClientDto clientDto = TimesheetMapper.toClientDto(clientDetails);
        assertEquals(clientDetails.getClient().getId(), clientDto.getId());
        assertEquals(clientDetails.getClient().getName(), clientDto.getName());
        //  assertEquals(clientDetails.getClient().getOrganizationId(), clientDto.getOrganizationDto().getId());
        assertEquals(clientDetails.getClient().getStatus(), clientDto.getStatus());
        assertEquals(2, clientDto.getProjectList().size());
        assertEquals(project1.getClientId(), clientDto.getProjectList().get(0).getClientId());
        assertEquals(project1.getName(), clientDto.getProjectList().get(0).getName());
        assertEquals(project1.getStatus(), clientDto.getProjectList().get(0).getStatus());
        assertEquals(project1.getDescription(), clientDto.getProjectList().get(0).getDescription());
    }

    @Test
    void fromClientDto() {
        ClientDto clientDto = new ClientDto();
        clientDto.setId(23L);
        OrganizationDto orgDto = new OrganizationDto();
        orgDto.setOrganizationNumber("828707933");
        clientDto.setOrganizationDto(orgDto);
        clientDto.setName("gunnarro as");
        PersonDto contactPersonDto = new PersonDto();
        contactPersonDto.setId(666L);
        contactPersonDto.setFullName("gunnar ronneberg");
        clientDto.setCntactPersonDto(contactPersonDto);
        Client client = TimesheetMapper.fromClientDto(clientDto);
        assertEquals(clientDto.getName(), client.getName());
        assertEquals(clientDto.getOrganizationDto().getId(), client.getOrganizationId());
        assertEquals(clientDto.getCntactPersonDto().getId(), client.getContactPersonId());
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
}
