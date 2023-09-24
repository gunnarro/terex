package com.gunnarro.android.terex.domain;

import com.gunnarro.android.terex.domain.entity.Address;
import com.gunnarro.android.terex.domain.entity.Company;
import com.gunnarro.android.terex.domain.entity.Contact;
import com.gunnarro.android.terex.domain.entity.Person;

public class CompanyTest {

    public void createCompany() {
        Company company = new Company();
        company.setName("Norway Consulting AS");
        company.setOrganizationNumber("");
        Address address = new Address();
        address.setStreetNumber("16");
        address.setStreetName("Grensen");
        address.setPostCode("0159");
        address.setCity("Oslo");
        address.setCountryCode("no");
        company.setBusinessAddress(address);
        Person person = new Person();

        Contact contactInfo = new Contact();
        company.setContactInfo(contactInfo);

    }
}
