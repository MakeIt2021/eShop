package eShop.client.net;

import eShop.common.entities.Artikel;
import eShop.common.entities.Benutzer;
import eShop.common.entities.Ereignis;
import eShop.common.exceptions.*;
import eShop.common.interfaces.EShopInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.Socket;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Repräsentiert die clientseitige Netzwerkschnittstelle zum eShop-Server.
 * Diese Klasse kapselt die gesamte TCP-Socket-Kommunikation und implementiert das
 * {@link EShopInterface}, sodass die Benutzeroberfläche transparent auf Kerndaten
 * wie Artikel, Warenkörbe und Benutzer zugreifen kann.
 */
public class EShopFassade implements EShopInterface {
    /** Der primäre Socket für die synchrone Kommunikation mit dem Server. */
    private Socket socket = null;

    /** Der Eingabestrom des primären Sockets zum Lesen der Serverantworten. */
    private BufferedReader sin;

    /** Der Ausgabestrom des primären Sockets zum Senden von Befehlen an den Server. */
    private PrintStream sout;

    /** Der dedizierte Socket für den asynchronen Empfang von Echtzeit-Aktualisierungen. */
    private Socket aktualisierungsSocket;

    /** Der Ausgabestrom für den Aktualisierungs-Kanal (Abonnement-Registrierung). */
    private PrintStream aktualisierungsAusgabe;

    /** Eindeutige Identifikationsnummer dieses Clients, um Push-Nachrichten korrekt zuzuordnen. */
    private final String clientKennung = UUID.randomUUID().toString();

    /**
     * Erstellt eine neue Verbindung zum eShop-Server und initialisiert die Kommunikationsströme.
     * Nach erfolgreichem Handshake wird automatisch das Update-Abonnement gestartet.
     *
     * @param host Der Hostname oder die IP-Adresse des eShop-Servers.
     * @param port Der Port, auf dem der eShop-Server auf Verbindungen lauscht.
     * @throws IOException Wenn beim Verbindungsaufbau oder dem Handshake ein Netzwerkfehler auftritt.
     */
    public EShopFassade(String host, int port) throws IOException {
        try {
            socket = new Socket(host, port);
            sin = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            sout = new PrintStream(socket.getOutputStream());
        } catch (IOException e) {
            System.err.println("Fehler beim Socket-Stream öffnen: " + e);
            // Wenn im "try"-Block Fehler auftreten, dann Socket schließen:
            if (socket != null)
                socket.close();
            System.err.println("Socket geschlossen");
            System.exit(0);
        }

        System.err.println("Verbunden: " + socket.getInetAddress() + ":"
                + socket.getPort());

        // Begrüßungsmeldung vom Server lesen
        String message = sin.readLine();
        System.out.println(message);

        sout.println("CLIENT_KENNUNG");
        sout.println(clientKennung);
        if (!"CLIENT_KENNUNG: OK".equals(sin.readLine())) {
            throw new IOException("Client-Kennung wurde vom Server nicht bestätigt.");
        }

        starteAktualisierungsVerbindung(host, port);
    }

    /**
     * Baut die sekundäre Socket-Verbindung für den asynchronen Benachrichtigungsdienst auf.
     * Bei Erfolg wird ein Daemon-Thread gestartet, der permanent auf eingehende Server-Updates wartet.
     *
     * @param host Der Hostname des Servers.
     * @param port Der Port des Servers.
     */
    private void starteAktualisierungsVerbindung(String host, int port) {
        try {
            aktualisierungsSocket = new Socket(host, port);
            BufferedReader aktualisierungsEingabe = new BufferedReader(
                    new InputStreamReader(aktualisierungsSocket.getInputStream(), StandardCharsets.UTF_8));
            aktualisierungsAusgabe = new PrintStream(
                    aktualisierungsSocket.getOutputStream(), true, StandardCharsets.UTF_8);

            aktualisierungsEingabe.readLine();
            aktualisierungsAusgabe.println("ABONNIERE_AKTUALISIERUNGEN");
            aktualisierungsAusgabe.println(clientKennung);
            if (!"ABONNIERT".equals(aktualisierungsEingabe.readLine())) {
                aktualisierungsSocket.close();
                return;
            }

            Thread thread = new Thread(() -> empfangeAktualisierungen(aktualisierungsEingabe),
                    "eshop-aktualisierungen");
            thread.setDaemon(true);
            thread.start();
        } catch (IOException e) {
            System.err.println("Aktualisierungsverbindung nicht verfügbar: " + e.getMessage());
        }
    }

