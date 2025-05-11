package org.monarchinitiative.automaxoviewer.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import org.monarchinitiative.automaxoviewer.Launcher;
import org.monarchinitiative.automaxoviewer.controller.ChebiTermAdderController;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class ChebiTermAdder extends AnchorPane {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChebiTermAdder.class);
    private ChebiTermAdderController controller;

    public ChebiTermAdder() {
        super();
        try {
            FXMLLoader loader = new FXMLLoader(Launcher.class.getResource("view/ChebiTermAdder.fxml"));
            Node node = loader.load(); // Don't set the controller manually

            this.controller = loader.getController(); // This will retrieve the controller set by the FXMLLoader
            this.getChildren().add(node);
            StringProperty parentTermLabelStringProperty = new SimpleStringProperty("");

        } catch (Exception e) {
            LOGGER.error("Error loading ChebiTermAdder FXML: {}", e.getMessage());
        }
        this.setStyle("-fx-background-color: lightblue;");
    }


    public void  clearFields() {
        this.controller.clearFields();
    }

    public void setUp() {
        this.controller.setUp();
    }

    public Optional<Term> getChebiTerm() {
       Optional<String> idOpt = controller.getIdentifier();
       Optional<String> labelOpt = controller.getLabel();
       if (idOpt.isEmpty() || labelOpt.isEmpty()) {
           LOGGER.info("No ChebiTerm found");
           return Optional.empty();
        }
       TermId tid = TermId.of(idOpt.get());
       Term chebiTerm = Term.of(tid, labelOpt.get());
        LOGGER.info("ChebiTerm being retrieved: {} ({})", chebiTerm.getName(), chebiTerm.id().getValue());
       return Optional.of(chebiTerm);
    }
}
