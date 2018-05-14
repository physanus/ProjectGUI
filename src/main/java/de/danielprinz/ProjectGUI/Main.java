package de.danielprinz.ProjectGUI;

import de.danielprinz.ProjectGUI.files.OpenFileHandler;
import de.danielprinz.ProjectGUI.io.ConnectionHandler;
import de.danielprinz.ProjectGUI.popupHandler.CloseSaveBox;
import de.danielprinz.ProjectGUI.popupHandler.CloseSaveBoxResult;
import de.danielprinz.ProjectGUI.popupHandler.FileErrorBox;
import de.danielprinz.ProjectGUI.popupHandler.FileErrorType;
import de.danielprinz.ProjectGUI.resources.SettingsHandler;
import de.danielprinz.ProjectGUI.resources.Strings;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
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
    private static ConnectionHandler connectionHandler;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        SettingsHandler.checkAvailableResources();

        instance = this;
        openFileHandler = new OpenFileHandler(new File("hi.png")); // TODO call when file is opened
        connectionHandler = new ConnectionHandler();

        window = primaryStage;
        window.setTitle(WINDOW_TITLE);
        if(SettingsHandler.checkAvailableResources())
            window.getIcons().add(SettingsHandler.APP_ICON);

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
        MenuItem open = new MenuItem(Strings.MENUBAR_OPEN.format());
        open.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(Strings.MENUBAR_OPEN.format());
            // TODO to configure
            //fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("image", "*.jpg"));
            File file = fileChooser.showOpenDialog(window);
            if(file == null) {
                // no file was selected/dialog was cancelled
                FileErrorBox.display(FileErrorType.NO_SUCH_FILE, WINDOW_TITLE, Strings.FILE_ERROR_NO_SUCH_FILE.format());
                return;
            }

            System.out.println(file == null ? "null" : file.getAbsolutePath());
            openFileHandler.read(file);

        });
        MenuItem save = new MenuItem(Strings.MENUBAR_SAVEAS.format());
        save.setOnAction(e -> {

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(Strings.MENUBAR_OPEN.format());
            // TODO to configure
            //fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("image", "*.jpg"));
            File file = fileChooser.showOpenDialog(window);
            if(file == null) {
                // no file was selected/dialog was cancelled
                FileErrorBox.display(FileErrorType.NO_SUCH_FILE, WINDOW_TITLE, Strings.FILE_ERROR_NO_SUCH_FILE.format());
                return;
            }

            System.out.println(file == null ? "null" : file.getAbsolutePath());
            openFileHandler.save();


        });
        MenuItem settings = new MenuItem(Strings.MENUBAR_SETTINGS.format());
        settings.setOnAction(e -> {
            // TODO
        });

        MenuItem close = new MenuItem(Strings.MENUBAR_CLOSE.format());
        close.setOnAction(e -> close());

        fileMenu.getItems().addAll(open, save, settings, new SeparatorMenuItem(), close);

        menuBar.getMenus().addAll(fileMenu);


        mainPane.getChildren().addAll(menuBar);

        Scene scene = new Scene(mainPane, WINDOW_WIDTH, WINDOW_HEIGHT);
        window.setScene(scene);
        window.show();


        // Connect to the serial device, TODO: move to a ui-button
        connectionHandler.connect("COM5");

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


    /**
     * Adds the given line to a text->scrollPane
     * @param line The line
     */
    public static void addToCmdWindow(String line) {
        // TODO implement
        //Platform.runLater(() -> text.setText(text.getText().equals("") ? text.getText() + line : text.getText() + "\n" + line));
        //scrollPane.setVvalue(1); // scroll to the bottom
        System.out.println("DEBUG: " + line);
    }

    /**
     * Clears the text of the cmdWindow
     */
    public static void clearCmdWindow() {
        //Platform.runLater(() -> text.setText(""));
    }


    public static ConnectionHandler getConnectionHandler() {
        return connectionHandler;
    }
}
