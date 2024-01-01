package com.gunnarro.android.terex.domain.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.gunnarro.android.terex.domain.converter.LocalDateConverter;
import com.gunnarro.android.terex.domain.converter.LocalDateTimeConverter;

import org.jetbrains.annotations.NotNull;

import lombok.NoArgsConstructor;

@NoArgsConstructor
@TypeConverters({LocalDateConverter.class, LocalDateTimeConverter.class})
@Entity(tableName = "project")
public class Project extends BaseEntity {

    public enum ProjectStatusEnum {
        ACTIVE, COMPLETED, TERMINATED
    }

    @PrimaryKey(autoGenerate = true)
    private Long id;

    /**
     * The company that is the contact point between the consultant and the client
     * <p>
     * This id will be the same as user id.
     */
    @ColumnInfo(name = "consultant_broker_id")
    private Long consultantBrokerId;

    /**
     * The consultant that is hired
     */
    @ColumnInfo(name = "consultant_id")
    private Long consultantId;

    /**
     * The company that have hired the consultant
     */
    @ColumnInfo(name = "client_id")
    private Long clientId;

    @NotNull
    @ColumnInfo(name = "project_name")
    private String name;

    @ColumnInfo(name = "project_description")
    private String description;

    @ColumnInfo(name = "project_status")
    private String status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getConsultantBrokerId() {
        return consultantBrokerId;
    }

    public void setConsultantBrokerId(Long consultantBrokerId) {
        this.consultantBrokerId = consultantBrokerId;
    }

    public Long getConsultantId() {
        return consultantId;
    }

    public void setConsultantId(Long consultantId) {
        this.consultantId = consultantId;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }


    @NotNull
    public String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
