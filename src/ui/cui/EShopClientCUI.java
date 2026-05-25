package ui.cui;

import domain.EShop;
import entities.Artikel;
import entities.Benutzer;
import entities.Kunde;
import entities.Mitarbeiter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

public class EShopClientCUI {
    private EShop eShop;
    private BufferedReader in;
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";

    public EShopClientCUI() throws IOException {

        eShop = new EShop();
        in = new BufferedReader(new InputStreamReader(System.in));
    }

    private void gibMenueAus() {

        // fuer Kunde
        if (eShop.getBenutzerVW().istKunde()) {

            System.out.println("\n===== KUNDENMENÜ =====");

            System.out.println("a  → Artikel ansehen");
            System.out.println("we → Artikel in Warenkorb");
            System.out.println("wl → Artikel aus Warenkorb löschen");
            System.out.println("w  → Warenkorb ansehen");
            System.out.println("ak → Kaufen");
            System.out.println("o  → Logout");
        }

        // fuer Mitarbeiter
        else if (eShop.getBenutzerVW().istMitarbeiter()) {

            System.out.println("\n===== MITARBEITERMENÜ =====");

            System.out.println("a  → Artikel ansehen");
            System.out.println("ae → Artikel hinzufügen");
            System.out.println("al → Artikel löschen");
            System.out.println("bv → Bezeichnung ändern");
            System.out.println("pv → Preis ändern");
            System.out.println("vn → Artikel vernichten");
            System.out.println("e  → Ereignisse anzeigen");
            System.out.println("es → Ereignisse speichern");
            System.out.println("rm → Neuen Mitarbeiter registrieren");
            System.out.println("s  → Daten speichern");
            System.out.println("o  → Logout");
        }

        // Person nicht eingeloggt
        else {

            System.out.println("\n===== HAUPTMENUE =====");

            System.out.println("r → Registrieren");
            System.out.println("l → Login");
            System.out.println("q → Beenden");
        }

        System.out.print("> ");
    }

    private String liesEingabe() throws IOException {
        return in.readLine();
    }

