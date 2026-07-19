package eShop.common.entities;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Repräsentiert ein Produkt im EShop und dient als Basisklasse
 * für spezifische Artikeltypen wie den {@link Massengutartikel}.
 *
 * @author Bulat Valiullin
 * @version 1.0
 */

public class Artikel {
    private int artikelID;
    private String bezeichnung;
    private BigDecimal preis;

    /**
     * Erstellt einen neuen Artikel mit den angegebenen Eigenschaften.
     *
     * @param artikelID   die eindeutige Identifikationsnummer des Artikels
     * @param bezeichnung der Name des Artikels
     * @param preis       der Preis des Einzelartikels (oder der gesamten Packung) ohne MwSt als {@link BigDecimal}. Bei Standardartikeln entspricht dies dem Einzelpreis, bei {@link Massengutartikel} dem Preis der gesamten Packung. Der Wert wird mit dem kaufmännischen Rundungsmodus {@link RoundingMode#HALF_EVEN} auf zwei Nachkommastellen gerundet.
     */
    public Artikel(int artikelID, String bezeichnung, BigDecimal preis) {
        this.artikelID = artikelID;
        this.bezeichnung = bezeichnung;
        this.preis = preis;
    }

    /**
     * Gibt eine lesbare Textdarstellung des Artikels zurück, die speziell für die Protokollierung in der Datei {@code Ereignisse.txt} formatiert ist.
     *
     * @return eine String-Repräsentation des Artikels mit allen Attributen
     */
    public String toString() {
        return ("Artikel ID: " + artikelID + " | Bezeichnung: " + bezeichnung + " | Preis: " + preis + " |");
    }

    public int getArtikelID() {
        return artikelID;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public BigDecimal getPreis() {
        return preis;
    }

    public void setBezeichnung(String newBezeichnung) {
        this.bezeichnung = newBezeichnung;
    }

    public void setPreis(BigDecimal newPreis) {
        this.preis = newPreis;
    }

    /**
     * Konvertiert den Artikel in ein bestimmtes Textformat für die Netzwerkübertragung.
     * Die '0' am Ende signalisiert, dass es sich um einen Standardartikel handelt.
     *
     * @return der formatierte Netzwerk-String
     */
    public String toNetworkString() {
        return artikelID + ";" + bezeichnung + ";" + preis + ";" + "0";
    }

    /**
     * Erstellt ein Artikel- oder Massengutartikel-Objekt
     * aus einem empfangenen Netzwerk-String.
     *
     * @return ein neues {@link Artikel}- oder {@link Massengutartikel}-Objekt,
     *         oder {@code null}, wenn der übergebene String null oder "null" ist
     */
    public static Artikel fromNetworkString(String line) {
        if (line == null || line.equals("null"))
            return null;

        String[] parts = line.split(";");
        int id = Integer.parseInt(parts[0]);
        String bezeichnung = parts[1];
        BigDecimal preis = new BigDecimal(parts[2]).setScale(2, RoundingMode.HALF_EVEN);
        int packungsGroesse = Integer.parseInt(parts[3]);

        if (packungsGroesse == 0) {
            return new Artikel(id, bezeichnung, preis);
        } else {
            return new Massengutartikel(id, bezeichnung, preis, packungsGroesse);
        }
    }
}