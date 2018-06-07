package de.danielprinz.ProjectGUI.files;

import de.danielprinz.ProjectGUI.Main;
import de.danielprinz.ProjectGUI.drawing.DrawHelper;
import de.danielprinz.ProjectGUI.exceptions.NoSuchFileException;
import de.danielprinz.ProjectGUI.exceptions.UnsupportedFileTypeException;
import de.danielprinz.ProjectGUI.popupHandler.CloseSaveBox;
import de.danielprinz.ProjectGUI.popupHandler.CloseSaveBoxResult;
import de.danielprinz.ProjectGUI.popupHandler.FileErrorBox;
import de.danielprinz.ProjectGUI.popupHandler.FileErrorType;
import de.danielprinz.ProjectGUI.resources.*;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class OpenFileHandler {

    private File openFile;
    private boolean fileChanged = false;

    private FileHolder fileHolder;


    public void save(CloseSaveBoxResult result) {
        if(!result.equals(CloseSaveBoxResult.SAVE)) return;
        save();
    }
    public void save() {
        System.out.println("saving...");

        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(openFile))) {
            // TODO save current document
            /*for(;;) {

            }*/
        } catch (IOException e) {
            e.printStackTrace();
            FileErrorBox.display(FileErrorType.WRITE_ERROR, Main.WINDOW_TITLE, Strings.FILE_WRITE_ERROR.format());
            return;
        }

        this.fileChanged = false;
    }

    public void read(File file) throws UnsupportedFileTypeException, NoSuchFileException {
        this.openFile = file;
        this.fileHolder = new FileHolder();

        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(openFile))) {
            // lets try to read the file.
            String line;
            while((line = bufferedReader.readLine()) != null) {
                if(line.equals("")) continue;
                for(String l : line.split(";")) {
                    if(l.equalsIgnoreCase("") || l.equalsIgnoreCase(" ")) continue;
                    this.fileHolder.addRawLine(l);
                }
            }
        } catch (IOException e) {
            throw new NoSuchFileException();
        }

        // get dimenstions and apply scaler for preview
        double[] dimensions = this.fileHolder.getSerializedCommands().getScale(SettingsHandler.MAX_WIDTH_IMAGE, SettingsHandler.MAX_HEIGHT_IMAGE);
        int imageWidth = (int) dimensions[0];
        int imageHeight = (int) dimensions[1];
        double scaleX = dimensions[2];
        double scaleY = dimensions[3];

        this.fileHolder.setImageWidth(imageWidth);
        this.fileHolder.setImageHeight(imageHeight);
        this.fileHolder.updateScale(scaleX, scaleY);


        Main.getMouseListener().reset();
        DrawHelper.reset();
    }



    /**
     * Shows the dialog box if the file has not been saved
     * @return SAVE/NOSAVE/CANCEL
     */
    public CloseSaveBoxResult showDialogBox() {
        if(!this.fileChanged) return CloseSaveBoxResult.NOSAVE; // TODO implement fileChanged variable
        CloseSaveBoxResult result = CloseSaveBox.display(Main.WINDOW_TITLE, Strings.SAVE_CHANGES_QUESTION.format(this.openFile.getName())); // TODO show the current filename
        if(result == null) result.equals(CloseSaveBoxResult.CANCEL);
        return result;
    }



    /**
     * Generates a bufferedImage from the file input
     * @return The buffered image
     * @throws UnsupportedFileTypeException Thrown in case the file contains statements the parser cannot interpret
     */
    public BufferedImage renderImage(boolean background) throws UnsupportedFileTypeException {

        // TODO exception when handling an empty file
        BufferedImage bufferedImage = new BufferedImage(this.fileHolder.getImageWidth(), this.fileHolder.getImageHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = (Graphics2D) bufferedImage.getGraphics();
        if(background) {
            graphics.setColor(new Color(244, 244, 244));
            graphics.fillRect(0, 0, this.fileHolder.getImageWidth(), this.fileHolder.getImageHeight());
        }
        graphics.setColor(Color.BLACK);

        Command oldCommand = null;
        for(Command command : this.fileHolder.getSerializedCommandsScaled().getValues()) {
            // bufferedImage: (0, 0) is at the top left
            // command:       (0, 0) is at the bottom left

            if(oldCommand == null) {
                oldCommand = command;
                continue;
            }

            if(command.getCommandType().equals(CommandType.PD)) {
                // TL = top left oriented dimensions
                // we need to convert them from BL to TL
                int xOldTL = oldCommand.getX();
                int yOldTL = this.fileHolder.getImageHeight() - oldCommand.getY();
                int xTL = command.getX();
                int yTL = this.fileHolder.getImageHeight() - command.getY();
                graphics.drawLine(xOldTL, yOldTL, xTL, yTL);
            }

            oldCommand = command;
        }

        return bufferedImage;
    }



    public void addCommand(Command command) {
        this.fileHolder.addCommand(command);
        this.fileChanged = true;
        // regenerate preview image
        try {
            BufferedImage bufferedImage = renderImage(true);
            Platform.runLater(() -> Main.preview.setImage(SwingFXUtils.toFXImage(bufferedImage, null)));
        } catch (UnsupportedFileTypeException e) {
            // TODO handle
            e.printStackTrace();
        }
    }


    public FileHolder getFileHolder() {
        return fileHolder;
    }
}
