package org.monarchinitiative.automaxoviewer.model;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Citation	Subject	Subject Label	Predicate	Object	Object Label	Qualifier	Qualifier Label	Subject Qualifier	Subject Extension	Object Extension	Count
 * <ol>
 *     <li>Citation:25840575 (i.e., a PMID)</li>
 *     <li>Subject:  maxo:0001001	</li>
 *     <li>Subject Label: gene therapy (corresponds to Subject)</li>
 *     <li>Non grounded Subject: (13)c magnetic resonance spectroscopy (mrs) technique (only used if no MAxO subject found)</li>
 *     <li>Potential MAXO: [('MAXO:0009070', 'SPECT')]</li>
 *     <li>Predicate: treats, measures, investigates</li>
 *     <li>Object: hp:0002415</li>
 *     <li>Object Label: leukodystrophies (Note -- may not be the primary label)</li>
 *     <li>Potential HP: [('HP:0001370', 'RA')]</li>
 *     <li>Qualifier: mondo:0010079</li>
 *     <li>Qualifier Label: cystic fibrosis</li>
 *     <li> Non grounded Qualifier: fanconi anemia</li>
 *     <li>Potential MONDO: [('MONDO:0002280', 'Fanconi Anemia'), ('MONDO:0002280', 'Fanconi anemia')]</li>
 *     <li>Subject Qualifier: with raav-haspa-gfp</li>
 *     <li>Subject Extension: chebi:17234</li>
 *     <li>Object Extension: n-acetyl-l-aspartate (naa)</li>
 *     <li>Count: 3 (number of times this combination seen)</li>
 * </ol>
 */
public class AutoMaxoItem {
    private static final Logger LOGGER = LoggerFactory.getLogger(AutoMaxoItem.class);
    private final String pmidAsTermId;

    private final String subjectMAxOId;

    private final String subjectLabel;


    public String getSubjectLabel() {
        return subjectLabel;
    }

    public AutoMaxoItem(String pmid,
                        String subject,
                        String subjtLabel) {
        pmidAsTermId = formatAsTermIdOrNull(pmid, "PMID");
        subjectMAxOId = formatAsTermIdOrNull(subject, "MAXO");
        this.subjectLabel = subjtLabel;
    }


    private String formatAsTermIdOrNull(String tidString, String expectedPrefix) {
        if (tidString == null) {
            return null;
        }
        if (! tidString.contains(":")) {
            tidString = String.format("%s:%s", expectedPrefix, tidString);
        }
        String[] fields = tidString.split(":");
        if (fields.length != 2) {
            LOGGER.error("Malformed ontology term: \"{}\".", tidString);
            return null;
        }
        if (! fields[0].equalsIgnoreCase(expectedPrefix)) {
            LOGGER.error("Expected prefix \"{}\" but got \"{}\".", expectedPrefix, fields[0]);
            return null;
        }
        return String.format("%s:%s", expectedPrefix, fields[1].trim());
    }



    public Optional<String> pmid() {
        return Optional.ofNullable(pmidAsTermId);
    }

    public Optional<String> maxoSubject() {
        return Optional.ofNullable(subjectMAxOId);
    }




    public static List<AutoMaxoItem> parseAutoMaxoItems(File source) {
        List<AutoMaxoItem> items = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(source))) {
           String line;
           while ((line = br.readLine()) != null) {
               String[] fields = line.split("\t");
               String pmid = fields[0];
               String subject = fields[1];
               String subjtLabel = fields[2];
               AutoMaxoItem item = new AutoMaxoItem(pmid, subject, subjtLabel);
               items.add(item);
           }
           return items;
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        // if we get here, there was an error, return an empty list
        return List.of();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.pmidAsTermId, this.subjectLabel, this.subjectMAxOId);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AutoMaxoItem item) {
            return item.subjectMAxOId.equals(this.subjectMAxOId) &&
                    item.subjectLabel.equals(this.subjectLabel) &&
                    item.pmidAsTermId.equals(this.pmidAsTermId);
        } else {
            return false;
        }
    }
}
