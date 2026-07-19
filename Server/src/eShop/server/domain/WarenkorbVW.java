package eShop.server.domain;

import eShop.common.entities.Warenkorb;
import java.util.HashMap;

/**
 * Verwaltet den Warenkorb auf der Serverseite.
 *
 * Die Klasse dient als Verwaltungsschicht zwischen der
 * Serverlogik und der Entitätsklasse {@link Warenkorb}.
 * Sie leitet die Warenkorboperationen an das gespeicherte
 * Warenkorbobjekt weiter.
 *
 * Die Abkürzung {@code VW} steht dabei für Verwaltung.
 */

public class WarenkorbVW {
    /**
     * Warenkorb, der durch diese Verwaltungsklasse verwaltet wird.
     */
    Warenkorb warenkorb = new Warenkorb();

    /**
     * Fügt eine bestimmte Menge eines Artikels in den Warenkorb ein.
     *
     * Die eigentliche Verarbeitung wird durch die Methode
     * {@link Warenkorb#hinzufuegen(int, int)} durchgeführt.
     *
     * @param artikelID eindeutige ID des Artikels
     * @param menge Menge, die dem Warenkorb hinzugefügt werden soll
     */
    public void einfuegen(int artikelID, int menge) {
        warenkorb.hinzufuegen(artikelID, menge);
    }

    /**
     * Entfernt eine bestimmte Menge eines Artikels aus dem Warenkorb.
     *
     * Die eigentliche Verarbeitung wird durch die Methode
     * {@link Warenkorb#loeschen(int, int)} durchgeführt.
     *
     * @param artikelID eindeutige ID des Artikels
     * @param menge Menge, die aus dem Warenkorb entfernt werden soll
     */
    public void loeschen(int artikelID, int menge) {
        warenkorb.loeschen(artikelID, menge);
    }

    /**
     * Leert den gesamten Warenkorb.
     *
     * Nach dem Aufruf enthält der Warenkorb keine Artikel mehr.
     */
    public void zuruecksetzen() {
        warenkorb.zuruecksetzen();
    }

    /**
     * Gibt den Inhalt des Warenkorbs zurück.
     *
     * Der Schlüssel der {@link HashMap} entspricht der Artikel-ID.
     * Der zugehörige Wert entspricht der im Warenkorb gespeicherten Menge.
     *
     * @return Zuordnung der Artikel-IDs zu den jeweiligen Mengen
     */
    public HashMap<Integer, Integer> gibWarenkorb() {
        return warenkorb.gibWarenkorb();
    }
}
