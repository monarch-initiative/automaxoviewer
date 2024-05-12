package org.monarchinitiative.automaxoviewer.controller.widgets;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Platform {

    /**
     * Get path to directory where HRMD-gui stores global settings.
     * The path depends on underlying operating system. Linux, Windows & OSX
     * currently supported.
     * @return File to directory
     */
    public static File getAutomaxoDir() {
        CurrentPlatform platform = figureOutPlatform();
        final String APPLICATION_NAME = "automaxoviewer";
        final String USER_HOME = System.getProperty("user.home");
        File linuxPath = new File(String.format("%s%s.%s", USER_HOME, File.separator, APPLICATION_NAME));
        File osxPath = linuxPath;
        // no dot here
        File windowsPath = new File(String.format("%s%s%s", USER_HOME, File.separator, APPLICATION_NAME));


        switch (platform) {
            case LINUX -> {
                createDirIfNotExists(linuxPath);
                return linuxPath;
            }
            case WINDOWS -> {
                createDirIfNotExists(windowsPath);
                return windowsPath;
            }
            case OSX -> {
                createDirIfNotExists(osxPath);
                return osxPath;
            }
            case UNKNOWN -> {
                return null;
            }
            default -> {
                Alert a = new Alert(AlertType.ERROR);
                a.setTitle("Find GUI config dir");
                a.setHeaderText(null);
                a.setContentText(String.format("Unrecognized platform: \"%s\"", platform));
                a.showAndWait();
                return null;
            }
        }
    }


    public static File getAutoMaxoViewerOptionsFile() {
        File dir = getAutomaxoDir();
        File file = new File(dir + File.separator + "automaxoviewer.txt");
        if (! file.isFile()) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                bw.write("! automaxoviewer.txt options file\n");
            } catch (IOException e) {
                PopUps.showErrorMessage("Could not initialize automaxoviewer.txt file");
            }
        }
        return file;
    }



    private static void createDirIfNotExists(File dirPath) {

        try {
            if (!dirPath.isDirectory()) {
                Files.createDirectories(Paths.get(dirPath.toURI()));
            }
        } catch (IOException e) {
            Alert a = new Alert(AlertType.ERROR);
            a.setTitle("Error creating automaxoviewer directory");
            a.setHeaderText(null);
            a.setContentText(String.format("Could not create directory: \"%s\"", e.getMessage()));
            a.showAndWait();
        }
    }

    /**
     * Get the absolute path to the log file.
     * @return the absolute path,e.g., /home/user/.automaxoviewer/automaxoviewer.log
     */
    public static String getAbsoluteLogPath() {
        File dir = getAutomaxoDir();
        return dir + File.separator +  "automaxoviewer.log";
    }


    /* Based on this post: http://www.mkyong.com/java/how-to-detect-os-in-java-systemgetpropertyosname/ */
    private static CurrentPlatform figureOutPlatform() {
        String osName = System.getProperty("os.name").toLowerCase();

        if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) {
            return CurrentPlatform.LINUX;
        } else if (osName.contains("win")) {
            return CurrentPlatform.WINDOWS;
        } else if (osName.contains("mac")) {
            return CurrentPlatform.OSX;
        } else {
            return CurrentPlatform.UNKNOWN;
        }
    }


    private enum CurrentPlatform {
        LINUX("Linux"),
        WINDOWS("Windows"),
        OSX("Mac OSX"),
        UNKNOWN("Unknown");
        private final String name;

        CurrentPlatform(String n) {this.name = n; }

        @Override
        public String toString() { return this.name; }
    }



}
