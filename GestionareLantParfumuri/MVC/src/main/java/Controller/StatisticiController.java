package Controller;

import Controller.dto.StatisticiView;
import Model.ViewModel.StatisticiViewModel;

import java.util.Map;

public class StatisticiController {
    private final StatisticiView view;
    private final StatisticiViewModel viewModel;

    public StatisticiController(StatisticiView view, StatisticiViewModel viewModel) {
        this.view = view;
        this.viewModel = viewModel;
        setupEventHandlers();
    }

    private void setupEventHandlers() {
        view.genereazaButton.setOnAction(e -> {
            Map<String, Integer> date = viewModel.genereazaStatisticiPeDescriere();
            view.afiseazaGrafic(date, "Distribuție Parfumuri pe Descriere");
        });
    }
}