package Presenter;

import Model.Parfum;
import Model.Repository.ParfumRepository;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class ParfumPresenter {
    private ParfumRepository parfumRepository;

    public ParfumPresenter(ParfumRepository parfumRepository) {
        this.parfumRepository = parfumRepository;
    }

    public List<ParfumInfo> getAllParfumuri() {
        return parfumRepository.getAll().stream()
                .map(p -> new ParfumInfo(p.getIdParfum(), p.getNume(), p.getProducator(), p.getDescriere(), p.getImagine()))
                .collect(Collectors.toList());
    }

    public Optional<ParfumInfo> cautaParfumDupaNume(String nume) {
        return parfumRepository.getAll().stream()
                .filter(p -> p.getNume().equalsIgnoreCase(nume))
                .findFirst()
                .map(p -> new ParfumInfo(p.getIdParfum(), p.getNume(), p.getProducator(), p.getDescriere(), p.getImagine()));
    }

    public ParfumInfo getParfumById(int idParfum) {
        return parfumRepository.getAll().stream()
                .filter(p -> p.getIdParfum() == idParfum)
                .findFirst()
                .map(p -> new ParfumInfo(p.getIdParfum(), p.getNume(), p.getProducator(), p.getDescriere(), p.getImagine()))
                .orElse(null);
    }

    public List<ParfumInfo> filtreazaParfumuri(int idParfumerie, String producatorFiltru, Boolean disponibilitateFiltru) {
        return parfumRepository.getParfumuriByParfumerie(idParfumerie).stream()
                .filter(p -> producatorFiltru.equals("Toți") || p.getProducator().equalsIgnoreCase(producatorFiltru))
                .filter(p -> disponibilitateFiltru == null || p.isDisponibilitate() == disponibilitateFiltru)
                .map(p -> new ParfumInfo(p.getIdParfum(), p.getNume(), p.getProducator(), p.getDescriere()))
                .collect(Collectors.toList());
    }

    public void addParfum(String nume, String producator, String descriere) {
        parfumRepository.addParfum(new Parfum(0, nume, producator, descriere));
    }

    public void deleteParfum(int idParfum) {
        parfumRepository.deleteParfum(idParfum);
    }

    public void updateParfum(int idParfum, String nume, String producator, String descriere) {
        parfumRepository.updateParfum(new Parfum(idParfum, nume, producator, descriere));
    }

    public Optional<ParfumInfo> cautaParfumCuDisponibilitate(String nume) {
        return parfumRepository.getByName(nume)
                .map(p -> new ParfumInfo(p.getIdParfum(), p.getNume(), p.getProducator(), p.getDescriere(), p.getImagine()));
    }

    // === Action methods ===
    public void handleAdaugare(String nume, String producator, String descriere, JTextArea mesajArea) {
        if (nume.isEmpty() || producator.isEmpty() || descriere.isEmpty()) {
            mesajArea.setText("Completează toate câmpurile!");
            return;
        }
        addParfum(nume, producator, descriere);
        mesajArea.setText("Parfum adăugat cu succes!");
    }

    public void handleStergere(String nume, JTextArea mesajArea) {
        if (nume.isEmpty()) {
            mesajArea.setText("Introdu numele parfumului pentru ștergere!");
            return;
        }
        getAllParfumuri().stream()
                .filter(p -> p.getNume().equalsIgnoreCase(nume))
                .findFirst()
                .ifPresentOrElse(
                        p -> {
                            deleteParfum(p.getId());
                            mesajArea.setText("Parfum șters cu succes!");
                        },
                        () -> mesajArea.setText("Parfumul nu există și nu poate fi șters!"));
    }

    public void handleActualizare(String nume, String producatorNou, String descriereNoua, JTextArea mesajArea) {
        if (nume.isEmpty() || producatorNou.isEmpty() || descriereNoua.isEmpty()) {
            mesajArea.setText("Completează toate câmpurile pentru actualizare!");
            return;
        }
        getAllParfumuri().stream()
                .filter(p -> p.getNume().equalsIgnoreCase(nume))
                .findFirst()
                .ifPresentOrElse(
                        p -> {
                            updateParfum(p.getId(), nume, producatorNou, descriereNoua);
                            mesajArea.setText("Parfumul a fost actualizat!");
                        },
                        () -> mesajArea.setText("Parfumul nu există și nu poate fi actualizat!"));
    }

    public void handleCautare(String numeParfum, JPanel displayPanel, JTextArea mesajArea) {
        displayPanel.removeAll();
        cautaParfumDupaNume(numeParfum).ifPresentOrElse(
                p -> displayPanel.add(new JLabel("Găsit: " + p.getNume() + " | Producător: " + p.getProducator())),
                () -> mesajArea.setText("Parfum negăsit!"));
        displayPanel.revalidate();
        displayPanel.repaint();
    }

    public void handleFiltrare(int idParfumerie, String producator, boolean disponibilitate, JPanel displayPanel, JTextArea mesajArea) {
        var lista = filtreazaParfumuri(idParfumerie, producator, disponibilitate);
        displayPanel.removeAll();
        if (lista.isEmpty()) {
            mesajArea.setText("Niciun parfum găsit conform filtrului.");
        } else {
            mesajArea.setText("Găsite " + lista.size() + " parfumuri.");
            for (var p : lista) {
                JLabel label = new JLabel("ID: " + p.getId() +
                        " | Nume: " + p.getNume() +
                        " | Producător: " + p.getProducator() +
                        " | Descriere: " + p.getDescriere());
                displayPanel.add(label);
            }
        }
        displayPanel.revalidate();
        displayPanel.repaint();
    }

    public void handleCautareGUI(JTextField txtCautareParfum, JTextArea mesajArea, JPanel displayPanel) {
        String numeParfum = txtCautareParfum.getText().trim();

        if (numeParfum.isEmpty()) {
            mesajArea.setText("Introdu numele parfumului pentru căutare!");
            return;
        }

        displayPanel.removeAll();

        cautaParfumDupaNume(numeParfum).ifPresentOrElse(
                p -> {
                    JLabel info = new JLabel("Găsit: " + p.getNume() + " | Producător: " + p.getProducator());
                    displayPanel.add(info);
                },
                () -> mesajArea.setText("Parfum negăsit!")
        );

        displayPanel.revalidate();
        displayPanel.repaint();
    }

    public void handleVizualizare(JPanel displayPanel) {
        displayPanel.removeAll();

        HashMap<String, String> parfumImages = new HashMap<>();
        parfumImages.put("Chanel No 5", "C:/Users/Daiana/Desktop/AN3_SEM2/PS/tema1_PS/Imagini/chanel_no_5.jpg");
        parfumImages.put("Dior Sauvage", "C:/Users/Daiana/Desktop/AN3_SEM2/PS/tema1_PS/Imagini/dior_sauvage.jpg");
        parfumImages.put("Gucci Bloom", "C:/Users/Daiana/Desktop/AN3_SEM2/PS/tema1_PS/Imagini/gucci_bloom.png");
        parfumImages.put("Versace Eros", "C:/Users/Daiana/Desktop/AN3_SEM2/PS/tema1_PS/Imagini/versace_eros.jpg");
        parfumImages.put("Armani Si", "C:/Users/Daiana/Desktop/AN3_SEM2/PS/tema1_PS/Imagini/armani_si.jpeg");


        List<ParfumInfo> lista = getAllParfumuri();
        lista.sort(Comparator.comparing(p -> p.getNume().toLowerCase()));

        for (ParfumInfo p : lista) {
            JPanel parfumPanel = new JPanel(new BorderLayout());

            JLabel textLabel = new JLabel("<html><b>ID:</b> " + p.getId() +
                    " | <b>Nume:</b> " + p.getNume() +
                    " | <b>Producator:</b> " + p.getProducator() +
                    " | <b>Descriere:</b> " + p.getDescriere() + "</html>");
            parfumPanel.add(textLabel, BorderLayout.NORTH);

            // Afișare imagine dacă există
            if (parfumImages.containsKey(p.getNume())) {
                ImageIcon icon = new ImageIcon(parfumImages.get(p.getNume()));
                Image img = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                JLabel imgLabel = new JLabel(new ImageIcon(img));
                imgLabel.setHorizontalAlignment(JLabel.CENTER);
                parfumPanel.add(imgLabel, BorderLayout.CENTER);
            }

            displayPanel.add(parfumPanel);
        }

        displayPanel.revalidate();
        displayPanel.repaint();
    }

    public static class ParfumInfo {
        private int id;
        private String nume;
        private String producator;
        private String descriere;
        private String imagine;

        public ParfumInfo(int id, String nume, String producator, String descriere, String imagine) {
            this.id = id;
            this.nume = nume;
            this.producator = producator;
            this.descriere = descriere;
            this.imagine = imagine;
        }

        public ParfumInfo(int id, String nume, String producator, String descriere) {
            this(id, nume, producator, descriere, null);
        }

        public int getId() { return id; }
        public String getNume() { return nume; }
        public String getProducator() { return producator; }
        public String getDescriere() { return descriere; }
        public String getImagine() { return imagine; }
    }
}