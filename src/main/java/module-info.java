module org.example.youbaksoftware {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.youbaksoftware to javafx.fxml;
    exports org.example.youbaksoftware;
}