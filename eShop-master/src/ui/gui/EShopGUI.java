package ui.gui;

import domain.EShop;
import entities.Artikel;
import entities.Kunde;
import entities.Ereignis;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
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

    //mitarbeiter bereich
    private mitarbeiterLoginPanel mitarbeiterLoginPanel;
    private mitarbeiterMainPanel mitarbeiterMainPanel;

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

        //COMBOX ACTION
        mitarbeiterMainPanel.getAktionenComboBox().addActionListener(e -> {
                    String aktionen = (String) mitarbeiterMainPanel
                            .getAktionenComboBox().getSelectedItem();
                    switch (aktionen) {
                        case "a -> Artikel ansehen":
                            ladeArtikelTabelle();
                            break;
                        case "ae -> neue Artikel im Katalog hinzufügen":
                            artikelHinzufuegenDialog();
                            break;
                        case "bv -> Bezeichnung ändern":
                           bezeichnungVerandern();
                            break;
                        case "al -> Artikel aus dem Katalog koplett löschen":
                            artikelLoschen();
                            break;
                        case "bsv -> Bestand ändern":
                            bestandVerandernDialog();
                            break;
                        case "pv -> Preis ändern":
                            preisAndernDialog();
                            break;
                        case "e -> Ereignisse ändern":
                            System.out.println(eShop.gibEreignissen().size());
                             zeigeEreignisseTabelle();
                            break;
                        case "es -> Ereignisse speichern":
                            speichereEreignisseDialog();
                            break;
                        case "bh -> Bestandhistorie anzeigen":
                           bestandHistorieDialog();
                            break;
                        case "rm -> neue Mitarbeiter registrieren":
                           mitarbeiterRegistrierenDialog();
                            break;
                        case "s -> Daten speichern":
                           datenSpeichernDialog();
                            break;

                    }

                });


        // PANELS ZUM CARDLAYOUT HINZUFÜGEN
        mainPanel.add(loginPanel, "LOGIN");
        mainPanel.add(registrierenPanel, "REGISTER");
        mainPanel.add(kundenPanel, "KUNDE");

        //mitarbeiter bereich
        mainPanel.add(mitarbeiterLoginPanel, "MIT_LOGIN");
        mainPanel.add(mitarbeiterMainPanel, "MITARBEITER");

        // CardLayout in das Fenster einfügen
        add(mainPanel, BorderLayout.CENTER);

        //artikel loschen
        mitarbeiterMainPanel.getLoschenButton().addActionListener(e ->{
            artikelLoschen();
        });

        //such panel mitarbeiter
        mitarbeiterMainPanel.getSuchenButton().addActionListener(e -> {
            String query = mitarbeiterMainPanel.getSucheField().getText().trim().toLowerCase();
            if (query.isEmpty()){
                ladeArtikelTabelle();
                return;
            }
           DefaultTableModel model = mitarbeiterMainPanel.getArtikelTabelleModel();
            model.setRowCount(0);
            for (int id : eShop.gibArtikelListe().keySet()){
                Artikel a = eShop.gibArtikelListe().get(id);
                if (a.getBezeichnung().toLowerCase().contains(query)){
                    model.addRow(new Object[]{
                            a.getArtikelID(),
                            a.getBezeichnung(),
                            eShop.gibArtikelMengeListe().get(id),
                            a.getPreis()
                    });
                }
            }
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
        loginPanel.getLoginButton()
                .addActionListener(e -> {
                    String benutzername = loginPanel.getBenutzername();
                    String passwort = loginPanel.getPasswort();
                    boolean erfolgreich = eShop.login(benutzername, passwort);
                    if (erfolgreich) {
                        JOptionPane.showMessageDialog(this, "Login erfolgreich!");

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
                    float p = Float.parseFloat(preisField.getText().trim());
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
                    float p = Float.parseFloat(preisField.getText().trim());
                    int m = Integer.parseInt(bestandField.getText().trim());
                    int pg = Integer.parseInt(packungField.getText().trim());

                    if (m % 2 != 0) {
                        JOptionPane.showMessageDialog(
                                this,
                                "Bestand muss durch 18 teilbar sein!",
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
    //artikel loschen auf dem tablle
    private void artikelLoschen(){
        String bezeichnung = JOptionPane.showInputDialog(this,
                "Gib Bezeinung des Artikels");
        if (bezeichnung == null || bezeichnung.trim().isEmpty()) {
            return;
        }
        bezeichnung = bezeichnung.trim();
        Integer artikelIdGefunden = null;
        for (int id : eShop.gibArtikelListe().keySet()) {
            Artikel a = eShop.gibArtikelListe().get(id);
            if (a.getBezeichnung().equalsIgnoreCase(bezeichnung)) {
                artikelIdGefunden= id;
                break;
            }
        }
        if (artikelIdGefunden == null) {
            JOptionPane.showMessageDialog(this, "Artikel nicht gefunden!");
            return;
        }
        try {
            eShop.artikelVernichten(artikelIdGefunden);
            ladeArtikelTabelle();
            JOptionPane.showMessageDialog(this, "Artikel wurde erfolgreich gelöscht!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Fehler beim Löschen!");
        }
    }
    private void bezeichnungVerandern(){
        JPanel panelBezeichnung = new JPanel(new BorderLayout(5,5));
        JTextField bezeichnungSuchField = new JTextField();
        DefaultComboBoxModel <String> model = new DefaultComboBoxModel<>();
        JComboBox<String> artikelBox = new JComboBox<>(model);

        for (int id : eShop.gibArtikelListe().keySet()) {
            Artikel a = eShop.gibArtikelListe().get(id);
            model.addElement(id + "-" + a.getBezeichnung());
        //suche im bv bereich
        bezeichnungSuchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void filter(){
                String texte = bezeichnungSuchField.getText().trim().toLowerCase();
                model.removeAllElements();
                for (int id : eShop.gibArtikelListe().keySet()) {
                    Artikel a = eShop.gibArtikelListe().get(id);
                    if (String.valueOf(id).contains(texte) || a.getBezeichnung().toLowerCase().contains(texte)){
                        model.addElement(id + "-" + a.getBezeichnung());
                    }
                }
            }
            public void insertUpdate(javax.swing.event.DocumentEvent e) {filter();}
            public void removeUpdate(javax.swing.event.DocumentEvent e) {filter();}
            public void changedUpdate(javax.swing.event.DocumentEvent e) {filter();}
        });
        panelBezeichnung.add(new JLabel("Suche bein Id oder Bezeichnung:"),BorderLayout.NORTH);
        panelBezeichnung.add(bezeichnungSuchField, BorderLayout.CENTER);
        panelBezeichnung.add(artikelBox, BorderLayout.SOUTH);

        int artikelImListe = JOptionPane.showConfirmDialog(
                this,
                panelBezeichnung,
                "Artikel auswählen:",
                JOptionPane.OK_CANCEL_OPTION
        );
        if (artikelImListe != JOptionPane.OK_OPTION)
            return;
           String ausgewählt = (String) artikelBox.getSelectedItem();

           if (ausgewählt == null) return;

           String neueBezeichnung = JOptionPane.showInputDialog(
                   this,
                   "neue Bezeichnung:"
           );
           if (neueBezeichnung == null || neueBezeichnung.trim().isEmpty()) return;
           try {
               eShop.bezeichnungVeraendern(id, neueBezeichnung.trim());
               ladeArtikelTabelle();
               JOptionPane.showMessageDialog(this, "Bezeichnung erfolgreich geändert!");
           } catch (Exception ex) {
               JOptionPane.showMessageDialog(this, "Fehler beim ändern!");
           }
        }

    }
    //bestand andern
    private void bestandVerandernDialog(){
        JTextField bezeichnungField = new JTextField();
        JTextField bestandField = new JTextField();

        Object[] felder = {
                "Bezeichnung: ", bezeichnungField,
                "Neuer Bestand:", bestandField
        };
        int bestandResult = JOptionPane.showConfirmDialog(
                this, felder, "Bestand ändern",
                JOptionPane.OK_CANCEL_OPTION
        );
        if (bestandResult != JOptionPane.OK_OPTION){
            return;
        }
        try {
            String bezeichnung = bezeichnungField.getText().trim();
            int neuerBestand = Integer.parseInt(bestandField.getText().trim());
            if (bezeichnung.isEmpty() || neuerBestand < 0){
                JOptionPane.showMessageDialog(this,
                        "Ungültige Eingabe!:");
                return;
            }
            Integer gefundeId = null;
            for (int id : eShop.gibArtikelListe().keySet()) {
                Artikel a = eShop.gibArtikelListe().get(id);
                if (a.getBezeichnung().equalsIgnoreCase(bezeichnung)) {
                    gefundeId = id;
                    break;
                }
            }
            if (gefundeId == null){
                JOptionPane.showMessageDialog(this,
                        "Artikel nicht gefunden!");
                return;
            }
            eShop.bestandVeraendern(
                    gefundeId,
                    neuerBestand,
                    eShop.aktuellerBenutzer().getBenutzerErkennung()
            );
            ladeArtikelTabelle();
            JOptionPane.showMessageDialog(this,
                    "Bezeichnung erfolgreich geändert!");
        }catch (NumberFormatException ex){
            JOptionPane.showMessageDialog(this,
                    "Bitte gültige Zahl eingeben!");
        }catch (Exception ex){
            JOptionPane.showMessageDialog(this,
                    "Fehler beim ändern");
        }
    }
  //preis ändern
    private void preisAndernDialog(){
        String[] items = eShop.gibArtikelListe().values().stream()
                .map(a -> a.getArtikelID() + " - " + a.getBezeichnung())
                .toArray(String[]::new);

        String ausgewählteArtikel = (String) JOptionPane.showInputDialog(
                this,
                "Artikel auswählen:",
                "Preis ändern",
                JOptionPane.QUESTION_MESSAGE,
                null,
                items,
                items[0]
        );

        if (ausgewählteArtikel  == null) return;

        int id = Integer.parseInt(ausgewählteArtikel.split(" - ")[0]);

        JTextField newPrice = new JTextField();

        int ok = JOptionPane.showConfirmDialog(
                this,
                new Object[]{"Neuer Preis:", newPrice},
                "Preis ändern",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (ok == JOptionPane.OK_OPTION) {
            try {
                float preis = Float.parseFloat(newPrice.getText());

                eShop.preisVeraendern(id, preis);

                ladeArtikelTabelle();
                JOptionPane.showMessageDialog(this, "Preis geändert!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Fehler!");
            }
        }
    }
    // ereignisse tabelle hinzufügen
    private void zeigeEreignisseTabelle() {

        JDialog ereignisDialog =  new JDialog(this,
                "Ereignisse", true);
        ereignisDialog.setSize(700, 400);
        ereignisDialog.setLocationRelativeTo(this);
        String[] cols = {"Tag", "Artikel", "Typ", "Menge", "Person"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);

        JScrollPane scrollPane = new JScrollPane(table);
        ereignisDialog.add(scrollPane);
        for (Ereignis e : eShop.gibEreignissen()) {
            model.addRow(new Object[]{
                    e.getTag(),
                    e.getArtikel().getBezeichnung(),
                    e.getTyp(),
                    e.getMenge(),
                    e.getPerson()
            });
        }

        ereignisDialog.setVisible(true);
    }
    private void speichereEreignisseDialog() {
        try {
            eShop.speichereEreignisse();

            JOptionPane.showMessageDialog(
                    this,
                    "Ereignisse erfolgreich gespeichert!",
                    "Erfolg",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Fehler beim Speichern!",
                    "Fehler",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
    //bestand histori zeigen
    private void bestandHistorieDialog() {

        //  Artikel auswahlen
        String[] items = eShop.gibArtikelListe().values().stream()
                .map(a -> a.getArtikelID() + " - " + a.getBezeichnung())
                .toArray(String[]::new);

        String selected = (String) JOptionPane.showInputDialog(
                this,
                "Artikel auswählen:",
                "Bestandhistorie",
                JOptionPane.QUESTION_MESSAGE,
                null,
                items,
                items.length > 0 ? items[0] : null
        );

        if (selected == null) return;

        int artikelID = Integer.parseInt(selected.split(" - ")[0]);

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

        // Dialog anzeigen
        JDialog dialog = new JDialog(this, "Bestandhistorie", true);
        dialog.setSize(500, 300);
        dialog.setLocationRelativeTo(this);
        dialog.add(scrollPane);

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

            Kunde mitarbeiter = new Kunde(
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
    //alle daten speichern
    private void datenSpeichernDialog() {

        try {
            //  Artikel speichern
            eShop.speichereArtikel();

            //  Ereignisse speichern
            eShop.speichereEreignisse();

            JOptionPane.showMessageDialog(
                    this,
                    "Alle Daten erfolgreich gespeichert!",
                    "Erfolg",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Fehler beim Speichern der Daten!",
                    "Fehler",
                    JOptionPane.ERROR_MESSAGE
            );
        }
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