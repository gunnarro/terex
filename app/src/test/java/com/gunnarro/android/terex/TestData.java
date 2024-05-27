package com.gunnarro.android.terex;

import com.gunnarro.android.terex.domain.dto.BusinessAddressDto;
import com.gunnarro.android.terex.domain.dto.ClientDto;
import com.gunnarro.android.terex.domain.dto.ContactInfoDto;
import com.gunnarro.android.terex.domain.dto.OrganizationDto;
import com.gunnarro.android.terex.domain.dto.PersonDto;
import com.gunnarro.android.terex.domain.dto.UserAccountDto;
import com.gunnarro.android.terex.domain.entity.TimesheetEntry;
import com.gunnarro.android.terex.domain.entity.TimesheetSummary;
import com.gunnarro.android.terex.domain.entity.UserAccount;

import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.ValueRange;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestData {

    public static List<TimesheetSummary> buildTimesheetSummaryByWeek(Long timesheetId, Integer year, Integer month, Integer hourlyRate) {
        Map<Integer, List<TimesheetEntry>> weekMap = new HashMap<>();
        generateTimesheetEntries(year, month).forEach(t -> {
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
                timesheetSummary.setTotalBilledAmount(timesheetSummary.getTotalBilledAmount() + (hourlyRate * ((double) t.getWorkedMinutes() / 60)));
                timesheetSummary.setTotalWorkedHours(timesheetSummary.getTotalWorkedHours() + (double) t.getWorkedMinutes() / 60);
            });
        });
        return timesheetSummaryByWeek;
    }

    /**
     * @param year  current year
     * @param month from january to december, for example Month.MARCH
     */
    public static List<TimesheetEntry> generateTimesheetEntries(Integer year, Integer month) {
        return getWorkingDays(year, month).stream().map(TestData::createTimesheetEntry).collect(Collectors.toList());
    }

    private static TimesheetEntry createTimesheetEntry(LocalDate day) {
        return TimesheetEntry.createDefault(new java.util.Random().nextLong(), day);
    }

    private static List<LocalDate> getWorkingDays(Integer year, Integer month) {
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

        Predicate<LocalDate> isWeekend = date -> date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY;

        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
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

    public static UserAccountDto createUserAccountDto(Long id, String userName) {
        UserAccountDto userAccount = new UserAccountDto();
        userAccount.setId(id);
        userAccount.setUserName(userName);
        userAccount.setPassword("nope");
        userAccount.setUserAccountType(UserAccount.UserAccountTypeEnum.BUSINESS.name());
        OrganizationDto organizationDto = new OrganizationDto();
        organizationDto.setId(11L);
        userAccount.setOrganizationDto(organizationDto);
        return userAccount;
    }

    public static ClientDto createClientDto(Long id, String orgName) {
        ClientDto clientDto = new ClientDto();
        clientDto.setId(id);
        clientDto.setName(orgName);
        clientDto.setStatus("ACTIVE");
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
}
