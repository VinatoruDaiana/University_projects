package View;


import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import Controller.dto.ParfumerieDTO;
import Model.Observable;
import Model.Observer;
import Model.ViewModel.ParfumerieViewModel;

import java.util.List;

public class ParfumerieView implements Observer {
    public final TableView<ParfumerieDTO> parfumerieTable = new TableView<>();
    public final TableColumn<ParfumerieDTO, Integer> idColumn = new TableColumn<>();
    public final TableColumn<ParfumerieDTO, String> numeColumn = new TableColumn<>();
    public final TableColumn<ParfumerieDTO, String> adresaColumn = new TableColumn<>();
    public final TableColumn<ParfumerieDTO, String> telefonColumn = new TableColumn<>();

    public final TextField numeField = new TextField();
    public final TextField adresaField = new TextField();
    public final TextField telefonField = new TextField();

    public final Button addButton = new Button();
    public final Button updateButton = new Button();
    public final Button deleteButton = new Button();
    public final Button clearButton = new Button();

    public final Label messageLabel = new Label();
    public final Label numeLabel = new Label();
    public final Label adresaLabel = new Label();
    public final Label telefonLabel = new Label();

    public final ToggleGroup languageToggleGroup = new ToggleGroup();
    public final RadioButton englishButton = new RadioButton("English");
    public final RadioButton frenchButton = new RadioButton("Francais");
    public final RadioButton romanianButton = new RadioButton("Romana");

    public final VBox root = new VBox();

    public ParfumerieView() {
        setupLayout();
    }

    private void setupLayout() {
        parfumerieTable.getColumns().addAll(idColumn, numeColumn, adresaColumn, telefonColumn);

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.addRow(0, numeLabel, numeField);
        form.addRow(1, adresaLabel, adresaField);
        form.addRow(2, telefonLabel, telefonField);

        HBox buttons = new HBox(10, addButton, updateButton, deleteButton, clearButton);

        englishButton.setToggleGroup(languageToggleGroup);
        frenchButton.setToggleGroup(languageToggleGroup);
        romanianButton.setToggleGroup(languageToggleGroup);
        englishButton.setSelected(true);

        HBox languageSelector = new HBox(10, new Label("Language:"), englishButton, frenchButton, romanianButton);
        languageSelector.setPadding(new Insets(5, 0, 5, 0));

        root.getChildren().addAll(languageSelector, parfumerieTable, form, buttons, messageLabel);
        root.setSpacing(10);
        root.setPadding(new Insets(10));
    }

    public VBox getView() {
        return root;
    }

    @Override
    public void update(Observable observable) {
        if (observable instanceof ParfumerieViewModel) {
            ParfumerieViewModel viewModel = (ParfumerieViewModel) observable;
            List<ParfumerieDTO> updated = viewModel.getCurrentParfumerii();
            parfumerieTable.setItems(FXCollections.observableArrayList(updated));
            messageLabel.setText("Refreshed!");
            System.out.println("Observer triggered: Parfumerie table refreshed.");
        }
    }
}