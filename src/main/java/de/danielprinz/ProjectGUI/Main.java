package de.danielprinz.ProjectGUI;

import de.danielprinz.ProjectGUI.files.OpenFileHandler;
import de.danielprinz.ProjectGUI.popupHandler.CloseSaveBox;
import de.danielprinz.ProjectGUI.popupHandler.CloseSaveBoxResult;
import de.danielprinz.ProjectGUI.resources.Settings;
import de.danielprinz.ProjectGUI.resources.Strings;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.File;


public class Main extends Application {

    private final static int WINDOW_WIDTH = 400;
    private final static int WINDOW_HEIGHT = 430;
    public final static String WINDOW_TITLE = "ProjectGUI";

    private static Stage window;
    private static MenuBar menuBar;

    private static Main instance;
    private static OpenFileHandler openFileHandler;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        instance = this;
        openFileHandler = new OpenFileHandler(new File("hi.png")); // TODO call when file is opened

        window = primaryStage;
        window.setTitle(WINDOW_TITLE);
        try {
            window.getIcons().add(Settings.ICON.getResource().convertToImage());
        } catch (NullPointerException e) {
            System.err.println(Strings.ICON_NOT_FOUND.format(Settings.ICON));
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
        Menu fileMenu = new Menu(Strings.MENUBAR_FILE.format());
        MenuItem load = new MenuItem(Strings.MENUBAR_LOAD.format());
        load.setOnAction(e -> {
            // TODO
        });
        MenuItem save = new MenuItem(Strings.MENUBAR_SAVEAS.format());
        save.setOnAction(e -> {
            // TODO
        });
        MenuItem settings = new MenuItem(Strings.MENUBAR_SETTINGS.format());
        settings.setOnAction(e -> {
            // TODO
        });

        MenuItem close = new MenuItem(Strings.MENUBAR_CLOSE.format());
        close.setOnAction(e -> close());

        fileMenu.getItems().addAll(load, save, settings, new SeparatorMenuItem(), close);

        menuBar.getMenus().addAll(fileMenu);


        mainPane.getChildren().addAll(menuBar);

        Scene scene = new Scene(mainPane, WINDOW_WIDTH, WINDOW_HEIGHT);
        window.setScene(scene);
        window.show();

    }

    private void close() {
        CloseSaveBoxResult result = openFileHandler.showDialogBox();
        if(result.equals(CloseSaveBoxResult.CANCEL)) return;
        openFileHandler.save(result);

        window.close();
        Platform.exit();
        System.exit(0);
    }


    public static Main getInstance() {
        return instance;
    }
}
