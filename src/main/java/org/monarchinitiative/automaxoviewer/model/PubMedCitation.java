package org.monarchinitiative.automaxoviewer.model;

import org.monarchinitiative.automaxoviewer.json.PubMedSource;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Map;

public class PubMedCitation {

    private String title;
    private String abstractText;
    private TermId pmidTermId;
    private Map<String, String> meshInfo;

    public PubMedCitation(String pmid, PubMedSource source) {
        if (pmid.startsWith("PMID:")) {
            pmidTermId = TermId.of(pmid);
        } else {
            pmidTermId = TermId.of(String.format("PMID:%s", pmid));
        }
        title = source.getText();
        abstractText = source.getText();
        meshInfo = source.getMesh_info();
    }

}
