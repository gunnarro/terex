package com.gunnarro.android.terex.domain.mapper;

import com.gunnarro.android.terex.domain.dto.AddressDto;
import com.gunnarro.android.terex.domain.dto.ClientDto;
import com.gunnarro.android.terex.domain.dto.ConsultantBrokerDto;
import com.gunnarro.android.terex.domain.dto.ContactInfoDto;
import com.gunnarro.android.terex.domain.dto.OrganizationDto;
import com.gunnarro.android.terex.domain.dto.PersonDto;
import com.gunnarro.android.terex.domain.dto.ProjectDto;
import com.gunnarro.android.terex.domain.dto.TimesheetDto;
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
import com.gunnarro.android.terex.domain.entity.Timesheet;
import com.gunnarro.android.terex.domain.entity.TimesheetEntry;
import com.gunnarro.android.terex.domain.entity.TimesheetSummary;

import java.util.List;
import java.util.stream.Collectors;

public class TimesheetMapper {

    private TimesheetMapper() {
    }

    public static List<TimesheetDto> toTimesheetDtoList(List<Timesheet> timesheetList) {
        if (timesheetList == null) {
            return null;
        }
        return timesheetList.stream().map(TimesheetMapper::toTimesheetDto).collect(Collectors.toList());
    }

    public static TimesheetDto toTimesheetDto(Timesheet timesheet) {
        return toTimesheetDto(timesheet, null, null);
    }

    public static TimesheetDto toTimesheetDto(Timesheet timesheet, Integer sumDays, Integer sumHours) {
        TimesheetDto timesheetDto = new TimesheetDto();
        timesheetDto.setTimesheetId(timesheet.getId());
        timesheetDto.setClientName(timesheet.getClientName());
        timesheetDto.setProjectCode(timesheet.getProjectCode());
        timesheetDto.setDescription(timesheet.getDescription());
        timesheetDto.setYear(timesheet.getYear());
        timesheetDto.setMonth(timesheet.getMonth());
        timesheetDto.setStatus(timesheet.getStatus());
        timesheetDto.setWorkingDaysInMonth(timesheet.getWorkingDaysInMonth());
        timesheetDto.setWorkingHoursInMonth(timesheet.getWorkingHoursInMonth());
        timesheetDto.setRegisteredWorkedDays(sumDays);
        timesheetDto.setRegisteredWorkedHours(sumHours);
        return timesheetDto;
    }


    public static Timesheet fromTimesheetDto(TimesheetDto timesheetDto) {
        Timesheet timesheet = new Timesheet();
        timesheet.setId(timesheetDto.getTimesheetId());
        timesheet.setClientName(timesheetDto.getClientName());
        timesheet.setProjectCode(timesheetDto.getProjectCode());
        timesheet.setDescription(timesheetDto.getDescription());
        timesheet.setYear(timesheetDto.getYear());
        timesheet.setMonth(timesheetDto.getMonth());
        timesheet.setStatus(timesheetDto.getStatus());
        timesheet.setFromDate(timesheet.getFromDate());
        timesheet.setToDate(timesheet.getToDate());
        timesheet.setWorkingDaysInMonth(timesheetDto.getWorkingDaysInMonth());
        timesheet.setWorkingHoursInMonth(timesheetDto.getWorkingHoursInMonth());
        return timesheet;
    }

    public static List<TimesheetEntryDto> toTimesheetEntryDtoList(List<TimesheetEntry> timesheetEntryList) {
        return timesheetEntryList.stream().map(TimesheetMapper::toTimesheetEntryDto).collect(Collectors.toList());
    }

    public static TimesheetEntryDto toTimesheetEntryDto(TimesheetEntry timesheetEntry) {
        TimesheetEntryDto timesheetEntryDto = new TimesheetEntryDto();
        timesheetEntryDto.setWorkdayDate(timesheetEntry.getWorkdayDate());
        timesheetEntryDto.setFromTime(timesheetEntry.getFromTime());
        timesheetEntryDto.setToTime(timesheetEntry.getToTime());
        timesheetEntryDto.setWorkedMinutes(timesheetEntry.getWorkedMinutes());
        timesheetEntryDto.setComments(timesheetEntry.getComment());
        return timesheetEntryDto;
    }

