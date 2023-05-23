package com.example.projet_android;

public class Postuleurs {
    String id,id_user,domaine,adresse,description,CV,carte_nationale,situation,currentDateTime;




    public Postuleurs(String id, String id_user, String domaine, String adresse, String description, String CV, String carte_nationale, String situation, String currentDateTime) {
        this.id = id;
        this.id_user=id_user;
        this.domaine=domaine;
        this.adresse=adresse;
        this.description=description;
        this.CV=CV;
        this.carte_nationale=carte_nationale;
        this.situation=situation;
        this.currentDateTime=currentDateTime;

    }
    public Postuleurs() {
        // Constructeur vide requis pour Firebase.
    }
    public String getCurrentDateTime() {
        return currentDateTime;
    }

    public void setCurrentDateTime(String currentDateTime) {
        this.currentDateTime = currentDateTime;
    }
    public String getId() {
        return id;
    }

    public String getAdresse() {
        return adresse;
    }

    public String getCarte_nationale() {
        return carte_nationale;
    }

    public String getSituation() {
        return situation;
    }

    public String getDomaine() {
        return domaine;
    }

    public String getId_user() {
        return id_user;
    }

    public String getCV() {
        return CV;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public void setDomaine(String domaine) {
        this.domaine = domaine;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSituation(String situation) {
        this.situation = situation;
    }

    public void setCV(String CV) {
        this.CV = CV;
    }

    public void setCarte_nationale(String carte_nationale) {
        this.carte_nationale = carte_nationale;
    }
}
