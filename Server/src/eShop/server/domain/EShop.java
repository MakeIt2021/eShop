package eShop.server.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;


public class EShop {
    private final String datei = "eShop";

    private final ArtikelVW artikelVW;
    private final BenutzerVW benutzerVW;
    private final EreignisVW ereignisVW;
    private final WarenkorbVW warenkorbVW;

    public BenutzerVW getBenutzerVW() {
        return benutzerVW;
    }

    public EShop() throws DateiNichtGefundenException {
        artikelVW = new ArtikelVW();
        artikelVW.ladeArtikelMengeDaten(datei);
        warenkorbVW = new WarenkorbVW();
        benutzerVW = new BenutzerVW();
        ereignisVW = new EreignisVW();
        ereignisVW.ladeEreignisse("Ereignisse.txt", artikelID -> artikelVW.findeArtikel(Integer.parseInt(artikelID)));
    }

    public HashMap<Integer, Artikel> gibArtikelListe() {
        return artikelVW.gibArtikelListe();
    }

    public HashMap<Integer, Integer> gibArtikelMengeListe() {
        return artikelVW.gibArtikelMengeListe();
    }

    public HashMap<Integer, Integer> gibWarenkorb() {
        return warenkorbVW.gibWarenkorb();
    }


    public void fuegeArtikelEin(int artikelID, String bezeichnung, int menge, BigDecimal preis, String mitarbeiter) throws DateiNichtGefundenException {
        Artikel art = new Artikel(artikelID, bezeichnung.toLowerCase(), preis);

        if (artikelVW.findeArtikel(artikelID) == null) {
            // NEUER ARTIKEL → in die Liste einfügen
            artikelVW.einfuegen(art, menge);
        } else {
            // Artikel existiert → Bestand erhöhen
            artikelVW.bestandErhoehen(artikelID, menge);
        }
        if (preis.compareTo(BigDecimal.ZERO) < 0) {
            throw new UngueltigerPreisException(preis);
        }
        if (menge <= 0) {
            throw new UngueltigeMengeException(menge);
        }

        ereignisVW.addEreignis(art, menge, "Einlagerung", "m:" + mitarbeiter);

        speichereArtikel();
    }

    public void fuegeMassengutartikelEin(
            int artikelID,
            String bezeichnung,
            int menge,
            BigDecimal preis,
            String mitarbeiter,
            int packungGroesse
    ) throws UngueltigeMengeException, UngueltigerPreisException, MengeWenigerAlsPackungGroesseException, MassengutartikelmengeNichtTeilbarException, DateiNichtGefundenException {
        if (preis.compareTo(BigDecimal.ZERO) < 0) {
            throw new UngueltigerPreisException(preis);
        }

        if (menge <= 0) {
            throw new UngueltigeMengeException(menge);
        }

        if (menge < packungGroesse) {
            throw new MengeWenigerAlsPackungGroesseException(
                    bezeichnung,
                    menge,
                    packungGroesse
            );
        }

        if (menge % packungGroesse != 0) {
            throw new MassengutartikelmengeNichtTeilbarException(
                    bezeichnung,
                    packungGroesse
            );
        }


        Artikel art = new Massengutartikel(
                artikelID,
                bezeichnung,
                preis,
                packungGroesse
        );

        artikelVW.einfuegen(art, menge);
        ereignisVW.addEreignis(art, menge, "Einlagerung", "m:" + mitarbeiter);

        speichereArtikel();
    }

    public void artikelVernichten(int artikelID) {
        artikelVW.artikelVernichten(artikelID);
        speichereArtikel();
    }

    public void bezeichnungVeraendern(int artikelID, String bezeichnung) throws DateiNichtGefundenException {
        artikelVW.bezeichnungVeraendern(artikelID, bezeichnung);
        speichereArtikel();
    }

    public void preisVeraendern(int artikelID, BigDecimal preis) throws DateiNichtGefundenException {

        if (preis.compareTo(BigDecimal.ZERO) < 0) {
            throw new UngueltigerPreisException(preis);
        }

        artikelVW.preisVeraendern(artikelID, preis);
        speichereArtikel();
    }

    public void fuegeInWarenkorb(int artikelID, int menge, String kunde) throws UngueltigeMengeException, BestandNichtAusreichendException, MengeWenigerAlsPackungGroesseException, MassengutartikelmengeNichtTeilbarException, DateiNichtGefundenException {

        if (menge <= 0) {
            throw new UngueltigeMengeException(menge);
        }

        if (artikelVW.getBestand(artikelID) < menge) {
            throw new BestandNichtAusreichendException(
                    getArtikelName(artikelID),
                    artikelVW.getBestand(artikelID),
                    menge
            );
        }

        if (istMassengutartikel(artikelID)) {
            if (menge < getPackungGroesse(artikelID)) {
                throw new MengeWenigerAlsPackungGroesseException(
                        getArtikelName(artikelID),
                        menge,
                        getPackungGroesse(artikelID)
                );
            }

            if (menge % getPackungGroesse(artikelID) != 0) {
                throw new MassengutartikelmengeNichtTeilbarException(
                        getArtikelName(artikelID),
                        getPackungGroesse(artikelID)
                );
            }
        }
        warenkorbVW.einfuegen(artikelID, menge);
        artikelVW.bestandVerringern(artikelID, menge);
        ereignisVW.addEreignis(artikelVW.findeArtikel(artikelID), menge, "Auslagerung", "k:" + kunde);

        speichereArtikel();
    }

