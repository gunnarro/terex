package com.gunnarro.android.terex.domain.dto;

import java.util.List;
import java.util.Objects;

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
    private String name;
    private String status;
    private PersonDto contactPersonDto;
    private OrganizationDto organizationDto;
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

    public OrganizationDto getOrganizationDto() {
        return organizationDto;
    }

    public void setOrganizationDto(OrganizationDto organizationDto) {
        this.organizationDto = organizationDto;
    }

    public PersonDto getContactPersonDto() {
        return contactPersonDto;
    }

    public void setContactPersonDto(PersonDto contactPersonDto) {
        this.contactPersonDto = contactPersonDto;
    }

    public List<ProjectDto> getProjectList() {
        return projectList;
    }

    public void setProjectList(List<ProjectDto> projectList) {
        this.projectList = projectList;
    }

    public boolean hasOrganization() {
        return organizationDto != null;
    }

    public boolean hasContactPersonDto() {
        return contactPersonDto != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientDto clientDto = (ClientDto) o;
        return Objects.equals(name, clientDto.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
