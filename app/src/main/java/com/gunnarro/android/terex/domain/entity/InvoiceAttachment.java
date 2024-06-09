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
@Entity(tableName = "invoice_attachment", indices = {@Index(value = {"invoice_id", "type", "file_name", "file_type"},
        unique = true)})
public class InvoiceAttachment extends BaseEntity {
    @NotNull
    @ColumnInfo(name = "invoice_id")
    private Long invoiceId;

    @NotNull
    @ColumnInfo(name = "file_name")
    private String fileName;

    @ColumnInfo(name = "type")
    private String type;

    @NotNull
    @ColumnInfo(name = "file_type")
    private String fileType;

    @NotNull
    @ColumnInfo(name = "file_content", typeAffinity = ColumnInfo.BLOB)
    private byte[] fileContent;

    public Long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public byte[] getFileContent() {
        return fileContent;
    }

    public void setFileContent(byte[] fileContent) {
        this.fileContent = fileContent;
    }
}
