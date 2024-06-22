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
@Entity(tableName = "integration", indices = {@Index(value = {"system"},
        unique = true)})
public class Integration extends BaseEntity {

    @NonNull
    @ColumnInfo(name = "system")
    private String system;
    @ColumnInfo(name = "service_type")
    private String serviceType;
    @NonNull
    @ColumnInfo(name = "base_url")
    private String baseUrl;
    @ColumnInfo(name = "schema_url")
    private String schemaUrl;
    @ColumnInfo(name = "authentication_type")
    private String authenticationType;
    @ColumnInfo(name = "user_name")
    private String userName;
    @ColumnInfo(name = "password")
    private String password;
    @ColumnInfo(name = "access_token")
    private String accessToken;
    @NonNull
    @ColumnInfo(name = "read_timeout_ms")
    private Long readTimeoutMs;
    @NonNull
    @ColumnInfo(name = "connection_timeout_ms")
    private Long connectionTimeoutMs;
    @ColumnInfo(name = "http_headers_content_type")
    private String httpHeaderContentType;


    @NonNull
    public String getSystem() {
        return system;
    }

    public void setSystem(@NonNull String system) {
        this.system = system;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    @NonNull
    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(@NonNull String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getSchemaUrl() {
        return schemaUrl;
    }

    public void setSchemaUrl(String schemaUrl) {
        this.schemaUrl = schemaUrl;
    }

    public String getAuthenticationType() {
        return authenticationType;
    }

    public void setAuthenticationType(String authenticationType) {
        this.authenticationType = authenticationType;
    }

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

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @NonNull
    public Long getReadTimeoutMs() {
        return readTimeoutMs;
    }

    public void setReadTimeoutMs(@NonNull Long readTimeoutMs) {
        this.readTimeoutMs = readTimeoutMs;
    }

    @NonNull
    public Long getConnectionTimeoutMs() {
        return connectionTimeoutMs;
    }

    public void setConnectionTimeoutMs(@NonNull Long connectionTimeoutMs) {
        this.connectionTimeoutMs = connectionTimeoutMs;
    }

    public String getHttpHeaderContentType() {
        return httpHeaderContentType;
    }

    public void setHttpHeaderContentType(String httpHeaderContentType) {
        this.httpHeaderContentType = httpHeaderContentType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Integration that = (Integration) o;
        return Objects.equals(system, that.system);
    }

    @Override
    public int hashCode() {
        return Objects.hash(system);
    }
}
