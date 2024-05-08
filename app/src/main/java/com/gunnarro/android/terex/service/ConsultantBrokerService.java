package com.gunnarro.android.terex.service;


import android.util.Log;

import com.gunnarro.android.terex.domain.dto.ConsultantBrokerDto;
import com.gunnarro.android.terex.domain.entity.ConsultantBroker;
import com.gunnarro.android.terex.domain.entity.ConsultantBrokerWithProject;
import com.gunnarro.android.terex.domain.mapper.TimesheetMapper;
import com.gunnarro.android.terex.exception.TerexApplicationException;
import com.gunnarro.android.terex.repository.ConsultantBrokerRepository;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ConsultantBrokerService {

    private final ProjectService projectService;
    private final ConsultantBrokerRepository consultantBrokerRepository;

    /**
     * For unit test onlu
     */
    @Inject
    public ConsultantBrokerService(ConsultantBrokerRepository consultantBrokerRepository) {
        this.consultantBrokerRepository = consultantBrokerRepository;
        this.projectService = new ProjectService();
    }

    @Inject
    public ConsultantBrokerService() {
        this.consultantBrokerRepository = new ConsultantBrokerRepository();
        this.projectService = new ProjectService();
    }

    public ConsultantBrokerDto getConsultantBroker(Long consultantBrokerId) {
        return TimesheetMapper.toConsultantBrokerDto(consultantBrokerRepository.getConsultantBroker(consultantBrokerId));
    }

    public ConsultantBrokerDto findConsultantBroker(String name) {
        return TimesheetMapper.toConsultantBrokerDto(new ConsultantBrokerWithProject(consultantBrokerRepository.findConsultantBroker(name), null));
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public Long saveConsultantBroker(@NotNull final ConsultantBrokerDto consultantBrokerDto) {
        try {
            ConsultantBroker consultantBrokerExisting;
            if (consultantBrokerDto.getId() == null) {
                consultantBrokerExisting = consultantBrokerRepository.findConsultantBroker(consultantBrokerDto.getName());
            } else {
                consultantBrokerExisting = consultantBrokerRepository.getConsultantBroker(consultantBrokerDto.getId()).getConsultantBroker();
            }
            Log.d("saveConsultantBroker", String.format("existingConsultantBroker=%s", consultantBrokerExisting));
            ConsultantBroker consultantBroker = TimesheetMapper.fromConsultantBrokerDto(consultantBrokerDto);
            consultantBroker.setLastModifiedDate(LocalDateTime.now());
            Long id;
            if (consultantBrokerExisting == null) {
                consultantBroker.setCreatedDate(LocalDateTime.now());
                consultantBroker.setStatus(ConsultantBroker.CosultantBrokerStatusEnum.ACTIVE.name());
                id = consultantBrokerRepository.insertConsultantBroker(consultantBroker);
            } else {
                consultantBroker.setCreatedDate(consultantBrokerExisting.getCreatedDate());
                consultantBroker.setStatus(consultantBrokerExisting.getStatus());
                consultantBroker.setOrganizationId(consultantBrokerExisting.getOrganizationId());
                consultantBrokerRepository.updateConsultantBroker(consultantBroker);
                id = consultantBrokerExisting.getId();
            }
            return id;
        } catch (Exception e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("Error saving project!", e.getMessage(), e.getCause());
        }
    }
}