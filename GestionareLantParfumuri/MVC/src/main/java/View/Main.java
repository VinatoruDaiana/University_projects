package View;

import Controller.StatisticiController;
import Controller.dto.StatisticiView;
import Model.Repository.StatisticiRepository;
import Model.ViewModel.StatisticiViewModel;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import Controller.ParfumController;
import Controller.ParfumerieController;
import Controller.StocController;
import Model.Repository.ParfumRepository;
import Model.Repository.ParfumerieRepository;
import Model.Repository.StocRepository;
import Model.ViewModel.ParfumViewModel;
import Model.ViewModel.ParfumerieViewModel;
import Model.ViewModel.StocViewModel;
import java.util.Locale;



public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Limba default
        Locale locale = Locale.ENGLISH;

        // Initializare pentru Parfum
        ParfumRepository parfumRepository = new ParfumRepository();
        ParfumViewModel parfumViewModel = new ParfumViewModel(parfumRepository);
        ParfumView parfumView = new ParfumView();
        ParfumController parfumController = new ParfumController(parfumViewModel, parfumView, locale);

        // Initializare pentru Parfumerie
        ParfumerieRepository parfumerieRepository = new ParfumerieRepository();
        ParfumerieViewModel parfumerieViewModel = new ParfumerieViewModel(parfumerieRepository);
        ParfumerieView parfumerieView = new ParfumerieView();
        ParfumerieController parfumerieController = new ParfumerieController(parfumerieViewModel, parfumerieView, locale);

        // Initializare pentru Stoc
        StocRepository stocRepository = new StocRepository();
        StocViewModel stocViewModel = new StocViewModel(stocRepository);
        StocView stocView = new StocView();
        StocController stocController = new StocController(stocViewModel, stocView, locale);

        // Initializare pentru Statistici
        // StatisticView statisticView = new StatisticView(); // doar view pentru moment

        // Butoane de navigare
        Button btnParfum = new Button("Parfumuri");
        Button btnParfumerie = new Button("Parfumerii");
        Button btnStoc = new Button("Stocuri");
        Button btnStatistici = new Button("Statistici");

        HBox menuBar = new HBox(15, btnParfum, btnParfumerie, btnStoc, btnStatistici);
        menuBar.setPadding(new Insets(10));

        // Layout principal
        BorderPane root = new BorderPane();
        root.setTop(menuBar);
        root.setCenter(parfumView.getView()); // View-ul implicit

        // Actiuni pe butoane
        btnParfum.setOnAction(e -> root.setCenter(parfumView.getView()));
        btnParfumerie.setOnAction(e -> root.setCenter(parfumerieView.getView()));
        btnStoc.setOnAction(e -> root.setCenter(stocView.getView()));
        // btnStatistici.setOnAction(e -> root.setCenter(statisticView.getView()));

        // Scenă
        Scene scene = new Scene(root, 1000, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Gestiune Parfumuri - Lanț de Parfumerii");
        primaryStage.show();



        StatisticiRepository statisticiRepository = new StatisticiRepository();
        StatisticiViewModel statisticiViewModel = new StatisticiViewModel(statisticiRepository);
        StatisticiView statisticiView = new StatisticiView();
        StatisticiController statisticiController = new StatisticiController(statisticiView, statisticiViewModel);

        btnStatistici.setOnAction(e -> root.setCenter(statisticiView.getView()));


    }

    public static void main(String[] args) {
        launch(args);
    }
}
