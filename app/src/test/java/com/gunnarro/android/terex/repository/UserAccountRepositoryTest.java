package com.gunnarro.android.terex.repository;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.gunnarro.android.terex.domain.entity.UserAccount;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserAccountRepositoryTest {

    private UserAccountRepository userAccountRepository;

    @Mock
    private UserAccountDao userAccountDaoMock;

    @BeforeEach
    public void setup() {
        userAccountRepository = new UserAccountRepository(userAccountDaoMock);
    }

    @Test
    void saveUserAccount_new_business() {
        UserAccount newUserAccount = createUserAccount(null);

        when(userAccountDaoMock.findUserAccount(anyString())).thenReturn(null);
        when(userAccountDaoMock.insert(any())).thenReturn(122L);

        Long userAccountId = userAccountRepository.save(newUserAccount);
        assertEquals(122L, userAccountId);
    }

    @Test
    void saveUserAccount_update_business() {
        UserAccount userAccount = createUserAccount(123L);

        when(userAccountDaoMock.getUserAccount(anyLong())).thenReturn(createUserAccount(123L));

        Long userAccountId = userAccountRepository.save(userAccount);
        assertEquals(123L, userAccountId);
    }

    private UserAccount createUserAccount(Long id) {
        UserAccount userAccount = new UserAccount();
        if (id != null) {
            userAccount.setId(id);
        }
        userAccount.setUserName("guro");
        userAccount.setPassword("change-me");
        userAccount.setUserAccountType(UserAccount.UserAccountTypeEnum.BUSINESS.name());
        userAccount.setOrganizationId(333L);
        return userAccount;
    }
}
