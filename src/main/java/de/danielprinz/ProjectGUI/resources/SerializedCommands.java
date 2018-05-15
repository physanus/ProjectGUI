package de.danielprinz.ProjectGUI.resources;

import java.util.ArrayList;

public class SerializedCommands {

    private ArrayList<Command> serializedCommands;

    public SerializedCommands(ArrayList<Command> serializedCommands) {
        this.serializedCommands = serializedCommands;
    }

    public SerializedCommands() {
        this.serializedCommands = new ArrayList<>();
    }


    public ArrayList<Command> getValues() {
        return serializedCommands;
    }
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
     * Scales the points to the specifies width and height. Image ratio will be kept.
     * @param maxWidth The maximum width
     * @param maxHeight The maximum height
     * @return The new image dimensions [x,y]
     */
    public int[] scale(int maxWidth, int maxHeight) {
        int[] dimensions = getBoundingDimensions();
        int dimX = dimensions[0];
        int dimY = dimensions[1];

        double scale = Math.min((double)maxWidth/dimX, (double)maxHeight/dimY);
        int imageWidth = (int) (dimX * scale);
        int imageHeight = (int) (dimY * scale);
        double scaleX = imageWidth / (double) dimX;
        double scaleY = imageHeight / (double) dimY;

        // scale all points
        for(Command command : this.serializedCommands) {
            command.scale(scaleX, scaleY);
        }

        return new int[]{imageWidth, imageHeight};
    }

}
