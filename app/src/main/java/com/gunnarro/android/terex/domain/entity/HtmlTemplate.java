package com.gunnarro.android.terex.domain.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.gunnarro.android.terex.domain.converter.LocalDateConverter;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
@TypeConverters(LocalDateConverter.class)
@Entity(tableName = "html_template")
public class HtmlTemplate {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "template_name")
    private String templateName;

    @ColumnInfo(name = "template_vendor")
    private String templateVendor;

    @ColumnInfo(name = "template_html")
    private String templateHtml;
}
