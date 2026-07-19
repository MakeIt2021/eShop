package eShop.client.ui.cui;

import eShop.client.net.EShopFassade;
import eShop.common.entities.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;

import eShop.common.exceptions.*;
import eShop.common.interfaces.EShopInterface;

/**
 * Textbasierte Benutzeroberfläche für den EShop-Client.
 *
 * Diese Klasse steuert den gesamten Interaktionszyklus mit dem Benutzer über die Konsole.
 * Sie unterscheidet dynamisch zwischen drei Zuständen: nicht eingeloggter Besucher,
 * Kunde und Mitarbeiter, und stellt jeweils ein passendes Menü sowie die
 * entsprechende Eingabeverarbeitung bereit. Die Kommunikation mit dem Server
 * erfolgt gekapselt über das {@link EShopInterface}.
 */
public class EShopClientCUI {
    /** Die Schnittstelle zum EShop-Kern bzw. der Netzwerk-Fassade. */
    private EShopInterface eShop;

    /** Der Stream-Reader für die Konsoleneingaben des Benutzers. */
    private BufferedReader in;

    // ANSI-Escape-Codes für farbige Konsolenausgaben
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";

    /** Der Standard-Netzwerkport für die Serververbindung. */
    public static final int DEFAULT_PORT = 6789;

    /**
     * Erstellt eine neue Instanz der CUI und initialisiert die Verbindung
     * zum EShop-Server via {@link EShopFassade} auf dem lokalen Host.
     *
     * @throws IOException wenn der Verbindungsaufbau zum Server fehlschlägt
     *                     oder die Stream-Initialisierung fehlschlägt
     */
    public EShopClientCUI() throws IOException {
        eShop = new EShopFassade("localhost", DEFAULT_PORT);
        in = new BufferedReader(new InputStreamReader(System.in));
    }

    /**
     * Gibt das kontextsensitive Menü auf der Konsole aus, basierend auf dem
     * aktuellen Authentifizierungsstatus des Benutzers (Besucher, Kunde oder Mitarbeiter).
     */
    private void gibMenueAus() {
        if (eShop.istKunde()) {
            System.out.println("\n===== KUNDENMENÜ =====");
            System.out.println("a  → Artikel ansehen");
            System.out.println("we → Artikel in Warenkorb");
            System.out.println("wl → Artikel aus Warenkorb löschen");
            System.out.println("w  → Warenkorb ansehen");
            System.out.println("ak → Kaufen");
            System.out.println("o  → Logout");
        } else if (eShop.istMitarbeiter()) {
            System.out.println("\n===== MITARBEITERMENÜ =====");
            System.out.println("a  → Artikel ansehen");
            System.out.println("ae → Neuer Artikel in Katalog hinzufügen");
            System.out.println("bv → Bezeichnung ändern");
            System.out.println("al → Artikel aus dem Katalog komplett löschen");
            System.out.println("bsv → Bestand ändern");
            System.out.println("pv → Preis ändern");
            System.out.println("e  → Ereignisse anzeigen");
            System.out.println("bh → Bestandshistorie anzeigen");
            System.out.println("rm → Neuen Mitarbeiter registrieren");
            System.out.println("s  → Daten speichern");
            System.out.println("o  → Logout");
        } else { // Person nicht eingeloggt
            System.out.println("\n===== HAUPTMENUE =====");
            System.out.println("r → Registrieren");
            System.out.println("l → Login");
            System.out.println("q → Beenden");
        }
        System.out.print("> ");
    }

    /**
     * Liest die nächste Eingabezeile von der Konsole ein.
     *
     * @return die vom Benutzer eingegebene Zeichenkette (String)
     * @throws IOException wenn ein Fehler beim Lesen des Eingabestreams auftritt
     */
    private String liesEingabe() throws IOException {
        return in.readLine();
    }

