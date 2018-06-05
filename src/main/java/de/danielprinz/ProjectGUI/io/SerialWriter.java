package de.danielprinz.ProjectGUI.io;

import de.danielprinz.ProjectGUI.Main;
import de.danielprinz.ProjectGUI.resources.Command;
import de.danielprinz.ProjectGUI.resources.SerializedCommands;

import java.io.IOException;
import java.io.OutputStream;

public class SerialWriter implements Runnable {

    private OutputStream out;
    private int mid = 0;
    private String message;

    private boolean isRunning;

    public SerialWriter(OutputStream out) {
        this.out = out;
    }

    public void run() {
        if(!isRunning) return;
        if(message == null) return;
        try {
            out.write(message.getBytes());
            Main.addToCmdWindow("sent: " + message);
        } catch (IOException e) {
            Main.getConnectionHandler().setDisconnected(Main.COM_PORT, true); // TODO portName to settings
        }
    }

    /**
     * Forces the execution of this particular message
     * @param cmd
     */
    public void sendUART(String cmd) {
        String message = "#" + getMid() + ":" + cmd + "$";

        this.message = message;
        run();
    }

    private int getMid() {
        mid = mid == 100 ? 0 : mid; // range: 0 - 99
        return mid++;
    }




    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    public void sendUART(SerializedCommands serialized, boolean titleProcess) {
        int count = 1;
        for(Command command : serialized.getValues()) {
            sendUART(command.toString());
            Main.setCountingTitleForDrawing(count, serialized.getValues().size());
            count++;
        }
        Main.resetCountingTitleForDrawing();
    }
}
