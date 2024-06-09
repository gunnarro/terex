package com.gunnarro.android.terex;

import com.gunnarro.android.terex.domain.dto.BusinessAddressDto;
import com.gunnarro.android.terex.domain.dto.ClientDto;
import com.gunnarro.android.terex.domain.dto.ContactInfoDto;
import com.gunnarro.android.terex.domain.dto.OrganizationDto;
import com.gunnarro.android.terex.domain.dto.PersonDto;
import com.gunnarro.android.terex.domain.dto.ProjectDto;
import com.gunnarro.android.terex.domain.dto.UserAccountDto;
import com.gunnarro.android.terex.domain.entity.Project;
import com.gunnarro.android.terex.domain.entity.Timesheet;
import com.gunnarro.android.terex.domain.entity.TimesheetEntry;
import com.gunnarro.android.terex.domain.entity.UserAccount;
import com.gunnarro.android.terex.utility.Utility;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class TestData {
    public static List<TimesheetEntry> createTimesheetEntriesForMonth(Long timesheetId, Integer year, Integer month) {
        List<TimesheetEntry> timesheetEntryList = new ArrayList<>();
        // get timesheet
        Timesheet timesheet = new Timesheet();
        timesheet.setFromDate(LocalDate.of(year, month, 1));
        timesheet.setToDate(LocalDate.of(year, month, Utility.getLastDayOfMonth(LocalDate.of(year, month, 1)).getDayOfMonth()));
        // interpolate missing days in the month.
        TimesheetEntry timesheetEntry = new TimesheetEntry();
        timesheetEntry.setTimesheetId(timesheetId);
        for (LocalDate date = timesheet.getFromDate(); date.isBefore(timesheet.getToDate()); date = date.plusDays(1)) {
            boolean isInList = false;
            for (TimesheetEntry e : timesheetEntryList) {
                if (e.getWorkdayDate().equals(date)) {
                    isInList = true;
                    break;
                }
            }
            if (!isInList) {
                TimesheetEntry timesheetEntry1 = new TimesheetEntry();
                timesheetEntry1.setTimesheetId(timesheetId);
                timesheetEntry1.setStatus(TimesheetEntry.TimesheetEntryStatusEnum.OPEN.name());
                timesheetEntry1.setType(TimesheetEntry.TimesheetEntryTypeEnum.REGULAR.name());
                timesheetEntry1.setWorkdayDate(date);
                timesheetEntry1.setFromTime(LocalTime.of(7, 0));
                timesheetEntry1.setToTime(LocalTime.of(3, 0));
                timesheetEntry1.setBreakInMin(30);
                timesheetEntry1.setWorkedMinutes(420);
                timesheetEntry1.setWorkdayWeek(date.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear()));
                timesheetEntryList.add(timesheetEntry1);
            }
        }
        timesheetEntryList.sort(Comparator.comparing(TimesheetEntry::getWorkdayDate));
        return timesheetEntryList;
    }

    public static UserAccountDto createUserAccountDto(Long id, String userName) {
        UserAccountDto userAccountDto = new UserAccountDto();
        userAccountDto.setId(id);
        userAccountDto.setDefaultUser(true);
        userAccountDto.setUserName(userName);
        userAccountDto.setPassword("nope");
        userAccountDto.setUserAccountType(UserAccount.UserAccountTypeEnum.BUSINESS.name());
        OrganizationDto organizationDto = new OrganizationDto();
        organizationDto.setId(11L);
        userAccountDto.setOrganizationDto(organizationDto);
        return userAccountDto;
    }

    public static ClientDto createClientDto(Long id, String name) {
        ClientDto clientDto = new ClientDto();
        clientDto.setId(id);
        clientDto.setName(name);
        clientDto.setStatus("ACTIVE");
        return clientDto;
    }

    public static ProjectDto createProjectDto(Long id, Long clientId, String projectName) {
        ProjectDto newProjectDto = new ProjectDto();
        newProjectDto.setClientId(clientId);
        newProjectDto.setName(projectName);
        newProjectDto.setId(id);
        newProjectDto.setDescription("some project description");
        newProjectDto.setStatus(Project.ProjectStatusEnum.ACTIVE.name());
        newProjectDto.setHourlyRate(1250);
        return newProjectDto;
    }

    public static OrganizationDto createOrganizationDto(String id, String organizationName, String organizationNumber) {
        OrganizationDto organizationDto = new OrganizationDto();
        organizationDto.setId(null);
        organizationDto.setName(organizationName);
        organizationDto.setOrganizationNumber(organizationNumber);
        organizationDto.setBankAccountNumber("23232323");
        organizationDto.setIndustryType("SOFTWARE");

        ContactInfoDto contactInfoDto = new ContactInfoDto();
        contactInfoDto.setEmailAddress("my@org.com");
        contactInfoDto.setMobileNumberCountryCode("+47");
        contactInfoDto.setMobileNumber("22334455");

        PersonDto contectPersonDto = new PersonDto();
        contectPersonDto.setFullName("ole hansem");

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

    private static PersonDto createPersonDto(Long id, String fullName) {
        PersonDto personDto = new PersonDto();
        personDto.setId(id);
        personDto.setFullName(fullName);
        return personDto;
    }
}
