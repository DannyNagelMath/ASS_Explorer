module com.ass.assexplorer {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.ass.assexplorer to javafx.fxml;
    exports com.ass.assexplorer;
}