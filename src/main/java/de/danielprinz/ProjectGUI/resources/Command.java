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


    /**
     * Scales x and y with the specifies value. 1 = no scale, <1 smaller, >1 bigger
     * @param x The scale for the x value
     * @param y The scale for the y values
     */
    public Command scale(double x, double y) {
        this.x *= x;
        this.y *= y;
        return this;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(commandType.toString())
                .append(x)
                .append(",")
                .append(y);

        return sb.toString();
    }


    public Command copy() {
        return new Command(commandType, x, y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Command command = (Command) o;

        if (x != command.x) return false;
        if (y != command.y) return false;
        return commandType == command.commandType;
    }

    @Override
    public int hashCode() {
        int result = commandType != null ? commandType.hashCode() : 0;
        result = 31 * result + x;
        result = 31 * result + y;
        return result;
    }
}
