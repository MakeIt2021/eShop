package ui.gui;

// Note: Diese Klasse wurde für Tests geschrieben.

import domain.EShop;

import java.math.BigDecimal;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;

public class TestDataGenerator {

    public static void generiereUmfangreicheTestdaten(EShop eshop, String mitarbeiterName) {
        try {
            // 1. Sicherheitscheck: Wenn der Shop schon Daten hat, tun wir nichts
            if (!eshop.gibArtikelListe().isEmpty()) {
                System.out.println(">>> Der Shop enthaelt bereits Artikel. Automatische Initialisierung abgebrochen.");
                return;
            }

            System.out.println(">>> [Externes Skript] Generiere Artikel ueber offizielle EShop-API...");

            // Struktur: {Bezeichnung, Startmenge, Preis (String), Packungsgroesse}
            Object[][] testKatalog = {
                    {"Gaming Laptop ASUS", 40, "1299.99", 1},
                    {"iPhone 15 Pro", 20, "1099.00", 1},
                    {"Mechanische Tastatur", 60, "89.90", 1},
                    {"4K Monitor 27\"", 15, "349.50", 1},
                    {"SSD 1TB NVMe", 100, "95.00", 1},
                    {"Ergonomic Chair", 10, "249.00", 1},
                    {"Webcam 1080p", 30, "69.99", 1},
                    {"LED Schreibtischlampe Pack", 100, "19.99", 10},
                    {"USB-C Hub Bulk", 500, "12.50", 50},
                    {"Wasserflaschen Set", 150, "8.00", 5}
            };

            int startID = 1000;
            List<String> gefaelschteLogs = new ArrayList<>();
            // Zeitraum auf 365 Tage (1 Jahr) erhoeht
            LocalDate startDatum = LocalDate.now().minusDays(365);

            // TAG 1: Alle Artikel initial anlegen
            for (Object[] art : testKatalog) {
                String name = (String) art[0];
                int menge = (int) art[1];
                BigDecimal preis = new BigDecimal((String) art[2]);
                int packGroesse = (int) art[3];

                if (packGroesse > 1) {
                    eshop.fuegeMassengutartikelEin(startID, name, menge, preis, mitarbeiterName, packGroesse);
                } else {
                    eshop.fuegeArtikelEin(startID, name, menge, preis, mitarbeiterName);
                }

                // Logzeile fuer Tag 1 generieren
                gefaelschteLogs.add("Tag: " + startDatum + " | Typ: Einlagerung | ArtikelID: " + startID + " | Artikel: " + name + " | Menge: " + menge + " | Person: m:" + mitarbeiterName);
                startID++;
            }

            Random random = new Random();

            // TAGE 2 bis 365: Simulation von Kaufen und Lieferungen
            for (LocalDate tag = startDatum.plusDays(1); !tag.isAfter(LocalDate.now()); tag = tag.plusDays(1)) {

                // Erstellt eine Liste aller IDs und mischt sie durch (Shuffling)
                List<Integer> alleArtikelIDs = new ArrayList<>();
                for (int i = 0; i < testKatalog.length; i++) {
                    alleArtikelIDs.add(1000 + i);
                }
                Collections.shuffle(alleArtikelIDs);

                // Bestimmt, wie viele Artikel sich heute maximal aendern (z.B. zwischen 2 und 5 Artikeln pro Tag)
                int anzahlAenderungenHeute = random.nextInt(4) + 2;

                for (int j = 0; j < anzahlAenderungenHeute && j < alleArtikelIDs.size(); j++) {
                    // Jede Artikel-ID wird pro Tag-Schleife maximal einmal gezogen
                    int aktuellerArtikelID = alleArtikelIDs.get(j);
                    int aktuellerBestand = eshop.getBestand(aktuellerArtikelID);
                    boolean istMassengut = eshop.istMassengutartikel(aktuellerArtikelID);
                    int packGroesse = istMassengut ? eshop.getPackungGroesse(aktuellerArtikelID) : 1;

                    if (aktuellerBestand <= (packGroesse * 2)) {
                        // Bestand niedrig -> Nachlieferung
                        int partien = random.nextInt(3) + 1;
                        int lieferung = partien * packGroesse;

                        eshop.bestandVeraendern(aktuellerArtikelID, aktuellerBestand + lieferung, mitarbeiterName);
                        gefaelschteLogs.add("Tag: " + tag + " | Typ: Einlagerung | ArtikelID: " + aktuellerArtikelID + " | Artikel: " + eshop.getArtikelName(aktuellerArtikelID) + " | Menge: " + lieferung + " | Person: m:" + mitarbeiterName);
                    } else {
                        // Bestand okay -> Kunde kauft
                        int partien = random.nextInt(2) + 1;
                        int kauf = partien * packGroesse;

                        if (aktuellerBestand - kauf > 0) {
                            eshop.bestandVeraendern(aktuellerArtikelID, aktuellerBestand - kauf, "GeneratedKunde_" + random.nextInt(100));
                            gefaelschteLogs.add("Tag: " + tag + " | Typ: Auslagerung | ArtikelID: " + aktuellerArtikelID + " | Artikel: " + eshop.getArtikelName(aktuellerArtikelID) + " | Menge: " + kauf + " | Person: k: " + "GeneratedKunde_" + random.nextInt(100));
                        }
                    }
                }
            }

            // 2. DER TRICK: Ereignisse.txt von aussen mit den gefaelschten Daten ueberschreiben
            System.out.println(">>> Korrigiere Ereignis-Historie in Ereignisse.txt rueckwirkend...");
            Path pfad = Paths.get("Ereignisse.txt");
            Files.write(pfad, gefaelschteLogs);

            System.out.println(">>> [Erfolg] Testdaten generiert!");

        } catch (Exception e) {
            System.out.println("Fehler beim Initialisieren: " + e.getMessage());
            e.printStackTrace();
        }
    }
}