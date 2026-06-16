package ui.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;

public class KundenPanel extends JPanel {

    private JLabel kundeLabel;
    private JButton warenkorbButton;

    // Suchfeld
    private JTextField sucheField;

    // Suchbutton
    private JButton suchenButton;

    // Logout-Button
    private JButton logoutButton;

    // Tabelle für Artikel
    private JTable artikelTable;

    // Tabellenmodell
    private DefaultTableModel tableModel;
    private JButton inWarenkorbButton;

    public KundenPanel() {

        // Hauptlayout des Panels
        setLayout(new BorderLayout());

        // Hintergrundfarbe
        setBackground(new Color(242, 246, 252));

        // NORD-BEREICH
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setBackground(new Color(242, 246, 252));

        // Titel
        JLabel titleLabel = new JLabel("eShop");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        northPanel.add(titleLabel, BorderLayout.NORTH);

        // Toolbar
        JPanel toolbarPanel = new JPanel(new BorderLayout(10, 10));
        toolbarPanel.setBackground(new Color(242, 246, 252));

        // Suchbereich rechts
        JPanel searchPanel = new JPanel();
        searchPanel.setBackground(new Color(242, 246, 252));
        searchPanel.add(new JLabel("Suche nach Bezeichnung:"));
        sucheField = new JTextField(20);
        searchPanel.add(sucheField);
        suchenButton = new JButton("\uD83D\uDD0D Suchen");
        searchPanel.add(suchenButton);

// Button zum Hinzufügen eines Artikels
        inWarenkorbButton = new JButton("➕ In Warenkorb");

        searchPanel.add(inWarenkorbButton);

// Button zum Anzeigen des Warenkorbs
        warenkorbButton = new JButton("\uD83D\uDED2 Warenkorb anzeigen");

        searchPanel.add(warenkorbButton);

        toolbarPanel.add(searchPanel, BorderLayout.EAST);
        northPanel.add(toolbarPanel, BorderLayout.SOUTH);
        add(northPanel, BorderLayout.NORTH);

        // MITTELBEREICH
        String[] spalten = {
                "Nr",
                "Bezeichnung",
                "Preis"
        };

        tableModel = new DefaultTableModel(
                spalten,
                0) {
            @Override
            public boolean isCellEditable(
                    int row,
                    int column
            ) {
                return false;
            }
        };
        artikelTable = new JTable(tableModel);

// Tabellenkopf einfärben
        artikelTable.getTableHeader().setBackground(
                new Color(70, 130, 180)
        );

        artikelTable.getTableHeader().setForeground(
                Color.WHITE
        );

        artikelTable.getTableHeader().setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        14
                )
        );
        JScrollPane scrollPane =
                new JScrollPane(artikelTable);

// Abstand links und rechts zum Fensterrand
        scrollPane.setBorder(
                BorderFactory.createEmptyBorder(
                        5,   // oben
                        25,  // links
                        0,   // unten
                        25   // rechts
                )
        );

        add(scrollPane, BorderLayout.CENTER);

        // SÜD-BEREICH
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBackground(new Color(242, 246, 252));
        kundeLabel = new JLabel("Kunde: Nicht angemeldet");
        southPanel.add(kundeLabel, BorderLayout.WEST);
        logoutButton = new JButton("\uD83D\uDEAA Logout");
        southPanel.add(logoutButton, BorderLayout.EAST);
        add(southPanel, BorderLayout.SOUTH);
    }

    // Getter
    public JButton getWarenkorbButton() {
        return warenkorbButton;}

    public JTextField getSucheField() {
        return sucheField;
    }

    public JButton getSuchenButton() {
        return suchenButton;
    }

    public JButton getLogoutButton() {
        return logoutButton;
    }

    public JTable getArtikelTable() {
        return artikelTable;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

     //Zeigt den aktuell angemeldeten Kunden an.
    public void setKundeName(String name) {
        kundeLabel.setText("Kunde: " + name);
    }

    // Liefert den Button "In Warenkorb" zurück
    public JButton getInWarenkorbButton() {
        return inWarenkorbButton;
    }

    /**
     * Zeigt alle Artikel in der Tabelle an.
     */
    public void zeigeArtikelListe(java.util.HashMap<Integer, entities.Artikel> artikelListe) {
        // Alte Einträge entfernen
        tableModel.setRowCount(0);

        // Artikel zur Tabelle hinzufügen
        for (Integer artikelID : artikelListe.keySet()) {
            entities.Artikel artikel = artikelListe.get(artikelID);
            tableModel.addRow(
                    new Object[]{
                            artikel.getArtikelID(),
                            artikel.getBezeichnung(),
                            artikel.getPreis()
                    }
            );
        }
    }

    /**
     * Filtert die Artikeltabelle nach der Bezeichnung.
     */
    public void filtereArtikel(String suchbegriff) {

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        artikelTable.setRowSorter(sorter);
        if (suchbegriff == null || suchbegriff.isBlank()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(
                    RowFilter.regexFilter(
                            "(?i)" + suchbegriff,
                            1 // Spalte "Bezeichnung"
                    )
            );
        }
    }
}