    public static List<TimesheetSummaryDto> toTimesheetSummaryDtoList(List<TimesheetSummary> timesheetSummaryList) {
        return timesheetSummaryList.stream().map(TimesheetMapper::toTimesheetSummaryDto).collect(Collectors.toList());
    }

    public static TimesheetSummaryDto toTimesheetSummaryDto(TimesheetSummary timesheetSummary) {
        TimesheetSummaryDto timesheetSummaryDto = new TimesheetSummaryDto();
        timesheetSummaryDto.setTimesheetId(timesheetSummary.getTimesheetId());
        timesheetSummaryDto.setCurrency(timesheetSummary.getCurrency());
        timesheetSummaryDto.setFromDate(timesheetSummary.getFromDate());
        timesheetSummaryDto.setToDate(timesheetSummary.getToDate());
        timesheetSummaryDto.setYear(timesheetSummary.getYear());
        timesheetSummaryDto.setWeekInYear(timesheetSummary.getWeekInYear());
        timesheetSummaryDto.setTotalWorkedHours(timesheetSummary.getTotalWorkedHours());
        timesheetSummaryDto.setTotalBilledAmount(timesheetSummary.getTotalBilledAmount());
        timesheetSummaryDto.setTotalWorkedDays(timesheetSummary.getTotalWorkedDays());
        return timesheetSummaryDto;
    }

    public static List<ProjectDto> toProjectDtoList(List<Project> projectList) {
        return projectList.stream().map(TimesheetMapper::toProjectDto).collect(Collectors.toList());
    }

    public static ProjectDto toProjectDto(Project project) {
        if (project == null) {
            return null;
        }
        ProjectDto projectDto = new ProjectDto();
        projectDto.setId(project.getId());
        projectDto.setClientId(project.getClientId());
        projectDto.setName(project.getName());
        projectDto.setDescription(project.getDescription());
        projectDto.setStatus(project.getStatus());
        return projectDto;
    }

    public static Project fromProjectDto(ProjectDto projectDto) {
        if (projectDto == null) {
            return null;
        }
        Project project = new Project();
        project.setId(projectDto.getId());
        project.setClientId(projectDto.getClientId());
        project.setName(projectDto.getName());
        project.setDescription(projectDto.getDescription());
        project.setStatus(projectDto.getStatus());
        return project;
    }

    public static ProjectDto toProjectDto(Project project, List<Timesheet> timesheetList) {
        if (project == null) {
            return null;
        }
        ProjectDto projectDto = toProjectDto(project);
        projectDto.setTimesheetDto(toTimesheetDtoList(timesheetList));
        return projectDto;
    }

    public static ConsultantBrokerDto toConsultantBrokerDto(ConsultantBrokerWithProject consultantBrokerWithProject) {
        if (consultantBrokerWithProject == null || consultantBrokerWithProject.getConsultantBroker() == null) {
            return null;
        }
        ConsultantBrokerDto consultantBrokerDto = new ConsultantBrokerDto();
        consultantBrokerDto.setId(consultantBrokerWithProject.getConsultantBroker().getId());
        consultantBrokerDto.setName(consultantBrokerWithProject.getConsultantBroker().getName());
        consultantBrokerDto.setStatus(consultantBrokerWithProject.getConsultantBroker().getStatus());
        consultantBrokerDto.setProjects(toProjectDtoList(consultantBrokerWithProject.getProjectList()));
        return consultantBrokerDto;
    }

    public static ConsultantBroker fromConsultantBrokerDto(ConsultantBrokerDto consultantBrokerDto) {
        if (consultantBrokerDto == null) {
            return null;
        }
        ConsultantBroker consultantBroker = new ConsultantBroker();
        consultantBroker.setId(consultantBrokerDto.getId());
        consultantBroker.setName(consultantBrokerDto.getName());
        consultantBroker.setStatus(consultantBrokerDto.getStatus());
        return consultantBroker;
    }

