package Controller;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableView;
import Controller.dto.StocDTO;
import Model.ViewModel.StocViewModel;
import View.StocView;
import java.util.Locale;
import java.util.ResourceBundle;

public class StocController {
    private final StocViewModel model;
    private final StocView view;
    private Locale currentLocale;
    private ResourceBundle bundle;

    public StocController(StocViewModel model, StocView view, Locale locale) {
        this.model = model;
        this.view = view;
        this.currentLocale = locale;
        this.bundle = ResourceBundle.getBundle("messages", currentLocale);

        model.addObserver(view);
        setupBindings();
        setupEventHandlers();
        load();
    }

    private void setupBindings() {
        TableView<StocDTO> table = view.stocTable;

        view.idColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("stoc_id"));
        view.idParfumColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("id_parfum"));
        view.idParfumerieColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("id_parfumerie"));
        view.cantitateColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("cantitate"));
        view.disponibilitateColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("disponibilitate"));

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                view.idParfumField.setText(String.valueOf(newVal.getId_parfum()));
                view.idParfumerieField.setText(String.valueOf(newVal.getId_parfumerie()));
                view.cantitateField.setText(String.valueOf(newVal.getCantitate()));
                view.disponibilitateCheck.setSelected(newVal.isDisponibilitate());
            }
        });

        updateLanguage();
    }

    private void setupEventHandlers() {
        view.addButton.setOnAction(e -> {
            model.addStoc(getDTOFromFields());
            clearFields();
        });

        view.updateButton.setOnAction(e -> {
            StocDTO dto = getDTOFromFieldsWithId();
            if (dto != null) {
                model.updateStoc(dto);
                clearFields();
            }
        });

        view.deleteButton.setOnAction(e -> {
            StocDTO dto = getDTOFromFieldsWithId();
            if (dto != null) {
                model.deleteStoc(dto);
                clearFields();
            }
        });

        view.clearButton.setOnAction(e -> clearFields());

        ChangeListener<javafx.scene.control.Toggle> languageListener = (obs, oldToggle, newToggle) -> {
            if (newToggle != null) {
                RadioButton selected = (RadioButton) newToggle;
                switch (selected.getText()) {
                    case "English":
                        currentLocale = Locale.ENGLISH;
                        break;
                    case "Francais":
                        currentLocale = Locale.FRENCH;
                        break;
                    case "Romana":
                        currentLocale = new Locale("ro", "RO");
                        break;
                }

                bundle = ResourceBundle.getBundle("messages", currentLocale);
                updateLanguage();
            }
        };

        view.languageToggleGroup.selectedToggleProperty().addListener(languageListener);
    }

    public void load() {
        model.loadStocuri();
    }

    private StocDTO getDTOFromFields() {
        return new StocDTO(0,
                Integer.parseInt(view.idParfumField.getText()),
                Integer.parseInt(view.idParfumerieField.getText()),
                Integer.parseInt(view.cantitateField.getText()),
                view.disponibilitateCheck.isSelected());
    }

    private StocDTO getDTOFromFieldsWithId() {
        StocDTO selected = view.stocTable.getSelectionModel().getSelectedItem();
        if (selected == null) return null;
        return new StocDTO(selected.getStoc_id(),
                Integer.parseInt(view.idParfumField.getText()),
                Integer.parseInt(view.idParfumerieField.getText()),
                Integer.parseInt(view.cantitateField.getText()),
                view.disponibilitateCheck.isSelected());
    }

    private void clearFields() {
        view.idParfumField.clear();
        view.idParfumerieField.clear();
        view.cantitateField.clear();
        view.disponibilitateCheck.setSelected(false);
    }

    private void updateLanguage() {
        view.idParfumLabel.setText(bundle.getString("label.stoc_idParfum"));
        view.idParfumerieLabel.setText(bundle.getString("label.stoc_idParfumerie"));
        view.cantitateLabel.setText(bundle.getString("label.stoc_cantitate"));
        view.disponibilitateLabel.setText(bundle.getString("label.stoc_disponibilitate"));

        view.idColumn.setText(bundle.getString("column.stoc_id"));
        view.idParfumColumn.setText(bundle.getString("column.stoc_idParfum"));
        view.idParfumerieColumn.setText(bundle.getString("column.stoc_idParfumerie"));
        view.cantitateColumn.setText(bundle.getString("column.stoc_cantitate"));
        view.disponibilitateColumn.setText(bundle.getString("column.stoc_disponibilitate"));

        view.addButton.setText(bundle.getString("button.add"));
        view.updateButton.setText(bundle.getString("button.update"));
        view.deleteButton.setText(bundle.getString("button.delete"));
        view.clearButton.setText(bundle.getString("button.clear"));
    }

}