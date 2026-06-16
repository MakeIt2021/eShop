//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package domain;

import entities.Benutzer;
import entities.Kunde;
import entities.Mitarbeiter;
import persistence.FilePersistenceManager;
import persistence.PersistenceManager;

import javax.management.relation.Role;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class BenutzerVW {
    private final String datei = "benutzer.txt";
    private Benutzer aktuellerBenutzer;
    private int nextId = 1;

    //benuzer speicherung im PM
    private PersistenceManager pm = new FilePersistenceManager();
    private Map<String, Benutzer> benutzerMap = new HashMap<>();

    public BenutzerVW(){
        try {
            this.benutzerMap = this.pm.ladeBenutzer();

            for (Benutzer benutzer : benutzerMap.values()) {
                nextId = Math.max(nextId, benutzer.getBenutzerId() + 1);
            }
        }catch (Exception e){
            benutzerMap = new HashMap<>();
        }
    }

    public boolean registrieren(Benutzer benutzer) {
        if (this.benutzerMap.containsKey(benutzer.getBenutzerErkennung())) {
            System.out.print("Benutzer existiert bereit! ");
            System.out.println("Bitte loggen Sie sich ein!");
            return false;
        }
        this.benutzerMap.put(benutzer.getBenutzerErkennung(), benutzer);
        try {
            pm.speicherBenutzer(benutzer);
        } catch (Exception e) {
            System.out.println("Fehler beim Speichern!");
            e.printStackTrace();
        }
        System.out.println("✔ Registrierung erfolgreich!");
        return true;

    }

    public boolean login(String benutzerErkennung, String benutzerPassword) {
        Benutzer benutzer = (Benutzer)this.benutzerMap.get(benutzerErkennung);
        if (benutzer != null && benutzer.checkPassword(benutzerPassword)) {
            this.aktuellerBenutzer = benutzer;
            System.out.println("Login erfogreich.");
            return true;
        } else {
            System.out.println("Falsher Benutzername oder Password.");
            return false;
        }
    }

    public void logout() {
        if (this.aktuellerBenutzer != null) {
            System.out.println(this.aktuellerBenutzer.getBenutzerErkennung() + " wurde ausgelogt.");
            this.aktuellerBenutzer = null;
        } else {
            System.out.println("keine Benutzer eingelogt.");
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
