package com.gunnarro.android.terex.domain.dto;

import java.util.List;

public class ClientDto {
    private Long id;
    private String name;
    private String status;

    private CompanyDto companyDto;

    private List<ProjectDto> projectList;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public CompanyDto getCompanyDto() {
        return companyDto;
    }

    public void setCompanyDto(CompanyDto companyDto) {
        this.companyDto = companyDto;
    }

    public List<ProjectDto> getProjectList() {
        return projectList;
    }

    public void setProjectList(List<ProjectDto> projectList) {
        this.projectList = projectList;
    }
}
