package com.gunnarro.android.terex.domain.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class ClientDto {
    private Long id;
    private String status;
    private OrganizationDto organizationDto;
    private List<ProjectDto> projectList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public OrganizationDto getOrganizationDto() {
        return organizationDto;
    }

    public void setOrganizationDto(OrganizationDto organizationDto) {
        this.organizationDto = organizationDto;
    }

    public List<ProjectDto> getProjectList() {
        return projectList;
    }

    public void setProjectList(List<ProjectDto> projectList) {
        this.projectList = projectList;
    }
}
