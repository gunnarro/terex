package com.gunnarro.android.terex.service;


import android.util.Log;

import com.gunnarro.android.terex.domain.dto.ClientDto;
import com.gunnarro.android.terex.domain.entity.Client;
import com.gunnarro.android.terex.domain.entity.ClientDetails;
import com.gunnarro.android.terex.domain.entity.ConsultantBroker;
import com.gunnarro.android.terex.domain.entity.Project;
import com.gunnarro.android.terex.domain.mapper.TimesheetMapper;
import com.gunnarro.android.terex.exception.TerexApplicationException;
import com.gunnarro.android.terex.repository.ClientRepository;
import com.gunnarro.android.terex.repository.ProjectRepository;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ClientService {

    private final ClientRepository clientRepository;
    private final ProjectRepository projectRepository;

    /**
     * For unit test only
     */
    @Inject
    public ClientService(ClientRepository clientRepository, ProjectRepository projectRepository) {
        this.clientRepository = clientRepository;
        this.projectRepository = projectRepository;
    }

    @Inject
    public ClientService() {
        this.clientRepository = new ClientRepository();
        this.projectRepository = new ProjectRepository();
    }

    public List<ClientDto> getClients() {
        List<Client> clients = clientRepository.getClients();
        return TimesheetMapper.toClientDtoList(clients);
    }

    public ClientDto getClient(Long clientId) {
        Client client = clientRepository.getClient(clientId);
        Log.d("getClient", "clientId=" + clientId + ", client=" + client);
        if (client != null) {
            List<Project> projects = projectRepository.getProjects(client.getId(), Project.ProjectStatusEnum.ACTIVE.name());
            ClientDetails clientDetails = new ClientDetails();
            clientDetails.setClient(client);
            clientDetails.setProjectList(projects);
            return TimesheetMapper.toClientDto(clientDetails);
        }
        return null;
    }

    public ClientDto findClient(String name) {
        Client client = clientRepository.findClient(name);
        if (client != null) {
            ClientDetails clientDetails = new ClientDetails();
            clientDetails.setClient(client);
            return TimesheetMapper.toClientDto(clientDetails);
        }
        return null;
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public Long saveClient(@NotNull final ClientDto clientDto) {
        try {
            Client clientExisting;
            if (clientDto.getId() == null) {
                clientExisting = clientRepository.findClient(clientDto.getName());
            } else {
                clientExisting = clientRepository.getClient(clientDto.getId());
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
                client.setOrganizationId(clientExisting.getOrganizationId());
                clientRepository.update(client);
                id = clientExisting.getId();
            }
            return id;
        } catch (Exception e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("Error saving client!" + e.getMessage(), "50050", e.getCause());
        }
    }
}