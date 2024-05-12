package org.monarchinitiative.automaxoviewer.controller;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableStringValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

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

    private final ObservableSet<String> parentTermLabels;

    private final ObservableStringValue ontologyTermLabelValue;

    public OntologyTermAdderController() {
        parentTermLabels = FXCollections.observableSet();
        ontologyTermLabelValue = new SimpleStringProperty();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addButton.setOnAction(e ->{
            String parentTermText = textField.getText();
            parentTermLabels.add(parentTermText);
            textField.clear();
            setValid(getErrorLabel());
            ontologyTermErrorLabel.setText(getErrorLabel());
        });
        addButton.setStyle("-fx-spacing: 10;");
        Font largeFont = Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 18);
        ontologyTermLabel.setFont(largeFont);
        IntegerBinding parentTermLabelSetSize = Bindings.size(parentTermLabels);
        ontologyTermReadyProperty.bind(parentTermLabelSetSize.greaterThan(0));
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
        if (parentTermLabels.isEmpty()) {
            return "";
        } else {
            List<String> shortLabels = new ArrayList<>();
            for (String ptl : parentTermLabels) {
                shortLabels.add(ptl.length() < 30 ? ptl : String.format("%s...", ptl.substring(0,27)));
            }
            return String.join("; ", shortLabels);
        }
    }

    public StringProperty ontologyTermProperty() {
        return textField.textProperty();
    }

    /**
     *
     * @return a set of Labels representing the parent or parents of the current term.
     */
    public Set<String> getParentSet() {
        return parentTermLabels;
    }

    public TextField getTextField() {
        return textField;
    }


    public void clearFields() {
        this.parentTermLabels.clear();
        this.ontologyTermErrorLabel.setText("");
        setInvalid();
    }

    public BooleanProperty ontologyTermReady() {
        return ontologyTermReadyProperty;
    }



}
