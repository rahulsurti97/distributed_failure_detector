package ece428.mp1;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;

public class GrepServer {
    private Connection connection;
    private ServerSocket serverSocket;

    public GrepServer() {
    }


    public GrepServer(final Integer port) {
        this.connection = new Connection();
        this.connection.setPort(port);
    }


    /**
     * Initializes the server and binds it to the port.
     *
     * @throws IOException
     */
    public void initialize() throws IOException {
        try {
            this.serverSocket = new ServerSocket(this.connection.getPort());
        } catch (final IOException e) {
            System.out.println(e.getLocalizedMessage());
        }
    }


    /**
     * @throws IOException
     */
    public void startServer() throws InterruptedException, IOException {
        String line;
        final TerminalParser terminalParser = new TerminalParser();
        try {
            this.connection.setSocket(this.serverSocket.accept());
            final DataInputStream dataInputStream = new DataInputStream(this.connection.getSocket().getInputStream());
            while (true) {
                line = dataInputStream.readUTF();
                terminalParser.setCommand(line);
                terminalParser.runCommand(this.connection);
            }
        } catch (final IOException e) {
            System.out.println(e.getLocalizedMessage());
            closeServer();
        }
    }


    /**
     * Closes the server and assigns variables to null for object de-referencing.
     *
     * @throws IOException
     */
    public void closeServer() throws IOException {
        if (this.connection != null && this.connection.getSocket() != null) {
            this.connection.getSocket().close();
        }
        if (this.serverSocket != null) {
            this.serverSocket.close();
        }
        this.connection = null;
        this.serverSocket = null;
    }


    /**
     * @return The connection configuration for the client.
     */
    public Connection getConnection() {
        return this.connection;
    }
}
