package com.gunnarro.android.terex.domain.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
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
@Entity(tableName = "invoice_file", indices = {@Index(value = {"invoice_file_name", "invoice_file_type"},
        unique = true)})
public class InvoiceFile extends BaseEntity {
    @PrimaryKey(autoGenerate = true)
    private Long id;

    @NotNull
    @ColumnInfo(name = "invoice_file_name")
    private String invoiceFileName;

    @NotNull
    @ColumnInfo(name = "invoice_file_type")
    private String invoiceFileType;

    @NotNull
    @ColumnInfo(name = "invoice_file_content", typeAffinity = ColumnInfo.BLOB)
    private byte[] invoiceFileContent;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NotNull
    public String getInvoiceFileName() {
        return invoiceFileName;
    }

    public void setInvoiceFileName(@NotNull String invoiceFileName) {
        this.invoiceFileName = invoiceFileName;
    }

    @NotNull
    public String getInvoiceFileType() {
        return invoiceFileType;
    }

    public void setInvoiceFileType(@NotNull String invoiceFileType) {
        this.invoiceFileType = invoiceFileType;
    }

    @NotNull
    public byte[] getInvoiceFileContent() {
        return invoiceFileContent;
    }

    public void setInvoiceFileContent(@NotNull byte[] invoiceFileContent) {
        this.invoiceFileContent = invoiceFileContent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InvoiceFile that = (InvoiceFile) o;
        return invoiceFileName.equals(that.invoiceFileName) && invoiceFileType.equals(that.invoiceFileType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(invoiceFileName, invoiceFileType);
    }
}
