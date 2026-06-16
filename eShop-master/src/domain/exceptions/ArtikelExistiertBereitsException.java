package domain.exceptions;

import entities.Artikel;

public class ArtikelExistiertBereitsException extends RuntimeException {
    private Artikel artikel;

    public ArtikelExistiertBereitsException(Artikel artikel, String zusatzMsg) {
        super("Artikel mit ID " + artikel.getArtikelID() + " und Bezeichnung " + artikel.getBezeichnung()
                + " existiert bereits" + zusatzMsg);
        this.artikel = artikel;
    }

    public Artikel getArtikel() {
        return artikel;
    }
}
