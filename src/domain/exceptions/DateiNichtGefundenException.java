package domain.exceptions;

import java.io.FileNotFoundException;
import java.io.IOException;

public class DateiNichtGefundenException extends RuntimeException {
    public DateiNichtGefundenException(String e) {
        super("Diese Datei konnte nicht gefunden werden: " + e);
    }

    public DateiNichtGefundenException(String e, Throwable t) {
        super("Diese Datei konnte nicht gefunden werden: " + e + "\nDie Ursache ist:" + t);
    }
}

