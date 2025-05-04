package org.monarchinitiative.automaxoviewer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.HostServices;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.monarchinitiative.automaxoviewer.controller.widgets.PopUps;
import org.monarchinitiative.automaxoviewer.json.AutomaxoJson;
import org.monarchinitiative.automaxoviewer.json.TripletItem;
import org.monarchinitiative.automaxoviewer.model.*;
import org.monarchinitiative.automaxoviewer.view.*;

import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.MinimalOntology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

public class MainWindowController extends BaseController implements Initializable {
    private final Logger LOGGER = LoggerFactory.getLogger(MainWindowController.class);

    private final ObjectProperty<MinimalOntology> hpOntology = new SimpleObjectProperty<>();
    private final ObjectProperty<MinimalOntology> maxoOntology = new SimpleObjectProperty<>();
    private final ObjectProperty<MinimalOntology> mondoOntology = new SimpleObjectProperty<>();

    private final static String ANNOTATED_COLOR = "-fx-background-color: #baffba;";
    private final static String CANNOT_ANNOTATE_COLOR = "-fx-background-color: #ffd7d1;";

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
    public ChoiceBox<MaxoRelation> relationCB;
    @FXML
    public CheckBox diseaseLevelAnnotCheckBox;
    @FXML
    public Button nextAbstractButton;
    @FXML
    public OntologyTermAdder mondoTermAdder;
    @FXML
    public CheckBox diseaseLevelAnnotationCheckBox;
    @FXML
    public ChebiTermAdder chebiAdder;

    @FXML
    private VBox statusBar;
    @FXML
    public Label statusBarLabel;
    private StringProperty statusBarTextProperty;

    @FXML
    private TableView<AutoMaxoRow> automaxoTableView;
    @FXML
    public TableColumn<AutoMaxoRow, ItemStatus> imageStatusCol;
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

    private final Model model;

    private final Set<PoetOutputRow> outputRowSet;

    /**
     * This gets set to true once the Ontology tree has finished initiatializing. Before that
     * we can check to make sure the user does not try to open a disease before the Ontology is
     * done loading.
     */
    private final BooleanProperty ontologyLoadedProperty = new SimpleBooleanProperty(false);

    private final BooleanProperty robotIssueIsReadyProperty = new SimpleBooleanProperty(false);


