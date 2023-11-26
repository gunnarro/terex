package com.gunnarro.android.terex.service;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.room.Transaction;

import com.gunnarro.android.terex.domain.TimesheetMapper;
import com.gunnarro.android.terex.domain.dto.TimesheetEntryDto;
import com.gunnarro.android.terex.domain.dto.TimesheetInfoDto;
import com.gunnarro.android.terex.domain.entity.Address;
import com.gunnarro.android.terex.domain.entity.Company;
import com.gunnarro.android.terex.domain.entity.Contact;
import com.gunnarro.android.terex.domain.entity.Person;
import com.gunnarro.android.terex.domain.entity.Timesheet;
import com.gunnarro.android.terex.domain.entity.TimesheetEntry;
import com.gunnarro.android.terex.domain.entity.TimesheetSummary;
import com.gunnarro.android.terex.domain.entity.TimesheetWithEntries;
import com.gunnarro.android.terex.exception.InputValidationException;
import com.gunnarro.android.terex.exception.TerexApplicationException;
import com.gunnarro.android.terex.repository.TimesheetRepository;
import com.gunnarro.android.terex.utility.Utility;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TimesheetService {

    private final TimesheetRepository timesheetRepository;

    /**
     * for unit test only
     */
    public TimesheetService(TimesheetRepository timesheetRepository) {
        this.timesheetRepository = timesheetRepository;
    }

    @Inject
    public TimesheetService(Context applicationContext) {
        timesheetRepository = new TimesheetRepository(applicationContext);
    }


    // ----------------------------------------
    // timesheet
    // ----------------------------------------

    public TimesheetInfoDto getTimesheetInfo(Long timesheetId) {
        Timesheet timesheet = getTimesheet(timesheetId);
        Integer sumDays = timesheetRepository.getRegisteredWorkedDays(timesheetId);
        Integer sumHours = timesheetRepository.getRegisteredWorkedHours(timesheetId);
        return TimesheetMapper.toTimesheetInfoDto(timesheet, sumDays, sumHours);
    }

    public List<Timesheet> getTimesheets(String status) {
        return timesheetRepository.getTimesheets(status);
    }

    public Timesheet getTimesheet(Long timesheetId) {
        return timesheetRepository.getTimesheet(timesheetId);
    }

    public LiveData<List<Timesheet>> getTimesheetListLiveData(Integer year) {
        return timesheetRepository.getTimesheetByYear(year);
    }

    public void deleteTimesheet(Timesheet timesheet) {
        if (timesheet.isBilled()) {
            throw new TerexApplicationException("Timesheet is BILLED, not allowed to delete or update", "40045", null);
        }
        timesheetRepository.deleteTimesheet(timesheet);
    }

    public Long saveTimesheet(Timesheet timesheet) {
        Timesheet timesheetExisting = timesheetRepository.getTimesheet(timesheet.getClientName(), timesheet.getProjectCode(), timesheet.getYear(), timesheet.getMonth());
        if (timesheetExisting != null && timesheet.isNew()) {
            throw new InputValidationException(String.format("timesheet already exist, timesheetId=%s, status=%s", timesheetExisting.getId(), timesheetExisting.getStatus()), "40040", null);

        }
        // first of all, check status
        if (timesheetExisting != null && timesheetExisting.isBilled()) {
            Log.d("", "timesheet is already billed, no changes is allowed. timesheetId=" + timesheetExisting.getId() + " " + timesheetExisting.getStatus());
            throw new InputValidationException(String.format("timesheet is already billed, no changes is allowed. timesheetId=%s, status=%s", timesheetExisting.getId(), timesheetExisting.getStatus()), "40040", null);
        }
        try {
            Log.d("TimesheetRepository.saveTimesheet", String.format("existingTimesheet: %s", timesheetExisting));
            timesheet.setWorkingDaysInMonth(Utility.countBusinessDaysInMonth(timesheet.getFromDate()));
            timesheet.setWorkingHoursInMonth((int) (timesheet.getWorkingDaysInMonth() * 7.5));
            Long id = null;
            if (timesheetExisting == null) {
                // this is a new timesheet
                timesheet.setCreatedDate(LocalDateTime.now());
                timesheet.setLastModifiedDate(LocalDateTime.now());
                id = timesheetRepository.insertTimesheet(timesheet);
                Log.d("TimesheetRepository.saveTimesheet", String.format("inserted new timesheetId=%s, %s", id, timesheet));
            } else {
                // this is a update of existing timesheet
                timesheet.setLastModifiedDate(LocalDateTime.now());
                // FIXME should not happen
                if (timesheet.getId() == null) {
                    timesheet.setId(timesheetExisting.getId());
                }
                timesheetRepository.updateTimesheet(timesheet);
                Log.d("TimesheetRepository.saveTimesheet", String.format("updated timesheet: %s", timesheet));
            }
            return id;
        } catch (InterruptedException | ExecutionException e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            e.printStackTrace();
            throw new TerexApplicationException("Error saving timesheet!", e.getMessage(), e.getCause());
        }
    }

    // ----------------------------------------
    // timesheet entry
    // ----------------------------------------

    public Timesheet updateTimesheetWorkedHoursAndDays(Long timesheetId) {
        Timesheet timesheet = timesheetRepository.getTimesheet(timesheetId);
        List<TimesheetEntry> timesheetEntries = timesheetRepository.getTimesheetEntryList(timesheetId);
        int workedDays = 0;
        int workedMinutes = 0;
        for (TimesheetEntry e : timesheetEntries) {
            workedDays++;
            workedMinutes += e.getWorkedMinutes();
        }
        timesheet.setTotalWorkedDays(workedDays);
        timesheet.setTotalWorkedHours(workedMinutes / 60);
        if (timesheet.isNew() || timesheet.isActive()) {
            if (timesheet.getTotalWorkedDays() >= timesheet.getWorkingDaysInMonth() || timesheet.getTotalWorkedHours() >= timesheet.getWorkingHoursInMonth()) {
                timesheet.setStatus(Timesheet.TimesheetStatusEnum.COMPLETED.name());
            }
        }
        return timesheet;
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    @Transaction
    public void saveTimesheetEntry(@NotNull final TimesheetEntry timesheetEntry) {
        try {
            TimesheetEntry timesheetEntryExisting = timesheetRepository.getTimesheetEntry(timesheetEntry.getTimesheetId(), timesheetEntry.getWorkdayDate());

            if (timesheetEntryExisting != null && timesheetEntry.isNew()) {
                throw new TerexApplicationException(String.format("timesheet entry already exist, timesheetId=%s, status=%s", timesheetEntryExisting.getId(), timesheetEntryExisting.getStatus()), "40040", null);

            }
            // first of all, check status
            if (timesheetEntryExisting != null && timesheetEntryExisting.isBilled()) {
                throw new TerexApplicationException(String.format("timesheet entry have status billed, no changes is allowed. timesheetId=%s, status=%s", timesheetEntryExisting.getId(), timesheetEntryExisting.getStatus()), "40040", null);
            }

            // validate work date, must be between the to and from date range of the timesheet
            Timesheet timesheet = timesheetRepository.getTimesheet(timesheetEntry.getTimesheetId());
            if (timesheetEntry.getWorkdayDate().isBefore(timesheet.getFromDate()) || timesheetEntry.getWorkdayDate().isAfter(timesheet.getToDate())) {
                throw new TerexApplicationException(String.format("timesheet entry work date not in the to and from date range of the timesheet. %s <> %s - %s", timesheetEntry.getWorkdayDate(), timesheet.getFromDate(), timesheet.getToDate()), "40040", null);
            }

            Log.d("TimesheetRepository.saveTimesheetEntry", String.format("%s", timesheetEntryExisting));
            // set the timesheet work date week number here, this is only used to simplify accumulate timesheet by week by the invoice service
            timesheetEntry.setWorkdayWeek(timesheetEntry.getWorkdayDate().get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear()));
            Long id;
            if (timesheetEntryExisting == null) {
                timesheetEntry.setCreatedDate(LocalDateTime.now());
                timesheetEntry.setLastModifiedDate(LocalDateTime.now());
                id = timesheetRepository.insertTimesheetEntry(timesheetEntry);
                Log.d("TimesheetRepository.saveTimesheetEntry", "insert new timesheet entry: " + id + " - " + timesheetEntry.getWorkdayDate());
            } else {
                timesheetEntry.setId(timesheetEntryExisting.getId());
                timesheetEntry.setCreatedDate(timesheetEntryExisting.getCreatedDate());
                timesheetRepository.updateTimesheetEntry(timesheetEntry);
                id = timesheetEntry.getId();
                Log.d("TimesheetRepository.saveTimesheetEntry", "update timesheet entry: " + id + " - " + timesheetEntry.getWorkdayDate());
            }
        } catch (InterruptedException | ExecutionException e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            e.printStackTrace();
            throw new TerexApplicationException("Error saving timesheet entry!", e.getMessage(), e.getCause());
        }
    }

    public void deleteTimesheetEntry(TimesheetEntry timesheetEntry) {
        if (timesheetEntry.isBilled()) {
            throw new TerexApplicationException("Timesheet entry is closed, not allowed to delete or update", "40045", null);
        }
        timesheetRepository.deleteTimesheetEntry(timesheetEntry);
    }

    public TimesheetEntry getMostRecentTimeSheetEntry(Long timesheetId) {
        return timesheetRepository.getMostRecentTimeSheetEntry(timesheetId);
    }

    public TimesheetWithEntries getTimesheetWithEntries(Long timesheetId) {
        return timesheetRepository.getTimesheetWithEntries(timesheetId);
    }

    public LiveData<List<TimesheetEntry>> getTimesheetEntryListLiveData(Long timesheetId) {
        return timesheetRepository.getTimesheetEntryListLiveData(timesheetId);
    }

    public List<TimesheetEntry> getTimesheetEntryList(Long timesheetId) {
        return timesheetRepository.getTimesheetEntryList(timesheetId);
    }

    // ----------------------------------------
    // timesheet summary
    // ----------------------------------------

    public Long saveTimesheetSummary(TimesheetSummary timesheetSummary) {
        return timesheetRepository.saveTimesheetSummary(timesheetSummary);
    }

    /**
     * Used as invoice attachment
     */
    public List<TimesheetSummary> createTimesheetSummary(@NotNull Long timesheetId) {
        // check timesheet status
        Timesheet timesheet = getTimesheet(timesheetId);
        if (!timesheet.isCompleted()) {
            throw new TerexApplicationException(String.format("Application error, timesheet not fulfilled! timesheetId=%s, status=%s", timesheetId, timesheet.getStatus()), "50023", null);
        }
        Log.d("createInvoiceSummary", String.format("timesheetId=%s", timesheetId));
        List<TimesheetEntry> timesheetEntryList = getTimesheetEntryList(timesheetId);
        if (timesheetEntryList == null || timesheetEntryList.isEmpty()) {
            throw new TerexApplicationException(String.format("Application error, timesheet not ready for billing, no entries found! timesheetId=%s, status=%s", timesheetId, timesheet.getStatus()), "50023", null);
        }
        Log.d("createInvoiceSummary", "timesheet entries: " + timesheetEntryList);
        // accumulate timesheet by week for the mount
        Map<Integer, List<TimesheetEntry>> timesheetWeekMap = timesheetEntryList.stream().collect(Collectors.groupingBy(TimesheetEntry::getWorkdayWeek));
        List<TimesheetSummary> invoiceSummaryByWeek = new ArrayList<>();
        timesheetWeekMap.forEach((k, e) -> {
            invoiceSummaryByWeek.add(buildTimesheetSummaryForWeek(timesheetId, k, e));
        });
        Log.d("createInvoiceSummary", "timesheet summary by week: " + invoiceSummaryByWeek);
        // close the timesheet after invoice have been generated, is not possible to do any form of changes on the time list.
        timesheet.setStatus(Timesheet.TimesheetStatusEnum.BILLED.name());
        saveTimesheet(timesheet);

        // then close all timesheet entries by setting status to billed
        timesheetEntryList.forEach(e -> {
            timesheetRepository.closeTimesheetEntry(e.getId());
        });

        invoiceSummaryByWeek.sort(Comparator.comparing(TimesheetSummary::getWeekInYear));
        return invoiceSummaryByWeek;
    }

    private TimesheetSummary buildTimesheetSummaryForMonth(@NotNull Long timesheetId, @NotNull List<TimesheetEntry> timesheetEntryList) {
        TimesheetSummary timesheetSummary = new TimesheetSummary();
        timesheetSummary.setCreatedDate(LocalDateTime.now());
        timesheetSummary.setLastModifiedDate(LocalDateTime.now());
        timesheetSummary.setTimesheetId(timesheetId);
        timesheetSummary.setYear(timesheetEntryList.get(0).getWorkdayDate().getYear());
        timesheetSummary.setFromDate(Utility.getFirstDayOfMonth(timesheetEntryList.get(0).getWorkdayDate()));
        timesheetSummary.setToDate(Utility.getLastDayOfMonth(timesheetEntryList.get(0).getWorkdayDate()));
        timesheetSummary.setTotalWorkedDays(timesheetEntryList.size());
        timesheetEntryList.forEach(t -> {
            timesheetSummary.setTotalBilledAmount(timesheetSummary.getTotalBilledAmount() + (t.getHourlyRate() * ((double) t.getWorkedMinutes() / 60)));
            timesheetSummary.setTotalWorkedHours(timesheetSummary.getTotalWorkedHours() + (double) t.getWorkedMinutes() / 60);
        });
        return timesheetSummary;
    }

    private TimesheetSummary buildTimesheetSummaryForWeek(@NotNull Long
                                                                  timesheetId, @NotNull Integer week, @NotNull List<TimesheetEntry> timesheetEntryList) {
        TimesheetSummary timesheetSummary = new TimesheetSummary();
        timesheetSummary.setCreatedDate(LocalDateTime.now());
        timesheetSummary.setLastModifiedDate(LocalDateTime.now());
        timesheetSummary.setTimesheetId(timesheetId);
        timesheetSummary.setWeekInYear(week);
        timesheetSummary.setYear(timesheetEntryList.get(0).getWorkdayDate().getYear());
        timesheetSummary.setFromDate(Utility.getFirstDayOfWeek(timesheetEntryList.get(0).getWorkdayDate(), week));
        timesheetSummary.setToDate(Utility.getLastDayOfWeek(timesheetEntryList.get(0).getWorkdayDate(), week));
        timesheetSummary.setTotalWorkedDays(timesheetEntryList.size());
        timesheetEntryList.forEach(t -> {
            timesheetSummary.setTotalBilledAmount(timesheetSummary.getTotalBilledAmount() + (t.getHourlyRate() * ((double) t.getWorkedMinutes() / 60)));
            timesheetSummary.setTotalWorkedHours(timesheetSummary.getTotalWorkedHours() + (double) t.getWorkedMinutes() / 60);
        });
        return timesheetSummary;
    }

    private TimesheetEntryDto mapToTimesheetEntryDto(TimesheetEntry timesheetEntry) {
        TimesheetEntryDto timesheetEntryDto = new TimesheetEntryDto();
        timesheetEntryDto.setComments(timesheetEntry.getComment());
        timesheetEntryDto.setFromTime(timesheetEntry.getFromTime().toString());
        timesheetEntryDto.setToTime(timesheetEntry.getToTime().toString());
        timesheetEntryDto.setWorkdayDate(timesheetEntry.getWorkdayDate());
        return timesheetEntryDto;
    }

    public static Company getCompany(Long timesheetId) {
        Company company = new Company();
        company.setId(10L);
        company.setName("gunnarro:as");
        company.setOrganizationNumber("828 707 922");
        company.setBankAccountNumber("9230 26 98831");
        Address address = new Address();
        address.setStreetNumber("35");
        address.setStreetName("Stavangergata");
        address.setPostCode("0467");
        address.setCity("Oslo");
        address.setCountryCode("no");
        company.setBusinessAddress(address);
        return company;
    }

    public static Company getClient(Long timesheetId) {
        Company client = new Company();
        client.setId(20L);
        client.setName("Norway Consulting AS");
        client.setOrganizationNumber("");
        Address address = new Address();
        address.setStreetNumber("16");
        address.setStreetName("Grensen");
        address.setPostCode("0159");
        address.setCity("Oslo");
        address.setCountryCode("no");
        client.setBusinessAddress(address);
        Person contactPerson = new Person();
        contactPerson.setFirstName("Anita");
        contactPerson.setLastName("Lundtveit");
        client.setContactPerson(contactPerson);

        Contact contactInfo = new Contact();
        client.setContactInfo(contactInfo);
        return client;
    }
}
