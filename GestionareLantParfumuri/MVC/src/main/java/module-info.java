module org.example.tema3ps {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;

    opens org.example.tema3ps to javafx.fxml;
    opens View to javafx.graphics;

    opens Controller.dto to javafx.base;
    exports Controller.dto;


    opens Controller to javafx.fxml;
    exports Controller;


    opens Model to javafx.base;
    exports Model;
    exports org.example.tema3ps;
}
