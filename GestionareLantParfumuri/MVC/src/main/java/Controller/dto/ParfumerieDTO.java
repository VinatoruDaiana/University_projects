package Controller.dto;

import Model.Parfumerie;

public class ParfumerieDTO {
    private final int id_parfumerie;
    private final String nume;
    private final String adresa;
    private final String telefon;

    public ParfumerieDTO(int id_parfumerie, String nume, String adresa, String telefon) {
        this.id_parfumerie = id_parfumerie;
        this.nume = nume;
        this.adresa = adresa;
        this.telefon = telefon;
    }

    public int getParfumerie_id() {
        return id_parfumerie;
    }

    public String getNume() {
        return nume;
    }

    public String getAdresa() {
        return adresa;
    }

    public String getTelefon() {
        return telefon;
    }

    public Parfumerie toEntity() {
        Parfumerie parfumerie = new Parfumerie();
        parfumerie.setId_parfumerie(id_parfumerie);
        parfumerie.setNume(nume);
        parfumerie.setAdresa(adresa);
        parfumerie.setTelefon(telefon);
        return parfumerie;
    }

    @Override
    public String toString() {
        return "ParfumerieDTO{" +
                "id_parfumerie=" + id_parfumerie +
                ", nume='" + nume + '\'' +
                ", adresa='" + adresa + '\'' +
                ", telefon='" + telefon + '\'' +
                '}';
    }
}