    private void verarbeiteEingabe(String line) throws IOException {
        int artikelID;
        int menge;
        String bezeichnung;
        float preis;
        int bestand;
        Benutzer aktuelleBenutzer;


        HashMap<Integer, Artikel> artikelListe;
        HashMap<Integer, Integer> warenkorbListe;

        switch (line) {
            case "a" -> {

                // Prüfen ob Benutzer eingeloggt ist
                if (!eShop.getBenutzerVW().istEingeloggt()) {

                    System.out.println("Bitte zuerst einloggen.");
                    break;
                }
                // Artikelliste anzeigen
                artikelListe = eShop.gibArtikelListe();
                gibArtikellisteAus(
                        artikelListe,
                        eShop.gibArtikelMengeListe()
                );
            }

            case "ae" -> {
                // Prüfen ob Benutzer eingeloggt ist
                if (!eShop.getBenutzerVW().istEingeloggt()) {
                    System.out.println("Bitte zuerst einloggen.");
                    break;
                }
                // Prüfen ob Mitarbeiter eingeloggt ist
                if (!eShop.getBenutzerVW().istMitarbeiter()) {
                    System.out.println("Nur Mitarbeiter dürfen Artikel hinzufügen.");
                    break;
                }
                // Artikelinformationen eingeben
                System.out.print("ArtikelID > ");
                artikelID = Integer.parseInt(liesEingabe());
                System.out.print("Bezeichnung > ");
                bezeichnung = liesEingabe();
                System.out.print("Preis > ");
                preis = Float.parseFloat(liesEingabe());
                System.out.print("Bestand > ");
                bestand = Integer.parseInt(liesEingabe());

                aktuelleBenutzer = eShop.aktuellerBenutzer();
                // Artikel hinzufügen
                eShop.fuegeArtikelEin(
                        artikelID,
                        bezeichnung,
                        bestand,
                        preis,
                        aktuelleBenutzer.getBenutzerVorNachname()
                );
                System.out.println(GREEN + "✔ Artikel erfolgreich hinzugefügt." + RESET);
            }

            case "al" -> {
                //Prüfen ob Benutzer eingeloggt ist
                if (!eShop.getBenutzerVW().istEingeloggt()) {

                    System.out.println("Bitte zuerst einloggen.");
                    break;
                }
                // Prüfen ob Mitarbeiter eingeloggt ist
                if (!eShop.getBenutzerVW().istMitarbeiter()) {
                    System.out.println("Nur Mitarbeiter dürfen Artikel löschen.");
                    break;
                }
                // Artikelinformationen eingeben
                System.out.print("Artikel ID > ");
                artikelID = Integer.parseInt(liesEingabe());
                System.out.print("Menge > ");
                menge = Integer.parseInt(liesEingabe());

                aktuelleBenutzer = eShop.aktuellerBenutzer();

                // Artikelbestand reduzieren
                eShop.loescheArtikel(
                        artikelID,
                        menge,
                        aktuelleBenutzer.getBenutzerVorNachname()
                );
                System.out.println(YELLOW + "✔ Artikelbestand erfolgreich reduziert." + RESET);
            }

            case "we" -> {
                if (!eShop.getBenutzerVW().istEingeloggt()) {
                    System.out.println("Bitte zuerst einloggen.");
                    break;
                }
                if (!eShop.getBenutzerVW().istKunde()) {
                    System.out.println("Nur Kunden dürfen Artikel kaufen.");
                    break;
                }
                System.out.print("Artikel ID > ");
                artikelID = Integer.parseInt(liesEingabe());

                System.out.print("Menge der Artikel > ");
                menge = Integer.parseInt(liesEingabe());

                aktuelleBenutzer = eShop.aktuellerBenutzer();

                eShop.fuegeInWarenkorb(artikelID, menge, aktuelleBenutzer.getBenutzerVorNachname());

                System.out.println(GREEN + "✔ Artikel wurde zum Warenkorb hinzugefügt." + RESET);
            }

            case "wl" -> {
                if (!eShop.getBenutzerVW().istEingeloggt()) {
                    System.out.println("Bitte zuerst einloggen.");
                    break;
                }
                if (!eShop.getBenutzerVW().istKunde()) {
                    System.out.println("Nur Kunden dürfen den Warenkorb ändern.");
                    break;
                }
                System.out.print("Artikel ID > ");
                artikelID = Integer.parseInt(liesEingabe());

                System.out.print("Menge der Artikel > ");
                menge = Integer.parseInt(liesEingabe());

                aktuelleBenutzer = eShop.aktuellerBenutzer();

                eShop.loescheAusWarenkorb(artikelID, menge, aktuelleBenutzer.getBenutzerVorNachname());

                System.out.println(YELLOW + "✔ Artikel wurde aus dem Warenkorb entfernt." + RESET);
            }

            case "w" -> {
                if (!eShop.getBenutzerVW().istEingeloggt()) {
                    System.out.println("Bitte zuerst einloggen.");
                    break;
                }
                if (!eShop.getBenutzerVW().istKunde()) {
                    System.out.println("Nur Kunden haben einen Warenkorb.");
                    break;
                }
                System.out.println("Warenkorb:");
                warenkorbListe = eShop.gibWarenkorb();
                gibWarenkorbAus(
                        warenkorbListe,
                        eShop.gibArtikelListe()
                );
            }

            case "ak" -> {
                if (!eShop.getBenutzerVW().istEingeloggt()) {
                    System.out.println("Bitte zuerst einloggen.");
                    break;
                }
                if (!eShop.getBenutzerVW().istKunde()) {
                    System.out.println("Nur Kunden dürfen Artikel kaufen.");
                    break;
                }
                warenkorbListe = eShop.gibWarenkorb();
                gibGekaufteAus(
                        warenkorbListe,
                        eShop.gibArtikelListe()
                );
                eShop.zuruecksetzeWarenkorb();
                System.out.println(GREEN + "✔ Einkauf erfolgreich abgeschlossen." + RESET);
            }

            case "bv" -> {
                if (!eShop.getBenutzerVW().istEingeloggt()) {
                    System.out.println("Bitte zuerst einloggen.");
                    break;
                }
                if (!eShop.getBenutzerVW().istMitarbeiter()) {
                    System.out.println("Nur Mitarbeiter dürfen Artikel bearbeiten.");
                    break;
                }
                System.out.print("Artikel ID > ");
                artikelID = Integer.parseInt(liesEingabe());

                System.out.print("Neue Bezeichnung > ");
                bezeichnung = liesEingabe();
                eShop.bezeichnungVeraendern(artikelID, bezeichnung);
                System.out.println(YELLOW + "✔ Bezeichnung erfolgreich geändert." + RESET);
            }

            case "pv" -> {

                if (!eShop.getBenutzerVW().istEingeloggt()) {
                    System.out.println("Bitte zuerst einloggen.");
                    break;
                }

                if (!eShop.getBenutzerVW().istMitarbeiter()) {
                    System.out.println("Nur Mitarbeiter dürfen Preise ändern.");
                    break;
                }

                System.out.print("Artikel ID > ");
                artikelID = Integer.parseInt(liesEingabe());

                System.out.print("Neuer Preis > ");
                preis = Float.parseFloat(liesEingabe());

                eShop.preisVeraendern(artikelID, preis);

                System.out.println(YELLOW + "✔ Preis erfolgreich geändert." + RESET);
            }

            case "vn" -> {

                if (!eShop.getBenutzerVW().istEingeloggt()) {
                    System.out.println("Bitte zuerst einloggen.");
                    break;
                }

                if (!eShop.getBenutzerVW().istMitarbeiter()) {
                    System.out.println("Nur Mitarbeiter dürfen Artikel vernichten.");
                    break;
                }

                System.out.println(
                        RED +
                                "Sie sind im Begriff, einen Artikel komplett zu löschen!"
                                + RESET
                );

                System.out.print("Artikel ID > ");
                artikelID = Integer.parseInt(liesEingabe());

                System.out.println(
                        RED +
                                "Sind Sie sicher? [y / n]"
                                + RESET
                );

                System.out.println(
                        eShop.gibArtikelListe().get(artikelID)
                );

                String eingabe = liesEingabe();

                if (Objects.equals(eingabe, "y")) {

                    eShop.artikelVernichten(artikelID);

                    System.out.println(
                            RED + "✔ Artikel wurde vollständig gelöscht." + RESET
                    );

                } else {

                    System.out.println(
                            YELLOW + "Löschvorgang abgebrochen." + RESET
                    );
                }
            }
            case "r" -> {
                System.out.println("Registration als Kunde:");

                //System.out.print("Benutzer ID > ");
                int benutzerId = eShop.getBenutzerVW().generiereId();

                System.out.print("Benutzername > ");
                String benutzerErkennung = liesEingabe();

                System.out.print("Vor- und Nachname > ");
                String benutzerVorNachname = liesEingabe();

                System.out.print("Passwort > ");
                String benutzerPassword = liesEingabe();

                eShop.getBenutzerVW().registrieren(
                            new Kunde(
                                    benutzerId,
                                    benutzerErkennung,
                                    benutzerVorNachname,
                                    benutzerPassword
                            )
                    );

                    System.out.println(GREEN + "✔ Kunde erfolgreich registriert." + RESET);
            }

            case "rm" -> {
                if (!eShop.getBenutzerVW().istMitarbeiter()) {
                    System.out.println("Nur Mitarbeiter dürfen neue Mitarbeiter registrieren.");
                    break;
                }

                System.out.println("Registration als Mitarbeiter:");

                System.out.print("Benutzer ID > ");
                int benutzerId = Integer.parseInt(liesEingabe());

                System.out.print("Benutzername > ");
                String benutzerErkennung = liesEingabe();

                System.out.print("Vor- und Nachname > ");
                String benutzerVorNachname = liesEingabe();

                System.out.print("Passwort > ");
                String benutzerPassword = liesEingabe();


                eShop.getBenutzerVW().registrieren(
                        new Mitarbeiter(
                                benutzerId,
                                benutzerErkennung,
                                benutzerVorNachname,
                                benutzerPassword
                        )
                );

                System.out.println(GREEN + "✔ Mitarbeiter erfolgreich registriert." + RESET);
            }

            case "l" -> {
                if (eShop.getBenutzerVW().istEingeloggt()) {
                    System.out.println(
                            "Ein Benutzer ist bereits eingeloggt."
                    );

                    break;
                }

                System.out.print("Benutzername > ");
                String benutzerErkennung = liesEingabe().trim();

                System.out.print("Passwort > ");
                String benutzerPassword = liesEingabe().trim();

                boolean erfolg = eShop.getBenutzerVW().login(
                        benutzerErkennung,
                        benutzerPassword
                );

                if (erfolg) {

                    Benutzer aktuellerBenutzer =
                            eShop.aktuellerBenutzer();

                    System.out.println(
                            GREEN + "✔ Login erfolgreich. Willkommen "
                                    + aktuellerBenutzer.getBenutzerVorNachname()
                                    + " (" + aktuellerBenutzer.getRole() + ")" + RESET
                    );

                } else {

                    System.out.println(
                            "✘ Login fehlgeschlagen."
                    );
                }
            }

            case "o" -> {

                if (!eShop.getBenutzerVW().istEingeloggt()) {

                    System.out.println(
                            "Kein Benutzer ist eingeloggt."
                    );

                    break;
                }

                System.out.print(
                        YELLOW + "Möchten Sie sich wirklich ausloggen? [y/n] > " + RESET
                );

                String antwort =
                        liesEingabe().trim().toLowerCase();

                if (antwort.equals("y")) {

                    eShop.getBenutzerVW().logout();

                    System.out.println(
                            GREEN + "✔ Logout erfolgreich." + RESET
                    );

                } else if (antwort.equals("n")) {

                    System.out.println(
                            YELLOW + "Logout abgebrochen." + RESET
                    );

                } else {

                    System.out.println(
                            "Ungültige Eingabe."
                    );
                }
            }

            case "e" -> {

                if (!eShop.getBenutzerVW().istEingeloggt()) {
                    System.out.println("Bitte zuerst einloggen.");
                    break;
                }

                if (!eShop.getBenutzerVW().istMitarbeiter()) {
                    System.out.println("Nur Mitarbeiter dürfen Ereignisse ansehen.");
                    break;
                }

                System.out.println("Ereignisliste:");

                eShop.gibEreignisseAus();
            }


            case "es" -> {

                if (!eShop.getBenutzerVW().istEingeloggt()) {
                    System.out.println("Bitte zuerst einloggen.");
                    break;
                }

                if (!eShop.getBenutzerVW().istMitarbeiter()) {
                    System.out.println("Nur Mitarbeiter dürfen Ereignisse speichern.");
                    break;
                }

                eShop.speichereEreignisseTXT();

                System.out.println(GREEN + "✔ Ereignisse erfolgreich gespeichert." + RESET);
            }


            case "s" -> {

                if (!eShop.getBenutzerVW().istEingeloggt()) {
                    System.out.println("Bitte zuerst einloggen.");
                    break;
                }

                if (!eShop.getBenutzerVW().istMitarbeiter()) {
                    System.out.println("Nur Mitarbeiter dürfen Daten speichern.");
                    break;
                }

                eShop.speichereArtikel();

                System.out.println(GREEN + "✔ Artikeldaten erfolgreich gespeichert." + RESET);
            }
        }
    }

