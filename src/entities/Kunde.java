package entities;

public class Kunde extends Benutzer {
    public Kunde(int benutzerId, String benutzerErkennung, String benutzerVorNachname, String benutzerPassword){
        super(benutzerId, benutzerErkennung, benutzerVorNachname, benutzerPassword);
    }

    @Override
    public String getRole() {
        return "Kunde";
    }
}