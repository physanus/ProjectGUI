package de.danielprinz.ProjectGUI;

import de.danielprinz.ProjectGUI.resources.Setting;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;


public class Main extends Application {

    private static Stage window;

    private static Main instance;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        instance = this;

        window = primaryStage;
        window.setTitle("ProjectGUI");
        try {
            window.getIcons().add(Setting.ICON.getResource().convertToImage());
        } catch (IOException e) {
            System.err.println("Das angegebene Icon konnte nicht gefunden werden: " + Setting.ICON);
        }

        GridPane mainPane = new GridPane();


        Scene scene = new Scene(mainPane, 400, 430);
        window.setScene(scene);
        window.show();

    }


    public static Main getInstance() {
        return instance;
    }
}
