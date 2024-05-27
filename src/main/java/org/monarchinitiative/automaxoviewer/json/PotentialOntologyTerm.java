package org.monarchinitiative.automaxoviewer.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.io.Serializable;

public class PotentialOntologyTerm implements Serializable {

    private String id;
    private String label;

    @JsonCreator
    public PotentialOntologyTerm(@JsonProperty("id") String id,
                                 @JsonProperty("label") String label) {
        this.id = id;
        this.label = label;
    }

    @JsonGetter("id")
    public String getId() {
        return id;
    }
    @JsonSetter("id")
    public void setId(String potentialId) {
        this.id = potentialId;
    }

    @JsonGetter("label")
    public String getLabel() {
        return label;
    }

    @JsonSetter("label")
    public void setLabel(String potentialLabel) {
        this.label = potentialLabel;
    }
}
