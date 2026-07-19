package eShop.server.net;

import eShop.server.domain.EShop;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Die zentrale Startklasse (Entry Point) des eShop-Servers.
 * Diese Klasse initialisiert den Kern des Systems (die Domänen-Fassade und den Aktualisierungsdienst)
 * und öffnet den Netzwerk-Port, um eingehende Verbindungen von Clients zu akzeptieren.
 */
public final class Main {
    /** Der Standard-Netzwerkport ({@code 6789}), auf dem der Server lauscht, falls kein Port übergeben wird. */
    public static final int DEFAULT_PORT = 6789;

    /**
     * Privater Konstruktor, um eine Instanziierung dieser reinen Utility- und Startklasse
     * von außen zu verhindern.
     */
    private Main() { }

    /**
     * Die Hauptmethode des Servers. Startet den eShop, bindet den ServerSocket an den Zielport
     * und wechselt in die Endlosschleife zur Verbindungsannahme.
     *
     * @param args Optionale Kommandozeilenargumente. Das erste Argument kann als numerischer Port
     *             interpretiert werden, der den {@link #DEFAULT_PORT} überschreibt.
     */
    public static void main(String[] args) {
        int port = args.length == 0 ? DEFAULT_PORT : Integer.parseInt(args[0]);
        try {
            // Zentrale Geschäftslogik-Instanz (Fassade) erzeugen
            EShop shop = new EShop();

            // Geteilten Benachrichtigungsdienst für Echtzeit-Updates initialisieren
            AktualisierungsDienst aktualisierungsDienst = new AktualisierungsDienst();
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                System.out.println("eShop-Server lauscht auf Port " + port);

                // Endlosschleife (Accept-Loop) zur Verbindungsannahme
                while (!serverSocket.isClosed()) {
                    Socket client = serverSocket.accept();

                    // Erstellt und startet einen dedizierten Worker-Thread für den Client
                    new Thread(new ClientRequestProcessor(client, shop, aktualisierungsDienst),
                            "eshop-client-" + client.getRemoteSocketAddress()).start();
                }
            }
        } catch (IOException | RuntimeException e) {
            System.err.println("Server konnte nicht gestartet werden: " + e.getMessage());
        }
    }
}
