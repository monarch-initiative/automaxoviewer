package org.monarchinitiative.automaxoviewer.json;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

public class AutomaxoJson {

    private TripletItem [] triplets;


    @JsonCreator
    public AutomaxoJson(@JsonProperty("triplets") TripletItem[] triplets) {
        this.triplets = triplets;
    }

    @JsonGetter("triplets")
    public TripletItem[] getTriplets() {
        return triplets;
    }

    @JsonSetter("triplets")
    public void setTriplets(TripletItem[] triplets) {
        this.triplets = triplets;
    }
}
