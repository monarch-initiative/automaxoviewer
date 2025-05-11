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
import java.util.*;
import java.util.stream.Collectors;

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
    private Button addMostCommandButton;

    @FXML
    private Button add2MostCommandButton;

    @FXML
    private Label ontologyTermErrorLabel;

    private final BooleanProperty ontologyTermReadyProperty = new SimpleBooleanProperty(false);

    private final StringProperty ontologTermLabelProperty;

    private OntologyRosettaStone rosettaStone = null;

    private HashMap<String, Integer> termUsageCount;

    public OntologyTermAdderController() {
        ontologTermLabelProperty = new SimpleStringProperty();
        termUsageCount = new HashMap<>();
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
                String errmsg = String.format("\"%s\" is not the primary label", candidateTermLabel);
                setInvalid(errmsg);
                return;
            }
            String label = opt.get().getName();
            ontologTermLabelProperty.set(label);
            incrementCommonLabel(label);
            textField.clear();
            setValid(getErrorLabel());
            setCommonLabel();
        });
        addButton.setStyle("-fx-spacing: 10;");
        Font largeFont = Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 18);
        ontologyTermLabel.setFont(largeFont);
        ontologyTermReadyProperty.bind(ontologTermLabelProperty.length().greaterThan(0));
        setInvalid();
        addMostCommandButton.setOnAction(e -> {
            List<String> commons = getTopTwoLabels();
            textField.setText(commons.getFirst());
        });
        add2MostCommandButton.setOnAction(e -> {
            List<String> commons = getTopTwoLabels();
            textField.setText(commons.get(1));
        });
    }

    private void setCommonLabel() {
        List<String> commons = getTopTwoLabels();
        this.addMostCommandButton.setText(commons.get(0));
        this.add2MostCommandButton.setText(commons.get(1));
    }

    private List<String> getTopTwoLabels() {
        if (termUsageCount.isEmpty()) {
            return List.of("", "");
        }
        List<String> sortedLabels = this.termUsageCount.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue())) // descending order
                .limit(2)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        if (sortedLabels.size() == 1) {
            sortedLabels.add("");
        }
        return sortedLabels;
    }

    private void incrementCommonLabel(String label) {
        this.termUsageCount.putIfAbsent(label, 0);
        this.termUsageCount.put(label, this.termUsageCount.get(label) + 1);
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
