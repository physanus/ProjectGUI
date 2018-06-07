package de.danielprinz.ProjectGUI.io;

import de.danielprinz.ProjectGUI.Main;
import de.danielprinz.ProjectGUI.resources.Command;
import de.danielprinz.ProjectGUI.resources.SerializedCommands;
import de.danielprinz.ProjectGUI.resources.SettingsHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class SerialWriter implements Runnable {

    private OutputStream out;
    private int mid = 0;
    private ArrayList<Command> queue;

    private boolean isRunning;

    public SerialWriter(OutputStream out) {
        this.out = out;
    }

     public void run() {
        while(true) {
            if(!(queue == null) && queue.size() > 0) {
                String message = "#" + getMid() + ":" + queue.get(0).toString() + "$";
                try {
                    out.write(message.getBytes());
                    System.out.println("sent: " + message);
                    System.out.println();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                queue.remove(0);
            }

            try {
                Thread.sleep(1000 / SettingsHandler.SERIAL_CONNECTION_COMMANDS_SENT_PER_SECOND);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }



    private int getMid() {
        mid = mid == 100 ? 0 : mid; // range: 0 - 99
        return mid++;
    }




    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void sendUART(SerializedCommands serialized, boolean titleProcess) {
        queue = new ArrayList<>(serialized.getValues());
        if(titleProcess) {
            new Thread(() -> {
                while(queue.size() > 0) {
                    Main.setCountingTitleForDrawing(serialized.getValues().size() - queue.size(), serialized.getValues().size());
                    try {
                        Thread.sleep(1000 / SettingsHandler.SERIAL_CONNECTION_COMMANDS_SENT_PER_SECOND);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                Main.resetCountingTitleForDrawing();
                Main.enableAll();
            }).start();
        }
    }
}