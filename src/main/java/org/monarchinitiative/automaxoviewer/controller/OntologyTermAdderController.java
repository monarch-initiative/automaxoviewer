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

import java.net.URL;
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

    public OntologyTermAdderController() {

        ontologTermLabelProperty = new SimpleStringProperty();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addButton.setOnAction(e ->{
            ontologTermLabelProperty.set(textField.getText());
            textField.clear();
            setValid(getErrorLabel());
            ontologyTermErrorLabel.setText(getErrorLabel());
        });
        addButton.setStyle("-fx-spacing: 10;");
        Font largeFont = Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 18);
        ontologyTermLabel.setFont(largeFont);
        ontologyTermReadyProperty.bind(ontologTermLabelProperty.length().greaterThan(0));
        setInvalid();
    }

    private void setInvalid() {
        ontologyTermErrorLabel.setTextFill(Color.RED);
        ontologyTermErrorLabel.setText("Enter at least one parent term");
        textField.setStyle("-fx-text-box-border: red; -fx-focus-color: red ;");
    }

    private void setValid(String msg) {
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


}
