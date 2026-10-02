package Presenter;

import Model.Repository.ParfumerieRepository;
import Model.Repository.ParfumRepository;
import Model.Repository.StocRepository;
import Model.Parfumerie;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import java.util.List;

public class ParfumeriePresenter {
    private ParfumerieRepository parfumerieRepository;
    private ParfumPresenter parfumPresenter;
    private StocPresenter stocPresenter;
    private StocRepository stocRepository;
    private IParfumerieGUI view;

    public void setView(IParfumerieGUI view) {
        this.view = view;
    }

    public ParfumeriePresenter(ParfumerieRepository pr, ParfumRepository par, StocRepository sr) {
        this.parfumerieRepository = pr;
        this.parfumPresenter = new ParfumPresenter(par);
        this.stocPresenter = new StocPresenter(sr);
        this.stocRepository = sr;
    }

    // DTO Class
    public static class ParfumerieInfo {
        private int id;
        private String nume;
        private String adresa;
        private String telefon;

        public ParfumerieInfo(int id, String nume, String adresa, String telefon) {
            this.id = id;
            this.nume = nume;
            this.adresa = adresa;
            this.telefon = telefon;
        }

        public int getId() { return id; }
        public String getNume() { return nume; }
        public String getAdresa() { return adresa; }
        public String getTelefon() { return telefon; }
    }

    // Metode apelate de View
    public void adaugaParfumerie(String nume, String adresa, String telefon) {
        if (nume.isEmpty() || adresa.isEmpty() || telefon.isEmpty()) {
            System.out.println("Completează toate câmpurile!");
            return;
        }
        parfumerieRepository.addParfumerie(new Parfumerie(0, nume, adresa, telefon));
        System.out.println("Parfumerie adăugată cu succes!");
    }

    public void stergeParfumerie(String nume) {
        if (nume.isEmpty()) {
            System.out.println("Introdu numele parfumeriei pentru ștergere!");
            return;
        }
        int idParfumerie = getIdParfumerieByName(nume);
        if (idParfumerie != -1) {
            parfumerieRepository.deleteParfumerie(idParfumerie);
            System.out.println("Parfumerie ștearsă cu succes!");
        } else {
            System.out.println("Parfumeria nu există!");
        }
    }

    public void actualizeazaParfumerie(String nume, String adresaNoua, String telefonNou) {
        if (nume.isEmpty() || adresaNoua.isEmpty() || telefonNou.isEmpty()) {
            System.out.println("Completează toate câmpurile pentru actualizare!");
            return;
        }
        int idParfumerie = getIdParfumerieByName(nume);
        if (idParfumerie != -1) {
            parfumerieRepository.updateParfumerie(new Parfumerie(idParfumerie, nume, adresaNoua, telefonNou));
            System.out.println("Parfumeria a fost actualizată!");
        } else {
            System.out.println("Parfumeria nu există!");
        }
    }

    public void vizualizeazaParfumierii() {
        getAllParfumerii().forEach(p -> {
            System.out.println("ID: " + p.getId() + " | Nume: " + p.getNume() +
                    " | Adresă: " + p.getAdresa() + " | Telefon: " + p.getTelefon());
        });
    }

    public void exportCSV() {
        exportaToateParfumurileEpuizateCSV("parfumuri_epuizate.csv");
    }

    public void exportDOC() {
        exportaToateParfumurileEpuizateDOC("parfumuri_epuizate.doc");
    }

    private int getIdParfumerieByName(String nume) {
        return getAllParfumerii().stream()
                .filter(p -> p.getNume().equalsIgnoreCase(nume))
                .map(ParfumerieInfo::getId)
                .findFirst().orElse(-1);
    }

    // Alte metode existente
    public List<ParfumerieInfo> getAllParfumerii() {
        return parfumerieRepository.getAll().stream()
                .map(p -> new ParfumerieInfo(p.getIdParfumerie(), p.getNume(), p.getAdresa(), p.getTelefon()))
                .collect(Collectors.toList());
    }

    public List<ParfumerieInfo> getParfumeriiCuParfumDisponibil(int idParfum) {
        List<StocPresenter.StocInfo> stocuri = stocPresenter.getAllStocuri();
        List<Integer> idsParfumerii = stocuri.stream()
                .filter(s -> s.getIdParfum() == idParfum && s.isDisponibilitate())
                .map(StocPresenter.StocInfo::getIdParfumerie)
                .collect(Collectors.toList());

        return getAllParfumerii().stream()
                .filter(p -> idsParfumerii.contains(p.getId()))
                .collect(Collectors.toList());
    }

