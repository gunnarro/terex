package com.gunnarro.android.terex.domain.dto;

public class OrganizationStatusDto {

    // Registered in the Register of Business Enterprises
    private boolean registrertIForetaksregisteret;
    // registered in the Foundation register
    private boolean registrertIStiftelsesregisteret;

    // registered in the volunteer register
    private boolean registrertIFrivillighetsregisteret;
    // last Submitted annual accounts
    private String sisteInnsendteAarsregnskap;
    // bankrupt
    private boolean konkurs;
    // under Liquidation
    private boolean underAvvikling;
    // under Compulsory Liquidation or Compulsory Dissolution
    private boolean underTvangsavviklingEllerTvangsopplosning;

    public boolean isRegistrertIForetaksregisteret() {
        return registrertIForetaksregisteret;
    }

    public void setRegistrertIForetaksregisteret(boolean registrertIForetaksregisteret) {
        this.registrertIForetaksregisteret = registrertIForetaksregisteret;
    }

    public boolean isRegistrertIStiftelsesregisteret() {
        return registrertIStiftelsesregisteret;
    }

    public void setRegistrertIStiftelsesregisteret(boolean registrertIStiftelsesregisteret) {
        this.registrertIStiftelsesregisteret = registrertIStiftelsesregisteret;
    }

    public boolean isRegistrertIFrivillighetsregisteret() {
        return registrertIFrivillighetsregisteret;
    }

    public void setRegistrertIFrivillighetsregisteret(boolean registrertIFrivillighetsregisteret) {
        this.registrertIFrivillighetsregisteret = registrertIFrivillighetsregisteret;
    }

    public String getSisteInnsendteAarsregnskap() {
        return sisteInnsendteAarsregnskap;
    }

    public void setSisteInnsendteAarsregnskap(String sisteInnsendteAarsregnskap) {
        this.sisteInnsendteAarsregnskap = sisteInnsendteAarsregnskap;
    }

    public boolean isKonkurs() {
        return konkurs;
    }

    public void setKonkurs(boolean konkurs) {
        this.konkurs = konkurs;
    }

    public boolean isUnderAvvikling() {
        return underAvvikling;
    }

    public void setUnderAvvikling(boolean underAvvikling) {
        this.underAvvikling = underAvvikling;
    }

    public boolean isUnderTvangsavviklingEllerTvangsopplosning() {
        return underTvangsavviklingEllerTvangsopplosning;
    }

    public void setUnderTvangsavviklingEllerTvangsopplosning(boolean underTvangsavviklingEllerTvangsopplosning) {
        this.underTvangsavviklingEllerTvangsopplosning = underTvangsavviklingEllerTvangsopplosning;
    }
}
