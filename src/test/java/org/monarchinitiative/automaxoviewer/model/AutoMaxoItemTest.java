package org.monarchinitiative.automaxoviewer.model;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AutoMaxoItemTest {

    private final static String EMPTY_STR = "";

    /**
     * Citation	Subject	Subject Label	Non grounded Subject	Potential MAXO	Predicate	Object	Object Label	Non grounded Object	Potential HP	Qualifier	Qualifier Label	Non grounded Qualifier	Potential MONDO	Subject Qualifier	Subject Extension	Object Extension	Count
     * 10894210	maxo:0001001	gene therapy		[]	treats			canavan disease	[]				[]				1
     */
    private static final AutoMaxoItem item1 = new AutoMaxoItem("10894210", "maxo:0001001", "gene therapy");
    private static final List<String> expected1 = List.of("PMID:10894210", "MAXO:0001001","gene therapy");

    private static final AutoMaxoItem item2 = new AutoMaxoItem("pmid:11279290", EMPTY_STR, EMPTY_STR);
    private static final List<String> expected2 = List.of("PMID:11279290", EMPTY_STR, EMPTY_STR);



    static Stream<Arguments> testCasesForPmid() {
        return Stream.of(
            Arguments.of("Example 1", item1, expected1),
                Arguments.of("Example 2", item2, expected2)
        );
    }


    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("testCasesForPmid")
    void testEvaluate(String description, AutoMaxoItem amitem, List<String> expected) { // Changes here
        String expectedPmid = expected.get(0);
        String expectedMaxoSubject = expected.get(1);
        Optional<String> opt = amitem.pmid();
        assertTrue(opt.isPresent());
        assertEquals(expectedPmid, opt.get());
        opt = amitem.maxoSubject();
        if (! expectedMaxoSubject.isEmpty()) {
            opt = amitem.maxoSubject();
            assertTrue(opt.isPresent());
            assertEquals(expectedMaxoSubject, opt.get());
        } else {
            assertTrue(opt.isEmpty());
        }

        String expectedSubjectLabel = expected.get(2);
        assertEquals(expectedSubjectLabel, amitem.getSubjectLabel());

    }


}
