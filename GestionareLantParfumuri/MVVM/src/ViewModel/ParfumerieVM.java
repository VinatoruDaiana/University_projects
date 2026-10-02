package ViewModel;

import Model.Parfumerie;
import Model.Repository.ParfumerieRepository;
import ViewModel.Commands.ParfumerieCommands;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ParfumerieVM {
    private final ParfumerieRepository repo;

    public final StringProperty nume = new SimpleStringProperty();
    public final StringProperty adresa = new SimpleStringProperty();
    public final StringProperty telefon = new SimpleStringProperty();
    public final StringProperty mesaj = new SimpleStringProperty();
    public final StringProperty selectedParfumerieId = new SimpleStringProperty();
    public final StringProperty cautareNume = new SimpleStringProperty();

    public final ObservableList<List<String>> parfumerii = FXCollections.observableArrayList();

    public final ParfumerieCommands addCommand;
    public final ParfumerieCommands updateCommand;
    public final ParfumerieCommands deleteCommand;
    public final ParfumerieCommands searchCommand;
    public final ParfumerieCommands afiseazaCommand;

    public ParfumerieVM() {
        this.repo = new ParfumerieRepository();

        this.addCommand = new ParfumerieCommands(this::addParfumerie, this::isValid);
        this.updateCommand = new ParfumerieCommands(this::updateParfumerie, () -> isValid() && hasSelection());
        this.deleteCommand = new ParfumerieCommands(this::deleteParfumerie, this::hasSelection);
        this.searchCommand = new ParfumerieCommands(this::searchParfumerie, () -> !cautareNume.get().isBlank());
        this.afiseazaCommand = new ParfumerieCommands(this::loadParfumerii, () -> true);

        loadParfumerii();
    }

    public void loadParfumerii() {
        parfumerii.setAll(repo.getAll().stream()
                .map(p -> List.of(
                        String.valueOf(p.getIdParfumerie()),
                        p.getNume(),
                        p.getAdresa(),
                        p.getTelefon()))
                .collect(Collectors.toList()));
    }

    public void addParfumerie() {
        if (isValid()) {
            Parfumerie p = new Parfumerie(0, nume.get(), adresa.get(), telefon.get());
            int id = repo.addParfumerie(p);
            if (id > 0) {
                mesaj.set("Parfumerie adăugată cu ID: " + id);
                clear();
                loadParfumerii();
            } else {
                mesaj.set("Eroare la adăugare.");
            }
        }
    }

    public void updateParfumerie() {
        if (isValid() && hasSelection()) {
            int id = Integer.parseInt(selectedParfumerieId.get());
            Parfumerie p = new Parfumerie(id, nume.get(), adresa.get(), telefon.get());
            repo.updateParfumerie(p);
            mesaj.set("Parfumerie actualizată cu succes.");
            clear();
            loadParfumerii();
        }
    }

    public void deleteParfumerie() {
        if (hasSelection()) {
            int id = Integer.parseInt(selectedParfumerieId.get());
            repo.deleteParfumerie(id);
            mesaj.set("Parfumerie ștearsă cu succes.");
            clear();
            loadParfumerii();
        }
    }

    public void searchParfumerie() {
        Optional<Parfumerie> result = repo.getAll().stream()
                .filter(p -> p.getNume().equalsIgnoreCase(cautareNume.get().trim()))
                .findFirst();

        if (result.isPresent()) {
            Parfumerie p = result.get();
            selectedParfumerieId.set(String.valueOf(p.getIdParfumerie()));
            nume.set(p.getNume());
            adresa.set(p.getAdresa());
            telefon.set(p.getTelefon());
            mesaj.set("Parfumerie găsită.");
        } else {
            mesaj.set("Parfumerie negăsită.");
        }
    }

    public void selectParfumerie(List<String> data) {
        if (data == null || data.size() < 4) return;
        selectedParfumerieId.set(data.get(0));
        nume.set(data.get(1));
        adresa.set(data.get(2));
        telefon.set(data.get(3));
    }

    private boolean isValid() {
        return !nume.get().isBlank() && !adresa.get().isBlank() && !telefon.get().isBlank();
    }

    private boolean hasSelection() {
        return selectedParfumerieId.get() != null && !selectedParfumerieId.get().isBlank();
    }

    private void clear() {
        selectedParfumerieId.set("");
        nume.set("");
        adresa.set("");
        telefon.set("");
    }
}