import domain.EShop;
import entities.Artikel;
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
        eShop.seedTestData(); // test articles
        in = new BufferedReader(new InputStreamReader(System.in));
    }


    // For tests only!
    private void gibMenueAus() {
        System.out.print("Befehle: \n  Artikel liste:  'a'");
        System.out.print("\n  Artikel einzufuegen:  'ae'");
        System.out.print("\n  Artikel loeschen:  'al'");
        System.out.print("\n  Artikel Bezeichnung verändern: 'bv'");
        System.out.print("\n  Artikel Preis verändern: 'pv'");
        System.out.print("\n  Artikel vernichten: 'vn'");
        System.out.print("\n  Artikel in Warenkorb einzufügen:  'we'");
        System.out.print("\n  Warenkorb ansehen:  'w'");
        System.out.print("\n  Artikel aus der Warenkorb löschen:  'wl'");
        System.out.print("\n  Artikel kaufen:  'ak'");
        System.out.print("         \n  ---------------------");
        System.out.println("         \n  Beenden:        'q'");
        System.out.print("> "); // Prompt
        System.out.flush();// ohne NL ausgeben
        System.out.print("\n Registrieren: 'r' ");//benutzer registrierung
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
        HashMap<Integer, Artikel> artikelListe;


        HashMap<Integer, Artikel> artikelListe;
        HashMap<Integer, Integer> warenkorbListe;

        switch (line) {
            case "a" -> {
                artikelListe = eShop.gibArtikelListe();
                gibArtikellisteAus(artikelListe, eShop.gibArtikelMengeListe());
            }

            case "ae" -> {
                // lies die notwendigen Parameter, einzeln pro Zeile
                System.out.print("ArtikelID > ");
                artikelID = Integer.parseInt(liesEingabe());
                System.out.print("Bezeichnung  > ");
                bezeichnung = liesEingabe();
                System.out.print("Preis  > ");
                preis = Float.parseFloat(liesEingabe());
                System.out.print("Bestand  > ");
                bestand = Integer.parseInt(liesEingabe());


                eShop.fuegeArtikelEin(artikelID, bezeichnung, bestand, preis);
                System.out.println("Einfügen ok");

            }

            case "al" -> {
                System.out.println("Artikel ID > ");
                artikelID = Integer.parseInt(liesEingabe());
                System.out.println("Menge: ");
                menge = Integer.parseInt(liesEingabe());

                eShop.loescheArtikel(artikelID, menge);
            }

            case "we" -> {
                System.out.println("Artikel ID > ");
                artikelID = Integer.parseInt(liesEingabe());
                System.out.println("Menge der Artikel: ");
                menge = Integer.parseInt(liesEingabe());

                eShop.fuegeInWarenkorb(artikelID, menge);
            }

            case "wl" -> {
                System.out.println("Artikel ID > ");
                artikelID = Integer.parseInt(liesEingabe());
                System.out.println("Menge der Artikel: ");
                menge = Integer.parseInt(liesEingabe());

                eShop.loescheAusWarenkorb(artikelID, menge);
            }

            case "w" -> {
                System.out.println("Warenkorb: ");

                warenkorbListe = eShop.gibWarenkorb();
                gibWarenkorbAus(warenkorbListe, eShop.gibArtikelListe());
            }

            case "ak" -> {
                warenkorbListe = eShop.gibWarenkorb();
                gibGekaufteAus(warenkorbListe, eShop.gibArtikelListe());
                eShop.zuruecksetzeWarenkorb();
            }

            case "bv" -> {
                System.out.println("Artikel ID > ");
                artikelID = Integer.parseInt(liesEingabe());
                System.out.print("Neue Bezeichnung  > ");
                bezeichnung = liesEingabe();

                eShop.bezeichnungVeraendern(artikelID, bezeichnung);
            }

            case "pv" -> {
                System.out.println("Artikel ID > ");
                artikelID = Integer.parseInt(liesEingabe());
                System.out.print("Neuer Preis  > ");
                preis = Float.parseFloat(liesEingabe());

                eShop.preisVeraendern(artikelID, preis);
            }

            case "vn" -> {
                String yellow = "\u001B[33m";
                String reset = "\u001B[0m";

                System.out.println(yellow + "Sie sind im Begriff, einen Artikel komplett zu löschen!" + reset);

                System.out.println("Artikel ID > ");
                artikelID = Integer.parseInt(liesEingabe());

                System.out.println(yellow + "Sind Sie sicher, dass Sie diesen Artikel vollständig aus dem Katalog löschen möchten? [y / n]" + reset);
                System.out.println(eShop.gibArtikelListe().get(artikelID));

                String eingabe = liesEingabe();
                if (Objects.equals(eingabe, "y")) {
                    eShop.artikelVernichten(artikelID);
                    System.out.println("Artikel wurde gelöscht.");
                } else {
                    System.out.println("Artikel wurde nicht agelöscht.");
                }

            }
            case "r" -> {
                System.out.print("benutzerId>");
                int benutzerId = Integer.parseInt(liesEingabe());//als liesEingabe gibt nur integer ich habe parseInt benutzt

                System.out.print("BenutzerErkennung > ");
                String benutzerErkennung = liesEingabe();

                System.out.print("benutzerVorNachname");
                String benutzerVorNachname = liesEingabe();

                System.out.print("Benutzerpassword > ");
                String benutzerPassword = liesEingabe();

                System.out.print("Typ (k = Kunde, m = Mitarbeiter) > ");
                String typ = liesEingabe();
                if (typ.equals("k")) {

                    eShop.getBenutzerVW().registrieren(
                            new Kunde(benutzerId, benutzerErkennung,
                                    benutzerVorNachname, benutzerPassword)
                    );

                } else {

                    eShop.getBenutzerVW().registrieren(
                            new Mitarbeiter(benutzerId, benutzerErkennung,
                                    benutzerVorNachname, benutzerPassword)
                    );
                }

                System.out.println("✔ Registrierung erfolgreich!");
            }

        }
    }


    private void gibArtikellisteAus(HashMap<Integer, Artikel> liste) {
        if (liste.isEmpty()) {
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
            String kundeName = "Johann Sebastian Bach";

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