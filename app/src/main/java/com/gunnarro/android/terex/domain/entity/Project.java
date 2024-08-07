package com.gunnarro.android.terex.domain.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.TypeConverters;

import com.gunnarro.android.terex.domain.converter.LocalDateConverter;
import com.gunnarro.android.terex.domain.converter.LocalDateTimeConverter;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import lombok.NoArgsConstructor;

@NoArgsConstructor
@TypeConverters({LocalDateConverter.class, LocalDateTimeConverter.class})
@Entity(tableName = "project", indices = {@Index(value = {"client_id", "name"},
        unique = true)})
public class Project extends BaseEntity {

    public enum ProjectStatusEnum {
        ACTIVE, CLOSED
    }

    /**
     * The company that have hired the consultant
     */
    @NotNull
    @ColumnInfo(name = "client_id")
    private Long clientId;

    @NotNull
    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "description")
    private String description;

    @NotNull
    @ColumnInfo(name = "status")
    private String status;

    @ColumnInfo(name = "hourly_rate")
    private Integer hourlyRate;

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

    public Integer getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(Integer hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public boolean isActive() {
        return ProjectStatusEnum.ACTIVE.name().equals(status);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return Objects.equals(clientId, project.clientId) && Objects.equals(name, project.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientId, name);
    }
}
