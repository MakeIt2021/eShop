package eShop.client.ui.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;

/**
 * Stellt die Hauptoberfläche für Mitarbeiter dar.
 *
 * Über dieses Panel können Mitarbeiter Artikel verwalten,
 * nach Artikeln suchen, Ereignisse und Bestandshistorien
 * anzeigen, neue Mitarbeiter registrieren und sich abmelden.
 */
public class mitarbeiterMainPanel extends JPanel {

    /**
     * Zeigt den Namen des aktuell angemeldeten Mitarbeiters an.
     */
    private JLabel mitarbeiterLabel;

    /**
     * Suchfeld zum Filtern der Artikeltabelle.
     */
    private JTextField sucheField;

    /**
     * Button zum Abmelden.
     */
    private JButton logoutButton;

    /**
     * Button zum Hinzufügen eines neuen Artikels.
     */
    private JButton artikelHinzufuegenButton;

    /**
     * Button zum Bearbeiten eines Artikels.
     */
    private JButton artikelVeraendernButton;

    /**
     * Button zum Anzeigen der Ereignisliste.
     */
    private JButton ereignisseButton;

    /**
     * Button zum Anzeigen der Bestandshistorie.
     */
    private JButton historieButton;

    /**
     * Button zur Registrierung neuer Mitarbeiter.
     */
    private JButton mitarbeiterRegButton;

    /**
     * Tabelle mit allen Artikeln.
     */
    private JTable artikelTabelle;

    /**
     * Tabellenmodell der Artikeltabelle.
     */
    private DefaultTableModel artikelTabelleModel;

