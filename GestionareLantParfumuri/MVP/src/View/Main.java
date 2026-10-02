package View;

import Model.Repository.ParfumerieRepository;
import Model.Repository.ParfumRepository;
import Model.Repository.StocRepository;
import Presenter.ParfumeriePresenter;
import Presenter.ParfumPresenter;
import Presenter.StocPresenter;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        // Inițializare Repositories și Presenters
        ParfumerieRepository parfumerieRepo = new ParfumerieRepository();
        ParfumRepository parfumRepo = new ParfumRepository();
        StocRepository stocRepo = new StocRepository();

        ParfumeriePresenter parfumeriePresenter = new ParfumeriePresenter(parfumerieRepo, parfumRepo, stocRepo);
        ParfumPresenter parfumPresenter = new ParfumPresenter(parfumRepo);
        StocPresenter stocPresenter = new StocPresenter(stocRepo);


        // Fereastra principală
        JFrame frame = new JFrame("Lant de Parfumuri");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        JButton btnParfumerie = new JButton("Parfumerie");
        JButton btnParfum = new JButton("Parfum");
        JButton btnStoc = new JButton("Stoc");

        panel.add(btnParfumerie);
        panel.add(btnParfum);
        panel.add(btnStoc);

        frame.add(panel);
        frame.setVisible(true);

        // Acțiuni butoane
        btnParfumerie.addActionListener(e -> {
            ViewParfumerieGUI parfumerieGUI = new ViewParfumerieGUI(parfumeriePresenter, parfumPresenter);
            parfumeriePresenter.setView(parfumerieGUI);
        });


        btnParfum.addActionListener(e -> new ViewParfumGUI(parfumPresenter, parfumeriePresenter));

        btnStoc.addActionListener(e -> new ViewStocGUI(stocPresenter));
    }
}
