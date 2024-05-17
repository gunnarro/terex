package com.gunnarro.android.terex.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.gunnarro.android.terex.domain.dto.BusinessAddressDto;
import com.gunnarro.android.terex.domain.dto.ClientDto;
import com.gunnarro.android.terex.domain.dto.ContactInfoDto;
import com.gunnarro.android.terex.domain.dto.OrganizationDto;
import com.gunnarro.android.terex.domain.dto.PersonDto;
import com.gunnarro.android.terex.domain.entity.Client;
import com.gunnarro.android.terex.domain.entity.Project;
import com.gunnarro.android.terex.domain.mapper.TimesheetMapper;
import com.gunnarro.android.terex.repository.ClientRepository;
import com.gunnarro.android.terex.repository.ProjectRepository;

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
    private ProjectRepository projectRepositoryMock;
    @Mock
    private PersonService personServiceMock;

    @BeforeEach
    public void setup() {
        clientService = new ClientService(clientRepositoryMock, projectRepositoryMock, personServiceMock);
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

        Project project = new Project();
        project.setId(111L);
        project.setClientId(client.getId());
        project.setName("terex app development");
        project.setStatus("ACTIVE");

        when(clientRepositoryMock.getClient(client.getId())).thenReturn(client);
        when(projectRepositoryMock.getProjects(client.getId(), Project.ProjectStatusEnum.ACTIVE.name())).thenReturn(List.of(project));
        when(personServiceMock.getPerson(client.getContactPersonId())).thenReturn(personDto);

        ClientDto clientDto = clientService.getClient(client.getId());
        assertEquals(client.getId(), clientDto.getId());
        assertEquals(client.getName(), clientDto.getName());
        assertEquals("ACTIVE", clientDto.getStatus());

        assertEquals(client.getContactPersonId(), clientDto.getCntactPersonDto().getId());
        assertEquals(client.getContactPersonId(), clientDto.getCntactPersonDto().getId());
        assertEquals("petter hansen", clientDto.getCntactPersonDto().getFullName());
        assertEquals(4444, clientDto.getCntactPersonDto().getContactInfo().getId());
        assertEquals("contact@gmail.org", clientDto.getCntactPersonDto().getContactInfo().getEmailAddress());
        assertEquals("+47", clientDto.getCntactPersonDto().getContactInfo().getMobileNumberCountryCode());
        assertEquals("33445566", clientDto.getCntactPersonDto().getContactInfo().getMobileNumber());

        assertEquals(1, clientDto.getProjectList().size());
        assertEquals(project.getClientId(), clientDto.getProjectList().get(0).getClientId());
        assertEquals(project.getName(), clientDto.getProjectList().get(0).getName());
        assertEquals(project.getStatus(), clientDto.getProjectList().get(0).getStatus());
    }

    @Test
    void saveClient_new() throws ExecutionException, InterruptedException {
        ClientDto clientDto = new ClientDto();
        clientDto.setName("gunnarro as");
        clientDto.setStatus("ACTIVE");
        clientDto.setOrganizationDto(createOrganizationDto());

        when(clientRepositoryMock.findClient(anyString())).thenReturn(null);
        when(clientRepositoryMock.insert(any())).thenReturn(1000L);

        Long clientId = clientService.saveClient(clientDto);

        assertEquals(1000L, clientId);
    }

    @Test
    void saveClient_update() throws ExecutionException, InterruptedException {
        ClientDto clientDto = new ClientDto();
        clientDto.setId(100L);
        clientDto.setName("gunnarro as");
        clientDto.setStatus("ACTIVE");
        clientDto.setOrganizationDto(createOrganizationDto());

        when(clientRepositoryMock.getClient(anyLong())).thenReturn(TimesheetMapper.fromClientDto(clientDto));
        when(clientRepositoryMock.update(any())).thenReturn(1);

        Long clientId = clientService.saveClient(clientDto);

        assertEquals(100L, clientId);
    }

    private OrganizationDto createOrganizationDto() {
        OrganizationDto organizationDto = new OrganizationDto();
        organizationDto.setName("gunnarro");
        organizationDto.setOrganizationNumber("123456789");
        organizationDto.setBankAccountNumber("23232323");
        organizationDto.setIndustryType("SOFTWARE");

        ContactInfoDto contactInfoDto = new ContactInfoDto();
        contactInfoDto.setEmailAddress("my@org.com");
        contactInfoDto.setMobileNumberCountryCode("+47");
        contactInfoDto.setMobileNumber("22334455");

        PersonDto contectPersonDto = new PersonDto();
        contectPersonDto.setFullName("ole hansen");

        BusinessAddressDto organizationAddress = new BusinessAddressDto();
        organizationAddress.setAddress("my-street 120 e");

        organizationAddress.setCity("oslo");
        organizationAddress.setCountry("norway");
        organizationAddress.setPostalCode("0467");

        organizationDto.setBusinessAddress(organizationAddress);
        organizationDto.setContactPerson(contectPersonDto);
        organizationDto.setContactInfo(contactInfoDto);
        return organizationDto;
    }
}
