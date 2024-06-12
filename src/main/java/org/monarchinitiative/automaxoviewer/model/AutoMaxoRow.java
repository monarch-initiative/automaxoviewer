package org.monarchinitiative.automaxoviewer.model;


import javafx.beans.property.ObjectProperty;
import org.monarchinitiative.automaxoviewer.json.PotentialOntologyTerm;
import org.monarchinitiative.automaxoviewer.json.TripletItem;
import org.monarchinitiative.phenol.ontology.data.Term;

import java.io.Serial;
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
    @Serial
    private static final long serialVersionUID = 1L;


    private final javafx.beans.property.ObjectProperty<ItemStatus> itemStatus;
    private Term mondoTerm = null;
    private Term maxoTerm = null;
    private Term hpoTerm = null;

    private String source_id = null;

    private MaxoRelation maxoRelation = MaxoRelation.UNKNOWN;
    private String evidence = null;
    private String extension_id = null;
    private String extension_name = null;
    /** If true, the medical action is for the entire disease rather than for a specific feature (HPO term). */
    private boolean diseaseLevelAnnotation = false;
    private final Set<String> approvedPmidSet;


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

    public Optional<Term> mondoTerm() {
        return Optional.ofNullable(mondoTerm);
    }
    public void setDiseaseTerm(Term term) {
        this.mondoTerm = term;
    }

    public Optional<Term> maxoTerm() {
        return Optional.ofNullable(maxoTerm);
    }
    public void setMaxo(Term term) {
        this.maxoTerm = term;
    }

    public Optional<Term> hpoTerm() {
        return Optional.ofNullable(hpoTerm);
    }
    public void setHpo(Term term) {
        this.hpoTerm = term;
    }

    public String getSource_id() {
        return source_id;
    }

    public void setSource_id(String source_id) {
        this.source_id = source_id;
    }


    public MaxoRelation getMaxoRelation() {
        return maxoRelation;
    }

    public void setMaxoRelation(MaxoRelation maxoRelation) {
        this.maxoRelation = maxoRelation;
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

    public List<String> getPoetRows(String orcid) {
        List<String> rows = new ArrayList<>();
        for (String pmid : getApprovedPmidSet()) {
            var row = new PoetOutputRow(mondoTerm,
                    pmid,
                    maxoTerm,
                    hpoTerm,
                    maxoRelation,
                    orcid);
            rows.add(row.getRowAsTsv());
        }
        return rows;
    }


}
