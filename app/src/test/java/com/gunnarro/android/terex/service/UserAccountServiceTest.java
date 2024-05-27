package com.gunnarro.android.terex.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.gunnarro.android.terex.domain.dto.OrganizationDto;
import com.gunnarro.android.terex.domain.dto.UserAccountDto;
import com.gunnarro.android.terex.domain.entity.UserAccount;
import com.gunnarro.android.terex.domain.mapper.TimesheetMapper;
import com.gunnarro.android.terex.repository.UserAccountRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.ExecutionException;

@ExtendWith(MockitoExtension.class)
class UserAccountServiceTest {

    private UserAccountService userAccountService;
    @Mock
    private UserAccountRepository userAccountRepositoryMock;
    @Mock
    private OrganizationService organizationServiceMock;

    @BeforeEach
    public void setup() {
        userAccountService = new UserAccountService(userAccountRepositoryMock, organizationServiceMock);
    }

    @Test
    void getUserAccount_business() {
        UserAccount userAccount = createUserAccount(333L);
        OrganizationDto organizationDto = new OrganizationDto();
        organizationDto.setId(userAccount.getOrganizationId());

        when(userAccountRepositoryMock.getUserAccount(anyLong())).thenReturn(userAccount);
        when(organizationServiceMock.getOrganization(anyLong())).thenReturn(organizationDto);

        UserAccountDto userAccountDto = userAccountService.getUserAccount(userAccount.getId());
        assertEquals(userAccount.getId(), userAccountDto.getId());
        assertEquals(userAccount.getUserName(), userAccountDto.getUserName());
        assertEquals(userAccount.getPassword(), userAccountDto.getPassword());
        assertNull(userAccountDto.getPersonDto());
        assertEquals(userAccount.getUserAccountType(), userAccountDto.getUserAccountType());
        assertEquals(userAccount.getOrganizationId(), userAccountDto.getOrganizationDto().getId());
    }

    @Test
    void saveUserAccount_new() throws ExecutionException, InterruptedException {
        UserAccountDto userAccountDto = TimesheetMapper.toUserAccountDto(createUserAccount(null));

        when(userAccountRepositoryMock.findUserAccount(anyString())).thenReturn(null);
        when(organizationServiceMock.save(any())).thenReturn(555L);
        when(userAccountRepositoryMock.insert(any())).thenReturn(400L);

        Long userAccountId = userAccountService.saveUserAccount(userAccountDto);

        assertEquals(400L, userAccountId);
    }

    @Test
    void userAccount_update() {
        UserAccount userAccount = createUserAccount(400L);
        UserAccountDto userAccountDto = TimesheetMapper.toUserAccountDto(userAccount);

        when(userAccountRepositoryMock.getUserAccount(anyLong())).thenReturn(userAccount);
        Long userAccountId = userAccountService.saveUserAccount(userAccountDto);

        assertEquals(400L, userAccountId);
    }

    private UserAccount createUserAccount(Long id) {
        UserAccount userAccount = new UserAccount();
        userAccount.setId(id);
        userAccount.setUserName("guro");
        userAccount.setPassword("nope");
        userAccount.setUserAccountType(UserAccount.UserAccountTypeEnum.BUSINESS.name());
        userAccount.setOrganizationId(11L);
        return userAccount;
    }
}
