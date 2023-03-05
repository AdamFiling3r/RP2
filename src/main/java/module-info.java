module com.example.rp2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires opencv;
    requires libtensorflow;
    requires java.logging;
    requires java.desktop;

    opens com.example.rp2 to javafx.fxml;
    exports com.example.rp2;
}