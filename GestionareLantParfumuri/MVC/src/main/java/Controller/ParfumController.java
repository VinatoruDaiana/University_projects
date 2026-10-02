package Controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import Controller.dto.ParfumDTO;
import Model.ViewModel.ParfumViewModel;
import View.ParfumView;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableView;
import javafx.scene.control.Toggle;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ParfumController {
    private final ParfumViewModel model;
    private final ParfumView view;
    private Locale currentLocale;
    private ResourceBundle bundle;

    public ParfumController(ParfumViewModel model, ParfumView view, Locale locale) {
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
        TableView<ParfumDTO> table = view.parfumTable;

        view.idColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("parfum_id"));
        view.numeColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("nume"));
        view.producatorColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("producator"));
        view.descriereColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("descriere"));

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                view.numeField.setText(newVal.getNume());
                view.producatorField.setText(newVal.getProducator());
                view.descriereField.setText(newVal.getDescriere());
            }
        });

        updateLanguage();
    }

    private void setupEventHandlers() {
        view.addButton.setOnAction(e -> {
            model.addParfum(getDTOFromFields());
            clearFields();
        });

        view.updateButton.setOnAction(e -> {
            ParfumDTO dto = getDTOFromFieldsWithId();
            if (dto != null) {
                model.updateParfum(dto);
                clearFields();
            }
        });

        view.deleteButton.setOnAction(e -> {
            ParfumDTO dto = getDTOFromFieldsWithId();
            if (dto != null) {
                model.deleteParfum(dto);
                clearFields();
            }
        });

        view.cautareParfumButton.setOnAction(e -> {
            String numeCautat = view.cautareParfumField.getText().trim();
            List<String> rezultate = model.cautaParfumCuParfumerii(numeCautat);

            view.messageLabel.setText(rezultate.isEmpty()
                    ? "Parfumul nu a fost găsit."
                    : "Rezultate găsite: " + rezultate.size());

            view.rezultatFiltrareList.getItems().setAll(rezultate);

            // opțional: afișează imagine dacă parfumul e cunoscut
            model.getCurrentParfumuri().stream()
                    .filter(p -> p.getNume().equalsIgnoreCase(numeCautat))
                    .findFirst()
                    .ifPresent(p -> {
                        String imagePath = model.getImagePathForParfum(p);
                        if (imagePath != null) {
                            Image img = new Image(getClass().getResourceAsStream(imagePath));
                            ImageView imgView = new ImageView(img);
                            imgView.setFitHeight(300);
                            imgView.setPreserveRatio(true);

                            Stage stage = new Stage();
                            stage.setTitle("Imagine Parfum");
                            stage.setScene(new Scene(new StackPane(imgView), 300, 400));
                            stage.show();
                        }
                    });
        });



        view.exportDocButton.setOnAction(e -> {
            try {
                int id = Integer.parseInt(view.idParfumerieField.getText());
                List<ParfumDTO> epuizate = model.getParfumuriEpuizateDTO(id);

                Export.exportParfumuriEpuizateDoc(epuizate, "parfumuri_epuizate.doc");
            } catch (NumberFormatException ex) {
                view.messageLabel.setText("ID Parfumerie invalid!");
            }
        });

        view.exportCsvButton.setOnAction(e -> {
            try {
                int id = Integer.parseInt(view.idParfumerieField.getText());
                List<ParfumDTO> epuizate = model.getParfumuriEpuizateDTO(id);

                Export.exportParfumuriEpuizateCsv(epuizate, "parfumuri_epuizate.csv");
            } catch (NumberFormatException ex) {
                view.messageLabel.setText("ID Parfumerie invalid!");
            }
        });



        view.filtrareDisponibilitateButton.setOnAction(e -> {
            ParfumDTO selectat = view.parfumTable.getSelectionModel().getSelectedItem();

            if (selectat == null) {
                view.messageLabel.setText("Selectează un parfum din listă!");
                view.rezultatFiltrareList.getItems().clear();
                return;
            }

            if (view.idParfumerieField.getText().isEmpty()) {
                view.messageLabel.setText("Introduceți ID-ul parfumeriei.");
                view.rezultatFiltrareList.getItems().clear();
                return;
            }

            int idParfum = selectat.getParfum_id();
            int idParfumerie = Integer.parseInt(view.idParfumerieField.getText());

            boolean esteDisponibil = model.verificaDisponibilitate(idParfum, idParfumerie);

            // Șterge conținutul anterior din listă
            view.rezultatFiltrareList.getItems().clear();

            if (esteDisponibil) {
                view.messageLabel.setText("Parfumul este disponibil în parfumeria selectată.");
                view.rezultatFiltrareList.getItems().add(
                        String.format("ID: %d | Nume: %s | Producător: %s | Descriere: %s",
                                selectat.getParfum_id(),
                                selectat.getNume(),
                                selectat.getProducator(),
                                selectat.getDescriere()
                        )
                );
            } else {
                view.messageLabel.setText("Parfumul NU este disponibil în parfumeria selectată.");
            }
        });

        view.viewImageButton.setOnAction(e -> {
            ParfumDTO selected = view.parfumTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                view.messageLabel.setText("Selectează un parfum pentru a vizualiza poza.");
                return;
            }

            String imagePath = model.getImagePathForParfum(selected);  // ex: "/Imagini/armani_si.jpeg"
            if (imagePath == null) {
                view.messageLabel.setText("Imagine indisponibilă pentru acest parfum.");
                return;
            }

            try (InputStream is = getClass().getResourceAsStream(imagePath)) {
                if (is == null) {
                    view.messageLabel.setText("Fișierul nu a fost găsit.");
                    return;
                }

                Image image = new Image(is);
                ImageView imageView = new ImageView(image);


                imageView.setFitWidth(250);
                imageView.setFitHeight(300);
                imageView.setPreserveRatio(true);

                VBox layout = new VBox(imageView);
                layout.setPadding(new Insets(10));

                Stage stage = new Stage();
                stage.setTitle("Imagine parfum: " + selected.getNume());
                stage.setScene(new Scene(layout));
                stage.show();

                view.messageLabel.setText("Imagine încărcată.");
            } catch (IOException ex) {
                view.messageLabel.setText("Eroare la încărcarea imaginii.");
                ex.printStackTrace();
            }
        });




        view.clearButton.setOnAction(e -> clearFields());

        ChangeListener<Toggle> languageListener = (obs, oldToggle, newToggle) -> {
            if (newToggle != null) {
                RadioButton selected = (RadioButton) newToggle;
                switch (selected.getText()) {
                    case "English" -> currentLocale = Locale.ENGLISH;
                    case "Francais" -> currentLocale = Locale.FRENCH;
                    case "Romana" -> currentLocale = new Locale("ro", "RO");
                }
                bundle = ResourceBundle.getBundle("messages", currentLocale);
                updateLanguage();
            }
        };

        view.languageToggleGroup.selectedToggleProperty().addListener(languageListener);
    }

    public void load() {
        model.loadParfumuri();
    }

    private ParfumDTO getDTOFromFields() {
        return new ParfumDTO(
                0,
                view.numeField.getText(),
                view.producatorField.getText(),
                view.descriereField.getText()
        );
    }

    private ParfumDTO getDTOFromFieldsWithId() {
        ParfumDTO selected = view.parfumTable.getSelectionModel().getSelectedItem();
        if (selected == null) return null;

        return new ParfumDTO(
                selected.getParfum_id(),
                view.numeField.getText(),
                view.producatorField.getText(),
                view.descriereField.getText()
        );
    }

    private void clearFields() {
        view.numeField.clear();
        view.producatorField.clear();
        view.descriereField.clear();
        view.imaginePathField.clear();
    }

    private void updateLanguage() {
        view.numeLabel.setText(bundle.getString("label.parfum_nume"));
        view.producatorLabel.setText(bundle.getString("label.parfum_producator"));
        view.descriereLabel.setText(bundle.getString("label.parfum_descriere"));

        view.idColumn.setText(bundle.getString("column.parfum_id"));
        view.numeColumn.setText(bundle.getString("column.parfum_nume"));
        view.producatorColumn.setText(bundle.getString("column.parfum_producator"));
        view.descriereColumn.setText(bundle.getString("column.parfum_descriere"));

        view.addButton.setText(bundle.getString("button.add"));
        view.updateButton.setText(bundle.getString("button.update"));
        view.deleteButton.setText(bundle.getString("button.delete"));
        view.clearButton.setText(bundle.getString("button.clear"));
    }
}
