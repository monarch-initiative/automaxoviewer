package org.monarchinitiative.automaxoviewer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.HostServices;
import javafx.beans.property.*;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Window;
import org.monarchinitiative.automaxoviewer.controller.widgets.PopUps;
import org.monarchinitiative.automaxoviewer.json.AutomaxoJson;
import org.monarchinitiative.automaxoviewer.json.TripletItem;
import org.monarchinitiative.automaxoviewer.model.AutoMaxoRow;
import org.monarchinitiative.automaxoviewer.model.Model;
import org.monarchinitiative.automaxoviewer.view.CurrentItemVisualizable;
import org.monarchinitiative.automaxoviewer.view.OntologyTermAdder;
import org.monarchinitiative.automaxoviewer.view.PmidAbstractTextVisualizer;
import org.monarchinitiative.automaxoviewer.view.ViewFactory;

import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.MinimalOntology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainWindowController extends BaseController implements Initializable {
    private final Logger LOGGER = LoggerFactory.getLogger(MainWindowController.class);

    private final ObjectProperty<MinimalOntology> hpOntology = new SimpleObjectProperty<>();
    private final ObjectProperty<MinimalOntology> maxoOntology = new SimpleObjectProperty<>();
    private final ObjectProperty<MinimalOntology> mondoOntology = new SimpleObjectProperty<>();

    @FXML
    public MenuItem newMenuItem;
    @FXML
    public MenuItem exitMenuItem;
    @FXML
    public MenuItem optionsMenuItem;
    @FXML
    public Menu templatesMenu;
    @FXML
    public WebView currentAutoMaxoWebView;
    @FXML
    public ChoiceBox<String> relationCB;
    @FXML
    public CheckBox diseaseLevelAnnotationCheckBBox;
    @FXML
    public Button nextAbstractButton;
    @FXML
    public OntologyTermAdder mondoTermAdder;

    @FXML
    private VBox statusBar;
    @FXML
    public Label statusBarLabel;
    private StringProperty statusBarTextProperty;
    private Optional<HostServices> hostServicesOpt;

    @FXML
    private TableView<AutoMaxoRow> automaxoTableView;
    @FXML
    private TableColumn<AutoMaxoRow, String> hpoLabelCol;
    @FXML
    private TableColumn<AutoMaxoRow, String> maxoLabelCol;
    @FXML
    private TableColumn<AutoMaxoRow, String> relationCol;
    @FXML
    private TableColumn<AutoMaxoRow, String> pmidCountCol;
    @FXML
    private TableColumn<AutoMaxoRow, String> mondoLabelCol;
    @FXML
    private OntologyTermAdder maxoTermAdder;
    @FXML
    private OntologyTermAdder hpoTermAdder;

   private Model model;

    /** This gets set to true once the Ontology tree has finished initiatializing. Before that
     * we can check to make sure the user does not try to open a disease before the Ontology is
     * done loading.
     */
    private final BooleanProperty ontologyLoadedProperty = new SimpleBooleanProperty(false);

    private final BooleanProperty robotIssueIsReadyProperty = new SimpleBooleanProperty(false);


    public MainWindowController(ViewFactory viewFactory, String fxmlName) {
        super(viewFactory, fxmlName);
       model = new Model();
    }

    @FXML
    void optionsAction() {
        this.viewFactory.showOptionsWindow();
    }



    /**
     * This method should be called after we have validated that the three
     * files needed in the Options are present and valid. This method then
     * loads the HPO Ontology object and uses it to set up the Ontology Tree
     * browser on the left of the GUI.
*/
    private void loadMaxo(File maxoJsonFile) {
        if (maxoJsonFile != null && maxoJsonFile.isFile()) {
            Task<MinimalOntology> maxoLoadTask = new Task<>() {
                @Override
                protected MinimalOntology call() {
                    MinimalOntology minOntology = OntologyLoader.loadOntology(maxoJsonFile);
                    LOGGER.info("Loaded MAxO, version {}", minOntology.version().orElse("n/a"));
                    maxoOntology.set(minOntology);
                    maxoTermAdder.setOntology(maxoOntology.get());
                    return minOntology;
                }
            };
            maxoLoadTask.setOnSucceeded(e -> {
                maxoOntology.set(maxoLoadTask.getValue());
                maxoTermAdder.setOntology(this.maxoOntology.get());
            });
            maxoLoadTask.setOnFailed(e -> {
                LOGGER.warn("Could not load MAxO from {}", maxoJsonFile.getAbsolutePath());
                maxoOntology.set(null);
            });
            Thread thread = new Thread(maxoLoadTask);
            thread.start();
        } else {
            maxoOntology.set(null);
        }
    }


    private void loadHpo(File hpJsonFilePath) {
        if (hpJsonFilePath != null && hpJsonFilePath.isFile()) {
            Task<MinimalOntology> hpoLoadTask = new Task<>() {
                @Override
                protected MinimalOntology call() {
                    MinimalOntology hpoOntology = OntologyLoader.loadOntology(hpJsonFilePath);
                    LOGGER.info("Loaded HPO, version {}", hpoOntology.version().orElse("n/a"));
                    hpoTermAdder.setOntology(hpOntology.get());
                    return hpoOntology;
                }
            };
            hpoLoadTask.setOnSucceeded(e -> {
                hpOntology.set(hpoLoadTask.getValue());
                hpoTermAdder.setOntology(this.hpOntology.get());
            });
            hpoLoadTask.setOnFailed(e -> {
                LOGGER.warn("Could not load HPO from {}", hpJsonFilePath.getAbsolutePath());
                hpOntology.set(null);
            });
            Thread thread = new Thread(hpoLoadTask);
            thread.start();
        } else {
            hpOntology.set(null);
        }
    }

    private void loadMondo(File mondoJsonFilePath) {
        if (mondoJsonFilePath != null && mondoJsonFilePath.isFile()) {
            Task<MinimalOntology> mondoLoadTask = new Task<>() {
                @Override
                protected MinimalOntology call() {
                    MinimalOntology hpoOntology = OntologyLoader.loadOntology(mondoJsonFilePath);
                    LOGGER.info("Loaded HPO, version {}", hpoOntology.version().orElse("n/a"));
                    hpoTermAdder.setOntology(hpOntology.get());
                    return hpoOntology;
                }
            };
            mondoLoadTask.setOnSucceeded(e -> {
                mondoOntology.set(mondoLoadTask.getValue());
                mondoTermAdder.setOntology(this.mondoOntology.get());
            });
            mondoLoadTask.setOnFailed(e -> {
                LOGGER.warn("Could not load HPO from {}", mondoJsonFilePath.getAbsolutePath());
                mondoOntology.set(null);
            });
            Thread thread = new Thread(mondoLoadTask);
            thread.start();
        } else {
            mondoOntology.set(null);
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        LOGGER.trace("Initializing MainWindowController");
        this.hostServicesOpt = this.viewFactory.getHostervicesOpt();

        //termLabelValidator.setFieldLabel("New Term Label");
        setUpStatusBar();
        setUpKeyAccelerators();
        setupStatusBarOptions();
        loadOntologies();
        setUpTableView();
        setUpChoiceBox();
        setupRobotItemHandlers();
        setUpNewTermReadiness();
    }

    /**
     * choose one of the MAxO relations
     * Treats	the medical action can be used to treat the phenotypic feature indicated by the HPO term in persons with the indicated disease
     * Prevents	the medical action can be used to prevent the phenotypic feature indicated by the HPO term in persons with the indicated disease
     * Investigates	the medical action can be used to investigate the phenotypic feature indicated by the HPO term in persons with the indicated disease
     * Contraindicated	the medical action should not be undertaken without consideration of the particular risk of harm to persons with the indicated disease
     * Lack of observed response
     */
    private void setUpChoiceBox() {
        this.relationCB.getItems().add("treats");
        this.relationCB.getItems().add("prevents");
        this.relationCB.getItems().add("investigates");
        this.relationCB.getItems().add("contraindicated");
        this.relationCB.getItems().add("lack of observed response");
        this.relationCB.getItems().add("unknown");
        relationCB.setValue("unknown");
        relationCB.setOnAction((event) -> {
            int selectedIndex = relationCB.getSelectionModel().getSelectedIndex();
            String selectedItem = relationCB.getSelectionModel().getSelectedItem();
            System.out.println("Selection made: [" + selectedIndex + "] " + selectedItem);
        });

    }

    /**
     * This method should be called after we have validated that the three
     * files needed in the Options are present and valid. This method then
     * loads the HPO Ontology object and uses it to set up the Ontology Tree
     * browser on the left of the GUI.
     */
    private void loadOntologies() {
        // Setup event handlers to update HPO in case the user changes path to another one
        viewFactory.getOptions().hpJsonFileProperty().addListener((obs, old, hpJsonFilePath) -> loadHpo(hpJsonFilePath));
        viewFactory.getOptions().maxoJsonFileProperty().addListener((obs, old, maxoJsonFilePath) -> loadMaxo(maxoJsonFilePath));
        viewFactory.getOptions().mondoJsonFileProperty().addListener((obs,old, mondoJsonFilePath) -> loadMondo(mondoJsonFilePath));
        loadHpo(viewFactory.getOptions().getHpJsonFile());
        loadMaxo(viewFactory.getOptions().getMaxoJsonFile());
        this.model.setOptions(viewFactory.getOptions());
        // set the labels
        hpoTermAdder.setLabel("HPO");
        maxoTermAdder.setLabel("MAxO");
        mondoTermAdder.setLabel("Mondo");
    }

    /**
     * We are ready to enter a new maxo curation item if we have a valid label, at least one parent term
     * and a valid definition. Here, we bind the new ROBOT item button to the
     * three corresponding Boolean properties.
     */
    private void setUpNewTermReadiness() {
        /*
         BooleanBinding readyBinding = hpoTermAdder.parentTermsReady()
                .and(termLabelValidator.getIsValidProperty())
                .and(definitionPane.isReadyProperty());
        this.robotIssueIsReadyProperty.bind(readyBinding);
        this.addNewHpoTermBox.bindNewRobotItemButton(robotIssueIsReadyProperty);
        IntegerBinding robotTableSizeBinding = Bindings.size(robotTableView.getItems());
        BooleanProperty tableReadyProperty = new SimpleBooleanProperty();
        tableReadyProperty.bind(robotTableSizeBinding.greaterThan(0));
        this.addNewHpoTermBox.bindWriteRobotFileButton(tableReadyProperty);
        */
    }



    private void clearFields() {
        hpoTermAdder.clearFields();
        maxoTermAdder.clearFields();
        /*
        this.termLabelValidator.clearFields();

        this.definitionPane.clearFields();
        this.pmidXrefAdderBox.clearFields();
        this.addNewHpoTermBox.clearFields();

         */
    }

    /**
     * This method uses to the data entered by the user to add another ROBOT item to the table
     */
    private void createNewRobotItem() {
       /*
        String newHpoLabel = termLabelValidator.getLabel().get();
        var hpo = hpOntology.get();
        boolean duplicated = hpo.getTerms().stream()
                .map(Term::getName)
                .anyMatch(t -> t.equals(newHpoLabel));
        if (duplicated) {
            PopUps.alertDialog("Error",
                    String.format("%s already present in ontology.", newHpoLabel));
            return;
        }
        model.setHpoTermLabel(newHpoLabel);
        model.setDefinition(this.definitionPane.getDefinition());
        model.setparentTerms(parentTermAdder.getParentTermList());
        model.setComment(this.definitionPane.getComment());
        model.setPmidList(pmidXrefAdderBox.getPmidList());
        model.setSynonymList(pmidXrefAdderBox.getSynonymList());
        Optional<String> opt = gitHubIssueBox.getGitHubIssueNumber();
        opt.ifPresent(model::setGitHubIssue);
        Optional<String> customOrcidOpt = pmidXrefAdderBox.getCustomOrcidOpt();
        customOrcidOpt.ifPresent(model::setOrcid);
        Optional<RobotItem> itemOpt = model.getRobotItemOpt();
        if (itemOpt.isPresent()) {
            model.reset();
            robotTableView.getItems().add(itemOpt.get());
        } else {
            PopUps.alertDialog("Error", "Could not create ROBOT Item");
        }
*/

    }


    private void setUpTableView() {
        automaxoTableView.setPlaceholder(new Text("Open automaxo file to see items"));
        // automaxoTableView.setEditable(false); ??

        maxoLabelCol.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().maxoDisplay()));
        maxoLabelCol.setCellFactory(TextFieldTableCell.forTableColumn());
        maxoLabelCol.setEditable(true);
        relationCol.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().getRelationship()));
        relationCol.setCellFactory(TextFieldTableCell.forTableColumn());
        relationCol.setEditable(true);

        hpoLabelCol.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().hpoDisplay()));
        hpoLabelCol.setCellFactory(TextFieldTableCell.forTableColumn());
        hpoLabelCol.setEditable(true);

        pmidCountCol.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(String.valueOf(cdf.getValue().getCount())));
        pmidCountCol.setCellFactory(TextFieldTableCell.forTableColumn());
        pmidCountCol.setEditable(false);


        mondoLabelCol.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().mondoDisplay()));
        mondoLabelCol.setCellFactory(TextFieldTableCell.forTableColumn());
        mondoLabelCol.setEditable(true);

        automaxoTableView.setOnMouseClicked(e -> {
            AutoMaxoRow item = automaxoTableView.getSelectionModel().getSelectedItems().getFirst();
            showRowInDetail(item);
            model.setCurrentRow(item);
        });
    }

    private void showRowInDetail(AutoMaxoRow item) {
        System.out.println("SHOW IN DETAI " + item);
        String label = item.getHpoLabel();
        if (label.length() < 5) {
            label = item.getNonGroundedHpo();
        }
        this.hpoTermAdder.setOntologyLabelCandidate(label);
        label = item.getMaxoLabel();
        if (label.length() < 5) {
            label = item.getNonGroundedMaxo();
        }
        this.maxoTermAdder.setOntologyLabelCandidate(label);
        String relation = item.getRelationship();

    }

    /**
     * This method is called if the user clicks on a row of the ROBOT item table, and causes details from
     * that row to be shown in the bottom part of the GUI in a WebView widget.
     * param item The ROBOT item (table row) that the user has clicked on and thereby marked/brought into focus

    private void showItemInTable(RobotItem item) {
        WebEngine engine = this.currentRobotView.getEngine();
        CurrentRobotItemVisualizer visualizer = new CurrentRobotItemVisualizer(viewFactory.getOptions());
        String html = visualizer.toHTML(item);
        engine.loadContent(html);
    }  */

    private void setupStatusBarOptions() {
        viewFactory.getOptions().isReadyProperty().addListener((obs, old, novel) -> {
            if (novel) {
                statusBarTextProperty.set("input data: ready");
                statusBarLabel.setTextFill(Color.BLACK);
                statusBarLabel.setFont(Font.font("Verdana", FontWeight.NORMAL, 12));
                if (! ontologyLoadedProperty.get()) {
                    statusBarTextProperty.set("hp.json not loaded.");
                    statusBarLabel.setTextFill(Color.RED);
                    statusBarLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
                }
            } else {
                statusBarTextProperty.set(viewFactory.getOptions().getErrorMessage());
                statusBarLabel.setTextFill(Color.RED);
                statusBarLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
            }
        });
    }

    private void setUpStatusBar() {
        statusBarTextProperty = new SimpleStringProperty("Starting");
        statusBar.setStyle("-fx-background-color: gainsboro");
        statusBar.setMinHeight(30);
        statusBar.setPadding(new Insets(10, 50, 10, 50));
        statusBar.setSpacing(10);
        statusBarLabel.textProperty().bind(statusBarTextProperty);
        statusBarLabel.setTextFill(Color.BLACK);
        statusBarLabel.setFont(Font.font("Verdana", FontWeight.NORMAL, 12));
    }


    private void setUpKeyAccelerators() {
        newMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.META_DOWN));
        exitMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.X, KeyCombination.META_DOWN));
        optionsMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.META_DOWN));
    }

    /**
     * This gets called as the "hook" for the OntologyTree widget
     * @param phenotypeTerm The term that is shown in the OntologyTree widget
     */
    private void addPhenotypeTerm(Term phenotypeTerm) {
        LOGGER.trace("Adding parent term from ontology tree: {}", phenotypeTerm);
       // parentTermAdder.setParentTerm(phenotypeTerm.getName());
    }


    /**
     * Write the settings from the current session to file and exit.
     */
    @FXML
    private void exitGui() {
        javafx.application.Platform.exit();
    }


    /**
     * Set up handlers for the three buttons on the new ROBOT item box
     */
    private void setupRobotItemHandlers() {
        EventHandler<ActionEvent> handler = actionEvent -> {
            createNewRobotItem();
            clearFields();
        };
        /*
        this.addNewHpoTermBox.setCreateNewRobotItemAction(handler);
        EventHandler<ActionEvent> clearHandler = actionEvent -> {
            Stage stage = (Stage) this.addNewHpoTermBox.getScene().getWindow();
            boolean OK = BooleanRetrieverWidget.getBooleanFromUser("Are you sure you want to clear the ROBOT item table?",
                    "Choose OK to permanently delete the data in the ROBOT item table",
                    "Warning",
                    stage);
            if (OK) {
                this.robotTableView.getItems().clear();
            }
        };
        this.addNewHpoTermBox.setClearRobotAction(clearHandler);
        EventHandler<ActionEvent> exportHandler = actionEvent -> {
            Optional<File> opt = this.model.getRobotSaveFileOpt();
            if (opt.isPresent()) {
                RobotItem.exportRobotItems(robotTableView.getItems(), opt.get());
            } else {
                PopUps.showInfoMessage("Error", "Could not set ROBOT export file");

            }
        };
        this.addNewHpoTermBox.setExportRobotAction(exportHandler);
        EventHandler<ActionEvent> clearRobotFileHandler = actionEvent -> {
            Optional<File> opt = model.getHpoSrcDir();
            if (opt.isPresent()) {
                File hpoSrcDir = opt.get();
                RobotRunner runner = new RobotRunner(hpoSrcDir);
                runner.clearRobotFile();
            } else {
                PopUps.showInfoMessage("Error", "Could not set ROBOT export file");
            }
        };
        this.addNewHpoTermBox.setClearRobotFileHandler(clearRobotFileHandler);
        EventHandler<ActionEvent> copyRobotCommandHandler = actionEvent -> {
            Optional<File> opt = model.getHpoSrcDir();
            if (opt.isPresent()) {
                File hpoSrcDir = opt.get();
                RobotRunner runner = new RobotRunner(hpoSrcDir);
                String command = runner.getCommandString();
                final Clipboard clipboard = Clipboard.getSystemClipboard();
                final ClipboardContent content = new ClipboardContent();
                content.putString(command);
                clipboard.setContent(content);
                LOGGER.trace(command);
            } else {
                PopUps.showInfoMessage("Error", "Could not get ROBOT command");
                LOGGER.error("Could not get ROBOT command");
            }
        };
        this.addNewHpoTermBox.copyRobotCommandAction(copyRobotCommandHandler);

         */
    }


    /**
     * Open a window and show the versions of HPO and MAxO
     */
    @FXML
    public void showVersionsAction(ActionEvent e) {
        e.consume();
        String hpo_json_version = "n/a";
        if (hpOntology != null) {
            Optional<String> opt = hpOntology.get().version();
            hpo_json_version = opt.orElse("could not retrieve version");
        }
        String maxo_json_version = "n/a";
        if (maxoOntology != null) {
            Optional<String> opt = maxoOntology.get().version();
            maxo_json_version = opt.orElse("could not retrieve version");
        }
        String msg = String.format("hp.json: %s, maxo.json: %s", hpo_json_version,maxo_json_version);
        PopUps.alertDialog("Ontology versions", msg);
    }

    public void openAutoMAxO(ActionEvent e) {
        e.consume();
        File automaxoFile = PopUps.selectFileToOpen(null, new File("."), "AutoMAxO file" );
        if (automaxoFile != null && automaxoFile.isFile()) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                AutomaxoJson automaxo = objectMapper.readValue(automaxoFile, AutomaxoJson.class);
                this.model.setTripletItemList(automaxo);
                this.model.setAutomaxoFile(automaxoFile);
                populateTable();
            } catch (Exception exc) {
                PopUps.showException("Error", exc.getMessage(), "Could not read JSON", exc);
            }
            String msg = String.format("Got %d automaxo items", model.getTripletItemList().size());
            PopUps.alertDialog("Success", msg);
            LOGGER.info(msg);
        } else {
            PopUps.alertDialog("Warning", "Could not get automaxo file");
        }
    }

    private void populateTable() {
        List<TripletItem> tilist = model.getTripletItemList();
        List<AutoMaxoRow> rowList = tilist.stream().map(AutoMaxoRow::new).toList();
        javafx.application.Platform.runLater(() -> {
            LOGGER.trace("populateTable: got a total of {} Automaxo items", rowList.size());
            automaxoTableView.getItems().clear(); /* clear previous rows, if any */
            automaxoTableView.getItems().addAll(rowList);
            automaxoTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        });
    }


    public void createAnnot(ActionEvent actionEvent) {
        System.out.println("create annotation");
    }

    public void viewNextAbstract(ActionEvent actionEvent) {
        viewCurrentItem(model);
    }


    public void viewCurrentItem(Model model) {
        AutoMaxoRow currentRow = model.getCurrentRow();
        WebEngine engine = this.currentAutoMaxoWebView.getEngine();
        Optional<Integer> opt = model.getNextAbstractCount();
        var visualizer = new PmidAbstractTextVisualizer();
        String html;
        if (opt.isEmpty()) {
            html = visualizer.getHtmlNoAbstract();
            engine.loadContent(html);
            return;
        }
        Optional<Term> optHpo = this.hpoTermAdder.getTermIfValid();
        Term hpo = optHpo.orElse(null);
        Optional<Term> optMaxo = this.maxoTermAdder.getTermIfValid();
        Term maxo = optMaxo.orElse(null);
        int totalAnnotationsToDate = 42;
        Optional<File> optAnnotFile = model.getAnnotationFile();
        String annotationFile;
        annotationFile = optAnnotFile.map(File::getAbsolutePath).orElse("n/a");
        Optional<File> automaxoFileOpt = this.model.getAutomaxoFile();
        String inputFile;
        inputFile = automaxoFileOpt.map(File::getAbsolutePath).orElse("n/a");
        Optional<Term> optDisease = Optional.empty();
        Term mondo = optDisease.orElse(null);
        CurrentItemVisualizable visualisable = new CurrentItemVisualizable(currentRow,
                hpo,
                maxo,
                mondo,
                totalAnnotationsToDate,
                annotationFile,
                inputFile);
        html = visualizer.toHTML(visualisable, opt.get());
        engine.loadContent(html);
    }

    @FXML
    public void openAnnotationFile(ActionEvent actionEvent) {
        Window stage = this.relationCB.getScene().getWindow();
        Optional<File> opt = PopUps.selectOrCreateInputFile(stage);
        if (opt.isPresent()) {
            File annotFile = opt.get();
            model.setAnnotationFile(annotFile);
        }
    }
}
