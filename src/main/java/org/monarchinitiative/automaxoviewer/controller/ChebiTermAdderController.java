package org.monarchinitiative.automaxoviewer.controller;

import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.fxml.FXML;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChebiTermAdderController implements Initializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChebiTermAdderController.class);
    @FXML
    private TextField keyField;

    @FXML
    private TextField valueField;

    @FXML
    private Button addPairButton;

    @FXML
    private Button clearButton;

    @FXML
    private ComboBox<String> choiceBox;

    private final List<Pair<String, String>> pairList = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addPairButton.setOnAction(e -> {
            String key = keyField.getText().trim();
            String value = valueField.getText().trim();

            if (!key.isEmpty() && !value.isEmpty()) {
                Pair<String, String> pair = new Pair<>(key, value);
                pairList.add(pair);

                String display = String.format("%s: %s", key, value);
                choiceBox.getItems().add(display);
            }
        });

        clearButton.setOnAction(e -> this.clearFields());

        choiceBox.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> {
            int index = newVal.intValue();
            if (index >= 0 && index < pairList.size()) {
                Pair<String, String> selectedPair = pairList.get(index);
                keyField.setText(selectedPair.getKey());
                valueField.setText(selectedPair.getValue());
            }
        });
    }


    public void clearFields() {
        this.keyField.clear();
        this.valueField.clear();
    }

    public void setUp() {
        clearFields();
    }

    public Optional<String> getIdentifier() {
        String identifier = this.keyField.getText();
        String chebiRegex = "^CHEBI:\\d+$";
        Pattern pattern = Pattern.compile(chebiRegex);
        Matcher matcher = pattern.matcher(identifier);
        if (matcher.matches()) {
            LOGGER.info("identifier '{}' matched", identifier);
            return Optional.of(identifier);
        } else {
            LOGGER.info("identifier '{}' did not match", identifier);
            return Optional.empty();
        }
    }

    public Optional<String> getLabel() {
        String label = this.valueField.getText();
        label = label.trim();
        if (label.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(label);
        }
    }
}
