package com.gunnarro.android.terex.integration.breg.model;

import com.gunnarro.android.terex.domain.dto.BusinessAddressDto;
import com.gunnarro.android.terex.domain.dto.OrganizationDto;
import com.gunnarro.android.terex.domain.dto.OrganizationStatusDto;

import java.util.StringJoiner;

public class BregMapper {

    private BregMapper() {
    }

    public static OrganizationDto toOrganizationDto(Enhet enhet) {
        if (enhet == null || enhet.getOrganisasjonsnummer() == null) {
            return null;
        }
        OrganizationDto organizationDto = new OrganizationDto();
        organizationDto.setOrganizationNumber(enhet.getOrganisasjonsnummer());
        organizationDto.setName(enhet.getNavn());
        OrganizationStatusDto organizationStatusDto = new OrganizationStatusDto();
        organizationStatusDto.setKonkurs(enhet.getKonkurs());
        organizationStatusDto.setRegistrertIForetaksregisteret(enhet.getRegistrertIForetaksregisteret());
        organizationStatusDto.setUnderAvvikling(enhet.getUnderAvvikling());
        organizationStatusDto.setSisteInnsendteAarsregnskap(null);
        organizationStatusDto.setRegistrertIFrivillighetsregisteret(enhet.getRegistrertIFrivillighetsregisteret());
        organizationStatusDto.setUnderTvangsavviklingEllerTvangsopplosning(enhet.getUnderTvangsavviklingEllerTvangsopplosning());
        organizationStatusDto.setRegistrertIStiftelsesregisteret(enhet.getRegistrertIStiftelsesregisteret());
        organizationDto.setOrganizationStatusDto(organizationStatusDto);

        BusinessAddressDto businessAddressDto = new BusinessAddressDto();

        StringJoiner address = new StringJoiner(",");
        enhet.getForretningsadresse().getAdresse().forEach(address::add);
        businessAddressDto.setAddress(address.toString());

        businessAddressDto.setCity(enhet.getForretningsadresse().getKommune());
        businessAddressDto.setPostalCode(enhet.getForretningsadresse().getPostnummer());
        businessAddressDto.setCountryCode(enhet.getForretningsadresse().getLandkode());
        businessAddressDto.setCountry(enhet.getForretningsadresse().getLand());
        organizationDto.setBusinessAddress(businessAddressDto);
        return organizationDto;
    }
}
