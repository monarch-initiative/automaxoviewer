package org.monarchinitiative.automaxoviewer.model;


import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.monarchinitiative.automaxoviewer.json.PotentialOntologyTerm;
import org.monarchinitiative.automaxoviewer.json.TripletItem;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

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
    /** This term is used for disease-level annotations */
    private final static Term PHENOTYPIC_ABNORMALITY = Term.of(TermId.of("HP:0000118"), "Phenotypic abnormality");


    private final ObjectProperty<ItemStatus> itemStatus;
    private final ObjectProperty<Term> mondoProperty;
    private final ObjectProperty<Term> maxoProperty;
    private final ObjectProperty<Term> hpoProperty;
    private final ObjectProperty<MaxoRelation> maxoRelationProperty;
    private final BooleanProperty diseaseLevelProperty;


    private String source_id = null;


    private String evidence = null;
    private String extension_id = null;
    private String extension_name = null;
    /** If true, the medical action is for the entire disease rather than for a specific feature (HPO term). */
    private final Set<String> approvedPmidSet;


    private final String maxoId;
    private final String candidateMaxoLabel;
    private final String nonGroundedMaxo;
    private final List<PotentialOntologyTerm> potentialMaxo;
    private final String relationship;
    private final String hpoId;
    private final String candidateHpoLabel;
    private final String nonGroundedHpo;
    private final List<PotentialOntologyTerm> potentialHpo;
    private final String mondoId;
    private final String candidateMondoLabel;
    private final String nonGroundedMondo;
    private final List<PotentialOntologyTerm> potentialMondo;
    private final String maxoQualifier;
    private final String chebi;
    private final String hpoExtension;
    private final int count;
    private final List<PubMedCitation> citationList;


    public AutoMaxoRow(TripletItem item) {
       this.maxoId = item.getTriplet().getMaxo().toUpperCase();
       this.candidateMaxoLabel = item.getTriplet().getMaxo_label();
       this.nonGroundedMaxo = item.getTriplet().getNon_grounded_maxo();
       this.potentialMaxo = Arrays.stream(item.getTriplet().getPotential_maxo()).toList();
       this.relationship = item.getTriplet().getRelationship();
       this.hpoId = item.getTriplet().getHpo().toUpperCase();
       this.candidateHpoLabel = item.getTriplet().getHpo_label();
       this.nonGroundedHpo = item.getTriplet().getNon_grounded_hpo();
       this.potentialHpo = Arrays.stream(item.getTriplet().getPotential_hpo()).toList();
       this.mondoId = item.getTriplet().getMondo().toUpperCase();
       this.candidateMondoLabel = item.getTriplet().getMondo_label();
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

        mondoProperty = new SimpleObjectProperty<>();
        maxoProperty = new SimpleObjectProperty<>();
        hpoProperty = new SimpleObjectProperty<>();
        maxoRelationProperty = new SimpleObjectProperty<>();
        maxoRelationProperty.set(MaxoRelation.UNKNOWN);
        diseaseLevelProperty = new SimpleBooleanProperty(false);
    }

    public String getMaxoId() {
        return maxoId;
    }


    public String getCandidateMaxoLabel() {
        return candidateMaxoLabel;
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

    public String getCandidateHpoLabel() {
        return candidateHpoLabel;
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

    public String getCandidateMondoLabel() {
        return candidateMondoLabel;
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
        if (! maxoId.isEmpty() && ! candidateMaxoLabel.isEmpty()) {
            return String.format("%s (%s)", candidateMaxoLabel, maxoId);
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
        if (! hpoId.isEmpty() && ! candidateHpoLabel.isEmpty()) {
            return String.format("%s (%s)", candidateHpoLabel, hpoId);
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
        if (! mondoId.isEmpty() && ! candidateMondoLabel.isEmpty()) {
            return String.format("%s (%s)", candidateMondoLabel, mondoId);
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
        return String.format("[AutoMaxoRow] %s - %s", getCandidateHpoLabel(), getCandidateMaxoLabel());
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

    public boolean isAnnotated() {
        return this.itemStatus.equals(ItemStatus.ANNOTATED);
    }

    public Optional<Term> mondoTerm() {
        return Optional.ofNullable(this.mondoProperty.get());
    }
    public void setDiseaseTerm(Term term) {
        this.mondoProperty.set(term);
    }

    public Optional<Term> maxoTerm() {
        return Optional.ofNullable(this.maxoProperty.get());
    }
    public void setMaxo(Term term) {
        this.maxoProperty.set(term);
    }

    public Optional<Term> hpoTerm() {
        return Optional.ofNullable(this.hpoProperty.get());
    }
    public void setHpo(Term term) {
        this.hpoProperty.set(term);
    }

    public String getSource_id() {
        return source_id;
    }

    public void setSource_id(String source_id) {
        this.source_id = source_id;
    }


    public MaxoRelation getMaxoRelation() {
        return maxoRelationProperty.get();
    }

    public void setMaxoRelation(MaxoRelation maxoRelation) {
        this.maxoRelationProperty.set(maxoRelation);
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
        return diseaseLevelProperty.get();
    }

    public void setDiseaseLevelAnnotation(boolean diseaseLevelAnnotation) {
        this.diseaseLevelProperty.set(diseaseLevelAnnotation);
    }

    public Set<String> getApprovedPmidSet() {
        return approvedPmidSet;
    }




    public List<String> getPoetRows(String orcid) {
        List<String> rows = new ArrayList<>();
        for (String pmid : getApprovedPmidSet()) {
            Term hpoT;
            if (diseaseLevelProperty.get()) {
                hpoT = PHENOTYPIC_ABNORMALITY; // "disease-level annotation"
            } else {
                hpoT = hpoTerm().get();
            }
            var row = new PoetOutputRow(mondoTerm().get(),
                    pmid,
                    maxoTerm().get(),
                    hpoT,
                    maxoRelationProperty.get(),
                    orcid);
            rows.add(row.getRowAsTsv());
        }
        return rows;
    }


}
