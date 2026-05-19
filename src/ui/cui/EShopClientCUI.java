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

    public EShopClientCUI() throws IOException {

        eShop = new EShop();
        in = new BufferedReader(new InputStreamReader(System.in));
    }


    // For tests only!
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
        String user;


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
                    System.out.println("Nur Mitarbeiter duerfen Artikel hinzufügen.");
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

                user = eShop.getBenutzerVW().getAktuellerBenutzer().getBenutzerVorNachname();
                // Artikel hinzufügen
                eShop.fuegeArtikelEin(
                        artikelID,
                        bezeichnung,
                        bestand,
                        preis,
                        user
                );
                System.out.println("✔ Artikel erfolgreich hinzugefügt.");
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

                user = eShop.getBenutzerVW().getAktuellerBenutzer().getBenutzerVorNachname();

                // Artikelbestand reduzieren
                eShop.loescheArtikel(
                        artikelID,
                        menge,
                        user
                );
                System.out.println("✔ Artikelbestand erfolgreich reduziert.");
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

                user = eShop.getBenutzerVW().getAktuellerBenutzer().getBenutzerVorNachname();

                eShop.fuegeInWarenkorb(artikelID, menge, user);

                System.out.println("✔ Artikel wurde zum Warenkorb hinzugefügt.");
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

                user = eShop.getBenutzerVW().getAktuellerBenutzer().getBenutzerVorNachname();

                eShop.loescheAusWarenkorb(artikelID, menge, user);

                System.out.println("✔ Artikel wurde aus dem Warenkorb entfernt.");
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
                System.out.println("✔ Einkauf erfolgreich abgeschlossen.");
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
                System.out.println("✔ Bezeichnung erfolgreich geändert.");
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

                System.out.println("✔ Preis erfolgreich geändert.");
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

                String yellow = "\u001B[33m";
                String reset = "\u001B[0m";

                System.out.println(
                        yellow +
                                "Sie sind im Begriff, einen Artikel komplett zu löschen!"
                                + reset
                );

                System.out.print("Artikel ID > ");
                artikelID = Integer.parseInt(liesEingabe());

                System.out.println(
                        yellow +
                                "Sind Sie sicher? [y / n]"
                                + reset
                );

                System.out.println(
                        eShop.gibArtikelListe().get(artikelID)
                );

                String eingabe = liesEingabe();

                if (Objects.equals(eingabe, "y")) {

                    eShop.artikelVernichten(artikelID);

                    System.out.println(
                            "✔ Artikel wurde vollständig gelöscht."
                    );

                } else {

                    System.out.println(
                            "Löschvorgang abgebrochen."
                    );
                }
            }
            case "r" -> {
                // TODO: Nur Mitarbeiter kann andere Mitarbeiter registrieren
                System.out.println("Rolle wählen:");
                System.out.println("1 → Kunde");
                System.out.println("2 → Mitarbeiter");

                System.out.print("> ");
                String rolle = liesEingabe();

                System.out.print("Benutzer ID > ");
                int benutzerId = Integer.parseInt(liesEingabe());

                System.out.print("Benutzername > ");
                String benutzerErkennung = liesEingabe();

                System.out.print("Vor- und Nachname > ");
                String benutzerVorNachname = liesEingabe();

                System.out.print("Passwort > ");
                String benutzerPassword = liesEingabe();

                if (rolle.equals("1")) {

                    eShop.getBenutzerVW().registrieren(
                            new Kunde(
                                    benutzerId,
                                    benutzerErkennung,
                                    benutzerVorNachname,
                                    benutzerPassword
                            )
                    );

                    System.out.println("✔ Kunde erfolgreich registriert.");

                } else if (rolle.equals("2")) {

                    eShop.getBenutzerVW().registrieren(
                            new Mitarbeiter(
                                    benutzerId,
                                    benutzerErkennung,
                                    benutzerVorNachname,
                                    benutzerPassword
                            )
                    );

                    System.out.println("✔ Mitarbeiter erfolgreich registriert.");

                } else {

                    System.out.println("Ungültige Rollenauswahl.");
                }
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
                            eShop.getBenutzerVW().getAktuellerBenutzer();

                    System.out.println(
                            "✔ Login erfolgreich. Willkommen "
                                    + aktuellerBenutzer.getBenutzerVorNachname()
                                    + " (" + aktuellerBenutzer.getRole() + ")"
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
                        "Möchten Sie sich wirklich ausloggen? (ja/nein) > "
                );

                String antwort =
                        liesEingabe().trim().toLowerCase();

                if (antwort.equals("ja")) {

                    eShop.getBenutzerVW().logout();

                    System.out.println(
                            "✔ Logout erfolgreich."
                    );

                } else if (antwort.equals("nein")) {

                    System.out.println(
                            "Logout abgebrochen."
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

                System.out.println("✔ Ereignisse erfolgreich gespeichert.");
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

                System.out.println("✔ Artikeldaten erfolgreich gespeichert.");
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
            float gesamtpreis = 0;

            LocalDate today = LocalDate.now();
            String kundeName = eShop.getBenutzerVW().getAktuellerBenutzer().getBenutzerVorNachname();

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

                gesamtpreis += entry.getValue() * curArt.getPreis();
            }

            System.out.println("-".repeat(rechnung_width));
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