    /**
     * Verarbeitet das vom Benutzer eingegebene Kommando und führt die entsprechende
     * Geschäftslogik über die EShop-Schnittstelle aus.
     *
     * Diese Methode fängt fehlerhafte Eingaben (z.B. falsche Zahlenformate) ab und
     * reagiert auf domänenspezifische Exceptions, um Fehlermeldungen benutzerfreundlich
     * auf der Konsole auszugeben.
     *
     * @param line das eingegebene Menü-Kommando (z.B. "a", "we", "l")
     * @throws IOException wenn bei interaktiven Unterabfragen ein E/A-Fehler auftritt
     */
    private void verarbeiteEingabe(String line) throws IOException {
        int artikelID;
        int menge;
        String bezeichnung;
        BigDecimal preis;
        int bestand = 0;
        Benutzer aktuelleBenutzer;
        int packungGroesse = 1;

        HashMap<Integer, Artikel> artikelListe = null;
        HashMap<Integer, Integer> warenkorbListe;

        switch (line) {
            case "a" -> {
                if (!eShop.istEingeloggt()) {
                    System.out.println("Bitte zuerst einloggen.");
                    break;
                }
                artikelListe = eShop.gibArtikelListe();
                gibArtikellisteAus(artikelListe, eShop.gibArtikelMengeListe());
            }

            case "ae" -> {
                if (!eShop.istEingeloggt()) {
                    System.out.println("Bitte zuerst einloggen.");
                    break;
                }
                if (!eShop.istMitarbeiter()) {
                    System.out.println("Nur Mitarbeiter dürfen Artikel hinzufügen.");
                    break;
                }

                System.out.println("Einzelartikel oder Massengutartikel? [e/m]");
                String artikelTyp = liesEingabe();

                if (!artikelTyp.equals("e") && !artikelTyp.equals("m")) {
                    System.out.println(RED + "Ungultige Eingabbe. Bitte e oder m eingeben" + RESET);
                    break;
                }

                ArrayList<Integer> vorhandeneIDs = new ArrayList<>(eShop.gibArtikelListe().keySet());
                Collections.sort(vorhandeneIDs);

                artikelID = eShop.generiereArtikelID();

                aktuelleBenutzer = eShop.aktuellerBenutzer();

                System.out.print("Bezeichnung > ");
                bezeichnung = liesEingabe();

                System.out.print("Preis > ");
                try {
                    preis = new BigDecimal(liesEingabe());
                } catch (NumberFormatException e) {
                    System.out.println(RED + "Ungültige Eingabe. Bitte geben Sie eine Zahl ein." + RESET);
                    break;
                }

                if (artikelTyp.equals("e")) {
                    System.out.print("Bestand > ");
                    try {
                        bestand = Integer.parseInt(liesEingabe());
                    } catch (NumberFormatException e) {
                        System.out.println(RED + "Ungültige Eingabe. Bitte geben Sie eine ganze Zahl ein." + RESET);
                        break;
                    }
                } else if (artikelTyp.equals("m")) {
                    System.out.print("Größe der Packung > ");
                    try {
                        packungGroesse = Integer.parseInt(liesEingabe());
                    } catch (NumberFormatException e) {
                        System.out.println(RED + "Ungültige Eingabe. Bitte geben Sie eine ganze Zahl ein." + RESET);
                        break;
                    }
                    System.out.println("Bestand");
                    System.out.println(YELLOW + "Der Bestand muss durch " + packungGroesse + " teilbar sein" + RESET);
                    System.out.print("> ");
                    try {
                        bestand = Integer.parseInt(liesEingabe());
                    } catch (NumberFormatException e) {
                        System.out.println(RED + "Ungültige Eingabe. Bitte geben Sie eine ganze Zahl ein." + RESET);
                        break;
                    }
                }

                if (artikelTyp.equals("e")) {
                    try {
                        eShop.fuegeArtikelEin(
                                artikelID,
                                bezeichnung,
                                bestand,
                                preis,
                                aktuelleBenutzer.getBenutzerVorNachname()
                        );
                        System.out.println(GREEN + "✔ Artikel erfolgreich hinzugefügt." + RESET);
                    } catch (UngueltigerPreisException | UngueltigeMengeException | ArtikelExistiertBereitsException e) {
                        System.out.println(RED + e.getMessage() + RESET);
                    }
                } else if (artikelTyp.equals("m")) {
                    try {
                        eShop.fuegeMassengutartikelEin(
                                artikelID,
                                bezeichnung,
                                bestand,
                                preis,
                                aktuelleBenutzer.getBenutzerVorNachname(),
                                packungGroesse
                        );
                        System.out.println(GREEN + "✔ Artikel erfolgreich hinzugefügt." + RESET);
                    } catch (UngueltigerPreisException | UngueltigeMengeException |
                             MengeWenigerAlsPackungGroesseException | MassengutartikelmengeNichtTeilbarException e) {
                        System.out.println(RED + e.getMessage() + RESET);
                    }
                }
            }

            case "bsv" -> {
                if (!eShop.istEingeloggt()) {
                    System.out.println("Bitte zuerst einloggen.");
                    break;
                }
                if (!eShop.istMitarbeiter()) {
                    System.out.println("Nur Mitarbeiter dürfen Artikel löschen.");
                    break;
                }

                System.out.print("Bezeichnung > ");
                try {
                    artikelID = eShop.sucheNachID(liesEingabe());
                } catch (ArtikelExistiertNichtException e) {
                    System.out.println(RED + e.getMessage() + RESET);
                    break;
                }

                aktuelleBenutzer = eShop.aktuellerBenutzer();

                if (!eShop.istMassengutartikel(artikelID)) {
                    System.out.print("Neuer Bestand > ");
                    try {
                        menge = Integer.parseInt(liesEingabe());
                    } catch (NumberFormatException e) {
                        System.out.println(RED + "Ungültige Eingabe. Bitte geben Sie eine gültige ganze Zahl ein." + RESET);
                        break;
                    }

                    try {
                        eShop.bestandVeraendern(
                                artikelID,
                                menge,
                                aktuelleBenutzer.getBenutzerVorNachname()
                        );
                        System.out.println(YELLOW + "✔ Artikelbestand erfolgreich verändert." + RESET);
                    } catch (UngueltigeMengeException e) {
                        System.out.println(RED + e.getMessage() + RESET);
                    }
                } else {
                    System.out.println("Die Größe der Packung ist bereits " + eShop.gibPackungGroesse(artikelID));
                    System.out.println("Möchten Sie die Größe der Packung verändern oder die gesamte Menge? [g / m]");
                    String operation = liesEingabe();

                    if (operation.equals("g")) {
                        System.out.println("Geben Sie bitte die neue Größe der Packung > ");
                        int neueGroesse;
                        try {
                            neueGroesse = Integer.parseInt(liesEingabe());
                        } catch (NumberFormatException e) {
                            System.out.println(RED + "Ungültige Eingabe. Bitte geben Sie eine ganze Zahl ein." + RESET);
                            break;
                        }
                        eShop.packungGroesseVeraendern(artikelID, neueGroesse);
                    } else if (operation.equals("m")) {
                        System.out.println("Geben Sie bitte die neue Menge der Artikel ein.");
                        System.out.println(YELLOW + "Die neue Menge muss durch " + eShop.gibPackungGroesse(artikelID) + " teilbar sein" + RESET);

                        try {
                            int neueMenge = Integer.parseInt(liesEingabe());
                            eShop.bestandVeraendern(
                                    artikelID,
                                    neueMenge,
                                    aktuelleBenutzer.getBenutzerVorNachname()
                            );
                            System.out.println(YELLOW + "✔ Artikelbestand erfolgreich verändert." + RESET);
                        } catch (NumberFormatException e) {
                            System.out.println(RED + "Ungültige Eingabe. Bitte geben Sie eine ganze Zahl ein." + RESET);
                        } catch (UngueltigeMengeException | MengeWenigerAlsPackungGroesseException | MassengutartikelmengeNichtTeilbarException e) {
                            System.out.println(RED + e.getMessage() + RESET);
                        }
                    }
                }
            }

            case "we" -> {
                if (!eShop.istEingeloggt()) {
                    System.out.println("Bitte zuerst einloggen.");
                    break;
                }
                if (!eShop.istKunde()) {
                    System.out.println("Nur Kunden dürfen Artikel kaufen.");
                    break;
                }

                System.out.print("Bezeichnung > ");
                try {
                    artikelID = eShop.sucheNachID(liesEingabe());
                } catch (ArtikelExistiertNichtException | ArtikelExistiertBereitsException e) {
                    System.out.println(RED + e.getMessage() + RESET);
                    break;
                }

                if (!eShop.istMassengutartikel(artikelID)) {
                    System.out.print("Menge der Artikel > ");
                    try {
                        menge = Integer.parseInt(liesEingabe());
                    } catch (NumberFormatException e) {
                        System.out.println(RED + "Ungültige Eingabe. Bitte geben Sie eine ganze Zahl ein." + RESET);
                        break;
                    }

                    aktuelleBenutzer = eShop.aktuellerBenutzer();
                    try {
                        eShop.fuegeInWarenkorb(
                                artikelID,
                                menge,
                                aktuelleBenutzer.getBenutzerVorNachname()
                        );
                        System.out.println(GREEN + "✔ Artikel wurde zum Warenkorb hinzugefügt." + RESET);
                    } catch (BestandNichtAusreichendException | UngueltigeMengeException | ArtikelExistiertBereitsException e) {
                        System.out.println(RED + e.getMessage() + RESET);
                    }
                } else {
                    System.out.println(YELLOW + eShop.gibArtikelName(artikelID) + " ist ein Massengutartikel!");
                    System.out.println("Das bedeutet, dass die Menge im Warenkorb durch " + eShop.gibPackungGroesse(artikelID) + " teilbar sein soll!" + RESET);
                    System.out.print("> ");

                    try {
                        menge = Integer.parseInt(liesEingabe());
                        aktuelleBenutzer = eShop.aktuellerBenutzer();
                        eShop.fuegeInWarenkorb(
                                artikelID,
                                menge,
                                aktuelleBenutzer.getBenutzerVorNachname()
                        );
                        System.out.println(GREEN + "✔ Artikel wurde zum Warenkorb hinzugefügt." + RESET);
                    } catch (NumberFormatException e) {
                        System.out.println(RED + "Ungültige Eingabe. Bitte geben Sie eine ganze Zahl ein." + RESET);
                    } catch (BestandNichtAusreichendException | UngueltigeMengeException | MassengutartikelmengeNichtTeilbarException e) {
                        System.out.println(RED + e.getMessage() + RESET);
                    }
                }
            }

            case "wl" -> {
                if (!eShop.istEingeloggt()) {
                    System.out.println("Bitte zuerst einloggen.");
                    break;
                }
                if (!eShop.istKunde()) {
                    System.out.println("Nur Kunden dürfen den Warenkorb ändern.");
                    break;
                }

                System.out.print("Bezeichnung > ");
                try {
                    artikelID = eShop.sucheNachID(liesEingabe());
                } catch (ArtikelExistiertNichtException e) {
                    System.out.println(RED + e.getMessage() + RESET);
                    break;
                }

                System.out.print("Menge der Artikel > ");
                try {
                    menge = Integer.parseInt(liesEingabe());
                    aktuelleBenutzer = eShop.aktuellerBenutzer();
                    eShop.loescheAusWarenkorb(artikelID, menge, aktuelleBenutzer.getBenutzerVorNachname());
                    System.out.println(YELLOW + "✔ Artikel wurde aus dem Warenkorb entfernt." + RESET);
                } catch (NumberFormatException e) {
                    System.out.println(RED + "Ungültige Eingabe. Bitte geben Sie eine ganze Zahl ein." + RESET);
                } catch (MengeWenigerAlsPackungGroesseException | UngueltigeMengeException | MassengutartikelmengeNichtTeilbarException e) {
                    System.out.println(RED + e.getMessage() + RESET);
                }
            }

            case "w" -> {
                if (!eShop.istEingeloggt()) {
                    System.out.println("Bitte zuerst einloggen.");
                    break;
                }
                if (!eShop.istKunde()) {
                    System.out.println("Nur Kunden haben einen Warenkorb.");
                    break;
                }
                System.out.println("Warenkorb:");
                warenkorbListe = eShop.gibWarenkorb();
                gibWarenkorbAus(warenkorbListe, eShop.gibArtikelListe());
            }

            case "ak" -> {
                if (!eShop.istEingeloggt()) {
                    System.out.println("Bitte zuerst einloggen.");
                    break;
                }
                if (!eShop.istKunde()) {
                    System.out.println("Nur Kunden dürfen Artikel kaufen.");
                    break;
                }
                warenkorbListe = eShop.gibWarenkorb();
                aktuelleBenutzer = eShop.aktuellerBenutzer();

                Rechnung rechnung = new Rechnung(aktuelleBenutzer.getBenutzerVorNachname(), warenkorbListe, eShop.gibArtikelListe());
                gibRechnungAus(rechnung);

                eShop.zuruecksetzeWarenkorb();
                System.out.println(GREEN + "✔ Einkauf erfolgreich abgeschlossen." + RESET);
            }

            case "bv" -> {
                if (!eShop.istEingeloggt()) {
                    System.out.println("Bitte zuerst einloggen.");
                    break;
                }
                if (!eShop.istMitarbeiter()) {
                    System.out.println("Nur Mitarbeiter dürfen Artikel bearbeiten.");
                    break;
                }
                System.out.print("Bezeichnung > ");
                try {
                    artikelID = eShop.sucheNachID(liesEingabe());
                } catch (ArtikelExistiertNichtException e) {
                    System.out.println(RED + e.getMessage() + RESET);
                    break;
                }

                System.out.print("Neue Bezeichnung > ");
                bezeichnung = liesEingabe();
                eShop.bezeichnungVeraendern(artikelID, bezeichnung);
                System.out.println(YELLOW + "✔ Bezeichnung erfolgreich geändert." + RESET);
            }

            case "pv" -> {
                if (!eShop.istEingeloggt()) {
                    System.out.println("Bitte zuerst einloggen.");
                    break;
                }
                if (!eShop.istMitarbeiter()) {
                    System.out.println("Nur Mitarbeiter dürfen Preise ändern.");
                    break;
                }

                System.out.print("Bezeichnung > ");
                try {
                    artikelID = eShop.sucheNachID(liesEingabe());
                } catch (ArtikelExistiertNichtException e) {
                    System.out.println(RED + e.getMessage() + RESET);
                    break;
                }

                System.out.print("Neuer Preis > ");
                try {
                    preis = new BigDecimal(liesEingabe());
                    eShop.preisVeraendern(artikelID, preis);
                    System.out.println(YELLOW + "✔ Preis erfolgreich geändert." + RESET);
                } catch (NumberFormatException e) {
                    System.out.println(RED + "Ungültige Eingabe. Bitte geben Sie eine Zahl ein." + RESET);
                } catch (UngueltigerPreisException e) {
                    System.out.println(RED + e.getMessage() + RESET);
                }
            }

            case "al" -> {
                if (!eShop.istEingeloggt()) {
                    System.out.println("Bitte zuerst einloggen.");
                    break;
                }
                if (!eShop.istMitarbeiter()) {
                    System.out.println("Nur Mitarbeiter dürfen Artikel vernichten.");
                    break;
                }

                System.out.println(RED + "Sie sind im Begriff, einen Artikel komplett zu löschen!" + RESET);
                System.out.print("Bezeichnung > ");
                try {
                    artikelID = eShop.sucheNachID(liesEingabe());
                } catch (ArtikelExistiertNichtException e) {
                    System.out.println(RED + e.getMessage() + RESET);
                    break;
                }

                System.out.println(RED + "Sind Sie sicher? [y / n]" + RESET);
                System.out.println(eShop.gibArtikelListe().get(artikelID));

                String eingabe = liesEingabe();
                if (Objects.equals(eingabe, "y")) {
                    eShop.artikelVernichten(artikelID);
                    System.out.println(RED + "✔ Artikel wurde vollständig gelöscht." + RESET);
                } else {
                    System.out.println(YELLOW + "Löschvorgang abgebrochen." + RESET);
                }
            }

            case "r" -> {
                System.out.println("Registration als Kunde:");
                int benutzerId = eShop.generiereId();

                System.out.print("Benutzername > ");
                String benutzerErkennung = liesEingabe();

                System.out.print("Vor- und Nachname > ");
                String benutzerVorNachname = liesEingabe();

                System.out.print("Passwort > ");
                String benutzerPassword = liesEingabe();

                eShop.registrieren(new Kunde(benutzerId, benutzerErkennung, benutzerVorNachname, benutzerPassword));
                System.out.println(GREEN + "✔ Kunde erfolgreich registriert." + RESET);
            }

            case "rm" -> {
                if (!eShop.istMitarbeiter()) {
                    System.out.println("Nur Mitarbeiter dürfen neue Mitarbeiter registrieren.");
                    break;
                }

                System.out.println("Registration als Mitarbeiter:");
                System.out.print("Benutzer ID > ");

                try {
                    int benutzerId = Integer.parseInt(liesEingabe());
                    System.out.print("Benutzername > ");
                    String benutzerErkennung = liesEingabe();

                    System.out.print("Vor- und Nachname > ");
                    String benutzerVorNachname = liesEingabe();

                    System.out.print("Passwort > ");
                    String benutzerPassword = liesEingabe();

                    eShop.registrieren(new Mitarbeiter(benutzerId, benutzerErkennung, benutzerVorNachname, benutzerPassword));
                    System.out.println(GREEN + "✔ Mitarbeiter erfolgreich registriert." + RESET);
                } catch (NumberFormatException e) {
                    System.out.println(RED + "Ungültige Eingabe. Bitte geben Sie eine ganze Zahl ein." + RESET);
                    break;
                }
            }

            case "l" -> {
                if (eShop.istEingeloggt()) {
                    System.out.println("Ein Benutzer ist bereits eingeloggt.");
                    break;
                }

                System.out.print("Benutzername > ");
                String benutzerErkennung = liesEingabe().trim();

                System.out.print("Passwort > ");
                String benutzerPassword = liesEingabe().trim();

                boolean erfolg = eShop.login(benutzerErkennung, benutzerPassword);

                if (erfolg) {
                    Benutzer aktuellerBenutzer = eShop.aktuellerBenutzer();
                    System.out.println(GREEN + "✔ Login erfolgreich. Willkommen "
                            + aktuellerBenutzer.getBenutzerVorNachname()
                            + " (" + aktuellerBenutzer.getRole() + ")" + RESET);
                } else {
                    System.out.println("✘ Login fehlgeschlagen.");
                }
            }

            case "o" -> {
                if (!eShop.istEingeloggt()) {
                    System.out.println("Kein Benutzer ist eingeloggt.");
                    break;
                }

                System.out.print(YELLOW + "Möchten Sie sich wirklich ausloggen? [y/n] > " + RESET);
                String antwort = liesEingabe().trim().toLowerCase();

                if (antwort.equals("y")) {
                    eShop.logout();
                    System.out.println(GREEN + "✔ Logout erfolgreich." + RESET);
                } else if (antwort.equals("n")) {
                    System.out.println(YELLOW + "Logout abgebrochen." + RESET);
                } else {
                    System.out.println("Ungültige Eingabe.");
                }
            }

            case "e" -> {
                if (!eShop.istEingeloggt()) {
                    System.out.println("Bitte zuerst einloggen.");
                    break;
                }
                if (!eShop.istMitarbeiter()) {
                    System.out.println("Nur Mitarbeiter dürfen Ereignisse ansehen.");
                    break;
                }

                System.out.println("Ereignisliste:");
                ArrayList<Ereignis> ereignisListe = eShop.gibEreignisListe();
                if (ereignisListe.isEmpty()) {
                    System.out.println("Keine Ereignisse vorhanden.");
                } else {
                    for (Ereignis e : ereignisListe) {
                        System.out.println(e);
                    }
                }
            }

            case "bh" -> {
                if (!eShop.istEingeloggt()) {
                    System.out.println("Bitte zuerst einloggen.");
                    break;
                }
                if (!eShop.istMitarbeiter()) {
                    System.out.println("Nur Mitarbeiter dürfen die Bestandshistorie sehen.");
                    break;
                }

                System.out.print("Bezeichnung > ");
                artikelID = eShop.sucheNachID(liesEingabe());

                if (artikelID == -1) {
                    System.out.println("Artikel existiert nicht!");
                    break;
                }

                var historie = eShop.berechneBestandHistorie(artikelID);
                System.out.println("Bestandshistorie der letzten 30 Tage:");
                historie.forEach((tag, bestandwert) ->
                        System.out.println("Tag " + tag + ": " + bestandwert)
                );
            }

            case "s" -> {
                if (!eShop.istEingeloggt() || !eShop.istMitarbeiter()) {
                    System.out.println("Nur eingeloggte Mitarbeiter können Daten speichern.");
                    break;
                }
                // eShop.speichereDaten(); // Hängt von deiner Interface-Implementierung ab
                System.out.println(GREEN + "✔ Daten erfolgreich gesichert." + RESET);
            }
        }
    }

