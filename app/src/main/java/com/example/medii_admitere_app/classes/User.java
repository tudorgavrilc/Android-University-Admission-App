package com.example.medii_admitere_app.classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    private String prenume;
    private String nume;
    private String descriere;
    private String pozaProfilUrl;
    private List<String> favorites = new ArrayList<>();

    public User() {}
    public User(String nume, String prenume, String descriere) {
        this.prenume = prenume;
        this.nume = nume;
        this.descriere = descriere;
    }

    public User(String nume, String prenume, String descriere,List<String> favorites) {
        this.prenume = prenume;
        this.nume = nume;
        this.descriere = descriere;
        this.favorites = favorites;
    }

    public String getPrenume() {
        return prenume;
    }

    public String getNume() {
        return nume;
    }
    public String getDescriere() {
        return descriere;
    }
    public String getPozaProfilUrl() { return pozaProfilUrl; }
    public void setPozaProfilUrl(String pozaProfilUrl) { this.pozaProfilUrl = pozaProfilUrl; }

    public List<String> getFavorites() {
        return favorites;
    }

    public void setFavorites(List<String> favorites) {
        this.favorites = favorites;
    }
}
