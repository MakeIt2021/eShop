package eShop.common.exceptions;

import eShop.common.entities.Artikel;

/**
 * Diese Exception wird ausgelöst, wenn versucht wird,
 * einen Artikel einzufügen, der bereits im eShop existiert.
 *
 * Die Exception speichert zusätzlich das betroffene Artikelobjekt,
 * damit weitere Informationen über den bereits vorhandenen Artikel
 * abgerufen werden können.
 *

 */
public class ArtikelExistiertBereitsException extends RuntimeException {

    /**
     * Der Artikel, der bereits im System vorhanden ist.
     */
    private Artikel artikel;

    /**
     * Erzeugt eine neue Exception für einen bereits existierenden Artikel.
     *
     * @param artikel   der bereits vorhandene Artikel
     */
    public ArtikelExistiertBereitsException(Artikel artikel) {
        super("Artikel mit Bezeichnung "
                + artikel.getBezeichnung()
                + " existiert bereits");

        this.artikel = artikel;
    }

    /**
     * Gibt den bereits vorhandenen Artikel zurück.
     *
     * @return der vorhandene Artikel
     */
    public Artikel getArtikel() {
        return artikel;
    }
}