    /**
     * Die Endlosschleife für den Hintergrund-Thread, die eingehende Push-Nachrichten
     * vom Server liest und verarbeitet.
     *
     * @param aktualisierungsEingabe Der Eingabestrom des Update-Sockets.
     */
    private void empfangeAktualisierungen(BufferedReader aktualisierungsEingabe) {
        try {
            String nachricht;
            while ((nachricht = aktualisierungsEingabe.readLine()) != null) {
                if (nachricht.startsWith("AKTUALISIERUNG:")) {
                    String bereich = nachricht.substring("AKTUALISIERUNG:".length());
                    System.out.println("[eShop] Serveränderung empfangen: " + bereich);
                }
            }
        } catch (IOException e) {
            if (aktualisierungsSocket != null && !aktualisierungsSocket.isClosed()) {
                System.err.println("Aktualisierungsverbindung beendet: " + e.getMessage());
            }
        }
    }

    /**
     * Authentifiziert einen Benutzer am Server.
     *
     * @param benutzerErkennung Der eindeutige Login-Name des Benutzers.
     * @param benutzerPasswort Das Passwort des Benutzers.
     * @return {@code true}, wenn die Anmeldedaten korrekt waren und der Login erfolgreich war,
     *         andernfalls {@code false}.
     */
    @Override
    public boolean login(String benutzerErkennung, String benutzerPasswort) {
        sout.println("LOGIN");

        sout.println(benutzerErkennung);
        sout.println(benutzerPasswort);

        try {
            String antwort = sin.readLine();

            return Boolean.parseBoolean(antwort);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    /**
     * Meldet den aktuell angemeldeten Benutzer am Server ab und beendet die Sitzung.
     */
    @Override
    public void logout() {
        sout.println("LOGOUT");

        try {
            String antwort = sin.readLine();

            if (antwort.equals("LOGOUT: OK"))
                return;

            System.err.println("Unerwartete Antwort vom Server bei Logout:" + antwort);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Prüft, ob aktuell ein Benutzer an diesem Client eingeloggt ist.
     *
     * @return {@code true}, wenn eine aktive Sitzung besteht, andernfalls {@code false}.
     */
    @Override
    public boolean istEingeloggt() {
        sout.println("IST_EINGELOGGT");
        try {
            String antwort = sin.readLine();
            return Boolean.parseBoolean(antwort);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    /**
     * Prüft, ob der aktuell angemeldete Benutzer die Rolle eines Mitarbeiters besitzt.
     *
     * @return {@code true}, wenn der Benutzer ein Mitarbeiter ist, andernfalls {@code false}.
     */
    @Override
    public boolean istMitarbeiter() {
        sout.println("IST_MITARBEITER");

        try {
            String antwort = sin.readLine();

            return Boolean.parseBoolean(antwort);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    /**
     * Prüft, ob der aktuell angemeldete Benutzer die Rolle eines Kunden besitzt.
     *
     * @return {@code true}, wenn der Benutzer ein Kunde ist, andernfalls {@code false}.
     */
    @Override
    public boolean istKunde() {
        sout.println("IST_KUNDE");

        try {
            String antwort = sin.readLine();

            return Boolean.parseBoolean(antwort);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    /**
     * Ruft die Daten des aktuell angemeldeten Benutzers vom Server ab.
     *
     * @return Das {@link Benutzer}-Objekt des angemeldeten Nutzers oder {@code null},
     *         wenn niemand eingeloggt ist.
     */
    @Override
    public Benutzer aktuellerBenutzer() {
        sout.println("AKTUELLER_BENUTZER");

        try {
            String antwort = sin.readLine();
            if (antwort.equals("null")) {
                return null;
            }

            return Benutzer.fromNetworkString(antwort);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    /**
     * Registriert einen neuen Benutzer (Kunde oder Mitarbeiter) im System.
     *
     * @param benutzer Das neu zu registrierende {@link Benutzer}-Objekt.
     * @return {@code true}, wenn die Registrierung erfolgreich war, andernfalls {@code false}.
     */
    @Override
    public boolean registrieren(Benutzer benutzer) {
        sout.println("REGISTRIEREN");

        sout.println(benutzer.toNetworkString());

        try {
            String antwort = sin.readLine();
            return Boolean.parseBoolean(antwort);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    /**
     * Fordert eine neue, eindeutige Benutzer-ID vom Server an.
     *
     * @return Eine eindeutige Benutzer-ID, oder {@code -1} bei einem Netzwerkfehler.
     */
    @Override
    public int generiereId() {
        sout.println("GENERIERE_ID");

        try {
            String antwort = sin.readLine();
            return Integer.parseInt(antwort);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return -1;
        }
    }

    /**
     * Fordert eine neue, eindeutige Artikel-ID vom Server an.
     *
     * @return Eine eindeutige Artikel-ID, oder {@code -1} bei einem Netzwerkfehler.
     */
    @Override
    public int generiereArtikelID() {
        sout.println("GENERIERE_ARTIKEL_ID");

        try {
            String antwort = sin.readLine();
            return Integer.parseInt(antwort);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return -1;
        }
    }

    /**
     * Ruft die vollständige Liste aller im Shop existierenden Artikel ab.
     *
     * @return Eine {@link HashMap}, die Artikel-IDs auf die zugehörigen {@link Artikel}-Objekte abbildet.
     */
    @Override
    public HashMap<Integer, Artikel> gibArtikelListe() {
        sout.println("GIB_ARTIKEL_LISTE");

        try {
            int anzahl = Integer.parseInt(sin.readLine());
            HashMap<Integer, Artikel> artikelListe = new HashMap<>();

            for (int i = 0; i < anzahl; i++) {
                Artikel a = Artikel.fromNetworkString(sin.readLine());
                artikelListe.put(a.getArtikelID(), a);
            }

            return artikelListe;
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    /**
     * Ruft eine Übersicht der aktuellen Lagerbestände aller Artikel ab.
     *
     * @return Eine {@link HashMap}, die Artikel-IDs auf deren verfügbare Menge (Bestand) abbildet.
     */
    @Override
    public HashMap<Integer, Integer> gibArtikelMengeListe() {
        sout.println("GIB_ARTIKEL_MENGE_LISTE");

        try {
            int anzahl = Integer.parseInt(sin.readLine());
            HashMap<Integer, Integer> artikelMengeListe = new HashMap<>();

            for (int i = 0; i < anzahl; i++) {
                int artikelID = Integer.parseInt(sin.readLine());
                int bestand = Integer.parseInt(sin.readLine());
                artikelMengeListe.put(artikelID, bestand);
            }

            return artikelMengeListe;
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    /**
     * Fügt einen neuen Standard-Artikel in das Warensortiment des eShops ein.
     *
     * @param artikelID Die eindeutige ID des neuen Artikels.
     * @param bezeichnung Der Name/die Bezeichnung des Artikels.
     * @param menge Der anfängliche Lagerbestand.
     * @param preis Der Stückpreis des Artikels.
     * @param mitarbeiter Der Name des Mitarbeiters, der die Aktion durchführt.
     * @throws DateiNichtGefundenException Wenn die serverseitige Persistenzdatei nicht bereitsteht.
     * @throws UngueltigerPreisException Wenn der übergebene Preis negativ oder fehlerhaft ist.
     * @throws UngueltigeMengeException Wenn die Menge negativ ist.
     */
    @Override
    public void fuegeArtikelEin(int artikelID, String bezeichnung, int menge, BigDecimal preis, String mitarbeiter) throws DateiNichtGefundenException, UngueltigerPreisException, UngueltigeMengeException {
        sout.println("FUEGE_ARTIKEL_EIN");

        sout.println(artikelID + ";" + bezeichnung + ";" + menge + ";" + preis + ";" + mitarbeiter);

        try {
            String antwort = sin.readLine();

            if (antwort.equals("FUEGE_ARTIKEL_EIN: OK")) {
                return;
            }

            if (antwort.equals("FUEGE_ARTIKEL_EIN: ERR_PREIS")) {
                throw new UngueltigerPreisException(preis);
            } else if (antwort.equals("FUEGE_ARTIKEL_EIN: ERR_MENGE")) {
                throw new UngueltigeMengeException(menge);
            } else if (antwort.equals("FUEGE_ARTIKEL_EIN: ERR_DATEI")) {
                String dateiName = sin.readLine();
                throw new DateiNichtGefundenException(dateiName);
            } else if (antwort.equals("FUEGE_ARTIKEL_EIN: ERR_ARTIKEL")) {
                throw new ArtikelExistiertBereitsException(findeArtikel(sucheNachID(bezeichnung)));
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Fügt einen neuen Massengutartikel in das Sortiment ein. Ein solcher Artikel kann nur in
     * definierten Abpackungseinheiten (Packungsgrößen) gekauft oder verändert werden.
     *
     * @param artikelID Die eindeutige ID des Artikels.
     * @param bezeichnung Die Bezeichnung des Artikels.
     * @param menge Der anfängliche Lagerbestand.
     * @param preis Der Preis pro Stück.
     * @param mitarbeiter Der Name des ausführenden Mitarbeiters.
     * @param packungGroesse Die Mindest- und Schrittgröße für Bestellungen dieses Artikels.
     * @throws UngueltigeMengeException Wenn die Menge negativ ist.
     * @throws UngueltigerPreisException Wenn der Preis ungültig ist.
     * @throws MengeWenigerAlsPackungGroesseException Wenn die Startmenge kleiner als die Packungsgröße ist.
     * @throws MassengutartikelmengeNichtTeilbarException Wenn die Gesamtmenge kein Vielfaches der Packungsgröße ist.
     * @throws DateiNichtGefundenException Wenn die Server-Datenquelle fehlt.
     */
    @Override
    public void fuegeMassengutartikelEin(int artikelID, String bezeichnung, int menge, BigDecimal preis, String mitarbeiter, int packungGroesse) throws UngueltigeMengeException, UngueltigerPreisException, MengeWenigerAlsPackungGroesseException, MassengutartikelmengeNichtTeilbarException, DateiNichtGefundenException {
        sout.println("FUEGE_MASSENGUTARTIKEL_EIN");

        sout.println(artikelID + ";" + bezeichnung + ";" + menge + ";" + preis + ";" + mitarbeiter + ";" + packungGroesse);

        try {
            String antwort = sin.readLine();

            if (antwort.equals("FUEGE_MASSENGUTARTIKEL_EIN: OK")) {
                return;
            }

            if (antwort.equals("FUEGE_MASSENGUTARTIKEL_EIN: ERR_PREIS")) {
                throw new UngueltigerPreisException(preis);
            } else if (antwort.equals("FUEGE_MASSENGUTARTIKEL_EIN: ERR_MENGE")) {
                throw new UngueltigeMengeException(menge);
            } else if (antwort.equals("FUEGE_MASSENGUTARTIKEL_EIN: ERR_DATEI")) {
                String dateiName = sin.readLine();
                throw new DateiNichtGefundenException(dateiName);
            } else if (antwort.equals("FUEGE_MASSENGUTARTIKEL_EIN: ERR_MENGE_WENIGER")) {
                throw new MengeWenigerAlsPackungGroesseException(bezeichnung, menge, packungGroesse);
            } else if (antwort.equals("FUEGE_MASSENGUTARTIKEL_EIN: ERR_MENGE_NICHT_TEILBAR")) {
                throw new MassengutartikelmengeNichtTeilbarException(bezeichnung, packungGroesse);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Löscht einen Artikel dauerhaft aus dem System.
     *
     * @param artikelID Die ID des zu löschenden Artikels.
     */
    @Override
    public void artikelVernichten(int artikelID) {
        sout.println("ARTIKEL_VERNICHTEN");
        sout.println(artikelID);

        try {
            String antwort = sin.readLine();

            if (antwort.equals("ARTIKEL_VERNICHTEN: OK"))
                return;

            System.err.println("Unerwartete Antwort vom Server beim Artikel Vernichten:" + antwort);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Ändert den Namen bzw. die Bezeichnung eines bestehenden Artikels.
     *
     * @param artikelID Die ID des betroffenen Artikels.
     * @param bezeichnung Die neue Bezeichnung.
     * @throws DateiNichtGefundenException Wenn die Datei auf dem Server nicht existiert.
     */
    @Override
    public void bezeichnungVeraendern(int artikelID, String bezeichnung) throws DateiNichtGefundenException {
        sout.println("BEZEICHNUNG_VERAENDERN");

        sout.println(artikelID + ";" + bezeichnung);
        try {
            String antwort = sin.readLine();

            if (antwort.equals("BEZEICHNUNG_VERAENDERN: OK"))
                return;

            if (antwort.equals("BEZEICHNUNG_VERAENDERN: ERR_DATEI")) {
                String dateiName = sin.readLine();
                throw new DateiNichtGefundenException(dateiName);
            }

            if (antwort.equals("BEZEICHNUNG_VERAENDERN: ERR_ARTIKEL")) {
                throw new ArtikelExistiertBereitsException(findeArtikel(sucheNachID(bezeichnung)));
            }

            System.err.println("Unerwartete Antwort vom Server beim Veraendern von der Bezeichnung:" + antwort);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Ändert den Verkaufspreis eines Artikels.
     *
     * @param artikelID Die ID des betroffenen Artikels.
     * @param preis Der neue Preis als {@link BigDecimal}.
     * @throws DateiNichtGefundenException Wenn die Serverdatei nicht gefunden wurde.
     * @throws UngueltigerPreisException Wenn der Preis die geschäftlichen Validierungsregeln verletzt.
     */
    @Override
    public void preisVeraendern(int artikelID, BigDecimal preis) throws DateiNichtGefundenException, UngueltigerPreisException {
        sout.println("PREIS_VERAENDERN");

        sout.println(artikelID + ";" + preis);
        try {
            String antwort = sin.readLine();

            if (antwort.equals("PREIS_VERAENDERN: OK"))
                return;

            if (antwort.equals("PREIS_VERAENDERN: ERR_DATEI")) {
                String dateiName = sin.readLine();
                throw new DateiNichtGefundenException(dateiName);
            } else if (antwort.equals("PREIS_VERAENDERN: ERR_PREIS")) {
                throw new UngueltigerPreisException(preis);
            }

            System.err.println("Unerwartete Antwort vom Server beim Veraendern vom Preis:" + antwort);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Sucht anhand einer exakten Artikelbezeichnung nach der zugehörigen Artikel-ID.
     *
     * @param bezeichnung Der Name des gesuchten Artikels.
     * @return Die ID des Artikels.
     * @throws ArtikelExistiertNichtException Wenn kein Artikel mit dieser Bezeichnung existiert.
     */
    @Override
    public int sucheNachID(String bezeichnung) throws ArtikelExistiertNichtException {
        sout.println("SUCHE_NACH_ID");

        sout.println(bezeichnung);
        try {
            String antwort = sin.readLine();

            if (antwort.equals("SUCHE_NACH_ID: ERR_ARTIKEL"))
                throw new ArtikelExistiertNichtException(bezeichnung);

            return Integer.parseInt(antwort);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return -1;
        }
    }

    /**
     * Ändert den Lagerbestand eines Artikels (z. B. bei Wareneingang oder Verlust).
     *
     * @param artikelID Die ID des Artikels.
     * @param neuerBestand Die neue absolute Stückzahl im Lager.
     * @param mitarbeiter Der Name des buchenden Mitarbeiters.
     * @throws MengeWenigerAlsPackungGroesseException Wenn es ein Massengutartikel ist und die Menge unter der Packungsgröße liegt.
     * @throws MassengutartikelmengeNichtTeilbarException Wenn die Menge nicht durch die Packungsgröße teilbar ist.
     * @throws DateiNichtGefundenException Wenn die Persistenzdatei fehlt.
     * @throws UngueltigeMengeException Wenn die Menge negativ ist.
     */
    @Override
    public void bestandVeraendern(int artikelID, int neuerBestand, String mitarbeiter) throws MengeWenigerAlsPackungGroesseException, MassengutartikelmengeNichtTeilbarException, DateiNichtGefundenException, UngueltigeMengeException {
        sout.println("BESTAND_VERAENDERN");

        sout.println(artikelID + ";" + neuerBestand + ";" + mitarbeiter);
        try {
            String antwort = sin.readLine();

            if (antwort.equals("BESTAND_VERAENDERN: OK"))
                return;

            if (antwort.equals("BESTAND_VERAENDERN: ERR_DATEI")) {
                String dateiName = sin.readLine();
                throw new DateiNichtGefundenException(dateiName);
            } else if (antwort.equals("BESTAND_VERAENDERN: ERR_MENGE_WENIGER")) {
                throw new MengeWenigerAlsPackungGroesseException(gibArtikelName(artikelID), neuerBestand, this.gibPackungGroesse(artikelID));
            } else if (antwort.equals("BESTAND_VERAENDERN: ERR_MENGE_NICHT_TEILBAR")) {
                throw new MassengutartikelmengeNichtTeilbarException(gibArtikelName(artikelID), this.gibPackungGroesse(artikelID));
            } else if (antwort.equals("BESTAND_VERAENDERN: ERR_MENGE")) {
                throw new UngueltigeMengeException(neuerBestand);
            }

            System.err.println("Unerwartete Antwort vom Server beim Veraendern vom Bestand:" + antwort);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Prüft, ob es sich bei dem angegebenen Artikel um einen Massengutartikel handelt.
     *
     * @param artikelID Die ID des Artikels.
     * @return {@code true}, wenn es ein Massengutartikel ist, sonst {@code false}.
     */
    @Override
    public boolean istMassengutartikel(int artikelID) {
        sout.println("IST_MASSENGUTARTIKEL");

        sout.println(artikelID);

        try {
            String antwort = sin.readLine();
            return Boolean.parseBoolean(antwort);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    /**
     * Gibt die Packungsgröße eines Artikels zurück.
     *
     * @param artikelID Die ID des Artikels.
     * @return Die Packungsgröße (z. B. 5er-Schritte). Gibt {@code 1} bei Standard-Artikeln zurück,
     *         oder {@code -1} bei Fehlern.
     */
    @Override
    public int gibPackungGroesse(int artikelID) {
        sout.println("GIB_PACKUNGSGROESSE");

        sout.println(artikelID);

        try {
            String antwort = sin.readLine();
            return Integer.parseInt(antwort);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return -1;
        }
    }

    /**
     * Ändert die Packungsgröße für einen Massengutartikel.
     *
     * @param artikelID Die ID des Artikels.
     * @param neueGroesse Die neue Mindest-Schrittgröße.
     * @throws MassengutartikelmengeNichtTeilbarException Wenn der aktuelle Lagerbestand nicht
     *         durch die neue Packungsgröße teilbar ist.
     */
    @Override
    public void packungGroesseVeraendern(int artikelID, int neueGroesse) throws MassengutartikelmengeNichtTeilbarException {
        sout.println("PACKUNGSGROESSE_VERAENDERN");

        sout.println(artikelID + ";" + neueGroesse);

        try {
            String antwort = sin.readLine();

            if (antwort.equals("PACKUNGSGROESSE_VERAENDERN: OK"))
                return;
            if (antwort.equals("PACKUNGSGROESSE_VERAENDERN: ERR_MENGE_NICHT_TEILBAR")) {
                throw new MassengutartikelmengeNichtTeilbarException(gibArtikelName(artikelID), neueGroesse);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Fragt den aktuellen Lagerbestand eines einzelnen Artikels ab.
     *
     * @param artikelID Die ID des Artikels.
     * @return Die im Lager vorhandene Menge.
     */
    @Override
    public int gibBestand(int artikelID) {
        sout.println("GIB_BESTAND");
        sout.println(artikelID);

        try {
            String antwort = sin.readLine();

            return Integer.parseInt(antwort);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return -1;
        }
    }

    /**
     * Ruft den Namen eines Artikels ab.
     *
     * @param artikelID Die ID des Artikels.
     * @return Der Name des Artikels als Text.
     */
    @Override
    public String gibArtikelName(int artikelID) {
        sout.println("GIB_ARTIKEL_NAME");
        sout.println(artikelID);

        try {
            String antwort = sin.readLine();

            return antwort;
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    /**
     * Sucht einen Artikel anhand seiner ID und gibt das vollständige Objekt zurück.
     *
     * @param artikelID Die ID des gesuchten Artikels.
     * @return Das geladene {@link Artikel}-Objekt.
     */
    @Override
    public Artikel findeArtikel(int artikelID) {
        sout.println("FINDE_ARTIKEL");
        sout.println(artikelID);

        try {
            String antwort = sin.readLine();

            return Artikel.fromNetworkString(antwort);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    /**
     * Ruft den aktuellen Preis eines Artikels ab und formatiert ihn kaufmännisch.
     *
     * @param artikelID Die ID des Artikels.
     * @return Der Stückpreis auf 2 Nachkommastellen gerundet (HALF_EVEN).
     */
    @Override
    public BigDecimal gibPreis(int artikelID) {
        sout.println("GIB_PREIS");
        sout.println(artikelID);

        try {
            String antwort = sin.readLine();

            return new BigDecimal(antwort).setScale(2, RoundingMode.HALF_EVEN);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    /**
     * Ruft den aktuellen Warenkorb des eingeloggten Kunden ab.
     *
     * @return Eine {@link HashMap}, die Artikel-IDs auf die im Warenkorb befindliche Anzahl abbildet.
     */
    @Override
    public HashMap<Integer, Integer> gibWarenkorb() {
        sout.println("GIB_WARENKORB");

        try {
            int anzahl = Integer.parseInt(sin.readLine());
            HashMap<Integer, Integer> warenkorbListe = new HashMap<>();

            for (int i = 0; i < anzahl; i++) {
                int artikelID = Integer.parseInt(sin.readLine());
                int bestand = Integer.parseInt(sin.readLine());
                warenkorbListe.put(artikelID, bestand);
            }

            return warenkorbListe;
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    /**
     * Legt eine bestimmte Menge eines Artikels in den Warenkorb des Kunden.
     *
     * @param artikelID Die ID des Artikels.
     * @param menge Die gewünschte Menge, die hinzugefügt werden soll.
     * @param kunde Der Benutzername des Kunden.
     * @throws UngueltigeMengeException Wenn die Menge kleiner oder gleich 0 ist.
     * @throws BestandNichtAusreichendException Wenn nicht mehr genügend Artikel im Lager vorhanden sind.
     * @throws MengeWenigerAlsPackungGroesseException Wenn die Menge die Massengutkriterien unterschreitet.
     * @throws MassengutartikelmengeNichtTeilbarException Wenn die Menge nicht zur Packungsgröße passt.
     * @throws DateiNichtGefundenException Wenn ein Server-Dateifehler auftritt.
     */
    @Override
    public void fuegeInWarenkorb(int artikelID, int menge, String kunde) throws UngueltigeMengeException, BestandNichtAusreichendException, MengeWenigerAlsPackungGroesseException, MassengutartikelmengeNichtTeilbarException, DateiNichtGefundenException {
        sout.println("FUEGE_IN_WARENKORB");

        sout.println(artikelID + ";" + menge + ";" + kunde);
        try {
            String antwort = sin.readLine();

            if (antwort.equals("FUEGE_IN_WARENKORB: OK"))
                return;

            if (antwort.equals("FUEGE_IN_WARENKORB: ERR_DATEI")) {
                String dateiName = sin.readLine();
                throw new DateiNichtGefundenException(dateiName);
            } else if (antwort.equals("FUEGE_IN_WARENKORB: ERR_MENGE_WENIGER")) {
                throw new MengeWenigerAlsPackungGroesseException(gibArtikelName(artikelID), menge, this.gibPackungGroesse(artikelID));
            } else if (antwort.equals("FUEGE_IN_WARENKORB: ERR_MENGE_NICHT_TEILBAR")) {
                throw new MassengutartikelmengeNichtTeilbarException(gibArtikelName(artikelID), this.gibPackungGroesse(artikelID));
            } else if (antwort.equals("FUEGE_IN_WARENKORB: ERR_MENGE")) {
                throw new UngueltigeMengeException(menge);
            } else if (antwort.equals("FUEGE_IN_WARENKORB: ERR_BESTAND_NICHT_GENUG")) {
                throw new BestandNichtAusreichendException(gibArtikelName(artikelID), gibBestand(artikelID), menge);
            }

            System.err.println("Unerwartete Antwort vom Server beim Fuegen in Warenkorb:" + antwort);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Entfernt eine bestimmte Anzahl eines Artikels aus dem Warenkorb des Kunden.
     *
     * @param artikelID Die ID des Artikels.
     * @param menge Die zu entfernende Stückzahl.
     * @param kunde Der Benutzername des Kunden.
     * @throws UngueltigeMengeException Wenn die übergebene Menge ungültig ist.
     * @throws MengeWenigerAlsPackungGroesseException Wenn die Regeln für Massengutartikel verletzt werden.
     * @throws MassengutartikelmengeNichtTeilbarException Wenn die Restmenge/Entfernungsmenge nicht teilbar ist.
     * @throws DateiNichtGefundenException Wenn ein Dateizugriffsfehler auf dem Server vorliegt.
     */
    @Override
    public void loescheAusWarenkorb(int artikelID, int menge, String kunde) throws UngueltigeMengeException, MengeWenigerAlsPackungGroesseException, MassengutartikelmengeNichtTeilbarException, DateiNichtGefundenException {
        sout.println("LOESCHE_AUS_WARENKORB");

        sout.println(artikelID + ";" + menge + ";" + kunde);
        try {
            String antwort = sin.readLine();

            if (antwort.equals("LOESCHE_AUS_WARENKORB: OK"))
                return;

            if (antwort.equals("LOESCHE_AUS_WARENKORB: ERR_DATEI")) {
                String dateiName = sin.readLine();
                throw new DateiNichtGefundenException(dateiName);
            } else if (antwort.equals("LOESCHE_AUS_WARENKORB: ERR_MENGE_WENIGER")) {
                throw new MengeWenigerAlsPackungGroesseException(gibArtikelName(artikelID), menge, this.gibPackungGroesse(artikelID));
            } else if (antwort.equals("LOESCHE_AUS_WARENKORB: ERR_MENGE_NICHT_TEILBAR")) {
                throw new MassengutartikelmengeNichtTeilbarException(gibArtikelName(artikelID), this.gibPackungGroesse(artikelID));
            } else if (antwort.equals("LOESCHE_AUS_WARENKORB: ERR_MENGE")) {
                throw new UngueltigeMengeException(menge);
            }

            System.err.println("Unerwartete Antwort vom Server beim Loeschen aus dem Warenkorb:" + antwort);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Leert den gesamten Warenkorb des aktuell angemeldeten Kunden (entfernt alle Positionen).
     */
    @Override
    public void zuruecksetzeWarenkorb() {
        sout.println("ZURUECKSETZE_WARENKORB");

        try {
            String antwort = sin.readLine();

            if (antwort.equals("ZURUECKSETZE_WARENKORB: OK"))
                return;

            System.err.println("Unerwartete Antwort vom Server beim Warenkorb Zurücksetzen:" + antwort);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Ruft das historische Logbuch (Ereignishistorie) des eShops ab. Enthält Informationen darüber,
     * welcher Benutzer wann welche Aktion (Kauf, Bestandsänderung, etc.) durchgeführt hat.
     *
     * @return Eine {@link ArrayList} mit allen aufgezeichneten {@link Ereignis}-Objekten.
     */
    @Override
    public ArrayList<Ereignis> gibEreignisListe() {
        sout.println("GIB_EREIGNISLISTE");

        try {
            int anzahl = Integer.parseInt(sin.readLine());
            ArrayList<Ereignis> ereignisListe = new ArrayList<>();

            for (int i = 0; i < anzahl; i++) {
                Ereignis e = Ereignis.fromNetworkString(sin.readLine());
                ereignisListe.add(e);
            }

            return ereignisListe;
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    /**
     * Berechnet den zeitlichen Verlauf des Lagerbestands für einen spezifischen Artikel.
     * Wertet dazu die historischen Ereignisse auf dem Server aus.
     *
     * @param artikelID Die ID des Artikels.
     * @return Eine sortierte {@link Map} (LinkedHashMap), bei der das Datum ({@link LocalDate})
     *         dem historischen Bestand an diesem Tag zugeordnet ist.
     */
    @Override
    public Map<LocalDate, Integer> berechneBestandHistorie(int artikelID) {
        sout.println("BERECHNE_BESTANDHISTORIE");
        sout.println(artikelID);

        try {
            int anzahl = Integer.parseInt(sin.readLine());
            Map<LocalDate, Integer> bestandHistorie = new LinkedHashMap<>();

            for (int i = 0; i < anzahl; i++) {
                String[] antwort = sin.readLine().split(";");
                LocalDate date = LocalDate.parse(antwort[0]);
                int bestand = Integer.parseInt(antwort[1]);

                bestandHistorie.put(date, bestand);
            }

            return bestandHistorie;
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }
}
