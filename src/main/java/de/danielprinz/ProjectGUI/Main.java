package de.danielprinz.ProjectGUI;

import de.danielprinz.ProjectGUI.resources.Setting;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;


public class Main extends Application {

    private final static int WINDOW_WIDTH = 400;
    private final static int WINDOW_HEIGHT = 430;

    private static Stage window;
    private static MenuBar menuBar;

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
        } catch (NullPointerException e) {
            System.err.println("Das angegebene Icon konnte nicht gefunden werden: " + Setting.ICON);
        }

        GridPane mainPane = new GridPane();

        menuBar = new MenuBar();
        menuBar.setMinWidth(WINDOW_WIDTH);
        GridPane.setConstraints(menuBar, 0, 0);
        //File menu
        Menu fileMenu = new Menu("Datei");
        MenuItem load = new MenuItem("Laden");
        MenuItem save = new MenuItem("Speichern unter...");
        MenuItem settings = new MenuItem("Einstellungen");
        settings.setOnAction(e -> {
            //SettingsGUI.display();
        });
        fileMenu.getItems().addAll(load, save, settings);
        //fileMenu.getItems().add(new SeparatorMenuItem());
        menuBar.getMenus().addAll(fileMenu);


        mainPane.getChildren().addAll(menuBar);

        Scene scene = new Scene(mainPane, WINDOW_WIDTH, WINDOW_HEIGHT);
        window.setScene(scene);
        window.show();

    }


    public static Main getInstance() {
        return instance;
    }
}
