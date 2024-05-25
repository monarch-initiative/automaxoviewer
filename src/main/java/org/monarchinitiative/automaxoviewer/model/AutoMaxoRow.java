package org.monarchinitiative.automaxoviewer.model;


import org.monarchinitiative.automaxoviewer.json.PotentialOntologyTerm;
import org.monarchinitiative.automaxoviewer.json.TripletItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An instance of this class represents one row in the table
 */
public class AutoMaxoRow {


    private String maxoId;
    private  String maxoLabel;
    private  String nonGroundedMaxo;
    private List<PotentialOntologyTerm> potentialMaxo;
    private  String relationship;
    private  String hpoId;
    private  String hpoLabel;
    private  String nonGroundedHpo;
    private  List<PotentialOntologyTerm> potentialHpo;
    private  String mondoId;
    private  String mondoLabel;
    private  String nonGroundedMondo;
    private  List<PotentialOntologyTerm> potentialMondo;
    private  String maxoQualifier;
    private  String chebi;
    private  String hpoExtension;
    private int count;
    private List<PubMedCitation> citationList;


    public AutoMaxoRow(TripletItem item) {
       this.maxoId = item.getTriplet().getMaxo().toUpperCase();
       this.maxoLabel = item.getTriplet().getMaxo_label();
       this.nonGroundedMaxo = item.getTriplet().getNon_grounded_maxo();
       this.potentialMaxo = Arrays.stream(item.getTriplet().getPotential_maxo()).toList();
       this.relationship = item.getTriplet().getRelationship();
       this.hpoId = item.getTriplet().getHpo().toUpperCase();
       this.hpoLabel = item.getTriplet().getHpo_label();
       this.nonGroundedHpo = item.getTriplet().getNon_grounded_hpo();
       this.potentialHpo = Arrays.stream(item.getTriplet().getPotential_hpo()).toList();
       this.mondoId = item.getTriplet().getMondo().toUpperCase();
       this.mondoLabel = item.getTriplet().getMondo_label();
       this.nonGroundedMondo = item.getTriplet().getNon_grounded_mondo();
       this.potentialMondo = Arrays.asList(item.getTriplet().getPotential_mondo());
       this.maxoQualifier = item.getTriplet().getMaxo_qualifier();
       this.chebi = item.getTriplet().getChebi();
       this.hpoExtension = item.getTriplet().getHpo_extension();
       this.count = item.getCount();
        citationList = new ArrayList<>();
        for (var e : item.getSource().entrySet()) {
            citationList.add(new PubMedCitation(e.getKey(), e.getValue()));
        }
    }

    public String getMaxoId() {
        return maxoId;
    }

    public void setMaxoId(String maxoId) {
        this.maxoId = maxoId;
    }

    public String getMaxoLabel() {
        return maxoLabel;
    }

    public void setMaxoLabel(String maxoLabel) {
        this.maxoLabel = maxoLabel;
    }

    public String getNonGroundedMaxo() {
        return nonGroundedMaxo;
    }

    public void setNonGroundedMaxo(String nonGroundedMaxo) {
        this.nonGroundedMaxo = nonGroundedMaxo;
    }

    public List<PotentialOntologyTerm> getPotentialMaxo() {
        return potentialMaxo;
    }

    public void setPotentialMaxo(List<PotentialOntologyTerm> potentialMaxo) {
        this.potentialMaxo = potentialMaxo;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getHpoId() {
        return hpoId;
    }

    public void setHpoId(String hpoId) {
        this.hpoId = hpoId;
    }

    public String getHpoLabel() {
        return hpoLabel;
    }

    public void setHpoLabel(String hpoLabel) {
        this.hpoLabel = hpoLabel;
    }

    public String getNonGroundedHpo() {
        return nonGroundedHpo;
    }

    public void setNonGroundedHpo(String nonGroundedHpo) {
        this.nonGroundedHpo = nonGroundedHpo;
    }

    public List<PotentialOntologyTerm> getPotentialHpo() {
        return potentialHpo;
    }

    public void setPotentialHpo(List<PotentialOntologyTerm> potentialHpo) {
        this.potentialHpo = potentialHpo;
    }

    public String getMondoId() {
        return mondoId;
    }

    public void setMondoId(String mondoId) {
        this.mondoId = mondoId;
    }

    public String getMondoLabel() {
        return mondoLabel;
    }

    public void setMondoLabel(String mondoLabel) {
        this.mondoLabel = mondoLabel;
    }

    public String getNonGroundedMondo() {
        return nonGroundedMondo;
    }

    public void setNonGroundedMondo(String nonGroundedMondo) {
        this.nonGroundedMondo = nonGroundedMondo;
    }

    public List<PotentialOntologyTerm> getPotentialMondo() {
        return potentialMondo;
    }

    public void setPotentialMondo(List<PotentialOntologyTerm> potentialMondo) {
        this.potentialMondo = potentialMondo;
    }

    public String getMaxoQualifier() {
        return maxoQualifier;
    }

    public void setMaxoQualifier(String maxoQualifier) {
        this.maxoQualifier = maxoQualifier;
    }

    public String getChebi() {
        return chebi;
    }

    public void setChebi(String chebi) {
        this.chebi = chebi;
    }

    public String getHpoExtension() {
        return hpoExtension;
    }

    public void setHpoExtension(String hpoExtension) {
        this.hpoExtension = hpoExtension;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<PubMedCitation> getCitationList() {
        return citationList;
    }

    public void setCitationList(List<PubMedCitation> citationList) {
        this.citationList = citationList;
    }

    public String maxoDisplay() {
        if (! maxoId.isEmpty() && ! maxoLabel.isEmpty()) {
            return String.format("%s (%s)", maxoLabel, maxoId);
        } else if (! nonGroundedMaxo.isEmpty()) {
            return String.format("Non-grounded: %s", nonGroundedMaxo);
        } else if (! potentialMaxo.isEmpty()) {
            String candidates = potentialMaxo.stream().map(PotentialOntologyTerm::getLabel).collect(Collectors.joining("; "));
            return String.format("Potential: %s", candidates);
        } else {
            return "n/a";
        }
    }

    public String hpoDisplay() {
        if (! hpoId.isEmpty() && ! hpoLabel.isEmpty()) {
            return String.format("%s (%s)", hpoLabel, hpoId);
        } else if (! nonGroundedHpo.isEmpty()) {
            return String.format("Non-grounded: %s", nonGroundedHpo);
        } else if (! potentialHpo.isEmpty()) {
            String candidates = potentialHpo.stream().map(PotentialOntologyTerm::getLabel).collect(Collectors.joining("; "));
            return String.format("Potential: %s", candidates);
        } else {
            return "n/a";
        }
    }

    public String mondoDisplay() {
        if (! mondoId.isEmpty() && ! mondoLabel.isEmpty()) {
            return String.format("%s (%s)", mondoLabel, mondoId);
        } else if (! nonGroundedMondo.isEmpty()) {
            return String.format("Non-grounded: %s", nonGroundedMondo);
        } else if (! potentialMondo.isEmpty()) {
            String candidates = potentialMondo.stream().map(PotentialOntologyTerm::getLabel).collect(Collectors.joining("; "));
            return String.format("Potential: %s", candidates);
        } else {
            return "n/a";
        }
    }

    @Override
    public String toString() {
        return String.format("[AutoMaxoRow] %s - %s", getHpoLabel(), getMaxoLabel());
    }

}
