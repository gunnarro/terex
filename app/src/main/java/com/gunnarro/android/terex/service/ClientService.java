package com.gunnarro.android.terex.service;


import android.util.Log;

import com.gunnarro.android.terex.domain.dto.ClientDto;
import com.gunnarro.android.terex.domain.dto.OrganizationDto;
import com.gunnarro.android.terex.domain.dto.PersonDto;
import com.gunnarro.android.terex.domain.dto.ProjectDto;
import com.gunnarro.android.terex.domain.entity.Client;
import com.gunnarro.android.terex.domain.mapper.TimesheetMapper;
import com.gunnarro.android.terex.exception.TerexApplicationException;
import com.gunnarro.android.terex.repository.ClientRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ClientService {

    private final ClientRepository clientRepository;
    private final OrganizationService organizationService;
    private final ProjectService projectService;
    private final PersonService personService;

    /**
     * For unit test only
     */
    @Inject
    public ClientService(ClientRepository clientRepository, OrganizationService organizationService, ProjectService projectService, PersonService personService) {
        this.clientRepository = clientRepository;
        this.organizationService = organizationService;
        this.projectService = projectService;
        this.personService = personService;
    }

    @Inject
    public ClientService() {
        this(new ClientRepository(), new OrganizationService(), new ProjectService(), new PersonService());
    }

    public Long getClientIdByTimesheetId(Long timesheetId) {
        Long clientId = clientRepository.getClientIdByTimesheetId(timesheetId);
        if (clientId == null) {
            throw new TerexApplicationException(String.format("Timesheet id is not linked to a client! timesheetId=%s", timesheetId), "50045", null);
        }
        return clientId;
    }

    public List<ClientDto> getClients() {
        List<ClientDto> clientDtoList = new ArrayList<>();
        List<Long> clientIds = clientRepository.getAllClientIds();
        clientIds.forEach(id -> clientDtoList.add(getClient(id)));
        return clientDtoList;
    }

    public ClientDto getActiveClient() {
        Client client = clientRepository.getActiveClient();
        return createClientDto(client);
    }

    public ClientDto getClient(Long clientId) {
        Client client = clientRepository.getClient(clientId);
        Log.d("getClient", String.format("clientId=%s, client=%s", clientId, client));
        return createClientDto(client);
    }

    public ClientDto getClientByTimesheetId(Long timesheetId) {
        Long clientId = clientRepository.getClientIdByTimesheetId(timesheetId);
        return getClient(clientId);
    }

    private ClientDto createClientDto(Client client) {
        if (client != null) {
            ClientDto clientDto = TimesheetMapper.toClientDto(client);
            // add organization info
            OrganizationDto organizationDto = organizationService.getOrganization(client.getOrganizationId());
            clientDto.setOrganizationDto(organizationDto);
            // add contact person info
            PersonDto contactPersonDto = personService.getPerson(client.getContactPersonId());
            clientDto.setContactPersonDto(contactPersonDto);
            // add projects, get all projects
            List<ProjectDto> projectDtos = projectService.getProjects(client.getId(), null);
            clientDto.setProjectList(projectDtos);
            return clientDto;
        }
        return null;
    }

    public Long saveClient(ClientDto clientDto) {
        try {
            // save organization data
            if (clientDto.hasOrganization()) {
                Long organizationId = organizationService.save(clientDto.getOrganizationDto());
                clientDto.getOrganizationDto().setId(organizationId);
            }
            // save contact person information
            if (clientDto.hasContactPersonDto()) {
                Long contactPersonId = personService.save(clientDto.getContactPersonDto());
                clientDto.getContactPersonDto().setId(contactPersonId);
            }
            // finally save client
            return save(clientDto);
        } catch (Exception e) {
            Log.e("saveClient", String.format("Error: %s", e.getCause()));
            throw new TerexApplicationException("Error saving client!" + e.getMessage(), "50050", e.getCause());
        }
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    private Long save(ClientDto clientDto) {
        try {
            Client clientExisting;
            if (clientDto.getId() == null) {
                clientExisting = clientRepository.find(clientDto.getName());
            } else {
                clientExisting = clientRepository.getClient(clientDto.getId());
            }
            Log.d("saveClient", String.format("existingClient=%s", clientExisting));
            Client client = TimesheetMapper.fromClientDto(clientDto);
            client.setLastModifiedDate(LocalDateTime.now());
            Long id;
            if (clientExisting == null) {
                client.setCreatedDate(LocalDateTime.now());
                client.setStatus(Client.ClientStatusEnum.ACTIVE.name());
                id = clientRepository.insert(client);
                Log.d("saveClient", String.format("inserted new client, id= %s - %s", id, client));
            } else {
                client.setCreatedDate(clientExisting.getCreatedDate());
                client.setStatus(clientDto.getStatus());
                clientRepository.update(client);
                id = clientExisting.getId();
                Log.d("saveClient", String.format("updated client, id= %s - %s", id, client));
            }
            return id;
        } catch (Exception e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("Error saving client!" + e.getMessage(), "50050", e.getCause());
        }
    }
}