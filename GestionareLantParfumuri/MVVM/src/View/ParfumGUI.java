package View;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import ViewModel.ParfumVM;

import java.io.File;
import java.util.List;

public class ParfumGUI {
    private final ParfumVM viewModel;
    private final Runnable onBack;

    public ParfumGUI(ParfumVM viewModel, Runnable onBack) {
        this.viewModel = viewModel;
        this.onBack = onBack;
    }

    public Scene createScene(Stage stage) {
        TableView<List<String>> table = new TableView<>(viewModel.parfumuri);
        table.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> viewModel.selectParfum(newVal)
        );

        TableColumn<List<String>, String> numeCol = new TableColumn<>("Nume");
        numeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(1)));

        TableColumn<List<String>, String> producatorCol = new TableColumn<>("Producator");
        producatorCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(2)));

        TableColumn<List<String>, String> descriereCol = new TableColumn<>("Descriere");
        descriereCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(3)));

        table.getColumns().addAll(numeCol, producatorCol, descriereCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TextField numeField = new TextField();
        numeField.setPromptText("Nume");
        numeField.textProperty().bindBidirectional(viewModel.nume);

        TextField producatorField = new TextField();
        producatorField.setPromptText("Producator");
        producatorField.textProperty().bindBidirectional(viewModel.producator);

        TextField descriereField = new TextField();
        descriereField.setPromptText("Descriere");
        descriereField.textProperty().bindBidirectional(viewModel.descriere);

        HBox inputs = new HBox(10, numeField, producatorField, descriereField);
        inputs.setAlignment(Pos.CENTER);

        TextField filtruId = new TextField();
        filtruId.setPromptText("ID Parfumerie");
        filtruId.textProperty().bindBidirectional(viewModel.filtrareIdParfumerie);

        TextField filtruProducator = new TextField();
        filtruProducator.setPromptText("Filtru Producator");
        filtruProducator.textProperty().bindBidirectional(viewModel.filtrareProducator);

        HBox filtruInputs = new HBox(10, filtruId, filtruProducator);
        filtruInputs.setAlignment(Pos.CENTER);


        Button seeImageBtn = new Button("Vezi Imagine");
        seeImageBtn.setOnAction(viewModel.showImageCommand);


        Button addBtn = new Button("Adauga");
        addBtn.setOnAction(viewModel.addCommand);

        Button updateBtn = new Button("Actualizeaza");
        updateBtn.setOnAction(viewModel.updateCommand);

        Button deleteBtn = new Button("Sterge");
        deleteBtn.setOnAction(viewModel.deleteCommand);

        Button searchBtn = new Button("Cauta");
        searchBtn.setOnAction(viewModel.searchCommand);

        Button filterBtn = new Button("Filtrare");
        filterBtn.setOnAction(viewModel.filterCommand);

        Button exportCSV = new Button("Export CSV");
        exportCSV.setOnAction(e -> viewModel.exportParfumuriEpuizateCSV());

        Button exportDOC = new Button("Export DOC");
        exportDOC.setOnAction(e -> viewModel.exportParfumuriEpuizateDOC());

        Button backBtn = new Button("Inapoi");
        backBtn.setOnAction(e -> onBack.run());

        Label mesajLabel = new Label();
        mesajLabel.textProperty().bind(viewModel.mesaj);



        HBox buttons1 = new HBox(10, addBtn, updateBtn, deleteBtn, searchBtn);
        HBox buttons2 = new HBox(10, filterBtn, exportCSV, exportDOC, seeImageBtn, backBtn);
        buttons1.setAlignment(Pos.CENTER);
        buttons2.setAlignment(Pos.CENTER);

        VBox layout = new VBox(15, table, inputs, filtruInputs, buttons1, buttons2, mesajLabel);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 20;");

        BackgroundImage backgroundImage = new BackgroundImage(
                new Image(new File("background.jpg").toURI().toString(), 1000, 600, false, true),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
        layout.setBackground(new Background(backgroundImage));

        return new Scene(layout, 1000, 600);
    }


}