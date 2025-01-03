package com.gunnarro.android.terex.domain.dto;

import com.gunnarro.android.terex.domain.entity.UserAccount;

import java.util.Objects;

public class UserAccountDto extends BaseDto {

    private String status;
    private String userName;
    private String password;
    private String userAccountType;
    private boolean defaultUser;
    private PersonDto personDto;
    private OrganizationDto organizationDto;

    public UserAccountDto() {
    }

    public UserAccountDto(Long id) {
        setId(id);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserAccountType() {
        return userAccountType;
    }

    public void setUserAccountType(String userAccountType) {
        this.userAccountType = userAccountType;
    }

    public boolean isDefaultUser() {
        return defaultUser;
    }

    public void setDefaultUser(boolean defaultUser) {
        this.defaultUser = defaultUser;
    }

    public PersonDto getPersonDto() {
        return personDto;
    }

    public void setPersonDto(PersonDto personDto) {
        this.personDto = personDto;
    }

    public OrganizationDto getOrganizationDto() {
        return organizationDto;
    }

    public void setOrganizationDto(OrganizationDto organizationDto) {
        this.organizationDto = organizationDto;
    }

    public boolean isBusiness() {
        return userAccountType.equals(UserAccount.UserAccountTypeEnum.BUSINESS.name());
    }

    public boolean isPrivate() {
        return userAccountType.equals(UserAccount.UserAccountTypeEnum.PRIVATE.name());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserAccountDto that = (UserAccountDto) o;
        return Objects.equals(userName, that.userName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName);
    }
}
