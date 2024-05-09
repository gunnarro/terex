package com.gunnarro.android.terex.domain.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.TypeConverters;

import com.gunnarro.android.terex.domain.converter.LocalDateTimeConverter;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
@TypeConverters({LocalDateTimeConverter.class})
@Entity(tableName = "invoice_attachment", indices = {@Index(value = {"invoice_id", "attachment_type", "attachment_file_name", "attachment_file_type"},
        unique = true)})
public class InvoiceAttachment extends BaseEntity {
    @NotNull
    @ColumnInfo(name = "invoice_id")
    private Long invoiceId;

    @NotNull
    @ColumnInfo(name = "attachment_file_name")
    private String attachmentFileName;

    @ColumnInfo(name = "attachment_type")
    private String attachmentType;

    @NotNull
    @ColumnInfo(name = "attachment_file_type")
    private String attachmentFileType;

    @NotNull
    @ColumnInfo(name = "attachment_file_content", typeAffinity = ColumnInfo.BLOB)
    private byte[] attachmentFileContent;

    @NotNull
    public Long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(@NotNull Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    @NotNull
    public String getAttachmentFileName() {
        return attachmentFileName;
    }

    public void setAttachmentFileName(@NotNull String attachmentFileName) {
        this.attachmentFileName = attachmentFileName;
    }

    public String getAttachmentType() {
        return attachmentType;
    }

    public void setAttachmentType(String attachmentType) {
        this.attachmentType = attachmentType;
    }

    @NotNull
    public String getAttachmentFileType() {
        return attachmentFileType;
    }

    public void setAttachmentFileType(@NotNull String attachmentFileType) {
        this.attachmentFileType = attachmentFileType;
    }

    @NotNull
    public byte[] getAttachmentFileContent() {
        return attachmentFileContent;
    }

    public void setAttachmentFileContent(@NotNull byte[] attachmentFileContent) {
        this.attachmentFileContent = attachmentFileContent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InvoiceAttachment that = (InvoiceAttachment) o;
        return invoiceId.equals(that.invoiceId) && attachmentFileName.equals(that.attachmentFileName) && attachmentType.equals(that.attachmentType) && attachmentFileType.equals(that.attachmentFileType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(invoiceId, attachmentFileName, attachmentType, attachmentFileType);
    }

    @NonNull
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("InvoiceAttachment{");
        sb.append("id=").append(getId());
        sb.append(", uuid=").append(getUuid());
        sb.append(", invoiceId=").append(invoiceId);
        sb.append(", attachmentFileName='").append(attachmentFileName).append('\'');
        sb.append(", attachmentType='").append(attachmentType).append('\'');
        sb.append(", attachmentFileType='").append(attachmentFileType).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
