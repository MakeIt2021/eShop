package eShop.client.ui.gui;

import eShop.common.entities.Artikel;
import eShop.common.entities.Massengutartikel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.HashMap;

/**
 * Stellt die Benutzeroberfläche für Kunden dar.
 *
 * Das Panel ermöglicht es dem Kunden,
 * verfügbare Artikel anzusehen, nach Artikeln zu suchen,
 * Artikel in den Warenkorb zu legen sowie den Warenkorb
 * anzuzeigen oder sich abzumelden.
 */
public class KundenPanel extends JPanel {

    /**
     * Zeigt den Namen des aktuell angemeldeten Kunden an.
     */
    private JLabel kundeLabel;

    /**
     * Button zum Anzeigen des Warenkorbs.
     */
    private JButton warenkorbButton;

    /**
     * Suchfeld zum Filtern der Artikelliste.
     */
    private JTextField sucheField;

    /**
     * Button zum Abmelden.
     */
    private JButton logoutButton;

    /**
     * Tabelle mit allen verfügbaren Artikeln.
     */
    private JTable artikelTable;

    /**
     * Tabellenmodell der Artikeltabelle.
     */
    private DefaultTableModel tableModel;

    /**
     * Button zum Hinzufügen eines Artikels in den Warenkorb.
     */
    private JButton inWarenkorbButton;

    /**
     * Erstellt das Kundenpanel und initialisiert
     * alle grafischen Komponenten.
     */
    public KundenPanel() {

        // Hauptlayout des Panels.
        setLayout(new BorderLayout());

        // Hintergrundfarbe festlegen.
        setBackground(new Color(242, 246, 252));

        // ===========================
        // Oberer Bereich
        // ===========================

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setBackground(new Color(242, 246, 252));

        // Titel anzeigen.
        JLabel titleLabel = new JLabel("eShop");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        northPanel.add(titleLabel, BorderLayout.NORTH);

        // Werkzeugleiste mit Suchfeld und Buttons.
        JPanel toolbarPanel = new JPanel(new BorderLayout(10, 10));
        toolbarPanel.setBackground(new Color(242, 246, 252));

        JPanel searchPanel = new JPanel();
        searchPanel.setBackground(new Color(242, 246, 252));

        searchPanel.add(new JLabel("Suche nach Bezeichnung:"));

        sucheField = new JTextField(20);
        searchPanel.add(sucheField);

        // Button zum Hinzufügen eines Artikels.
        inWarenkorbButton = new JButton("➕ In Warenkorb");
        searchPanel.add(inWarenkorbButton);

        // Button zum Öffnen des Warenkorbs.
        warenkorbButton = new JButton("🛒 Warenkorb anzeigen");
        searchPanel.add(warenkorbButton);

        toolbarPanel.add(searchPanel, BorderLayout.EAST);

        northPanel.add(toolbarPanel, BorderLayout.SOUTH);

        add(northPanel, BorderLayout.NORTH);

        // ===========================
        // Mittelbereich
        // ===========================

        String[] spalten = {
                "Nr",
                "Bezeichnung",
                "Preis"
        };

        // Tabellenmodell erstellen.
        tableModel = new DefaultTableModel(spalten, 0) {

            /**
             * Verhindert das Bearbeiten der Tabellenzellen.
             */
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        artikelTable = new JTable(tableModel);

        // Tabellenkopf gestalten.
        artikelTable.getTableHeader().setBackground(
                new Color(70, 130, 180));

        artikelTable.getTableHeader().setForeground(Color.WHITE);

        artikelTable.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(artikelTable);

        // Abstand zum Fensterrand.
        scrollPane.setBorder(
                BorderFactory.createEmptyBorder(
                        5,
                        25,
                        0,
                        25
                )
        );

        add(scrollPane, BorderLayout.CENTER);

        // ===========================
        // Unterer Bereich
        // ===========================

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBackground(new Color(242, 246, 252));

        kundeLabel = new JLabel("Kunde: Nicht angemeldet");
        southPanel.add(kundeLabel, BorderLayout.WEST);

        logoutButton = new JButton("🚪 Logout");
        southPanel.add(logoutButton, BorderLayout.EAST);

        add(southPanel, BorderLayout.SOUTH);
    }

    /**
     * Gibt den Warenkorb-Button zurück.
     *
     * @return Button zum Anzeigen des Warenkorbs
     */
    public JButton getWarenkorbButton() {
        return warenkorbButton;
    }

    /**
     * Gibt das Suchfeld zurück.
     *
     * @return Suchfeld
     */
    public JTextField getSucheField() {
        return sucheField;
    }

    /**
     * Gibt den Logout-Button zurück.
     *
     * @return Logout-Button
     */
    public JButton getLogoutButton() {
        return logoutButton;
    }

    /**
     * Gibt die Artikeltabelle zurück.
     *
     * @return Artikeltabelle
     */
    public JTable getArtikelTable() {
        return artikelTable;
    }

    /**
     * Gibt das Tabellenmodell zurück.
     *
     * @return Tabellenmodell
     */
    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    /**
     * Aktualisiert den angezeigten Kundennamen.
     *
     * @param name Name des angemeldeten Kunden
     */
    public void setKundeName(String name) {
        kundeLabel.setText("Kunde: " + name);
    }

    /**
     * Gibt den Button zum Hinzufügen
     * eines Artikels in den Warenkorb zurück.
     *
     * @return In-Warenkorb-Button
     */
    public JButton getInWarenkorbButton() {
        return inWarenkorbButton;
    }

    /**
     * Zeigt alle verfügbaren Artikel
     * in der Tabelle an.
     *
     * Massengutartikel werden zusätzlich
     * mit ihrer Packungsgröße dargestellt.
     *
     * @param artikelListe Liste aller verfügbaren Artikel
     */
    public void zeigeArtikelListe(HashMap<Integer, Artikel> artikelListe) {

        // Alte Tabelleninhalte entfernen.
        tableModel.setRowCount(0);

        // Alle Artikel in die Tabelle einfügen.
        for (Integer artikelID : artikelListe.keySet()) {

            Artikel artikel = artikelListe.get(artikelID);

            if (artikel instanceof Massengutartikel) {

                tableModel.addRow(
                        new Object[]{
                                artikel.getArtikelID(),
                                artikel.getBezeichnung()
                                        + " (Massengutartikel: "
                                        + ((Massengutartikel) artikel).getPackungGroesse()
                                        + " in der Packung)",
                                artikel.getPreis()
                        });

            } else {

                tableModel.addRow(
                        new Object[]{
                                artikel.getArtikelID(),
                                artikel.getBezeichnung(),
                                artikel.getPreis()
                        });
            }
        }
    }

    /**
     * Filtert die Artikeltabelle anhand
     * der Artikelbezeichnung.
     *
     * Die Suche erfolgt unabhängig von Groß-
     * und Kleinschreibung.
     *
     * @param suchbegriff eingegebener Suchtext
     */
    public void filtereArtikel(String suchbegriff) {

        TableRowSorter<DefaultTableModel> sorter =
                new TableRowSorter<>(tableModel);

        artikelTable.setRowSorter(sorter);

        // Leeres Suchfeld → alle Artikel anzeigen.
        if (suchbegriff == null || suchbegriff.isBlank()) {

            sorter.setRowFilter(null);

        } else {

            // Nur passende Artikel anzeigen.
            sorter.setRowFilter(
                    RowFilter.regexFilter(
                            "(?i)" + suchbegriff,
                            1
                    )
            );
        }
    }
}