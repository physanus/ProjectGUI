package de.danielprinz.ProjectGUI.files;

import de.danielprinz.ProjectGUI.Main;
import de.danielprinz.ProjectGUI.drawing.DrawHelper;
import de.danielprinz.ProjectGUI.exceptions.UnsupportedFileTypeException;
import de.danielprinz.ProjectGUI.popupHandler.CloseSaveBox;
import de.danielprinz.ProjectGUI.popupHandler.CloseSaveBoxResult;
import de.danielprinz.ProjectGUI.popupHandler.FileErrorBox;
import de.danielprinz.ProjectGUI.popupHandler.FileErrorType;
import de.danielprinz.ProjectGUI.resources.Command;
import de.danielprinz.ProjectGUI.resources.CommandType;
import de.danielprinz.ProjectGUI.resources.SerializedCommands;
import de.danielprinz.ProjectGUI.resources.Strings;
import javafx.embed.swing.SwingFXUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

public class OpenFileHandler {

    private File openFile;
    private ArrayList<String> fileContent = new ArrayList<>();

    private SerializedCommands serialized;
    private boolean fileChanged = false;

    public OpenFileHandler(File openFile) {
        this.openFile = openFile;
    }

    public OpenFileHandler() {

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
     * @param maxWidth Specifies the maximum width of th resulting buffered image
     * @param maxHeight Specifies the maximum height of th resulting buffered image
     */
    public BufferedImage renderImage(int maxWidth, int maxHeight, boolean background) throws UnsupportedFileTypeException {

        this.serialize();
        int[] dimensions = this.serialized.scale(maxWidth, maxHeight);
        int imageWidth = dimensions[0];
        int imageHeight = dimensions[1];

        BufferedImage bufferedImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = (Graphics2D) bufferedImage.getGraphics();
        if(background) {
            graphics.setColor(new Color(244, 244, 244));
            graphics.fillRect(0, 0, imageWidth, imageHeight);
        }
        graphics.setColor(Color.BLACK);

        Command oldCommand = null;
        for(Command command : this.serialized.getValues()) {
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


    /**
     * Serializes the fileContent
     * @return The serialized content
     * @throws UnsupportedFileTypeException Thrown in case the file contains statements the parser cannot interpret
     */
    private void serialize() throws UnsupportedFileTypeException {

        this.serialized = new SerializedCommands();
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

                this.serialized.add(new Command(commandType, x, y));
            }
        }

        if(error)
            throw new UnsupportedFileTypeException();

    }


    public void addCommand(Command command) {
        this.fileContent.add("\n" + command.toString());
        this.serialized.add(command);
        this.fileChanged = true;
        // regenerate preview image
        try {
            BufferedImage bufferedImage = renderImage(Main.MAX_WIDTH_IMAGE, Main.MAX_HEIGHT_IMAGE, true);
            Main.preview.setImage(SwingFXUtils.toFXImage(bufferedImage, null));
        } catch (UnsupportedFileTypeException e) {
            // TODO handle
            e.printStackTrace();
        }
    }


    public SerializedCommands getSerialized() {
        return serialized;
    }
}
