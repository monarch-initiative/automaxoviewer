package org.monarchinitiative.automaxoviewer.json;


import com.fasterxml.jackson.annotation.*;

@JsonPropertyOrder({ "triplet", "count", "source"})
public class TripletItem {


    private Triplet triplet;

    private int count;

    private Source source;


    @JsonCreator
    public TripletItem(@JsonProperty("triplet") Triplet triplet,
                       @JsonProperty("count") int count,
                       @JsonProperty("source") Source source){
        this.triplet = triplet;
        this.count = count;
        this.source = source;
    }
    @JsonGetter("triplet")
    public Triplet getTriplet() {
        return triplet;
    }


    @JsonGetter("count")
    public int getCount() {
        return count;
    }

    @JsonGetter("source")
    public Source getSource() {
        return source;
    }

    @JsonSetter("triplet")
    public void setTriplet(Triplet triplet) {
        this.triplet = triplet;
    }

    @JsonSetter("count")
    public void setCount(int count) {
        this.count = count;
    }

    @JsonSetter("source")
    public void setSource(Source source) {
        this.source = source;
    }
}
