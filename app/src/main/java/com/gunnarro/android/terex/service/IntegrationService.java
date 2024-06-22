package com.gunnarro.android.terex.service;


import android.util.Log;

import com.gunnarro.android.terex.domain.dto.IntegrationDto;
import com.gunnarro.android.terex.domain.entity.Integration;
import com.gunnarro.android.terex.domain.mapper.TimesheetMapper;
import com.gunnarro.android.terex.exception.InputValidationException;
import com.gunnarro.android.terex.exception.TerexApplicationException;
import com.gunnarro.android.terex.repository.IntegrationRepository;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class IntegrationService {

    private final IntegrationRepository integrationRepository;

    /**
     * For unit test onlu
     */
    @Inject
    public IntegrationService(IntegrationRepository integrationRepository) {
        this.integrationRepository = integrationRepository;
    }

    @Inject
    public IntegrationService() {
        this(new IntegrationRepository());
    }

    public IntegrationDto getIntegrationBySystem(String system) {
        return TimesheetMapper.toIntegrationDto(integrationRepository.find(system));
    }

    public IntegrationDto getIntegration(Long integrationId) {
        return TimesheetMapper.toIntegrationDto(integrationRepository.getIntegration(integrationId));
    }

    public List<IntegrationDto> getIntegrations() {
        return TimesheetMapper.toIntegrationDtoList(integrationRepository.getIntegrations());
    }

    public Long saveIntegration(@NotNull final IntegrationDto integrationDto) {
        Integration integration = TimesheetMapper.fromIntegrationDto(integrationDto);
        try {
            // lookup by primary key
            Integration integrationExisting = integrationRepository.find(integration.getSystem());
            Log.d("IntegrationRepository.saveIntegration", String.format("%s", integrationExisting));
            Long id;
            if (integrationExisting == null) {
                integration.setCreatedDate(LocalDateTime.now());
                integration.setLastModifiedDate(LocalDateTime.now());
                id = integrationRepository.insert(integration);
                Log.d("", "inserted new integration: " + id + " - " + integration.getSystem());
            } else {
                integration.setId(integrationExisting.getId());
                integration.setCreatedDate(integrationExisting.getCreatedDate());
                integration.setLastModifiedDate(LocalDateTime.now());
                integrationRepository.update(integration);
                id = integration.getId();
                Log.d("", "updated integration: " + id + " - " + integration.getSystem());
            }
            return id;
        } catch (Exception e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("Error saving integration! " + e.getMessage(), "50050", e.getCause());
        }
    }

    public void delete(Long integrationId) {
        Integration integration = integrationRepository.getIntegration(integrationId);
        if (integration == null) {
            throw new InputValidationException(String.format("Integration not found! integrationId=%s", integrationId), "40044", null);
        }
        integrationRepository.delete(integration);
    }
}
