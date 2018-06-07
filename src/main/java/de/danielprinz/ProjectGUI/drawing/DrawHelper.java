package de.danielprinz.ProjectGUI.drawing;

import de.danielprinz.ProjectGUI.Main;
import de.danielprinz.ProjectGUI.resources.Command;
import de.danielprinz.ProjectGUI.resources.CommandType;
import de.danielprinz.ProjectGUI.resources.SettingsHandler;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Bounds;
import javafx.scene.image.ImageView;

import java.awt.image.BufferedImage;

public class DrawHelper {

    private static CommandType commandType = CommandType.PU;
    private static boolean skipCommand = true;
    private static Command moveTo; // used when pen is up and the user moves to a specific location. this saved a lot of data

    private static boolean threadIsEnabled = false;
    private static int crosshairPositionX;
    private static int crosshairPositionY;

    public static void setCommandType(CommandType ct) {
        commandType = ct;
    }


    public static void reset() {
        crosshairPositionX = 0;
        crosshairPositionY = 0;
    }


    public static void drawLine(int x, int y) {

        Command command = new Command(commandType, x, y);

        if(commandType.equals(CommandType.PU)) {
            moveTo = command;
            return;
        }

        if(moveTo != null) {
            Main.getOpenFileHandler().addCommand(moveTo);
            moveTo = null;
            skipCommand = true;
        }

        if(skipCommand) {
            // removes duplicates
            skipCommand = false;
            return;
        }

        Main.getOpenFileHandler().addCommand(command);
    }

    /**
     * Checks for mouse movements
     * @param node
     */
    public static void runMovementChecker(ImageView node) {

        final Bounds BOUNDS = node.getLayoutBounds();

        threadIsEnabled = true;
        new Thread(() -> {
            while(threadIsEnabled) {

                double dx = node.getLayoutBounds().getMaxX() - BOUNDS.getMaxX();
                double dy = node.getLayoutBounds().getMaxY() - BOUNDS.getMaxY();

                dx *= SettingsHandler.HANDDRAWING_SCALE;  // positive when moving right, negative when moving left
                dy *= -SettingsHandler.HANDDRAWING_SCALE; // positive when moving up, negative when moving down

                if(crosshairPositionX + (int)dx <= 0)
                    crosshairPositionX = 0;
                else if(crosshairPositionX + (int)dx > Main.getOpenFileHandler().getFileHolder().getImageWidth() / Main.getOpenFileHandler().getFileHolder().getScaleX())
                    crosshairPositionX = (int) (Main.getOpenFileHandler().getFileHolder().getImageWidth() / Main.getOpenFileHandler().getFileHolder().getScaleX());
                else
                    crosshairPositionX += (int)dx;


                if(crosshairPositionY + (int)dy <= 0)
                    crosshairPositionY = 0;
                else if(crosshairPositionY + (int)dy > Main.getOpenFileHandler().getFileHolder().getImageHeight() / Main.getOpenFileHandler().getFileHolder().getScaleY())
                    crosshairPositionY = (int) (Main.getOpenFileHandler().getFileHolder().getImageHeight() / Main.getOpenFileHandler().getFileHolder().getScaleY());
                else
                    crosshairPositionY += (int)dy;

                drawLine(crosshairPositionX, crosshairPositionY);

                // regenerate preview image
                BufferedImage bufferedImage = Main.getOpenFileHandler().renderImage(true);
                Platform.runLater(() -> Main.preview.setImage(SwingFXUtils.toFXImage(bufferedImage, null)));


                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    public static void stopMovementChecker() {
        threadIsEnabled = false;
    }


    public static int getCrosshairPositionX() {
        return (int) (crosshairPositionX * Main.getOpenFileHandler().getFileHolder().getScaleX());
    }

    public static int getCrosshairPositionY() {
        return (int) (Main.getOpenFileHandler().getFileHolder().getImageHeight() - crosshairPositionY * Main.getOpenFileHandler().getFileHolder().getScaleY());
    }
}
