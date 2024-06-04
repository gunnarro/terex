package com.gunnarro.android.terex.domain.dto;

import java.util.List;
import java.util.Objects;

public class ProjectDto {
    private Long id;
    private Long clientId;
    private String name;
    private String description;
    private String status;
    private Integer hourlyRate;

    private List<TimesheetDto> timesheetDto;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
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
        return Objects.equals(clientId, that.clientId) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientId, name);
    }
}
