package de.danielprinz.ProjectGUI.drawing;

import de.danielprinz.ProjectGUI.Main;
import de.danielprinz.ProjectGUI.resources.Command;
import de.danielprinz.ProjectGUI.resources.CommandType;
import javafx.geometry.Bounds;
import javafx.scene.image.ImageView;

public class DrawHelper {

    private static CommandType commandType = CommandType.PU;
    private static Command moveTo; // used when pen is up and the user moves to a specific location. this saved a lot of data

    private static boolean threadIsEnabled = false;
    private static int crosshairPositionX;
    private static int crosshairPositionY;

    public static void setCommandType(CommandType ct) {
        commandType = ct;
    }

    public static CommandType getCommandType() {
        return commandType;
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
        }

        Main.getOpenFileHandler().addCommand(command);
    }

    /**
     * Checks for mouse movements
     * @param node
     */
    public static void runMovementChecker(ImageView node) {

        final Bounds BOUNDS = node.getLayoutBounds();
        final int SCALE = 2; // TODO to settings

        threadIsEnabled = true;
        new Thread(() -> {
            while(threadIsEnabled) {

                double dx = node.getLayoutBounds().getMaxX() - BOUNDS.getMaxX();
                double dy = node.getLayoutBounds().getMaxY() - BOUNDS.getMaxY();

                dx *= SCALE;  // positive when moving right, negative when moving left
                dy *= -SCALE; // positive when moving up, negative when moving down

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

}