    /**
     * Formatiert und gibt die aktuelle Artikelliste inklusive der verfügbaren Mengen
     * auf der Konsole aus. Die Ausgabe erfolgt sortiert nach der Artikel-ID.
     * Es wird visuell zwischen Standard- und Massengutartikeln unterschieden.
     *
     * @param artikelListe eine Map aller im System registrierten Artikel, zugeordnet ihrer ID
     * @param artikelMenge eine Map der verfügbaren Bestandsmengen, zugeordnet der Artikel-ID
     */
    private void gibArtikellisteAus(HashMap<Integer, Artikel> artikelListe, HashMap<Integer, Integer> artikelMenge) {
        if (artikelListe.isEmpty()) {
            System.out.println("Liste ist leer.");
        } else {
            ArrayList<Integer> sortedIDs = new ArrayList<>(artikelListe.keySet());
            Collections.sort(sortedIDs);

            for (int i : sortedIDs) {
                if (eShop.istMassengutartikel(i)) {
                    System.out.println(artikelListe.get(i) + " Gesamte Menge: " + artikelMenge.get(i));
                } else {
                    System.out.println(artikelListe.get(i) + " Menge: " + artikelMenge.get(i));
                }
            }
        }
    }

    /**
     * Gibt den aktuellen Inhalt des Warenkorbs für den eingeloggten Kunden aus.
     * Mappt die IDs der im Warenkorb befindlichen Artikel auf die echten Artikeldaten
     * aus dem Katalog, um Bezeichnungen und Preise anzuzeigen.
     *
     * @param warenkorbListe eine Map der im Warenkorb liegenden Artikel-IDs und deren Anzahl
     * @param artikelListe   die Gesamte Map der Artikel zur Auflösung der Artikeldetails
     */
    private void gibWarenkorbAus(HashMap<Integer, Integer> warenkorbListe, HashMap<Integer, Artikel> artikelListe) {
        if (warenkorbListe.isEmpty()) {
            System.out.println("Warenkorb ist leer.");
        } else {
            for (int i : warenkorbListe.keySet()) {
                if (eShop.istMassengutartikel(i)) {
                    System.out.println(artikelListe.get(i) + " Gesamte Menge: " + warenkorbListe.get(i));
                } else {
                    System.out.println(artikelListe.get(i) + " Menge: " + warenkorbListe.get(i));
                }
            }
        }
    }

