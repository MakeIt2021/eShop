package entities;

public class Mitarbeiter extends Benutzer{
    public Mitarbeiter(int benutzerId, String benutzerErkennung, String benutzerVorNachname, String benutzerPassword){
        super(benutzerId, benutzerErkennung, benutzerVorNachname, benutzerPassword);
    }

    @Override
    public String getRole() {
        return "Mitarbeiter";
    }
}