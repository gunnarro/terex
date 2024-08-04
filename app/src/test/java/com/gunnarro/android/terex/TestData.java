package com.gunnarro.android.terex;

import androidx.annotation.NonNull;

import com.gunnarro.android.terex.domain.dto.BusinessAddressDto;
import com.gunnarro.android.terex.domain.dto.ClientDto;
import com.gunnarro.android.terex.domain.dto.ContactInfoDto;
import com.gunnarro.android.terex.domain.dto.OrganizationDto;
import com.gunnarro.android.terex.domain.dto.PersonDto;
import com.gunnarro.android.terex.domain.dto.ProjectDto;
import com.gunnarro.android.terex.domain.dto.UserAccountDto;
import com.gunnarro.android.terex.domain.entity.Timesheet;
import com.gunnarro.android.terex.domain.entity.TimesheetEntry;
import com.gunnarro.android.terex.domain.entity.TimesheetSummary;
import com.gunnarro.android.terex.domain.entity.UserAccount;
import com.gunnarro.android.terex.utility.Utility;

import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.Year;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.ValueRange;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestData {

    public static List<TimesheetEntry> createTimesheetEntriesForMonth(Long timesheetId, LocalDate workDay) {
        List<TimesheetEntry> timesheetEntryList = new ArrayList<>();
        // get timesheet
        Timesheet timesheet = new Timesheet();
        timesheet.setFromDate(LocalDate.of(workDay.getYear(), workDay.getMonthValue(), 1));
        timesheet.setToDate(LocalDate.of(workDay.getYear(), workDay.getMonthValue(), Utility.getLastDayOfMonth(LocalDate.of(workDay.getYear(), workDay.getMonthValue(), 1)).getDayOfMonth()));
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
                timesheetEntry1.setStartTime(LocalTime.of(7, 0, 0));
                timesheetEntry1.setWorkedSeconds((long) 420 * 60);
                timesheetEntry1.setWorkdayWeek(date.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear()));
                timesheetEntryList.add(timesheetEntry1);
            }
        }
        timesheetEntryList.sort(Comparator.comparing(TimesheetEntry::getWorkdayDate));
        return timesheetEntryList;
    }

    public static List<TimesheetSummary> buildTimesheetSummaryByWeek(Long timesheetId, Long projectId, Integer year, Integer month, Integer hourlyRate) {
        Map<Integer, List<TimesheetEntry>> weekMap = new HashMap<>();
        generateTimesheetEntries(year, month, projectId, List.of(), List.of()).forEach(t -> {
            int week = getWeek(t.getWorkdayDate());
            if (!weekMap.containsKey(week)) {
                weekMap.put(week, new ArrayList<>());
            }
            Objects.requireNonNull(weekMap.get(week)).add(t);
        });
        List<TimesheetSummary> timesheetSummaryByWeek = new ArrayList<>();

        weekMap.forEach((k, e) -> {
            TimesheetSummary timesheetSummary = new TimesheetSummary();
            timesheetSummary.setTimesheetId(timesheetId);
            timesheetSummary.setCreatedDate(LocalDateTime.now());
            timesheetSummary.setLastModifiedDate(LocalDateTime.now());
            timesheetSummary.setFromDate(e.get(0).getWorkdayDate());
            timesheetSummary.setToDate(e.get(e.size() - 1).getWorkdayDate());
            timesheetSummary.setYear(year);
            timesheetSummary.setWeekInYear(k);
            timesheetSummary.setCurrency("NOK");
            timesheetSummary.setTotalWorkedDays(e.size());
            timesheetSummaryByWeek.add(timesheetSummary);
            Objects.requireNonNull(weekMap.get(k)).forEach(t -> {
                timesheetSummary.setTotalBilledAmount(timesheetSummary.getTotalBilledAmount() + (hourlyRate * ((double) t.getWorkedSeconds() / 3600)));
                timesheetSummary.setTotalWorkedHours(timesheetSummary.getTotalWorkedHours() + (double) t.getWorkedSeconds() / 3600);
            });
        });
        return timesheetSummaryByWeek;
    }

    /**
     * @param year  current year
     * @param month from january to december, for example Month.MARCH
     */
    public static List<TimesheetEntry> generateTimesheetEntries(Integer year, Integer month, Long projectId, List<Integer> sickDates, List<Integer> vacationDates) {
        return getWorkingDays(year, month).stream().map(e -> TestData.createTimesheetEntry(e, projectId, sickDates, vacationDates)).collect(Collectors.toList());
    }

    private static TimesheetEntry createTimesheetEntry(LocalDate day, Long projectId, List<Integer> sickDates, List<Integer> vacationDates) {
        TimesheetEntry timesheetEntry = TimesheetEntry.createDefault(new java.util.Random().nextLong(), projectId, day);
        if (sickDates.contains(timesheetEntry.getWorkdayDate().getDayOfMonth())) {
            timesheetEntry.setType(TimesheetEntry.TimesheetEntryTypeEnum.SICK.name());
            timesheetEntry.setStartTime(null);
          //  timesheetEntry.setEndTime(null);
            timesheetEntry.setWorkedSeconds(null);
        } else if (vacationDates.contains(timesheetEntry.getWorkdayDate().getDayOfMonth())) {
            timesheetEntry.setType(TimesheetEntry.TimesheetEntryTypeEnum.VACATION.name());
            timesheetEntry.setStartTime(null);
            //timesheetEntry.setEndTime(null);
            timesheetEntry.setWorkedSeconds(null);
        }
        return timesheetEntry;
    }

    public static List<LocalDate> getWorkingDays(Integer year, Integer month) {
        // validate year and mount
        try {
            Year.of(year);
        } catch (DateTimeException e) {
            throw new RuntimeException(e.getMessage());
        }
        try {
            Month.of(month);
        } catch (DateTimeException e) {
            throw new RuntimeException(e.getMessage());
        }

        LocalDate startDate = LocalDate.of(Year.of(year).getValue(), Month.of(month).getValue(), 1);
        ValueRange range = startDate.range(ChronoField.DAY_OF_MONTH);
        LocalDate endDate = startDate.withDayOfMonth((int) range.getMaximum());
        System.out.print("======================================== " + endDate.toString() + ", range " + range.getMaximum() + ", " + endDate.getDayOfWeek());

        Predicate<LocalDate> isWeekend = date -> date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY;
        // pluss 1in orer to include the last day of the mount
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate.plusDays(1));
        return Stream.iterate(startDate, date -> date.plusDays(1)).limit(daysBetween).filter(isWeekend.negate()).collect(Collectors.toList());
    }

    private static int getWeek(LocalDate date) {
        TemporalField woy = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
        return date.get(woy);
    }

    public static OrganizationDto createOrganizationDto(Long orgId, String orgName, String orgNumber) {
        OrganizationDto organizationDto = new OrganizationDto();
        organizationDto.setId(orgId);
        organizationDto.setName(orgName);
        organizationDto.setOrganizationNumber(orgNumber);
        organizationDto.setBankAccountNumber("6011 12 23456");
        organizationDto.setIndustryType("SOFTWARE");

        ContactInfoDto contactInfoDto = new ContactInfoDto();
        contactInfoDto.setEmailAddress("my@org.com");
        contactInfoDto.setMobileNumberCountryCode("+47");
        contactInfoDto.setMobileNumber("22334455");

        PersonDto contectPersonDto = new PersonDto();
        contectPersonDto.setFullName("ole hansen");

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

    public static UserAccount createUserAccount(Long id) {
        UserAccount userAccount = new UserAccount();
        userAccount.setId(id);
        userAccount.setStatus(UserAccount.UserAccountStatusEnum.ACTIVE.name());
        userAccount.setUserName("guro");
        userAccount.setPassword("nope");
        userAccount.setDefaultUser(1);
        userAccount.setUserAccountType(UserAccount.UserAccountTypeEnum.BUSINESS.name());
        userAccount.setOrganizationId(11L);
        return userAccount;
    }

    public static UserAccountDto createUserAccountDto(Long id, String userName) {
        UserAccountDto userAccountDto = new UserAccountDto();
        userAccountDto.setId(id);
        userAccountDto.setUserName(userName);
        userAccountDto.setPassword("nope");
        userAccountDto.setDefaultUser(true);
        userAccountDto.setUserAccountType(UserAccount.UserAccountTypeEnum.BUSINESS.name());
        userAccountDto.setOrganizationDto(TestData.createOrganizationDto(1L, "gunnarro as", "1122334455"));
        return userAccountDto;
    }

    public static ClientDto createClientDto(Long id, String orgName) {
        ClientDto clientDto = new ClientDto(null);
        clientDto.setId(id);
        clientDto.setName(orgName);
        clientDto.setStatus("ACTIVE");
        clientDto.setContactPersonDto(createContactPerson(1234L, "contact person full name"));
        return clientDto;
    }

    public static PersonDto createContactPerson(Long id, String fullName) {
        PersonDto contactPersonDto = new PersonDto();
        contactPersonDto.setId(id);
        contactPersonDto.setFullName(fullName);
        ContactInfoDto contactInfoDto = new ContactInfoDto();
        contactInfoDto.setEmailAddress(fullName + "@yahoo.org");
        contactInfoDto.setMobileNumberCountryCode("+47");
        contactInfoDto.setMobileNumber("44556677");
        contactPersonDto.setContactInfo(contactInfoDto);
        return contactPersonDto;
    }

    public static ProjectDto createProjectDto(Long id, Long clientId, String name) {
        ProjectDto projectDto = new ProjectDto();
        projectDto.setId(id);
        projectDto.setName(name);
        projectDto.setDescription("project description");
        projectDto.setClientDto(new ClientDto(clientId));
        projectDto.setHourlyRate(1000);
        return projectDto;
    }

    @NonNull
    public static TimesheetSummary createTimesheetSummary(Long timesheetId) {
        TimesheetSummary timesheetSummaryWeek1 = new TimesheetSummary();
        timesheetSummaryWeek1.setTimesheetId(timesheetId);
        timesheetSummaryWeek1.setCurrency("NOK");
        timesheetSummaryWeek1.setYear(2023);
        timesheetSummaryWeek1.setWeekInYear(40);
        timesheetSummaryWeek1.setTotalSickLeaveDays(0);
        timesheetSummaryWeek1.setTotalVacationDays(0);
        timesheetSummaryWeek1.setTotalWorkedDays(5);
        timesheetSummaryWeek1.setTotalWorkedHours(37.5);
        timesheetSummaryWeek1.setTotalBilledAmount(25000d);
        return timesheetSummaryWeek1;
    }

    public static BusinessAddressDto createBusinessAddressDto(Long id) {
        BusinessAddressDto businessAddressDto = new BusinessAddressDto();
        businessAddressDto.setId(id);
        businessAddressDto.setCity("oslo");
        businessAddressDto.setPostalCode("0880");
        businessAddressDto.setStreetAddress("persgate 34");
        businessAddressDto.setCountry("Norge");
        businessAddressDto.setCountryCode("no");
        return businessAddressDto;
    }
}
