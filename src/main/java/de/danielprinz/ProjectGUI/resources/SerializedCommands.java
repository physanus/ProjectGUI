package de.danielprinz.ProjectGUI.resources;

import de.danielprinz.ProjectGUI.exceptions.UnsupportedFileTypeException;

import java.util.ArrayList;

public class SerializedCommands {

    private ArrayList<Command> serializedCommands;

    public SerializedCommands() {
        this.serializedCommands = new ArrayList<>();
    }

    public SerializedCommands(ArrayList<String> fileContent) throws UnsupportedFileTypeException {

        this.serializedCommands = new ArrayList<>();
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

                this.serializedCommands.add(new Command(commandType, x, y));
            }
        }

        if(error)
            throw new UnsupportedFileTypeException();
    }




    public ArrayList<Command> getValues() {
        return serializedCommands;
    }
    public Command getLastValue() { return serializedCommands.get(serializedCommands.size() - 1); }
    public boolean add(Command command) {
        return this.serializedCommands.add(command);
    }


    /**
     * Retrieves the bounding dimensions of the serialized content. (0, 0) is in the bottom left corner
     * @return [x, y]
     */
    public int[] getBoundingDimensions() {
        int[] result = {0, 0};
        Command oldCommand = null; // we currently are at this position
        for(Command command : this.serializedCommands) { // we want to move to this position

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

    /**
     * Retrieves the scale. Image ratio will be kept.
     * @param maxWidth The maximum width
     * @param maxHeight The maximum height
     * @return The new image dimensions [x, y, scaleX, scaleY]
     */
    public double[] getScale(int maxWidth, int maxHeight) {
        int[] dimensions = getBoundingDimensions();
        int dimX = dimensions[0];
        int dimY = dimensions[1];

        double scale = Math.min((double)maxWidth/dimX, (double)maxHeight/dimY);
        int imageWidth = (int) (dimX * scale);
        int imageHeight = (int) (dimY * scale);
        double scaleXPreview = imageWidth / (double) dimX;
        double scaleYPreview = imageHeight / (double) dimY;

        return new double[]{imageWidth, imageHeight, scaleXPreview, scaleYPreview};
    }

}
