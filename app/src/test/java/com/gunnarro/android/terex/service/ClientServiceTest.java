package com.gunnarro.android.terex.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gunnarro.android.terex.TestData;
import com.gunnarro.android.terex.domain.dto.ClientDto;
import com.gunnarro.android.terex.domain.dto.ContactInfoDto;
import com.gunnarro.android.terex.domain.dto.PersonDto;
import com.gunnarro.android.terex.domain.dto.ProjectDto;
import com.gunnarro.android.terex.domain.entity.Client;
import com.gunnarro.android.terex.domain.mapper.TimesheetMapper;
import com.gunnarro.android.terex.repository.ClientRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.ExecutionException;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    private ClientService clientService;

    @Mock
    private ClientRepository clientRepositoryMock;
    @Mock
    private ProjectService projectServiceMock;
    @Mock
    private PersonService personServiceMock;

    @Mock
    private OrganizationService organizationServiceMock;

    @BeforeEach
    public void setup() {
        clientService = new ClientService(clientRepositoryMock, organizationServiceMock, projectServiceMock, personServiceMock);
    }

    @Test
    void getClient() {
        Client client = new Client();
        client.setId(23L);
        client.setName("unit-test-org");
        client.setStatus(Client.ClientStatusEnum.ACTIVE.name());
        client.setOrganizationId(12345678L);
        client.setContactPersonId(444L);

        PersonDto personDto = new PersonDto();
        personDto.setId(client.getContactPersonId());
        personDto.setFullName("petter hansen");

        ContactInfoDto contactInfoDto = new ContactInfoDto();
        contactInfoDto.setId(4444L);
        contactInfoDto.setEmailAddress("contact@gmail.org");
        contactInfoDto.setMobileNumber("33445566");
        contactInfoDto.setMobileNumberCountryCode("+47");

        personDto.setContactInfo(contactInfoDto);

        ProjectDto projectDto = new ProjectDto();
        projectDto.setId(111L);
        projectDto.setClientDto(new ClientDto(client.getId()));
        projectDto.setName("terex app development");
        projectDto.setStatus("ACTIVE");

        when(clientRepositoryMock.getClient(client.getId())).thenReturn(client);
        when(projectServiceMock.getProjects(client.getId(), null)).thenReturn(List.of(projectDto));
        when(personServiceMock.getPerson(client.getContactPersonId())).thenReturn(personDto);

        ClientDto clientDto = clientService.getClient(client.getId());
        assertEquals(client.getId(), clientDto.getId());
        assertEquals(client.getName(), clientDto.getName());
        assertEquals("ACTIVE", clientDto.getStatus());

        assertEquals(client.getContactPersonId(), clientDto.getContactPersonDto().getId());
        assertEquals(client.getContactPersonId(), clientDto.getContactPersonDto().getId());
        assertEquals("petter hansen", clientDto.getContactPersonDto().getFullName());
        assertEquals(4444, clientDto.getContactPersonDto().getContactInfo().getId());
        assertEquals("contact@gmail.org", clientDto.getContactPersonDto().getContactInfo().getEmailAddress());
        assertEquals("+47", clientDto.getContactPersonDto().getContactInfo().getMobileNumberCountryCode());
        assertEquals("33445566", clientDto.getContactPersonDto().getContactInfo().getMobileNumber());

        assertEquals(1, clientDto.getProjectList().size());
        assertEquals(projectDto.getClientDto().getId(), clientDto.getProjectList().get(0).getClientDto().getId());
        assertEquals(projectDto.getName(), clientDto.getProjectList().get(0).getName());
        assertEquals(projectDto.getStatus(), clientDto.getProjectList().get(0).getStatus());
    }

    @Test
    void saveClient_new() throws ExecutionException, InterruptedException {
        ClientDto clientDto = new ClientDto(null);
        clientDto.setName("gunnarro as");
        clientDto.setStatus("ACTIVE");
        clientDto.setOrganizationDto(TestData.createOrganizationDto(null, "my org name", "123456789"));
        clientDto.setContactPersonDto(TestData.createContactPerson(null, "jen petter hansen"));

        when(clientRepositoryMock.find(anyString())).thenReturn(null);
        when(clientRepositoryMock.insert(any())).thenReturn(1000L);

        Long clientId = clientService.saveClient(clientDto);

        assertEquals(1000L, clientId);

        verify(clientRepositoryMock, times(1)).find(clientDto.getName());
        verify(clientRepositoryMock, times(1)).insert(any());
    }

    @Test
    void saveClient_update() throws ExecutionException, InterruptedException {
        ClientDto clientDto = new ClientDto(null);
        clientDto.setId(100L);
        clientDto.setName("gunnarro as");
        clientDto.setStatus("ACTIVE");
        clientDto.setOrganizationDto(TestData.createOrganizationDto(null, "my org name", "123456789"));
        clientDto.setContactPersonDto(TestData.createContactPerson(null, "jen petter hansen"));

        when(clientRepositoryMock.getClient(anyLong())).thenReturn(TimesheetMapper.fromClientDto(clientDto));
        when(clientRepositoryMock.update(any())).thenReturn(1);

        Long clientId = clientService.saveClient(clientDto);

        assertEquals(100L, clientId);

        verify(clientRepositoryMock, times(1)).getClient(clientDto.getId());
        verify(clientRepositoryMock, times(1)).update(any());
    }


}
