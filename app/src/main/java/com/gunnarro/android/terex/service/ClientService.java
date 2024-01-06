package com.gunnarro.android.terex.service;


import android.util.Log;

import com.gunnarro.android.terex.domain.dto.ClientDto;
import com.gunnarro.android.terex.domain.entity.Client;
import com.gunnarro.android.terex.domain.entity.ClientWithProject;
import com.gunnarro.android.terex.domain.entity.ConsultantBroker;
import com.gunnarro.android.terex.domain.mapper.TimesheetMapper;
import com.gunnarro.android.terex.exception.TerexApplicationException;
import com.gunnarro.android.terex.repository.ClientRepository;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ClientService {

    private final ProjectService projectService;
    private final ClientRepository clientRepository;

    /**
     * For unit test onlu
     */
    @Inject
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
        this.projectService = new ProjectService();
    }

    @Inject
    public ClientService() {
        this.clientRepository = new ClientRepository();
        this.projectService = new ProjectService();
    }

    public ClientDto getClient(Long clientId) {
        return TimesheetMapper.toClientDto(clientRepository.getClient(clientId));
    }

    public ClientDto findClient(String name) {
        return TimesheetMapper.toClientDto(new ClientWithProject(clientRepository.findClient(name), null));
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public Long saveClient(@NotNull final ClientDto clientDto) {
        try {
            Client clientExisting;
            if (clientDto.getId() == null) {
                clientExisting = clientRepository.findClient(clientDto.getName());
            } else {
                clientExisting = clientRepository.getClient(clientDto.getId()).getClient();
            }
            Log.d("saveClient", String.format("existingClient=%s", clientExisting));
            Client client = TimesheetMapper.fromClientDto(clientDto);
            client.setLastModifiedDate(LocalDateTime.now());
            Long id;
            if (clientExisting == null) {
                client.setCreatedDate(LocalDateTime.now());
                client.setStatus(ConsultantBroker.CosultantBrokerStatusEnum.ACTIVE.name());
                id = clientRepository.insert(client);
            } else {
                client.setCreatedDate(clientExisting.getCreatedDate());
                client.setStatus(clientExisting.getStatus());
                client.setCompanyId(clientExisting.getCompanyId());
                clientRepository.update(client);
                id = clientExisting.getId();
            }
            return id;
        } catch (Exception e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("Error saving client!", e.getMessage(), e.getCause());
        }
    }
}