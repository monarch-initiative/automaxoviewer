package org.monarchinitiative.automaxoviewer.controller.widgets;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provide simple dialogs to fetch data from the user
 */
public class UserStringFetcher {


    private static final String ORCID_REGEX =
            "^[0-9]{4}-[0-9]{4}-[0-9]{4}-[0-9]{4}$";

    /** Regular expression to check whether an input string is a valid ORCID id. */
    private static final Pattern ORCID_PATTERN = Pattern.compile(ORCID_REGEX);


    private static final String PUBMED_REGEX = "^PMID:\\d+";

    private static final Pattern PUBMED_PATTERN = Pattern.compile(PUBMED_REGEX);


    public static Optional<String> fetchORCID() {
        String toolTipText = "Enter ORCID identifier as 1234-1234-1234-1234. White space will be removed automatically";

        return fetch("Enter ORCID Identifier", "ORCID", toolTipText, ORCID_PATTERN);
    }

    public static Optional<String> fetchPubmed() {
        String toolTipText = "Enter PubMed identifier as PMID:123456. White space will be removed automatically";
        return fetch("Enter PMID", "PMID", toolTipText, PUBMED_PATTERN);
    }

    /**
     * This widget fetches a string from the user. It automatically removes white space.
     * @param windowTitle Title of dialog
     * @param labelText Explanatory text
     * @param regex regular expression that the text must fulfil to enable the "OK" button
     * @return an Optional of the string the user entered.
     */
    public static Optional<String> fetch(String windowTitle, String labelText, String toolTipText, Pattern regex) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(windowTitle);
        dialog.setHeaderText(null);
        dialog.setContentText(labelText);
        dialog.getEditor().textProperty().addListener( // ChangeListener
                (observable, oldValue, newValue) -> {
                    String txt = dialog.getEditor().getText();
                    txt = txt.replaceAll("\\s", "");
                    dialog.getEditor().setText(txt);
                });
        Tooltip tooltip_userName=new Tooltip(toolTipText);
        dialog.getEditor().setTooltip(tooltip_userName);
        BooleanProperty isValidProperty = new SimpleBooleanProperty();
        dialog.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            Matcher matcher = regex.matcher(newValue);
            isValidProperty.set(matcher.matches());
            dialog.getDialogPane().lookupButton(ButtonType.OK).setDisable(! isValidProperty.get());
        });
        return dialog.showAndWait();
    }


}
