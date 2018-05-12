package de.danielprinz.ProjectGUI.io;

import de.danielprinz.ProjectGUI.Main;

import java.io.IOException;
import java.io.OutputStream;

public class SerialWriter implements Runnable {

    private OutputStream out;
    private int mid = 0;
    private String message;
    private String cmd = "";
    private String lastcmd = "";
    private int data = -1;
    private int lastdata = -1;

    private boolean isRunning;

    public SerialWriter(OutputStream out) {
        this.out = out;
    }

    public void run() {
        if(!isRunning) return;
        try {
            if(cmd.equals(lastcmd) && data == lastdata) return;
            out.write(message.getBytes());
            Main.addToCmdWindow("sent: " + message);

            lastcmd = cmd;
            lastdata = data;
            message = "";

        } catch (IOException e) {
            Main.getConnectionHandler().setDisconnected();
        }
    }


    /**
     * Sends a message to the connected device
     * @param cmd The cmd (String)
     * @param data The data (int)
     */
    public void sendUART(String cmd, int data) {
        sendUART(cmd, data, false);
    }

    /**
     * Forces the execution of this particular message
     * @param cmd
     * @param data
     * @param force
     */
    public void sendUART(String cmd, int data, boolean force) {
        if(cmd.equals(lastcmd) && data == lastdata && !force) return;

        String message = "#" + getMid() + ":" + cmd + ":" + data + "$";

        this.message = message;
        this.cmd = cmd;
        this.data = data;
        if(force) this.lastcmd = cmd + "h";
        if(force) this.lastdata = -1;
        run();
    }

    private int getMid() {
        mid = mid == 100 ? 0 : mid; // range: 0 - 99
        return mid++;
    }




    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

}