    /**
     * Erstellt das Hauptpanel für Mitarbeiter und
     * initialisiert alle grafischen Komponenten.
     */
    public mitarbeiterMainPanel() {

        // Hauptlayout festlegen.
        setLayout(new BorderLayout());

        // Hintergrundfarbe setzen.
        setBackground(Color.WHITE);

        // ===========================
        // Oberer Bereich
        // ===========================

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setBackground(Color.WHITE);

        // Titel des Mitarbeiterbereichs.
        JLabel eShopLabel = new JLabel("eShop - Mitarbeiter");
        eShopLabel.setFont(new Font("Arial", Font.BOLD, 26));
        eShopLabel.setHorizontalAlignment(SwingConstants.CENTER);

        northPanel.add(eShopLabel, BorderLayout.NORTH);

        // Suchbereich.
        JPanel suchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        suchPanel.setBackground(Color.WHITE);

        suchPanel.add(new JLabel("Artikel suchen:"));

        sucheField = new JTextField(15);

        suchPanel.add(sucheField);

        northPanel.add(suchPanel, BorderLayout.SOUTH);

        add(northPanel, BorderLayout.NORTH);

        // ===========================
        // Artikeltabelle
        // ===========================

        String[] spalten = {
                "ID",
                "Bezeichnung",
                "Bestand",
                "Preis"
        };

        // Tabellenmodell erstellen.
        artikelTabelleModel = new DefaultTableModel(spalten, 0) {

            /**
             * Verhindert das Bearbeiten der Tabellenzellen.
             */
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        artikelTabelle = new JTable(artikelTabelleModel);

        // Sortierung und Filterung ermöglichen.
        TableRowSorter<DefaultTableModel> sorter =
                new TableRowSorter<>(artikelTabelleModel);

        artikelTabelle.setRowSorter(sorter);

        JScrollPane scrollPane =
                new JScrollPane(artikelTabelle);

        add(scrollPane, BorderLayout.CENTER);

        // ===========================
        // Unterer Bereich
        // ===========================

        JPanel southPanel =
                new JPanel(new GridLayout(2, 1));

        JPanel buttonBar =
                new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        buttonBar.setBackground(new Color(242, 246, 252));

        // Aktionsbuttons erstellen.
        artikelHinzufuegenButton =
                new JButton("Artikel Hinzufügen");

        artikelVeraendernButton =
                new JButton("Artikel verändern");

        historieButton =
                new JButton("Bestandhistorie ansehen");

        ereignisseButton =
                new JButton("Ereignisse ansehen");

        mitarbeiterRegButton =
                new JButton("Mitarbeiter registrieren");

        buttonBar.add(artikelHinzufuegenButton);
        buttonBar.add(artikelVeraendernButton);
        buttonBar.add(historieButton);

        // Optische Trennung der Buttons.
        JLabel separator = new JLabel("|");
        separator.setForeground(Color.LIGHT_GRAY);
        separator.setBorder(
                BorderFactory.createEmptyBorder(
                        0,
                        15,
                        0,
                        15
                )
        );

        buttonBar.add(separator);

        buttonBar.add(ereignisseButton);
        buttonBar.add(mitarbeiterRegButton);

        southPanel.add(buttonBar);

        // ===========================
        // Statusleiste
        // ===========================

        JPanel statusBar =
                new JPanel(new BorderLayout());

        statusBar.setBackground(new Color(230, 235, 245));

        statusBar.setBorder(
                BorderFactory.createEmptyBorder(
                        5,
                        10,
                        5,
                        10
                )
        );

        mitarbeiterLabel =
                new JLabel("Mitarbeiter: nicht angemeldet");

        statusBar.add(mitarbeiterLabel, BorderLayout.WEST);

        logoutButton = new JButton("Logout");

        statusBar.add(logoutButton, BorderLayout.EAST);

        southPanel.add(statusBar);

        add(southPanel, BorderLayout.SOUTH);
    }

    /**
     * Gibt das Tabellenmodell zurück.
     *
     * @return Tabellenmodell der Artikeltabelle
     */
    public DefaultTableModel getArtikelTabelleModel() {
        return artikelTabelleModel;
    }

    /**
     * Gibt die Artikeltabelle zurück.
     *
     * @return Artikeltabelle
     */
    public JTable getArtikelTabelle() {
        return artikelTabelle;
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
     * Gibt das Suchfeld zurück.
     *
     * @return Suchfeld
     */
    public JTextField getSucheField() {
        return sucheField;
    }

    /**
     * Gibt das Label mit dem Mitarbeiternamen zurück.
     *
     * @return Mitarbeiter-Label
     */
    public JLabel getMitarbeiterLabel() {
        return mitarbeiterLabel;
    }

    /**
     * Gibt den Button zum Hinzufügen eines Artikels zurück.
     *
     * @return Artikel-Hinzufügen-Button
     */
    public JButton getArtikelHinzufuegenButton() {
        return artikelHinzufuegenButton;
    }

    /**
     * Gibt den Button zum Bearbeiten eines Artikels zurück.
     *
     * @return Artikel-Verändern-Button
     */
    public JButton getArtikelVeraendernButton() {
        return artikelVeraendernButton;
    }

    /**
     * Gibt den Button zur Anzeige der Bestandshistorie zurück.
     *
     * @return Historie-Button
     */
    public JButton getHistorieButton() {
        return historieButton;
    }

    /**
     * Gibt den Button zur Anzeige der Ereignisse zurück.
     *
     * @return Ereignisse-Button
     */
    public JButton getEreignisseButton() {
        return ereignisseButton;
    }

    /**
     * Gibt den Button zur Registrierung neuer Mitarbeiter zurück.
     *
     * @return Registrierungs-Button
     */
    public JButton getMitarbeiterRegButton() {
        return mitarbeiterRegButton;
    }

    /**
     * Aktualisiert den angezeigten Mitarbeiternamen.
     *
     * @param name Name des angemeldeten Mitarbeiters
     */
    public void setMitarbeiterName(String name) {
        mitarbeiterLabel.setText("Mitarbeiter: " + name);
    }

    /**
     * Filtert die Artikeltabelle anhand der Bezeichnung.
     *
     * Die Suche erfolgt unabhängig von Groß-
     * und Kleinschreibung.
     *
     * @param suchbegriff Suchtext
     */
    public void filtereArtikel(String suchbegriff) {

        TableRowSorter<DefaultTableModel> sorter =
                new TableRowSorter<>(artikelTabelleModel);

        artikelTabelle.setRowSorter(sorter);

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