    public void loescheAusWarenkorb(int artikelID, int menge, String kunde) throws UngueltigeMengeException, MengeWenigerAlsPackungGroesseException, MassengutartikelmengeNichtTeilbarException, DateiNichtGefundenException {

        if (menge <= 0) {
            throw new UngueltigeMengeException(menge);
        }

        if (istMassengutartikel(artikelID)) {
            if (menge < getPackungGroesse(artikelID)) {
                throw new MengeWenigerAlsPackungGroesseException(getArtikelName(artikelID), menge, getPackungGroesse(artikelID));
            }

            if (menge % getPackungGroesse(artikelID) != 0) {
                throw new MassengutartikelmengeNichtTeilbarException(getArtikelName(artikelID), getPackungGroesse(artikelID));
            }
        }

        Artikel einArtikel = artikelVW.gibArtikelListe().get(artikelID);
        warenkorbVW.loeschen(artikelID, menge);
        artikelVW.einfuegen(einArtikel, menge);


        speichereArtikel();
    }

    public void zuruecksetzeWarenkorb() {
        warenkorbVW.zuruecksetzen();
    }

    public void speichereArtikel() throws DateiNichtGefundenException {
        artikelVW.speichereArtikelMengeDaten(datei+"_AM.txt");
        artikelVW.speichereArtikelDaten(datei+"_A.txt");
    }

    public int sucheNachID(String bezeichnung) {

        int artikelID = artikelVW.sucheNachIDMitBezeichnung(bezeichnung);

        if (artikelID == -1) {
            throw new ArtikelExistiertNichtException(bezeichnung);
        }
        return artikelID;
    }

    public void bestandVeraendern(
            int artikelID,
            int neuerBestand,
            String mitarbeiter
    ) throws MengeWenigerAlsPackungGroesseException, MassengutartikelmengeNichtTeilbarException, DateiNichtGefundenException {
        if (neuerBestand <= 0) {
            throw new UngueltigeMengeException(neuerBestand);
        }

        if (istMassengutartikel(artikelID)) {
            if (neuerBestand < getPackungGroesse(artikelID)) {
                throw new MengeWenigerAlsPackungGroesseException(
                        getArtikelName(artikelID),
                        neuerBestand,
                        getPackungGroesse(artikelID)
                );
            }

            if (neuerBestand % getPackungGroesse(artikelID) != 0) {
                throw new MassengutartikelmengeNichtTeilbarException(
                        getArtikelName(artikelID),
                        getPackungGroesse(artikelID)
                );
            }
        }

        int aktuellerBestand = artikelVW.getBestand(artikelID);

        Artikel a = artikelVW.findeArtikel(artikelID);

        if (aktuellerBestand < neuerBestand) {

            artikelVW.bestandErhoehen(
                    artikelID,
                    neuerBestand - aktuellerBestand
            );

            ereignisVW.addEreignis(a, neuerBestand - aktuellerBestand, "Einlagerung", "m:" + mitarbeiter);
        } else {
            artikelVW.bestandVerringern(
                    artikelID,
                    aktuellerBestand - neuerBestand
            );

            ereignisVW.addEreignis(a, aktuellerBestand - neuerBestand, "Auslagerung", "m:" + mitarbeiter);
        }

        speichereArtikel();
    }

    public boolean istMassengutartikel(int artikelID) {
        return artikelVW.istMassengutartikel(artikelID);
    }

    public int getPackungGroesse(int artikelID) {
        if (istMassengutartikel(artikelID))
            return artikelVW.getPackungGroesse(artikelID);
        else
            return 1;
    }

    public void packungGroesseVeraendern(int artikelID, int neueGroesse) {
        artikelVW.packungGroesseVeraendern(artikelID, neueGroesse);
        if (getBestand(artikelID) % neueGroesse != 0) {
            throw new MassengutartikelmengeNichtTeilbarException(getArtikelName(artikelID), neueGroesse);
        }
    }

    public int getBestand(int artikelID) {
        return artikelVW.getBestand(artikelID);
    }

    public String getArtikelName(int artikelID) {
        return artikelVW.getArtikelName(artikelID);
    }

    public boolean login (String benutzerErkennung, String benutzerPasswort) {
        return benutzerVW.login(benutzerErkennung, benutzerPasswort);

    }

    public void logout () {
        benutzerVW.logout();
    }

    public boolean istEingeloggt(){
        return benutzerVW.istEingeloggt();
    }

    public boolean istMitarbeiter(){
        return benutzerVW.istMitarbeiter();
    }

    public boolean istKunde(){
        return benutzerVW.istKunde();
    }

    public boolean registrieren (Benutzer benutzer){
        return benutzerVW.registrieren(benutzer);
    }

    public Benutzer aktuellerBenutzer () {
        return benutzerVW.getAktuellerBenutzer();
    }

    public void pruefeArtikelExistiertBereits(String bezeichnung) throws ArtikelExistiertBereitsException {
        int artikelID = artikelVW.sucheNachIDMitBezeichnung(bezeichnung);

        if (artikelID != -1) {
            Artikel artikel = artikelVW.findeArtikel(artikelID);
            throw new ArtikelExistiertBereitsException(artikel, "");
        }
    }

    public ArrayList<Ereignis> gibEreignisListe() {
        return ereignisVW.gibEreignisListe();
    }

    public Map<LocalDate, Integer> berechneBestandHistorie(int artikelID) {
        return ereignisVW.gibBestandHistorie(artikelID);
    }

    public Rechnung gibAlleGekaufteArtikel() {
        return new Rechnung(aktuellerBenutzer().getBenutzerVorNachname(), gibWarenkorb(), gibArtikelListe());
    }

    public ArrayList<Integer> gibBestandHistorie(int artikelID) {
        return ereignisVW.gibBestandHistorieAlsIntegers(artikelID);
    }

    public Artikel findeArtikel(int artikelID) {
        return artikelVW.findeArtikel(artikelID);
    }

    public BigDecimal gibPreis(int artikelID) {
        return artikelVW.gibPreis(artikelID);
    }
}
