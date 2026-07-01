package domain;

import domain.exceptions.DateiNichtGefundenException;
import entities.Benutzer;
import persistence.FilePersistenceManager;
import persistence.PersistenceManager;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class BenutzerVW {
    private final String datei = "benutzer.txt"; //TODO: nicht so hardcoded!
    private Benutzer aktuellerBenutzer;
    private int nextId = 1;

    //benuzer speicherung im PM
    private PersistenceManager pm = new FilePersistenceManager();
    private HashMap<String, Benutzer> benutzerMap = new HashMap<>();

    public BenutzerVW() throws DateiNichtGefundenException {
        try {
            this.benutzerMap = this.pm.ladeBenutzer();

            for (Benutzer benutzer : benutzerMap.values()) {
                nextId = Math.max(nextId, benutzer.getBenutzerId() + 1);
            }
        } catch (IOException e){
            throw new DateiNichtGefundenException(e.getMessage(), e.getCause());
        }
    }

    public boolean registrieren(Benutzer benutzer) throws DateiNichtGefundenException {
        if (this.benutzerMap.containsKey(benutzer.getBenutzerErkennung())) {
            return false;
        }

        this.benutzerMap.put(benutzer.getBenutzerErkennung(), benutzer);

        try {
            pm.openForWriting(datei);
            pm.speicherBenutzer(benutzerMap);
            pm.close();
        } catch (IOException e) {
            throw new DateiNichtGefundenException(e.getMessage(), e.getCause());
        }

        return true;
    }

    public boolean login(String benutzerErkennung, String benutzerPassword) {
        Benutzer benutzer = (Benutzer)this.benutzerMap.get(benutzerErkennung);
        if (benutzer != null && benutzer.checkPassword(benutzerPassword)) {
            this.aktuellerBenutzer = benutzer;
            return true;
        } else {
            return false;
        }
    }

    public void logout() {
        if (this.aktuellerBenutzer != null) {
            this.aktuellerBenutzer = null;
        }
    }

    protected Benutzer getAktuellerBenutzer() {
        return this.aktuellerBenutzer;
    }

    public boolean istEingeloggt() {
        return this.aktuellerBenutzer != null;
    }

    public boolean istMitarbeiter() {
        return this.aktuellerBenutzer != null && this.aktuellerBenutzer.getRole().equals("Mitarbeiter");
    }

    public boolean istKunde() {
        return this.aktuellerBenutzer != null && this.aktuellerBenutzer.getRole().equals("Kunde");
    }

    //Id generiere method
    public int generiereId(){
        return nextId++;
    }
}
