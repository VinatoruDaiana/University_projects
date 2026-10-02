package View;

import Presenter.IParfumerieGUI;
import Presenter.ParfumPresenter;
import Presenter.ParfumeriePresenter;

import javax.swing.*;
import java.awt.*;

public class ViewParfumerieGUI implements IParfumerieGUI {
    private final ParfumeriePresenter presenter;
    private final ParfumPresenter parfumPresenter;

    public ViewParfumerieGUI(ParfumeriePresenter presenter, ParfumPresenter parfumPresenter) {
        this.presenter = presenter;
        this.parfumPresenter = parfumPresenter;
        this.presenter.setView(this);
        show();
    }

    public void show() {
        JFrame frame = new JFrame("Gestionare Parfumerii");
        frame.setSize(600, 450);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(10, 1, 5, 5));

        JTextField txtNume = new JTextField();
        JTextField txtAdresa = new JTextField();
        JTextField txtTelefon = new JTextField();

        inputPanel.add(new JLabel("Nume Parfumerie:"));
        inputPanel.add(txtNume);
        inputPanel.add(new JLabel("Adresă:"));
        inputPanel.add(txtAdresa);
        inputPanel.add(new JLabel("Telefon:"));
        inputPanel.add(txtTelefon);

        JButton btnAdauga = new JButton("Adaugă");
        JButton btnSterge = new JButton("Șterge");
        JButton btnUpdate = new JButton("Actualizează");
        JButton btnVizualizare = new JButton("Vizualizează");
        JButton btnExportCSV = new JButton("Exportă CSV Epuizate");
        JButton btnExportDOC = new JButton("Exportă DOC Epuizate");

        inputPanel.add(btnAdauga);
        inputPanel.add(btnSterge);
        inputPanel.add(btnUpdate);
        inputPanel.add(btnVizualizare);
        inputPanel.add(btnExportCSV);
        inputPanel.add(btnExportDOC);

        JTextArea mesajArea = new JTextArea();
        mesajArea.setEditable(false);

        JPanel displayPanel = new JPanel();
        displayPanel.setLayout(new BoxLayout(displayPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(displayPanel);

        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(mesajArea, BorderLayout.SOUTH);

        frame.setVisible(true);

        // Doar apelare metode din presenter
        btnAdauga.addActionListener(e -> presenter.handleAdaugare(txtNume, txtAdresa, txtTelefon, mesajArea));
        btnSterge.addActionListener(e -> presenter.handleStergere(txtNume, mesajArea));
        btnUpdate.addActionListener(e -> presenter.handleActualizare(txtNume, txtAdresa, txtTelefon, mesajArea));
        btnVizualizare.addActionListener(e -> presenter.handleVizualizare(displayPanel));
        btnExportCSV.addActionListener(e -> presenter.handleExportCSV(mesajArea));
        btnExportDOC.addActionListener(e -> presenter.handleExportDOC(mesajArea));
    }

    @Override
    public void afiseazaListaParfumuri() {
        parfumPresenter.getAllParfumuri().forEach(p -> System.out.println(p.getNume() + " - " + p.getProducator()));
    }

    @Override
    public void afiseazaListaParfumerii() {
        presenter.getAllParfumerii().forEach(p -> System.out.println(p.getNume() + " - " + p.getAdresa()));
    }

    @Override
    public void cautaParfumDupaNume(String nume) {
        presenter.handleCautareParfum(nume);
    }

    @Override
    public void filtreazaParfumuri(int idParfumerie, String producator, boolean disponibilitate) {
        presenter.handleFiltrareParfumuri(idParfumerie, producator, disponibilitate);
    }
}