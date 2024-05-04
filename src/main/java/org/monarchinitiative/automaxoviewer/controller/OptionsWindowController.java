package org.monarchinitiative.automaxoviewer.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import org.monarchinitiative.automaxoviewer.controller.widgets.FileDownloader;
import org.monarchinitiative.automaxoviewer.controller.widgets.Platform;
import org.monarchinitiative.automaxoviewer.controller.widgets.UserStringFetcher;
import org.monarchinitiative.automaxoviewer.model.Options;
import org.monarchinitiative.automaxoviewer.view.ViewFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

public class OptionsWindowController extends BaseController implements Initializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(OptionsWindowController.class);

    private static final String NOT_INITIALIZED = "not initialized";

    private static final String N_A = "na";
    @FXML
    public Label hpSrcOntologyLabel;
    @FXML
    public Button hpJsonButton;
    @FXML
    public Button orcidButton;
    @FXML
    public Button hpSrcOntoButton;
    @FXML
    public Button okButton;
    @FXML
    public Button cancelButton;
    public VBox buttonBox;
    @FXML
    private Label hpJsonLabel;
    @FXML
    private Label orcidLabel;

    private final StringProperty hpJsonProperty;

    private final StringProperty hpSrcOntologyProperty;

    private final StringProperty orcidProperty;

    private Options options;


    @FXML
    void okButtonAction() {
        Stage stage = (Stage) this.orcidLabel.getScene().getWindow();
        viewFactory.closeStage(stage);
    }

    @FXML
    void cancelButtonAction() {
        Stage stage = (Stage) this.orcidLabel.getScene().getWindow();
        viewFactory.closeStage(stage);
    }

    public OptionsWindowController(ViewFactory viewFactory, String fxmlName) {
        super(viewFactory, fxmlName);
        hpJsonProperty = new SimpleStringProperty(NOT_INITIALIZED);
        hpSrcOntologyProperty = new SimpleStringProperty(NOT_INITIALIZED);
        orcidProperty = new SimpleStringProperty(NOT_INITIALIZED);
        // initialize options that were saved in the $HOME/.hpo2robot.txt file
        Map<String, String> opts = Options.readOptions();
        for (var e : opts.entrySet()) {
            switch (e.getKey()) {
                case Options.HP_JSON_KEY -> hpJsonProperty.set(e.getValue());
                case Options.ORCID_KEY -> orcidProperty.set(e.getValue());
                case Options.HP_SRC_DIRECTORY -> hpSrcOntologyProperty.set(e.getValue());
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        hpJsonLabel.textProperty().bind(hpJsonProperty);
        hpSrcOntologyLabel.textProperty().bind(hpSrcOntologyProperty);
        orcidLabel.textProperty().bind(orcidProperty);
        buttonBox.setSpacing(10);
        setupCss();
    }

    private static final String BUTTON_CSS = """
            #bevel-grey {
                -fx-background-color:\s
                    linear-gradient(#f2f2f2, #d6d6d6),
                    linear-gradient(#fcfcfc 0%, #d9d9d9 20%, #d6d6d6 100%),
                    linear-gradient(#dddddd 0%, #f6f6f6 50%);
                -fx-background-radius: 8,7,6;
                -fx-background-insets: 0,1,2;
                -fx-text-fill: black;
                -fx-pref-width: 100;
                -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );
            }
            """;

    private void setupCss() {
        hpJsonButton.setStyle(BUTTON_CSS);
        hpSrcOntoButton.setStyle(BUTTON_CSS);
        orcidButton.setStyle(BUTTON_CSS);
        okButton.setStyle(BUTTON_CSS);
        cancelButton.setStyle(BUTTON_CSS);
    }

    /**
     * Download the hp.json file to $HOME/.hpo2robot/hp.json
     */
    public void downloadJson(ActionEvent e) {
        e.consume();
        File hpo2robotDir = Platform.getHpo2RobotDir();
        Path hpJsonPath = Paths.get(String.valueOf(hpo2robotDir), "hp.json");

        try {
            URL hpoJson = new URL("http://purl.obolibrary.org/obo/hp.json");
            FileDownloader downloader = new FileDownloader();
            downloader.copyURLToFile(hpoJson, hpJsonPath.toFile());
            } catch (MalformedURLException ex) {
            LOGGER.error("Could not download hp.json: {}", ex.getMessage());
                throw new RuntimeException(ex);
        }
        // if we get here, download was successful
        hpJsonProperty.set(hpJsonPath.toFile().getAbsolutePath());
    }



    public void setORCID(ActionEvent e) {
        e.consume();
        Optional<String> opt = UserStringFetcher.fetchORCID();
        if (opt.isPresent()) {
            String orcid = opt.get();
            //this.options.setOrcid(orcid);
            this.orcidProperty.set(orcid);
        } else {
            LOGGER.warn("Could not retrieve ORCID from user");
        }
    }

    public Optional<Options> getOptions() {
        Options options =  new Options(hpJsonProperty.get(), orcidProperty.get(),
                hpSrcOntologyProperty.get());
        if (options.isValid()) {
            return Optional.of(options);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Set the path to the cloned GitHub repository human-phenotype-ontology/src/ontology
     * Note that the repository must be up-to-date (pull) to avoid BAD THINGS.
     */
    public void setHpSrcOntology(ActionEvent e) {
        e.consume();
        Optional<File> opt = setDirectory("Set hpo/src/ontology folder");
        if (opt.isPresent()) {
            File f = opt.get();
           // this.options.setHpSrcOntologyDir(f);
            this.hpSrcOntologyProperty.set(f.getAbsolutePath());
        }
    }


    private Optional<File> setFile(String title, String extensionFileDisplay, String suffix) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(extensionFileDisplay, suffix));
        Stage stage = (Stage) this.hpJsonLabel.getScene().getWindow();
        File f = fileChooser.showOpenDialog(stage);
        return Optional.ofNullable(f);
    }

    private Optional<File> setDirectory(String title) {
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle(title);
        Stage stage = (Stage) this.hpJsonLabel.getScene().getWindow();
        File f = dirChooser.showDialog(stage);
        return Optional.ofNullable(f);
    }


    public void setCurrentOptions(Options options) {
        //this.options = options;
        this.hpJsonProperty.set(options.getHpJsonFile().getAbsolutePath());
        this.hpSrcOntologyProperty.set(options.getHpSrcOntologyDir().getAbsolutePath());
        this.orcidProperty.set(options.getOrcid());
    }



}
