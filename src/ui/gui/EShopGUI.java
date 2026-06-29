package ui.gui;

import domain.EShop;
import domain.exceptions.BestandNichtAusreichendException;
import domain.exceptions.MassengutartikelmengeNichtTeilbarException;
import domain.exceptions.MengeWenigerAlsPackungGroesseException;
import domain.exceptions.UngueltigeMengeException;
import entities.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
import java.math.BigDecimal;
import java.util.ArrayList;
import javax.swing.event.DocumentListener;


public class EShopGUI extends JFrame {

    // Zentrale Geschäftslogik des eShops
    private EShop eShop;

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
    private mitarbeiterLoginPanel mitarbeiterLoginPanel;
    private mitarbeiterMainPanel mitarbeiterMainPanel;

    private JButton warenkorbButton;

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
        //mitarbeiter bereich
        mitarbeiterLoginPanel = new mitarbeiterLoginPanel();
        mitarbeiterMainPanel = new mitarbeiterMainPanel();


        // PANELS ZUM CARDLAYOUT HINZUFÜGEN
        mainPanel.add(loginPanel, "LOGIN");
        mainPanel.add(registrierenPanel, "REGISTER");
        mainPanel.add(kundenPanel, "KUNDE");

        //mitarbeiter bereich
        mainPanel.add(mitarbeiterLoginPanel, "MIT_LOGIN");
        mainPanel.add(mitarbeiterMainPanel, "MITARBEITER");

        // CardLayout in das Fenster einfügen
        add(mainPanel, BorderLayout.CENTER);

        mitarbeiterMainPanel.getSucheField().getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { mitarbeiterMainPanel.filtereArtikel(mitarbeiterMainPanel.getSucheField().getText().trim()); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { mitarbeiterMainPanel.filtereArtikel(mitarbeiterMainPanel.getSucheField().getText().trim()); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { mitarbeiterMainPanel.filtereArtikel(mitarbeiterMainPanel.getSucheField().getText().trim()); }
        });

        mitarbeiterMainPanel.getArtikelHinzufuegenButton().addActionListener(e -> {
            artikelHinzufuegenDialog();
        });

        mitarbeiterMainPanel.getArtikelVeraendernButton().addActionListener(e -> {
            int selectedID = getArtikelIDFromRow(mitarbeiterMainPanel.getArtikelTabelle());
            if (selectedID == -1)
                return;

            ArtikelVeraendernDialog dialog = new ArtikelVeraendernDialog(this, eShop, this::ladeArtikelTabelle, selectedID);
            dialog.setVisible(true);
        });

        mitarbeiterMainPanel.getHistorieButton().addActionListener(e -> {
            int selectedID = getArtikelIDFromRow(mitarbeiterMainPanel.getArtikelTabelle());
            if (selectedID == -1)
                return;

            bestandHistorieDialog(selectedID);
        });

        mitarbeiterMainPanel.getEreignisseButton().addActionListener(e -> {
            zeigeEreignisseTabelle();
        });

        mitarbeiterMainPanel.getMitarbeiterRegButton().addActionListener(e -> {
            mitarbeiterRegistrierenDialog();
        });

        //logout fur mitarbeiter
        mitarbeiterMainPanel.getLogoutButton().addActionListener(e -> {
            eShop.logout();
            setExtendedState(JFrame.NORMAL);
            setSize(900, 600);
            setLocationRelativeTo(null);
            cardLayout.show(mainPanel, "LOGIN");
        });

