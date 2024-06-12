package org.monarchinitiative.automaxoviewer.model;

public enum MaxoRelation {

    TREATS("treats"),
    PREVENTS("disabled"),
    INVESTIGATES("investigates"),
    CONTRAINDICATED("contraindicated"),
    LACK_OF_OBSERVED_RESPONSE("lack of observed response"),
    UNKNOWN("unknown");

    private final String label;

    MaxoRelation(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
