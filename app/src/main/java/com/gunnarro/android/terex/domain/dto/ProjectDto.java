package com.gunnarro.android.terex.domain.dto;

import java.util.List;

public class ProjectDto {
    private Long id;
    private String name;
    private String description;
    private String status;

    private List<TimesheetDto> timesheetDto;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<TimesheetDto> getTimesheetDto() {
        return timesheetDto;
    }

    public void setTimesheetDto(List<TimesheetDto> timesheetDto) {
        this.timesheetDto = timesheetDto;
    }
}
