package com.gunnarro.android.terex.domain.entity;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.gunnarro.android.terex.domain.entity.Address;
import com.gunnarro.android.terex.domain.entity.Company;
import com.gunnarro.android.terex.domain.entity.ContactInfo;
import com.gunnarro.android.terex.domain.entity.Person;

import org.junit.jupiter.api.Test;


class CompanyTest {

    @Test
    void createCompany() {
        Company company = new Company();
        company.setName("Norway Consulting AS");
        company.setOrganizationNumber("");
        Address address = new Address();
        address.setStreetNumber("16");
        address.setStreetName("Grensen");
        address.setPostCode("0159");
        address.setCity("Oslo");
        address.setCountryCode("no");
        /*
        company.setBusinessAddress(address);
        Person person = new Person();
        company.setContactPerson(person);

        ContactInfo contactInfo = new ContactInfo();
        company.setContactInfo(contactInfo);

        assertNotNull(company.getContactInfo());
        assertNotNull(company.getContactPerson());

         */
    }
}
