package ece428.mp1;

import java.io.IOException;

public class StartServer {
    public static void main(final String[] args) throws InterruptedException, IOException {
        final GrepServer grepServer = new GrepServer(9090);
        grepServer.initialize();
        grepServer.startServer();
        grepServer.closeServer();
    }
}
