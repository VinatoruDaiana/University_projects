package Presenter;

import Model.Repository.StocRepository;
import Model.Stoc;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class StocPresenter {
    private StocRepository stocRepository;

    public StocPresenter(StocRepository stocRepository) {
        this.stocRepository = stocRepository;
    }

    public List<StocInfo> getAllStocuri() {
        return stocRepository.getAll().stream()
                .map(s -> new StocInfo(s.getIdStoc(), s.getIdParfumerie(), s.getIdParfum(), s.getCantitate(), s.isDisponibilitate()))
                .collect(Collectors.toList());
    }

    public List<StocInfo> getStocuriByParfumerie(int idParfumerie) {
        return stocRepository.getByParfumerie(idParfumerie).stream()
                .map(s -> new StocInfo(s.getIdStoc(), s.getIdParfumerie(), s.getIdParfum(), s.getCantitate(), s.isDisponibilitate()))
                .collect(Collectors.toList());
    }

    public List<String> getParfumuriEpuizate(int idParfumerie) {
        return stocRepository.getParfumuriEpuizate(idParfumerie);
    }

    // === CRUD ===
    public void addStoc(int idParfumerie, int idParfum, int cantitate, boolean disponibilitate) {
        Stoc stoc = new Stoc(0, idParfumerie, idParfum, cantitate, disponibilitate);
        stocRepository.addStoc(stoc);
    }

    public void updateStoc(int idStoc, int cantitate, boolean disponibilitate) {
        stocRepository.updateStoc(idStoc, cantitate, disponibilitate);
    }

    public int getIdStoc(int idParfum, int idParfumerie) {
        return stocRepository.getIdStoc(idParfum, idParfumerie);
    }

    // === METODE PENTRU VIEW ===
    public void handleAdaugare(JTextField txtIdParfumerie, JTextField txtIdParfum, JTextField txtCantitate, JCheckBox chkDisponibil, JTextArea mesajArea) {
        try {
            int idParfumerie = Integer.parseInt(txtIdParfumerie.getText().trim());
            int idParfum = Integer.parseInt(txtIdParfum.getText().trim());
            int cantitate = Integer.parseInt(txtCantitate.getText().trim());
            boolean disponibilitate = chkDisponibil.isSelected();

            addStoc(idParfumerie, idParfum, cantitate, disponibilitate);
            mesajArea.setText("Stoc adăugat cu succes!");
        } catch (NumberFormatException ex) {
            mesajArea.setText("Toate câmpurile trebuie completate corect (numere)!");
        }
    }

    public void handleActualizare(JTextField txtIdStoc, JTextField txtCantitate, JCheckBox chkDisponibil, JTextArea mesajArea) {
        try {
            int idStoc = Integer.parseInt(txtIdStoc.getText().trim());
            int cantitate = Integer.parseInt(txtCantitate.getText().trim());
            boolean disponibilitate = chkDisponibil.isSelected();

            updateStoc(idStoc, cantitate, disponibilitate);
            mesajArea.setText("Stoc actualizat cu succes!");
        } catch (NumberFormatException ex) {
            mesajArea.setText("ID-ul și cantitatea trebuie să fie numere valide!");
        }
    }

    public void handleVizualizare(JPanel displayPanel) {
        displayPanel.removeAll();
        var lista = getAllStocuri();
        for (var s : lista) {
            JLabel label = new JLabel("ID: " + s.getId() +
                    " | Parfumerie: " + s.getIdParfumerie() +
                    " | Parfum: " + s.getIdParfum() +
                    " | Cantitate: " + s.getCantitate() +
                    " | Disponibil: " + (s.isDisponibilitate() ? "Da" : "Nu"));
            displayPanel.add(label);
        }
        displayPanel.revalidate();
        displayPanel.repaint();
    }

    public static class StocInfo {
        private int id;
        private int idParfumerie;
        private int idParfum;
        private int cantitate;
        private boolean disponibilitate;

        public StocInfo(int id, int idParfumerie, int idParfum, int cantitate, boolean disponibilitate) {
            this.id = id;
            this.idParfumerie = idParfumerie;
            this.idParfum = idParfum;
            this.cantitate = cantitate;
            this.disponibilitate = disponibilitate;
        }

        public int getId() { return id; }
        public int getIdParfumerie() { return idParfumerie; }
        public int getIdParfum() { return idParfum; }
        public int getCantitate() { return cantitate; }
        public boolean isDisponibilitate() { return disponibilitate; }
    }
}