package eShop.server.domain;

import domain.exceptions.DateiNichtGefundenException;
import entities.Artikel;
import entities.Ereignis;
import persistence.FilePersistenceManager;
import persistence.PersistenceManager;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;

public class EreignisVW {
    private PersistenceManager pm = new FilePersistenceManager();
    ArrayList<Ereignis> ereignisListe = new ArrayList<>();

    public void ladeEreignisse(String datei, Function<String, Artikel> findeArtikel) throws DateiNichtGefundenException {
        try {
            pm.openForReading(datei);
            ArrayList<PersistenceManager.einEreignisInfo> gespeichertEreignisListe = pm.ladeEreignisse();
            pm.close();

            for (PersistenceManager.einEreignisInfo einEreignis : gespeichertEreignisListe) {
                Artikel artikel = findeArtikel.apply(String.valueOf(einEreignis.artikelID()));

                if (artikel == null) {
                    artikel = new Artikel(einEreignis.artikelID(), einEreignis.bezeichnung(), BigDecimal.ZERO);
                }

                ereignisListe.add(new Ereignis(
                        einEreignis.date(),
                        artikel,
                        einEreignis.menge(),
                        einEreignis.typ(),
                        einEreignis.person()
                ));
            }
        } catch (IOException e) {
            throw new DateiNichtGefundenException(e.getMessage(), e.getCause());
        }
    }

    public void speichereEreignisse(String datei) throws DateiNichtGefundenException {
        try {
            pm.openForWriting(datei);
            pm.speichereEreignis(ereignisListe);
            pm.close();
        } catch (IOException e) {
            throw new DateiNichtGefundenException(e.getMessage(), e.getCause());
        }

    }

    public void addEreignis(Artikel einArtikel, int menge, String typ, String person) throws DateiNichtGefundenException {
        ereignisListe.add(new Ereignis(
                LocalDate.now(),
                einArtikel,
                menge,
                typ,
                person
        ));

        speichereEreignisse("Ereignisse.txt"); // TODO: Nicht hier Filepath zu schreiben!
    }

    public ArrayList<Ereignis> gibEreignisListe() {
        return ereignisListe;
    }

    public Map<LocalDate, Integer> gibBestandHistorie(int artikelID) {
        int bestand = 0;

        List<Ereignis> events = ereignisListe.stream()
                .filter(e -> e.getArtikel() != null && e.getArtikel().getArtikelID() == artikelID)
                .toList();

        Map<LocalDate, Integer> vollstaendigeHistorie = new LinkedHashMap<>();

        for (Ereignis e : events) {
            if (e.getTyp().equalsIgnoreCase("EINLAGERUNG")) {
                bestand += e.getMenge();
            } else if (e.getTyp().equalsIgnoreCase("AUSLAGERUNG")) {
                bestand -= e.getMenge();
            }
            vollstaendigeHistorie.put(e.getTag(), bestand);
        }

        Map<LocalDate, Integer> historie = new LinkedHashMap<>();

        int ignorieren = Math.max(0, vollstaendigeHistorie.size() - 30);

        vollstaendigeHistorie.entrySet().stream()
                .skip(ignorieren)
                .forEach(entry -> historie.put(entry.getKey(), entry.getValue()));

        return historie;
    }

    public ArrayList<Integer> gibBestandHistorieAlsIntegers(int artikelID) {
        ArrayList<Integer> data = new ArrayList<>();

        LocalDate prevDate = (LocalDate) ((LinkedHashMap) gibBestandHistorie(artikelID)).sequencedKeySet().getFirst();
        int prevValue = (int) ((LinkedHashMap) gibBestandHistorie(artikelID)).sequencedValues().getFirst();
        for (Map.Entry<LocalDate, Integer> entry : gibBestandHistorie(artikelID).entrySet()) {
            LocalDate date = entry.getKey();
            int v = entry.getValue();

            long daysBetween = ChronoUnit.DAYS.between(prevDate, date);
            if (daysBetween != 0) {
                for (int i = 0; i < daysBetween; i++) {
                    data.add(prevValue);
                }
            }
            data.add(v);

            prevDate = date;
            prevValue = v;
        }

        return data;
    }
}