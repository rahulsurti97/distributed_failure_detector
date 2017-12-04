package ece428.mp1;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class GrepClient {
    Connection connection;
    private DataOutputStream dataOutputStream;
    private boolean isAvailable = false;


    public GrepClient() {
    }


    /**
     * @param hostName The hostname that the client will connect to.
     * @param port     The port number that the client will connect to.
     * @throws Exception
     */
    public GrepClient(final String hostName, final Integer port) throws Exception {
        this.connection = new Connection();
        this.connection.setHost(hostName);
        this.connection.setPort(port);
    }

    /**
     * @return If the connection is available or not.
     */
    public boolean isAvailable() {
        return this.isAvailable;
    }

    /**
     * Clears the internal variables and sets them to null.
     */
    private void clearVars() {
        this.connection.setSocket(null);
        this.dataOutputStream = null;
        this.isAvailable = false;
    }


    /**
     * Opens a connection to the hostname and the port.
     * It is up to the programmer to open the connection
     * and close the connection with the methods provided in the class.
     *
     * @return Returns if the connection was successfully opened or not.
     * @throws Exception
     */
    public boolean openConnection() throws Exception {
        try {
            this.connection.setSocket(new Socket(this.connection.getHost(), this.connection.getPort()));
            this.dataOutputStream = new DataOutputStream(this.connection.getSocket().getOutputStream());
        } catch (final UnknownHostException e) {
            clearVars();
            return false;
        } catch (final IOException e) {
            clearVars();
            return false;
        } catch (final Exception e) {
            clearVars();
            return false;
        }
        this.isAvailable = this.connection.getSocket() != null &&
                this.dataOutputStream != null;
        return this.isAvailable;
    }


    /**
     * Closes the connection.
     *
     * @throws Exception if the connection was never opened.
     */
    public void closeConnection() throws Exception {
        try {
            this.connection.getSocket().close();
            this.dataOutputStream.close();
        } catch (final Exception e) {
            System.err.println(e.getLocalizedMessage());
        }
        clearVars();
        this.isAvailable = false;
    }


    /**
     * This method sends data as a string.
     *
     * @throws Exception if the connection was not properly set.
     */
    public void writeData(final String cmd) throws Exception {
        if (!this.isAvailable) {
            throw new SocketException("Opening of the connection was not successful.");
        }
        this.dataOutputStream.writeUTF(cmd);
    }


    /**
     * Reads the data from all the servers and outputs it to the client's screen.
     *
     * @throws IOException
     */
    public int readData() throws IOException {
        final int i = 0;
        String line;
        final DataInputStream dataInputStream = new DataInputStream(this.connection.getSocket().getInputStream());
        try {
            while (!(line = dataInputStream.readUTF()).equals("DONE")) {
                System.out.println(line);
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return i;
    }


    /**
     * Getter for the connection object.
     *
     * @return The connection configuration for the client.
     */
    public Connection getConnection() {
        return this.connection;
    }
}
