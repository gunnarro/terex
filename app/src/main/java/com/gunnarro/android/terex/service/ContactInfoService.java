package com.gunnarro.android.terex.service;


import android.util.Log;

import com.gunnarro.android.terex.domain.dto.ContactInfoDto;
import com.gunnarro.android.terex.domain.entity.ContactInfo;
import com.gunnarro.android.terex.domain.mapper.TimesheetMapper;
import com.gunnarro.android.terex.exception.TerexApplicationException;
import com.gunnarro.android.terex.repository.ContactInfoRepository;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ContactInfoService {

    private final ContactInfoRepository contactInfoRepository;

    /**
     * For unit test only
     */
    @Inject
    public ContactInfoService(ContactInfoRepository contactInfoRepository) {
        this.contactInfoRepository = contactInfoRepository;
    }

    @Inject
    public ContactInfoService() {
        this.contactInfoRepository = new ContactInfoRepository();
    }

    public ContactInfoDto getContactInfo(Long contactInfoId) {
        return TimesheetMapper.toContactInfoDto(contactInfoRepository.getContactInfo(contactInfoId));
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public Long save(@NotNull final ContactInfoDto contactInfoDto) {
        ContactInfo contactInfo = TimesheetMapper.fromContactInfoDto(contactInfoDto);
        try {
            ContactInfo contactInfoExisting;
            if (contactInfo.getId() == null) {
                contactInfoExisting = contactInfoRepository.find(contactInfo.getMobileNumber(), contactInfoDto.getEmailAddress());
            } else {
                contactInfoExisting = contactInfoRepository.getContactInfo(contactInfo.getId());
            }
            Log.d("saveContactInformation", String.format("existing contact info=%s", contactInfoExisting));
            Long id;
            if (contactInfoExisting == null) {
                contactInfo.setCreatedDate(LocalDateTime.now());
                contactInfo.setLastModifiedDate(LocalDateTime.now());
                id = contactInfoRepository.insert(contactInfo);
                Log.d("saveContactInformation", String.format("inserted new contact info, id= %s - %s", id, contactInfo));
            } else {
                contactInfo.setId(contactInfoExisting.getId());
                contactInfo.setCreatedDate(contactInfoExisting.getCreatedDate());
                contactInfo.setLastModifiedDate(LocalDateTime.now());
                contactInfoRepository.update(contactInfo);
                id = contactInfo.getId();
                Log.d("saveContactInformation", String.format("updated contact info, Id= %s - %s", id, contactInfo));
            }
            return id;
        } catch (Exception e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("Error saving organization! " + e.getMessage(), "50050", e.getCause());
        }
    }
}
