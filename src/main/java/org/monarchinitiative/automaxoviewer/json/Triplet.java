package org.monarchinitiative.automaxoviewer.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Triplet {
    
    private  String maxo;
    private  String maxo_label;
    private  String non_grounded_maxo;
    private  String [] potential_maxo;
    private  String relationship;
    private  String hpo;
    private  String hpo_label;
    private  String non_grounded_hpo;
    private  String [] potential_hpo;
    private  String mondo;
    private  String mondo_label;
    private  String non_grounded_mondo;
    private  String [] potential_mondo;
    private  String maxo_qualifier;
    private  String chebi;
    private  String hpo_extension;

    @JsonCreator
    public Triplet(@JsonProperty("maxo") String maxo,
                   @JsonProperty("maxo_label") String maxoLabel,
                   @JsonProperty("non_grounded_maxo") String nonGroundedMaxo,
                   @JsonProperty("potential_maxo") String [] potentialMaxo,
                   @JsonProperty("relationship") String relationship,
                   @JsonProperty("hpo") String hpo,
                   @JsonProperty("hpo_label") String hpoLabel,
                   @JsonProperty("non_grounded_hpo") String nonGroundedHpo,
                   @JsonProperty("potential_hpo") String [] potentialHpo,
                   @JsonProperty("mondo") String mondo,
                   @JsonProperty("mondo_label") String mondoLabel,
                   @JsonProperty("non_grounded_mondo") String nonGroundedMondo,
                   @JsonProperty("potential_mondo") String [] potentialMondo,
                   @JsonProperty("maxo_qualifier") String maxoQualifier,
                   @JsonProperty("chebi") String chebi,
                   @JsonProperty("hpo_extension") String hpoExtension) {
        this.maxo = maxo;
        maxo_label = maxoLabel;
        non_grounded_maxo = nonGroundedMaxo;
        potential_maxo = potentialMaxo;
        this.relationship = relationship;
        this.hpo = hpo;
        hpo_label = hpoLabel;
        non_grounded_hpo = nonGroundedHpo;
        potential_hpo = potentialHpo;
        this.mondo = mondo;
        mondo_label = mondoLabel;
        non_grounded_mondo = nonGroundedMondo;
        potential_mondo = potentialMondo;
        maxo_qualifier = maxoQualifier;
        this.chebi = chebi;
        hpo_extension = hpoExtension;
    }

    @JsonGetter("maxo")
    public String getMaxo() {
        return maxo;
    }
    @JsonGetter("maxo_label")
    public String getMaxo_label() {
        return maxo_label;
    }
    @JsonGetter("non_grounded_maxo")
    public String getNon_grounded_maxo() {
        return non_grounded_maxo;
    }
    @JsonGetter("potential_maxo")
    public String [] getPotential_maxo() {
        return potential_maxo;
    }
    @JsonGetter("relationship")
    public String getRelationship() {
        return relationship;
    }
    @JsonGetter("hpo")
    public String getHpo() {
        return hpo;
    }
    @JsonGetter("hpo_label")
    public String getHpo_label() {
        return hpo_label;
    }
    @JsonGetter("non_grounded_hpo")
    public String getNon_grounded_hpo() {
        return non_grounded_hpo;
    }
    @JsonGetter("potential_hpo")
    public String [] getPotential_hpo() {
        return potential_hpo;
    }
    @JsonGetter("mondo")
    public String getMondo() {
        return mondo;
    }
    @JsonGetter("mondo_label")
    public String getMondo_label() {
        return mondo_label;
    }
    @JsonGetter("non_grounded_mondo")
    public String getNon_grounded_mondo() {
        return non_grounded_mondo;
    }
    @JsonGetter("potential_mondo")
    public String [] getPotential_mondo() {
        return potential_mondo;
    }
    @JsonGetter("maxo_qualifier")
    public String getMaxo_qualifier() {
        return maxo_qualifier;
    }
    @JsonGetter("chebi")
    public String getChebi() {
        return chebi;
    }
    @JsonGetter("hpo_extension")
    public String getHpo_extension() {
        return hpo_extension;
    }

    public void setMaxo(String maxo) {
        this.maxo = maxo;
    }

    public void setMaxo_label(String maxo_label) {
        this.maxo_label = maxo_label;
    }

    public void setNon_grounded_maxo(String non_grounded_maxo) {
        this.non_grounded_maxo = non_grounded_maxo;
    }

    public void setPotential_maxo(String [] potential_maxo) {
        this.potential_maxo = potential_maxo;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public void setHpo(String hpo) {
        this.hpo = hpo;
    }

    public void setHpo_label(String hpo_label) {
        this.hpo_label = hpo_label;
    }

    public void setNon_grounded_hpo(String non_grounded_hpo) {
        this.non_grounded_hpo = non_grounded_hpo;
    }

    public void setPotential_hpo(String [] potential_hpo) {
        this.potential_hpo = potential_hpo;
    }

    public void setMondo(String mondo) {
        this.mondo = mondo;
    }

    public void setMondo_label(String mondo_label) {
        this.mondo_label = mondo_label;
    }

    public void setNon_grounded_mondo(String non_grounded_mondo) {
        this.non_grounded_mondo = non_grounded_mondo;
    }

    public void setPotential_mondo(String [] potential_mondo) {
        this.potential_mondo = potential_mondo;
    }

    public void setMaxo_qualifier(String maxo_qualifier) {
        this.maxo_qualifier = maxo_qualifier;
    }

    public void setChebi(String chebi) {
        this.chebi = chebi;
    }

    public void setHpo_extension(String hpo_extension) {
        this.hpo_extension = hpo_extension;
    }
}
