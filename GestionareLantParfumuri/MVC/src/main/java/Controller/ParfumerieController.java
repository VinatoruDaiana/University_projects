package Controller;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableView;
import Controller.dto.ParfumerieDTO;
import Model.ViewModel.ParfumerieViewModel;
import View.ParfumerieView;
import java.util.Locale;
import java.util.ResourceBundle;

public class ParfumerieController {
    private final ParfumerieViewModel model;
    private final ParfumerieView view;
    private Locale currentLocale;
    private ResourceBundle bundle;

    public ParfumerieController(ParfumerieViewModel model, ParfumerieView view, Locale locale) {
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
        TableView<ParfumerieDTO> table = view.parfumerieTable;

        view.idColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("parfumerie_id"));
        view.numeColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("nume"));
        view.adresaColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("adresa"));
        view.telefonColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("telefon"));

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                view.numeField.setText(newVal.getNume());
                view.adresaField.setText(newVal.getAdresa());
                view.telefonField.setText(newVal.getTelefon());
            }
        });

        updateLanguage();
    }

    private void setupEventHandlers() {
        view.addButton.setOnAction(e -> {
            model.addParfumerie(getDTOFromFields());
            clearFields();
        });

        view.updateButton.setOnAction(e -> {
            ParfumerieDTO dto = getDTOFromFieldsWithId();
            if (dto != null) {
                model.updateParfumerie(dto);
                clearFields();
            }
        });

        view.deleteButton.setOnAction(e -> {
            ParfumerieDTO dto = getDTOFromFieldsWithId();
            if (dto != null) {
                model.deleteParfumerie(dto);
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
        model.loadParfumerii();
    }

    private ParfumerieDTO getDTOFromFields() {
        return new ParfumerieDTO(0,
                view.numeField.getText(),
                view.adresaField.getText(),
                view.telefonField.getText());
    }

    private ParfumerieDTO getDTOFromFieldsWithId() {
        ParfumerieDTO selected = view.parfumerieTable.getSelectionModel().getSelectedItem();
        if (selected == null) return null;
        return new ParfumerieDTO(selected.getParfumerie_id(),
                view.numeField.getText(),
                view.adresaField.getText(),
                view.telefonField.getText());
    }

    private void clearFields() {
        view.numeField.clear();
        view.adresaField.clear();
        view.telefonField.clear();
    }

    private void updateLanguage() {
        view.adresaLabel.setText(bundle.getString("label.parfumerie_adresa"));
        view.telefonLabel.setText(bundle.getString("label.parfumerie_telefon"));
        view.numeLabel.setText(bundle.getString("label.parfumerie_nume"));

        view.idColumn.setText(bundle.getString("column.parfumerie_id"));
        view.adresaColumn.setText(bundle.getString("column.parfumerie_adresa"));
        view.telefonColumn.setText(bundle.getString("column.parfumerie_telefon"));
        view.numeColumn.setText(bundle.getString("column.parfumerie_nume"));

        view.addButton.setText(bundle.getString("button.add"));
        view.updateButton.setText(bundle.getString("button.update"));
        view.deleteButton.setText(bundle.getString("button.delete"));
        view.clearButton.setText(bundle.getString("button.clear"));
    }

}