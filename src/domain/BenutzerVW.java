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

    public void speicherBenutzer(Benutzer benutzer) {
            try {
                BufferedWriter writer = new BufferedWriter(new
                        FileWriter("benutzer.txt", true));
                int var10001 = benutzer.getBenutzerId();
                writer.write(var10001 + ";" + benutzer.getBenutzerErkennung()
                        + ";" + benutzer.getBenutzerVorNachname() + ";" +
                        benutzer.getBenutzerPassword() + ";" + benutzer.getRole());
                writer.newLine();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

    }

    // TODO: Jetzt es gibt das Problem, dass Kunde wird als Mitarbeiter gesehen, nachdem die Daten gelesen werden = done
    public void ladeBenutzer() {
        File file = new File(datei);
        if (!file.exists()) {
            System.out.println("Keine Benutzer Datei gefunden");
        }
        try {
            BufferedReader reader;
            String zeile;
            String erkennung;
            Benutzer benutzer = null;
            for(reader = new BufferedReader(new FileReader("benutzer.txt")); (zeile = reader.readLine()) != null; this.benutzerMap.put(erkennung, benutzer)) {
                String[] daten = zeile.split(";");
                int id = Integer.parseInt(daten[0]);
                nextId = Math.max(nextId, id + 1);
                erkennung = daten[1];
                String name = daten[2];
                String password = daten[3].trim();
                String rolle = daten[4].trim();
                if (rolle.equalsIgnoreCase("kunde")) {
                    benutzer = new Kunde(id, erkennung, name, password);
                } else if(rolle.equalsIgnoreCase("mitarbeiter")) {
                    benutzer = new Mitarbeiter(id, erkennung, name, password);
                }else {
                    System.out.println("Unbekannte Rolle:" + rolle);
                }
            }

            reader.close();
        } catch (Exception var10) {
            System.out.println("keine Datei gefunfen.");
        }
    }
    //Id generiere method
    public int generiereId(){
        return nextId++;
    }
}
