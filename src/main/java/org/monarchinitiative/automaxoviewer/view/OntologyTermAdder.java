package org.monarchinitiative.automaxoviewer.view;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import org.controlsfx.control.textfield.TextFields;
import org.monarchinitiative.automaxoviewer.Launcher;
import org.monarchinitiative.automaxoviewer.controller.OntologyTermAdderController;
import org.monarchinitiative.automaxoviewer.model.OntologyRosettaStone;
import org.monarchinitiative.phenol.ontology.data.MinimalOntology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**]
 * Widget for adding parent terms to the current ROBOT entry
 * @author Peter N Robinson
 */
public class OntologyTermAdder extends HBox {
    private final Logger LOGGER = LoggerFactory.getLogger(OntologyTermAdder.class);
    private OntologyTermAdderController controller;


   private OntologyRosettaStone rosettaStone = null;

   private final BooleanProperty parentTermReadyProperty = new SimpleBooleanProperty(false);




    public OntologyTermAdder() {
        super();
        try {
            FXMLLoader loader = new FXMLLoader(Launcher.class.getResource("view/OntologyTermAdder.fxml"));
            controller = new OntologyTermAdderController();
            loader.setController(controller);
            Node node = loader.load();
            this.getChildren().add(node);
            StringProperty parentTermLabelStringProperty = new SimpleStringProperty("");
            parentTermLabelStringProperty.bindBidirectional(controller.ontologyTermProperty());
            parentTermReadyProperty.bind(controller.ontologyTermReady());
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Error loading OntologyTermAdderController: {}", e.getMessage());
        }
    }

    public void setOntology(MinimalOntology ontology) {
        if (ontology == null) {
            LOGGER.error("Attempt to set Ontology with null pointer.");
            return;
        }
        Map<String,String> metaInfo = ontology.getMetaInfo();
        String dataVersion = "n/a";
        if (metaInfo.containsKey("data-version")) {
            dataVersion = metaInfo.get("data-version");
            int last = dataVersion.lastIndexOf("/");
            if (last > 0) {
                dataVersion = dataVersion.substring(last+1);
            }
            LOGGER.info("Adding {} to OntologyTermAdder, version {}", dataVersion, ontology.version().orElse("n/a"));
        } else {
            LOGGER.info("Adding ontology to OntologyTermAdder {}", ontology.version().orElse("n/a"));
        }
        rosettaStone = new OntologyRosettaStone(ontology);
        controller.setRosettaStone(rosettaStone);
        TextFields.bindAutoCompletion(controller.getTextField(), rosettaStone.allLabels());
    }


    public Optional<Term> getTermIfValid() {
        String label = controller.getOntologyTerm();
        if (rosettaStone == null) {
            return Optional.empty();
        }
        Optional<Term> opt = rosettaStone.termFromPrimaryLabel(label);
        if (opt.isPresent()) controller.setValid(opt.get().getName());
        else controller.setInvalid();
        return opt;
    }




    public void setOntologyTerm(String label) {
        this.controller.getTextField().setText(label);
    }

    public void clearFields() {
        this.controller.clearFields();
    }

    public BooleanProperty parentTermsReady() {
        return parentTermReadyProperty;
    }

    public void setLabel(String label) {
        controller.setOntologyLabel(label);
    }

    public void setOntologyLabelCandidate(String label) {
        Optional<Term> opt = this.rosettaStone.termFromPrimaryLabel(label);
        if (opt.isPresent()) {
            this.controller.setValidOntologyLabel(label);
        } else {
            this.controller.setInValidOntologyLabel(label);
        }
    }
}
