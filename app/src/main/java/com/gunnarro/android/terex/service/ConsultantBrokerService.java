package com.gunnarro.android.terex.service;


import android.content.Context;
import android.util.Log;

import com.gunnarro.android.terex.domain.dto.ConsultantBrokerDto;
import com.gunnarro.android.terex.domain.entity.ConsultantBroker;
import com.gunnarro.android.terex.domain.mapper.TimesheetMapper;
import com.gunnarro.android.terex.exception.TerexApplicationException;
import com.gunnarro.android.terex.repository.ConsultantBrokerRepository;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ConsultantBrokerService {

    private final ConsultantBrokerRepository consultantBrokerRepository;

    /**
     * For unit test onlu
     */
    @Inject
    public ConsultantBrokerService(ConsultantBrokerRepository consultantBrokerRepository) {
        this.consultantBrokerRepository = consultantBrokerRepository;
    }

    @Inject
    public ConsultantBrokerService() {
        consultantBrokerRepository = new ConsultantBrokerRepository();
    }

    public ConsultantBrokerDto getConsultantBroker(Long consultantBrokerId) {
       return TimesheetMapper.toConsultantBrokerDto(consultantBrokerRepository.getConsultantBroker(consultantBrokerId));
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
                id = consultantBrokerRepository.insertConsultantBroker(consultantBroker);
            } else {
                consultantBroker.setCreatedDate(consultantBrokerExisting.getCreatedDate());
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