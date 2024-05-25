package org.monarchinitiative.automaxoviewer.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;



public class PubMedSourceTest {

    @Test
    public void whenSerializingUsingJsonPropertyOrder_thenCorrect()
            throws JsonProcessingException {
        String title = "Renal Medullary Carcinoma";
        String abstractText = "A Surveillance, Epidemiology, and End Results (SEER) Analysis.";
        Map<String,String> mesh_info_map = new HashMap<>();
        mesh_info_map.put("D011379", "Prognosis");

        PubMedSource bean = new PubMedSource(title, abstractText, mesh_info_map);

        String result = new ObjectMapper().writeValueAsString(bean);
        assertTrue(result.contains("Renal Medullary Carcinoma"));
        assertTrue(result.contains("D011379"));
    }
}
