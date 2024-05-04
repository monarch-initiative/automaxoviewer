package org.monarchinitiative.automaxoviewer;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.stage.Stage;
import org.monarchinitiative.automaxoviewer.controller.persistence.PersistenceAccess;
import org.monarchinitiative.automaxoviewer.model.Options;
import org.monarchinitiative.automaxoviewer.view.ViewFactory;

public class Launcher extends Application {

    ViewFactory viewFactory = null;

    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage stage) {
        Options options = PersistenceAccess.loadFromPersistence();
        HostServices hostServices = getHostServices();
        viewFactory = new ViewFactory(options, hostServices);
        viewFactory.showMainWindow();
        stage.setOnCloseRequest(e -> PersistenceAccess.saveToPersistence(viewFactory.getOptions()));
    }

    @Override
    public void stop() {
        if (viewFactory != null) {
            PersistenceAccess.saveToPersistence(viewFactory.getOptions());
        }
    }
}

