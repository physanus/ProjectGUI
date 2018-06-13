package de.danielprinz.ProjectGUI;

import de.danielprinz.ProjectGUI.drawing.DrawHelper;
import de.danielprinz.ProjectGUI.exceptions.NoSuchFileException;
import de.danielprinz.ProjectGUI.exceptions.SerialConnectionException;
import de.danielprinz.ProjectGUI.exceptions.UnsupportedFileTypeException;
import de.danielprinz.ProjectGUI.files.OpenFileHandler;
import de.danielprinz.ProjectGUI.gui.MouseListener;
import de.danielprinz.ProjectGUI.io.ConnectionHandler;
import de.danielprinz.ProjectGUI.popupHandler.*;
import de.danielprinz.ProjectGUI.resources.CommandType;
import de.danielprinz.ProjectGUI.resources.SettingsHandler;
import de.danielprinz.ProjectGUI.resources.Strings;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
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

    public static boolean isUIDisabled = false;

    public static final boolean DEBUG = false;

    private final static int WINDOW_WIDTH = 800;
    private final static int WINDOW_HEIGHT = 860;
    public final static String WINDOW_TITLE = "ProjectGUI";

    private static Stage window;
    private static MenuBar menuBar;
    private static ImageView preview;
    private static ImageView crosshair;
    private static Circle overlay;
    private static Button penToggle;
    private static Button draw;

    private static OpenFileHandler openFileHandler;
    private static ConnectionHandler connectionHandler;
    private static MouseListener mouseListener;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        SettingsHandler.checkAvailableResources();

        openFileHandler = new OpenFileHandler();
        connectionHandler = new ConnectionHandler();

        SettingsHandler.PORT = connectionHandler.autoChooseSerialPort();

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

            if(!(openFileHandler.getOpenFile() == null)) {
                fileChooser.setInitialDirectory(openFileHandler.getOpenFile().getParentFile());
            }
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("HP Graphics Language-Datei", "*.hpgl"));
            File file = fileChooser.showOpenDialog(window);
            if (file == null) {
                // no file was selected/dialog was cancelled
                FileErrorBox.display(FileErrorType.NO_SUCH_FILE, WINDOW_TITLE, Strings.FILE_ERROR_NO_SUCH_FILE.format());
                return;
            }

            new Thread(() -> {
                try {
                    openFileHandler.read(file);
                    BufferedImage bufferedImage = openFileHandler.renderImage(true);
                    preview.setFitWidth(0);
                    preview.setFitHeight(0);
                    preview.setImage(SwingFXUtils.toFXImage(bufferedImage, null));
                } catch (UnsupportedFileTypeException e1) {
                    Platform.runLater(() -> FileErrorBox.display(FileErrorType.NOT_COMPATIBLE, WINDOW_TITLE, Strings.FILE_ERROR_NOT_COMPATIBLE.format()));
                    return;
                } catch (NoSuchFileException e1) {
                    Platform.runLater(() -> FileErrorBox.display(FileErrorType.NO_SUCH_FILE, Main.WINDOW_TITLE, Strings.FILE_ERROR_NO_SUCH_FILE.format()));
                    return;
                }
            }).start();
        });

        MenuItem save = new MenuItem(Strings.MENUBAR_SAVEAS.format());
        save.setOnAction(e -> {

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(Strings.MENUBAR_OPEN.format());

            if(!(openFileHandler.getOpenFile() == null)) {
                fileChooser.setInitialDirectory(openFileHandler.getOpenFile().getParentFile());
            }
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("HP Graphics Language-Datei", "*.hpgl"));
            File file = fileChooser.showSaveDialog(window);
            if(file == null) {
                // no file was selected/dialog was cancelled
                FileErrorBox.display(FileErrorType.NO_SUCH_FILE, WINDOW_TITLE, Strings.FILE_ERROR_NO_SUCH_FILE.format());
                return;
            }

            openFileHandler.saveas(file);

        });
        MenuItem settings = new MenuItem(Strings.MENUBAR_SETTINGS.format());
        settings.setOnAction(e -> {
            SettingsBox.display("Settings");
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

        /*
         * Create the draggable overlay
         */
        int movementRadius = 25;

        overlay = new Circle();
        overlay.setFill(Color.BLACK);
        overlay.setOpacity(0.2);
        overlay.setCursor(Cursor.HAND);

        overlay.setLayoutX((int) crosshair.getImage().getWidth() / 2);
        overlay.setLayoutY((int) crosshair.getImage().getHeight() / 2);
//        overlay.setCenterX(45);
//        overlay.setCenterY(15);
//
//        crosshair.setX(45);
//        crosshair.setY(15);

        //overlay.setRadius((int) crosshair.getImage().getWidth() / 2);
        overlay.setRadius(movementRadius);
        mouseListener = new MouseListener(crosshair, movementRadius, 0.5);
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
        stackPane.setPrefWidth((int) crosshair.getImage().getHeight() * 1.5);
        stackPane.setPrefHeight((int) crosshair.getImage().getWidth() * 1.5);

        GridPane.setConstraints(stackPane, 1, 0);


        /*
         * custom drawing
         */

        GridPane rightSideButtons = new GridPane();
        rightSideButtons.setPadding(new Insets(10, 10, 10, 10));
        rightSideButtons.setVgap(8);
        rightSideButtons.setHgap(10);

        penToggle = new Button("Pen DOWN"); // Pen is up at the moment
        DrawHelper.setCommandType(CommandType.PU); // should be the default anyways
        penToggle.setPrefWidth(100);
        GridPane.setConstraints(penToggle, 0, 2);
        penToggle.setOnAction(e -> {
            if(penToggle.getText().equalsIgnoreCase("Pen UP")) {
                penToggle.setText("Pen DOWN");
                DrawHelper.setCommandType(CommandType.PU);
            } else {
                penToggle.setText("Pen UP");
                DrawHelper.setCommandType(CommandType.PD);
            }
        });

        draw = new Button("Draw");
        draw.setPrefWidth(100);
        GridPane.setConstraints(draw, 0, 3);
        draw.setOnAction(event -> {

            // send the commands via uart
            try {
                disableAll(true);
                connectionHandler.connectIfNotConnected();
                connectionHandler.getSerialWriter().sendUART(openFileHandler.getFileHolder().getSerializedCommands(), true);
            } catch (SerialConnectionException | NullPointerException e) {
                Platform.runLater(() -> ConnectionErrorBox.display(Main.WINDOW_TITLE, Strings.CONNECTION_ERROR_DIALOGUE.format()));
                if(DEBUG) System.err.println("No serial connection could be established");
            }

        });

        rightSideButtons.getChildren().addAll(penToggle, draw);
        GridPane.setConstraints(rightSideButtons, 1, 1);

        GridPane rightSide = new GridPane();
        rightSide.getChildren().addAll(stackPane, rightSideButtons);
        GridPane.setConstraints(rightSide, 1, 0);

        innerPane.getChildren().addAll(preview, rightSide);
        mainPane.getChildren().addAll(menuBar, innerPane);


        disableAll(false);

        Scene scene = new Scene(mainPane, WINDOW_WIDTH, WINDOW_HEIGHT);
        window.setScene(scene);
        window.show();


        if(DEBUG) {
            new Thread(() -> {
                File laptop = new File("C:\\Users\\prinz\\ownCloud\\Technikum\\BEL4\\EZB Echtzeitbetriebssysteme\\Tasks\\Projekt\\ProjectGUI\\src\\main\\resources");
                File computer = new File("F:\\owncloud\\Technikum\\BEL4\\EZB Echtzeitbetriebssysteme\\Tasks\\Projekt\\ProjectGUI\\src\\main\\resources");
                File file;
                if(laptop.exists()) file = new File(laptop, "\\FH_Technikum_Wien_logo.hpgl");
                else file = new File(computer, "\\FH_Technikum_Wien_logo.hpgl");

                try {

                    openFileHandler.read(file);
                    BufferedImage bufferedImage = openFileHandler.renderImage(true);
                    preview.setFitWidth(0);
                    preview.setFitHeight(0);
                    preview.setImage(SwingFXUtils.toFXImage(bufferedImage, null));

                } catch (UnsupportedFileTypeException e1) {
                    Platform.runLater(() -> FileErrorBox.display(FileErrorType.NOT_COMPATIBLE, WINDOW_TITLE, Strings.FILE_ERROR_NOT_COMPATIBLE.format()));
                    return;
                } catch (NoSuchFileException e) {
                    Platform.runLater(() -> FileErrorBox.display(FileErrorType.NO_SUCH_FILE, Main.WINDOW_TITLE, Strings.FILE_ERROR_NO_SUCH_FILE.format()));
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



    public static ConnectionHandler getConnectionHandler() {
        return connectionHandler;
    }

    public static OpenFileHandler getOpenFileHandler() {
        return openFileHandler;
    }

    public static MouseListener getMouseListener() {
        return mouseListener;
    }

    public static void setCountingTitleForDrawing(int count, int size) {
        Platform.runLater(() -> {
            window.setTitle(WINDOW_TITLE + " (" + count + "/" + size + ")");
            draw.setText("Draw (" + (100 * count / size) + " %)");
        });
    }

    public static void resetCountingTitleForDrawing() {
        Platform.runLater(() -> {
            window.setTitle(WINDOW_TITLE);
            draw.setText("Draw");
        });
    }


    public static void disableAll(boolean disableMenu) {
        toggleUI(true, disableMenu);
    }

    public static void enableAll() {
        toggleUI(false, true);
    }

    private static void toggleUI(boolean option, boolean toggleMenu) {
        isUIDisabled = option;

        draw.setDisable(option);
        penToggle.setDisable(option);
        if(toggleMenu) menuBar.setDisable(option);
        overlay.setCursor(option ? Cursor.DEFAULT : Cursor.HAND);
    }

    public static ImageView getPreview() {
        return preview;
    }
}
