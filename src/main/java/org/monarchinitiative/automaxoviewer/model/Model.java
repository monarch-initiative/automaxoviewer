package org.monarchinitiative.automaxoviewer.model;

import org.monarchinitiative.automaxoviewer.json.AutomaxoJson;
import org.monarchinitiative.automaxoviewer.json.TripletItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Model {

    List<TripletItem> tripletItemList;

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
    }

    public int getNextAbstractCount() {
        int c = currentAbstractCount;
        currentAbstractCount ++;
        if (currentAbstractCount >= currentRow.getCount()) {
            currentAbstractCount = 0;
        }
        return c;
    }

    public AutoMaxoRow getCurrentRow() {
        return currentRow;
    }
}
