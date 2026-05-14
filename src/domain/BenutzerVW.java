//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package domain;

import entities.Benutzer;
import entities.Kunde;
import entities.Mitarbeiter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BenutzerVW {
    private final String datei = "benutzer.txt";
    private Map<String, Benutzer> benutzerMap = new HashMap();
    private Benutzer aktuellerBenutzer;

    public BenutzerVW() {
        this.ladeBenutzer();
    }

    public boolean registrieren(Benutzer benutzer) {
        if (this.benutzerMap.containsKey(benutzer.getBenutzerErkennung())) {
            System.out.print("Benutzer existiert bereit! ");
            System.out.println("Bitte loggen Sie sich ein!");
            return false;
        } else {
            this.benutzerMap.put(benutzer.getBenutzerErkennung(), benutzer);
            this.speicherBenutzer(benutzer);
            System.out.println("✔ Registrierung erfolgreich!");
            return true;
        }
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

    public Benutzer getAktuellerBenutzer() {
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
            BufferedWriter writer = new BufferedWriter(new FileWriter("benutzer.txt", true));
            int var10001 = benutzer.getBenutzerId();
            writer.write(var10001 + ";" + benutzer.getBenutzerErkennung() + ";" + benutzer.getBenutzerVorNachname() + ";" + benutzer.getBenutzerPassword() + ";" + benutzer.getRole());
            writer.newLine();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void ladeBenutzer() {
        try {
            BufferedReader reader;
            String zeile;
            String erkennung;
            Benutzer benutzer;
            for(reader = new BufferedReader(new FileReader("benutzer.txt")); (zeile = reader.readLine()) != null; this.benutzerMap.put(erkennung, benutzer)) {
                String[] daten = zeile.split(";");
                int id = Integer.parseInt(daten[0]);
                erkennung = daten[1];
                String name = daten[2];
                String password = daten[3];
                String rolle = daten[4];
                if (rolle.equals("kunde")) {
                    benutzer = new Kunde(id, erkennung, name, password);
                } else {
                    benutzer = new Mitarbeiter(id, erkennung, name, password);
                }
            }

            reader.close();
        } catch (Exception var10) {
            System.out.println("keine Datei gefunfen.");
        }

    }
}
