package org.monarchinitiative.automaxoviewer.model;

import org.monarchinitiative.automaxoviewer.json.AutomaxoJson;
import org.monarchinitiative.automaxoviewer.json.TripletItem;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class Model {

    List<TripletItem> tripletItemList;

    private Options options = null;


    public Model() {
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

}
