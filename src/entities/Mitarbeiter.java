//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package entities;

public class Mitarbeiter extends Benutzer {
    public Mitarbeiter(int benutzerId, String benutzerErkennung, String benutzerVorNachname, String benutzerPassword) {
        super(benutzerId, benutzerErkennung, benutzerVorNachname, benutzerPassword);
    }

    public String getRole() {
        return "Mitarbeiter";
    }
}
