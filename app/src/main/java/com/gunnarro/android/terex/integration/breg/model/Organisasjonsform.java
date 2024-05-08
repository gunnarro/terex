
package com.gunnarro.android.terex.integration.breg.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Organisasjonsform {

    @SerializedName("kode")
    @Expose
    private String kode;
    @SerializedName("beskrivelse")
    @Expose
    private String beskrivelse;

    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    public String getBeskrivelse() {
        return beskrivelse;
    }

    public void setBeskrivelse(String beskrivelse) {
        this.beskrivelse = beskrivelse;
    }

}
