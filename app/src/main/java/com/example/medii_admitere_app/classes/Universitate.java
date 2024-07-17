package com.example.medii_admitere_app.classes;

import java.io.Serializable;
import java.util.ArrayList;

public class Universitate implements Serializable {
    private String nume;
    private String adresa;
    private String email;
    private String telefon;
    private String website;
    private String logo;
    private String acronim;

    public Universitate(String nume, String adresa, String email, String telefon, String website, String logo, String acronim) {
        this.nume = nume;
        this.adresa = adresa;
        this.email = email;
        this.telefon = telefon;
        this.website = website;
        this.logo = logo;
        this.acronim = acronim;
    }

    public String getAcronim() {return acronim;}
    public String getLogo() {
        return logo;
    }
    public String getNume() {
        return nume;
    }

    public String getAdresa() {
        return adresa;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefon() {
        return telefon;
    }

    public String getWebsite() {
        return website;
    }

    @Override
    public String toString() {
        return "Universitate{" +
                "nume='" + nume + '\'' +
                ", adresa='" + adresa + '\'' +
                ", email='" + email + '\'' +
                ", telefon='" + telefon + '\'' +
                ", website='" + website + '\'' +
                ", logo='" + logo + '\'' +
                ", acronim='" + acronim + '\'' +
                '}';
    }
}
