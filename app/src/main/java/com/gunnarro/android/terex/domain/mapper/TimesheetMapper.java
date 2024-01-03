package com.gunnarro.android.terex.domain.mapper;

import com.gunnarro.android.terex.domain.dto.ClientDto;
import com.gunnarro.android.terex.domain.dto.ConsultantBrokerDto;
import com.gunnarro.android.terex.domain.dto.ProjectDto;
import com.gunnarro.android.terex.domain.dto.TimesheetDto;
import com.gunnarro.android.terex.domain.dto.TimesheetEntryDto;
import com.gunnarro.android.terex.domain.dto.TimesheetSummaryDto;
import com.gunnarro.android.terex.domain.entity.Client;
import com.gunnarro.android.terex.domain.entity.ClientWithProject;
import com.gunnarro.android.terex.domain.entity.ConsultantBroker;
import com.gunnarro.android.terex.domain.entity.ConsultantBrokerWithProject;
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
        projectDto.setName(project.getName());
        projectDto.setDescription(project.getDescription());
        projectDto.setStatus(project.getStatus());
        return projectDto;
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
        if (consultantBrokerWithProject == null) {
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

    public static Client fromClientDto(ClientDto clientDto) {
        return null;
    }

    public static ClientDto toClientDto(ClientWithProject client) {
        return null;
    }
}
