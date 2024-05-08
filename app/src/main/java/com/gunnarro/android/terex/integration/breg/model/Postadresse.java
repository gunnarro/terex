
package com.gunnarro.android.terex.integration.breg.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class Postadresse {

    @SerializedName("land")
    @Expose
    private String land;
    @SerializedName("landkode")
    @Expose
    private String landkode;
    @SerializedName("postnummer")
    @Expose
    private String postnummer;
    @SerializedName("poststed")
    @Expose
    private String poststed;
    @SerializedName("adresse")
    @Expose
    private List<String> adresse;
    @SerializedName("kommune")
    @Expose
    private String kommune;
    @SerializedName("kommunenummer")
    @Expose
    private String kommunenummer;

    public String getLand() {
        return land;
    }

    public void setLand(String land) {
        this.land = land;
    }

    public String getLandkode() {
        return landkode;
    }

    public void setLandkode(String landkode) {
        this.landkode = landkode;
    }

    public String getPostnummer() {
        return postnummer;
    }

    public void setPostnummer(String postnummer) {
        this.postnummer = postnummer;
    }

    public String getPoststed() {
        return poststed;
    }

    public void setPoststed(String poststed) {
        this.poststed = poststed;
    }

    public List<String> getAdresse() {
        return adresse;
    }

    public void setAdresse(List<String> adresse) {
        this.adresse = adresse;
    }

    public String getKommune() {
        return kommune;
    }

    public void setKommune(String kommune) {
        this.kommune = kommune;
    }

    public String getKommunenummer() {
        return kommunenummer;
    }

    public void setKommunenummer(String kommunenummer) {
        this.kommunenummer = kommunenummer;
    }

}
