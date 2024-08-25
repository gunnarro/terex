package com.gunnarro.android.terex.domain.dto;

import com.gunnarro.android.terex.domain.entity.Project;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class ProjectDto {
    private Long id;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private String name;
    private String description;
    private String status;
    private Integer hourlyRate;
    private ClientDto clientDto;

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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
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
        return Project.ProjectStatusEnum.ACTIVE.name().equals(status);
    }

    public boolean isClosed() {
        return Project.ProjectStatusEnum.CLOSED.name().equals(status);
    }

    public boolean isNew() {
        return this.id == null;
    }
}
