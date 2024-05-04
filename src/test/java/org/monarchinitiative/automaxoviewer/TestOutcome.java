package org.monarchinitiative.automaxoviewer;

import java.util.function.Supplier;

public sealed interface TestOutcome {
    record Success(int value) implements TestOutcome {}
    record Failure(Supplier<? extends RuntimeException> exceptionSupplier) implements TestOutcome {}
}

