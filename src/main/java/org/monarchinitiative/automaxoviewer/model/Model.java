package org.monarchinitiative.automaxoviewer.model;

import org.monarchinitiative.automaxoviewer.json.AutomaxoJson;
import org.monarchinitiative.automaxoviewer.json.TripletItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Model {
    private static final Logger LOGGER = LoggerFactory.getLogger(Model.class);


    List<TripletItem> tripletItemList;

    private File automaxoFile = null;

    private File annotationFile = null;


    private Options options = null;

    private MaxoAnnotation currentAnnotation;

    private List<MaxoAnnotation> annotationList;

    private AutoMaxoRow currentRow = null;

    private int currentAbstractCount;

    public Model() {
        currentAnnotation = new MaxoAnnotation();
        annotationList = new ArrayList<>();
    }

    public void setTripletItemList(AutomaxoJson automaxoJson) {
        this.tripletItemList = Arrays.stream(automaxoJson.getTriplets()).toList();
    }


    public List<TripletItem> getTripletItemList() {
        return tripletItemList;
    }

    public void setOptions(Options options) {
        this.options = options;
    }


    public void setCurrentRow(AutoMaxoRow row) {
        this.currentRow = row;
        currentAbstractCount = 0;
        LOGGER.info("Setting current row to {}", currentRow);
    }

    public Optional<Integer> getNextAbstractCount() {
        if (currentRow == null) {
            return Optional.empty();
        }
        int c = currentAbstractCount;
        currentAbstractCount ++;
        if (currentAbstractCount >= currentRow.getCount()) {
            currentAbstractCount = 0;
        }
        return Optional.of(c);
    }

    public AutoMaxoRow getCurrentRow() {
        return currentRow;
    }

    public void setAutomaxoFile(File automaxoFile) {
        this.automaxoFile = automaxoFile;
    }

    public Optional<File> getAutomaxoFile() {
        return Optional.ofNullable(automaxoFile);
    }

    public void setAnnotationFile(File annotFile) {
        this.annotationFile = annotFile;
    }

    public Optional<File> getAnnotationFile() {
        return Optional.ofNullable(annotationFile);
    }


    public Optional<String> getOrcid() {
        if (options == null) return Optional.empty();
        else return Optional.of(options.getOrcid());
    }
}
