package com.example.medii_admitere_app.classes;

import java.io.Serializable;

public class Specializare implements Serializable {
    private String numeID;
    private String nume;
    private double ultimaMedieBuget;
    private double ultimaMedieTaxa;
    private double longitudine;
    private double latitudine;
    private String documentPath;

    public Specializare(){

    }
    public Specializare(String numeID, String nume, double ultimaMedieBuget, double ultimaMedieTaxa) {
        this.numeID =numeID;
        this.nume = nume;
        this.ultimaMedieBuget = ultimaMedieBuget;
        this.ultimaMedieTaxa = ultimaMedieTaxa;
    }
    public Specializare(String numeID, String nume, double ultimaMedieBuget, double ultimaMedieTaxa, String documentPath) {
        this.numeID =numeID;
        this.nume = nume;
        this.ultimaMedieBuget = ultimaMedieBuget;
        this.ultimaMedieTaxa = ultimaMedieTaxa;
        this.documentPath = documentPath;
    }

    public Specializare(String nume, double ultimaMedieBuget, double ultimaMedieTaxa, String documentPath) {
        this.nume = nume;
        this.ultimaMedieBuget = ultimaMedieBuget;
        this.ultimaMedieTaxa = ultimaMedieTaxa;
        this.documentPath = documentPath;
    }
    public Specializare(String numeID, String nume, double ultimaMedieBuget, double ultimaMedieTaxa, double longitudine, double latitudine) {
        this.numeID =numeID;
        this.nume = nume;
        this.ultimaMedieBuget = ultimaMedieBuget;
        this.ultimaMedieTaxa = ultimaMedieTaxa;
        this.longitudine = longitudine;
        this.latitudine = latitudine;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public double getUltimaMedieBuget() {
        return ultimaMedieBuget;
    }

    public void setUltimaMedieBuget(double ultimaMedieBuget) {
        this.ultimaMedieBuget = ultimaMedieBuget;
    }

    public double getUltimaMedieTaxa() {
        return ultimaMedieTaxa;
    }

    public void setUltimaMedieTaxa(double ultimaMedieTaxa) {
        this.ultimaMedieTaxa = ultimaMedieTaxa;
    }

    public String getNumeID() {
        return numeID;
    }

    public void setNumeID(String numeID) {
        this.numeID = numeID;
    }

    public String getDocumentPath() {
        return documentPath;
    }

    public void setDocumentPath(String documentPath) {
        this.documentPath = documentPath;
    }

    public double getLongitudine() {
        return longitudine;
    }

    public void setLongitudine(double longitudine) {
        this.longitudine = longitudine;
    }

    public double getLatitudine() {
        return latitudine;
    }

    public void setLatitudine(double latitudine) {
        this.latitudine = latitudine;
    }

    @Override
    public String toString() {
        return "Profil{" +
                "nume='" + nume + '\'' +
                ", ultimaMedieBuget=" + ultimaMedieBuget +
                ", ultimaMedieTaxa=" + ultimaMedieTaxa +
                '}';
    }

}
