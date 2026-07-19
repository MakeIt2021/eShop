package eShop.common.entities;

import java.util.HashMap;

/**
 * Repräsentiert den Warenkorb eines Benutzers.
 *
 * Im Warenkorb werden die ausgewählten Artikel sowie deren
 * jeweilige Mengen gespeichert. Jeder Artikel wird über seine
 * Artikel-ID identifiziert.
 */
public class Warenkorb {
    /**
     * Speichert die Artikel-ID und die zugehörige Menge.
     * Key = Artikel-ID
     * Value = Menge
     */
    private HashMap<Integer, Integer> warenkorbMenge = new HashMap<>();

    /**
     * Erstellt einen leeren Warenkorb.
     */
    public Warenkorb() {}

    /**
     * Fügt einen Artikel zum Warenkorb hinzu.
     * Existiert der Artikel bereits, wird die Menge erhöht.
     *
     * @param artikelID ID des Artikels
     * @param menge hinzuzufügende Menge
     */
    public void hinzufuegen(int artikelID, int menge) {
        // Prüft, ob sich der Artikel bereits im Warenkorb befindet.
        if (warenkorbMenge.containsKey(artikelID)) {
            // Vorhandene Menge erhöhen.
            warenkorbMenge.put(artikelID,
                    warenkorbMenge.get(artikelID) + menge);
        } else {
            // Artikel neu in den Warenkorb aufnehmen.
            warenkorbMenge.put(artikelID, menge);
        }
    }

    /**
     * Entfernt eine bestimmte Menge eines Artikels aus dem Warenkorb.
     * Wird die verbleibende Menge kleiner oder gleich 0,
     * wird der Artikel vollständig entfernt.
     *
     * @param artikelID ID des Artikels
     * @param menge zu entfernende Menge
     */
    public void loeschen(int artikelID, int menge) {
        int neueMenge = warenkorbMenge.get(artikelID) - menge;

        if (neueMenge <= 0) {
            // Artikel vollständig entfernen.
            warenkorbMenge.remove(artikelID);
        } else {
            // Neue Menge speichern.
            warenkorbMenge.put(artikelID, neueMenge);
        }
    }

    /**
     * Entfernt alle Artikel aus dem Warenkorb.
     */
    public void zuruecksetzen() {
        warenkorbMenge.clear();
    }

    /**
     * Gibt den gesamten Warenkorb zurück.
     *
     * @return HashMap mit Artikel-ID und Menge
     */
    public HashMap<Integer, Integer> gibWarenkorb() {
        return warenkorbMenge;
    }
}