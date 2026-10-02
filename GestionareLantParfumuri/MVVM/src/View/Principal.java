package View;

import ViewModel.ParfumVM;
import ViewModel.ParfumerieVM;
import ViewModel.StocVM;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Parent;

public class Principal extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Butoane principale
        Button btnParfum = new Button("Parfumuri");
        Button btnParfumerie = new Button("Parfumerii");
        Button btnStoc = new Button("Stocuri");

        // Acțiuni pe butoane
        btnParfum.setOnAction(e -> deschideParfumGUI());
        btnParfumerie.setOnAction(e -> deschideParfumerieGUI());
        btnStoc.setOnAction(e -> deschideStocGUI());

        // Layout vertical
        VBox root = new VBox(15, btnParfum, btnParfumerie, btnStoc);
        root.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Scene scene = new Scene(root, 300, 200);
        primaryStage.setTitle("Lanț de Parfumuri - MVVM");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void deschideParfumGUI() {
        Stage stage = new Stage(); // Creează noul Stage pentru ParfumGUI

        ParfumVM viewModel = new ParfumVM();
        ParfumGUI gui = new ParfumGUI(viewModel, () -> {

            stage.close();
        });

        stage.setTitle("Parfum GUI");
        stage.setScene(gui.createScene(stage));
        stage.show();
    }


    private void deschideParfumerieGUI() {
        Stage stage = new Stage();

        ParfumerieVM viewModel = new ParfumerieVM();
        ParfumerieGUI gui = new ParfumerieGUI(viewModel, () -> {
            stage.close();
        });

        stage.setTitle("Parfumerii");
        stage.setScene(gui.createScene(stage));
        stage.show();
    }


    private void deschideStocGUI() {
        Stage stage = new Stage();
        StocVM viewModel = new StocVM();
        StocGUI gui = new StocGUI(viewModel, () -> stage.close());

        stage.setTitle("Stocuri");
        stage.setScene(gui.createScene(stage));
        stage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