    public void exportaToateParfumurileEpuizateCSV(String filePath) {
        List<ParfumPresenter.ParfumInfo> parfumuri = parfumPresenter.getAllParfumuri();
        List<StocPresenter.StocInfo> stocuri = stocPresenter.getAllStocuri();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("ID, Nume, Producator, Disponibilitate\n");

            for (StocPresenter.StocInfo stoc : stocuri) {
                if (stoc.getCantitate() == 0) {
                    parfumuri.stream()
                            .filter(p -> p.getId() == stoc.getIdParfum())
                            .findFirst()
                            .ifPresent(parfum -> {
                                try {
                                    writer.write(parfum.getId() + "," + parfum.getNume() + "," +
                                            parfum.getProducator() + ",Epuizat\n");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                }
            }
            writer.flush();
            System.out.println("Export CSV realizat cu succes!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exportaToateParfumurileEpuizateDOC(String filePath) {
        List<ParfumPresenter.ParfumInfo> parfumuri = parfumPresenter.getAllParfumuri();
        List<StocPresenter.StocInfo> stocuri = stocPresenter.getAllStocuri();

        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("Lista tuturor parfumurilor epuizate:\n\n");

            for (StocPresenter.StocInfo stoc : stocuri) {
                if (stoc.getCantitate() == 0) {
                    parfumuri.stream()
                            .filter(p -> p.getId() == stoc.getIdParfum())
                            .findFirst()
                            .ifPresent(parfum -> {
                                try {
                                    writer.write("ID: " + parfum.getId() + "\n");
                                    writer.write("Nume: " + parfum.getNume() + "\n");
                                    writer.write("Producator: " + parfum.getProducator() + "\n");
                                    writer.write("Disponibilitate: Epuizat\n");
                                    writer.write("-------------------------------\n");
                                } catch (IOException e) { e.printStackTrace(); }
                            });
                }
            }
            System.out.println("Export DOC realizat cu succes!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getParfumeriiPentruParfum(String numeParfum) {
        return stocRepository.getParfumeriiForParfum(numeParfum);
    }

    // În clasa ParfumeriePresenter



    public void handleAdaugare(JTextField txtNume, JTextField txtAdresa, JTextField txtTelefon, JTextArea mesajArea) {
        String nume = txtNume.getText().trim();
        String adresa = txtAdresa.getText().trim();
        String telefon = txtTelefon.getText().trim();

        if (nume.isEmpty() || adresa.isEmpty() || telefon.isEmpty()) {
            mesajArea.setText("Completează toate câmpurile!");
            return;
        }

        adaugaParfumerie(nume, adresa, telefon);
        mesajArea.setText("Parfumerie adăugată cu succes!");
    }

    public void handleStergere(JTextField txtNume, JTextArea mesajArea) {
        String nume = txtNume.getText().trim();
        if (nume.isEmpty()) {
            mesajArea.setText("Introdu numele parfumeriei pentru ștergere!");
            return;
        }

        int idParfumerie = -1;
        for (var p : getAllParfumerii()) {
            if (p.getNume().equalsIgnoreCase(nume)) {
                idParfumerie = p.getId();
                break;
            }
        }

        if (idParfumerie != -1) {
            stergeParfumerie(nume);
            mesajArea.setText("Parfumerie ștearsă cu succes!");
        } else {
            mesajArea.setText("Parfumeria nu există!");
        }
    }

    public void handleActualizare(JTextField txtNume, JTextField txtAdresa, JTextField txtTelefon, JTextArea mesajArea) {
        String nume = txtNume.getText().trim();
        String adresaNoua = txtAdresa.getText().trim();
        String telefonNou = txtTelefon.getText().trim();

        if (nume.isEmpty() || adresaNoua.isEmpty() || telefonNou.isEmpty()) {
            mesajArea.setText("Completează toate câmpurile pentru actualizare!");
            return;
        }

        int idParfumerie = -1;
        for (var p : getAllParfumerii()) {
            if (p.getNume().equalsIgnoreCase(nume)) {
                idParfumerie = p.getId();
                break;
            }
        }

        if (idParfumerie != -1) {
            actualizeazaParfumerie( nume, adresaNoua, telefonNou);
            mesajArea.setText("Parfumeria a fost actualizată!");
        } else {
            mesajArea.setText("Parfumeria nu există!");
        }
    }

    public void handleVizualizare(JPanel displayPanel) {
        displayPanel.removeAll();
        for (var p : getAllParfumerii()) {
            JLabel label = new JLabel("ID: " + p.getId() +
                    " | Nume: " + p.getNume() +
                    " | Adresă: " + p.getAdresa() +
                    " | Telefon: " + p.getTelefon());
            displayPanel.add(label);
        }
        displayPanel.revalidate();
        displayPanel.repaint();
    }

    public void handleExportCSV(JTextArea mesajArea) {
        String filePath = "parfumuri_epuizate.csv";
        exportaToateParfumurileEpuizateCSV(filePath);
        mesajArea.setText("Export CSV realizat: " + filePath);
    }

    public void handleExportDOC(JTextArea mesajArea) {
        String filePath = "parfumuri_epuizate.doc";
        exportaToateParfumurileEpuizateDOC(filePath);
        mesajArea.setText("Export DOC realizat: " + filePath);
    }
    public void handleCautareParfum(String nume) {
        parfumPresenter.cautaParfumCuDisponibilitate(nume).ifPresentOrElse(
                p -> {
                    System.out.println("Parfum găsit: " + p.getNume() + ", Producător: " + p.getProducator());
                    List<ParfumerieInfo> lista = getParfumeriiCuParfumDisponibil(p.getId());
                    if (!lista.isEmpty()) {
                        System.out.println("Disponibil în următoarele parfumerii:");
                        lista.forEach(parf -> System.out.println(parf.getNume() + " - " + parf.getAdresa()));
                    } else {
                        System.out.println("Nu există parfumerii unde acest parfum este disponibil.");
                    }
                },
                () -> System.out.println("Parfum negăsit.")
        );
    }

    public void handleFiltrareParfumuri(int idParfumerie, String producator, boolean disponibilitate) {
        List<ParfumPresenter.ParfumInfo> parfumuriFiltrate =
                parfumPresenter.filtreazaParfumuri(idParfumerie, producator, disponibilitate);

        parfumuriFiltrate.forEach(p -> {
            System.out.println("Parfum: " + p.getNume() + " | Producator: " + p.getProducator() +
                    " | Disponibil: " + disponibilitate);
        });
    }



}
