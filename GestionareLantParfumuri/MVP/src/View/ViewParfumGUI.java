package View;

import Presenter.IParfumGUI;
import Presenter.ParfumPresenter;
import Presenter.ParfumeriePresenter;

import javax.swing.*;
import java.awt.*;

public class ViewParfumGUI implements IParfumGUI {
    private final ParfumPresenter parfumPresenter;
    private final ParfumeriePresenter parfumeriePresenter;
    JPanel displayPanel;
    JTextArea mesajArea;

    public ViewParfumGUI(ParfumPresenter parfumPresenter, ParfumeriePresenter parfumeriePresenter) {
        this.parfumPresenter = parfumPresenter;
        this.parfumeriePresenter = parfumeriePresenter;
        initGUI();
    }

    @Override
    public void afiseazaListaParfumuri() {
        parfumPresenter.getAllParfumuri().forEach(p -> {
            System.out.println(p.getNume() + " - " + p.getProducator());
        });
    }

    @Override
    public void cautaParfumDupaNume(String nume) {
        parfumPresenter.handleCautare(nume, displayPanel, mesajArea);
    }

    private void initGUI() {
        JFrame parfumFrame = new JFrame("Gestionare Parfumuri");
        parfumFrame.setSize(900, 700);
        parfumFrame.setLocationRelativeTo(null);
        parfumFrame.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // === Secțiunea Informații parfum ===
        JTextField txtNume = new JTextField();
        JTextField txtProducator = new JTextField();
        JTextField txtDescriere = new JTextField();
        JTextField txtCautareParfum = new JTextField();

        inputPanel.add(new JLabel("Nume Parfum:"));
        inputPanel.add(txtNume);
        inputPanel.add(new JLabel("Producator:"));
        inputPanel.add(txtProducator);
        inputPanel.add(new JLabel("Descriere:"));
        inputPanel.add(txtDescriere);

        inputPanel.add(new JLabel("Nume parfum pentru căutare:"));
        inputPanel.add(txtCautareParfum);

        // === Butoane ===
        JPanel btnPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        JButton btnCautareParfum = new JButton("Caută");
        JButton btnAdauga = new JButton("Adaugă");
        JButton btnSterge = new JButton("Șterge");
        JButton btnUpdate = new JButton("Actualizează");
        JButton btnVizualizare = new JButton("Vizualizează Parfumuri");
        JButton btnFiltrare = new JButton("Filtrează Parfumuri");

        btnPanel.add(btnCautareParfum);
        btnPanel.add(btnAdauga);
        btnPanel.add(btnSterge);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnVizualizare);

        inputPanel.add(Box.createVerticalStrut(10));
        inputPanel.add(btnPanel);

        // === Panel Filtrare ===
        JTextField txtIdParfumerie = new JTextField();
        JTextField txtFiltruProducator = new JTextField();
        JCheckBox chkDisponibil = new JCheckBox("Disponibil");

        JPanel filtrarePanel = new JPanel(new GridLayout(4, 2, 5, 5));
        filtrarePanel.setBorder(BorderFactory.createTitledBorder("Filtrare parfumuri"));

        filtrarePanel.add(new JLabel("ID Parfumerie pentru Filtrare:"));
        filtrarePanel.add(txtIdParfumerie);

        filtrarePanel.add(new JLabel("Producător Filtru:"));
        filtrarePanel.add(txtFiltruProducator);

        filtrarePanel.add(new JLabel("Disponibilitate:"));
        filtrarePanel.add(chkDisponibil);

        filtrarePanel.add(new JLabel(""));
        filtrarePanel.add(btnFiltrare);

        inputPanel.add(Box.createVerticalStrut(10));
        inputPanel.add(filtrarePanel);

        // === Mesaj și Display Panel ===
        mesajArea = new JTextArea();
        mesajArea.setEditable(false);

        displayPanel = new JPanel();
        displayPanel.setLayout(new BoxLayout(displayPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(displayPanel);

        parfumFrame.add(inputPanel, BorderLayout.NORTH);
        parfumFrame.add(scrollPane, BorderLayout.CENTER);
        parfumFrame.add(mesajArea, BorderLayout.SOUTH);

        parfumFrame.setVisible(true);

        // === Apeluri către presenter ===
        btnAdauga.addActionListener(e -> parfumPresenter.handleAdaugare(
                txtNume.getText().trim(), txtProducator.getText().trim(),
                txtDescriere.getText().trim(), mesajArea));

        btnSterge.addActionListener(e -> parfumPresenter.handleStergere(txtNume.getText().trim(), mesajArea));

        btnUpdate.addActionListener(e -> parfumPresenter.handleActualizare(
                txtNume.getText().trim(), txtProducator.getText().trim(),
                txtDescriere.getText().trim(), mesajArea));

        btnVizualizare.addActionListener(e -> parfumPresenter.handleVizualizare(displayPanel));

        btnCautareParfum.addActionListener(e -> parfumPresenter.handleCautareGUI(txtCautareParfum, mesajArea, displayPanel));

        btnFiltrare.addActionListener(e -> {
            try {
                int idParfumerie = Integer.parseInt(txtIdParfumerie.getText().trim());
                parfumPresenter.handleFiltrare(idParfumerie,
                        txtFiltruProducator.getText().trim(),
                        chkDisponibil.isSelected(),
                        displayPanel, mesajArea);
            } catch (NumberFormatException ex) {
                mesajArea.setText("ID Parfumerie trebuie să fie un număr valid!");
            }
        });
    }
}
