package com.gunnarro.android.terex.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.gunnarro.android.terex.domain.dto.AddressDto;
import com.gunnarro.android.terex.domain.dto.ClientDto;
import com.gunnarro.android.terex.domain.dto.ContactInfoDto;
import com.gunnarro.android.terex.domain.dto.OrganizationDto;
import com.gunnarro.android.terex.domain.dto.PersonDto;
import com.gunnarro.android.terex.domain.mapper.TimesheetMapper;
import com.gunnarro.android.terex.repository.ClientRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.ExecutionException;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    private ClientService clientService;

    @Mock
    private ClientRepository clientRepositoryMock;

    @BeforeEach
    public void setup() {
        clientService = new ClientService(clientRepositoryMock);
    }


    @Test
    void saveClient_new() throws ExecutionException, InterruptedException {
        ClientDto clientDto = new ClientDto();
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
        contectPersonDto.setFirstName("ole");
        contectPersonDto.setLastName("hansen");

        AddressDto organizationAddress = new AddressDto();
        organizationAddress.setStreetName("my-street");
        organizationAddress.setStreetNumber("23");
        organizationAddress.setStreetNumberPrefix("b");
        organizationAddress.setCity("oslo");
        organizationAddress.setCountry("norway");
        organizationAddress.setPostCode("0467");

        organizationDto.setAddress(organizationAddress);
        organizationDto.setContactPerson(contectPersonDto);
        organizationDto.setContactInfo(contactInfoDto);
        return organizationDto;
    }

}
