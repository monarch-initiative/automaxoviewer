package org.monarchinitiative.automaxoviewer.json;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

public class AutomaxoJsonTest {

    @Test
    public void testImport() {
        String input = "/Users/robin/GIT/automaxo/data/stickler_syndrome/detailed_post_ontoGPT.json";
        File automaxoFile = new File(input);
        AutomaxoJson automaxo = null;
        if (automaxoFile.isFile()) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                automaxo = objectMapper.readValue(automaxoFile, AutomaxoJson.class);

            } catch (Exception exc) {
                exc.printStackTrace();
            }
        }
        Assertions.assertNotNull(automaxo);
    }

    @Test
    public void whenSerializingUsingJsonPropertyOrder_thenCorrect()
            throws IOException {
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        InputStream is = AutomaxoJsonTest.class.getClassLoader().getResourceAsStream("triplet1.json");
        TripletItem tripletItem1 = mapper.readValue(is, TripletItem.class);
        assertNotNull(tripletItem1);
        assertEquals(1, tripletItem1.getCount());
        assertEquals( "maxo:0000004", tripletItem1.getTriplet().getMaxo());
        assertEquals( "surgery", tripletItem1.getTriplet().getMaxo_label());
        assertEquals(0, tripletItem1.getTriplet().getPotential_maxo().length);
        assertTrue(tripletItem1.getSource().containsKey("37567029"));
    }


    @Test
    public void whenSerializingUsingJsonPropertyOrder_thenCorrectTriplet9()
            throws IOException {
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        InputStream is = AutomaxoJsonTest.class.getClassLoader().getResourceAsStream("triplet9.json");
        TripletItem tripletItem9 = mapper.readValue(is, TripletItem.class);
        assertNotNull(tripletItem9);
        assertEquals(9, tripletItem9.getCount());
        assertEquals( "maxo:0001001", tripletItem9.getTriplet().getMaxo());
        assertEquals( "gene therapy", tripletItem9.getTriplet().getMaxo_label());
        assertEquals(0, tripletItem9.getTriplet().getPotential_maxo().length);
        assertTrue(tripletItem9.getSource().containsKey("36485129"));
    }


    @Test
    public void whenSerializingUsingJsonPropertyOrder_thenCorrectTriplets()
            throws IOException {
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        InputStream is = AutomaxoJsonTest.class.getClassLoader().getResourceAsStream("threeTriplets.json");
        AutomaxoJson automax = mapper.readValue(is, AutomaxoJson.class);
        TripletItem[] tripletItemList = automax.getTriplets();
        assertEquals(3, tripletItemList.length);
    }
}
