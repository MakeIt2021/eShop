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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.Socket;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class EShopFassade implements EShopInterface {
    private Socket socket = null;
    private BufferedReader sin;
    private PrintStream sout;

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
    }




    @Override
    public void disconnect() throws IOException {
        // Kennzeichen für gewählte Aktion senden
        sout.println("q");
        // (Parameter sind hier nicht zu senden)

        // Antwort vom Server lesen:
        String antwort = "Fehler";
        try {
            antwort = sin.readLine();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return;
        }
        System.out.println(antwort);
    }

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
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

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

    @Override
    public void artikelVernichten(int artikelID) {
        sout.println("ARTIKEL_VERNICHTEN");

        try {
            String antwort = sin.readLine();

            if (antwort.equals("ARTIKEL_VERNICHTEN: OK"))
                return;

            System.err.println("Unerwartete Antwort vom Server beim Artikel Vernichten:" + antwort);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

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

            System.err.println("Unerwartete Antwort vom Server beim Veraendern von der Bezeichnung:" + antwort);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

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

    @Override
    public int gibBestand(int artikelID) {
        sout.println("GIB_BESTAND");

        try {
            String antwort = sin.readLine();

            return Integer.parseInt(antwort);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return -1;
        }
    }

    @Override
    public String gibArtikelName(int artikelID) {
        sout.println("GIB_ARTIKEL_NAME");

        try {
            String antwort = sin.readLine();

            return antwort;
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    @Override
    public Artikel findeArtikel(int artikelID) {
        sout.println("FINDE_ARTIKEL");

        try {
            String antwort = sin.readLine();

            return Artikel.fromNetworkString(antwort);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    @Override
    public BigDecimal gibPreis(int artikelID) {
        sout.println("GIB_PREIS");

        try {
            String antwort = sin.readLine();

            return new BigDecimal(antwort).setScale(2, RoundingMode.HALF_EVEN);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

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

    @Override
    public Map<LocalDate, Integer> berechneBestandHistorie(int artikelID) {
        sout.println("BERECHNE_BESTANDHISTORIE");

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
