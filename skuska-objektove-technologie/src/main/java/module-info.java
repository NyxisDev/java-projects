module com.example.skuska2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires mysql.connector.j;
    requires java.sql;
    requires javafx.base;


    opens com.example.skuska2 to javafx.fxml;
    exports com.example.skuska2;
}