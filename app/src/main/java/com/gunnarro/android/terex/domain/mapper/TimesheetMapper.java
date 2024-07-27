package com.gunnarro.android.terex.domain.mapper;

import com.gunnarro.android.terex.domain.dto.AddressDto;
import com.gunnarro.android.terex.domain.dto.BusinessAddressDto;
import com.gunnarro.android.terex.domain.dto.ClientDto;
import com.gunnarro.android.terex.domain.dto.ContactInfoDto;
import com.gunnarro.android.terex.domain.dto.IntegrationDto;
import com.gunnarro.android.terex.domain.dto.InvoiceDto;
import com.gunnarro.android.terex.domain.dto.OrganizationDto;
import com.gunnarro.android.terex.domain.dto.PersonDto;
import com.gunnarro.android.terex.domain.dto.ProjectDto;
import com.gunnarro.android.terex.domain.dto.TimesheetDto;
import com.gunnarro.android.terex.domain.dto.TimesheetEntryDto;
import com.gunnarro.android.terex.domain.dto.TimesheetSummaryDto;
import com.gunnarro.android.terex.domain.dto.UserAccountDto;
import com.gunnarro.android.terex.domain.entity.Address;
import com.gunnarro.android.terex.domain.entity.Client;
import com.gunnarro.android.terex.domain.entity.ContactInfo;
import com.gunnarro.android.terex.domain.entity.Integration;
import com.gunnarro.android.terex.domain.entity.Invoice;
import com.gunnarro.android.terex.domain.entity.Organization;
import com.gunnarro.android.terex.domain.entity.Person;
import com.gunnarro.android.terex.domain.entity.Project;
import com.gunnarro.android.terex.domain.entity.Timesheet;
import com.gunnarro.android.terex.domain.entity.TimesheetEntry;
import com.gunnarro.android.terex.domain.entity.TimesheetSummary;
import com.gunnarro.android.terex.domain.entity.UserAccount;

