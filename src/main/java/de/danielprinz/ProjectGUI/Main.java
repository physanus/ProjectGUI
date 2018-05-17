package de.danielprinz.ProjectGUI;

import de.danielprinz.ProjectGUI.exceptions.UnsupportedFileTypeException;
import de.danielprinz.ProjectGUI.files.OpenFileHandler;
import de.danielprinz.ProjectGUI.gui.MouseListener;
import de.danielprinz.ProjectGUI.io.ConnectionHandler;
import de.danielprinz.ProjectGUI.popupHandler.CloseSaveBoxResult;
import de.danielprinz.ProjectGUI.popupHandler.FileErrorBox;
import de.danielprinz.ProjectGUI.popupHandler.FileErrorType;
import de.danielprinz.ProjectGUI.resources.SettingsHandler;
import de.danielprinz.ProjectGUI.resources.Strings;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.io.File;


public class Main extends Application {

    public static final boolean DEBUG = true;

    private final static int WINDOW_WIDTH = 800;
    private final static int WINDOW_HEIGHT = 860;
    public final static String WINDOW_TITLE = "ProjectGUI";

    private static Stage window;
    private static MenuBar menuBar;
    private static ImageView preview;
    private static ImageView crosshair;

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
            File debugTest = new File("C:\\Users\\prinz\\ownCloud\\Technikum\\BEL4\\EZB Echtzeitbetriebssysteme\\Tasks\\Projekt\\ProjectGUI\\src\\main\\resources");
            if (debugTest.exists()) {
                // debug
                fileChooser.setInitialDirectory(debugTest);
            }
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("HP Graphics Language-Datei", "*.hpgl"));
            File file = fileChooser.showOpenDialog(window);
            if (file == null) {
                // no file was selected/dialog was cancelled
                FileErrorBox.display(FileErrorType.NO_SUCH_FILE, WINDOW_TITLE, Strings.FILE_ERROR_NO_SUCH_FILE.format());
                return;
            }

            openFileHandler.read(file); // TODO make async
            BufferedImage bufferedImage;
            try {

                bufferedImage = openFileHandler.renderImage(500, 500, true);
                preview.setFitWidth(0);
                preview.setFitHeight(0);
                preview.setImage(SwingFXUtils.toFXImage(bufferedImage, null));

            } catch (UnsupportedFileTypeException e1) {
                FileErrorBox.display(FileErrorType.NOT_COMPATIBLE, WINDOW_TITLE, Strings.FILE_ERROR_NOT_COMPATIBLE.format());
                return;
            }
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

        GridPane innerPane = new GridPane();
        innerPane.setPadding(new Insets(10, 10, 10, 10));
        innerPane.setVgap(8);
        innerPane.setHgap(10);
        GridPane.setConstraints(innerPane, 0, 1);

        preview = new ImageView();
        preview.setFitHeight(500);
        preview.setFitWidth(500);
        GridPane.setConstraints(preview, 0, 0);

        /*
         * Create the crosshair
         */
        crosshair = new ImageView();
        crosshair.setImage(SettingsHandler.JOYSTICK_CROSSHAIRS);

        /**
         * Create the draggable overlay
         */
        Circle overlay = new Circle();
        overlay.setFill(Color.RED);
        overlay.setOpacity(0.2);
        overlay.setCursor(Cursor.HAND);
        overlay.setCenterX((int) crosshair.getImage().getWidth() / 2);
        overlay.setCenterY((int) crosshair.getImage().getHeight() / 2);
        overlay.setRadius((int) crosshair.getImage().getWidth() / 2);
        MouseListener mouseListener = new MouseListener(crosshair, 25, 0.5);
        overlay.setOnMousePressed(mouseListener.new MouseListenerPress());
        overlay.setOnMouseDragged(mouseListener.new MouseListenerDrag());
        overlay.setOnMouseReleased(mouseListener.new MouseListenerReleased());

        /**
         * Merge the controller
         */
        Pane crosshairPane = new Pane();
        Pane overlayPane = new Pane();
        crosshairPane.getChildren().add(crosshair);
        overlayPane.getChildren().add(overlay);

        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(crosshairPane, overlayPane); // overlayPane needs to be the last element being added to the stackPane!

        GridPane.setConstraints(stackPane, 1, 0);

        innerPane.getChildren().addAll(preview, stackPane);
        mainPane.getChildren().addAll(menuBar, innerPane);

        Scene scene = new Scene(mainPane, WINDOW_WIDTH, WINDOW_HEIGHT);
        window.setScene(scene);
        window.show();


        // Connect to the serial device, TODO: move to a ui-button
        //connectionHandler.connect("COM5");

        if(DEBUG) {
            new Thread(() -> {
                File laptop = new File("C:\\Users\\prinz\\ownCloud\\Technikum\\BEL4\\EZB Echtzeitbetriebssysteme\\Tasks\\Projekt\\ProjectGUI\\src\\main\\resources");
                File computer = new File("F:\\owncloud\\Technikum\\BEL4\\EZB Echtzeitbetriebssysteme\\Tasks\\Projekt\\ProjectGUI\\src\\main\\resources");
                File file;
                if(laptop.exists()) file = new File(laptop, "\\FH_Technikum_Wien_logo.hpgl");
                else file = new File(computer, "\\FH_Technikum_Wien_logo.hpgl");

                openFileHandler.read(file);
                BufferedImage bufferedImage;
                try {

                    bufferedImage = openFileHandler.renderImage(500, 500, true);
                    preview.setFitWidth(0);
                    preview.setFitHeight(0);
                    preview.setImage(SwingFXUtils.toFXImage(bufferedImage, null));

                } catch (UnsupportedFileTypeException e1) {
                    FileErrorBox.display(FileErrorType.NOT_COMPATIBLE, WINDOW_TITLE, Strings.FILE_ERROR_NOT_COMPATIBLE.format());
                    return;
                }
            }).start();
        }

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
