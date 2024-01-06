package com.gunnarro.android.terex.domain.dto;

public class CompanyDto {

    private Long id;
    private String name;
    private String organizationNumber;
    private String bankAccountNumber;
    private String companyIndustryType;

    private AddressDto address;

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

    public String getCompanyIndustryType() {
        return companyIndustryType;
    }

    public void setCompanyIndustryType(String companyIndustryType) {
        this.companyIndustryType = companyIndustryType;
    }

    public AddressDto getAddress() {
        return address;
    }

    public void setAddress(AddressDto address) {
        this.address = address;
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
