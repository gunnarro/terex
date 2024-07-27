package com.gunnarro.android.terex.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.gunnarro.android.terex.IntegrationTestSetup;
import com.gunnarro.android.terex.domain.dto.OrganizationDto;
import com.gunnarro.android.terex.domain.dto.UserAccountDto;
import com.gunnarro.android.terex.domain.entity.UserAccount;
import com.gunnarro.android.terex.repository.UserAccountRepository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UserAccountServiceTest extends IntegrationTestSetup {

    private UserAccountService userAccountService;

    @Before
    public void setup() {
        super.setupDatabase();
        userAccountService = new UserAccountService(new UserAccountRepository(), new OrganizationService());
    }

    @After
    public void cleanUp() {
        super.cleanUpDatabase();
    }

    @Test
    public void getUserAccount_not_found() {
        assertNull(userAccountService.getUserAccount(456L));
    }

    @Test
    public void saveUserAccount_new() {
        UserAccountDto newUserAccountDto = createUserAccountDto(200L);

        Long userAccountId = userAccountService.saveUserAccount(newUserAccountDto);
        assertEquals(200, userAccountId.longValue());

        UserAccountDto userAccountDto = userAccountService.getUserAccount(userAccountId);
        assertEquals(200, userAccountDto.getId().longValue());

        // update and save use account
        userAccountDto.setPassword("change-me");
        userAccountService.saveUserAccount(userAccountDto);

        UserAccountDto updatedUserAccountDto = userAccountService.getUserAccount(userAccountId);
        assertEquals(200, updatedUserAccountDto.getId().longValue());
        assertEquals("change-me", updatedUserAccountDto.getPassword());
    }

    private UserAccountDto createUserAccountDto(Long id) {
        UserAccountDto userAccountDto = new UserAccountDto();
        userAccountDto.setId(id);
        userAccountDto.setStatus(UserAccount.UserAccountStatusEnum.ACTIVE.name());
        userAccountDto.setUserName("guro-integration-test");
        userAccountDto.setPassword("nope");
        userAccountDto.setDefaultUser(true);
        userAccountDto.setUserAccountType(UserAccount.UserAccountTypeEnum.BUSINESS.name());
        OrganizationDto organizationDto = new OrganizationDto();
        organizationDto.setId(11L);
        organizationDto.setName("gunnarro it test");
        organizationDto.setOrganizationNumber("11223344");
        userAccountDto.setOrganizationDto(organizationDto);
        return userAccountDto;
    }
}
