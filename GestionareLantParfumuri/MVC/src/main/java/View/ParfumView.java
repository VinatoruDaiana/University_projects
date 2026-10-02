package View;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import Controller.dto.ParfumDTO;
import Model.Observable;
import Model.Observer;
import Model.ViewModel.ParfumViewModel;
import java.util.List;

public class ParfumView implements Observer {
    public final TableView<ParfumDTO> parfumTable = new TableView<>();
    public final TableColumn<ParfumDTO, Integer> idColumn = new TableColumn<>();
    public final TableColumn<ParfumDTO, String> numeColumn = new TableColumn<>();
    public final TableColumn<ParfumDTO, String> producatorColumn = new TableColumn<>();
    public final TableColumn<ParfumDTO, String> descriereColumn = new TableColumn<>();
    public final TableColumn<ParfumDTO, String> imaginePathColumn = new TableColumn<>();

    public final TextField numeField = new TextField();
    public final TextField producatorField = new TextField();
    public final TextField descriereField = new TextField();
    public final TextField imaginePathField = new TextField();

    public final Button addButton = new Button();
    public final Button updateButton = new Button();
    public final Button deleteButton = new Button();
    public final Button clearButton = new Button();
    public final Button exportDocButton = new Button("Export DOC");
    public final Button exportCsvButton = new Button("Export CSV");


    public final Label messageLabel = new Label();
    public final Label numeLabel = new Label();
    public final Label producatorLabel = new Label();
    public final Label descriereLabel = new Label();
    public final Label imaginePathLabel = new Label();

    public final Button filtrareDisponibilitateButton = new Button("Filtrare");
    public final TextField idParfumerieField = new TextField();
    public final Label idParfumerieLabel = new Label("ID Parfumerie:");
    public final ListView<String> rezultatFiltrareList = new ListView<>();

    public final ToggleGroup languageToggleGroup = new ToggleGroup();
    public final RadioButton englishButton = new RadioButton("Engleza");
    public final RadioButton frenchButton = new RadioButton("Franceza");
    public final RadioButton romanianButton = new RadioButton("Romana");

    public final Button viewImageButton = new Button("Vizualizare Poză");
    public final ImageView parfumImageView = new ImageView();

    public final TextField cautareParfumField = new TextField();
    public final Button cautareParfumButton = new Button("Căutare Parfum");

    public final VBox root = new VBox();

    public ParfumView() {
        setupLayout();
    }

    private void setupLayout() {
        // Tabel
        parfumTable.getColumns().addAll(idColumn, numeColumn, producatorColumn, descriereColumn, imaginePathColumn);

        // Formular
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.addRow(0, numeLabel, numeField);
        form.addRow(1, producatorLabel, producatorField);
        form.addRow(2, descriereLabel, descriereField);
        form.addRow(3, imaginePathLabel, imaginePathField);

        // Butoane
        HBox buttons = new HBox(10, addButton, updateButton, deleteButton, clearButton, viewImageButton,exportDocButton, exportCsvButton);

        // Selector limba
        englishButton.setToggleGroup(languageToggleGroup);
        frenchButton.setToggleGroup(languageToggleGroup);
        romanianButton.setToggleGroup(languageToggleGroup);
        englishButton.setSelected(true); // default

        HBox languageSelector = new HBox(10, new Label("Limba:"), englishButton, frenchButton, romanianButton);
        languageSelector.setPadding(new Insets(5, 0, 5, 0));

        // Căutare parfum
        HBox cautareBox = new HBox(10, new Label("Nume parfum:"), cautareParfumField, cautareParfumButton);
        cautareBox.setPadding(new Insets(5, 0, 5, 0));

        // Filtrare parfumuri disponibile
        HBox filtrareBox = new HBox(10, idParfumerieLabel, idParfumerieField, filtrareDisponibilitateButton);
        filtrareBox.setPadding(new Insets(5, 0, 5, 0));

        // Asamblare UI
        root.setSpacing(10);
        root.setPadding(new Insets(10));
        root.getChildren().addAll(
                languageSelector,
                parfumTable,
                form,
                buttons,
                messageLabel,
                cautareBox,
                filtrareBox,
                rezultatFiltrareList
        );
    }

    public VBox getView() {
        return root;
    }

    @Override
    public void update(Observable observable) {
        if (observable instanceof ParfumViewModel) {
            ParfumViewModel viewModel = (ParfumViewModel) observable;
            List<ParfumDTO> updated = viewModel.getCurrentParfumuri();
            parfumTable.setItems(FXCollections.observableArrayList(updated));
            messageLabel.setText("Refreshed!");
            System.out.println("Observer triggered: Parfum table refreshed.");
        }
    }
}
