package com.gunnarro.android.terex.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import com.gunnarro.android.terex.DbHelper;
import com.gunnarro.android.terex.config.AppDatabase;
import com.gunnarro.android.terex.domain.dto.OrganizationDto;
import com.gunnarro.android.terex.domain.dto.UserAccountDto;
import com.gunnarro.android.terex.domain.entity.UserAccount;
import com.gunnarro.android.terex.repository.UserAccountRepository;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class UserAccountServiceTest {

    private UserAccountService userAccountService;
    private AppDatabase appDatabase;

    @Before
    public void setup() {
        Context appContext = ApplicationProvider.getApplicationContext();
        appDatabase = Room.inMemoryDatabaseBuilder(appContext, AppDatabase.class).build();
        AppDatabase.init(appContext);
        userAccountService = new UserAccountService(new UserAccountRepository(), new OrganizationService());
        // load test data
        List<String> sqlQueryList = DbHelper.readMigrationSqlQueryFile(appContext, "database/test_data.sql");
        appDatabase.getOpenHelper().getWritableDatabase().beginTransaction();
        sqlQueryList.forEach(query -> {
            System.out.println(query);
            appDatabase.getOpenHelper().getWritableDatabase().execSQL(query);
        });
        appDatabase.getOpenHelper().getWritableDatabase().endTransaction();
        assertTrue(appDatabase.getOpenHelper().getWritableDatabase().isDatabaseIntegrityOk());
    }
/* fixme
    @Test
    public void getUserAccount_business() {
        UserAccountDto userAccountDto = userAccountService.getUserAccount(2001L);
        assertEquals(2001, userAccountDto.getId().longValue());
    }
*/
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
        UserAccountDto userAccount = new UserAccountDto();
        userAccount.setId(id);
        userAccount.setUserName("guro-integration-test");
        userAccount.setPassword("nope");
        userAccount.setUserAccountType(UserAccount.UserAccountTypeEnum.BUSINESS.name());
        OrganizationDto organizationDto = new OrganizationDto();
        organizationDto.setId(11L);
        userAccount.setOrganizationDto(organizationDto);
        return userAccount;
    }
}
