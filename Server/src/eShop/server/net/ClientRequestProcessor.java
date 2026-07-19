package eShop.server.net;

import eShop.common.entities.Artikel;
import eShop.common.entities.Benutzer;
import eShop.common.entities.Ereignis;
import eShop.common.exceptions.*;
import eShop.server.domain.EShop;

import java.io.*;
import java.math.BigDecimal;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

/**
 * Der ClientRequestProcessor ist für die serverseitige Verarbeitung des zeilenbasierten
 * eShop-Netzwerkprotokolls für genau einen verbundenen Client zuständig.
 * Er implementiert das {@link Runnable}-Interface, um in einem dedizierten Worker-Thread
 * eingehende Befehle zu parsen, die Kernlogik der Domänenschicht (@link EShop} thread-sicher
 * aufzurufen und die entsprechenden Antworttelegramme an den Client zurückzusenden.
 */
public final class ClientRequestProcessor implements Runnable {
    /** Das Netzwerk-Socket für die bidirektionale Kommunikation mit dem Client. */
    private final Socket socket;

    /** Die zentrale Fassade der Domänenschicht zur Ausführung der Geschäftslogik. */
    private final EShop shop;

    /** Der Dienst zur Verteilung von Echtzeit-Aktualisierungen an andere verbundene Clients. */
    private final AktualisierungsDienst aktualisierungsDienst;

    /** Der gepufferte Eingabestrom zum Lesen von Textzeilen aus dem Netzwerk. */
    private BufferedReader in;

    /** Der Ausgabestrom zum Senden von Textzeilen an den Client. */
    private PrintWriter out;

    /** Das Domänenobjekt des aktuell in dieser Sitzung authentifizierten Benutzers (null, falls anonym). */
    private Benutzer currentUser;

    /** Eine temporäre, sitzungsbasierte Warenkorb-ID für nicht angemeldete (anonyme) Kunden. */
    private final String sitzungsWarenkorb = "sitzung-" + UUID.randomUUID();

    /** Die eindeutige Kennung dieser Netzwerkverbindung zur Identifikation im Aktualisierungsdienst. */
    private String clientKennung;

    /**
     * Erstellt einen neuen Prozessor für eine Client-Verbindung mit einem standardmäßig
     * neu instanziierten Aktualisierungsdienst.
     *
     * @param socket Das geöffnete Client-Socket.
     * @param shop   Die Domänen-Fassade des eShops.
     */
    public ClientRequestProcessor(Socket socket, EShop shop) {
        this(socket, shop, new AktualisierungsDienst());
    }

    /**
     * Erstellt einen neuen Prozessor für eine Client-Verbindung unter Verwendung eines
     * geteilten Aktualisierungsdienstes.
     *
     * @param socket                Das geöffnete Client-Socket.
     * @param shop                  Die Domänen-Fassade des eShops.
     * @param aktualisierungsDienst Der zentrale Aktualisierungsdienst für Server-Events.
     */
    public ClientRequestProcessor(Socket socket, EShop shop, AktualisierungsDienst aktualisierungsDienst) {
        this.socket = socket;
        this.shop = shop;
        this.aktualisierungsDienst = aktualisierungsDienst;
        this.clientKennung = "verbindung-" + UUID.randomUUID();
    }

