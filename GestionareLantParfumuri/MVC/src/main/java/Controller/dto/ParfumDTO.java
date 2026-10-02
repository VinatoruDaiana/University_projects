package Controller.dto;

import Model.Parfum;



public class ParfumDTO {
    private final int id_parfum;
    private final String nume;
    private final String producator;
    private final String descriere;


    public ParfumDTO(int id_parfum, String nume, String producator, String descriere) {
        this.id_parfum = id_parfum;
        this.nume = nume;
        this.producator = producator;
        this.descriere = descriere;

    }

    public int getParfum_id() {
        return id_parfum;
    }

    public String getNume() {
        return nume;
    }

    public String getProducator() {
        return producator;
    }

    public String getDescriere() {
        return descriere;
    }



    public Parfum toEntity() {
        Parfum parfum = new Parfum();
        parfum.setId_parfum(id_parfum);
        parfum.setNume(nume);
        parfum.setProducator(producator);
        parfum.setDescriere(descriere);
        return parfum;
    }

    @Override
    public String toString() {
        return "ParfumDTO{" +
                "id_parfum=" + id_parfum +
                ", nume='" + nume + '\'' +
                ", producator='" + producator + '\'' +
                ", descriere='" + descriere + '\'' +
                '}';
    }
}