        // LOGIN
        loginPanel.getLoginButton().addActionListener(e -> {
            String benutzername = loginPanel.getBenutzername();
            String passwort = loginPanel.getPasswort();
            boolean erfolgreich = eShop.login(benutzername, passwort);
            if (erfolgreich) {
                //mitarbeiter login flow
                if (eShop.istMitarbeiter()){
                    mitarbeiterMainPanel.setMitarbeiterName(
                            eShop.aktuellerBenutzer().getBenutzerErkennung()
                    );
                    ladeArtikelTabelle();
                    cardLayout.show(mainPanel, "MITARBEITER");
                }

                // Kunde eingeloggt
                if (eShop.istKunde()) {
                    kundenPanel.setKundeName(eShop.aktuellerBenutzer().getBenutzerVorNachname());
                    // Artikelliste laden
                    kundenPanel.zeigeArtikelListe(eShop.gibArtikelListe());
                    // Kunde Fenster maximieren
                    setExtendedState(JFrame.MAXIMIZED_BOTH);
                    cardLayout.show(mainPanel, "KUNDE");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Benutzername oder Passwort falsch.", "Fehler", JOptionPane.ERROR_MESSAGE);
            }
        });

        // ZUR REGISTRIERUNG WECHSELN
        loginPanel.getRegistrierenButton().addActionListener(e -> {
            cardLayout.show(mainPanel, "REGISTER");
        });

        // ZUM LOGIN ZURÜCKKEHREN
        registrierenPanel.getZurueckButton().addActionListener(e -> {
            cardLayout.show(mainPanel, "LOGIN");
        });

        // NEUEN KUNDEN REGISTRIEREN
        registrierenPanel.getRegistrierenButton().addActionListener(e -> {

            // Neue Benutzer-ID erzeugen
            int benutzerId = eShop.getBenutzerVW().generiereId();

            // Kundenobjekt erzeugen
            Kunde kunde = new Kunde(benutzerId, registrierenPanel.getBenutzername(), registrierenPanel.getName(), registrierenPanel.getPasswort());

            // Registrierung durchführen
            boolean erfolgreich = eShop.registrieren(kunde);
            if (erfolgreich) {
                JOptionPane.showMessageDialog(this, "Registrierung erfolgreich!");
                cardLayout.show(mainPanel, "LOGIN");
            } else {
                JOptionPane.showMessageDialog(this, "Benutzer existiert bereits.", "Fehler", JOptionPane.ERROR_MESSAGE);
            }
        });

        // LOGOUT
        kundenPanel.getLogoutButton().addActionListener(e -> {
            eShop.logout();
            // Fenster wieder auf Standardgröße setzen
            setExtendedState(JFrame.NORMAL);
            setSize(900, 600);
            setLocationRelativeTo(null);
            cardLayout.show(mainPanel, "LOGIN");
        });

        kundenPanel.getSucheField().getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { kundenPanel.filtereArtikel(kundenPanel.getSucheField().getText()); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { kundenPanel.filtereArtikel(kundenPanel.getSucheField().getText()); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { kundenPanel.filtereArtikel(kundenPanel.getSucheField().getText()); }
        });

        // Ausgewählten Artikel in den Warenkorb legen
        kundenPanel.getInWarenkorbButton().addActionListener(e -> {
            // Artikel-ID aus der ersten Spalte lesen
            int artikelID = getArtikelIDFromRow(kundenPanel.getArtikelTable());

            // Menge vom Benutzer abfragen
            String mengeText;
            if (eShop.istMassengutartikel(artikelID)) {
                mengeText = JOptionPane.showInputDialog(this, (eShop.getArtikelName(artikelID) + " ist ein Massengutartikel!\nDas bedeutet, dass die Menge im Warenkorb durch " + eShop.getPackungGroesse(artikelID) + " teilbar sein soll!"));
            } else {
                mengeText = JOptionPane.showInputDialog(this, "Menge eingeben:");
            }

            // Prüfen, ob der Dialog abgebrochen wurde
            if (mengeText == null) {
                return;
            }
            try {
                int menge = Integer.parseInt(mengeText);
                // Artikel wirklich in den Warenkorb legen
                eShop.fuegeInWarenkorb(artikelID, menge, eShop.aktuellerBenutzer().getBenutzerVorNachname());
                JOptionPane.showMessageDialog(this, "Artikel wurde erfolgreich zum Warenkorb hinzugefügt.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
                JOptionPane.showMessageDialog(this, "Bitte eine gültige Zahl eingeben.");
            }
        });

        // Warenkorb anzeigen
        kundenPanel.getWarenkorbButton().addActionListener(e -> {
            WarenkorbDialog dialog = new WarenkorbDialog(this, eShop.gibWarenkorb(), eShop.gibArtikelListe());

            // Menge ändern
            dialog.getMengeAendernButton().addActionListener(event -> {
                int selectedRow = dialog.getWarenkorbTable().getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(this, "Bitte zuerst einen Artikel auswählen.");
                    return;
                }

                int artikelID = (Integer) dialog.getWarenkorbTable().getValueAt(selectedRow, 0);
                int alteMenge = dialog.getAusgewaehlteMenge();
                String neueMengeText = JOptionPane.showInputDialog(this, "Neue Menge:");

                if (neueMengeText == null) {
                    return;
                }

                try {
                    int neueMenge = Integer.parseInt(neueMengeText);
                    String kunde = eShop.aktuellerBenutzer().getBenutzerVorNachname();
                    eShop.loescheAusWarenkorb(artikelID, alteMenge, kunde);
                    eShop.fuegeInWarenkorb(artikelID, neueMenge, kunde);
                    JOptionPane.showMessageDialog(this, "Menge erfolgreich geändert.");
                    dialog.ladeWarenkorbNeu(eShop.gibWarenkorb(), eShop.gibArtikelListe());
                } catch (BestandNichtAusreichendException | MassengutartikelmengeNichtTeilbarException |
                         MengeWenigerAlsPackungGroesseException | UngueltigeMengeException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
                }
            });

            // Artikel vollständig entfernen
            dialog.getArtikelEntfernenButton().addActionListener(event -> {
                int selectedRow = dialog.getWarenkorbTable().getSelectedRow();

                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(this, "Bitte zuerst einen Artikel auswählen.");
                    return;
                }

                int artikelID = (Integer) dialog.getWarenkorbTable().getValueAt(selectedRow, 0);
                int menge = dialog.getAusgewaehlteMenge();

                try {
                    String kunde = eShop.aktuellerBenutzer().getBenutzerVorNachname();
                    eShop.loescheAusWarenkorb(artikelID, menge, kunde);
                    JOptionPane.showMessageDialog(this, "Artikel wurde aus dem Warenkorb entfernt.");
                    dialog.ladeWarenkorbNeu(eShop.gibWarenkorb(), eShop.gibArtikelListe());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
                }
            });

            // Kaufen
            dialog.getKaufenButton().addActionListener(event -> {
                if (eShop.gibWarenkorb().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Der Warenkorb ist leer.");
                    return;
                }

                Rechnung rechnung = new Rechnung(eShop.aktuellerBenutzer().getBenutzerVorNachname(), eShop.gibWarenkorb(), eShop.gibArtikelListe());
                KaufenDialog kaufenDialog = new KaufenDialog(this, rechnung);
                kaufenDialog.zeigeAusgewaehlteArtikel(rechnung);
                kaufenDialog.getBestaetigenButton().addActionListener(kaufEvent -> {
                    eShop.zuruecksetzeWarenkorb();
                    dialog.ladeWarenkorbNeu(eShop.gibWarenkorb(), eShop.gibArtikelListe());
                    JOptionPane.showMessageDialog(this, "Kauf erfolgreich abgeschlossen.");
                    kaufenDialog.dispose();
                });

                kaufenDialog.setVisible(true);
            });

            dialog.setVisible(true);
        });

