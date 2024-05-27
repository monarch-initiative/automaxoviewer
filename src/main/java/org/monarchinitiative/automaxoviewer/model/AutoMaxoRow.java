package org.monarchinitiative.automaxoviewer.model;


import javafx.beans.property.ObjectProperty;
import org.monarchinitiative.automaxoviewer.json.PotentialOntologyTerm;
import org.monarchinitiative.automaxoviewer.json.TripletItem;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * An instance of this class represents one row in the table
 * Note that we consider the table data (following import of JSON) to be the source of truth
 * We keep the original data unchanged but have new data that will represent the output for the
 * MAxO output annotation file:
 * <ol>
 *     <li>disease_id (MONDO)</li>
 *      <li>disease_name</li>
 *     <li>source_id (PMID)</li>
 *     <li>maxo_id</li>
 *      <li>maxo_name</li>
 *     <li>hpo_id</li>
 *     <li>relation (e.g., Treats)</li>
 *      <li>evidence (e.g., PCS; TAS)</li>
 *     <li>extension_id (e.g., CHEBI)</li>
 *     <li>extension_name</li>
 *      <li>comment</li>
 *     <li>other</li>
 *     <li>author</li>
 *      <li>last_updated</li>
 *     <li>created</li>
 * </ol>
 *
 */
public class AutoMaxoRow implements Serializable {


    private final javafx.beans.property.ObjectProperty<ItemStatus> itemStatus;

    private String disease_id = null;
    private String disease_name = null;
    private String source_id = null;
    private String maxo_id = null;
    private String maxo_name = null;
    private String hpo_name = null;
    private String hpo_id = null;
    private String relation = null;
    private String evidence = null;
    private String extension_id = null;
    private String extension_name = null;
    /** If true, the medical action is for the entire disease rather than for a specific feature (HPO term). */
    private boolean diseaseLevelAnnotation = false;
    private Set<String> approvedPmidSet;


    private final String maxoId;
    private final String maxoLabel;
    private final String nonGroundedMaxo;
    private final List<PotentialOntologyTerm> potentialMaxo;
    private final String relationship;
    private final String hpoId;
    private final String hpoLabel;
    private final String nonGroundedHpo;
    private final List<PotentialOntologyTerm> potentialHpo;
    private final String mondoId;
    private final String mondoLabel;
    private final String nonGroundedMondo;
    private final List<PotentialOntologyTerm> potentialMondo;
    private final String maxoQualifier;
    private final String chebi;
    private final String hpoExtension;
    private final int count;
    private final List<PubMedCitation> citationList;


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
       this.citationList = new ArrayList<>();
        for (var e : item.getSource().entrySet()) {
            this.citationList.add(new PubMedCitation(e.getKey(), e.getValue()));
        }
        // The following items are shown in the GUI and are mutable
        itemStatus =  new javafx.beans.property.SimpleObjectProperty<>();
        itemStatus.set(ItemStatus.IN_PROGRESS);
        this.disease_name = mondoDisplay();
        this.maxo_name = maxoDisplay();
        this.hpo_name = hpoDisplay();
        this.approvedPmidSet = new HashSet<>();
    }

    public String getMaxoId() {
        return maxoId;
    }


    public String getMaxoLabel() {
        return maxoLabel;
    }


    public String getNonGroundedMaxo() {
        return nonGroundedMaxo;
    }


    public List<PotentialOntologyTerm> getPotentialMaxo() {
        return potentialMaxo;
    }


    public String getRelationship() {
        return relationship;
    }


    public String getHpoId() {
        return hpoId;
    }

    public String getHpoLabel() {
        return hpoLabel;
    }


    public String getNonGroundedHpo() {
        return nonGroundedHpo;
    }


    public List<PotentialOntologyTerm> getPotentialHpo() {
        return potentialHpo;
    }


    public String getMondoId() {
        return mondoId;
    }

    public String getMondoLabel() {
        return mondoLabel;
    }

    public String getNonGroundedMondo() {
        return nonGroundedMondo;
    }


    public List<PotentialOntologyTerm> getPotentialMondo() {
        return potentialMondo;
    }

    public String getMaxoQualifier() {
        return maxoQualifier;
    }


    public String getChebi() {
        return chebi;
    }


    public String getHpoExtension() {
        return hpoExtension;
    }


    public int getCount() {
        return count;
    }


    public List<PubMedCitation> getCitationList() {
        return citationList;
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


    public ItemStatus getItemStatus() {
        return itemStatus.get();
    }

    public ObjectProperty<ItemStatus> getItemStatusProperty() {
        return itemStatus;
    }

    public void setItemStatus(ItemStatus itemStatus) {
        this.itemStatus.set(itemStatus);
    }

    public String getDisease_id() {
        return disease_id;
    }

    public void setDisease_id(String disease_id) {
        this.disease_id = disease_id;
    }

    public String getDisease_name() {
        return disease_name;
    }

    public void setDisease_name(String disease_name) {
        this.disease_name = disease_name;
    }

    public String getSource_id() {
        return source_id;
    }

    public void setSource_id(String source_id) {
        this.source_id = source_id;
    }

    public String getMaxo_id() {
        return maxo_id;
    }

    public void setMaxo_id(String maxo_id) {
        this.maxo_id = maxo_id;
    }

    public String getMaxo_name() {
        return maxo_name;
    }

    public void setMaxo_name(String maxo_name) {
        this.maxo_name = maxo_name;
    }

    public String getHpo_name() {
        return hpo_name;
    }

    public void setHpo_name(String hpo_name) {
        this.hpo_name = hpo_name;
    }

    public String getHpo_id() {
        return hpo_id;
    }

    public void setHpo_id(String hpo_id) {
        this.hpo_id = hpo_id;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getEvidence() {
        return evidence;
    }

    public void setEvidence(String evidence) {
        this.evidence = evidence;
    }

    public String getExtension_id() {
        return extension_id;
    }

    public void setExtension_id(String extension_id) {
        this.extension_id = extension_id;
    }

    public String getExtension_name() {
        return extension_name;
    }

    public void setExtension_name(String extension_name) {
        this.extension_name = extension_name;
    }

    public boolean isDiseaseLevelAnnotation() {
        return diseaseLevelAnnotation;
    }

    public void setDiseaseLevelAnnotation(boolean diseaseLevelAnnotation) {
        this.diseaseLevelAnnotation = diseaseLevelAnnotation;
    }

    public Set<String> getApprovedPmidSet() {
        return approvedPmidSet;
    }


}
