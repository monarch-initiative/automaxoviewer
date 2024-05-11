package org.monarchinitiative.automaxoviewer.json;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

public class AutomaxoJsonText {


    @Test
    public void whenSerializingUsingJsonPropertyOrder_thenCorrect()
            throws IOException {
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        InputStream is = AutomaxoJsonText.class.getClassLoader().getResourceAsStream("triplet1.json");
        TripletItem tripletItem1 = mapper.readValue(is, TripletItem.class);
        assertNotNull(tripletItem1);
        assertEquals(1, tripletItem1.getCount());
        assertEquals( "maxo:0000004", tripletItem1.getTriplet().getMaxo());
        assertEquals( "surgery", tripletItem1.getTriplet().getMaxo_label());
        assertEquals(0, tripletItem1.getTriplet().getPotential_maxo().length);
       // assertTrue(tripletItem1.getSource().getSource().containsKey("37567029"));
    }



}
