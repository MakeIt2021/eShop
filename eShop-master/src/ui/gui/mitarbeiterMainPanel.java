package ui.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;


public class mitarbeiterMainPanel extends JPanel {
    private JLabel mitarbeiterLabel;
    private JComboBox <String> aktionenComboBox;
    private JTextField sucheField;
    private JButton suchenButton;
    private JButton logoutButton;
    private JButton loschenButton;
    private JTable ereignisTable;
    private DefaultTableModel ereignisModel;

    //mitarbeiter artikel tabelle "bestand"
    private JTable artikelTabelle;
    private DefaultTableModel artikelTabelleModel;

    public mitarbeiterMainPanel(){
        setLayout(new BorderLayout());
        setBackground(new Color(255, 255, 255));

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setBackground(new Color(255, 255, 255));
     //titel label
        JLabel eShopLabel = new JLabel("eShop - Mitarbeiter");
        eShopLabel.setFont(new Font("Arial", Font.BOLD, 26));
        eShopLabel.setHorizontalAlignment(SwingConstants.CENTER);
        northPanel.add(eShopLabel, BorderLayout.NORTH);
     //loschen object
        loschenButton = new JButton("löschen");
     //toolbar label
     JPanel toolbarPanel = new JPanel(new BorderLayout(10, 10));
     toolbarPanel.setBackground(new Color(255, 255, 255));
     //aktionen
     String aktionen[] = {
             "a -> Artikel ansehen",
             "ae -> neue Artikel im Katalog hinzufügen",
             "bv -> Bezeichnung ändern",
             "al -> Artikel aus dem Katalog koplett löschen",
             "bsv -> Bestand ändern",
             "pv -> Preis ändern",
             "e -> Ereignisse ändern",
             "es -> Ereignisse speichern",
             "bh -> Bestandhistorie anzeigen",
             "rm -> neue Mitarbeiter registrieren",
             "s -> Daten speichern",
     };
     aktionenComboBox = new JComboBox<>(aktionen);
     toolbarPanel.add(aktionenComboBox, BorderLayout.WEST);

     // such bereich panel
    JPanel suchpanel = new JPanel();
    suchpanel.setBackground(new Color(255, 255, 255));
    suchpanel.add(new JLabel("Artikel suchen:"));
    sucheField = new JTextField(15);
    suchpanel.add(sucheField);
    suchenButton = new JButton("Suchen");
    suchpanel.add(suchenButton);
    toolbarPanel.add(suchpanel, BorderLayout.EAST);

    northPanel.add(toolbarPanel, BorderLayout.SOUTH);
    add(northPanel, BorderLayout.NORTH);

    String [] spalten = {
            "ID",
            "Bezeichnung",
            "Bestand",
            "Preis"

        };
        artikelTabelleModel = new DefaultTableModel(spalten,0);
        artikelTabelle = new JTable(artikelTabelleModel);
        JScrollPane sp = new JScrollPane(artikelTabelle);
        add(sp, BorderLayout.CENTER);

    //south bereich
    JPanel southPanel = new  JPanel(new BorderLayout());
    southPanel.setBackground(new Color(242, 246, 252));
     mitarbeiterLabel = new JLabel("Mitarbeiter: nicht angemeldet");
     southPanel.add(mitarbeiterLabel, BorderLayout.WEST);

     logoutButton = new JButton("Logout");
     southPanel.add(logoutButton, BorderLayout.EAST);
     add(southPanel, BorderLayout.SOUTH);

    }
    public DefaultTableModel getArtikelTabelleModel() {
        return artikelTabelleModel;
    }

    public JTable getArtikelTabelle() {
        return artikelTabelle;
    }

    public JButton getLogoutButton() {
        return logoutButton;
    }

    public JButton getSuchenButton() {
        return suchenButton;
    }

    public JTextField getSucheField() {
        return sucheField;
    }

    public JComboBox<String> getAktionenComboBox() {
        return aktionenComboBox;
    }

    public JLabel getMitarbeiterLabel() {
        return mitarbeiterLabel;
    }
    public void setMitarbeiterName(String name){
        mitarbeiterLabel.setText("Mitarbeiter: " + name);
    }
    public JButton getLoschenButton() {
        return loschenButton;
    }
    public JTable getEreignisTable() {
        return ereignisTable;
    }

    public DefaultTableModel getEreignisModel() {
        return ereignisModel;
    }
}