    public MainWindowController(ViewFactory viewFactory, String fxmlName) {
        super(viewFactory, fxmlName);
        model = new Model();
        outputRowSet = new HashSet<>();
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
            LOGGER.info("Loading MAxO from {}", maxoJsonFile.getAbsolutePath());
            Task<MinimalOntology> maxoLoadTask = new Task<>() {
                @Override
                protected MinimalOntology call() {
                    MinimalOntology minOntology = OntologyLoader.loadOntology(maxoJsonFile);
                    LOGGER.info("Loaded MAxO, version {}", minOntology.version().orElse("n/a"));
                    maxoOntology.set(minOntology);
                    maxoTermAdder.setOntology(maxoOntology.get(), "MAXO");
                    return minOntology;
                }
            };
            maxoLoadTask.setOnSucceeded(e -> {
                maxoOntology.set(maxoLoadTask.getValue());
                maxoTermAdder.setOntology(this.maxoOntology.get(), "MAXO");
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
            LOGGER.info("Loading HPO from {}", hpJsonFilePath.getAbsolutePath());
            Task<MinimalOntology> hpoLoadTask = new Task<>() {
                @Override
                protected MinimalOntology call() {
                    MinimalOntology minOntology = OntologyLoader.loadOntology(hpJsonFilePath);
                    LOGGER.info("Loaded HPO, version {}", minOntology.version().orElse("n/a"));
                    hpOntology.set(minOntology);
                    hpoTermAdder.setOntology(hpOntology.get(), "HP");
                    return minOntology;
                }
            };
            hpoLoadTask.setOnSucceeded(e -> {
                hpOntology.set(hpoLoadTask.getValue());
                hpoTermAdder.setOntology(this.hpOntology.get(), "HP");
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
        LOGGER.info("Loading MONDO from {}.", mondoJsonFilePath.getAbsolutePath());
        if (mondoJsonFilePath != null && mondoJsonFilePath.isFile()) {
            Task<MinimalOntology> mondoLoadTask = new Task<>() {
                @Override
                protected MinimalOntology call() {
                    MinimalOntology minOntology = OntologyLoader.loadOntology(mondoJsonFilePath);
                    LOGGER.info("Loaded Mondo, version {}", minOntology.version().orElse("n/a"));
                    mondoOntology.set(minOntology);
                    mondoTermAdder.setOntology(mondoOntology.get(), "MONDO");
                    return minOntology;
                }
            };
            mondoLoadTask.setOnSucceeded(e -> {
                mondoOntology.set(mondoLoadTask.getValue());
                mondoTermAdder.setOntology(mondoOntology.get(), "MONDO");
            });
            mondoLoadTask.setOnFailed(e -> {
                LOGGER.warn("Could not load Mondo from {}", mondoJsonFilePath.getAbsolutePath());
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
        //termLabelValidator.setFieldLabel("New Term Label");
        setUpStatusBar();
        setUpKeyAccelerators();
        setupStatusBarOptions();
        loadOntologies();
        setUpTableView();
        setUpChoiceBox();
        //setUpChebiAdder();
    }

    private void setUpChebiAdder() {
        this.chebiAdder.setUp();
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
        this.relationCB.getItems().addAll(MaxoRelation.values());
        relationCB.setValue(MaxoRelation.TREATS);
        relationCB.setOnAction((event) -> {
            int selectedIndex = relationCB.getSelectionModel().getSelectedIndex();
            MaxoRelation selectedItem = relationCB.getSelectionModel().getSelectedItem();
            System.out.println("Selection made: [" + selectedIndex + "] " + selectedItem.toString());
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
        viewFactory.getOptions().mondoJsonFileProperty().addListener((obs, old, mondoJsonFilePath) -> loadMondo(mondoJsonFilePath));
        loadHpo(viewFactory.getOptions().getHpJsonFile());
        loadMaxo(viewFactory.getOptions().getMaxoJsonFile());
        loadMondo(viewFactory.getOptions().getMondoJsonFile());
        this.model.setOptions(viewFactory.getOptions());
        // set the labels
        hpoTermAdder.setLabel("HPO");
        maxoTermAdder.setLabel("MAxO");
        mondoTermAdder.setLabel("Mondo");
    }




    private void clearFields() {
        hpoTermAdder.clearFields();
        maxoTermAdder.clearFields();
        diseaseLevelAnnotCheckBox.setSelected(false);
        chebiAdder.clearFields();
        /* Don't clear Mondo -- we want to leave the correct disease */
    }




    private void setUpTableView() {

        Image successImage = null;
        Image failImage = null;
        try {
            failImage = new Image(getClass().getResourceAsStream("/img/fail.png"));
            successImage = new Image(getClass().getResourceAsStream("/img/success.png"));
        } catch (Exception e) {
            LOGGER.error("Error loading images: {}", e.getMessage());
        }

        automaxoTableView.setPlaceholder(new Text("Open automaxo file to see items"));
        imageStatusCol.setCellValueFactory(cd -> cd.getValue().getItemStatusProperty());
        Image finalSuccessImage = successImage;
        Image finalFailImage = failImage;
        final ImageView imageView = new ImageView();
        imageView.setFitHeight(25);
        imageView.setFitWidth(25);
        imageView.setPreserveRatio(true);
        imageStatusCol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(ItemStatus status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setGraphic(null);
                } else {
                    switch (status) {
                        case ItemStatus.ANNOTATED:
                            imageView.setImage(finalSuccessImage);
                            break;
                        case ItemStatus.CANNOT_ANNOTATE:
                            imageView.setImage(finalFailImage);
                            break;
                        default:
                            imageView.setImage(null);
                            break;
                    }
                    setGraphic(imageView);
                }
            }
        });

        maxoLabelCol.setCellValueFactory(cdf -> new ReadOnlyStringWrapper(cdf.getValue().maxoDisplay()));
        maxoLabelCol.setCellFactory((column) -> {
            final TableCell<AutoMaxoRow, String> cell = new TableCell<>();
            cell.itemProperty().addListener(// ChangeListener
                    (obs, oldValue, newValue) -> {
                        if (newValue != null) {
                            final ContextMenu cellMenu = new ContextMenu();
                            MenuItem ghMenuItem = new MenuItem("In progress");
                            ghMenuItem.setOnAction(e -> {
                                AutoMaxoRow item = cell.getTableRow().getItem();
                                item.setItemStatus(ItemStatus.IN_PROGRESS);
                                TableRow<AutoMaxoRow> currentRow = cell.getTableRow();
                                currentRow.setStyle("");
                            });
                            MenuItem summaryMenuItem = new MenuItem("annotate");
                            summaryMenuItem.setOnAction(e -> {
                                AutoMaxoRow item = cell.getTableRow().getItem();
                                if (! item.readyToBeAnnotated()) {
                                    PopUps.alertDialog("ERROR", "Current row not ready to be annotated");
                                    return;
                                }
                                annotateAutomaxoRow(item);
                                item.setItemStatus(ItemStatus.ANNOTATED);
                                TableRow<AutoMaxoRow> currentRow = cell.getTableRow();
                                currentRow.setStyle(ANNOTATED_COLOR);
                            });
                            MenuItem markedFinishedMenuItem = new MenuItem("Cannot annotate");
                            markedFinishedMenuItem.setOnAction(e -> {
                                AutoMaxoRow item = cell.getTableRow().getItem();
                                item.setItemStatus(ItemStatus.CANNOT_ANNOTATE);
                                TableRow<AutoMaxoRow> currentRow = cell.getTableRow();
                                item.setUnprocessable();
                                currentRow.setStyle(CANNOT_ANNOTATE_COLOR);
                            });
                            cellMenu.getItems().addAll(ghMenuItem, summaryMenuItem, markedFinishedMenuItem);
                            cell.setContextMenu(cellMenu);
                        }
                    }
            );
            cell.textProperty().bind(cell.itemProperty());
            return cell;
        });
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
            model.setCurrentRow(item);
            showRowInDetail(item);
        });

        // Set custom row factory to apply CSS style
        automaxoTableView.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(AutoMaxoRow amrow, boolean empty) {
                super.updateItem(amrow, empty);
                if (amrow == null || empty) {
                    setStyle("");
                } else if (amrow.isProcessed()) {
                    setStyle("-fx-background-color: lightgreen;");
                } else if (amrow.isUnprocessable()) {
                    setStyle("-fx-background-color: red;");
                } else if (amrow.pmidProcessed()) {
                    setStyle("-fx-background-color: powderblue;");
                } else {
                    setStyle("");
                }
            }
        });

    }

    private void showRowInDetail(AutoMaxoRow item) {
        String label = item.getCandidateHpoLabel();
        if (label.length() < 5) {
            label = item.getNonGroundedHpo();
        }
        this.hpoTermAdder.setOntologyLabelCandidate(label);
        label = item.getCandidateMaxoLabel();
        if (label.length() < 5) {
            label = item.getNonGroundedMaxo();
        }
        this.maxoTermAdder.setOntologyLabelCandidate(label);
        String relation = item.getRelationship();
        Optional<MaxoRelation> opt = MaxoRelation.fromString(relation);
        if (opt.isPresent()) {
            this.relationCB.setValue(opt.get());
        } else {
            this.relationCB.setValue(MaxoRelation.UNKNOWN);
        }
        viewCurrentItem(model);
    }

    /**
     * This method is called if the user clicks on a row of the ROBOT item table, and causes details from
     * that row to be shown in the bottom part of the GUI in a WebView widget.
     * param item The ROBOT item (table row) that the user has clicked on and thereby marked/brought into focus
     * <p>
     * private void showItemInTable(RobotItem item) {
     * WebEngine engine = this.currentRobotView.getEngine();
     * CurrentRobotItemVisualizer visualizer = new CurrentRobotItemVisualizer(viewFactory.getOptions());
     * String html = visualizer.toHTML(item);
     * engine.loadContent(html);
     * }
     */

    private void setupStatusBarOptions() {
        viewFactory.getOptions().isReadyProperty().addListener((obs, old, novel) -> {
            if (novel) {
                statusBarTextProperty.set("input data: ready");
                statusBarLabel.setTextFill(Color.BLACK);
                statusBarLabel.setFont(Font.font("Verdana", FontWeight.NORMAL, 12));
                if (!ontologyLoadedProperty.get()) {
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
     * Write the settings from the current session to file and exit.
     */
    @FXML
    private void exitGui() {
        javafx.application.Platform.exit();
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
        String mondo_json_version = "n/a";
        if (mondoOntology != null) {
            Optional<String> opt = mondoOntology.get().version();
            maxo_json_version = opt.orElse("could not retrieve version");
        }
        String msg = String.format("hp.json: %s, maxo.json: %s, mondo.json: %s",
                hpo_json_version, maxo_json_version, mondo_json_version);
        PopUps.alertDialog("Ontology versions", msg);
    }

    public void openAutoMAxO(ActionEvent e) {
        e.consume();
        File automaxoFile = PopUps.selectFileToOpen(null, new File("."), "AutoMAxO file");
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
        ObservableList<AutoMaxoRow> items = FXCollections.observableArrayList(rowList);
        javafx.application.Platform.runLater(() -> {
            LOGGER.trace("populateTable: got a total of {} Automaxo items", rowList.size());
            automaxoTableView.getItems().clear(); /* clear previous rows, if any */
            automaxoTableView.getItems().addAll(items);
            automaxoTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        });
    }




    public void viewNextAbstract(ActionEvent actionEvent) {
        viewCurrentItem(model);
    }


    public void viewCurrentItem(Model model) {
        AutoMaxoRow currentRow = model.getCurrentRow();
        WebEngine engine = this.currentAutoMaxoWebView.getEngine();
        Optional<Integer> nextAbstractCountOpt = model.getNextAbstractCount();
        var visualizer = new PmidAbstractTextVisualizer();
        String html;
        if (nextAbstractCountOpt.isEmpty()) {
            html = visualizer.getHtmlNoAbstract();
            engine.loadContent(html);
            return;
        }
        // The following is the index of the citation (PMID) we are viewing.
        int nextAbstractCount = nextAbstractCountOpt.get();
        Optional<Term> optHpo = this.hpoTermAdder.getTermIfValid();
        Term hpo = optHpo.orElse(null);
        Optional<Term> optMaxo = this.maxoTermAdder.getTermIfValid();
        Term maxo = optMaxo.orElse(null);

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
                model.getTotalAnnotationsToDate(),
                annotationFile,
                inputFile);
        html = visualizer.toHTML(visualisable, nextAbstractCount);
        engine.loadContent(html);
    }

    @FXML
    public void openAbstractInPubMed(ActionEvent e) {
        e.consume();
        Optional<String> opt = model.getCurrentPmid();
        final String urlPart = "https://pubmed.ncbi.nlm.nih.gov/";
        if (opt.isPresent()) {
            String url = urlPart + opt.get();
            LOGGER.trace("Opening PMID at {}", url);
            try {
                if (viewFactory.getHostervicesOpt().isPresent()) {
                    var services = viewFactory.getHostervicesOpt().get();
                    services.showDocument(url);
                } else {
                   LOGGER.error("Could not retrieve host services to open PMID");
                }
            } catch (InternalError ee) {
                LOGGER.error("Error opening system browser to show PMID {}", ee.getMessage());
            }
        }
    }








    private boolean annotateAutomaxoRow(AutoMaxoRow currentRow) {
        boolean diseaseLevel = this.diseaseLevelAnnotCheckBox.isSelected();
        Optional<Term> hpoTermOpt = this.hpoTermAdder.getTermIfValid();
        Optional<Term> maxoTermOpt = this.maxoTermAdder.getTermIfValid();
        Optional<Term> mondoTermOpt = this.mondoTermAdder.getTermIfValid();
        if (mondoTermOpt.isEmpty()) {
            PopUps.alertDialog("Error", "Cannot add annotation unless MONDO term is valid (green border)");
            return false;
        }
        if (hpoTermOpt.isEmpty() && ! diseaseLevel) {
            PopUps.alertDialog("Error", "Cannot add annotation unless HPO term is valid (green border) or diseaseLevel is selected");
            return false;
        }
        if (maxoTermOpt.isEmpty()) {
            PopUps.alertDialog("Error", "Cannot add annotation unless Maxo term is valid (green border)");
            return false;
        }
        MaxoRelation relation = this.relationCB.getValue();
        Term mondoTerm = mondoTermOpt.get();
        Term maxoTerm = maxoTermOpt.get();
        currentRow.setItemStatus(ItemStatus.ANNOTATED);
        Optional<Term> chebiOpt = this.chebiAdder.getChebiTerm();
        chebiOpt.ifPresent(currentRow::setChebi);
        currentRow.setMaxo(maxoTerm);
        if (diseaseLevel) {
            currentRow.setDiseaseLevel(true);
        } else {
            Term hpoTerm = hpoTermOpt.get();
            currentRow.setHpo(hpoTerm);
        }
        currentRow.setDiseaseTerm(mondoTerm);
        currentRow.setDiseaseLevel(diseaseLevel);
        currentRow.setMaxoRelation(relation);
        if (chebiOpt.isPresent()) {
            LOGGER.info("ChEBI was found, setting ChEBI in current row");
            currentRow.setChebi(chebiOpt.get());
        } else {
            LOGGER.info("No ChEBI found for current row");
        }
        String orcid = model.getOrcid().orElse("n/a");
        outputRowSet.addAll(currentRow.getPoetRows(orcid));
        LOGGER.info("Number of annotations so far {}", outputRowSet.size());
        long count = automaxoTableView.getItems().stream().filter(AutoMaxoRow::isAnnotated).count();
        LOGGER.info("Current row status {}", currentRow.getItemStatus());
        LOGGER.info("Total annotated items {}", count);
        currentRow.setProcessed(true);
        setAnnotatedPmid(currentRow);
        clearFields();
        return true;
    }


    public void setAnnotatedPmid(AutoMaxoRow row) {
        List<PubMedCitation> citations = row.getCitationList();
        Set<TermId> allCitedPmids = citations.stream().map(PubMedCitation::getPmidTermId).collect(Collectors.toSet());
        for (AutoMaxoRow arow : this.automaxoTableView.getItems()) {
            var rowcites = arow.getCitationList().stream().map(PubMedCitation::getPmidTermId).toList();
            for (TermId termId : rowcites) {
                if (allCitedPmids.contains(termId)) {
                    arow.setPmidProcessed();
                }
            }
        }
    }


    public void createAnnot(ActionEvent e) {
        e.consume();
        System.out.println("create annotation");
        if (automaxoTableView.getSelectionModel().getSelectedItems().isEmpty()) {
            LOGGER.warn("Attempt to create annotation with no row being selected");
            return;
        }
        // we can only mark one row at a time
        AutoMaxoRow row = automaxoTableView.getSelectionModel().getSelectedItems().getFirst();
        annotateAutomaxoRow(row);
    }

    @FXML
    public void exportAnnotationFile(ActionEvent actionEvent) {
        LOGGER.info("Exporting POET annotation file");
        List<PoetOutputRow> porList = new ArrayList<>(outputRowSet);
        Collections.sort(porList);
        List<String> outputrows = new ArrayList<>();
        outputrows.add(PoetOutputRow.getHeader());
        for (var row : porList) {
            outputrows.add(row.geTsvLine());
        }

        Optional<Window> opt = Stage.getWindows().stream().filter(Window::isShowing).findAny();
        Stage stage;
        stage = (Stage) opt.orElse(null);
        String home = System.getProperty("user.home");
        File f = PopUps.selectFileToSave(stage, new File(home), "Save maxo annotations", "maxo_DISEASE.tsv");
        try {
            Path filePath = Paths.get(f.getAbsolutePath());
            Files.deleteIfExists(filePath);
            Files.createFile(filePath);
            for (String str : outputrows) {
                Files.writeString(filePath, str + System.lineSeparator(),
                        StandardOpenOption.APPEND);
            }
        } catch (IOException e) {
            PopUps.alertDialog("Could not save Maxo annotations", e.getMessage());
        }
    }

    public void ntrMaxo(ActionEvent actionEvent) {
        Optional<HostServices> opt = viewFactory.getHostervicesOpt();
        if (opt.isPresent()) {
            AutoMaxoRow row = model.getCurrentRow();
            if (row == null) {
                PopUps.alertDialog("Error", "Mark row for this function");
                return;
            }
            Optional<Term> termOpt = row.maxoTerm();
            List<String> pmids = row.getAllPmids();
            String pmidString = String.join("; ", pmids);
            String bodyOfText;
            if (termOpt.isPresent()) {
                Term maxo = termOpt.get();
                bodyOfText = String.format("Revision for term %s (%s)\n%s",
                       maxo.getName(), maxo.id().getValue(), pmidString );
            } else {
                String candidate = row.getCandidateMaxoLabel();
                if (candidate.length() > 5) {
                    bodyOfText = String.format("NTR for %s\n%s", candidate, pmids);
                } else {
                    bodyOfText = String.format("NTR (could not parse candidate MAxO label)\n%s", pmids);
                }
            }
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(bodyOfText);
            clipboard.setContent(content);
            HostServices hostServices = opt.get();
            final String url = "https://github.com/monarch-initiative/MAxO/issues/";
            hostServices.showDocument(url);
        } else {
            LOGGER.error("Could not find maxo term for issues page");
        }
    }

    public void olsChEBI(ActionEvent actionEvent) {
        actionEvent.consume();
        Optional<HostServices> opt = viewFactory.getHostervicesOpt();
        if (opt.isPresent()) {
            var hostServices = opt.get();
            final String url = "https://www.ebi.ac.uk/ols4/ontologies/chebi";
            hostServices.showDocument(url);
        } else {
            LOGGER.error("Could not get HostServices instance");
        }

    }
}
