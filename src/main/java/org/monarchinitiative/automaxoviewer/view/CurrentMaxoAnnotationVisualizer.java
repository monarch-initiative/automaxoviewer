package org.monarchinitiative.automaxoviewer.view;


import org.monarchinitiative.automaxoviewer.model.AutoMaxoRow;
import org.monarchinitiative.automaxoviewer.model.Options;
import org.monarchinitiative.phenol.ontology.data.Term;

import java.util.ArrayList;
import java.util.List;

public class CurrentMaxoAnnotationVisualizer extends MaxoVisualizer {


    private final Options options;



    public CurrentMaxoAnnotationVisualizer(Options options) {
        this.options = options;
    }

    public String toHTML(AutoMaxoRow item) {
        StringBuilder builder = new StringBuilder();
        builder.append(HTML_HEADER);
        builder.append(row("HPO ID", item.getHpoId()));
        builder.append(row("HPO Label", item.getCandidateHpoLabel()));
        builder.append(row("Relation", item.getRelationship()));
        builder.append(row("Maxo ID", item.getMaxoId()));
        builder.append(row("Maxo Label", item.getCandidateMaxoLabel()));
        List<String> ids = new ArrayList<>();
        for (var i : item.getCitationList()) {
            ids.add(i.getPmidTermId().getValue());
        }
        String pmids = String.join("; ", ids);
        builder.append(row("PMIDs", pmids));
        builder.append(orcidRow());
        builder.append(HTML_FOOT);
        return builder.toString();
    }




    private String getParentTermString(List<Term> terms) {
        List<String> termStrings = new ArrayList<>();
        for (Term term : terms) {
            String display = String.format("%s (%s)", term.getName(), term.id().getValue());
            termStrings.add(display);
        }
        return String.join("; ", termStrings);
    }


    private String row(String key, String value) {
        return "<tr class=\"row100\"><td class=\"column100 column1\" data-column=\"column1\">" +
                key +
                "</td><td class=\"column100 column2\" data-column=\"column1\">" +
                value +
                "</td></tr>";
    }


    private String orcidRow() {
        return row("ORCID", options.getOrcid());
    }


}