        setVisible(true);
    }

    private int getArtikelIDFromRow(JTable table) {
        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Bitte wählen Sie zuerst einen Artikel aus der Tabelle aus!",
                    "Kein Artikel ausgewählt",
                    JOptionPane.WARNING_MESSAGE);
            return -1;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);
        return Integer.parseInt(table.getModel().getValueAt(modelRow, 0).toString());
    }


    //lade ARTIKEL PANEL
    private void ladeArtikelTabelle(){
        DefaultTableModel artikelModel = mitarbeiterMainPanel.getArtikelTabelleModel();
        artikelModel.setRowCount(0);
        for (int id : eShop.gibArtikelListe().keySet()) {
            Artikel a = eShop.gibArtikelListe().get(id);
            artikelModel.addRow(new Object[]{
                    a.getArtikelID(),
                    a.getBezeichnung(),
                    eShop.gibArtikelMengeListe().get(id),
                    a.getPreis()
            });

        }
    }

    private void artikelHinzufuegenDialog() {
        String[] optionen = {"Single Artikel", "Massengut Artikel"};

        int wahl = JOptionPane.showOptionDialog(
                this,
                "Welche Art von Artikel möchtest du hinzufügen?",
                "Artikeltyp auswählen",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                optionen,
                optionen[0]
        );

        if (wahl == -1) return;

        // SINGLE ARTIKEL
        if (wahl == 0) {
            JTextField bezeichnungField = new JTextField();
            JTextField preisField = new JTextField();
            JTextField bestandField = new JTextField();

            Object[] felder = {
                    "Bezeichnung:", bezeichnungField,
                    "Preis:", preisField,
                    "Bestand:", bestandField
            };

            int ok = JOptionPane.showConfirmDialog(
                    this,
                    felder,
                    "Neuer Artikel",
                    JOptionPane.OK_CANCEL_OPTION
            );

            if (ok != JOptionPane.OK_OPTION) return;

            try {
                String b = bezeichnungField.getText().trim();
                BigDecimal p = new BigDecimal(preisField.getText().trim());
                int m = Integer.parseInt(bestandField.getText().trim());

                int id = eShop.gibArtikelListe().size();
                while (eShop.gibArtikelListe().containsKey(id)) id++;

                String mitarbeiter = eShop.aktuellerBenutzer().getBenutzerErkennung();

                eShop.fuegeArtikelEin(id, b, m, p, mitarbeiter);

                ladeArtikelTabelle();
                JOptionPane.showMessageDialog(this, "Single Artikel hinzugefügt!");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ungültige Eingabe!");
            }
        }
        // MASSENGUT ARTIKEL
        else {

            JTextField bezeichnungField = new JTextField();
            JTextField preisField = new JTextField();
            JTextField bestandField = new JTextField();
            JTextField packungField = new JTextField();

            Object[] felder = {
                    "Bezeichnung:", bezeichnungField,
                    "Preis:", preisField,
                    "Bestand:", bestandField,
                    "Packungsgröße:", packungField
            };

            int ok = JOptionPane.showConfirmDialog(
                    this,
                    felder,
                    "Massengut Artikel hinzufügen",
                    JOptionPane.OK_CANCEL_OPTION
            );

            if (ok != JOptionPane.OK_OPTION) return;

            try {
                String b = bezeichnungField.getText().trim();
                BigDecimal p = new BigDecimal(preisField.getText().trim());
                int m = Integer.parseInt(bestandField.getText().trim());
                int pg = Integer.parseInt(packungField.getText().trim());

                if (m % pg != 0) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Bestand muss durch " + pg + " teilbar sein!",
                            "Regel verletzt",
                            JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }

                int id = eShop.gibArtikelListe().size();
                while (eShop.gibArtikelListe().containsKey(id)) id++;

                String mitarbeiter = eShop.aktuellerBenutzer().getBenutzerErkennung();

                eShop.fuegeMassengutartikelEin(
                        id,
                        b,
                        m,
                        p,
                        mitarbeiter,
                        pg
                );

                ladeArtikelTabelle();
                JOptionPane.showMessageDialog(this, "Massengut Artikel hinzugefügt!");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Fehler bei Eingabe!");
            }
        }
    }

    // ereignisse tabelle hinzufügen
    private void zeigeEreignisseTabelle() {
        EreignisListeDialog dialog = new EreignisListeDialog(this, eShop);
        dialog.setVisible(true);
    }
    //bestand histori zeigen
    private void bestandHistorieDialog(int artikelID) {
        // Historie holen
        var historie = eShop.berechneBestandHistorie(artikelID);

        // Table bauen
        String[] cols = {"Tag", "Bestand"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);

        for (var entry : historie.entrySet()) {
            model.addRow(new Object[]{
                    entry.getKey(),
                    entry.getValue()
            });
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(150, 0));
        ArrayList<Integer> graphData = new ArrayList<>(historie.values());
        BestandsHistorieGraph graphPanel = new BestandsHistorieGraph(graphData);

        // Dialog anzeigen
        JDialog dialog = new JDialog(this, "Bestandhistorie", true);
        dialog.setSize(800, 400);

        dialog.setLayout(new BorderLayout());

        dialog.add(scrollPane, BorderLayout.WEST);
        dialog.add(graphPanel, BorderLayout.CENTER);

        GraphicsConfiguration gc = this.getGraphicsConfiguration();
        Rectangle bounds = gc.getBounds();
        Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(gc);
        int width = bounds.width - insets.left - insets.right;
        int height = bounds.height - insets.top - insets.bottom;
        dialog.setBounds(0, 0, width, height);

        dialog.setVisible(true);
    }

    //neue mitarbeiter registrieren
    private void mitarbeiterRegistrierenDialog() {
        JTextField benutzernameField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField passwortField = new JTextField();

        Object[] felder = {
                "Benutzername:", benutzernameField,
                "Name:", nameField,
                "Passwort:", passwortField
        };

        int result = JOptionPane.showConfirmDialog(
                this,
                felder,
                "Neuen Mitarbeiter registrieren",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (result != JOptionPane.OK_OPTION) return;

        try {
            String benutzername = benutzernameField.getText().trim();
            String name = nameField.getText().trim();
            String passwort = passwortField.getText().trim();

            if (benutzername.isEmpty() || name.isEmpty() || passwort.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Alle Felder ausfüllen!");
                return;
            }

            int id = eShop.getBenutzerVW().generiereId();

            Mitarbeiter mitarbeiter = new Mitarbeiter(
                    id,
                    benutzername,
                    name,
                    passwort
            );

            boolean success = eShop.registrieren(mitarbeiter);

            if (success) {
                JOptionPane.showMessageDialog(
                        this,
                        "Mitarbeiter erfolgreich registriert!"
                );
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Benutzer existiert bereits!",
                        "Fehler",
                        JOptionPane.ERROR_MESSAGE
                );
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Fehler bei Registrierung!",
                    "Fehler",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // PROGRAMMSTART
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            TestDataGenerator.generiereUmfangreicheTestdaten(new EShop(), "Generator");
            try {
                new EShopGUI();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}