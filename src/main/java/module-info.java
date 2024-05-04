module org.monarchinitiative.automaxoviewer {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires org.monarchinitiative.phenol.core;
    requires org.monarchinitiative.phenol.io;
    requires org.controlsfx.controls;
    requires org.slf4j;
    requires json.simple;
    requires java.net.http;

    opens org.monarchinitiative.automaxoviewer.view to javafx.fxml, javafx.web;
    opens org.monarchinitiative.automaxoviewer.controller to javafx.fxml, javafx.web;
    opens org.monarchinitiative.automaxoviewer.model to javafx.base;

    exports org.monarchinitiative.automaxoviewer;
}