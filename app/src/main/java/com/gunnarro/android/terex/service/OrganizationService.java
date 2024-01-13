package com.gunnarro.android.terex.service;


import android.util.Log;

import com.gunnarro.android.terex.domain.dto.OrganizationDto;
import com.gunnarro.android.terex.domain.entity.Organization;
import com.gunnarro.android.terex.domain.mapper.TimesheetMapper;
import com.gunnarro.android.terex.exception.TerexApplicationException;
import com.gunnarro.android.terex.repository.OrganizationRepository;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class OrganizationService {

    private final OrganizationRepository organizationRepository;

    /**
     * For unit test onlu
     */
    @Inject
    public OrganizationService(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    @Inject
    public OrganizationService() {
        this.organizationRepository = new OrganizationRepository();
    }

    public OrganizationDto getOrganization(Long organizationId) {
        return TimesheetMapper.toOrganizationDto(organizationRepository.getOrganization(organizationId));
    }

    public List<OrganizationDto> getOrganizations() {
        return TimesheetMapper.toOrganizationDtoList(organizationRepository.getOrganizations());
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public Long save(@NotNull final OrganizationDto organizationDto) {
        Organization organization = TimesheetMapper.fromOrganizationDto(organizationDto);
        try {
            Organization organizationExisting = organizationRepository.findOrganization(organization.getName());
            Log.d("save organization", String.format("%s", organizationExisting));
            Long id;
            if (organizationExisting == null) {
                organization.setCreatedDate(LocalDateTime.now());
                organization.setLastModifiedDate(LocalDateTime.now());
                id = organizationRepository.insert(organization);
                Log.d("", "inserted new organization: " + id + " - " + organization.getName());
            } else {
                organization.setId(organizationExisting.getId());
                organization.setCreatedDate(organizationExisting.getCreatedDate());
                organization.setLastModifiedDate(LocalDateTime.now());
                organizationRepository.update(organization);
                id = organization.getId();
                Log.d("", "updated organization: " + id + " - " + organization.getName());
            }
            return id;
        } catch (Exception e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("Error saving organization! " + e.getMessage(), "50050", e.getCause());
        }
    }
}
