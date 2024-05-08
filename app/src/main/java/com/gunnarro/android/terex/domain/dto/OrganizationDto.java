package com.gunnarro.android.terex.domain.dto;

public class OrganizationDto {

    private Long id;
    private String name;
    private String organizationNumber;
    private String bankAccountNumber;
    private String industryType;
    private OrganizationStatusDto organizationStatusDto;
    private BusinessAddressDto businessAddress;
    private PostalAddressDto postalAddressDto;
    private PersonDto contactPerson;
    private ContactInfoDto contactInfo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrganizationNumber() {
        return organizationNumber;
    }

    public void setOrganizationNumber(String organizationNumber) {
        this.organizationNumber = organizationNumber;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

    public String getIndustryType() {
        return industryType;
    }

    public void setIndustryType(String industryType) {
        this.industryType = industryType;
    }

    public OrganizationStatusDto getOrganizationStatusDto() {
        return organizationStatusDto;
    }

    public void setOrganizationStatusDto(OrganizationStatusDto organizationStatusDto) {
        this.organizationStatusDto = organizationStatusDto;
    }

    public BusinessAddressDto getBusinessAddress() {
        return businessAddress;
    }

    public void setBusinessAddress(BusinessAddressDto businessAddress) {
        this.businessAddress = businessAddress;
    }

    public PostalAddressDto getPostalAddressDto() {
        return postalAddressDto;
    }

    public void setPostalAddressDto(PostalAddressDto postalAddressDto) {
        this.postalAddressDto = postalAddressDto;
    }

    public PersonDto getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(PersonDto contactPerson) {
        this.contactPerson = contactPerson;
    }

    public ContactInfoDto getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(ContactInfoDto contactInfo) {
        this.contactInfo = contactInfo;
    }
}