    public static ClientDto toClientDto(ClientDetails clientDetails) {
        ClientDto clientDto = new ClientDto();
        // clientDto.setCompanyDto(toCompanyDto(clientDetails.getCompany()));
        clientDto.setProjectList(toProjectDtoList(clientDetails.getProjectList()));
        return clientDto;
    }


    public static Client fromClientDto(ClientDto clientDto) {
        Client client = new Client();
        client.setId(clientDto.getId());
        client.setOrganizationId(clientDto.getOrganizationDto().getId());
        client.setStatus(clientDto.getStatus());
        return client;
    }


    public static List<OrganizationDto> toOrganizationDtoList(List<Organization> organizations) {
        return List.of();
    }

    public static Organization fromOrganizationDto(OrganizationDto organizationDto) {
        if (organizationDto == null) {
            return null;
        }
        Organization organization = new Organization();
        organization.setId(organizationDto.getId());
        organization.setName(organizationDto.getName());
        organization.setOrganizationNumber(organizationDto.getOrganizationNumber());
        organization.setOrganizationIndustryType(organizationDto.getIndustryType());
        organization.setBankAccountNumber(organizationDto.getBankAccountNumber());
        return organization;
    }

    public static OrganizationDto toOrganizationDto(Organization organization) {
        if (organization == null) {
            return null;
        }
        OrganizationDto organizationDto = new OrganizationDto();
        organizationDto.setId(organization.getId());
        organizationDto.setName(organization.getName());
        organizationDto.setOrganizationNumber(organization.getOrganizationNumber());
        organizationDto.setIndustryType(organization.getOrganizationIndustryType());
        organizationDto.setBankAccountNumber(organization.getBankAccountNumber());
        return organizationDto;
    }

    public static ContactInfoDto toContactInfoDto(ContactInfo contactInfo) {
        if (contactInfo == null) {
            return null;
        }
        ContactInfoDto contactInfoDto = new ContactInfoDto();
        contactInfoDto.setId(contactInfo.getId());
        contactInfoDto.setEmailAddress(contactInfo.getEmailAddress());
        contactInfoDto.setMobileNumber(contactInfo.getMobileNumber());
        contactInfoDto.setMobileNumberCountryCode(contactInfo.getMobileNumberCountryCode());
        return contactInfoDto;
    }

    public static PersonDto toPersonDto(Person person) {
        if (person == null) {
            return null;
        }
        PersonDto personDto = new PersonDto();
        personDto.setId(person.getId());
        personDto.setGender(person.getGender());
        personDto.setFirstName(person.getFirstName());
        personDto.setMiddleName(person.getMiddleName());
        personDto.setLastName(person.getLastName());
        personDto.setGender(person.getGender());
        personDto.setDateOfBirth(person.getDateOfBirth());
        personDto.setSocialSecurityNumber(person.getSocialSecurityNumber());
        personDto.setMaritalStatus(person.getMaritalStatus());
        personDto.setContactInfoId(null);
        return personDto;
    }

    public static Person fromPersonDto(PersonDto personDto) {
        if (personDto == null) {
            return null;
        }
        Person person = new Person();
        person.setId(personDto.getId());
        person.setGender(personDto.getGender());
        person.setFirstName(personDto.getFirstName());
        person.setMiddleName(personDto.getMiddleName());
        person.setLastName(personDto.getLastName());
        person.setGender(personDto.getGender());
        person.setDateOfBirth(personDto.getDateOfBirth());
        person.setSocialSecurityNumber(personDto.getSocialSecurityNumber());
        person.setMaritalStatus(personDto.getMaritalStatus());
        return person;
    }


    public static AddressDto toAddressDto(Address address) {
        if (address == null) {
            return null;
        }
        AddressDto addressDto = new AddressDto();
        addressDto.setId(address.getId());
        addressDto.setCity(address.getCity());
        addressDto.setCountry(address.getCountry());
        addressDto.setCountryCode(address.getCountryCode());
        addressDto.setPostCode(address.getPostCode());
        addressDto.setStreetName(address.getStreetName());
        addressDto.setStreetNumber(address.getStreetNumber());
        addressDto.setStreetNumberPrefix(address.getStreetNumberPrefix());
        return addressDto;
    }

}
