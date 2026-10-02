package Controller.dto;

import javafx.collections.FXCollections;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.Map;
import java.util.stream.Collectors;

public class StatisticiView {
    public final Button genereazaButton = new Button("Generează statistici");
    private final PieChart pieChart = new PieChart();
    private final VBox root = new VBox();

    public StatisticiView() {
        root.setSpacing(10);
        root.getChildren().addAll(new Text("Distribuție Parfumuri pe Arome"), genereazaButton, pieChart);
    }

    public VBox getView() {
        return root;
    }

    public void afiseazaGrafic(Map<String, Integer> statistici, String titlu) {
        pieChart.setTitle(titlu);
        pieChart.setData(FXCollections.observableArrayList(
                statistici.entrySet().stream()
                        .map(entry -> new PieChart.Data(entry.getKey(), entry.getValue()))
                        .collect(Collectors.toList())

        ));
    }
}
