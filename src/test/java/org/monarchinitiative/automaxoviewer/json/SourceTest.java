package org.monarchinitiative.automaxoviewer.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SourceTest {


    @Test
    public void whenSerializingUsingJsonPropertyOrder_thenCorrect()
            throws JsonProcessingException {

        String text = "Renal Medullary Carcinoma: A Surveillance, Epidemiology, and End Results (SEER) Analysis.";
        Map<String,String> mesh_info_map = new HashMap<>();
        mesh_info_map.put("D011379", "Prognosis");

        PubMedSource bean = new PubMedSource(text, mesh_info_map);

        String key = "37567029";
        Map<String,PubMedSource> source_map = new HashMap<>();
        source_map.put(key, bean);

        String result = new ObjectMapper().writeValueAsString(source_map);
        assertTrue(result.contains("Renal Medullary Carcinoma"));
        assertTrue(result.contains("D011379"));
        assertTrue(result.contains("37567029"));
    }
}
