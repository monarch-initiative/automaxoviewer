package org.monarchinitiative.automaxoviewer.controller;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import org.monarchinitiative.automaxoviewer.model.OntologyRosettaStone;
import org.monarchinitiative.phenol.ontology.data.Term;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller for a widget that allows the user to select parent terms by autocomplete or
 * by connecting to the OntologyTree widget.
 */
public class OntologyTermAdderController implements Initializable {

    @FXML
    private Label ontologyTermLabel;

    @FXML
    private TextField textField;

    @FXML
    private Button addButton;

    @FXML
    private Label ontologyTermErrorLabel;

    private final BooleanProperty ontologyTermReadyProperty = new SimpleBooleanProperty(false);

    private final StringProperty ontologTermLabelProperty;

    private OntologyRosettaStone rosettaStone = null;

    public OntologyTermAdderController() {
        ontologTermLabelProperty = new SimpleStringProperty();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addButton.setOnAction(e ->{
            String candidateTermLabel = textField.getText();
            if (rosettaStone == null) {
                setInvalid("ontology not initialized");
                return;
            }
            Optional<Term> opt = rosettaStone.termFromPrimaryLabel(candidateTermLabel);
            if (opt.isEmpty()) {
                setInvalid("Not primary label");
                return;
            }
            ontologTermLabelProperty.set(opt.get().getName());
            textField.clear();
            setValid(getErrorLabel());
        });
        addButton.setStyle("-fx-spacing: 10;");
        Font largeFont = Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 18);
        ontologyTermLabel.setFont(largeFont);
        ontologyTermReadyProperty.bind(ontologTermLabelProperty.length().greaterThan(0));
        setInvalid();
    }

    public void setInvalid() {
        ontologyTermErrorLabel.setTextFill(Color.RED);
        ontologyTermErrorLabel.setText("Use autocomplete to enter an ontology term");
        textField.setStyle("-fx-text-box-border: red; -fx-focus-color: red ;");
    }

    public void setInvalid(String msg) {
        ontologyTermErrorLabel.setTextFill(Color.RED);
        ontologyTermErrorLabel.setText(msg);
        textField.setStyle("-fx-text-box-border: red; -fx-focus-color: red ;");
    }

    public void setValid(String msg) {
        ontologyTermErrorLabel.setTextFill(Color.BLACK);
        ontologyTermErrorLabel.setText(msg);
        textField.setStyle("-fx-text-box-border: green; -fx-focus-color: green ;");
    }



    private String getErrorLabel() {
        if (ontologTermLabelProperty.get().isEmpty()) {
            return "";
        } else {
            return ontologTermLabelProperty.get();
        }
    }

    public StringProperty ontologyTermProperty() {
        return textField.textProperty();
    }



    public String getOntologyTerm() {
        return ontologTermLabelProperty.get();
    }

    public TextField getTextField() {
        return textField;
    }


    public void clearFields() {
        this.ontologTermLabelProperty.set("");
        this.ontologyTermErrorLabel.setText("");
        setInvalid();
    }

    public BooleanProperty ontologyTermReady() {
        return ontologyTermReadyProperty;
    }


    public void setOntologyLabel(String label) {
        this.ontologyTermLabel.setText(label);
    }

    public void setValidOntologyLabel(String label) {
        this.textField.setText(label);
        setValid(label);
    }
    public void setInValidOntologyLabel(String label) {
        this.textField.setText(label);
        setInvalid();
    }


    public void setRosettaStone(OntologyRosettaStone rosettaStone) {
        this.rosettaStone = rosettaStone;
    }
}
