package org.monarchinitiative.automaxoviewer.controller.widgets;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Optional;

public class BooleanRetrieverWidget {

    /**
     * Ask user a boolean question and get an answer.
     *
     * @param windowTitle Title of PopUp window
     * @return true or false response from user
     */
    public static boolean getBooleanFromUser(String question, String headerText, String windowTitle, Stage owner) {
        Alert al = new Alert(Alert.AlertType.CONFIRMATION);
        al.setTitle(windowTitle);
        al.setHeaderText(headerText);
        al.setContentText(question);
        Optional<ButtonType> result = al.showAndWait();
        al.initOwner(owner);
        Stage stage = (Stage) al.getDialogPane().getScene().getWindow();
        stage.setAlwaysOnTop(true);
        stage.initModality(Modality.APPLICATION_MODAL);
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}
