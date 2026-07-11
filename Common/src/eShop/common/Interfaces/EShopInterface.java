package eShop.common.interfaces;

import eShop.common.entities.*;
import eShop.common.exceptions.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

// Hinweis: Diese Klasse wurde zur Zeitersparnis anhand EShop.java von einer KI erstellt.

public interface EShopInterface {
    boolean login(String benutzerErkennung, String benutzerPasswort);
    void logout();
    boolean istEingeloggt();
    boolean istMitarbeiter();
    boolean istKunde();
    Benutzer aktuellerBenutzer();
    boolean registrieren(Benutzer benutzer);
    int generiereId();

    HashMap<Integer, Artikel> gibArtikelListe();
    HashMap<Integer, Integer> gibArtikelMengeListe();
    void fuegeArtikelEin(int artikelID, String bezeichnung, int menge, BigDecimal preis, String mitarbeiter) throws DateiNichtGefundenException, UngueltigerPreisException, UngueltigeMengeException;
    void fuegeMassengutartikelEin(int artikelID, String bezeichnung, int menge, BigDecimal preis, String mitarbeiter, int packungGroesse) throws UngueltigeMengeException, UngueltigerPreisException, MengeWenigerAlsPackungGroesseException, MassengutartikelmengeNichtTeilbarException, DateiNichtGefundenException;
    void artikelVernichten(int artikelID);
    void bezeichnungVeraendern(int artikelID, String bezeichnung) throws DateiNichtGefundenException;
    void preisVeraendern(int artikelID, BigDecimal preis) throws DateiNichtGefundenException, UngueltigerPreisException;
    int sucheNachID(String bezeichnung) throws ArtikelExistiertNichtException;
    void bestandVeraendern(int artikelID, int neuerBestand, String mitarbeiter) throws MengeWenigerAlsPackungGroesseException, MassengutartikelmengeNichtTeilbarException, DateiNichtGefundenException, UngueltigeMengeException;
    boolean istMassengutartikel(int artikelID);
    int gibPackungGroesse(int artikelID);
    void packungGroesseVeraendern(int artikelID, int neueGroesse) throws MassengutartikelmengeNichtTeilbarException;
    int gibBestand(int artikelID);
    String gibArtikelName(int artikelID);
    Artikel findeArtikel(int artikelID);
    BigDecimal gibPreis(int artikelID);

    HashMap<Integer, Integer> gibWarenkorb();
    void fuegeInWarenkorb(int artikelID, int menge, String kunde) throws UngueltigeMengeException, BestandNichtAusreichendException, MengeWenigerAlsPackungGroesseException, MassengutartikelmengeNichtTeilbarException, DateiNichtGefundenException;
    void loescheAusWarenkorb(int artikelID, int menge, String kunde) throws UngueltigeMengeException, MengeWenigerAlsPackungGroesseException, MassengutartikelmengeNichtTeilbarException, DateiNichtGefundenException;
    void zuruecksetzeWarenkorb();

    ArrayList<Ereignis> gibEreignisListe();
    Map<LocalDate, Integer> berechneBestandHistorie(int artikelID);

    void disconnect() throws IOException;
}