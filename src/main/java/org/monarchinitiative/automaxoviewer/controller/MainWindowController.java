package org.monarchinitiative.automaxoviewer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.monarchinitiative.automaxoviewer.view.CurrentItemVisualizable;
import org.monarchinitiative.automaxoviewer.view.OntologyTermAdder;
import org.monarchinitiative.automaxoviewer.view.PmidAbstractTextVisualizer;
import org.monarchinitiative.automaxoviewer.view.ViewFactory;

import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.MinimalOntology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

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
    public ChoiceBox<MaxoRelation> relationCB;
    @FXML
    public CheckBox diseaseLevelAnnotationCheckBBox;
    @FXML
    public Button nextAbstractButton;
    @FXML
    public OntologyTermAdder mondoTermAdder;
    @FXML
    public CheckBox diseaseLevelAnnotationCheckBox;


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
            Task<MinimalOntology> hpoLoadTask = new Task<>() {
                @Override
                protected MinimalOntology call() {
                    MinimalOntology hpoOntology = OntologyLoader.loadOntology(hpJsonFilePath);
                    LOGGER.info("Loaded HPO, version {}", hpoOntology.version().orElse("n/a"));
                    hpoTermAdder.setOntology(hpOntology.get(), "HPO");
                    return hpoOntology;
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
        LOGGER.info("Loading mondo from {}.", mondoJsonFilePath.getAbsolutePath());
        if (mondoJsonFilePath != null && mondoJsonFilePath.isFile()) {
            Task<MinimalOntology> mondoLoadTask = new Task<>() {
                @Override
                protected MinimalOntology call() {
                    MinimalOntology hpoOntology = OntologyLoader.loadOntology(mondoJsonFilePath);
                    LOGGER.info("Loaded Mondo, version {}", hpoOntology.version().orElse("n/a"));
                    hpoTermAdder.setOntology(hpOntology.get(), "MONDO");
                    return hpoOntology;
                }
            };
            mondoLoadTask.setOnSucceeded(e -> {
                mondoOntology.set(mondoLoadTask.getValue());
                mondoTermAdder.setOntology(this.mondoOntology.get(), "MONDO");
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
        /* Don't clear Mondo -- we want to leave the correct disease */
    }




    private void setUpTableView() {
        final String ANNOTATED_COLOR = "-fx-background-color: #baffba;";
        final String CANNOT_ANNOTATE_COLOR = "-fx-background-color: #ffd7d1;";
        Image successImage = null;
        Image failImage = null;
        try {
            failImage = new Image(getClass().getResourceAsStream("/img/fail.png"));
            successImage = new Image(getClass().getResourceAsStream("/img/success.png"));
        } catch (Exception e) {
            e.printStackTrace();
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
            showRowInDetail(item);
            model.setCurrentRow(item);
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
    public void openAnnotationFile(ActionEvent e) {
        e.consume();
        Window stage = this.relationCB.getScene().getWindow();
        Optional<File> opt = PopUps.openInputFile(stage, "*.ser");
        if (opt.isPresent()) {
            File annotFile = opt.get();
            model.setAnnotationFile(annotFile);
            deserializeAutomaxoRowsToFile(annotFile);
        }
    }


    public void serializeAutomaxoRowsToFile() {
        Optional<File> opt = model.getAnnotationFile();
        if (opt.isEmpty()) {
            PopUps.alertDialog("Warning", "Set annotation file prior to seriaqlizing");
        }
        File annotFile = opt.get();
        List<AutoMaxoRow> rows = this.automaxoTableView.getItems();
        try (FileOutputStream fileOut = new FileOutputStream(annotFile);
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(rows);
            LOGGER.info("Serialized data is saved in {}", annotFile.getAbsolutePath());
        } catch (IOException i) {
            LOGGER.error(i.getMessage());
        }
    }

    public void deserializeAutomaxoRowsToFile(File file) {
        try (FileInputStream fileIn = new FileInputStream(file);
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            List<AutoMaxoRow> autoMaxoRows = (List<AutoMaxoRow>) in.readObject();
            this.automaxoTableView.getItems().clear();
            ObservableList<AutoMaxoRow> list = FXCollections.observableArrayList();
            list.addAll(autoMaxoRows);
            this.automaxoTableView.setItems(list);
            LOGGER.info("Deserialized autoMaxoRows n={}", autoMaxoRows.size());
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.error(e.getMessage());
        }
    }


    public void saveAnnotationFile(ActionEvent e) {
        e.consume();
        Window stage = this.relationCB.getScene().getWindow();
        Optional<File> opt = PopUps.selectOrCreateInputFile(stage, "*.ser");
        if (opt.isPresent()) {
            File annotFile = opt.get();
            model.setAnnotationFile(annotFile);
            serializeAutomaxoRowsToFile();
        }
    }


    private void annotateAutomaxoRow(AutoMaxoRow currentRow) {
        Optional<Term> hpoTermOpt = this.hpoTermAdder.getTermIfValid();
        Optional<Term> maxoTermOpt = this.maxoTermAdder.getTermIfValid();
        Optional<Term> mondoTermOpt = this.mondoTermAdder.getTermIfValid();
        if (mondoTermOpt.isEmpty()) {
            PopUps.alertDialog("Error", "Cannot add annotation unless MONDO term is valid (green border)");
            return;
        }
        if (hpoTermOpt.isEmpty()) {
            PopUps.alertDialog("Error", "Cannot add annotation unless HPO term is valid (green border)");
            return;
        }
        if (maxoTermOpt.isEmpty()) {
            PopUps.alertDialog("Error", "Cannot add annotation unless Maxo term is valid (green border)");
            return;
        }
        MaxoRelation relation = this.relationCB.getValue();

        Term mondoTerm = mondoTermOpt.get();
        Term hpoTerm = hpoTermOpt.get();
        Term maxoTerm = maxoTermOpt.get();
        currentRow.setItemStatus(ItemStatus.ANNOTATED);
        currentRow.setMaxo(maxoTerm);
        currentRow.setHpo(hpoTerm);
        currentRow.setDiseaseTerm(mondoTerm);
        currentRow.setMaxoRelation(relation);
        String orcid = model.getOrcid().orElse("n/a");
        outputRowSet.addAll(currentRow.getPoetRows(orcid));
        LOGGER.info("Number of annotations so far {}", outputRowSet.size());
        long count = automaxoTableView.getItems().stream().filter(AutoMaxoRow::isAnnotated).count();
        LOGGER.info("Current row status {}", currentRow.getItemStatus());
        LOGGER.info("Total annotated items {}", count);
        clearFields();
    }


    public void createAnnot(ActionEvent e) {
        e.consume();
        System.out.println("create annotation");
       // AutoMaxoRow currentRow = model.getCurrentRow();
        AutoMaxoRow currentRow = automaxoTableView.getSelectionModel().getSelectedItems().getFirst();
       annotateAutomaxoRow(currentRow);
    }

    @FXML
    public void exportAnnotationFile(ActionEvent actionEvent) {
        LOGGER.info("Exporting POET annotation file");
        /*List<AutoMaxoRow> autoMaxoRowList = this.automaxoTableView.getItems();
        List<String> outputrows = new ArrayList<>();
        String orcid = model.getOrcid().orElse("n/a");
        for (var amrow : autoMaxoRowList) {
            System.out.println(amrow.maxoDisplay());
            if (amrow.getItemStatus().equals(ItemStatus.ANNOTATED)) {
                outputrows.addAll(amrow.getPoetRows(orcid));
            }
        }

         */
        List<PoetOutputRow> porList = new ArrayList<>(outputRowSet);
        Collections.sort(porList);
        List<String> outputrows = new ArrayList<>();
        for (var row : porList) {
            outputrows.add(row.geTsvLine());
        }

        Optional<Window> opt =   Stage.getWindows().stream().filter(Window::isShowing).findAny();
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
}
