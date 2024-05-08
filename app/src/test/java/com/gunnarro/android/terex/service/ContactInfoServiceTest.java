package com.gunnarro.android.terex.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.gunnarro.android.terex.domain.dto.ContactInfoDto;
import com.gunnarro.android.terex.domain.mapper.TimesheetMapper;
import com.gunnarro.android.terex.repository.ContactInfoRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.ExecutionException;

@ExtendWith(MockitoExtension.class)
class ContactInfoServiceTest {

    private ContactInfoService contactInfoService;

    @Mock
    private ContactInfoRepository contactInfoRepositoryMock;

    @BeforeEach
    public void setup() {
        contactInfoService = new ContactInfoService(contactInfoRepositoryMock);
    }

    @Test
    void saveContactInfo_new() throws ExecutionException, InterruptedException {
        ContactInfoDto contactInfoDto = createContactInfoDto();

        when(contactInfoRepositoryMock.findContactInfo(anyString(), anyString())).thenReturn(null);
        when(contactInfoRepositoryMock.insert(any())).thenReturn(1000L);

        Long clientId = contactInfoService.save(contactInfoDto);

        assertEquals(1000L, clientId);
    }

    @Test
    void saveContactInfo_update() {
        ContactInfoDto contactInfoDto = createContactInfoDto();
        contactInfoDto.setId(101L);

        when(contactInfoRepositoryMock.getContactInfo(anyLong())).thenReturn(TimesheetMapper.fromContactInfoDto(contactInfoDto));

        Long personId = contactInfoService.save(contactInfoDto);
        assertEquals(101L, personId);
    }

    private ContactInfoDto createContactInfoDto() {
        ContactInfoDto contactInfoDto = new ContactInfoDto();
        contactInfoDto.setEmailAddress("my@gmail.com");
        contactInfoDto.setMobileNumberCountryCode("+47");
        contactInfoDto.setMobileNumber("34343434");
        return contactInfoDto;
    }

}
