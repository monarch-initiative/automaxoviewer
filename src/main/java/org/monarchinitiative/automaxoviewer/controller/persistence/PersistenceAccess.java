package org.monarchinitiative.automaxoviewer.controller.persistence;

import org.monarchinitiative.automaxoviewer.model.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Class to deal with storing the option values in the user home directory automaxiviewer dir.
 * @author Peter Robinson
 */
public class PersistenceAccess {
    private static final Logger LOGGER = LoggerFactory.getLogger(PersistenceAccess.class);

    private static final String AUTOMAXO_VIEWER_DIRNAME = ".automaxoviewer";
    private static final String AUTOMAXO_VIEWER_DIRPATH = System.getProperty("user.home") + File.separator + AUTOMAXO_VIEWER_DIRNAME;
    private static final String HP_JSON_FILE = "hp.json.file";
    private static final String MAXO_JSON_FILE = "maxo.json.file";
    private static final String MONDO_JSON_FILE = "mondo.json.file";
    private static final String USER_ORCID = "user.orcid";
    private static final String AUTOMAXO_VIEWER_LOCATION = AUTOMAXO_VIEWER_DIRPATH + File.separator + "hpo2robot_options.properties";

    private PersistenceAccess() {
    }

    public static Options loadFromPersistence(){
        Options options = new Options();
        File f = new File(AUTOMAXO_VIEWER_LOCATION);
        if (! f.isFile()) {
            // The options file has not been created yet or there is
            // some other problem. Then the user will have to
            // create new options
            return options;
        }
        Properties properties = new Properties();
        try (FileInputStream is = new FileInputStream(AUTOMAXO_VIEWER_LOCATION)) {
            properties.load(is);
            if (properties.containsKey(HP_JSON_FILE))
                options.setHpJsonFile(new File(properties.getProperty(HP_JSON_FILE)));
            if (properties.containsKey(MAXO_JSON_FILE))
                options.setMaxoJsonFile(new File(properties.getProperty(MAXO_JSON_FILE)));
            if (properties.containsKey(MONDO_JSON_FILE))
                options.setMondoJsonFile(new File(properties.getProperty(MONDO_JSON_FILE)));
            if (properties.containsKey(USER_ORCID))
                options.setOrcid(properties.getProperty(USER_ORCID));
        } catch (IOException e) {
            LOGGER.error("Could not initialize properties: {}", e.getMessage());
        }
        return options;
    }


    public void saveOptions(Options options) {
        Properties properties = new Properties();
        properties.setProperty(HP_JSON_FILE, options.getHpJsonFile().getAbsolutePath());
        properties.setProperty(MAXO_JSON_FILE, options.getMaxoJsonFile().getAbsolutePath());
        properties.setProperty(MONDO_JSON_FILE, options.getMondoJsonFile().getAbsolutePath());
        properties.setProperty(USER_ORCID, options.getOrcid());
        try {
            properties.store(new FileOutputStream(AUTOMAXO_VIEWER_LOCATION), null);
        } catch (IOException e) {
            LOGGER.error("Could not store properties file: {}", e.getMessage());
        }
        // prop.store(new FileOutputStream("xyz.properties"), null);
    }


    /**
     * Create the automaxoviewer directory if it does not exist already and
     * save the Options object there as a serialized object
     * Note that Files.createDirectories() creates a new directory and
     * parent directories that do not exist. This method does not throw an
     * exception if the directory already exists.
     */
    public static void saveToPersistence(Options options) {
        Properties properties = new Properties();

        File hpJsonFile = options.getHpJsonFile();
        if (hpJsonFile != null)
            properties.setProperty(HP_JSON_FILE, hpJsonFile.getAbsolutePath());
        File MaxoJsonFile = options.getMaxoJsonFile();
        if (MaxoJsonFile != null)
            properties.setProperty(MAXO_JSON_FILE, MaxoJsonFile.getAbsolutePath());
        File MondoJsonFile = options.getMondoJsonFile();
        if (MondoJsonFile != null)
            properties.setProperty(MONDO_JSON_FILE, MondoJsonFile.getAbsolutePath());
        if (options.getOrcid() != null) {
            properties.setProperty(USER_ORCID, options.getOrcid());
        }

        try {
            Files.createDirectories(Paths.get(AUTOMAXO_VIEWER_DIRPATH));
            try (FileOutputStream os = new FileOutputStream(AUTOMAXO_VIEWER_LOCATION)) {
                properties.store(os, "automaxoviewer");
            }
        } catch (IOException e) {
            LOGGER.error("Could not save properties: {}", e.getMessage());
        }

    }
}
