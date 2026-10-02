package View;

import Presenter.IStocGUI;
import Presenter.StocPresenter;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ViewStocGUI implements IStocGUI {
    private final StocPresenter stocPresenter;

    public ViewStocGUI(StocPresenter stocPresenter) {
        this.stocPresenter = stocPresenter;
        initGUI();
    }

    @Override
    public void afiseazaListaStocuri() {
        List<StocPresenter.StocInfo> lista = stocPresenter.getAllStocuri();
        System.out.println("=== Lista completă a stocurilor ===");
        for (StocPresenter.StocInfo stoc : lista) {
            System.out.println("ID Stoc: " + stoc.getId() +
                    " | ID Parfumerie: " + stoc.getIdParfumerie() +
                    " | ID Parfum: " + stoc.getIdParfum() +
                    " | Cantitate: " + stoc.getCantitate() +
                    " | Disponibilitate: " + (stoc.isDisponibilitate() ? "Disponibil" : "Indisponibil"));
        }
    }

    @Override
    public void afiseazaStocuriByParfumerie(int idParfumerie) {
        List<StocPresenter.StocInfo> lista = stocPresenter.getStocuriByParfumerie(idParfumerie);
        System.out.println("=== Stocuri pentru parfumeria cu ID " + idParfumerie + " ===");
        for (StocPresenter.StocInfo stoc : lista) {
            System.out.println("ID Stoc: " + stoc.getId() +
                    " | ID Parfum: " + stoc.getIdParfum() +
                    " | Cantitate: " + stoc.getCantitate() +
                    " | Disponibilitate: " + (stoc.isDisponibilitate() ? "Disponibil" : "Indisponibil"));
        }
    }

    private void initGUI() {
        JFrame stocFrame = new JFrame("Gestionare Stocuri");
        stocFrame.setSize(700, 500);
        stocFrame.setLocationRelativeTo(null);
        stocFrame.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(10, 1, 5, 5));

        JTextField txtIdStoc = new JTextField();
        JTextField txtIdParfumerie = new JTextField();
        JTextField txtIdParfum = new JTextField();
        JTextField txtCantitate = new JTextField();
        JCheckBox chkDisponibil = new JCheckBox("Disponibil");

        inputPanel.add(new JLabel("ID Stoc (pentru actualizare):"));
        inputPanel.add(txtIdStoc);
        inputPanel.add(new JLabel("ID Parfumerie:"));
        inputPanel.add(txtIdParfumerie);
        inputPanel.add(new JLabel("ID Parfum:"));
        inputPanel.add(txtIdParfum);
        inputPanel.add(new JLabel("Cantitate:"));
        inputPanel.add(txtCantitate);
        inputPanel.add(chkDisponibil);

        JButton btnAdauga = new JButton("Adaugă");
        JButton btnUpdate = new JButton("Actualizează");
        JButton btnVizualizare = new JButton("Vizualizează Stocuri");

        inputPanel.add(btnAdauga);
        inputPanel.add(btnUpdate);
        inputPanel.add(btnVizualizare);

        stocFrame.add(inputPanel, BorderLayout.NORTH);

        JTextArea mesajArea = new JTextArea();
        mesajArea.setEditable(false);
        stocFrame.add(mesajArea, BorderLayout.SOUTH);

        JPanel displayPanel = new JPanel();
        displayPanel.setLayout(new BoxLayout(displayPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(displayPanel);
        stocFrame.add(scrollPane, BorderLayout.CENTER);

        stocFrame.setVisible(true);

        // Apeluri către metodele din presenter
        btnAdauga.addActionListener(e ->
                stocPresenter.handleAdaugare(txtIdParfumerie, txtIdParfum, txtCantitate, chkDisponibil, mesajArea));

        btnUpdate.addActionListener(e ->
                stocPresenter.handleActualizare(txtIdStoc, txtCantitate, chkDisponibil, mesajArea));

        btnVizualizare.addActionListener(e ->
                stocPresenter.handleVizualizare(displayPanel));
    }
}
