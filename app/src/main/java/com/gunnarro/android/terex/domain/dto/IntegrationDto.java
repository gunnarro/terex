package com.gunnarro.android.terex.domain.dto;

import com.gunnarro.android.terex.domain.entity.Integration;

import java.time.LocalDateTime;

public class IntegrationDto {

    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private Long id;
    private String status;
    private String system;
    private String integrationType;
    private String baseUrl;
    private String schemaUrl;
    private String authenticationType;
    private String userName;
    private String password;
    private String accessToken;
    private Long readTimeoutMs;
    private Long connectionTimeoutMs;
    private String httpHeaderContentType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getIntegrationType() {
        return integrationType;
    }

    public void setIntegrationType(String integrationType) {
        this.integrationType = integrationType;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
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

    public Long getReadTimeoutMs() {
        return readTimeoutMs;
    }

    public void setReadTimeoutMs(Long readTimeoutMs) {
        this.readTimeoutMs = readTimeoutMs;
    }

    public Long getConnectionTimeoutMs() {
        return connectionTimeoutMs;
    }

    public void setConnectionTimeoutMs(Long connectionTimeoutMs) {
        this.connectionTimeoutMs = connectionTimeoutMs;
    }

    public String getHttpHeaderContentType() {
        return httpHeaderContentType;
    }

    public void setHttpHeaderContentType(String httpHeaderContentType) {
        this.httpHeaderContentType = httpHeaderContentType;
    }

    public boolean isActive() {
        return Integration.IntegrationStatusEnum.valueOf(status).equals(Integration.IntegrationStatusEnum.ACTIVE);
    }

    public boolean isDeActivated() {
        return Integration.IntegrationStatusEnum.valueOf(status).equals(Integration.IntegrationStatusEnum.DEACTIVATED);
    }
}
