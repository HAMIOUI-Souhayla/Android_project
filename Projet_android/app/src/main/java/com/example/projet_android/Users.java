package com.example.projet_android;

public class Users {
    String email,nom,prénom,id,photoDeProfil,adresse;




    public Users(String email, String nom, String prénom, String id, String photoDeProfil, String adresse) {
        this.email = email;
        this.nom = nom;
        this.prénom = prénom;
        this.id = id;
        this.photoDeProfil=photoDeProfil;
        this.adresse=adresse;

    }

    public String getPhotoDeProfil() {
        return photoDeProfil;
    }

    public void setPhotoDeProfil(String photoDeProfil) {
        this.photoDeProfil = photoDeProfil;
    }

    public Users() {
        // Constructeur vide requis pour Firebase.
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrénom() {
        return prénom;
    }

    public void setPrénom(String prénom) {
        this.prénom = prénom;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }
}