    /**
     * Startet die Endlosschleife des Protokoll-Parsers. Initialisiert die I/O-Streams
     * in UTF-8, liest eingehende Client-Befehle zeilenweise ein und delegiert sie an die
     * Verarbeitung. Fängt Verbindungsabbrüche ab und sorgt im {@code finally}-Block für
     * die saubere Abmeldung vom Aktualisierungsdienst.
     */
    @Override
    public void run() {
        try (socket;
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {
            in = reader;
            out = writer;
            out.println("Willkommen beim eShop-Server");
            String command;
            while ((command = in.readLine()) != null) {
                if (command.equals("q")) {
                    out.println("Verbindung beendet");
                    break;
                }
                try {
                    process(command);
                } catch (RuntimeException e) {
                    out.println("ERR");
                    System.err.println("Ungültige Client-Anfrage " + command + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Client-Verbindung beendet: " + e.getMessage());
        } finally {
            aktualisierungsDienst.abmelden(out);
        }
    }

    /**
     * Verarbeitet den empfangenen Protokoll-Befehl über ein kaskadierendes Switch-Statement.
     * Liest bei Bedarf zusätzliche Parameterzeilen aus dem Netzwerkstrom.
     *
     * @param command Der zu verarbeitende Befehlsschlüssel als Text.
     * @throws IOException Wenn ein Fehler beim Lesen oder Schreiben auf dem Socket auftritt.
     */
    private void process(String command) throws IOException {
        switch (command) {
            case "CLIENT_KENNUNG" -> { clientKennung = requiredLine(); out.println("CLIENT_KENNUNG: OK"); }
            case "ABONNIERE_AKTUALISIERUNGEN" -> abonniereAktualisierungen();
            case "LOGIN" -> login();
            case "LOGOUT" -> { currentUser = null; out.println("LOGOUT: OK"); }
            case "IST_EINGELOGGT" -> out.println(currentUser != null);
            case "IST_MITARBEITER" -> out.println(currentUser != null && "Mitarbeiter".equals(currentUser.getRole()));
            case "IST_KUNDE" -> out.println(currentUser != null && "Kunde".equals(currentUser.getRole()));
            case "AKTUELLER_BENUTZER" -> out.println(currentUser == null ? "null" : currentUser.toNetworkString());
            case "REGISTRIEREN" -> registrieren();
            case "GENERIERE_ID" -> locked(() -> out.println(shop.generiereId()));
            case "GENERIERE_ARTIKEL_ID" -> out.println(shop.generiereArtikelID());
            case "GIB_ARTIKEL_LISTE" -> locked(this::sendArticles);
            case "GIB_ARTIKEL_MENGE_LISTE" -> locked(this::sendQuantities);
            case "FUEGE_ARTIKEL_EIN" -> addArticle(false);
            case "FUEGE_MASSENGUTARTIKEL_EIN" -> addArticle(true);
            case "ARTIKEL_VERNICHTEN" -> locked(() -> { shop.artikelVernichten(readInt()); out.println("ARTIKEL_VERNICHTEN: OK"); meldeAenderung("ARTIKEL"); });
            case "BEZEICHNUNG_VERAENDERN" -> changeDescription();
            case "PREIS_VERAENDERN" -> changePrice();
            case "SUCHE_NACH_ID" -> searchId();
            case "BESTAND_VERAENDERN" -> changeStock();
            case "IST_MASSENGUTARTIKEL" -> locked(() -> out.println(shop.istMassengutartikel(readInt())));
            case "GIB_PACKUNGSGROESSE" -> locked(() -> out.println(shop.gibPackungGroesse(readInt())));
            case "PACKUNGSGROESSE_VERAENDERN" -> changePackageSize();
            case "GIB_BESTAND" -> locked(() -> out.println(shop.gibBestand(readInt())));
            case "GIB_ARTIKEL_NAME" -> locked(() -> out.println(shop.gibArtikelName(readInt())));
            case "FINDE_ARTIKEL" -> locked(() -> { Artikel a = shop.findeArtikel(readInt()); out.println(a == null ? "null" : a.toNetworkString()); });
            case "GIB_PREIS" -> locked(() -> out.println(shop.gibPreis(readInt())));
            case "GIB_WARENKORB" -> locked(this::sendCart);
            case "FUEGE_IN_WARENKORB" -> changeCart(true);
            case "LOESCHE_AUS_WARENKORB" -> changeCart(false);
            case "ZURUECKSETZE_WARENKORB" -> locked(() -> { shop.zuruecksetzeWarenkorb(warenkorbSchluessel()); out.println("ZURUECKSETZE_WARENKORB: OK"); meldeAenderung("WARENKORB"); });
            case "GIB_EREIGNISLISTE" -> locked(this::sendEvents);
            case "BERECHNE_BESTANDHISTORIE" -> locked(this::sendHistory);
            default -> out.println("ERR_UNBEKANNTER_BEFEHL");
        }
    }

    /**
     * Registriert die aktuelle Client-Verbindung im {@link AktualisierungsDienst},
     * um fortan Live-Push-Benachrichtigungen über Datenänderungen zu empfangen.
     *
     * @throws IOException Bei I/O-Fehlern auf dem Netzwerkstrom.
     */
    private void abonniereAktualisierungen() throws IOException {
        clientKennung = requiredLine();
        out.println("ABONNIERT");
        aktualisierungsDienst.anmelden(clientKennung, out);
    }

    /**
     * Liest die serialisierten Benutzerdaten ein und registriert einen neuen
     * Account im System. Meldet bei Erfolg eine Änderung im Bereich "BENUTZER".
     *
     * @throws IOException Bei I/O-Fehlern auf dem Netzwerkstrom.
     */
    private void registrieren() throws IOException {
        Benutzer benutzer = Benutzer.fromNetworkString(requiredLine());
        locked(() -> {
            boolean erfolgreich = shop.registrieren(benutzer);
            out.println(erfolgreich);
            if (erfolgreich) meldeAenderung("BENUTZER");
        });
    }

    /**
     * Authentifiziert den Benutzer anhand von Name und Passwort an der Domänenschicht.
     * Setzt bei erfolgreichem Login das {@code currentUser}-Feld für diese Sitzung.
     *
     * @throws IOException Bei I/O-Fehlern auf dem Netzwerkstrom.
     */
    private void login() throws IOException {
        String name = requiredLine();
        String password = requiredLine();
        locked(() -> {
            if (shop.login(name, password)) currentUser = shop.aktuellerBenutzer();
            shop.logout();
            out.println(currentUser != null);
        });
    }

    /**
     * Liest die Parameterzeile ein und fügt entweder einen Standardartikel oder einen
     * Massengutartikel in das Warensortiment ein. Fängt domänenspezifische Validierungsfehler
     * ab und übersetzt sie in standardisierte Protokoll-Fehlermeldungen für den Client.
     *
     * @param bulk {@code true}, wenn ein Massengutartikel mit Packungsgröße erzeugt werden soll;
     *             {@code false} für Standardartikel.
     * @throws IOException Bei I/O-Fehlern auf dem Netzwerkstrom.
     */
    private void addArticle(boolean bulk) throws IOException {
        String line = requiredLine();
        locked(() -> {
            try {
                String[] p = parts(line, bulk ? 6 : 5);
                int id = integer(p[0]);
                String name = p[1];
                int menge = integer(p[2]);
                BigDecimal preis = decimal(p[3]);
                String mitarbeiter = p[4];

                if (bulk) {
                    int packungGroesse = integer(p[5]);
                    shop.fuegeMassengutartikelEin(id, name, menge, preis, mitarbeiter, packungGroesse);
                } else {
                    shop.fuegeArtikelEin(id, name, menge, preis, mitarbeiter);
                }

                out.println(bulk ? "FUEGE_MASSENGUTARTIKEL_EIN: OK" : "FUEGE_ARTIKEL_EIN: OK");
                meldeAenderung("ARTIKEL");

            } catch (UngueltigerPreisException e) {
                out.println(prefix(bulk) + "ERR_PREIS");
            } catch (UngueltigeMengeException e) {
                out.println(prefix(bulk) + "ERR_MENGE");
            } catch (MengeWenigerAlsPackungGroesseException e) {
                out.println(prefix(bulk) + "ERR_MENGE_WENIGER");
            } catch (MassengutartikelmengeNichtTeilbarException e) {
                out.println(prefix(bulk) + "ERR_MENGE_NICHT_TEILBAR");
            } catch (DateiNichtGefundenException e) {
                out.println(prefix(bulk) + "ERR_DATEI");
            } catch (ArtikelExistiertBereitsException e) {
                out.println(prefix(bulk) + "ERR_ARTIKEL");
            } catch (RuntimeException e) {
                out.println(prefix(bulk) + "ERR_INVALID_FORMAT");
                System.err.println("Fehler beim Verarbeiten von addArticle: " + e.getMessage());
            }
        });
    }

    /**
     * Hilfsmethode zur Bestimmung des Protokoll-Präfixes basierend auf dem Artikel-Typ.
     */
    private String prefix(boolean bulk) { return bulk ? "FUEGE_MASSENGUTARTIKEL_EIN: " : "FUEGE_ARTIKEL_EIN: "; }

    /**
     * Ändert die Bezeichnung eines bestehenden Artikels im System.
     *
     * @throws IOException Bei I/O-Fehlern auf dem Netzwerkstrom.
     */
    private void changeDescription() throws IOException {
        String[] p = parts(requiredLine(), 2);
        locked(() -> { try { shop.bezeichnungVeraendern(integer(p[0]), p[1]); out.println("BEZEICHNUNG_VERAENDERN: OK"); meldeAenderung("ARTIKEL"); }
        catch (ArtikelExistiertBereitsException e) { out.println("BEZEICHNUNG_VERAENDERN: ERR_ARTIKEL"); }
        catch (DateiNichtGefundenException e) { out.println("BEZEICHNUNG_VERAENDERN: ERR_DATEI"); }
        });
    }

    /**
     * Ändert den Verkaufspreis eines bestehenden Artikels.
     *
     * @throws IOException Bei I/O-Fehlern auf dem Netzwerkstrom.
     */
    private void changePrice() throws IOException {
        String[] p = parts(requiredLine(), 2);
        locked(() -> { try { shop.preisVeraendern(integer(p[0]), decimal(p[1])); out.println("PREIS_VERAENDERN: OK"); meldeAenderung("ARTIKEL"); }
            catch (UngueltigerPreisException e) { out.println("PREIS_VERAENDERN: ERR_PREIS"); }
            catch (DateiNichtGefundenException e) { out.println("PREIS_VERAENDERN: ERR_DATEI"); } });
    }

    /**
     * Sucht die numerische ID eines Artikels anhand seiner exakten Bezeichnung.
     *
     * @throws IOException Bei I/O-Fehlern auf dem Netzwerkstrom.
     */
    private void searchId() throws IOException {
        String name = requiredLine();
        locked(() -> { try { out.println(shop.sucheNachID(name)); } catch (ArtikelExistiertNichtException e) { out.println("SUCHE_NACH_ID: ERR_ARTIKEL"); } });
    }

    /**
     * Ändert den Lagerbestand eines Artikels im System.
     *
     * @throws IOException Bei I/O-Fehlern auf dem Netzwerkstrom.
     */
    private void changeStock() throws IOException {
        String[] p = parts(requiredLine(), 3);
        locked(() -> { try { shop.bestandVeraendern(integer(p[0]), integer(p[1]), p[2]); out.println("BESTAND_VERAENDERN: OK"); meldeAenderung("BESTAND"); }
            catch (UngueltigeMengeException e) { out.println("BESTAND_VERAENDERN: ERR_MENGE"); }
            catch (MengeWenigerAlsPackungGroesseException e) { out.println("BESTAND_VERAENDERN: ERR_MENGE_WENIGER"); }
            catch (MassengutartikelmengeNichtTeilbarException e) { out.println("BESTAND_VERAENDERN: ERR_MENGE_NICHT_TEILBAR"); }
            catch (DateiNichtGefundenException e) { out.println("BESTAND_VERAENDERN: ERR_DATEI"); } });
    }

    /**
     * Ändert die vordefinierte Packungsgröße (Stückelung) eines Massengutartikels.
     *
     * @throws IOException Bei I/O-Fehlern auf dem Netzwerkstrom.
     */
    private void changePackageSize() throws IOException {
        String[] p = parts(requiredLine(), 2);
        locked(() -> { try { shop.packungGroesseVeraendern(integer(p[0]), integer(p[1])); out.println("PACKUNGSGROESSE_VERAENDERN: OK"); meldeAenderung("ARTIKEL"); }
            catch (MassengutartikelmengeNichtTeilbarException e) { out.println("PACKUNGSGROESSE_VERAENDERN: ERR_MENGE_NICHT_TEILBAR"); } });
    }

    /**
     * Manipuliert den Inhalt des Warenkorbs (Hinzufügen oder Entfernen von Artikeln).
     *
     * @param add {@code true} zum Hinzufügen von Artikeln, {@code false} zum Entfernen.
     * @throws IOException Bei I/O-Fehlern auf dem Netzwerkstrom.
     */
    private void changeCart(boolean add) throws IOException {
        String[] p = parts(requiredLine(), 3);
        String base = add ? "FUEGE_IN_WARENKORB: " : "LOESCHE_AUS_WARENKORB: ";
        locked(() -> { try {
                String kunde = warenkorbSchluessel();
                if (add) shop.fuegeInWarenkorb(integer(p[0]), integer(p[1]), kunde);
                else shop.loescheAusWarenkorb(integer(p[0]), integer(p[1]), kunde);
                out.println(base + "OK");
                meldeAenderung("BESTAND");
            } catch (UngueltigeMengeException e) { out.println(base + "ERR_MENGE");
            } catch (BestandNichtAusreichendException e) { out.println(base + "ERR_BESTAND_NICHT_GENUG");
            } catch (MengeWenigerAlsPackungGroesseException e) { out.println(base + "ERR_MENGE_WENIGER");
            } catch (MassengutartikelmengeNichtTeilbarException e) { out.println(base + "ERR_MENGE_NICHT_TEILBAR");
            } catch (DateiNichtGefundenException e) { out.println(base + "ERR_DATEI"); }
        });
    }

    /** Sendet die komplette Artikelliste inklusive Netzwerk-Serialisierung an den Client. */
    private void sendArticles() { Map<Integer, Artikel> m = shop.gibArtikelListe(); out.println(m.size()); m.values().forEach(a -> out.println(a.toNetworkString())); }

    /** Sendet eine Liste aller Artikel-IDs gepaart mit ihren aktuellen Bestandsmengen. */
    private void sendQuantities() { Map<Integer, Integer> m = shop.gibArtikelMengeListe(); out.println(m.size()); m.forEach((id, n) -> { out.println(id); out.println(n); }); }

    /** Sendet den aktuellen Inhalt des aktiven Warenkorbs (Artikel-ID und Anzahl). */
    private void sendCart() { Map<Integer, Integer> m = shop.gibWarenkorb(warenkorbSchluessel()); out.println(m.size()); m.forEach((id, n) -> { out.println(id); out.println(n); }); }

    /** Sendet das vollständige Logbuch aller aufgezeichneten Systemereignisse. */
    private void sendEvents() { var events = shop.gibEreignisListe(); out.println(events.size()); for (Ereignis e : events) out.println(e.toNetworkString()); }

    /** Sendet die taggenaue historische Bestandsentwicklung eines Artikels der letzten 30 Tage. */
    private void sendHistory() throws IOException { Map<LocalDate, Integer> m = shop.berechneBestandHistorie(readInt()); out.println(m.size()); m.forEach((date, n) -> out.println(date + ";" + n)); }

    /**
     * Führt eine I/O-Operation innerhalb einer kritischen Sektion aus.
     * Synchronisiert den Zugriff auf das zentrale {@link EShop}-Objekt, um Race Conditions
     * in einer Multithreading-Serverumgebung wirksam zu verhindern.
     *
     * @param action Die auszuführende, potenziell fehlerwerfende Operation.
     * @throws IOException Wenn die Operation eine I/O-Ausnahme auslöst.
     */
    private void locked(IoAction action) throws IOException {
        synchronized (shop) {
            action.run();
        }
    }

    /** Delegiert die Änderungsbenachrichtigung unter Angabe der eigenen Client-Kennung an den Dienst. */
    private void meldeAenderung(String bereich) { aktualisierungsDienst.meldeAenderung(clientKennung, bereich); }

    /** Bestimmt den eindeutigen Schlüssel für den Warenkorb (Benutzerkennung oder anonyme Sitzungs-ID). */
    private String warenkorbSchluessel() { return currentUser == null ? sitzungsWarenkorb : currentUser.getBenutzerErkennung(); }

    /** Liest eine Zeile aus dem Eingabestrom und wirft eine Ausnahme, falls der Stream unerwartet endet. */
    private String requiredLine() throws IOException { String s = in.readLine(); if (s == null) throw new EOFException("Verbindung während Anfrage beendet"); return s; }

    /** Liest eine Zeile ein und konvertiert diese direkt in einen Integer-Wert. */
    private int readInt() throws IOException { return integer(requiredLine()); }

    /** Splittet eine Textzeile anhand von Semikolons in eine feste Anzahl erwarteter Parameter-Teile auf. */
    private static String[] parts(String line, int count) { String[] p = line.split(";", count); if (p.length != count) throw new IllegalArgumentException("Falsche Parameteranzahl"); return p; }

    /** Konvertiert einen String performant in ein primitives {@code int}. */
    private static int integer(String value) { return Integer.parseInt(value); }

    /** Konvertiert einen String präzise in ein {@link BigDecimal}. */
    private static BigDecimal decimal(String value) { return new BigDecimal(value); }

    /**
     * Ein funktionales Interface zur Kapselung von I/O-sensiblen Aktionen innerhalb
     * von kritischen, synchronisierten Blöcken.
     */
    @FunctionalInterface private interface IoAction { void run() throws IOException; }
}