    private void gibArtikellisteAus(HashMap<Integer, Artikel> artikelListe, HashMap<Integer, Integer> artikelMenge) {
        if (artikelListe.isEmpty()) {
            System.out.println("Liste ist leer.");
        } else {
            for (int i : artikelListe.keySet()) {
                System.out.println(artikelListe.get(i) + " Menge: " + artikelMenge.get(i));
            }
        }
    }

    private void gibWarenkorbAus(HashMap<Integer, Integer> warenkorbListe, HashMap<Integer, Artikel> artikelListe) {
        if (warenkorbListe.isEmpty()) {
            System.out.println("Warenkorb ist leer.");
        } else {
            for (int i : warenkorbListe.keySet()) {
                System.out.println(artikelListe.get(i) + " Menge: " + warenkorbListe.get(i));
            }
        }
    }

    public void gibGekaufteAus(HashMap<Integer, Integer> warenkorbListe, HashMap<Integer, Artikel> artikelListe) {
        if (warenkorbListe.isEmpty()) {
            System.out.println("Warenkorb ist leer.");
        } else {
            int rechnung_width = 40;
            float summe = 0;
            double mwstSumme;
            double gesamtpreis;
            final double MWST = 0.19;

            LocalDate today = LocalDate.now();
            String kundeName = eShop.aktuellerBenutzer().getBenutzerVorNachname();// mussen wir alle logic im eshop machen

            System.out.println();
            System.out.println(" ".repeat((rechnung_width - 8) / 2) + "Rechnung");
            System.out.printf("%-10s %29s\n", today, kundeName);
            System.out.println("-".repeat(rechnung_width));

            for (Map.Entry<Integer, Integer> entry : warenkorbListe.entrySet()) {
                Artikel curArt = artikelListe.get(entry.getKey());
                System.out.printf(
                        "%-11s %12s€ %13.2f€\n",
                        curArt.getBezeichnung(),
                        entry.getValue() + " × " + String.format("%.2f", curArt.getPreis()),
                        curArt.getPreis() * entry.getValue()
                );

                summe += entry.getValue() * curArt.getPreis();
            }

            mwstSumme = MWST * summe;
            gesamtpreis = mwstSumme + summe;

            System.out.println("-".repeat(rechnung_width));
            System.out.printf("%-11s %27.2f€\n", "Summe", summe);
            System.out.printf("%-11s %27.2f€\n", "MWSt", mwstSumme);
            System.out.printf("%-11s %27.2f€\n", "Gesamtpreis", gesamtpreis);

        }
    }

    public void run() {
        String input = "";

        do {
            gibMenueAus();
            try {
                input = liesEingabe();
                verarbeiteEingabe(input);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } while (!input.equals("q"));
    }

    public static void main(String[] args) {
        EShopClientCUI cui;
        try {
            cui = new EShopClientCUI();
            cui.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}