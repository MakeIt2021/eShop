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

/** Processes the line-based eShop protocol for one connected client. */
public final class ClientRequestProcessor implements Runnable {
    private final Socket socket;
    private final EShop shop;
    private final AktualisierungsDienst aktualisierungsDienst;
    private BufferedReader in;
    private PrintWriter out;
    private Benutzer currentUser;
    private final String sitzungsWarenkorb = "sitzung-" + UUID.randomUUID();
    private String clientKennung;

    public ClientRequestProcessor(Socket socket, EShop shop) {
        this(socket, shop, new AktualisierungsDienst());
    }

    public ClientRequestProcessor(Socket socket, EShop shop, AktualisierungsDienst aktualisierungsDienst) {
        this.socket = socket;
        this.shop = shop;
        this.aktualisierungsDienst = aktualisierungsDienst;
        this.clientKennung = "verbindung-" + UUID.randomUUID();
    }

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

    private void abonniereAktualisierungen() throws IOException {
        clientKennung = requiredLine();
        out.println("ABONNIERT");
        aktualisierungsDienst.anmelden(clientKennung, out);
    }

    private void registrieren() throws IOException {
        Benutzer benutzer = Benutzer.fromNetworkString(requiredLine());
        locked(() -> {
            boolean erfolgreich = shop.registrieren(benutzer);
            out.println(erfolgreich);
            if (erfolgreich) meldeAenderung("BENUTZER");
        });
    }

    private void login() throws IOException {
        String name = requiredLine();
        String password = requiredLine();
        locked(() -> {
            if (shop.login(name, password)) currentUser = shop.aktuellerBenutzer();
            shop.logout();
            out.println(currentUser != null);
        });
    }

    private void addArticle(boolean bulk) throws IOException {
        String[] p = parts(requiredLine(), bulk ? 6 : 5);
        locked(() -> {
            try {
                if (bulk) shop.fuegeMassengutartikelEin(integer(p[0]), p[1], integer(p[2]), decimal(p[3]), p[4], integer(p[5]));
                else shop.fuegeArtikelEin(integer(p[0]), p[1], integer(p[2]), decimal(p[3]), p[4]);
                out.println(bulk ? "FUEGE_MASSENGUTARTIKEL_EIN: OK" : "FUEGE_ARTIKEL_EIN: OK");
                meldeAenderung("ARTIKEL");
            } catch (UngueltigerPreisException e) { out.println(prefix(bulk) + "ERR_PREIS");
            } catch (UngueltigeMengeException e) { out.println(prefix(bulk) + "ERR_MENGE");
            } catch (MengeWenigerAlsPackungGroesseException e) { out.println(prefix(bulk) + "ERR_MENGE_WENIGER");
            } catch (MassengutartikelmengeNichtTeilbarException e) { out.println(prefix(bulk) + "ERR_MENGE_NICHT_TEILBAR");
            } catch (DateiNichtGefundenException e) { out.println(prefix(bulk) + "ERR_DATEI");
            } catch (ArtikelExistiertBereitsException e) { out.println(prefix(bulk) + "ERR_ARTIKEL"); }
        });
    }

    private String prefix(boolean bulk) { return bulk ? "FUEGE_MASSENGUTARTIKEL_EIN: " : "FUEGE_ARTIKEL_EIN: "; }

    private void changeDescription() throws IOException {
        String[] p = parts(requiredLine(), 2);
        locked(() -> { try { shop.bezeichnungVeraendern(integer(p[0]), p[1]); out.println("BEZEICHNUNG_VERAENDERN: OK"); meldeAenderung("ARTIKEL"); }
        catch (ArtikelExistiertBereitsException e) { out.println("BEZEICHNUNG_VERAENDERN: ERR_ARTIKEL"); }
        catch (DateiNichtGefundenException e) { out.println("BEZEICHNUNG_VERAENDERN: ERR_DATEI"); }
        });
    }

