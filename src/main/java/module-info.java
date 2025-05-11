module org.monarchinitiative.automaxoviewer {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires org.monarchinitiative.phenol.core;
    requires org.monarchinitiative.phenol.io;
    requires org.controlsfx.controls;
    requires org.slf4j;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    opens org.monarchinitiative.automaxoviewer.view to javafx.fxml, javafx.web;
    opens org.monarchinitiative.automaxoviewer.model to javafx.base;
    opens org.monarchinitiative.automaxoviewer.json to com.fasterxml.jackson.databind;

    exports org.monarchinitiative.automaxoviewer;
    exports org.monarchinitiative.automaxoviewer.json to com.fasterxml.jackson.databind;
    opens org.monarchinitiative.automaxoviewer to javafx.base;
    exports org.monarchinitiative.automaxoviewer.controller;
    opens org.monarchinitiative.automaxoviewer.controller to javafx.base, javafx.fxml, javafx.web;

}