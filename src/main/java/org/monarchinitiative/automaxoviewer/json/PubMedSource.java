package org.monarchinitiative.automaxoviewer.json;

import com.fasterxml.jackson.annotation.*;

import java.util.Map;

@JsonPropertyOrder({ "text", "mesh_info" })
public class PubMedSource {

    private String text;

    private Map<String, String> mesh_info;


    @JsonCreator
    public PubMedSource(@JsonProperty("text") String text,
                        @JsonProperty("mesh_info") Map<String, String> meshInfoMap) {
        this.text = text;
        this.mesh_info = meshInfoMap;
    }

    @JsonGetter("text")
    public String getText() {
        return text;
    }

    @JsonGetter("mesh_info")
    public Map<String, String> getMesh_info() {
        return mesh_info;
    }

    @JsonSetter("text")
    public void setText(String text) {
        this.text = text;
    }

    @JsonSetter("mesh_info")
    public void setMesh_info(Map<String, String> mesh_info) {
        this.mesh_info = mesh_info;
    }
}
