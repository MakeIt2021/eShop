//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package entities;

public class Kunde extends Benutzer {
    public Kunde(int benutzerId, String benutzerErkennung, String benutzerVorNachname, String benutzerPassword) {
        super(benutzerId, benutzerErkennung, benutzerVorNachname, benutzerPassword);
    }

    public String getRole() {
        return "Kunde";
    }
}