    private void changePrice() throws IOException {
        String[] p = parts(requiredLine(), 2);
        locked(() -> { try { shop.preisVeraendern(integer(p[0]), decimal(p[1])); out.println("PREIS_VERAENDERN: OK"); meldeAenderung("ARTIKEL"); }
            catch (UngueltigerPreisException e) { out.println("PREIS_VERAENDERN: ERR_PREIS"); }
            catch (DateiNichtGefundenException e) { out.println("PREIS_VERAENDERN: ERR_DATEI"); } });
    }

    private void searchId() throws IOException {
        String name = requiredLine();
        locked(() -> { try { out.println(shop.sucheNachID(name)); } catch (ArtikelExistiertNichtException e) { out.println("SUCHE_NACH_ID: ERR_ARTIKEL"); } });
    }

    private void changeStock() throws IOException {
        String[] p = parts(requiredLine(), 3);
        locked(() -> { try { shop.bestandVeraendern(integer(p[0]), integer(p[1]), p[2]); out.println("BESTAND_VERAENDERN: OK"); meldeAenderung("BESTAND"); }
            catch (UngueltigeMengeException e) { out.println("BESTAND_VERAENDERN: ERR_MENGE"); }
            catch (MengeWenigerAlsPackungGroesseException e) { out.println("BESTAND_VERAENDERN: ERR_MENGE_WENIGER"); }
            catch (MassengutartikelmengeNichtTeilbarException e) { out.println("BESTAND_VERAENDERN: ERR_MENGE_NICHT_TEILBAR"); }
            catch (DateiNichtGefundenException e) { out.println("BESTAND_VERAENDERN: ERR_DATEI"); } });
    }

    private void changePackageSize() throws IOException {
        String[] p = parts(requiredLine(), 2);
        locked(() -> { try { shop.packungGroesseVeraendern(integer(p[0]), integer(p[1])); out.println("PACKUNGSGROESSE_VERAENDERN: OK"); meldeAenderung("ARTIKEL"); }
            catch (MassengutartikelmengeNichtTeilbarException e) { out.println("PACKUNGSGROESSE_VERAENDERN: ERR_MENGE_NICHT_TEILBAR"); } });
    }

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

    private void sendArticles() { Map<Integer, Artikel> m = shop.gibArtikelListe(); out.println(m.size()); m.values().forEach(a -> out.println(a.toNetworkString())); }
    private void sendQuantities() { Map<Integer, Integer> m = shop.gibArtikelMengeListe(); out.println(m.size()); m.forEach((id, n) -> { out.println(id); out.println(n); }); }
    private void sendCart() { Map<Integer, Integer> m = shop.gibWarenkorb(warenkorbSchluessel()); out.println(m.size()); m.forEach((id, n) -> { out.println(id); out.println(n); }); }
    private void sendEvents() { var events = shop.gibEreignisListe(); out.println(events.size()); for (Ereignis e : events) out.println(e.toNetworkString()); }
    private void sendHistory() throws IOException { Map<LocalDate, Integer> m = shop.berechneBestandHistorie(readInt()); out.println(m.size()); m.forEach((date, n) -> out.println(date + ";" + n)); }

    private void locked(IoAction action) throws IOException { synchronized (shop) { action.run(); } }
    private void meldeAenderung(String bereich) { aktualisierungsDienst.meldeAenderung(clientKennung, bereich); }
    private String warenkorbSchluessel() { return currentUser == null ? sitzungsWarenkorb : currentUser.getBenutzerErkennung(); }
    private String requiredLine() throws IOException { String s = in.readLine(); if (s == null) throw new EOFException("Verbindung während Anfrage beendet"); return s; }
    private int readInt() throws IOException { return integer(requiredLine()); }
    private static String[] parts(String line, int count) { String[] p = line.split(";", count); if (p.length != count) throw new IllegalArgumentException("Falsche Parameteranzahl"); return p; }
    private static int integer(String value) { return Integer.parseInt(value); }
    private static BigDecimal decimal(String value) { return new BigDecimal(value); }

    @FunctionalInterface private interface IoAction { void run() throws IOException; }
}
