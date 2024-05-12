package org.monarchinitiative.automaxoviewer.model;

import org.monarchinitiative.phenol.ontology.data.MinimalOntology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class OntologyRosettaStone {
    private final static Logger LOGGER = LoggerFactory.getLogger(OntologyRosettaStone.class);

    private final Map<String, Term> labelToTermMap;

    private final Map<TermId, String> idToLabelMap;

    public OntologyRosettaStone(MinimalOntology ontology) {
        Map<String, Term> labelMap = new HashMap<>();
        Map<TermId, String> idMap = new HashMap<>();
        if (ontology == null) {
            LOGGER.error("Attempt to initialize HpoRosettaStone but ontology argument was null");
        } else {
            ontology.getTerms().forEach(term ->  labelMap.putIfAbsent(term.getName(), term));
            ontology.getTerms().forEach(term ->  idMap.putIfAbsent(term.id(), term.getName()));
        }
        this.labelToTermMap = Map.copyOf(labelMap);
        idToLabelMap = Map.copyOf(idMap);
    }

    public Optional<Term> termFromPrimaryLabel(String label) {
        return Optional.ofNullable(labelToTermMap.get(label));
    }

    public Optional<String> primaryLabelFromId(TermId tid) {
        return Optional.ofNullable(idToLabelMap.get(tid));
    }

    public Set<String> allLabels() {
        return this.labelToTermMap.keySet();
    }




}
