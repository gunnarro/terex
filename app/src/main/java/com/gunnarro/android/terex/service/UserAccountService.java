package com.gunnarro.android.terex.service;


import android.util.Log;

import com.gunnarro.android.terex.domain.dto.UserAccountDto;
import com.gunnarro.android.terex.domain.entity.UserAccount;
import com.gunnarro.android.terex.domain.mapper.TimesheetMapper;
import com.gunnarro.android.terex.exception.TerexApplicationException;
import com.gunnarro.android.terex.repository.UserAccountRepository;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UserAccountService {

    private final UserAccountRepository userAccountRepository;
    private final OrganizationService organizationService;


    /**
     * For unit test only
     */
    @Inject
    public UserAccountService(UserAccountRepository userAccountRepository, OrganizationService organizationService) {
        this.userAccountRepository = userAccountRepository;
        this.organizationService = organizationService;
    }

    @Inject
    public UserAccountService() {
        this(new UserAccountRepository(), new OrganizationService());
    }

    public Long getActiveUserAccountId() {
        return getUserAccount(1L).getId();
    }

    public Long getDefaultUserAccountId() {
        return userAccountRepository.getDefaultUserAccountId();
    }

    public UserAccountDto getDefaultUserAccount() {
        UserAccount userAccount = userAccountRepository.getDefaultUserAccount();
        if (userAccount == null) {
            return null;
        }
        UserAccountDto userAccountDto = TimesheetMapper.toUserAccountDto(userAccount);
        if (userAccount.getUserAccountType().equals(UserAccount.UserAccountTypeEnum.BUSINESS.name())) {
            userAccountDto.setOrganizationDto(organizationService.getOrganization(userAccount.getOrganizationId()));
        }
        return userAccountDto;
    }

    public UserAccountDto getUserAccount(Long userAccountId) {
        UserAccount userAccount = userAccountRepository.getUserAccount(userAccountId);
        if (userAccount == null) {
            return null;
        }
        UserAccountDto userAccountDto = TimesheetMapper.toUserAccountDto(userAccount);
        if (userAccount.getUserAccountType().equals(UserAccount.UserAccountTypeEnum.BUSINESS.name())) {
            userAccountDto.setOrganizationDto(organizationService.getOrganization(userAccount.getOrganizationId()));
        }
        return userAccountDto;
    }

    public Long saveUserAccount(@NotNull final UserAccountDto userAccountDto) {
        try {
            // save organization data
            if (userAccountDto.getUserAccountType().equals(UserAccount.UserAccountTypeEnum.BUSINESS.name())) {
                Long organizationId = organizationService.save(userAccountDto.getOrganizationDto());
                userAccountDto.getOrganizationDto().setId(organizationId);
            } else if (userAccountDto.getUserAccountType().equals(UserAccount.UserAccountTypeEnum.PRIVATE.name())) {
                throw new TerexApplicationException("INFO", "Private user account is not supported yet!", null);
            }
            // finally save client
            return save(userAccountDto);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("saveUserAccount", String.format("Error: %s", e.getCause()));
            throw new TerexApplicationException("Error saving user account!" + e.getMessage(), "50050", e.getCause());
        }
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    private Long save(@NotNull final UserAccountDto userAccountDto) {
        UserAccount userAccount = TimesheetMapper.fromUserAccountDto(userAccountDto);
        try {
            UserAccount userAccountExisting;
            if (userAccount.getId() == null) {
                userAccountExisting = userAccountRepository.find(userAccount.getUserName());
            } else {
                userAccountExisting = userAccountRepository.getUserAccount(userAccount.getId());
            }
            Log.d("saveUserAccount", String.format("userAccountExisting=%s", userAccountExisting));
            Long id;
            if (userAccountExisting == null) {
                userAccount.setCreatedDate(LocalDateTime.now());
                userAccount.setLastModifiedDate(LocalDateTime.now());
                id = userAccountRepository.insert(userAccount);
                Log.d("savePerson", String.format("inserted new user account, id= %s - %s", id, userAccount));
            } else {
                userAccount.setId(userAccountExisting.getId());
                userAccount.setCreatedDate(userAccountExisting.getCreatedDate());
                userAccount.setLastModifiedDate(LocalDateTime.now());
                userAccountRepository.update(userAccount);
                id = userAccount.getId();
                Log.d("saveUserAccount", String.format("updated user account, Id= %s - %s", id, userAccount));
            }
            return id;
        } catch (Exception e) {
            // Something crashed, therefore restore interrupted state before leaving.
            Thread.currentThread().interrupt();
            throw new TerexApplicationException("Error saving user account! " + e.getMessage(), "50050", e.getCause());
        }
    }
}
