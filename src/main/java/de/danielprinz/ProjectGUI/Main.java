package de.danielprinz.ProjectGUI;

import de.danielprinz.ProjectGUI.popupHandler.CloseSaveBox;
import de.danielprinz.ProjectGUI.popupHandler.CloseSaveBoxResult;
import de.danielprinz.ProjectGUI.resources.Setting;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;


public class Main extends Application {

    private final static int WINDOW_WIDTH = 400;
    private final static int WINDOW_HEIGHT = 430;
    private final static String WINDOW_TITLE = "ProjectGUI";

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
        window.setTitle(WINDOW_TITLE);
        try {
            window.getIcons().add(Setting.ICON.getResource().convertToImage());
        } catch (NullPointerException e) {
            System.err.println("Das angegebene Icon konnte nicht gefunden werden: " + Setting.ICON);
        }
        window.setOnCloseRequest(e -> {
            e.consume();
            close();
        });

        GridPane mainPane = new GridPane();


        menuBar = new MenuBar();
        menuBar.setMinWidth(WINDOW_WIDTH);
        GridPane.setConstraints(menuBar, 0, 0);
        //File menu
        Menu fileMenu = new Menu("Datei");
        MenuItem load = new MenuItem("Laden");
        load.setOnAction(e -> {
            // TODO
        });
        MenuItem save = new MenuItem("Speichern unter...");
        save.setOnAction(e -> {
            // TODO
        });
        MenuItem settings = new MenuItem("Einstellungen");
        settings.setOnAction(e -> {
            // TODO
        });

        MenuItem close = new MenuItem("Beenden");
        close.setOnAction(e -> close());

        fileMenu.getItems().addAll(load, save, settings, new SeparatorMenuItem(), close);

        menuBar.getMenus().addAll(fileMenu);


        mainPane.getChildren().addAll(menuBar);

        Scene scene = new Scene(mainPane, WINDOW_WIDTH, WINDOW_HEIGHT);
        window.setScene(scene);
        window.show();

    }

    private void close() {
        CloseSaveBoxResult result = CloseSaveBox.display(WINDOW_TITLE, "Möchten Sie die Änderungen an XXX speichern?"); // TODO show the current filename
        if(result == null || result.equals(CloseSaveBoxResult.CANCEL)) return;
        else if(result.equals(CloseSaveBoxResult.SAVE)) {
            // TODO save current document
            System.out.println("saving...");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        window.close();
        Platform.exit();
        System.exit(0);
    }


    public static Main getInstance() {
        return instance;
    }
}
