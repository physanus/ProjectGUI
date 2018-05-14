package de.danielprinz.ProjectGUI.files;

import de.danielprinz.ProjectGUI.Main;
import de.danielprinz.ProjectGUI.exceptions.UnsupportedFileTypeException;
import de.danielprinz.ProjectGUI.popupHandler.CloseSaveBox;
import de.danielprinz.ProjectGUI.popupHandler.CloseSaveBoxResult;
import de.danielprinz.ProjectGUI.popupHandler.FileErrorBox;
import de.danielprinz.ProjectGUI.popupHandler.FileErrorType;
import de.danielprinz.ProjectGUI.resources.Command;
import de.danielprinz.ProjectGUI.resources.CommandType;
import de.danielprinz.ProjectGUI.resources.Strings;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

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
     */
    public BufferedImage renderImage() throws UnsupportedFileTypeException {

        ArrayList<Command> serialized = serialize();
        int[] dimensions = getBoundingDimensions(serialized);
        int maxX = dimensions[0];
        int maxY = dimensions[1];
        System.out.println(maxX);
        System.out.println(maxY);

        BufferedImage bufferedImage = new BufferedImage(maxX, maxY, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = (Graphics2D) bufferedImage.getGraphics();
        graphics.setColor(Color.BLACK);

        Command oldCommand = null;
        for(Command command : serialized) {
            // bufferedImage: (0, 0) is at the top left
            // command:       (0, 0) is at the bottom left

            if(oldCommand == null) {
                oldCommand = command;
                continue;
            }

            if(command.getCommandType().equals(CommandType.PD)) {
                // TL = top left oriented dimensions
                int xOldTL = oldCommand.getX();
                int yOldTL = maxY - oldCommand.getY();
                int xTL = command.getX();
                int yTL = maxY - command.getY();
                graphics.drawLine(xOldTL, yOldTL, xTL, yTL);
            }

            oldCommand = command;
        }

        return bufferedImage;
    }


    /**
     * Serializes the fileContent
     * @return The serialized content
     * @throws UnsupportedFileTypeException Thrown in case the file contains statements the parser cannot interpret
     */
    private ArrayList<Command> serialize() throws UnsupportedFileTypeException {

        ArrayList<Command> result = new ArrayList<>();
        boolean error = false;

        for(String line : fileContent) {
            CommandType commandType;
            if(line.startsWith("PD"))
                commandType = CommandType.PD;
            else if(line.startsWith("PU"))
                commandType = CommandType.PU;
            else if(line.startsWith("SP"))
                // select pen, unused
                continue;
            else if(line.startsWith("IN"))
                // initialize, unused
                continue;
            else {
                error = true;
                System.err.println("Found unknown command: " + line.substring(0, 2));
                continue;
            }

            line = line.substring(2, line.length()); // remove command and semicolon

            String[] split = line.split(",");
            int x, y;
            for(int i= 0; i < split.length; ) {
                try {
                    x = Integer.parseInt(split[i++]);
                    y = Integer.parseInt(split[i++]);
                } catch (NumberFormatException e) {
                    throw new UnsupportedFileTypeException();
                }

                result.add(new Command(commandType, x, y));
            }
        }

        if(error)
            throw new UnsupportedFileTypeException();

        return result;
    }


    /**
     * Retrieves the bounding dimensions of the serialized content. (0, 0) is in the bottom left corner
     * @param serialized The serialized fileContent
     * @return [x, y]
     */
    private int[] getBoundingDimensions(ArrayList<Command> serialized) {
        int[] result = {0, 0};
        Command oldCommand = null; // we currently are at this position
        for(Command command : serialized) { // we want to move to this position

            if(oldCommand == null) {
                oldCommand = command;
                continue;
            }

            if(command.getCommandType().equals(CommandType.PD) || oldCommand.getCommandType().equals(CommandType.PD)) {
                // top = x
                if(oldCommand.getX() > result[0])
                    result[0] = oldCommand.getX();
                // right = y
                if(oldCommand.getY() > result[1])
                    result[1] = oldCommand.getY();
            }

            oldCommand = command;
        }

        return result;
    }

}
