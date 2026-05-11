package entities;

public  abstract class Benutzer {
    private int benutzerId;
    private String benutzerErkennung;
    private String benutzerVorNachname; //warum brauchen wir diese, wenn wir benutzerWrkennung haben?
    private String benutzerPassword;

    public Benutzer(int benutzerId, String benutzerErkennung, String benutzerVorNachname, String benutzerPassword){
        this.benutzerId =benutzerId;
        this.benutzerErkennung = benutzerErkennung;
        this.benutzerVorNachname = benutzerVorNachname;
        this.benutzerPassword = benutzerPassword;
    }

    public String getBenutzerErkennung() {
        return benutzerErkennung;
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
        return benutzerPassword;
    }

    public void setBenutzerPassword(String benutzerPassword) {
        this.benutzerPassword = benutzerPassword;
    }

    public String getBenutzerVorNachname() {
        return benutzerVorNachname;
    }

    public int getBenutzerId() {
        return benutzerId;
    }

    public void setBenutzerId(int benutzerId) {
        benutzerId = benutzerId;
    }
    public abstract String getRole();
}
