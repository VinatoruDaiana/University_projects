package View;

import ViewModel.ParfumerieVM;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

public class ParfumerieGUI {
    private final ParfumerieVM viewModel;
    private final Runnable onBack;

    public ParfumerieGUI(ParfumerieVM viewModel, Runnable onBack) {
        this.viewModel = viewModel;
        this.onBack = onBack;
    }

    public Scene createScene(Stage stage) {
        TableView<List<String>> table = new TableView<>(viewModel.parfumerii);
        table.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> viewModel.selectParfumerie(newVal)
        );

        TableColumn<List<String>, String> numeCol = new TableColumn<>("Nume");
        numeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(1)));

        TableColumn<List<String>, String> adresaCol = new TableColumn<>("Adresa");
        adresaCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(2)));

        TableColumn<List<String>, String> telefonCol = new TableColumn<>("Telefon");
        telefonCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(3)));

        table.getColumns().addAll(numeCol, adresaCol, telefonCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TextField numeField = new TextField();
        numeField.setPromptText("Nume");
        numeField.textProperty().bindBidirectional(viewModel.nume);

        TextField adresaField = new TextField();
        adresaField.setPromptText("Adresa");
        adresaField.textProperty().bindBidirectional(viewModel.adresa);

        TextField telefonField = new TextField();
        telefonField.setPromptText("Telefon");
        telefonField.textProperty().bindBidirectional(viewModel.telefon);

        HBox inputs = new HBox(10, numeField, adresaField, telefonField);
        inputs.setAlignment(Pos.CENTER);

        TextField cautareField = new TextField();
        cautareField.setPromptText("Cauta dupa nume");
        cautareField.textProperty().bindBidirectional(viewModel.cautareNume);

        HBox cautareBox = new HBox(10, cautareField);
        cautareBox.setAlignment(Pos.CENTER);

        Button addBtn = new Button("Adauga");
        addBtn.setOnAction(viewModel.addCommand);

        Button updateBtn = new Button("Actualizeaza");
        updateBtn.setOnAction(viewModel.updateCommand);

        Button deleteBtn = new Button("Sterge");
        deleteBtn.setOnAction(viewModel.deleteCommand);

        Button searchBtn = new Button("Cauta");
        searchBtn.setOnAction(viewModel.searchCommand);



        Button backBtn = new Button("Inapoi");
        backBtn.setOnAction(e -> onBack.run());

        Label mesajLabel = new Label();
        mesajLabel.textProperty().bind(viewModel.mesaj);

        HBox buttons1 = new HBox(10, addBtn, updateBtn, deleteBtn, searchBtn);
        HBox buttons2 = new HBox(10, backBtn);

        buttons1.setAlignment(Pos.CENTER);
        buttons2.setAlignment(Pos.CENTER);

        VBox layout = new VBox(15, table, inputs, cautareBox, buttons1, buttons2, mesajLabel);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 20;");

        return new Scene(layout, 800, 600);
    }
}