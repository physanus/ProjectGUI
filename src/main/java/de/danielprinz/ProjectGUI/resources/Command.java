package de.danielprinz.ProjectGUI.resources;

public class Command {

    private CommandType commandType;
    private int x, y;

    public Command(CommandType commandType, int x, int y) {
        this.commandType = commandType;
        this.x = x;
        this.y = y;
    }


    public CommandType getCommandType() {
        return commandType;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

}