    /**
     * Generiert eine tabellarische und formatierte Quittung auf der
     * Konsole. Listet alle gekauften Posten, Einzelpreise, die Zwischensumme, die
     * Mehrwertsteuer (MwSt) sowie den finalen Gesamtpreis auf.
     *
     * @param rechnung das zu druckende Rechnungsobjekt mit allen Kaufdaten
     */
    public void gibRechnungAus(Rechnung rechnung) {
        int rechnung_width = 40;

        // 1. Rechnungskopf zentrieren und ausgeben
        System.out.println();

        // Berechnet die exakte Anzahl an Leerzeichen, um das Wort "Rechnung" (8 Zeichen) mittig zu platzieren
        System.out.println(" ".repeat((rechnung_width - 8) / 2) + "Rechnung");

        // Linksbuendiges Datum (10 Zeichen) und rechtsbuendiger Kundenname (29 Zeichen)
        System.out.printf("%-10s %29s\n", rechnung.getHeutigesDatum(), rechnung.getKundeName());
        System.out.println("-".repeat(rechnung_width));

        // 2. Gekaufte Artikel in Tabellenform auflisten
        for (Rechnung.GekaufterArtikel gekaufterArtikel : rechnung.gibAlleGekaufteArtikel()) {
            System.out.printf(
                    // Format-Matrix (Gesamtbreite: 40 Zeichen):
                    // %-11s   -> Spalte 1: Artikelname, 11 Zeichen breit, LINKSBÜNDIG durch '-'
                    // %12s€   -> Spalte 2: Menge × Preis, 12 Zeichen breit, RECHTSBÜNDIG (ohne '-') + '€'
                    // %13.2f€ -> Spalte 3: Gesamtsumme, 13 Zeichen breit, RECHTSBÜNDIG, fix 2 Nachkommastellen ('.2f') + '€'
                    // \n      -> Zeilenumbruch
                    "%-11s %12s€ %13.2f€\n",
                    gekaufterArtikel.bezeichnung(),
                    gekaufterArtikel.menge() + " × " + String.format("%.2f", gekaufterArtikel.preis()),
                    gekaufterArtikel.summe()
            );
        }

        System.out.println("-".repeat(rechnung_width));
        System.out.printf("%-11s %27.2f€\n", "Summe", rechnung.getSumme());
        System.out.printf("%-11s %27.2f€\n", "MWSt", rechnung.getMwst());
        System.out.printf("%-11s %27.2f€\n", "Gesamtpreis", rechnung.getGesamtPreis());
    }