import java.util.ArrayList;
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
        timesheetDto.setId(timesheet.getId());
        timesheetDto.setClientDto(new ClientDto(timesheet.getClientId()));
        timesheetDto.setUserAccountDto(new UserAccountDto(timesheet.getClientId()));
        timesheetDto.setDescription(timesheet.getDescription());
        timesheetDto.setFromDate(timesheet.getFromDate());
        timesheetDto.setToDate(timesheet.getToDate());
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
        timesheet.setId(timesheetDto.getId());
        timesheet.setUserId(timesheetDto.getUserAccountDto().getId());
        timesheet.setDescription(timesheetDto.getDescription());
        timesheet.setYear(timesheetDto.getYear());
        timesheet.setMonth(timesheetDto.getMonth());
        timesheet.setStatus(timesheetDto.getStatus());
        timesheet.setFromDate(timesheetDto.getFromDate());
        timesheet.setToDate(timesheetDto.getToDate());
        timesheet.setWorkingDaysInMonth(timesheetDto.getWorkingDaysInMonth());
        timesheet.setWorkingHoursInMonth(timesheetDto.getWorkingHoursInMonth());
        return timesheet;
    }

    public static List<TimesheetEntryDto> toTimesheetEntryDtoList(List<TimesheetEntry> timesheetEntryList) {
        return timesheetEntryList.stream().map(TimesheetMapper::toTimesheetEntryDto).collect(Collectors.toList());
    }

    public static TimesheetEntryDto toTimesheetEntryDto(TimesheetEntry timesheetEntry) {
        TimesheetEntryDto timesheetEntryDto = new TimesheetEntryDto();
        timesheetEntryDto.setId(timesheetEntry.getId());
        timesheetEntryDto.setCreatedDate(timesheetEntry.getCreatedDate());
        timesheetEntryDto.setLastModifiedDate(timesheetEntry.getLastModifiedDate());
        timesheetEntryDto.setTimesheetId(timesheetEntry.getTimesheetId());
        timesheetEntryDto.setProjectId(timesheetEntry.getProjectId());
        timesheetEntryDto.setType(timesheetEntry.getType());
        timesheetEntryDto.setStatus(timesheetEntry.getStatus());
        timesheetEntryDto.setWorkdayDate(timesheetEntry.getWorkdayDate());
        timesheetEntryDto.setStartTime(timesheetEntry.getStartTime());
        timesheetEntryDto.setEndTime(timesheetEntry.getEndTime());
        timesheetEntryDto.setWorkedSeconds(timesheetEntry.getWorkedSeconds());
        timesheetEntryDto.setBreakSeconds(timesheetEntry.getBreakSeconds());
        timesheetEntryDto.setComments(timesheetEntry.getComments());
        return timesheetEntryDto;
    }

    public static TimesheetEntry fromTimesheetEntryDto(TimesheetEntryDto timesheetEntryDto) {
        TimesheetEntry timesheetEntry = new TimesheetEntry();
        timesheetEntry.setId(timesheetEntryDto.getId());
        timesheetEntry.setCreatedDate(timesheetEntryDto.getCreatedDate());
        timesheetEntry.setLastModifiedDate(timesheetEntryDto.getLastModifiedDate());
        timesheetEntry.setTimesheetId(timesheetEntryDto.getTimesheetId());
        timesheetEntry.setProjectId(timesheetEntryDto.getProjectId());
        timesheetEntry.setType(timesheetEntryDto.getType());
        timesheetEntry.setStatus(timesheetEntryDto.getStatus());
        timesheetEntry.setWorkdayDate(timesheetEntryDto.getWorkdayDate());
        timesheetEntry.setStartTime(timesheetEntryDto.getStartTime());
        //timesheetEntry.setEndTime(timesheetEntryDto.getEndTime());
        timesheetEntry.setWorkedSeconds(timesheetEntryDto.getWorkedSeconds());
        timesheetEntry.setBreakSeconds(timesheetEntryDto.getBreakSeconds());
        timesheetEntry.setComments(timesheetEntryDto.getComments());
        return timesheetEntry;
    }

    public static List<TimesheetSummaryDto> toTimesheetSummaryDtoList(List<TimesheetSummary> timesheetSummaryList) {
        return timesheetSummaryList.stream().map(TimesheetMapper::toTimesheetSummaryDto).collect(Collectors.toList());
    }

    public static TimesheetSummaryDto toTimesheetSummaryDto(TimesheetSummary timesheetSummary) {
        TimesheetSummaryDto timesheetSummaryDto = new TimesheetSummaryDto();
        timesheetSummaryDto.setTimesheetId(timesheetSummary.getTimesheetId());
        timesheetSummaryDto.setSummedByPeriod(timesheetSummary.getSummedByPeriod());
        timesheetSummaryDto.setCurrency(timesheetSummary.getCurrency());
        timesheetSummaryDto.setFromDate(timesheetSummary.getFromDate());
        timesheetSummaryDto.setToDate(timesheetSummary.getToDate());
        timesheetSummaryDto.setYear(timesheetSummary.getYear());
        timesheetSummaryDto.setWeekInYear(timesheetSummary.getWeekInYear());
        timesheetSummaryDto.setTotalWorkedHours(timesheetSummary.getTotalWorkedHours());
        timesheetSummaryDto.setTotalBilledAmount(timesheetSummary.getTotalBilledAmount());
        timesheetSummaryDto.setTotalWorkedDays(timesheetSummary.getTotalWorkedDays());
        timesheetSummaryDto.setTotalSickLeaveDays(timesheetSummary.getTotalSickLeaveDays());
        timesheetSummaryDto.setTotalVacationDays(timesheetSummary.getTotalVacationDays());
        return timesheetSummaryDto;
    }

    public static List<ProjectDto> toProjectDtoList(List<Project> projectList) {
        if (projectList == null) {
            return new ArrayList<>();
        }
        return projectList.stream().map(TimesheetMapper::toProjectDto).collect(Collectors.toList());
    }

    public static ProjectDto toProjectDto(Project project) {
        if (project == null) {
            return null;
        }
        ProjectDto projectDto = new ProjectDto();
        projectDto.setId(project.getId());
        projectDto.setClientDto(new ClientDto(project.getClientId()));
        projectDto.setName(project.getName());
        projectDto.setDescription(project.getDescription());
        projectDto.setStatus(project.getStatus());
        projectDto.setHourlyRate(project.getHourlyRate());
        return projectDto;
    }

    public static Project fromProjectDto(ProjectDto projectDto) {
        if (projectDto == null) {
            return null;
        }
        Project project = new Project();
        project.setId(projectDto.getId());
        project.setClientId(projectDto.getClientDto().getId());
        project.setName(projectDto.getName());
        project.setDescription(projectDto.getDescription());
        project.setStatus(projectDto.getStatus());
        project.setHourlyRate(projectDto.getHourlyRate());
        return project;
    }

    public static ProjectDto toProjectDto(Project project, List<Timesheet> timesheetList) {
        if (project == null) {
            return null;
        }
        return toProjectDto(project);
    }

    public static List<ClientDto> toClientDtoList(List<Client> clientList) {
        if (clientList == null) {
            return new ArrayList<>();
        }
        return clientList.stream().map(TimesheetMapper::toClientDto).collect(Collectors.toList());
    }

    public static ClientDto toClientDto(Client client) {
        ClientDto clientDto = new ClientDto(null);
        clientDto.setId(client.getId());
        clientDto.setName(client.getName());
        clientDto.setStatus(client.getStatus());
        clientDto.setInvoiceEmailAddress(client.getInvoiceEmailAddress());
        // map org
        OrganizationDto orgDto = new OrganizationDto();
        orgDto.setId(client.getOrganizationId());
        clientDto.setOrganizationDto(orgDto);
        // map person
        PersonDto contactPersonDto = new PersonDto();
        contactPersonDto.setId(client.getContactPersonId());
        clientDto.setContactPersonDto(contactPersonDto);
        return clientDto;
    }

    public static Client fromClientDto(ClientDto clientDto) {
        Client client = new Client();
        client.setId(clientDto.getId());
        client.setName(clientDto.getName());
        client.setInvoiceEmailAddress(clientDto.getInvoiceEmailAddress());
        client.setOrganizationId(clientDto.getOrganizationDto() != null ? clientDto.getOrganizationDto().getId() : null);
        client.setStatus(clientDto.getStatus());
        client.setContactPersonId(clientDto.getContactPersonDto() != null ? clientDto.getContactPersonDto().getId() : null);
        return client;
    }

    public static Organization fromOrganizationDto(OrganizationDto organizationDto) {
        if (organizationDto == null) {
            return null;
        }
        Organization organization = new Organization();
        organization.setId(organizationDto.getId());
        //organization.setUuid(organizationDto.getUuid());
        organization.setName(organizationDto.getName());
        organization.setOrganizationNumber(organizationDto.getOrganizationNumber());
        organization.setOrganizationIndustryType(organizationDto.getIndustryType());
        organization.setBankAccountNumber(organizationDto.getBankAccountNumber());
        organization.setBusinessAddressId(organizationDto.getBusinessAddress() != null ? organizationDto.getBusinessAddress().getId() : null);
        organization.setPostalAddressId(organizationDto.getPostalAddressDto() != null ? organizationDto.getPostalAddressDto().getId() : null);
        organization.setContactInfoId(organizationDto.getContactPerson() != null ? organizationDto.getContactPerson().getId() : null);
        return organization;
    }

    public static OrganizationDto toOrganizationDto(Organization organization) {
        if (organization == null) {
            return null;
        }
        OrganizationDto organizationDto = new OrganizationDto();
        organizationDto.setId(organization.getId());
        //organizationDto.setUuid(organization.getUuid());
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

    public static ContactInfo fromContactInfoDto(ContactInfoDto contactInfoDto) {
        if (contactInfoDto == null) {
            return null;
        }
        ContactInfo contactInfo = new ContactInfo();
        contactInfo.setId(contactInfoDto.getId());
        contactInfo.setEmailAddress(contactInfoDto.getEmailAddress());
        contactInfo.setMobileNumber(contactInfoDto.getMobileNumber());
        contactInfo.setMobileNumberCountryCode(contactInfoDto.getMobileNumberCountryCode());
        return contactInfo;
    }


    public static PersonDto toPersonDto(Person person) {
        if (person == null) {
            return null;
        }
        PersonDto personDto = new PersonDto();
        personDto.setId(person.getId());
        personDto.setFullName(person.getFullName());
        return personDto;
    }

    public static Person fromPersonDto(PersonDto personDto) {
        if (personDto == null) {
            return null;
        }
        Person person = new Person();
        person.setId(personDto.getId());
        person.setFullName(personDto.getFullName());
        person.setAddressId(personDto.getAddress() != null ? personDto.getAddress().getId() : null);
        person.setContactInfoId(personDto.getContactInfo() != null ? personDto.getContactInfo().getId() : null);
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
        addressDto.setPostCode(address.getPostalCode());
        addressDto.setStreetAddress(address.getStreetAddress());
        return addressDto;
    }

    public static BusinessAddressDto toABusinessAddressDto(Address address) {
        if (address == null) {
            return null;
        }
        BusinessAddressDto businessAddressDto = new BusinessAddressDto();
        businessAddressDto.setId(address.getId());
        businessAddressDto.setStreetAddress(address.getStreetAddress());
        businessAddressDto.setCity(address.getCity());
        businessAddressDto.setCountry(address.getCountry());
        businessAddressDto.setCountryCode(address.getCountryCode());
        businessAddressDto.setPostalCode(address.getPostalCode());
        return businessAddressDto;
    }

    public static Address fromBusinessAddressDto(BusinessAddressDto businessAddressDto) {
        if (businessAddressDto == null) {
            return null;
        }
        Address businessAddress = new Address();
        businessAddress.setId(businessAddressDto.getId());
        businessAddress.setStreetAddress(businessAddressDto.getStreetAddress());
        businessAddress.setCity(businessAddressDto.getCity());
        businessAddress.setCountry(businessAddressDto.getCountry());
        businessAddress.setCountryCode(businessAddressDto.getCountryCode());
        businessAddress.setPostalCode(businessAddressDto.getPostalCode());
        return businessAddress;
    }

    public static UserAccountDto toUserAccountDto(UserAccount userAccount) {
        if (userAccount == null) {
            return null;
        }
        UserAccountDto userAccountDto = new UserAccountDto();
        userAccountDto.setId(userAccount.getId());
        userAccountDto.setStatus(userAccount.getStatus());
        userAccountDto.setUserAccountType(userAccount.getUserAccountType());
        userAccountDto.setUserName(userAccount.getUserName());
        userAccountDto.setPassword(userAccount.getPassword());
        userAccountDto.setDefaultUser(userAccount.isDefaultUser());
        OrganizationDto organizationDto = new OrganizationDto();
        organizationDto.setId(userAccount.getOrganizationId());
        userAccountDto.setOrganizationDto(organizationDto);
        userAccountDto.setCreatedDate(userAccount.getCreatedDate());
        userAccountDto.setLastModifiedDate(userAccount.getLastModifiedDate());
        return userAccountDto;
    }

    public static UserAccount fromUserAccountDto(UserAccountDto userAccountDto) {
        if (userAccountDto == null) {
            return null;
        }
        UserAccount userAccount = new UserAccount();
        userAccount.setStatus(userAccountDto.getStatus());
        userAccount.setId(userAccountDto.getId());
        userAccount.setUserAccountType(userAccountDto.getUserAccountType());
        userAccount.setUserName(userAccountDto.getUserName());
        userAccount.setPassword(userAccountDto.getPassword());
        userAccount.setDefaultUser(userAccountDto.isDefaultUser() ? 1 : 0);
        userAccount.setOrganizationId(userAccountDto.getOrganizationDto() != null ? userAccountDto.getOrganizationDto().getId() : null);
        return userAccount;
    }

    public static InvoiceDto toInvoiceDto(Invoice invoice) {
        if (invoice == null) {
            return null;
        }
        InvoiceDto invoiceDto = new InvoiceDto();
        invoiceDto.setId(invoice.getId());
        invoiceDto.setInvoiceType(invoice.getInvoiceType());
        invoiceDto.setTimesheetDto(new TimesheetDto(invoice.getTimesheetId()));
        invoiceDto.setStatus(invoice.getStatus());
        invoiceDto.setAmount(invoice.getAmount());
        invoiceDto.setBillingDate(invoice.getBillingDate());
        UserAccountDto userAccountDto = new UserAccountDto();
        userAccountDto.setId(invoice.getIssuerId());
        invoiceDto.setInvoiceIssuer(userAccountDto);
        ClientDto clientDto = new ClientDto(null);
        clientDto.setId(invoice.getRecipientId());
        invoiceDto.setInvoiceRecipient(clientDto);
        invoiceDto.setInvoiceNumber(invoice.getInvoiceNumber());
        invoiceDto.setCurrency(invoice.getCurrency());
        invoiceDto.setDueDate(invoice.getDueDate());
        invoiceDto.setAmount(invoice.getAmount());
        invoiceDto.setReference(invoice.getReference());
        invoiceDto.setVatAmount(invoice.getVatAmount());
        invoiceDto.setVatPercent(invoice.getVatPercent());
        invoiceDto.setBillingPeriodStartDate(invoice.getBillingPeriodStartDate());
        invoiceDto.setBillingPeriodEndDate(invoice.getBillingPeriodEndDate());
        return invoiceDto;
    }

    public static List<IntegrationDto> toIntegrationDtoList(List<Integration> integrationList) {
        if (integrationList == null) {
            return new ArrayList<>();
        }
        return integrationList.stream().map(TimesheetMapper::toIntegrationDto).collect(Collectors.toList());
    }

    public static IntegrationDto toIntegrationDto(Integration integration) {
        if (integration == null) {
            return null;
        }
        IntegrationDto integrationDto = new IntegrationDto();
        integrationDto.setId(integration.getId());
        integrationDto.setStatus(integration.getStatus());
        integrationDto.setSystem(integration.getSystem());
        integrationDto.setBaseUrl(integration.getBaseUrl());
        integrationDto.setIntegrationType(integration.getIntegrationType());
        integrationDto.setUserName(integration.getUserName());
        integrationDto.setPassword(integration.getPassword());
        integrationDto.setSchemaUrl(integration.getSchemaUrl());
        integrationDto.setAuthenticationType(integration.getAuthenticationType());
        integrationDto.setAccessToken(integration.getAccessToken());
        integrationDto.setReadTimeoutMs(integration.getReadTimeoutMs());
        integrationDto.setConnectionTimeoutMs(integration.getConnectionTimeoutMs());
        integrationDto.setHttpHeaderContentType(integration.getHttpHeaderContentType());
        return integrationDto;
    }


    public static Integration fromIntegrationDto(IntegrationDto integrationDto) {
        if (integrationDto == null) {
            return null;
        }
        Integration integration = new Integration();
        integration.setId(integrationDto.getId());
        integration.setStatus(integrationDto.getStatus());
        integration.setSystem(integrationDto.getSystem());
        integration.setBaseUrl(integrationDto.getBaseUrl());
        integration.setIntegrationType(integrationDto.getIntegrationType());
        integration.setUserName(integrationDto.getUserName());
        integration.setPassword(integrationDto.getPassword());
        integration.setSchemaUrl(integrationDto.getSchemaUrl());
        integration.setAuthenticationType(integrationDto.getAuthenticationType());
        integration.setAccessToken(integrationDto.getAccessToken());
        integration.setReadTimeoutMs(integrationDto.getReadTimeoutMs());
        integration.setConnectionTimeoutMs(integrationDto.getConnectionTimeoutMs());
        integration.setHttpHeaderContentType(integrationDto.getHttpHeaderContentType());
        return integration;
    }
}
