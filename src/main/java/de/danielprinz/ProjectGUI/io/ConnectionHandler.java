package de.danielprinz.ProjectGUI.io;

import de.danielprinz.ProjectGUI.Main;
import de.danielprinz.ProjectGUI.exceptions.SerialConnectionException;
import de.danielprinz.ProjectGUI.resources.SettingsHandler;
import purejavacomm.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;

public class ConnectionHandler {

    private SerialReader serialReader;
    private SerialWriter serialWriter;
    private Thread serialReaderThread;
    private Thread serialWriterThread;

    private Thread disconnectedThread;


    public void connectIfNotConnected() throws SerialConnectionException {
        System.out.println("connectIfNotConnected(String portName)");
        if(serialReader == null || serialWriter == null || !serialReader.isRunning() || !serialWriter.isRunning())
            connect();
    }

    public void connectAsync(String portName) {
        new Thread(() -> {
            try {
                connect();
            } catch (SerialConnectionException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void connect() throws SerialConnectionException {
        System.out.println("connect()");
        // http://rxtx.qbang.org/wiki/index.php/Two_way_communcation_with_the_serial_port

        if(SettingsHandler.PORT.equals(null)) throw new SerialConnectionException("Connection is null");

        try {

            if(SettingsHandler.PORT.isCurrentlyOwned()) {
                // we already connected somewhen before

                return;
            } else {
                CommPort commPort = SettingsHandler.PORT.open(Main.WINDOW_TITLE,SettingsHandler.SERIAL_CONNECTION_TIMEOUT);

                if(commPort instanceof SerialPort) {
                    SerialPort serialPort = (SerialPort) commPort;
                    serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

                    InputStream in = serialPort.getInputStream();
                    OutputStream out = serialPort.getOutputStream();

                    /*if(serialReader == null) serialReader = new SerialReader(in);
                    serialReaderThread = new Thread(serialReader);
                    serialReader.setRunning(true);
                    serialReaderThread.start();*/

                    if(serialWriter == null) serialWriter = new SerialWriter(out);
                    serialWriterThread = new Thread(serialWriter);
                    serialWriter.setRunning(true);
                    serialWriterThread.start();
                } else {
                    System.err.println("Error: Only serial ports are handled.");
                    throw new SerialConnectionException();
                }
            }
            System.out.println("Established serial connection to " + SettingsHandler.PORT.getName());
        } catch (UnsupportedCommOperationException | IOException | PortInUseException e) {
            //System.err.println("Failed connection to " + portName);
            setDisconnected(false);
            throw new SerialConnectionException();
        }

    }



    public void setDisconnected(boolean reconnect) {
        System.out.println("setDisconnected()");
        if(disconnectedThread != null) return;

        if(reconnect) Main.disableAll(false); // will be enabled again in the Runnable
        if(serialReader != null) serialReader.setRunning(false);
        if(serialWriter != null) serialWriter.setRunning(false);
        //Main.addToCmdWindow("Serial device is not connected to " + Main.COM_PORT);

        disconnectedThread = new Thread(() -> {
            while(true) {
                try {
                    Thread.sleep(2000);
                    connect();
                    Main.enableAll();
                    disconnectedThread = null;
                    break;
                } catch (Exception e1) { }
            }
        });
        if(reconnect) disconnectedThread.start();
    }


    public SerialReader getSerialReader() {
        return serialReader;
    }

    public SerialWriter getSerialWriter() {
        return serialWriter;
    }


    public CommPortIdentifier autoChooseSerialPort() {
        List<CommPortIdentifier> ports = getAllPorts();
        if(ports.isEmpty()) return null;
        return ports.get((new Random()).nextInt(ports.size()));
    }

    public List<CommPortIdentifier> getAllPorts() {
        ArrayList<CommPortIdentifier> result = new ArrayList<>();
        Enumeration<CommPortIdentifier> portIdentifiers = CommPortIdentifier.getPortIdentifiers();

        while(portIdentifiers.hasMoreElements()) {
            CommPortIdentifier portid = portIdentifiers.nextElement();
            result.add(portid);
        }

        return result;
    }

}
