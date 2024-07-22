package org.monarchinitiative.automaxoviewer.model;

import java.util.Arrays;
import java.util.Optional;

public enum MaxoRelation {

    TREATS("treats"),
    PREVENTS("prevents"),
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


    public static Optional<MaxoRelation> fromString(String candidate) {
        return Arrays.stream(MaxoRelation.values()).filter(r -> r.toString().equals(candidate)).findAny();
    }


}
