module com.example.daiquiri {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.daiquiri to javafx.fxml;
    exports com.example.daiquiri;
}