package de.danielprinz.ProjectGUI.files;

import de.danielprinz.ProjectGUI.Main;
import de.danielprinz.ProjectGUI.exceptions.UnsupportedFileTypeException;
import de.danielprinz.ProjectGUI.popupHandler.CloseSaveBox;
import de.danielprinz.ProjectGUI.popupHandler.CloseSaveBoxResult;
import de.danielprinz.ProjectGUI.popupHandler.FileErrorBox;
import de.danielprinz.ProjectGUI.popupHandler.FileErrorType;
import de.danielprinz.ProjectGUI.resources.Command;
import de.danielprinz.ProjectGUI.resources.CommandType;
import de.danielprinz.ProjectGUI.resources.SerializedCommands;
import de.danielprinz.ProjectGUI.resources.Strings;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

public class OpenFileHandler {

    private File openFile;
    private ArrayList<String> fileContent = new ArrayList<>();
    private boolean isSaved = true;

    public OpenFileHandler(File openFile) {
        this.openFile = openFile;
    }

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
        }
    }

    public void read(File file) {
        this.openFile = file;
        this.fileContent = new ArrayList<>();

        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(openFile))) {
            // lets try to read the file.
            String line;
            while((line = bufferedReader.readLine()) != null) {
                if(line.equals("")) continue;
                for(String l : line.split(";")) {
                    if(l.equalsIgnoreCase("") || l.equalsIgnoreCase(" ")) continue;
                    this.fileContent.add(l);
                }
            }
        } catch (IOException e1) {
            e1.printStackTrace();
            FileErrorBox.display(FileErrorType.NO_SUCH_FILE, Main.WINDOW_TITLE, Strings.FILE_ERROR_NO_SUCH_FILE.format());
        }

    }



    /**
     * Shows the dialog box if the file has not been saved
     * @return SAVE/NOSAVE/CANCEL
     */
    public CloseSaveBoxResult showDialogBox() {
        if(this.isSaved) return CloseSaveBoxResult.NOSAVE; // TODO implement isSaved variable
        CloseSaveBoxResult result = CloseSaveBox.display(Main.WINDOW_TITLE, Strings.SAVE_CHANGES_QUESTION.format(this.openFile.getName())); // TODO show the current filename
        if(result == null) result.equals(CloseSaveBoxResult.CANCEL);
        return result;
    }

    /**
     * Generates a bufferedImage from the file input
     * @return The buffered image
     * @throws UnsupportedFileTypeException Thrown in case the file contains statements the parser cannot interpret
     * @param maxWidth Specifies the maximum width of th resulting buffered image
     * @param maxHeight Specifies the maximum height of th resulting buffered image
     */
    public BufferedImage renderImage(int maxWidth, int maxHeight) throws UnsupportedFileTypeException {

        SerializedCommands serialized = new SerializedCommands(this.fileContent);
        int[] dimensions = serialized.scale(maxWidth, maxHeight);
        int imageWidth = dimensions[0];
        int imageHeight = dimensions[1];

        BufferedImage bufferedImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = (Graphics2D) bufferedImage.getGraphics();
        graphics.setColor(Color.BLACK);

        Command oldCommand = null;
        for(Command command : serialized.getValues()) {
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
                int yOldTL = imageHeight - oldCommand.getY();
                int xTL = command.getX();
                int yTL = imageHeight - command.getY();
                graphics.drawLine(xOldTL, yOldTL, xTL, yTL);
            }

            oldCommand = command;
        }

        return bufferedImage;
    }


}
