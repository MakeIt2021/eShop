package domain.exceptions;

    public class ArtikelExistiertNichtException extends RuntimeException {

        public ArtikelExistiertNichtException(int artikelID) {
            super("Artikel mit ID " + artikelID + " existiert nicht.");
        }

        public ArtikelExistiertNichtException(String bezeichnung) {
            super("Artikel mit Bezeichnung \"" + bezeichnung + "\" existiert nicht.");
        }
    }
