package com.gunnarro.android.terex.domain.dto;

import com.gunnarro.android.terex.domain.entity.Project;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class ProjectDto {
    private Long id;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private String name;
    private String description;
    private String status;
    private Integer hourlyRate;
    private ClientDto clientDto;
    private List<TimesheetDto> timesheetDto;

    public ProjectDto() {
        this.status = Project.ProjectStatusEnum.ACTIVE.name();
    }

    public ProjectDto(Long clientId) {
        this();
        this.clientDto = new ClientDto(clientId);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(Integer hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public ClientDto getClientDto() {
        return clientDto;
    }

    public void setClientDto(ClientDto clientDto) {
        this.clientDto = clientDto;
    }

    public List<TimesheetDto> getTimesheetDto() {
        return timesheetDto;
    }

    public void setTimesheetDto(List<TimesheetDto> timesheetDto) {
        this.timesheetDto = timesheetDto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectDto that = (ProjectDto) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public boolean isActive() {
        return this.status.equals(Project.ProjectStatusEnum.ACTIVE.name());
    }

    public boolean isClosed() {
        return this.status.equals(Project.ProjectStatusEnum.CLOSED.name());
    }
}
