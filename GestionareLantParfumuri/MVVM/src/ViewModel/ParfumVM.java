package ViewModel;

import Model.Parfum;
import Model.Repository.ParfumRepository;
import ViewModel.Commands.ParfumCommands;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ParfumVM {
    private final ParfumRepository repo;

    public final StringProperty nume = new SimpleStringProperty();
    public final StringProperty producator = new SimpleStringProperty();
    public final StringProperty descriere = new SimpleStringProperty();
    public final StringProperty selectedParfumId = new SimpleStringProperty();
    public final StringProperty mesaj = new SimpleStringProperty();
    public final StringProperty filtrareIdParfumerie = new SimpleStringProperty();
    public final StringProperty filtrareProducator = new SimpleStringProperty();

    public final ObservableList<List<String>> parfumuri = FXCollections.observableArrayList();

    public final ParfumCommands addCommand;
    public final ParfumCommands updateCommand;
    public final ParfumCommands deleteCommand;
    public final ParfumCommands searchCommand;
    public final ParfumCommands filterCommand;

    public final ParfumCommands showImageCommand;
    public ParfumVM() {
        this.repo = new ParfumRepository();

        this.addCommand = new ParfumCommands(this::addParfum, this::isValid);
        this.updateCommand = new ParfumCommands(this::updateParfum, () -> isValid() && hasSelection());
        this.deleteCommand = new ParfumCommands(this::deleteParfum, this::hasSelection);
        this.searchCommand = new ParfumCommands(this::searchParfum, () -> !nume.get().isBlank());
        this.filterCommand = new ParfumCommands(this::filtrareParfumuri, () -> !filtrareIdParfumerie.get().isBlank());
        this.showImageCommand = new ParfumCommands(this::veziImagine, this::hasSelection);

        loadParfumuri();
    }

    public void loadParfumuri() {
        parfumuri.setAll(repo.getAll().stream()
                .map(p -> List.of(
                        String.valueOf(p.getIdParfum()),
                        p.getNume(),
                        p.getProducator(),
                        p.getDescriere()))
                .collect(Collectors.toList()));
    }

    public void addParfum() {
        if (isValid()) {
            repo.addParfum(new Parfum(0, nume.get(), producator.get(), descriere.get()));
            loadParfumuri();
            clear();
            mesaj.set("Parfumul a fost adăugat cu succes!");
        }
    }

    public void updateParfum() {
        if (isValid() && hasSelection()) {
            int id = Integer.parseInt(selectedParfumId.get());
            repo.updateParfum(new Parfum(id, nume.get(), producator.get(), descriere.get()));
            loadParfumuri();
            clear();
            mesaj.set("Parfumul a fost actualizat cu succes!");
        }
    }

    public void deleteParfum() {
        if (hasSelection()) {
            repo.deleteParfum(Integer.parseInt(selectedParfumId.get()));
            loadParfumuri();
            clear();
            mesaj.set("Parfumul a fost șters cu succes!");
        }
    }

    public void searchParfum() {
        Optional<Parfum> p = repo.getAll().stream()
                .filter(parfum -> parfum.getNume().equalsIgnoreCase(nume.get().trim()))
                .findFirst();

        if (p.isPresent()) {
            Parfum found = p.get();
            selectedParfumId.set(String.valueOf(found.getIdParfum()));
            producator.set(found.getProducator());
            descriere.set(found.getDescriere());
            mesaj.set("Parfumul a fost găsit.");
        } else {
            mesaj.set("Parfumul nu a fost găsit.");
        }
    }

    public void filtrareParfumuri() {
        try {
            int idParf = Integer.parseInt(filtrareIdParfumerie.get());
            List<String> rezultate = repo.getParfumuriFiltrate(idParf, filtrareProducator.get());

            parfumuri.clear();
            for (String linie : rezultate) {
                // Presupunem formatul "ID: 1 | Nume: X | Producator: Y | Descriere: Z"
                String[] parts = linie.split("\\|");
                if (parts.length == 4) {
                    String id = parts[0].split(":")[1].trim();
                    String nume = parts[1].split(":")[1].trim();
                    String producator = parts[2].split(":")[1].trim();
                    String descriere = parts[3].split(":")[1].trim();
                    parfumuri.add(List.of(id, nume, producator, descriere));
                }
            }
            mesaj.set("Parfumuri filtrate: " + parfumuri.size());
        } catch (NumberFormatException e) {
            mesaj.set("ID parfumerie invalid pentru filtrare!");
        }
    }


    public void exportParfumuriEpuizateCSV() {
        try {
            int id = Integer.parseInt(filtrareIdParfumerie.get());
            List<String> lista = repo.getParfumuriEpuizate(id);
            try (FileWriter writer = new FileWriter("parfumuri_epuizate.csv")) {
                writer.write("ID,Nume,Producator,Descriere\n");
                for (String linie : lista) writer.write(linie + "\n");
                mesaj.set("Export CSV realizat!");
            }
        } catch (Exception e) {
            mesaj.set("Eroare la export CSV!");
        }
    }

    public void exportParfumuriEpuizateDOC() {
        try {
            int id = Integer.parseInt(filtrareIdParfumerie.get());
            List<String> lista = repo.getParfumuriEpuizate(id);
            try (FileWriter writer = new FileWriter("parfumuri_epuizate.doc")) {
                writer.write("=== Parfumuri epuizate ===\n");
                for (String linie : lista) writer.write(linie + "\n");
                mesaj.set("Export DOC realizat!");
            }
        } catch (Exception e) {
            mesaj.set("Eroare la export DOC!");
        }
    }

    public void selectParfum(List<String> data) {
        if (data == null || data.size() < 4) return;
        selectedParfumId.set(data.get(0));
        nume.set(data.get(1));
        producator.set(data.get(2));
        descriere.set(data.get(3));
    }

    private boolean isValid() {
        return !nume.get().isBlank() && !producator.get().isBlank();
    }

    private boolean hasSelection() {
        return selectedParfumId.get() != null && !selectedParfumId.get().isBlank();
    }

    private void clear() {
        nume.set(""); producator.set(""); descriere.set(""); selectedParfumId.set("");
    }
    public void veziImagine() {
        List<String> selected = parfumuri.stream()
                .filter(p -> p.get(0).equals(selectedParfumId.get()))
                .findFirst()
                .orElse(null);

        if (selected != null && selected.size() >= 2) {
            String numeParfum = selected.get(1);
            String path = "Imagini/" + numeParfum.toLowerCase().replace(" ", "_") + ".jpg";
            File f = new File(path);

            if (f.exists()) {
                Stage imageStage = new Stage();
                imageStage.setTitle(numeParfum + " - Imagine");

                ImageView iv = new ImageView(new Image(f.toURI().toString()));
                iv.setPreserveRatio(true);
                iv.setFitWidth(400);

                Scene scene = new Scene(new StackPane(iv), 420, 500);
                imageStage.setScene(scene);
                imageStage.show();
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Imagine lipsă");
                alert.setContentText("Nu există imagine pentru " + numeParfum);
                alert.showAndWait();
            }
        }
    }


}