package com.gunnarro.android.terex.domain.entity;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.TypeConverters;

import com.gunnarro.android.terex.domain.converter.LocalDateConverter;
import com.gunnarro.android.terex.domain.converter.LocalDateTimeConverter;

import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TypeConverters({LocalDateConverter.class, LocalDateTimeConverter.class})
@Entity(tableName = "client", indices = {@Index(value = {"organization_id"},
        unique = true)})
public class Client extends BaseEntity {

    public enum ClientStatusEnum {ACTIVE, DEACTIVATED}

    @ColumnInfo(name = "organization_id")
    private Long organizationId;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "contact_person_id")
    private Long contactPersonId;

    @ColumnInfo(name = "status")
    private String status;

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getContactPersonId() {
        return contactPersonId;
    }

    public void setContactPersonId(Long contactPersonId) {
        this.contactPersonId = contactPersonId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return Objects.equals(organizationId, client.organizationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(organizationId);
    }


    @NonNull
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Client{");
        sb.append("organizationId=").append(organizationId);
        sb.append(", name='").append(name).append('\'');
        sb.append(", contactPersonId=").append(contactPersonId);
        sb.append(", status='").append(status).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
