package de.danielprinz.ProjectGUI.resources;

import de.danielprinz.ProjectGUI.exceptions.UnsupportedFileTypeException;

import java.util.ArrayList;

public class FileHolder {

    private ArrayList<String> fileContent = new ArrayList<>();
    private SerializedCommands serializedCommands = new SerializedCommands();
    private SerializedCommands serializedCommandsScaled = new SerializedCommands();
    private double scaleX = 1;
    private double scaleY = 1;
    private int imageWidth = 0;
    private int imageHeight = 0;


    public ArrayList<String> getFileContent() {
        return fileContent;
    }

    public SerializedCommands getSerializedCommands() {
        return serializedCommands;
    }

    public SerializedCommands getSerializedCommandsScaled() {
        return serializedCommandsScaled;
    }

    public void updateScale(double scaleX, double scaleY) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;

        // update the scale
        serializedCommandsScaled = new SerializedCommands();
        for(Command command : this.serializedCommands.getValues()) {
            command = command.copy();
            command.scale(scaleX, scaleY);
            this.serializedCommandsScaled.add(command);
        }
    }

    public void addCommand(Command command) {
        if(serializedCommands.getLastValue().getCommandType().equals(command.getCommandType()) && command.getCommandType().equals(CommandType.PD)) {
            // last and current command are both the same, here: PD
            // ==> lets concatenate them

            //this.fileContent.add(command);
            String prevLine = this.fileContent.remove(this.fileContent.size() - 1);
            this.fileContent.add(prevLine + "," + command.getX() + "," + command.getY());

        } else {
            this.fileContent.add(command.toString());
        }

        this.serializedCommands.add(command);
        this.serializedCommandsScaled.add(command.copy().scale(this.scaleX, this.scaleY));
    }

    public void addScaledCommand(Command command) {
        this.serializedCommandsScaled.add(command);
        command = command.copy().scale(1/scaleX, 1/scaleY);
        this.fileContent.add(command.toString());
        this.serializedCommands.add(command);
    }

    /**
     * Adds a raw line/single command to the fileHolder
     * @param line The line. May just be one single command
     */
    public void addRawLine(String line) throws UnsupportedFileTypeException {
        this.fileContent.add(line);

        CommandType commandType;
        if(line.startsWith("PD"))
            commandType = CommandType.PD;
        else if(line.startsWith("PU"))
            commandType = CommandType.PU;
        else if(line.startsWith("SP"))
            // select pen, unused
            return;
        else if(line.startsWith("IN"))
            // initialize, unused
            return;
        else {
            System.err.println("Found unknown command: " + line.substring(0, 2));
            return;
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

            Command command = new Command(commandType, x, y);
            this.serializedCommands.add(command);
        }
    }


    public int getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public double getScaleX() {
        return scaleX;
    }

    public double getScaleY() {
        return scaleY;
    }
}
