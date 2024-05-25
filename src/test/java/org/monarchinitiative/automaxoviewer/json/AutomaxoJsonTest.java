package org.monarchinitiative.automaxoviewer.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.automaxoviewer.controller.widgets.PopUps;

import java.io.File;

public class AutomaxoJsonTest {


    @Test
    public void testImport() {
        String input = "/Users/robin/GIT/automaxo/data/stickler_syndrome/detailed_post_ontoGPT.json";
        File automaxoFile = new File(input);
        AutomaxoJson automaxo = null;
        if (automaxoFile != null && automaxoFile.isFile()) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                automaxo = objectMapper.readValue(automaxoFile, AutomaxoJson.class);

            } catch (Exception exc) {
                exc.printStackTrace();
            }
        }
        Assertions.assertNotNull(automaxo);
    }

}
