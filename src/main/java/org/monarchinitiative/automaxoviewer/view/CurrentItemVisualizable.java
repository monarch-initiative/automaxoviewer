package org.monarchinitiative.automaxoviewer.view;


import org.monarchinitiative.automaxoviewer.model.AutoMaxoRow;
import org.monarchinitiative.phenol.ontology.data.Term;

/**
 * A class to simplify display of current status in the Webengine
 */
public class CurrentItemVisualizable {

    private final String mondoString;
    private final String hpoString;

    private final String maxoString;

    private final int totalAnnots;
    private final String annotFile;
    private final String inputFile;

    private final AutoMaxoRow item;

    public CurrentItemVisualizable(AutoMaxoRow currentRow,
                                   Term hpoTerm,
                                   Term maxoTerm,
                                   Term mondoTerm,
                                   int totalAnnotationsToDate,
                                   String annotationFile,
                                   String inputFile) {
        item = currentRow;
        if (mondoTerm != null) {
            mondoString = String.format("%s (%s)", mondoTerm.getName(), mondoTerm.id().getValue());
        } else {
            mondoString = "Need MONDO term";
        }
        if (hpoTerm != null) {
            hpoString = String.format("%s (%s)", hpoTerm.getName(), hpoTerm.id().getValue());
        } else {
            hpoString = "Need HPO term";
        }
        if (maxoTerm != null) {
            maxoString = String.format("%s (%s)", maxoTerm.getName(), maxoTerm.id().getValue());
        } else {
            maxoString = "Need MAxO term";
        }
        this.totalAnnots = totalAnnotationsToDate;
        this.annotFile = annotationFile;
        this.inputFile = inputFile;
    }

    public String getMondoString() {
        return mondoString;
    }

    public String getHpoString() {
        return hpoString;
    }

    public String getMaxoString() {
        return maxoString;
    }

    public int getTotalAnnots() {
        return totalAnnots;
    }

    public String getAnnotFile() {
        return annotFile;
    }

    public String getInputFile() {
        return inputFile;
    }

    public AutoMaxoRow getItem() {
        return item;
    }
}
