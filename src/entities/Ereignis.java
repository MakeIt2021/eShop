package entities;

/*
 * Diese Klasse speichert jedes Lager-Ereignis.
 *
 * Ein Ereignis bedeutet:
 * - Einlagerung
 * - Auslagerung
 *
 * Gespeichert werden:
 * - Datum (Tag des Jahres)
 * - Artikel
 * - Menge
 * - beteiligte Person
 * - Typ des Ereignisses
 */



public class Ereignis {
    private int tag;
    private Artikel artikel;
    private int menge;
    private String typ; //EINLAGERUNG oder AUSLAGERUNG
    private String person; // TODO: eigentlich sollte hier Benutzer sein (nicht verändern ohne zu besprechen, bitte)

    public Ereignis(int tag, Artikel artikel, int menge, String typ, String person) {
        this.tag = tag;
        this.artikel = artikel;
        this.menge = menge;
        this.typ = typ;
        this.person = person;

    }

    public int getTag() {
        return tag;
    }

    public Artikel getArtikel() {
        return artikel;
    }

    public int getMenge() {
        return menge;
    }

    public String getTyp() {
        return typ;
    }

    public String getPerson() {
        return person;
    }

    public String toString() {
        return "Tag:" + typ + " | Typ: " + typ
                + " | Artikel: " + artikel.getBezeichnung()
                + " | Menge: " + menge
                + " | Person: " + person;
    }
}
