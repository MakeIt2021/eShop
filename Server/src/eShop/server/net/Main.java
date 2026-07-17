package eShop.server.net;

import eShop.server.domain.EShop;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public final class Main {
    public static final int DEFAULT_PORT = 6789;

    private Main() { }

    public static void main(String[] args) {
        int port = args.length == 0 ? DEFAULT_PORT : Integer.parseInt(args[0]);
        try {
            EShop shop = new EShop();
            AktualisierungsDienst aktualisierungsDienst = new AktualisierungsDienst();
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                System.out.println("eShop-Server lauscht auf Port " + port);
                while (!serverSocket.isClosed()) {
                    Socket client = serverSocket.accept();
                    new Thread(new ClientRequestProcessor(client, shop, aktualisierungsDienst),
                            "eshop-client-" + client.getRemoteSocketAddress()).start();
                }
            }
        } catch (IOException | RuntimeException e) {
            System.err.println("Server konnte nicht gestartet werden: " + e.getMessage());
        }
    }
}
