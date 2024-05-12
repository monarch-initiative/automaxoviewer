package org.monarchinitiative.automaxoviewer.model;

import org.monarchinitiative.automaxoviewer.json.AutomaxoJson;
import org.monarchinitiative.automaxoviewer.json.TripletItem;

import java.util.Arrays;
import java.util.List;

public class Model {

    List<TripletItem> tripletItemList;

    public Model(AutomaxoJson automaxoJson) {
        this.tripletItemList = Arrays.stream(automaxoJson.getTriplets()).toList();
    }


    public List<TripletItem> getTripletItemList() {
        return tripletItemList;
    }
}
