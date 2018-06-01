package de.danielprinz.ProjectGUI.io;

import de.danielprinz.ProjectGUI.Main;
import de.danielprinz.ProjectGUI.exceptions.SerialConectionException;
import purejavacomm.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ConnectionHandler {

    private SerialReader serialReader;
    private SerialWriter serialWriter;
    private Thread serialReaderThread;
    private Thread serialWriterThread;

    private Thread disconnectedThread;


    public void connectIfNotConnected(String portName) throws SerialConectionException {
        if(serialReader == null || serialWriter == null)
            connect(portName);
    }

    public void connectAsync(String portName) {
        new Thread(() -> {
            try {
                connect(portName);
            } catch (SerialConectionException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void connect(String portName) throws SerialConectionException {
        // http://rxtx.qbang.org/wiki/index.php/Two_way_communcation_with_the_serial_port

        try {
            CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
            if(portIdentifier.isCurrentlyOwned()) {
                System.out.println("Error: Port is currently in use");
                return;
            } else {
                CommPort commPort = portIdentifier.open(Main.WINDOW_TITLE,2000); // TODO timeout to settings

                if(commPort instanceof SerialPort) {
                    SerialPort serialPort = (SerialPort) commPort;
                    serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

                    InputStream in = serialPort.getInputStream();
                    OutputStream out = serialPort.getOutputStream();

                    serialReader = new SerialReader(in);
                    serialReaderThread = new Thread(serialReader);
                    serialReader.setRunning(true);
                    serialReaderThread.start();

                    serialWriter = new SerialWriter(out);
                    serialWriterThread = new Thread(serialWriter);
                    serialWriter.setRunning(true);
                    serialWriterThread.start();
                } else {
                    System.err.println("Error: Only serial ports are handled.");
                    throw new SerialConectionException();
                }
            }
            System.out.println("Established serial connection to " + portName);
        } catch (UnsupportedCommOperationException | IOException | NoSuchPortException | PortInUseException e) {
            //System.err.println("Failed connection to " + portName);
            setDisconnected(false);
            throw new SerialConectionException();
        }

    }



    public void setDisconnected(boolean startLoop) {
        if(disconnectedThread != null) return;

        // TODO disable buttons
        if(serialReader != null) serialReader.setRunning(false);
        if(serialWriter != null) serialWriter.setRunning(false);
        //Main.addToCmdWindow("Serial device is not connected to COM5");

        disconnectedThread = new Thread(() -> {
            while(true) {
                try {
                    Thread.sleep(2000);
                    connect("COM5");
                    // set connected
                    // TODO enable buttons
                    Main.clearCmdWindow();
                    disconnectedThread = null;
                    break;
                } catch (Exception e1) {
                }
            }
        });
        if(startLoop) disconnectedThread.start();
    }


    public SerialReader getSerialReader() {
        return serialReader;
    }

    public SerialWriter getSerialWriter() {
        return serialWriter;
    }

}
