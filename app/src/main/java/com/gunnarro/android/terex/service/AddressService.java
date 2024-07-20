package com.gunnarro.android.terex.service;


import android.util.Log;

import com.gunnarro.android.terex.domain.dto.BusinessAddressDto;
import com.gunnarro.android.terex.domain.entity.Address;
import com.gunnarro.android.terex.domain.mapper.TimesheetMapper;
import com.gunnarro.android.terex.exception.TerexApplicationException;
import com.gunnarro.android.terex.repository.AddressRepository;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AddressService {

    private final AddressRepository addressRepository;

    /**
     * For unit test only
     */
    @Inject
    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @Inject
    public AddressService() {
        this(new AddressRepository());
    }

    public BusinessAddressDto getBusinessAddress(Long addressId) {
        return TimesheetMapper.toABusinessAddressDto(addressRepository.getAddress(addressId));
    }

    public Long saveBusinessAddress(@NotNull final BusinessAddressDto businessAddressDto) {
        Address address = TimesheetMapper.fromBusinessAddressDto(businessAddressDto);
        try {
            Address addressExisting;
            if (address.getId() == null) {
                addressExisting = addressRepository.find(address.getStreetAddress());
            } else {
                addressExisting = addressRepository.getAddress(address.getId());
            }
            Log.d("saveAddress", String.format("streetAddress=%s, isExisting=%s", address.getStreetAddress(), addressExisting != null));
            Long addressId;
            if (addressExisting == null) {
                address.setCreatedDate(LocalDateTime.now());
                address.setLastModifiedDate(LocalDateTime.now());
                addressId = addressRepository.insert(address);
                Log.d("saveAddress", String.format("inserted new address: %s - %s", addressId, address.getStreetAddress()));
            } else {
                address.setId(addressExisting.getId());
                address.setCreatedDate(addressExisting.getCreatedDate());
                address.setLastModifiedDate(LocalDateTime.now());
                addressRepository.update(address);
                addressId = address.getId();
                Log.d("saveAddress", String.format("updated address: %s - %s", addressId, address.getStreetAddress()));
            }
            return addressId;
        } catch (Exception e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("Error saving address! " + e.getMessage(), "50050", e.getCause());
        }
    }
}
