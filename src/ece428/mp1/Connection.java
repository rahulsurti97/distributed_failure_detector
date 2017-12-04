package ece428.mp1;

import java.net.Socket;

public class Connection {
    private String host;
    private Integer port;
    private Socket socket;


    public Connection() {
    }


    public Connection(final String host, final Integer port, final Socket socket) {
        this.host = host;
        this.port = port;
        this.socket = socket;
    }


    /**
     * @return The socket that the connection holds.
     */
    public Socket getSocket() {
        return socket;
    }


    /**
     * Sets the host name.
     *
     * @param socket The socket that you want the connection to hold.
     */
    public void setSocket(final Socket socket) {
        this.socket = socket;
    }


    /**
     * @return The host name that the connection holds.
     */
    public String getHost() {
        return host;
    }


    /**
     * Sets the host name.
     *
     * @param host The host name that you want the connection to hold.
     */
    public void setHost(final String host) {
        this.host = host;
    }


    /**
     * @return The port that the connection holds.
     */
    public Integer getPort() {
        return port;
    }


    /**
     * Sets the port.
     *
     * @param port The port that you want the connection to hold.
     */
    public void setPort(final Integer port) {
        this.port = port;
    }
}
