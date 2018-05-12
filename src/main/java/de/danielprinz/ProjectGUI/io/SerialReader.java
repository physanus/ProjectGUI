package de.danielprinz.ProjectGUI.io;

import de.danielprinz.ProjectGUI.Main;

import java.io.IOException;
import java.io.InputStream;

public class SerialReader implements Runnable{

    private InputStream in;
    private boolean isRunning = true;

    public SerialReader(InputStream in) {
        this.in = in;
    }

    public void run() {
        byte[] buffer = new byte[1024];
        try {
            StringBuilder sb = new StringBuilder();
            int len;
            while(isRunning && (len = this.in.read(buffer)) > -1) {
                String received = new String(buffer, 0, len);
                sb.append(received);
                if(received.endsWith("$")) {
                    String out = sb.toString();

                    // strips off all non-ASCII characters
                    out = out.replaceAll("[^\\x00-\\x7F]", "");
                    // erases all the ASCII control characters
                    out = out.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "");
                    // removes non-printable characters from Unicode
                    out = out.replaceAll("\\p{C}", "");

                    Main.addToCmdWindow(out);
                    sb = new StringBuilder();
                }
            }
        } catch (IOException e) {
            Main.getConnectionHandler().setDisconnected();
        }

    }


    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

}
