package entities;

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
}
