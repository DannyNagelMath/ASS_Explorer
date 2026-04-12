module com.dannynagel.assexplorer {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.dannynagel.assexplorer to javafx.fxml;
    exports com.dannynagel.assexplorer;
}