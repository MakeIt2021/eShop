
import domain.ArtikelVW;
import domain.EShop;
import entities.Artikel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

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
        System.out.print("\n  Artikel in Warenkorb einzufügen:  'we'");
        System.out.print("\n  Artikel aus der Warenkorb löschen:  'wl'");
        System.out.print("         \n  ---------------------");
        System.out.println("         \n  Beenden:        'q'");
        System.out.print("> "); // Prompt
        System.out.flush(); // ohne NL ausgeben
    }

    private String liesEingabe() throws IOException {
        // einlesen von Konsole
        return in.readLine();
    }

    private void verarbeiteEingabe(String line) throws IOException {
        String nummer;
        int artikelID;
        int menge;
        String bezeichnung;
        int preis;
        int bestand;

        HashMap<Integer, Artikel> artikelListe;
        HashMap<Integer, Integer> warenkorbListe;


        switch (line) {
            // TODO: Change name, price and other parameters

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
                preis = Integer.parseInt(liesEingabe());
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

    public void run() {
        // Variable für Eingaben von der Konsole
        String input = "";

        // Hauptschleife der Benutzungsschnittstelle
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
