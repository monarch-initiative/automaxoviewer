package org.monarchinitiative.automaxoviewer.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Map;
@JsonPropertyOrder({ "source" })
public class Source {

    private Map<String, PubMedSource> source;

    @JsonCreator
    public Source(@JsonProperty("source") Map<String, PubMedSource> source) {
        this.source = source;
    }

    @JsonGetter("source")
    public Map<String, PubMedSource> getSource() {
        return source;
    }

    public void setSource(Map<String, PubMedSource> source) {
        this.source = source;
    }
}
