package domain;

import entities.Artikel;
import entities.Ereignis;
import persistence.FilePersistenceManager;
import persistence.PersistenceManager;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;

public class EreignisVW {
    private PersistenceManager pm = new FilePersistenceManager();
    ArrayList<Ereignis> ereignisListe = new ArrayList<>();

    public void ladeEreignisse(String datei, Function<String, Artikel> findeArtikelMitBezeichnung) throws IOException {
        pm.openForReading(datei);
        ArrayList<PersistenceManager.einEreignisInfo> gespeichertEreignisListe = pm.ladeEreignisse();
        pm.close();

        for (PersistenceManager.einEreignisInfo einEreignis : gespeichertEreignisListe) {
            Artikel artikel = findeArtikelMitBezeichnung.apply(einEreignis.bezeichnung());

            ereignisListe.add(new Ereignis(
                    einEreignis.date(),
                    artikel,
                    einEreignis.menge(),
                    einEreignis.typ(),
                    einEreignis.person()
            ));
        }
    }

    public void speichereEreignisse(String datei) throws IOException {
        pm.openForWriting(datei);
        pm.speichereEreignis(ereignisListe);
        pm.close();
    }

    public void addEreignis(Artikel einArtikel, int menge, String typ, String person) throws IOException {
        ereignisListe.add(new Ereignis(
                LocalDate.now(),
                einArtikel,
                menge,
                typ,
                person
        ));

        speichereEreignisse("Ereignisse.txt");
    }

    public ArrayList<Ereignis> gibEreignisListe() {
        return ereignisListe;
    }

    public Map<LocalDate, Integer> gibBestandHistorie(int artikelID) {
        int bestand = 0;

        List<Ereignis> events = ereignisListe.stream().filter(e -> e.getArtikel().getArtikelID() == artikelID).toList();

        Map<LocalDate, Integer> historie = new LinkedHashMap<>();

        int counter = 0;
        for (Ereignis e : events) {
            if (e.getTyp().equalsIgnoreCase("EINLAGERUNG")) {
                bestand += e.getMenge();
            } else if (e.getTyp().equalsIgnoreCase("AUSLAGERUNG")) {
                bestand -= e.getMenge();
            }
            if (counter == 30) {
                break;
            }
            historie.put(e.getTag(), bestand);
            counter += 1;
        }

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




