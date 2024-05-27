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
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires java.desktop;
    opens org.monarchinitiative.automaxoviewer.view to javafx.fxml, javafx.web;
    opens org.monarchinitiative.automaxoviewer.controller to javafx.fxml, javafx.web;
    opens org.monarchinitiative.automaxoviewer.model to javafx.base;
    opens org.monarchinitiative.automaxoviewer.json to com.fasterxml.jackson.databind;

    exports org.monarchinitiative.automaxoviewer;
    exports org.monarchinitiative.automaxoviewer.json to com.fasterxml.jackson.databind;

}