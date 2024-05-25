package org.monarchinitiative.automaxoviewer.json;

import com.fasterxml.jackson.annotation.*;

import java.util.Map;

@JsonPropertyOrder({ "text", "mesh_info" })
public class PubMedSource {

    private String title;

    private String text;

    private Map<String, String> mesh_info;


    @JsonCreator
    public PubMedSource(@JsonProperty("title") String title,
                        @JsonProperty("abstract") String text,
                        @JsonProperty("mesh_info") Map<String, String> meshInfoMap) {
        this.title = title;
        this.text = text;
        this.mesh_info = meshInfoMap;
    }

    @JsonGetter("title")
    public String getTitle() {
        return title;
    }

    @JsonGetter("abstract")
    public String getAbstract() {
        return text;
    }

    @JsonGetter("mesh_info")
    public Map<String, String> getMesh_info() {
        return mesh_info;
    }

    @JsonSetter("title")
    public void setTitle(String title) {
        this.title = title;
    }

    @JsonSetter("abstract")
    public void setAbstract(String text) {
        this.text = text;
    }

    @JsonSetter("mesh_info")
    public void setMesh_info(Map<String, String> mesh_info) {
        this.mesh_info = mesh_info;
    }
}
