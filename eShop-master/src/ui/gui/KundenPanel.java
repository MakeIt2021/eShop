package ui.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class KundenPanel extends JPanel {

    private JLabel kundeLabel;
    // Dropdown-Menü für Aktionen
    private JComboBox<String> aktionenComboBox;

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

        // Aktionen links
        String[] aktionen = {
                "Artikel anzeigen",
                "Warenkorb anzeigen",
                "Kaufen",
                "Logout"
        };
        aktionenComboBox = new JComboBox<>(aktionen);
        toolbarPanel.add(aktionenComboBox, BorderLayout.WEST);

        // Suchbereich rechts
        JPanel searchPanel = new JPanel();
        searchPanel.setBackground(new Color(242, 246, 252));
        searchPanel.add(new JLabel("Suche:"));
        sucheField = new JTextField(20);
        searchPanel.add(sucheField);
        suchenButton = new JButton("Suchen");
        searchPanel.add(suchenButton);
        toolbarPanel.add(searchPanel, BorderLayout.EAST);
        northPanel.add(toolbarPanel, BorderLayout.SOUTH);
        add(northPanel, BorderLayout.NORTH);

        // MITTELBEREICH
        String[] spalten = {
                "ID",
                "Bezeichnung",
                "Preis"
        };

        tableModel = new DefaultTableModel(spalten, 0);
        artikelTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(artikelTable);
        add(scrollPane, BorderLayout.CENTER);

        // SÜD-BEREICH
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBackground(new Color(242, 246, 252));
        kundeLabel = new JLabel("Kunde: Nicht angemeldet");
        southPanel.add(kundeLabel, BorderLayout.WEST);
        logoutButton = new JButton("Logout");
        southPanel.add(logoutButton, BorderLayout.EAST);
        add(southPanel, BorderLayout.SOUTH);
    }

    // Getter
    public JComboBox<String> getAktionenComboBox() {
        return aktionenComboBox;
    }

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
}
