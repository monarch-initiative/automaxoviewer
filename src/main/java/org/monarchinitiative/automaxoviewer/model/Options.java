package org.monarchinitiative.automaxoviewer.model;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.monarchinitiative.automaxoviewer.controller.widgets.Platform;
import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Stores the current HPO file, ROBOT file, and ORCID identifier of the curator
 * between Automaxoviewer sessions.
 */
public class Options implements Serializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(Options.class);

    public final static String OPTIONS_HEADER_LINE = "! automaxoviewer options file";

    public final static String N_A = "n/a";

    /** Regular expression to check whether an input string is a valid ORCID id. (just the 16 digits) */
    private static final String ORCID_REGEX =
            "^[0-9]{4}-[0-9]{4}-[0-9]{4}-[0-9]{4}$";

    private static final String EMPTY_STRING = "";

    /** Regular expression to check whether an input string is a valid ORCID id. */
    private static final Pattern ORCID_PATTERN = Pattern.compile(ORCID_REGEX);

    private final ObjectProperty<File> hpJsonFile = new SimpleObjectProperty<>();

    private final ObjectProperty<File> maxoJsonFile = new SimpleObjectProperty<>();

    private final ObjectProperty<File> mondoJsonFile = new SimpleObjectProperty<>();

    private final StringProperty orcid = new SimpleStringProperty();

    private final BooleanBinding isReady;




    private boolean isUninitialzedOrValid(String jsonPath) {
        if (jsonPath == null) return false;
        if (jsonPath.equals("not initialized")) return true;
        File jsonFile = new File(jsonPath);
        if (jsonFile.getAbsolutePath().isEmpty()) return true; // uninitialized, OK
        return  (jsonFile.getName().contains(".json"));
    }

    public Options(String hpJsonFile, String maxoFile, String mondoFile, String orcid) {
        this();
        if (! isUninitialzedOrValid(hpJsonFile)) {
            throw new PhenolRuntimeException("Malformed hp.json file: " + hpJsonFile);
        }
        if (! isUninitialzedOrValid(maxoFile)) {
            throw new PhenolRuntimeException("Malformed maxo.json file: " + maxoJsonFile);
        }
        if (! isUninitialzedOrValid(mondoFile)) {
            throw new PhenolRuntimeException("Malformed mondo.json file: " + maxoJsonFile);
        }
        this.hpJsonFile.set(new File(hpJsonFile));
        this.maxoJsonFile.set(new File(maxoFile));
        this.mondoJsonFile.set(new File(mondoFile));
        this.orcid.set(orcid);
        LOGGER.info("hp.json: {}", hpJsonFile);
        LOGGER.info("mondo.json: {}", mondoFile);
        LOGGER.info("maxo.json: {}", maxoFile);
        LOGGER.info("ORCID: {}", orcid);
    }

    public Options(){
        isReady = hpJsonFile.isNotNull().and(maxoJsonFile.isNotNull()).and(orcid.isNotEmpty()).and(mondoJsonFile.isNotNull());
    }



    public File getHpJsonFile() {
        return hpJsonFile.get();
    }

    public void setHpJsonFile(File hpJsonFile) {
        this.hpJsonFile.set(hpJsonFile);
    }

    public File getMondoJsonFile() {
        return mondoJsonFile.get();
    }

    public void setMondoJsonFile(File jsonFile) {
        this.mondoJsonFile.set(jsonFile);
    }

    public ObjectProperty<File> mondoJsonFileProperty() {
        return mondoJsonFile;
    }

    public ObjectProperty<File> hpJsonFileProperty() {
        return hpJsonFile;
    }

    public File getMaxoJsonFile() {
        return maxoJsonFile.get();
    }

    public void setMaxoJsonFile(File maxoJsonFile) {
        this.maxoJsonFile.set(maxoJsonFile);
    }
    public ObjectProperty<File> maxoJsonFileProperty() {
        return maxoJsonFile;
    }


    public String getOrcid() {
        return orcid.get();
    }

    public void setOrcid(String orcid) {
        this.orcid.set(orcid);
    }

    public BooleanBinding isReadyProperty() {
        return isReady;
    }

    public boolean isValid() {
        if (hpJsonFile.get() == null || ! hpJsonFile.get().isFile()) {
            return false;
        }
        if (maxoJsonFile.get() == null || ! maxoJsonFile.get().isFile()) {
            return false;
        }
        if (mondoJsonFile.get() == null || ! mondoJsonFile.get().isFile()) {
            return false;
        }
        // the last thing to check is if the ORCID matches
        String o = orcid.get();
        if (o == null)
            return false;
        final Matcher matcher = ORCID_PATTERN.matcher(o);
        return  matcher.matches();
    }

    @Override
    public String toString() {
        return String.format("""
                                HPO: %s
                                MAXO: %s
                                biocurator: %s
                                valid: %s""",
                hpJsonFile.get(), maxoJsonFile.get(), orcid, isValid());
    }

    public String getErrorMessage() {
        File hp = hpJsonFile.get();
        if (hp == null) {
            return "hp.json not initialized.";
        } else if (!hp.isFile()) {
            return String.format("could not find hp.json at %s", hp.getAbsolutePath());
        }
        File maxoJson = maxoJsonFile.get();
        if (maxoJson == null) {
            return "maxo.json not initialized.";
        } else if (! maxoJson.isFile()) {
            return String.format("could not find maxo.json at %s.", maxoJson.getAbsolutePath());
        }
        final Matcher matcher = ORCID_PATTERN.matcher(orcid.get());
        boolean ORCID_OK = matcher.matches();
        if (! ORCID_OK) {
            return String.format("Malformed ORCID: \"%s\". ", orcid.get());
        }
        return EMPTY_STRING;
    }

    private boolean validOrcid(String orcid) {
        if (orcid == null) return false;
        final Matcher matcher = ORCID_PATTERN.matcher(orcid);
        return matcher.matches();
    }

    private boolean jsonFileValid(File jsonFile, String fname) {
        if (jsonFile == null) return false;
        if (! jsonFile.isFile()) return false;
        return jsonFile.getName().equals(fname);
    }

    public final static String HP_JSON_KEY = "hp.json.file";
    public final static String MAXO_JSON_KEY = "maxo.json.file";
    public final static String MONDO_JSON_KEY = "mondo.json.file";
    public final static String ORCID_KEY = "orcid";

    public void writeOptions() {
        File file = Platform.getAutoMaxoViewerOptionsFile();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))){
            bw.write(OPTIONS_HEADER_LINE + "\n");
            if (jsonFileValid(hpJsonFile.get(),"hp.json")) {
                bw.write(HP_JSON_KEY + ":" + hpJsonFile.get());
            }
            if (jsonFileValid(maxoJsonFile.get(), "maxo.json")) {
                bw.write(MAXO_JSON_KEY + ":" + maxoJsonFile.get());
            }
            if (jsonFileValid(mondoJsonFile.get(), "mondo.json")) {
                bw.write(MONDO_JSON_KEY + ":" + mondoJsonFile.get());
            }
            if (validOrcid(orcid.get())) {
                bw.write(ORCID_KEY + ":" + orcid.get());
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

    public static Map<String, String> readOptions() {
        File autoMaxoViewerOptionsFile = Platform.getAutoMaxoViewerOptionsFile();
        Map<String,String> optionsMap = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(autoMaxoViewerOptionsFile))){
            String line;
            while ((line=br.readLine()) != null) {
                if (line.startsWith("!")) continue;
                String [] fields = line.split(":");
                if (fields.length != 2) {
                    LOGGER.error("Malformed options line with {} fields: {}", fields.length, line);
                    continue;
                }
                String item = fields[0].trim();
                String value = fields[1].trim();
                switch (item) {
                    case HP_JSON_KEY -> optionsMap.put(HP_JSON_KEY, value);
                    case ORCID_KEY -> optionsMap.put(ORCID_KEY, value);
                    case MAXO_JSON_KEY -> optionsMap.put(MAXO_JSON_KEY, value);
                    case MONDO_JSON_KEY -> optionsMap.put(MONDO_JSON_KEY, value);
                    default -> {LOGGER.error("Did not recognize option {}:{}", item, value);}
                }
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        return optionsMap;
    }
}
