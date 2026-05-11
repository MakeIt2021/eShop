package domain;
import entities.Benutzer;

import java.util.HashMap;
import java.util.Map;
public class BenutzerVW {
    private Map<String, Benutzer> benutzerMap = new HashMap<>();
    private Benutzer aktuellerBenutzer;

    public void registrieren(Benutzer benutzer){
        if (benutzerMap.containsKey(benutzer.getBenutzerVorNachname())){
            throw new IllegalArgumentException("Benutzer existiert bereit!");
        }
        benutzerMap.put(benutzer.getBenutzerVorNachname(), benutzer);
        System.out.println("Registrierung erfolgreich");
    }
    public boolean login(String benutzerName, String benutzerPassword){
       Benutzer benutzer = benutzerMap.get(benutzerName);
        if (benutzer != null && benutzer.checkPassword(benutzerPassword)) {
            aktuellerBenutzer = benutzer;
            System.out.println("Login erfogreich.");
            return true;
        }
        System.out.println("Falsher Benutzername oder Password.");
        return false;
    }
    public void logout(){
        if(aktuellerBenutzer != null){
            System.out.println(aktuellerBenutzer.getBenutzerVorNachname() + "wurde ausgelogt.");
            aktuellerBenutzer = null;
        }else{
            System.out.println("keine Benutzer eingelogt.");
        }
    }
    public void kontoErstellen(){
        if (aktuellerBenutzer !=null){
            System.out.println("konto für " + aktuellerBenutzer.getBenutzerVorNachname()
                    + "wurde erstellt.");
        }else{
            System.out.println("Bitte zuerst einloggen");
        }
    }
    public Benutzer getAktuellerBenutzer(){
        return aktuellerBenutzer;
    }
    public boolean istEingeloggt(){
        return aktuellerBenutzer !=null;
    }
    public boolean istMitarbeiter(){
        return aktuellerBenutzer != null &&
                aktuellerBenutzer.getRole().equals("Mitarbeiter");
    }
    public boolean istKunde(){
        return aktuellerBenutzer !=null &&
                aktuellerBenutzer.getRole().equals("Kunde");
    }
}