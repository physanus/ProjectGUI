package de.danielprinz.ProjectGUI.resources;

import de.danielprinz.ProjectGUI.exceptions.UnsupportedFileTypeException;

import java.util.ArrayList;

public class FileHolder {

    private ArrayList<String> fileContent = new ArrayList<>();
    private SerializedCommands serializedCommands = new SerializedCommands();
    private SerializedCommands serializedCommandsScaledPreview = new SerializedCommands();
    private SerializedCommands serializedCommandsScaledPrint = new SerializedCommands();
    private double scaleXPreview = 1;
    private double scaleYPreview = 1;
    private int imageWidth = 0;
    private int imageHeight = 0;


    public ArrayList<String> getFileContent() {
        return fileContent;
    }

    public SerializedCommands getSerializedCommands() {
        return serializedCommands;
    }

    public SerializedCommands getSerializedCommandsScaledPreview() {
        return serializedCommandsScaledPreview;
    }

    public SerializedCommands getSerializedCommandsScaledPrint() {
        updateScalePrint();
        return serializedCommandsScaledPrint;
    }

    public void updateScalePreview(double scaleX, double scaleY) {
        this.scaleXPreview = scaleX;
        this.scaleYPreview = scaleY;

        // update the scale
        serializedCommandsScaledPreview = new SerializedCommands();
        for(Command command : this.serializedCommands.getValues()) {
            command = command.copy();
            command.scale(scaleX, scaleY);
            this.serializedCommandsScaledPreview.add(command);
        }
    }

    public void updateScalePrint() {

        // get dimensions and apply scaler for preview
        double[] dimensions = getSerializedCommands().getScale(SettingsHandler.PRINT_IMAGE_MAX_WIDTH, SettingsHandler.PRINT_IMAGE_MAX_HEIGHT);
        int imageWidth = (int) dimensions[0];
        int imageHeight = (int) dimensions[1];
        double scaleXPrint = dimensions[2];
        double scaleYPrint = dimensions[3];

        serializedCommandsScaledPrint = new SerializedCommands();
        Command previousCommand = null;
        for(Command command : this.serializedCommands.getValues()) {
            command = command.copy();
            if(scaleXPrint < 1 || scaleYPrint < 1) {
                // update the scale
                command.scale(scaleXPrint, scaleYPrint);
            }

            // scales for the hardware since x is twice as small
            command.scale(1, 0.5);

            // TODO remove
            command.scale(10, 10);

            System.out.println(command);
            System.out.println(previousCommand == null ? "" : command.copy().subtract(previousCommand));
            System.out.println();

            if(previousCommand == null)
                this.serializedCommandsScaledPrint.add(command);
            else if(!previousCommand.equals(command))
                this.serializedCommandsScaledPrint.add(command.copy().subtract(previousCommand));

            previousCommand = command;
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
        this.serializedCommandsScaledPreview.add(command.copy().scale(this.scaleXPreview, this.scaleYPreview));
        this.serializedCommandsScaledPrint.add(command.copy().scale(SettingsHandler.PRINT_IMAGE_MAX_WIDTH, SettingsHandler.PRINT_IMAGE_MAX_HEIGHT));
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

    public double getScaleXPreview() {
        return scaleXPreview;
    }

    public double getScaleYPreview() {
        return scaleYPreview;
    }
}
