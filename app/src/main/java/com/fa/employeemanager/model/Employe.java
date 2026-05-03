package com.fa.employeemanager.model;

public class Employe {

    private int numemp;
    private String nom;
    private double salaire;

    public Employe(int numemp, String nom, double salaire) {
        this.numemp = numemp;
        this.nom = nom;
        this.salaire = salaire;
    }

    public int getNumemp() {
        return numemp;
    }

    public void setNumemp(int numemp) {
        this.numemp = numemp;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public double getSalaire() {
        return salaire;
    }

    public void setSalaire(double salaire) {
        this.salaire = salaire;
    }

    public String getObservation() {
        if (salaire < 1000) {
            return "Médiocre";
        } else if (salaire <= 5000) {
            return "Moyen";
        } else {
            return "Grand";
        }
    }
}