package com.gunnarro.android.terex.domain.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.TypeConverters;

import com.gunnarro.android.terex.domain.converter.LocalDateConverter;
import com.gunnarro.android.terex.domain.converter.LocalDateTimeConverter;

import java.util.Objects;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@TypeConverters({LocalDateConverter.class, LocalDateTimeConverter.class})
@Entity(tableName = "user_account", indices = {@Index(value = {"user_name"},
        unique = true)})
public class UserAccount extends BaseEntity {

    public enum UserAccountTypeEnum {
        BUSINESS, PRIVATE
    }

    public enum UserAccountStatusEnum {
        ACTIVE, DEACTIVATED
    }

    @NonNull
    @ColumnInfo(name = "user_name")
    private String userName;

    @ColumnInfo(name = "password")
    private String password;

    /**
     * Account type is business or private
     */
    @NonNull
    @ColumnInfo(name = "account_type")
    private String userAccountType;

    @NonNull
    @ColumnInfo(name = "status")
    private String status;

    @ColumnInfo(name = "organization_id")
    private Long organizationId;

    @ColumnInfo(name = "person_id")
    private Long personId;

    @ColumnInfo(name = "is_default_user")
    private int defaultUser;

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

    public @NonNull String getStatus() {
        return status;
    }

    public void setStatus(@NonNull String status) {
        this.status = status;
    }

    public int getDefaultUser() {
        return defaultUser;
    }

    public boolean isDefaultUser() {
        return defaultUser == 1;
    }

    public void setDefaultUser(int defaultUser) {
        this.defaultUser = defaultUser;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserAccount that = (UserAccount) o;
        return Objects.equals(userName, that.userName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName);
    }


    @androidx.annotation.NonNull
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UserAccount{");
        sb.append("userName='").append(userName).append('\'');
        sb.append(", password='").append(password).append('\'');
        sb.append(", userAccountType='").append(userAccountType).append('\'');
        sb.append(", organizationId='").append(organizationId).append('\'');
        sb.append(", personId='").append(personId).append('\'');
        sb.append(", defaultUser='").append(defaultUser).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
