module um.galang.cce109svenebot {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires com.google.gson;

    opens um.galang.cce109svenebot to javafx.fxml;
    exports um.galang.cce109svenebot;
}