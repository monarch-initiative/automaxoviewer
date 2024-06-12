package org.monarchinitiative.automaxoviewer.model;

import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenol.ontology.data.Term;

import java.util.ArrayList;
import java.util.List;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;



/**
 * POET format
 * <ol>
disease_id</li>
disease_name</li>
source_id (e.g.", PMID:20301537)</li>
maxo_id</li>
maxo_name</li>
hpo_id</li>
relation</li>
evidence</li>
extension_id</li>
extension_name</li>
comment</li>
other</li>
author</li>
last_updated</li>
created</li>
 * </ol>
 */
public class PoetOutputRow {

    private static final List<String> headerFields = List.of("disease_id",
            "disease_name",
            "source_id",
            "maxo_id",
            "maxo_name",
            "hpo_id",
            "relation",
            "evidence",
            "extension_id",
            "extension_name",
            "comment",
            "other",
            "author",
            "last_updated",
            "created");
    private static final String header = String.join("\t", headerFields);
    
    private static final String EMPTY_STRING = "";
    /** All of the items we curate with this tool are PCS. */
    private static final String EVIDENCE_CODE = "PCS";

    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
;

    private final List<String> fields;

    public PoetOutputRow(Term mondoTerm,
                         String sourceId,
                         Term maxoTerm,
                         Term hpoTerm,
                         MaxoRelation maxoRelation,
                         String orcid) {
        fields = new ArrayList<>();
        fields.add(mondoTerm.id().getValue());
        fields.add(mondoTerm.getName());
        if (! sourceId.startsWith("PMID")) {
            throw new PhenolRuntimeException("Malformed source id \"" + sourceId + "\"");
        }
        fields.add(sourceId);
        fields.add(maxoTerm.id().getValue());
        fields.add(maxoTerm.getName());
        fields.add(hpoTerm.id().getValue());
        fields.add(maxoRelation.toString());
        fields.add(EVIDENCE_CODE);
        // We cannot currently add extension id with this tool
        fields.add(EMPTY_STRING);
        fields.add(EMPTY_STRING);
        // comment,  other
        fields.add(EMPTY_STRING);
        fields.add(EMPTY_STRING);
        // author
        fields.add(orcid);
       // last_updated
        fields.add(EMPTY_STRING);
        // String like 2022-02-15
        LocalDateTime today = LocalDateTime.now();
        fields.add(dtf.format(today));
    }

    public static String getHeader() {
        return header;
    }

    public String getRowAsTsv() {
        return String.join("\t", fields);
    }
}
