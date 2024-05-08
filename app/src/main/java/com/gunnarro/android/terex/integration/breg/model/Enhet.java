
package com.gunnarro.android.terex.integration.breg.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Enhet {

    @SerializedName("organisasjonsnummer")
    @Expose
    private String organisasjonsnummer;
    @SerializedName("navn")
    @Expose
    private String navn;
    @SerializedName("organisasjonsform")
    @Expose
    private Organisasjonsform organisasjonsform;
    @SerializedName("postadresse")
    @Expose
    private Postadresse postadresse;
    @SerializedName("registreringsdatoEnhetsregisteret")
    @Expose
    private String registreringsdatoEnhetsregisteret;
    @SerializedName("registrertIMvaregisteret")
    @Expose
    private Boolean registrertIMvaregisteret;
    @SerializedName("naeringskode1")
    @Expose
    private Naeringskode1 naeringskode1;
    @SerializedName("antallAnsatte")
    @Expose
    private Integer antallAnsatte;
    @SerializedName("forretningsadresse")
    @Expose
    private Forretningsadresse forretningsadresse;
    @SerializedName("stiftelsesdato")
    @Expose
    private String stiftelsesdato;
    @SerializedName("registrertIForetaksregisteret")
    @Expose
    private Boolean registrertIForetaksregisteret;
    @SerializedName("registrertIStiftelsesregisteret")
    @Expose
    private Boolean registrertIStiftelsesregisteret;
    @SerializedName("registrertIFrivillighetsregisteret")
    @Expose
    private Boolean registrertIFrivillighetsregisteret;
    @SerializedName("konkurs")
    @Expose
    private Boolean konkurs;
    @SerializedName("underAvvikling")
    @Expose
    private Boolean underAvvikling;
    @SerializedName("underTvangsavviklingEllerTvangsopplosning")
    @Expose
    private Boolean underTvangsavviklingEllerTvangsopplosning;
    @SerializedName("maalform")
    @Expose
    private String maalform;

    public String getOrganisasjonsnummer() {
        return organisasjonsnummer;
    }

    public void setOrganisasjonsnummer(String organisasjonsnummer) {
        this.organisasjonsnummer = organisasjonsnummer;
    }

    public String getNavn() {
        return navn;
    }

    public void setNavn(String navn) {
        this.navn = navn;
    }

    public Organisasjonsform getOrganisasjonsform() {
        return organisasjonsform;
    }

    public void setOrganisasjonsform(Organisasjonsform organisasjonsform) {
        this.organisasjonsform = organisasjonsform;
    }

    public Postadresse getPostadresse() {
        return postadresse;
    }

    public void setPostadresse(Postadresse postadresse) {
        this.postadresse = postadresse;
    }

    public String getRegistreringsdatoEnhetsregisteret() {
        return registreringsdatoEnhetsregisteret;
    }

    public void setRegistreringsdatoEnhetsregisteret(String registreringsdatoEnhetsregisteret) {
        this.registreringsdatoEnhetsregisteret = registreringsdatoEnhetsregisteret;
    }

    public Boolean getRegistrertIMvaregisteret() {
        return registrertIMvaregisteret;
    }

    public void setRegistrertIMvaregisteret(Boolean registrertIMvaregisteret) {
        this.registrertIMvaregisteret = registrertIMvaregisteret;
    }

    public Naeringskode1 getNaeringskode1() {
        return naeringskode1;
    }

    public void setNaeringskode1(Naeringskode1 naeringskode1) {
        this.naeringskode1 = naeringskode1;
    }

    public Integer getAntallAnsatte() {
        return antallAnsatte;
    }

    public void setAntallAnsatte(Integer antallAnsatte) {
        this.antallAnsatte = antallAnsatte;
    }

    public Forretningsadresse getForretningsadresse() {
        return forretningsadresse;
    }

    public void setForretningsadresse(Forretningsadresse forretningsadresse) {
        this.forretningsadresse = forretningsadresse;
    }

    public String getStiftelsesdato() {
        return stiftelsesdato;
    }

    public void setStiftelsesdato(String stiftelsesdato) {
        this.stiftelsesdato = stiftelsesdato;
    }

    public Boolean getRegistrertIForetaksregisteret() {
        return registrertIForetaksregisteret;
    }

    public void setRegistrertIForetaksregisteret(Boolean registrertIForetaksregisteret) {
        this.registrertIForetaksregisteret = registrertIForetaksregisteret;
    }

    public Boolean getRegistrertIStiftelsesregisteret() {
        return registrertIStiftelsesregisteret;
    }

    public void setRegistrertIStiftelsesregisteret(Boolean registrertIStiftelsesregisteret) {
        this.registrertIStiftelsesregisteret = registrertIStiftelsesregisteret;
    }

    public Boolean getRegistrertIFrivillighetsregisteret() {
        return registrertIFrivillighetsregisteret;
    }

    public void setRegistrertIFrivillighetsregisteret(Boolean registrertIFrivillighetsregisteret) {
        this.registrertIFrivillighetsregisteret = registrertIFrivillighetsregisteret;
    }

    public Boolean getKonkurs() {
        return konkurs;
    }

    public void setKonkurs(Boolean konkurs) {
        this.konkurs = konkurs;
    }

    public Boolean getUnderAvvikling() {
        return underAvvikling;
    }

    public void setUnderAvvikling(Boolean underAvvikling) {
        this.underAvvikling = underAvvikling;
    }

    public Boolean getUnderTvangsavviklingEllerTvangsopplosning() {
        return underTvangsavviklingEllerTvangsopplosning;
    }

    public void setUnderTvangsavviklingEllerTvangsopplosning(Boolean underTvangsavviklingEllerTvangsopplosning) {
        this.underTvangsavviklingEllerTvangsopplosning = underTvangsavviklingEllerTvangsopplosning;
    }

    public String getMaalform() {
        return maalform;
    }

    public void setMaalform(String maalform) {
        this.maalform = maalform;
    }


}
