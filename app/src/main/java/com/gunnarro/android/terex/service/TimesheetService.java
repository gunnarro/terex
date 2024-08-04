package com.gunnarro.android.terex.service;

import android.util.Log;

import androidx.room.Transaction;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.gunnarro.android.terex.domain.dto.ClientDto;
import com.gunnarro.android.terex.domain.dto.ProjectDto;
import com.gunnarro.android.terex.domain.dto.TimesheetDto;
import com.gunnarro.android.terex.domain.dto.TimesheetEntryDto;
import com.gunnarro.android.terex.domain.dto.TimesheetSummaryDto;
import com.gunnarro.android.terex.domain.dto.UserAccountDto;
import com.gunnarro.android.terex.domain.entity.Timesheet;
import com.gunnarro.android.terex.domain.entity.TimesheetEntry;
import com.gunnarro.android.terex.domain.entity.TimesheetSummary;
import com.gunnarro.android.terex.domain.mapper.TimesheetMapper;
import com.gunnarro.android.terex.exception.InputValidationException;
import com.gunnarro.android.terex.exception.TerexApplicationException;
import com.gunnarro.android.terex.integration.jira.TempoApi;
import com.gunnarro.android.terex.integration.jira.TempoDomainMapper;
import com.gunnarro.android.terex.repository.TimesheetRepository;
import com.gunnarro.android.terex.utility.Utility;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
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

    // shoud not be here, move to config part, when ready
    private static final int VAT_PERCENT = 25;
    private final TimesheetRepository timesheetRepository;
    private final UserAccountService userAccountService;
    private final ClientService clientService;
    private final ProjectService projectService;
    private final TempoApi tempoApi;

    /**
     * for unit test only
     */
    public TimesheetService(TimesheetRepository timesheetRepository, UserAccountService userAccountService, ProjectService projectService, ClientService clientService, TempoApi tempoApi) {
        this.timesheetRepository = timesheetRepository;
        this.userAccountService = userAccountService;
        this.projectService = projectService;
        this.clientService = clientService;
        this.tempoApi = tempoApi;
    }

    @Inject
    public TimesheetService() {
        this(new TimesheetRepository(), new UserAccountService(), new ProjectService(), new ClientService(), new TempoApi());
    }

    // ----------------------------------------
    // import timesheet from external system
    // ----------------------------------------

    public void importTempoTimesheetCvs(Timesheet timesheet, Long projectId) throws IOException {
        List<TimesheetEntry> timesheetEntryList = TempoDomainMapper.fromTempoTimesheetCvs(tempoApi.getTimesheetExportCvs());
        // timesheet must/should be empty

        timesheetEntryList.forEach(e -> {
            e.setTimesheetId(timesheet.getId());
            e.setProjectId(projectId);
            saveTimesheetEntry(e);
        });
    }

    // ----------------------------------------
    // timesheet
    // ----------------------------------------

    public TimesheetDto getTimesheetDto(Long timesheetId) {
        Timesheet timesheet = timesheetRepository.getTimesheet(timesheetId);
        if (timesheet != null) {
            TimesheetDto timesheetDto = TimesheetMapper.toTimesheetDto(timesheet, null, null);
            timesheetDto.setUserAccountDto(userAccountService.getUserAccount(timesheet.getUserId()));
            timesheetDto.setClientDto(clientService.getClient(timesheet.getClientId()));
            timesheetDto.setRegisteredWorkedDays(timesheetRepository.countRegisteredDays(timesheetId, TimesheetEntry.TimesheetEntryTypeEnum.REGULAR));
            timesheetDto.setRegisteredWorkedHours(timesheetRepository.getTotalWorkedHours(timesheetId));
            timesheetDto.setVacationDays(timesheetRepository.countRegisteredDays(timesheetId, TimesheetEntry.TimesheetEntryTypeEnum.VACATION));
            timesheetDto.setSickDays(timesheetRepository.countRegisteredDays(timesheetId, TimesheetEntry.TimesheetEntryTypeEnum.SICK));
            return timesheetDto;
        }
        return null;
    }

    public List<TimesheetDto> getTimesheetsReadyForBilling() {
        List<Timesheet> timesheetList = timesheetRepository.getTimesheets(Timesheet.TimesheetStatusEnum.COMPLETED.name());
        return TimesheetMapper.toTimesheetDtoList(timesheetList);
    }

    public List<Timesheet> getTimesheetsByStatus(String status) {
        Log.d("getTimesheetsByStatus", "status: " + status);
        return timesheetRepository.getTimesheets(status);
    }

    public String getTimesheetTitle(Long timesheetId) {
        return timesheetRepository.getTimesheetTitle(timesheetId);
    }

    public Timesheet getTimesheet(Long timesheetId) {
        return timesheetRepository.getTimesheet(timesheetId);
    }

    public List<Integer> getAllTimesheetYear() {
        return timesheetRepository.getAllTimesheetYear();
    }


    public List<TimesheetDto> getTimesheetDtoList(Integer year) {
        Log.d("getTimesheetDtoList", "get timesheets for year=" + year);
        List<TimesheetDto> timesheetDtoList = new ArrayList<>();
        List<Long> timesheetIdList = timesheetRepository.getTimesheetIds(year);
        timesheetIdList.forEach(id -> timesheetDtoList.add(getTimesheetDto(id)));
        return timesheetDtoList;
    }

    public void deleteTimesheet(Long timesheetId) {
        Timesheet timesheet = timesheetRepository.getTimesheet(timesheetId);
        if (timesheet.isBilled()) {
            throw new InputValidationException("Timesheet is BILLED, not allowed to delete or update", "40045", null);
        }
        timesheetRepository.deleteTimesheet(timesheet);
    }

    public Long saveTimesheet(Timesheet timesheet) {
        Log.d("TimesheetRepository.saveTimesheet", String.format("%s", timesheet));
        Timesheet timesheetExisting = timesheetRepository.find(timesheet.getUserId(), timesheet.getClientId(), timesheet.getYear(), timesheet.getMonth());
        // first of all, check status
        if (timesheetExisting != null && timesheetExisting.isBilled()) {
            Log.e("", "Timesheet is already billed, no changes is allowed. timesheetId=" + timesheetExisting.getId() + " " + timesheetExisting.getStatus());
            throw new InputValidationException(String.format("Timesheet is already billed, no changes is allowed. %s", timesheetExisting), "40040", null);
        }
        try {
            Log.d("TimesheetRepository.saveTimesheet", String.format("existingTimesheet: %s", timesheetExisting));
            Long id;
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
                id = timesheetExisting.getId();
            }
            return id;
        } catch (Exception e) {
            e.printStackTrace();
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("Error saving timesheet!" + e.getMessage(), "50500", e.getCause());
        }
    }

    // ----------------------------------------
    // timesheet entry
    // ----------------------------------------

    public TimesheetEntry getTimesheetEntry(Long timesheetEntryId) {
        return timesheetRepository.getTimesheetEntry(timesheetEntryId);
    }

    public List<TimesheetEntryDto> getTimesheetEntryListDto(Long timesheetId) {
        List<TimesheetEntryDto> timesheetEntryDtoList = new ArrayList<>();
        List<Long> timesheetEntryIdList = timesheetRepository.getTimesheetEntryIds(timesheetId);
        timesheetEntryIdList.forEach(id -> timesheetEntryDtoList.add(getTimesheetEntryDto(id)));
        timesheetEntryDtoList.sort(Comparator.comparing(TimesheetEntryDto::getWorkdayDate));
        return timesheetEntryDtoList;
    }

    public TimesheetEntryDto getTimesheetEntryDto(Long timesheetEntryId) {
        TimesheetEntryDto timesheetEntryDto = TimesheetMapper.toTimesheetEntryDto(timesheetRepository.getTimesheetEntry(timesheetEntryId));
        timesheetEntryDto.setProjectDto(projectService.getProject(timesheetEntryDto.getProjectId()));
        return timesheetEntryDto;
    }

    /**
     * At time not possible to update a timesheet entry and it will not be supported in the future either.
     */
    @Transaction
    public Long saveTimesheetEntry(@NotNull final TimesheetEntry timesheetEntry) {
        try {
            TimesheetEntry timesheetEntryExisting = timesheetRepository.getTimesheetEntry(timesheetEntry.getTimesheetId(), timesheetEntry.getProjectId(), timesheetEntry.getWorkdayDate());
            // first of all, check status
            if (timesheetEntryExisting != null && timesheetEntryExisting.isClosed()) {
                throw new InputValidationException(String.format("timesheet entry have status closed, no changes is allowed. workday date=%s, status=%s", timesheetEntryExisting.getWorkdayDate(), timesheetEntryExisting.getStatus()), "40040", null);
            }
            // the check if this is a new one or update of an existing
            if (timesheetEntryExisting != null) {
                throw new InputValidationException(String.format("Workday already registered and update is not allowed! timesheetId=%s, date=%s, hours=%s, status=%s", timesheetEntryExisting.getTimesheetId(), timesheetEntryExisting.getWorkdayDate(), timesheetEntry.getWorkedHours(), timesheetEntryExisting.getStatus()), "40040", null);
            }

            // validate work date, must be between the to and from date range of the timesheet
            Timesheet timesheet = timesheetRepository.getTimesheet(timesheetEntry.getTimesheetId());
            // check the timesheet entry date
            if (timesheetEntry.getWorkdayDate().isBefore(timesheet.getFromDate()) || timesheetEntry.getWorkdayDate().isAfter(timesheet.getToDate())) {
                throw new InputValidationException(String.format("timesheet entry work date not in the to and from date range of the timesheet! workDate = %s <> dateRange = %s - %s", timesheetEntry.getWorkdayDate(), timesheet.getFromDate(), timesheet.getToDate()), "40040", null);
            }

            // set the timesheet work date week number here, this is only used to simplify accumulate timesheet by week by the invoice service
            timesheetEntry.setWorkdayWeek(timesheetEntry.getWorkdayDate().get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear()));

            timesheetEntry.setCreatedDate(LocalDateTime.now());
            timesheetEntry.setLastModifiedDate(LocalDateTime.now());
            long id = timesheetRepository.insertTimesheetEntry(timesheetEntry);
            Log.d("TimesheetRepository.saveTimesheetEntry", String.format("insert new timesheet entry, timesheetId=%s, timesheetEntryId=%s, workDate=%s", timesheetEntry.getTimesheetId(), id, timesheetEntry.getWorkdayDate()));
            return id;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("Error saving timesheet entry!", e.getMessage(), e.getCause());
        }
    }

    @Transaction
    public void deleteTimesheetEntry(TimesheetEntry timesheetEntry) {
        TimesheetEntry deleteTimesheetEntry = getTimesheetEntry(timesheetEntry.getId());
        if (deleteTimesheetEntry == null) {
            throw new InputValidationException(String.format("Timesheet entry not found! timesheetEntryId=%s", timesheetEntry.getId()), "40044", null);
        }
        if (deleteTimesheetEntry.isClosed()) {
            throw new InputValidationException("Timesheet entry is closed, not allowed to delete or update", "40045", null);
        }
        timesheetRepository.deleteTimesheetEntry(deleteTimesheetEntry);
    }

    public TimesheetEntryDto getMostRecentTimeSheetEntryDto(Long timesheetId) {
        return TimesheetMapper.toTimesheetEntryDto(getMostRecentTimeSheetEntry(timesheetId));
    }

    @NotNull
    public TimesheetEntry getMostRecentTimeSheetEntry(Long timesheetId) {
        TimesheetEntry timesheetEntry = timesheetRepository.getMostRecentTimeSheetEntry(timesheetId);
        if (timesheetEntry == null) {
            // no timesheet entries found, so simply create a default entry;
            // then set work date equal to the timesheet from date.
            Timesheet timesheet = timesheetRepository.getTimesheet(timesheetId);
            timesheetEntry = TimesheetEntry.createDefault(timesheet.getId(), -1L, timesheet.getFromDate());
        }
        // clear the id, so this will be taken as a new timesheet entry, the created and last modified date will the be overridden upon save.
        timesheetEntry.setId(null);
        timesheetEntry.setWorkdayDate(null);
        timesheetEntry.setCreatedDate(null);
        timesheetEntry.setLastModifiedDate(null);
        Log.d("getMostRecentTimeSheetEntry", String.format("timesheetId=%s, most recent:%s ", timesheetId, timesheetEntry));
        return timesheetEntry;
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

        List<TimesheetSummary> timesheetSummaryByWeek = createTimesheetSummary(timesheetId, TimesheetSummary.TimesheetSummaryPeriodEnum.WEEK);
        timesheetSummaryByWeek.forEach(this::saveTimesheetSummary);
        // close the timesheet after invoice have been generated, is not possible to do any form of changes on the time list.
        timesheet.setStatus(Timesheet.TimesheetStatusEnum.BILLED.name());
        saveTimesheet(timesheet);

        // then close all timesheet entries by setting status to billed
        timesheetEntryList.forEach(e -> timesheetRepository.closeTimesheetEntry(e.getId()));
        return TimesheetMapper.toTimesheetSummaryDtoList(timesheetSummaryByWeek);
    }

    private List<TimesheetSummary> createTimesheetSummary(Long timesheetId, TimesheetSummary.TimesheetSummaryPeriodEnum summedByPeriod) {
        List<TimesheetEntry> timesheetEntryList = getTimesheetEntryList(timesheetId);
        Log.d("createTimesheetSummary", String.format("timesheet entries: %s", timesheetEntryList));
        // create a map based on summery type
        Map<Integer, List<TimesheetEntry>> timesheetWeekMap = switch (summedByPeriod) {
            case WEEK ->
                    timesheetEntryList.stream().collect(Collectors.groupingBy(TimesheetEntry::getWorkdayWeek));
            case MONTH ->
                    timesheetEntryList.stream().collect(Collectors.groupingBy(TimesheetEntry::getWorkdayMonth));
            case YEAR ->
                    timesheetEntryList.stream().collect(Collectors.groupingBy(TimesheetEntry::getWorkdayYear));
        };

        // debug
        // timesheetWeekMap.forEach((p, list) -> list.forEach(i -> System.out.println(p + ", " + list.size() + " - " + i.getWorkdayDate() + ", " + i.getType() + ", " + i.getWorkedHours())));
        List<TimesheetSummary> timesheetSummaryByWeek = new ArrayList<>();
        timesheetWeekMap.forEach((period, list) -> timesheetSummaryByWeek.add(buildTimesheetSummaryForWeek(timesheetId, period, list)));
        timesheetSummaryByWeek.sort(Comparator.comparing(TimesheetSummary::getWeekInYear));
        return timesheetSummaryByWeek;
    }

    public String createTimesheetSummaryAttachmentHtml(@NotNull Long timesheetId, @NotNull UserAccountDto userAccountDto, @NotNull ClientDto clientDto, @NotNull String timesheetSummeryMustacheTemplate) {
        Timesheet timesheet = getTimesheet(timesheetId);
        List<TimesheetSummary> timesheetSummaryList = getTimesheetSummary(timesheetId);
        double totalBilledAmount = timesheetSummaryList.stream().mapToDouble(TimesheetSummary::getTotalBilledAmount).sum();
        double totalBilledHours = timesheetSummaryList.stream().mapToDouble(TimesheetSummary::getTotalWorkedHours).sum();
        double totalVat = totalBilledAmount * VAT_PERCENT / 100;
        double totalBilledAmountWithVat = totalBilledAmount + totalVat;

        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile(new StringReader(timesheetSummeryMustacheTemplate), "");
        Map<String, Object> context = new HashMap<>();
        context.put("invoiceAttachmentTitle", "Vedlegg til faktura");
        context.put("invoiceBillingPeriod", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM")));
        context.put("timesheetPeriod", String.format("%s/%s", timesheet.getMonth(), timesheet.getYear()));
        context.put("invoiceIssuer", userAccountDto);
        context.put("invoiceReceiver", clientDto);
        context.put("timesheetProjectCode", String.format("%s %s", clientDto.getProjectList().get(0).getName(), clientDto.getProjectList().get(0).getDescription()));
        context.put("timesheetSummaryList", timesheetSummaryList);
        context.put("totalBilledHours", String.format(Locale.getDefault(), "%,.1f", totalBilledHours));
        context.put("totalBilledAmount", Utility.formatAmountToNOK(totalBilledAmount));
        context.put("vatInPercent", String.format(Locale.getDefault(), "%s%s", VAT_PERCENT, "%"));
        context.put("totalVat", Utility.formatAmountToNOK(totalVat));
        context.put("totalBilledAmountWithVat", Utility.formatAmountToNOK(totalBilledAmountWithVat));
        context.put("generatedDate", LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        StringWriter writer = new StringWriter();
        mustache.execute(writer, context);
        return writer.toString();
    }

    /**
     * Not in use yet, used bt TimesheetSummaryFragment
     */
    public String createTimesheetSummaryHtml(@NotNull Long timesheetId, @NotNull String timesheetSummaryMustacheTemplate) {
        TimesheetDto timesheet = getTimesheetDto(timesheetId);
        List<TimesheetSummary> timesheetSummaryList = createTimesheetSummary(timesheetId, TimesheetSummary.TimesheetSummaryPeriodEnum.WEEK);
        double totalBilledAmount = timesheetSummaryList.stream().mapToDouble(TimesheetSummary::getTotalBilledAmount).sum();
        double totalBilledHours = timesheetSummaryList.stream().mapToDouble(TimesheetSummary::getTotalWorkedHours).sum();
        double totalVat = totalBilledAmount * ((double) VAT_PERCENT / 100);
        double totalBilledAmountWithVat = totalBilledAmount + totalVat;
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile(new StringReader(timesheetSummaryMustacheTemplate), "");
        Map<String, Object> context = new HashMap<>();
        context.put("invoiceAttachmentTitle", "Vedlegg til faktura");
        context.put("invoiceBillingPeriod", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM")));
        context.put("timesheetPeriod", String.format("%s/%s", timesheet.getMonth(), timesheet.getYear()));
        context.put("company", timesheet.getUserAccountDto().getOrganizationDto());
        context.put("client", null);
        context.put("timesheetProjectCode", timesheet.toString());
        context.put("timesheetSummaryList", TimesheetMapper.toTimesheetSummaryDtoList(timesheetSummaryList));
        context.put("totalBilledHours", String.format(Locale.getDefault(), "%.1f", totalBilledHours));
        context.put("totalBilledAmount", String.format(Locale.getDefault(), "%.2f", totalBilledAmount));
        context.put("vatInPercent", String.format(Locale.getDefault(), "%s", VAT_PERCENT));
        context.put("totalVat", String.format(Locale.getDefault(), "%.2f", totalVat));
        context.put("totalBilledAmountWithVat", String.format(Locale.getDefault(), "%.2f", totalBilledAmountWithVat));
        context.put("generatedDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS")));
        StringWriter writer = new StringWriter();
        mustache.execute(writer, context);
        return writer.toString();
    }

    /**
     * Creates a timesheet overview in html, which show all days in the month.
     */
    public String createTimesheetListHtml(@NotNull Long timesheetId, @NotNull UserAccountDto userAccountDto, @NotNull ClientDto clientDto, @NotNull String clientTimesheetMustacheTemplate) {
        TimesheetDto timesheetDto = getTimesheetDto(timesheetId);
        List<TimesheetEntryDto> timesheetEntryDtoList = getTimesheetEntryDtoListReadyForBilling(timesheetId);
        Double sumBilledHours = timesheetEntryDtoList.stream().filter(e -> e.getWorkedSeconds() != null).mapToDouble(TimesheetEntryDto::getWorkedSeconds).sum() / 3600;
        long numberOfWorkedDays = timesheetEntryDtoList.stream().filter(e -> e.getWorkedSeconds() != null && e.getWorkedSeconds() > 0).count();
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile(new StringReader(clientTimesheetMustacheTemplate), "");
        Map<String, Object> context = new HashMap<>();
        context.put("title", "Timeliste");
        context.put("consultant", userAccountDto);
        context.put("customer", clientDto);
        context.put("customerProjectName", String.format("%s %s", clientDto.getProjectList().get(0).getName(), clientDto.getProjectList().get(0).getDescription()));
        context.put("timesheetPeriod", String.format("%s", timesheetDto.getFromDate().format(DateTimeFormatter.ofPattern("MMMM yyyy"))));
        context.put("timesheetEntryDtoList", timesheetEntryDtoList);
        context.put("numberOfWorkedDays", (int) numberOfWorkedDays);
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
        // get to and from date form the first entry in the list
        timesheetSummary.setYear(timesheetEntryList.get(0).getWorkdayDate().getYear());
        timesheetSummary.setFromDate(Utility.getFirstDayOfWeek(timesheetEntryList.get(0).getWorkdayDate(), week));
        timesheetSummary.setToDate(Utility.getLastDayOfWeek(timesheetEntryList.get(0).getWorkdayDate(), week));
        timesheetEntryList.forEach(t -> {
            ProjectDto projectDto = projectService.getProject(t.getProjectId());
            // only sum regular work days
            timesheetSummary.setTotalBilledAmount(t.isRegularWorkDay() ? timesheetSummary.getTotalBilledAmount() + (projectDto.getHourlyRate() * ((double) t.getWorkedSeconds() / 3600)) : timesheetSummary.getTotalBilledAmount());
            timesheetSummary.setTotalWorkedHours(t.isRegularWorkDay() ? timesheetSummary.getTotalWorkedHours() + (double) t.getWorkedSeconds() / 3600 : timesheetSummary.getTotalWorkedHours());
            timesheetSummary.setTotalWorkedDays(t.isRegularWorkDay() ? timesheetSummary.getTotalWorkedDays() + 1 : timesheetSummary.getTotalWorkedDays());
            timesheetSummary.setTotalSickLeaveDays(t.isSickDay() ? timesheetSummary.getTotalSickLeaveDays() + 1 : timesheetSummary.getTotalSickLeaveDays());
            timesheetSummary.setTotalVacationDays(t.isVacationDay() ? timesheetSummary.getTotalVacationDays() + 1 : timesheetSummary.getTotalVacationDays());
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
                timesheetEntryList.add(TimesheetEntry.createNotWorked(timesheetId, -1L, date, TimesheetEntry.TimesheetEntryTypeEnum.REGULAR.name()));
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
            timesheetSummary.setTotalBilledAmount(timesheetSummary.getTotalBilledAmount() + (1250 * ((double) t.getWorkedSeconds() / 60 * 60))); // FIXME
            timesheetSummary.setTotalWorkedHours(timesheetSummary.getTotalWorkedHours() + (double) t.getWorkedSeconds() / 60 * 60);
        });
        return timesheetSummary;
    }

    public int countTimesheetEntries() {
        return timesheetRepository.countTimesheetEntries();
    }

    public void deleteTimesheetEntryDto(TimesheetEntryDto timesheetEntryDto) {
        deleteTimesheetEntry(TimesheetMapper.fromTimesheetEntryDto(timesheetEntryDto));
    }

    public void saveTimesheetEntryDto(TimesheetEntryDto timesheetEntryDto) {
        saveTimesheetEntry(TimesheetMapper.fromTimesheetEntryDto(timesheetEntryDto));
    }
}
