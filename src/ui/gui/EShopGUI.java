package ui.gui;

import domain.EShop;
import entities.Kunde;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class EShopGUI extends JFrame{

    // Zentrale Geschäftslogik des eShops
    private static EShop eShop;

    static {
        try {
            eShop = new EShop();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Login-Bildschirm
    private LoginPanel loginPanel;

    // Registrierungsbildschirm
    private RegistrierenPanel registrierenPanel;

    // Kundenbereich nach erfolgreichem Login
    private KundenPanel kundenPanel;

    // Verwaltet die verschiedenen Bildschirme
    private CardLayout cardLayout;

    // Enthält alle Panels des CardLayouts
    private JPanel mainPanel;

    public EShopGUI() throws IOException {

        // ESHOP INITIALISIEREN
        eShop = new EShop();

        // FENSTER KONFIGURIEREN
        setTitle("eShop");

        setSize(900, 600);
        setLocationRelativeTo(null);

        // Programm beim Schließen beenden
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Hauptlayout des Fensters
        setLayout(new BorderLayout());

        // Hintergrundfarbe
        getContentPane().setBackground(new Color(119, 135, 145));

        // CARDLAYOUT ERZEUGEN
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // PANELS ERZEUGEN
        loginPanel = new LoginPanel();
        registrierenPanel = new RegistrierenPanel();
        kundenPanel = new KundenPanel();

        // PANELS ZUM CARDLAYOUT HINZUFÜGEN
        mainPanel.add(loginPanel, "LOGIN");
        mainPanel.add(registrierenPanel, "REGISTER");
        mainPanel.add(kundenPanel, "KUNDE");

        // CardLayout in das Fenster einfügen
        add(mainPanel, BorderLayout.CENTER);

        // LOGIN
        loginPanel.getLoginButton()
                .addActionListener(e -> {
                    String benutzername = loginPanel.getBenutzername();
                    String passwort = loginPanel.getPasswort();
                    boolean erfolgreich = eShop.login(benutzername, passwort);
                    if (erfolgreich) {
                        JOptionPane.showMessageDialog(this, "Login erfolgreich!");

                        // Kunde eingeloggt
                        if (eShop.istKunde()) {
                            kundenPanel.setKundeName(eShop.aktuellerBenutzer().getBenutzerVorNachname());
                            // Kunde Fenster maximieren
                            setExtendedState(JFrame.MAXIMIZED_BOTH);
                            cardLayout.show(mainPanel, "KUNDE");
                        }
                    } else {
                        JOptionPane.showMessageDialog(
                                this,
                                "Benutzername oder Passwort falsch.",
                                "Fehler",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                });

        // ZUR REGISTRIERUNG WECHSELN
        loginPanel.getRegistrierenButton()
                .addActionListener(e -> {
                    cardLayout.show(mainPanel, "REGISTER");
                });

        // ZUM LOGIN ZURÜCKKEHREN
        registrierenPanel.getZurueckButton()
                .addActionListener(e -> {
                    cardLayout.show(mainPanel, "LOGIN");
                });

        // NEUEN KUNDEN REGISTRIEREN
        registrierenPanel.getRegistrierenButton()
                .addActionListener(e -> {

                    // Neue Benutzer-ID erzeugen
                    int benutzerId = eShop.getBenutzerVW().generiereId();

                    // Kundenobjekt erzeugen
                    Kunde kunde = new Kunde(
                                    benutzerId,
                                    registrierenPanel.getBenutzername(),
                                    registrierenPanel.getName(),
                                    registrierenPanel.getPasswort()
                    );

                    // Registrierung durchführen
                    boolean erfolgreich = eShop.registrieren(kunde);
                    if (erfolgreich) {
                        JOptionPane.showMessageDialog(
                                this,
                                "Registrierung erfolgreich!"
                        );
                        cardLayout.show(mainPanel, "LOGIN");
                    } else {
                        JOptionPane.showMessageDialog(
                                this,
                                "Benutzer existiert bereits.",
                                "Fehler",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                });

        // LOGOUT
        kundenPanel.getLogoutButton()
                .addActionListener(e -> {
                    eShop.logout();
                    // Fenster wieder auf Standardgröße setzen
                    setExtendedState(JFrame.NORMAL);
                    setSize(900, 600);
                    setLocationRelativeTo(null);
                    cardLayout.show(mainPanel, "LOGIN");
                });

        setVisible(true);
    }

    // PROGRAMMSTART
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new EShopGUI();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}