package com.gunnarro.android.terex.domain.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.gunnarro.android.terex.TestData;
import com.gunnarro.android.terex.domain.dto.ClientDto;
import com.gunnarro.android.terex.domain.dto.ConsultantBrokerDto;
import com.gunnarro.android.terex.domain.dto.ProjectDto;
import com.gunnarro.android.terex.domain.dto.TimesheetEntryDto;
import com.gunnarro.android.terex.domain.dto.TimesheetSummaryDto;
import com.gunnarro.android.terex.domain.entity.Client;
import com.gunnarro.android.terex.domain.entity.ClientDetails;
import com.gunnarro.android.terex.domain.entity.CompanyDetails;
import com.gunnarro.android.terex.domain.entity.ConsultantBroker;
import com.gunnarro.android.terex.domain.entity.ConsultantBrokerWithProject;
import com.gunnarro.android.terex.domain.entity.Project;
import com.gunnarro.android.terex.domain.entity.TimesheetEntry;
import com.gunnarro.android.terex.domain.entity.TimesheetSummary;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

class TimesheetMapperTest {

    @Test
    void toTimesheetEntryDto() {
        LocalDate localDate = LocalDate.of(2023, 12, 1);
        List<TimesheetEntry> timesheetEntryList = TestData.generateTimesheetEntries(localDate.getYear(), localDate.getMonthValue());

        TimesheetEntryDto timesheetEntryDto = TimesheetMapper.toTimesheetEntryDto(timesheetEntryList.get(0));
        assertEquals("2023-12-01", timesheetEntryDto.getWorkdayDate().toString());
        assertEquals("08:00", timesheetEntryDto.getFromTime().toString());
        assertEquals("15:30", timesheetEntryDto.getToTime().toString());
        assertEquals("7.5", timesheetEntryDto.getWorkedHours());
        assertNull(timesheetEntryDto.getComments());
    }

    @Test
    void toTimesheetEntryDtoList() {
        LocalDate localDate = LocalDate.of(2023, 12, 1);
        List<TimesheetEntry> timesheetEntryList = TestData.generateTimesheetEntries(localDate.getYear(), localDate.getMonthValue());

        List<TimesheetEntryDto> timesheetEntryDtoList = TimesheetMapper.toTimesheetEntryDtoList(timesheetEntryList);
        assertEquals(21, timesheetEntryDtoList.size());
    }

    @Test
    void toTimesheetSummaryDto() {
        LocalDate localDate = LocalDate.of(2023, 12, 1);
        List<TimesheetSummary> timesheetSummaryList = TestData.buildTimesheetSummaryByWeek(23L, localDate.getYear(), localDate.getMonthValue());

        TimesheetSummaryDto timesheetSummaryDto = TimesheetMapper.toTimesheetSummaryDto(timesheetSummaryList.get(0));
        assertEquals(23, timesheetSummaryDto.getTimesheetId());
        assertEquals("01.12", timesheetSummaryDto.getFromDateDDMM());
        assertEquals("9375.00", timesheetSummaryDto.getTotalBilledAmount());
        assertEquals("12", timesheetSummaryDto.getFromDateMM());
        assertEquals("48", timesheetSummaryDto.getWeekInYear().toString());
        assertEquals("7.5", timesheetSummaryDto.getTotalWorkedHours());
    }

    @Test
    void toTimesheetSummaryDtoList() {
        LocalDate localDate = LocalDate.of(2023, 12, 1);
        List<TimesheetSummary> timesheetSummaryList = TestData.buildTimesheetSummaryByWeek(23L, localDate.getYear(), localDate.getMonthValue());

        List<TimesheetSummaryDto> timesheetSummaryDtoList = TimesheetMapper.toTimesheetSummaryDtoList(timesheetSummaryList);
        assertEquals(5, timesheetSummaryDtoList.size());
    }

    @Test
    void toProjectDto() {
        Project project = new Project();
        project.setName("gunnarro timesheet project");
        project.setId(1L);
        project.setClientId(1L);
        project.setDescription("develop a timesheet app");
        project.setStatus(Project.ProjectStatusEnum.ACTIVE.name());
        ProjectDto projectDto = TimesheetMapper.toProjectDto(project);
        assertEquals(1, projectDto.getId().intValue());
        assertEquals("gunnarro timesheet project", projectDto.getName());
        assertEquals("develop a timesheet app", projectDto.getDescription());
        assertEquals(Project.ProjectStatusEnum.ACTIVE.name(), projectDto.getStatus());
        assertNull(projectDto.getTimesheetDto());
    }


    @Test
    void toConsultantBrokerDto() {
        ConsultantBroker consultantBroker = new ConsultantBroker();
        consultantBroker.setId(12L);
        consultantBroker.setName("gunnarro consultant broker");
        consultantBroker.setStatus("ACTIVE");

        Project project = new Project();
        project.setName("gunnarro timesheet project");
        project.setId(1L);
        project.setClientId(1L);
        project.setDescription("develop a timesheet app");
        project.setStatus(Project.ProjectStatusEnum.ACTIVE.name());

        ConsultantBrokerWithProject consultantBrokerWithProject = new ConsultantBrokerWithProject();
        consultantBrokerWithProject.setConsultantBroker(consultantBroker);
        consultantBrokerWithProject.setProjectList(List.of(project));

        ConsultantBrokerDto consultantBrokerDto = TimesheetMapper.toConsultantBrokerDto(consultantBrokerWithProject);
        assertEquals(12, consultantBrokerDto.getId().intValue());
        assertEquals("gunnarro consultant broker", consultantBrokerDto.getName());
        assertEquals(1, consultantBrokerDto.getProjects().size());
        assertEquals("gunnarro timesheet project", consultantBrokerDto.getProjects().get(0).getName());
    }

    @Test
    void fromConsultantBrokerDto() {
        ConsultantBrokerDto consultantBrokerDto = new ConsultantBrokerDto();
        consultantBrokerDto.setId(12L);
        consultantBrokerDto.setName("gunnarro consultant broker");
        consultantBrokerDto.setStatus("ACTIVE");

        ConsultantBroker consultantBroker = TimesheetMapper.fromConsultantBrokerDto(consultantBrokerDto);
        assertEquals(12, consultantBroker.getId().intValue());
        assertEquals("gunnarro consultant broker", consultantBroker.getName());
        assertEquals("ACTIVE", consultantBroker.getStatus());

    }

    @Test
    void toClientDto() {
        ClientDetails clientDetails = new ClientDetails();
        clientDetails.setClient(new Client());
        clientDetails.setCompany(new CompanyDetails());
        clientDetails.setProjectList(List.of(new Project()));
        ClientDto clientDto = TimesheetMapper.toClientDto(clientDetails);
        assertNull(clientDto.getName());
    }
}
