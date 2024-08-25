package com.gunnarro.android.terex.domain.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.TypeConverters;

import com.gunnarro.android.terex.domain.converter.LocalDateConverter;
import com.gunnarro.android.terex.domain.converter.LocalDateTimeConverter;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.Objects;
import java.util.StringJoiner;

import lombok.NoArgsConstructor;

@NoArgsConstructor
@TypeConverters({LocalDateConverter.class, LocalDateTimeConverter.class})
@Entity(tableName = "project", indices = {@Index(value = {"client_id", "name"},
        unique = true)})
public class Project extends BaseEntity {

    public enum ProjectStatusEnum {
        ACTIVE, CLOSED
    }

    @NotNull
    @ColumnInfo(name = "client_id")
    private Long clientId;

    @NotNull
    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "description")
    private String description;
    
    @ColumnInfo(name = "status")
    private String status;

    @ColumnInfo(name = "hourly_rate")
    private Integer hourlyRate;

    @ColumnInfo(name = "start_date")
    private LocalDate startDate;

    @ColumnInfo(name = "end_date")
    private LocalDate endDate;

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(@NotNull Long clientId) {
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

    public void setStatus(@NotNull String status) {
        this.status = status;
    }

    public Integer getHourlyRate() {
        return hourlyRate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setHourlyRate(Integer hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public boolean isActive() {
        return ProjectStatusEnum.ACTIVE.name().equals(status);
    }

    public boolean isClosed() {
        return ProjectStatusEnum.CLOSED.name().equals(status);
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


    @NonNull
    @Override
    public String toString() {
        return new StringJoiner(", ", Project.class.getSimpleName() + "[", "]")
                .add("createdDate=" + getCreatedDate())
                .add("lastModifiedDate=" + getLastModifiedDate())
                .add("id=" + getId())
                .add("clientId=" + clientId)
                .add("name='" + name + "'")
                .add("status='" + status + "'")
                .toString();
    }
}
