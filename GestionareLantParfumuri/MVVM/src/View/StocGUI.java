package View;

import ViewModel.StocVM;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

public class StocGUI {
    private final StocVM viewModel;
    private final Runnable onBack;

    public StocGUI(StocVM viewModel, Runnable onBack) {
        this.viewModel = viewModel;
        this.onBack = onBack;
    }

    public Scene createScene(Stage stage) {
        TableView<List<String>> table = new TableView<>(viewModel.stocuri);
        table.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> viewModel.selectStoc(newVal)
        );

        TableColumn<List<String>, String> idParfumerieCol = new TableColumn<>("ID Parfumerie");
        idParfumerieCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(1)));

        TableColumn<List<String>, String> idParfumCol = new TableColumn<>("ID Parfum");
        idParfumCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(2)));

        TableColumn<List<String>, String> cantitateCol = new TableColumn<>("Cantitate");
        cantitateCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(3)));

        TableColumn<List<String>, String> disponibilitateCol = new TableColumn<>("Disponibilitate");
        disponibilitateCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(4)));

        table.getColumns().addAll(idParfumerieCol, idParfumCol, cantitateCol, disponibilitateCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TextField idParfumerieField = new TextField();
        idParfumerieField.setPromptText("ID Parfumerie");
        idParfumerieField.textProperty().bindBidirectional(viewModel.idParfumerie);

        TextField idParfumField = new TextField();
        idParfumField.setPromptText("ID Parfum");
        idParfumField.textProperty().bindBidirectional(viewModel.idParfum);

        TextField cantitateField = new TextField();
        cantitateField.setPromptText("Cantitate");
        cantitateField.textProperty().bindBidirectional(viewModel.cantitate);

        TextField disponibilitateField = new TextField();
        disponibilitateField.setPromptText("Disponibilitate");
        disponibilitateField.textProperty().bindBidirectional(viewModel.disponibilitate);

        HBox inputs = new HBox(10, idParfumerieField, idParfumField, cantitateField, disponibilitateField);
        inputs.setAlignment(Pos.CENTER);

        TextField searchIdParfumField = new TextField();
        searchIdParfumField.setPromptText("Cauta - ID Parfum");
        searchIdParfumField.textProperty().bindBidirectional(viewModel.searchParfumId);

        TextField searchIdParfumerieField = new TextField();
        searchIdParfumerieField.setPromptText("Cauta - ID Parfumerie");
        searchIdParfumerieField.textProperty().bindBidirectional(viewModel.searchParfumerieId);

        HBox searchBox = new HBox(10, searchIdParfumField, searchIdParfumerieField);
        searchBox.setAlignment(Pos.CENTER);

        Button addBtn = new Button("Adauga");
        addBtn.setOnAction(viewModel.addCommand);

        Button updateBtn = new Button("Actualizeaza");
        updateBtn.setOnAction(viewModel.updateCommand);

        Button deleteBtn = new Button("Sterge");
        deleteBtn.setOnAction(viewModel.deleteCommand);

        Button searchBtn = new Button("Cauta");
        searchBtn.setOnAction(viewModel.searchCommand);

        Button filtrareBtn = new Button("Filtrare Epuizate");
        filtrareBtn.setOnAction(viewModel.filtrareCommand);

        Button backBtn = new Button("Inapoi");
        backBtn.setOnAction(e -> onBack.run());

        Label mesajLabel = new Label();
        mesajLabel.textProperty().bind(viewModel.mesaj);

        HBox buttons1 = new HBox(10, addBtn, updateBtn, deleteBtn, searchBtn, filtrareBtn);
        buttons1.setAlignment(Pos.CENTER);

        HBox buttons2 = new HBox(10, backBtn);
        buttons2.setAlignment(Pos.CENTER);

        VBox layout = new VBox(15, table, inputs, searchBox, buttons1, buttons2, mesajLabel);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 20;");

        return new Scene(layout, 900, 600);
    }
}
