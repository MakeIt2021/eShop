package eShop.common.entities;

import java.util.regex.Pattern;

public abstract class Benutzer {
    private int benutzerId;
    private String benutzerErkennung;
    private String benutzerVorNachname;
    private String benutzerPassword;

    public Benutzer(int benutzerId, String benutzerErkennung, String benutzerVorNachname, String benutzerPassword) {
        this.benutzerId = benutzerId;
        this.benutzerErkennung = benutzerErkennung;
        this.benutzerVorNachname = benutzerVorNachname;
        this.benutzerPassword = benutzerPassword;
    }

    public String getBenutzerErkennung() {
        return this.benutzerErkennung;
    }

    public void setBenutzerErkennung(String benutzerErkennung) {
        this.benutzerErkennung = benutzerErkennung;
    }

    public void setBenutzerVorNachname(String benutzerVorNachname) {
        this.benutzerVorNachname = benutzerVorNachname;
    }

    public boolean checkPassword(String benutzerPassword) {
        return this.benutzerPassword.equals(benutzerPassword);
    }

    public String getBenutzerPassword() {
        return this.benutzerPassword;
    }

    public void setBenutzerPassword(String benutzerPassword) {
        this.benutzerPassword = benutzerPassword;
    }

    public String getBenutzerVorNachname() {
        return this.benutzerVorNachname;
    }

    public int getBenutzerId() {
        return this.benutzerId;
    }

    public void setBenutzerId(int benutzerId) {
        this.benutzerId = benutzerId;
    }

    public abstract String getRole();

    public String toNetworkString() {
        return benutzerId + ";" + benutzerErkennung + ";" + benutzerVorNachname + ";" + benutzerPassword + ";" + getRole();
    }

    public static Benutzer fromNetworkString(String line) {
        if (line == null || line.equals("null"))
            return null;

        String[] parts = line.split(";");
        int id = Integer.parseInt(parts[0]);
        String benutzerErkennung = parts[1];
        String benutzerVorNachName = parts[2];
        String benutzerPasswort = parts[3];
        String role = parts[4];

        if (role.equals("Kunde")) {
            return new Kunde(id, benutzerErkennung, benutzerVorNachName, benutzerPasswort);
        } else {
            return new Mitarbeiter(id, benutzerErkennung, benutzerVorNachName, benutzerPasswort);
        }
    }


}
