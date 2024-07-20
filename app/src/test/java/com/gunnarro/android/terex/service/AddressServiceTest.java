package com.gunnarro.android.terex.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gunnarro.android.terex.TestData;
import com.gunnarro.android.terex.domain.dto.BusinessAddressDto;
import com.gunnarro.android.terex.domain.entity.Address;
import com.gunnarro.android.terex.domain.mapper.TimesheetMapper;
import com.gunnarro.android.terex.repository.AddressRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.ExecutionException;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    private AddressService addressService;

    @Mock
    private AddressRepository addressRepositoryMock;


    @BeforeEach
    public void setup() {
        addressService = new AddressService(addressRepositoryMock);
    }

    @Test
    void getBusinessAddress() {
        Address address = new Address();
        address.setId(33L);
        address.setCity("oslo");
        address.setPostalCode("0880");
        address.setStreetAddress("persgate 34");
        address.setCountry("Norge");
        address.setCountryCode("no");

        when(addressRepositoryMock.getAddress(anyLong())).thenReturn(address);

        BusinessAddressDto businessAddressDto = addressService.getBusinessAddress(address.getId());
        assertEquals(address.getId(), businessAddressDto.getId());
    }

    @Test
    void saveAddress_new() throws ExecutionException, InterruptedException {
        when(addressRepositoryMock.find(anyString())).thenReturn(null);
        when(addressRepositoryMock.insert(any())).thenReturn(1000L);

        Long id = addressService.saveBusinessAddress(TestData.createBusinessAddressDto(null));

        assertEquals(1000L, id);

        verify(addressRepositoryMock, times(1)).find(anyString());
        verify(addressRepositoryMock, times(1)).insert(any());
    }

    @Test
    void saveAddress_update() throws ExecutionException, InterruptedException {
        BusinessAddressDto businessAddressDto = TestData.createBusinessAddressDto(1234L);
        Address address = TimesheetMapper.fromBusinessAddressDto(businessAddressDto);
        when(addressRepositoryMock.getAddress(1234L)).thenReturn(address);

        Long id = addressService.saveBusinessAddress(TestData.createBusinessAddressDto(1234L));
        assertEquals(1234L, id);

        verify(addressRepositoryMock, times(1)).getAddress(1234L);
        verify(addressRepositoryMock, times(1)).update(address);
    }

}
