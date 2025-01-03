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

    /**
     * EHF: invoice is sent to customers billing system, only for business.
     * E_INVOICE: invoice is sent to customers online banking (nettbank), for private persons.
     * E_MAIL: invoice is sent to customers email address.
     */
    public enum InvoiceTypeEnum {
        EHF, E_INVOICE, E_MAIL
    }

    @ColumnInfo(name = "organization_id")
    private Long organizationId;

    @ColumnInfo(name = "contact_person_id")
    private Long contactPersonId;

    @ColumnInfo(name = "name")
    private String name;

    @NonNull
    @ColumnInfo(name = "status")
    private String status;

    @ColumnInfo(name = "invoice_type")
    private String invoiceType;

    @ColumnInfo(name = "invoice_email_address")
    private String invoiceEmailAddress;

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

    public String getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(String invoiceType) {
        this.invoiceType = invoiceType;
    }

    public String getInvoiceEmailAddress() {
        return invoiceEmailAddress;
    }

    public void setInvoiceEmailAddress(String invoiceEmailAddress) {
        this.invoiceEmailAddress = invoiceEmailAddress;
    }

    public boolean isActive() {
        return ClientStatusEnum.ACTIVE.name().equals(status);
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
