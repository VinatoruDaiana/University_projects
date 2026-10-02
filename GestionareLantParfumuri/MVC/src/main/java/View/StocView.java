package View;


import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import Controller.dto.StocDTO;
import Model.Observable;
import Model.Observer;
import Model.ViewModel.StocViewModel;

import java.util.List;

public class StocView implements Observer {
    public final TableView<StocDTO> stocTable = new TableView<>();
    public final TableColumn<StocDTO, Integer> idColumn = new TableColumn<>();
    public final TableColumn<StocDTO, Integer> idParfumColumn = new TableColumn<>();
    public final TableColumn<StocDTO, Integer> idParfumerieColumn = new TableColumn<>();
    public final TableColumn<StocDTO, Integer> cantitateColumn = new TableColumn<>();
    public final TableColumn<StocDTO, Boolean> disponibilitateColumn = new TableColumn<>();

    public final TextField idParfumField = new TextField();
    public final TextField idParfumerieField = new TextField();
    public final TextField cantitateField = new TextField();
    public final CheckBox disponibilitateCheck = new CheckBox();

    public final Button addButton = new Button();
    public final Button updateButton = new Button();
    public final Button deleteButton = new Button();
    public final Button clearButton = new Button();

    public final Label messageLabel = new Label();
    public final Label idParfumLabel = new Label();
    public final Label idParfumerieLabel = new Label();
    public final Label cantitateLabel = new Label();
    public final Label disponibilitateLabel = new Label();

    public final ToggleGroup languageToggleGroup = new ToggleGroup();
    public final RadioButton englishButton = new RadioButton("English");
    public final RadioButton frenchButton = new RadioButton("Francais");
    public final RadioButton romanianButton = new RadioButton("Romana");

    public final VBox root = new VBox();

    public StocView() {
        setupLayout();
    }

    private void setupLayout() {
        stocTable.getColumns().addAll(idColumn, idParfumColumn, idParfumerieColumn, cantitateColumn, disponibilitateColumn);

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.addRow(0, idParfumLabel, idParfumField);
        form.addRow(1, idParfumerieLabel, idParfumerieField);
        form.addRow(2, cantitateLabel, cantitateField);
        form.addRow(3, disponibilitateLabel, disponibilitateCheck);

        HBox buttons = new HBox(10, addButton, updateButton, deleteButton, clearButton);

        englishButton.setToggleGroup(languageToggleGroup);
        frenchButton.setToggleGroup(languageToggleGroup);
        romanianButton.setToggleGroup(languageToggleGroup);
        englishButton.setSelected(true);

        HBox languageSelector = new HBox(10, new Label("Language:"), englishButton, frenchButton, romanianButton);
        languageSelector.setPadding(new Insets(5, 0, 5, 0));

        root.getChildren().addAll(languageSelector, stocTable, form, buttons, messageLabel);
        root.setSpacing(10);
        root.setPadding(new Insets(10));
    }

    public VBox getView() {
        return root;
    }

    @Override
    public void update(Observable observable) {
        if (observable instanceof StocViewModel) {
            StocViewModel viewModel = (StocViewModel) observable;
            List<StocDTO> updated = viewModel.getCurrentStocuri();
            stocTable.setItems(FXCollections.observableArrayList(updated));
            messageLabel.setText("Refreshed!");
            System.out.println("Observer triggered: Stoc table refreshed.");
        }
    }
}