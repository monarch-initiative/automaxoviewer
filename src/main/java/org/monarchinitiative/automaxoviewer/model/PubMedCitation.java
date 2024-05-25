package org.monarchinitiative.automaxoviewer.model;

import org.monarchinitiative.automaxoviewer.json.PubMedSource;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Map;

public class PubMedCitation {

    private final String title;
    private final String abstractText;
    private final TermId pmidTermId;
    private final Map<String, String> meshInfo;

    public PubMedCitation(String pmid, PubMedSource source) {
        if (pmid.startsWith("PMID:")) {
            pmidTermId = TermId.of(pmid);
        } else {
            pmidTermId = TermId.of(String.format("PMID:%s", pmid));
        }
        title = source.getTitle();
        abstractText = source.getAbstract();
        meshInfo = source.getMesh_info();
    }

    public String getTitle() {
        return title;
    }

    public String getAbstractText() {
        return abstractText;
    }

    public TermId getPmidTermId() {
        return pmidTermId;
    }

    public Map<String, String> getMeshInfo() {
        return meshInfo;
    }
}
