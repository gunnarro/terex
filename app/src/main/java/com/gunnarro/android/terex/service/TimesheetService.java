package com.gunnarro.android.terex.service;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.room.Transaction;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.gunnarro.android.terex.config.AppDatabase;
import com.gunnarro.android.terex.domain.dto.OrganizationDto;
import com.gunnarro.android.terex.domain.dto.TimesheetDto;
import com.gunnarro.android.terex.domain.dto.TimesheetEntryDto;
import com.gunnarro.android.terex.domain.dto.TimesheetSummaryDto;
import com.gunnarro.android.terex.domain.entity.Timesheet;
import com.gunnarro.android.terex.domain.entity.TimesheetEntry;
import com.gunnarro.android.terex.domain.entity.TimesheetSummary;
import com.gunnarro.android.terex.domain.entity.TimesheetWithEntries;
import com.gunnarro.android.terex.domain.mapper.TimesheetMapper;
import com.gunnarro.android.terex.exception.InputValidationException;
import com.gunnarro.android.terex.exception.TerexApplicationException;
import com.gunnarro.android.terex.repository.OrganizationRepository;
import com.gunnarro.android.terex.repository.TimesheetRepository;
import com.gunnarro.android.terex.utility.Utility;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
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

    private final OrganizationRepository organizationRepository;

    /**
     * for unit test only
     */
    public TimesheetService(TimesheetRepository timesheetRepository, OrganizationRepository organizationRepository) {
        this.timesheetRepository = timesheetRepository;
        this.organizationRepository = organizationRepository;
    }

    @Inject
    public TimesheetService() {
        timesheetRepository = new TimesheetRepository();
        organizationRepository = new OrganizationRepository();
    }


    private String loadMustacheTemplate(Context applicationContext, InvoiceService.InvoiceAttachmentTypesEnum template) {
        StringBuilder mustacheTemplateStr = new StringBuilder();
        try (InputStream fis = applicationContext.getAssets().open(template.getTemplate()); InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8); BufferedReader br = new BufferedReader(isr)) {
            br.lines().forEach(mustacheTemplateStr::append);
            return mustacheTemplateStr.toString();
        } catch (IOException e) {
            throw new TerexApplicationException("error reading mustache template", "50050", e);
        }
    }

    // ----------------------------------------
    // timesheet
    // ----------------------------------------

    public LiveData<Map<Timesheet, List<TimesheetEntry>>> getTimesheetLiveData(Long timesheetId) {
        return timesheetRepository.getTimesheetLiveData(timesheetId);
    }

    public TimesheetDto getTimesheetDto(Long timesheetId) {
        TimesheetWithEntries timesheet = getTimesheetWithEntries(timesheetId);
        Integer sumDays = timesheet.getTimesheetEntryList().size();
        Integer sumHours = timesheet.getTimesheetEntryList().stream().mapToInt(TimesheetEntry::getWorkedMinutes).sum() / 60;
        return TimesheetMapper.toTimesheetDto(timesheet.getTimesheet(), sumDays, sumHours);
    }


    public List<TimesheetDto> getTimesheetsReadyForBilling() {
        List<Timesheet> timesheetList = timesheetRepository.getTimesheets(Timesheet.TimesheetStatusEnum.COMPLETED.name());
        return TimesheetMapper.toTimesheetDtoList(timesheetList);
    }

    public List<Timesheet> getTimesheetsByStatus(String status) {
        Log.d("getTimesheetsByStatus", "status: " + status);
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
            throw new InputValidationException("Timesheet is BILLED, not allowed to delete or update", "40045", null);
        }
        timesheetRepository.deleteTimesheet(timesheet);
    }

    public Long saveTimesheet(Timesheet timesheet) {
        Log.d("saveTimesheet", String.format("%s", timesheet));
        Timesheet timesheetExisting = timesheetRepository.getTimesheet(timesheet.getClientName(), timesheet.getProjectCode(), timesheet.getYear(), timesheet.getMonth());
        if (timesheetExisting != null && timesheet.isNew()) {
            throw new InputValidationException(String.format("timesheet already exist. %s %s, status=%s", timesheetExisting.getClientName(), timesheet.getProjectCode(), timesheetExisting.getStatus()), "40040", null);
        }
        // first of all, check status
        if (timesheetExisting != null && timesheetExisting.isBilled()) {
            Log.e("", "timesheet is already billed, no changes is allowed. timesheetId=" + timesheetExisting.getId() + " " + timesheetExisting.getStatus());
            throw new InputValidationException(String.format("timesheet is already billed, no changes is allowed. %s %s, status=%s", timesheetExisting.getClientName(), timesheet.getProjectCode(), timesheetExisting.getStatus()), "40040", null);
        }
        try {
            Log.d("TimesheetRepository.saveTimesheet", String.format("existingTimesheet: %s", timesheetExisting));
            Long id = null;
            if (timesheetExisting == null) {
                // this is a new timesheet
                timesheet.setCreatedDate(LocalDateTime.now());
                timesheet.setLastModifiedDate(LocalDateTime.now());
                timesheet.setStatus(Timesheet.TimesheetStatusEnum.ACTIVE.name());
                // need only to this once, not allowed to change from and to date of a timesheet
                timesheet.setWorkingDaysInMonth(Utility.countBusinessDaysInMonth(timesheet.getFromDate()));
                timesheet.setWorkingHoursInMonth((int) (timesheet.getWorkingDaysInMonth() * 7.5));
                id = timesheetRepository.insertTimesheet(timesheet);
                Log.d("TimesheetRepository.saveTimesheet", String.format("inserted new timesheetId=%s, %s", id, timesheet));
            } else {
                // this is a update of existing timesheet
                // must be set here, because the values is not handled in the fragment/UI and will therefore be reset during an update.
                // this should be fixed by not using the entity directly in the fragment/UI.
                timesheet.setLastModifiedDate(LocalDateTime.now());
                // TODO since these values are not used in the view and therefore get lost when updating the timesheet, we simply take care of this here.
                timesheet.setWorkingDaysInMonth(timesheetExisting.getWorkingDaysInMonth());
                timesheet.setWorkingHoursInMonth(timesheetExisting.getWorkingHoursInMonth());
                //timesheet.setTotalWorkedDays(timesheetExisting.getTotalWorkedDays());
                //timesheet.setTotalWorkedHours(timesheetExisting.getTotalWorkedHours());
                timesheetRepository.updateTimesheet(timesheet);
                Log.d("TimesheetRepository.saveTimesheet", String.format("updated timesheet: %s", timesheet));
            }
            return id;
        } catch (Exception e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("Error saving timesheet!", e.getMessage(), e.getCause());
        }
    }

    // ----------------------------------------
    // timesheet entry
    // ----------------------------------------

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    @Transaction
    public void saveTimesheetEntry(@NotNull final TimesheetEntry timesheetEntry) {
        try {
            TimesheetEntry timesheetEntryExisting = timesheetRepository.getTimesheetEntry(timesheetEntry.getTimesheetId(), timesheetEntry.getWorkdayDate());

            // first of all, check status
            if (timesheetEntryExisting != null && timesheetEntryExisting.isBilled()) {
                throw new InputValidationException(String.format("timesheet entry have status billed, no changes is allowed. workday date=%s, status=%s", timesheetEntryExisting.getWorkdayDate(), timesheetEntryExisting.getStatus()), "40040", null);
            }
            // the check if this is a new one or update of an existing
            if (timesheetEntryExisting != null) {
                throw new InputValidationException(String.format("Workday already registered, timesheetId=%s, date=%s, hours=%s, status=%s", timesheetEntryExisting.getTimesheetId(), timesheetEntryExisting.getWorkdayDate(), timesheetEntry.getWorkedHours(), timesheetEntryExisting.getStatus()), "40040", null);
            }

            // validate work date, must be between the to and from date range of the timesheet
            Timesheet timesheet = timesheetRepository.getTimesheet(timesheetEntry.getTimesheetId());
            // check the timesheet entry date
            if (timesheetEntry.getWorkdayDate().isBefore(timesheet.getFromDate()) || timesheetEntry.getWorkdayDate().isAfter(timesheet.getToDate())) {
                throw new InputValidationException(String.format("timesheet entry work date not in the to and from date range of the timesheet. %s <> %s - %s", timesheetEntry.getWorkdayDate(), timesheet.getFromDate(), timesheet.getToDate()), "40040", null);
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
            throw new TerexApplicationException("Error saving timesheet entry!", e.getMessage(), e.getCause());
        }
    }

    @Transaction
    public void deleteTimesheetEntry(TimesheetEntry timesheetEntry) {
        if (timesheetEntry.isBilled()) {
            throw new InputValidationException("Timesheet entry is closed, not allowed to delete or update", "40045", null);
        }
        timesheetRepository.deleteTimesheetEntry(timesheetEntry);
    }

    @NotNull
    public TimesheetEntry getMostRecentTimeSheetEntry(Long timesheetId) {
        TimesheetEntry timesheetEntry = timesheetRepository.getMostRecentTimeSheetEntry(timesheetId);
        if (timesheetEntry == null) {
            // no timesheet entries found, so simply create a default entry;
            timesheetEntry = TimesheetEntry.createDefault(timesheetId, LocalDate.now());
            // then set date equal to the timesheet from date.
            Timesheet timesheet = timesheetRepository.getTimesheet(timesheetId);
            timesheetEntry.setWorkdayDate(timesheet.getFromDate());
        }
        return timesheetEntry;
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

    /**
     * This service return timesheet entries where holidays and free days are added, if missing.
     * This so the timesheet contains an entry for all days in the month.
     * Added entries will not have any impact the timesheet calculated fields.
     */
    public List<TimesheetEntryDto> getTimesheetEntryDtoListReadyForBilling(Long timesheetId) {
        List<TimesheetEntry> timesheetEntryList = timesheetRepository.getTimesheetEntryList(timesheetId);
        populateTimesheetList(timesheetId, timesheetEntryList);
        return TimesheetMapper.toTimesheetEntryDtoList(timesheetEntryList);
    }

    // ----------------------------------------
    // timesheet summary
    // ----------------------------------------

    public List<TimesheetSummary> getTimesheetSummary(Long timesheetId) {
        return timesheetRepository.getTimesheetSummary(timesheetId);
    }

    public Long saveTimesheetSummary(TimesheetSummary timesheetSummary) {
        Log.d("saved timesheet summary", "" + timesheetSummary);
        return timesheetRepository.saveTimesheetSummary(timesheetSummary);
    }

    /**
     * Used as invoice attachment
     */
    public List<TimesheetSummaryDto> createTimesheetSummaryForBilling(@NotNull Long timesheetId) {
        // check timesheet status
        Timesheet timesheet = getTimesheet(timesheetId);
        if (!timesheet.isCompleted()) {
            throw new TerexApplicationException(String.format("Application error, timesheet not fulfilled! timesheetId=%s, status=%s", timesheetId, timesheet.getStatus()), "50023", null);
        }
        Log.d("createTimesheetSummary", String.format("timesheetId=%s", timesheetId));
        List<TimesheetEntry> timesheetEntryList = getTimesheetEntryList(timesheetId);
        if (timesheetEntryList == null || timesheetEntryList.isEmpty()) {
            throw new TerexApplicationException(String.format("Application error, timesheet not ready for billing, no entries found! timesheetId=%s, status=%s", timesheetId, timesheet.getStatus()), "50023", null);
        }

        List<TimesheetSummary> timesheetSummaryByWeek = createTimesheetSummary(timesheetId, "WEEK");
        timesheetSummaryByWeek.forEach(this::saveTimesheetSummary);
        // close the timesheet after invoice have been generated, is not possible to do any form of changes on the time list.
        timesheet.setStatus(Timesheet.TimesheetStatusEnum.BILLED.name());
        saveTimesheet(timesheet);

        // then close all timesheet entries by setting status to billed
        timesheetEntryList.forEach(e -> timesheetRepository.closeTimesheetEntry(e.getId()));
        return TimesheetMapper.toTimesheetSummaryDtoList(timesheetSummaryByWeek);
    }

    public List<TimesheetSummary> createTimesheetSummary(Long timesheetId, String by) {
        List<TimesheetEntry> timesheetEntryList = getTimesheetEntryList(timesheetId);
        Log.d("createTimesheetSummary", "timesheet entries: " + timesheetEntryList);
        // accumulate timesheet by week for the mount
        Map<Integer, List<TimesheetEntry>> timesheetWeekMap = switch (by) {
            case "WEEK" ->
                    timesheetEntryList.stream().collect(Collectors.groupingBy(TimesheetEntry::getWorkdayWeek));
            case "MONTH" ->
                    timesheetEntryList.stream().collect(Collectors.groupingBy(TimesheetEntry::getWorkdayMonth));
            case "YEAR" ->
                    timesheetEntryList.stream().collect(Collectors.groupingBy(TimesheetEntry::getWorkdayYear));
            default -> Map.of();
        };
        List<TimesheetSummary> timesheetSummaryByWeek = new ArrayList<>();
        timesheetWeekMap.forEach((k, e) -> timesheetSummaryByWeek.add(buildTimesheetSummaryForWeek(timesheetId, k, e)));
        timesheetSummaryByWeek.sort(Comparator.comparing(TimesheetSummary::getWeekInYear));
        return timesheetSummaryByWeek;
    }

    public String createTimesheetSummaryAttachmentHtml(@NotNull Long timesheetId, @NotNull Long myCompanyOrgId, @NotNull Long clientOrgId, @NotNull Context applicationContext) {
        Timesheet timesheet = getTimesheet(timesheetId);
        List<TimesheetSummary> timesheetSummaryList = getTimesheetSummary(timesheetId);
        double totalBilledAmount = timesheetSummaryList.stream().mapToDouble(TimesheetSummary::getTotalBilledAmount).sum();
        double totalBilledHours = timesheetSummaryList.stream().mapToDouble(TimesheetSummary::getTotalWorkedHours).sum();
        double totalVat = totalBilledAmount * 0.25;
        double totalBilledAmountWithVat = totalBilledAmount + totalVat;

        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile(new StringReader(loadMustacheTemplate(applicationContext, InvoiceService.InvoiceAttachmentTypesEnum.TIMESHEET_SUMMARY)), "");
        Map<String, Object> context = new HashMap<>();
        context.put("invoiceAttachmentTitle", "Vedlegg til faktura");
        context.put("invoiceBillingPeriod", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM")));
        context.put("timesheetPeriod", String.format("%s/%s", timesheet.getMonth(), timesheet.getYear()));
        context.put("company", organizationRepository.getOrganization(myCompanyOrgId));
        context.put("client", organizationRepository.getOrganization(clientOrgId));
        context.put("timesheetProjectCode", "techlead-catalystone-solution-as");
        context.put("timesheetSummaryList", timesheetSummaryList);
        context.put("totalBilledHours", Double.toString(totalBilledHours));
        context.put("totalBilledAmount", Double.toString(totalBilledAmount));
        context.put("vatInPercent", "25%");
        context.put("totalVat", Double.toString(totalVat));
        context.put("totalBilledAmountWithVat", Double.toString(totalBilledAmountWithVat));
        context.put("generatedDate", LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        StringWriter writer = new StringWriter();
        mustache.execute(writer, context);
        return writer.toString();
    }

    public String createTimesheetSummaryHtml(@NotNull Long timesheetId, @NotNull Context applicationContext) {
        Timesheet timesheet = getTimesheet(timesheetId);
        List<TimesheetSummary> timesheetSummaryList = createTimesheetSummary(timesheetId, "WEEK");
        double totalBilledAmount = timesheetSummaryList.stream().mapToDouble(TimesheetSummary::getTotalBilledAmount).sum();
        double totalBilledHours = timesheetSummaryList.stream().mapToDouble(TimesheetSummary::getTotalWorkedHours).sum();
        double vat = 25;
        double totalVat = totalBilledAmount * (vat / 100);
        double totalBilledAmountWithVat = totalBilledAmount + totalVat;
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile(new StringReader(loadMustacheTemplate(applicationContext, InvoiceService.InvoiceAttachmentTypesEnum.TIMESHEET_SUMMARY_2)), "");
        Map<String, Object> context = new HashMap<>();
        context.put("invoiceAttachmentTitle", "Vedlegg til faktura");
        context.put("invoiceBillingPeriod", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM")));
        context.put("timesheetPeriod", String.format("%s/%s", timesheet.getMonth(), timesheet.getYear()));
        context.put("company", organizationRepository.getOrganization(1L)); //fixme
        context.put("client", organizationRepository.getOrganization(2L)); // fixme
        context.put("timesheetProjectCode", "techlead-catalystone-solution-as");
        context.put("timesheetSummaryList", TimesheetMapper.toTimesheetSummaryDtoList(timesheetSummaryList));
        context.put("totalBilledHours", String.format(Locale.getDefault(), "%.1f", totalBilledHours));
        context.put("totalBilledAmount", String.format(Locale.getDefault(), "%.2f", totalBilledAmount));
        context.put("vatInPercent", String.format(Locale.getDefault(), "%.0f", vat));
        context.put("totalVat", String.format(Locale.getDefault(), "%.2f", totalVat));
        context.put("totalBilledAmountWithVat", String.format(Locale.getDefault(), "%.2f", totalBilledAmountWithVat));
        context.put("generatedDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS")));
        StringWriter writer = new StringWriter();
        mustache.execute(writer, context);
        return writer.toString();
    }

    public String createTimesheetListHtml(@NotNull Long timesheetId, @NotNull Context applicationContext) {
        Timesheet timesheet = getTimesheet(timesheetId);
        List<TimesheetEntryDto> timesheetEntryDtoList = getTimesheetEntryDtoListReadyForBilling(timesheetId);
        Double sumBilledHours = timesheetEntryDtoList.stream().mapToDouble(TimesheetEntryDto::getWorkedMinutes).sum() / 60;
        Integer numberOfWorkedDays = timesheetEntryDtoList.stream().filter(e -> e.getWorkedMinutes() != null && e.getWorkedMinutes() > 0).collect(Collectors.toList()).size();
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile(new StringReader(loadMustacheTemplate(applicationContext, InvoiceService.InvoiceAttachmentTypesEnum.CLIENT_TIMESHEET)), "");
        Map<String, Object> context = new HashMap<>();
        context.put("title", "Timeliste for konsulentbistand");
        context.put("consultantName", "Gunnar Rønneberg");
        context.put("clientProjectName", "");
        context.put("clientName", "");
        context.put("clientContactPersonName", "");
        context.put("clientContactPersonMobile", "");
        context.put("clientContactPersonEmail", "");



        context.put("timesheetPeriod", String.format("%s/%s", timesheet.getMonth(), timesheet.getYear()));
        context.put("timesheetEntryDtoList", timesheetEntryDtoList);
        context.put("numberOfWorkedDays", numberOfWorkedDays);
        context.put("sunBilledHours", String.format(Locale.getDefault(), "%.1f", sumBilledHours));
        context.put("generatedDate", LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        StringWriter writer = new StringWriter();
        mustache.execute(writer, context);
        return writer.toString();
    }

    private TimesheetSummary buildTimesheetSummaryForWeek(@NotNull Long timesheetId, @NotNull Integer week, @NotNull List<TimesheetEntry> timesheetEntryList) {
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

    private void populateTimesheetList(Long timesheetId, List<TimesheetEntry> timesheetEntryList) {
        Timesheet timesheet = getTimesheet(timesheetId);
        // interpolate missing days in the month.
        for (LocalDate date = timesheet.getFromDate(); date.isBefore(timesheet.getToDate()); date = date.plusDays(1)) {
            boolean isInList = false;
            for (TimesheetEntry e : timesheetEntryList) {
                if (e.getWorkdayDate().equals(date)) {
                    isInList = true;
                    break;
                }
            }
            if (!isInList) {
                TimesheetEntry timesheetEntry = new TimesheetEntry();
                timesheetEntry.setTimesheetId(timesheetId);
                timesheetEntry.setStatus(TimesheetEntry.TimesheetEntryStatusEnum.CLOSED.name());
                timesheetEntry.setWorkdayDate(date);
                timesheetEntry.setWorkedMinutes(0);
                timesheetEntryList.add(timesheetEntry);
            }
        }
        timesheetEntryList.sort(Comparator.comparing(TimesheetEntry::getWorkdayDate));
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
}
