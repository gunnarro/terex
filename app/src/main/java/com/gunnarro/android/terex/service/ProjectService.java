package com.gunnarro.android.terex.service;

import android.content.Context;

import androidx.room.Transaction;

import com.gunnarro.android.terex.domain.dto.TimesheetEntryDto;
import com.gunnarro.android.terex.domain.entity.InvoiceSummary;
import com.gunnarro.android.terex.domain.entity.Project;
import com.gunnarro.android.terex.domain.entity.Timesheet;
import com.gunnarro.android.terex.domain.entity.TimesheetEntry;
import com.gunnarro.android.terex.repository.ProjectRepository;
import com.gunnarro.android.terex.utility.Utility;

import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
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

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ProjectService {

    private final ProjectRepository projectRepository;

    /**
     * default constructor
     */

    @Inject
    public ProjectService(Context applicationContext) {
        projectRepository = new ProjectRepository(applicationContext);
    }

    @Transaction
    public Long createProject(String companyName, String projectName, String description) {
        Project project = new Project();
        project.setCompanyName(companyName);
        project.setName(projectName);
        project.setDescription(description);
        return projectRepository.saveProject(project);
    }


    public List<Project> getProjects(String status) {
        return projectRepository.getProjects(status);
    }

    private TimesheetEntryDto mapToTimesheetEntryDto(TimesheetEntry timesheetEntry) {
        TimesheetEntryDto timesheetEntryDto = new TimesheetEntryDto();
        timesheetEntryDto.setComments(timesheetEntry.getComment());
        timesheetEntryDto.setFromTime(timesheetEntry.getFromTime().toString());
        timesheetEntryDto.setToTime(timesheetEntry.getToTime().toString());
        timesheetEntryDto.setWorkdayDate(timesheetEntry.getWorkdayDate());
        return timesheetEntryDto;
    }

    /**
     * @param year  current year
     * @param month from january to december, for example Month.MARCH
     * @return
     */
    public List<TimesheetEntry> generateTimesheet(Integer year, Integer month) {
        return getWorkingDays(year, month).stream().map(this::createTimesheet).collect(Collectors.toList());
    }

    private InvoiceSummary buildInvoiceSummaryForWeek(Long invoiceId, Integer week, List<TimesheetEntry> timesheets) {
        InvoiceSummary invoiceSummary = new InvoiceSummary();
        invoiceSummary.setInvoiceId(invoiceId);
        invoiceSummary.setWeekInYear(week);
        invoiceSummary.setYear(timesheets.get(0).getWorkdayDate().getYear());
        invoiceSummary.setFromDate(Utility.getFirstDayOfWeek(timesheets.get(0).getWorkdayDate(), week));
        invoiceSummary.setToDate(Utility.getLastDayOfWeek(timesheets.get(0).getWorkdayDate(), week));
        invoiceSummary.setSumWorkedDays(timesheets.size());
        timesheets.forEach(t -> {
            invoiceSummary.setSumBilledWork(invoiceSummary.getSumBilledWork() + (t.getHourlyRate() * ((double) t.getWorkedMinutes() / 60)));
            invoiceSummary.setSumWorkedHours(invoiceSummary.getSumWorkedHours() + (double) t.getWorkedMinutes() / 60);
        });
        return invoiceSummary;
    }

    public List<InvoiceSummary> buildInvoiceSummaryByWeek(Integer year, Integer month) {
        Map<Integer, List<TimesheetEntry>> weekMap = new HashMap<>();
        generateTimesheet(year, month).forEach(t -> {
            int week = getWeek(t.getWorkdayDate());
            if (!weekMap.containsKey(week)) {
                weekMap.put(week, new ArrayList<>());
            }
            Objects.requireNonNull(weekMap.get(week)).add(t);
        });
        List<InvoiceSummary> invoiceSummaryByWeek = new ArrayList<>();

        weekMap.forEach((k, e) -> {
            InvoiceSummary invoiceSummary = new InvoiceSummary();
            invoiceSummary.setWeekInYear(k);
            invoiceSummary.setSumWorkedDays(e.size());
            invoiceSummaryByWeek.add(invoiceSummary);
            Objects.requireNonNull(weekMap.get(k)).forEach(t -> {
                invoiceSummary.setSumBilledWork(invoiceSummary.getSumBilledWork() + (t.getHourlyRate() * ((double) t.getWorkedMinutes() / 60)));
                invoiceSummary.setSumWorkedHours(invoiceSummary.getSumWorkedHours() + (double) t.getWorkedMinutes() / 60);
            });
        });
        return invoiceSummaryByWeek;
    }

    private List<LocalDate> getWorkingDays(Integer year, Integer month) {
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

    private TimesheetEntry createTimesheet(LocalDate day) {
        return TimesheetEntry.createDefault(new java.util.Random().nextLong(), Timesheet.TimesheetStatusEnum.OPEN.name(), Utility.DEFAULT_DAILY_BREAK_IN_MINUTES, day, Utility.DEFAULT_DAILY_WORKING_HOURS_IN_MINUTES, Utility.DEFAULT_HOURLY_RATE);
    }

    private static int getWeek(LocalDate date) {
        TemporalField woy = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
        return date.get(woy);
    }
}
