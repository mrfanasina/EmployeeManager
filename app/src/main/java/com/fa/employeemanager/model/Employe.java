package com.fa.employeemanager.model;

public class Employe {

    private int numEmp;
    private String nom;
    private double salaire;

    public Employe(int numEmp, String nom, double salaire) {
        this.numEmp = numEmp;
        this.nom = nom;
        this.salaire = salaire;
    }

    public int getNumEmp() {
        return numEmp;
    }

    public void setNumEmp(int numEmp) {
        this.numEmp = numEmp;
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