    /**
     * Startet die zentrale Anwendungsschleife (REPL - Read-Eval-Print-Loop) der CUI.
     * Das Menü wird zyklisch so lange ausgegeben und verarbeitet, bis der Benutzer
     * den Befehl 'q' zum Beenden eingibt.
     */
    public void run() {
        String input = "";

        do {
            gibMenueAus();
            try {
                input = liesEingabe();
                if (input == null) {
                    break;
                }
                verarbeiteEingabe(input.trim());
            } catch (IOException e) {
                System.out.println(RED + "Fehler bei der Ein-/Ausgabe: " + e.getMessage() + RESET);
            }
        } while (!input.equals("q"));

        System.out.println("Auf Wiedersehen!");
    }

    /**
     * Der Haupteinstiegspunkt (Main-Method) für den EShop-Client.
     * Initialisiert die CUI-Komponente und fängt potenzielle Verbindungsfehler
     * beim Starten der Netzwerk-Fassade sauber ab.
     *
     * @param args optionale Befehlszeilenargumente (werden nicht ausgwertet)
     */
    public static void main(String[] args) {
        try {
            EShopClientCUI cui = new EShopClientCUI();
            cui.run();
        } catch (IOException e) {
            System.err.println("\u001B[31m[FEHLER] Der Server konnte nicht erreicht werden. " +
                    "Stellen Sie sicher, dass der EShop-Server läuft.\u001B[0m");
            e.printStackTrace();
        }
    }
}