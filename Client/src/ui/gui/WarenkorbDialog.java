package ui.gui;

import entities.Artikel;
import entities.Massengutartikel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;

public class WarenkorbDialog extends JDialog {

    // Tabelle zur Anzeige des Warenkorbs
    private JTable warenkorbTable;

    // Tabellenmodell
    private DefaultTableModel tableModel;

    // Label für die Gesamtsumme
    private JLabel summeLabel;

    private JButton mengeAendernButton;
    private JButton artikelEntfernenButton;
    private JButton kaufenButton;
    private JButton schliessenButton;

    public WarenkorbDialog(
            JFrame parent,
            HashMap<Integer, Integer> warenkorb,
            HashMap<Integer, Artikel> artikelListe
    ) {

        super(parent, "Warenkorb", true);

        // Dialog konfigurieren
        setSize(900, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        // TABELLE ERZEUGEN
        String[] spalten = {
                "Nr",
                "Bezeichnung",
                "Einzelpreis",
                "Menge",
                "Gesamtpreis"
        };

        tableModel = new DefaultTableModel(
                spalten,
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        warenkorbTable = new JTable(tableModel);
        //Tablekopft einfärben
        warenkorbTable.getTableHeader().setBackground(new Color(70, 130, 180));
        warenkorbTable.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane =
                new JScrollPane(warenkorbTable);
        scrollPane.setBorder(
                BorderFactory.createEmptyBorder(
                        0,   // oben
                        25,  // links
                        0,   // unten
                        55   // rechts
                )
        );

        add(
                scrollPane,
                BorderLayout.CENTER
        );

        // WARENKORB LADEN
        BigDecimal gesamtSumme = BigDecimal.ZERO;

        for (Integer artikelID : warenkorb.keySet()) {
            Artikel artikel = artikelListe.get(artikelID);

            int menge = warenkorb.get(artikelID);

            // Artikel mit Menge 0 nicht anzeigen
            if (menge <= 0) {
                continue;
            }

            BigDecimal einzelpreis =
                    artikel.getPreis();

            BigDecimal gesamtpreis = einzelpreis.multiply(new BigDecimal(menge)).setScale(2, RoundingMode.HALF_EVEN);;
            if (artikel instanceof Massengutartikel) {
                gesamtpreis = einzelpreis.multiply(new BigDecimal(menge)).divide(new BigDecimal(((Massengutartikel) artikel).getPackungGroesse()), 2, RoundingMode.HALF_EVEN);
            }

            gesamtSumme = gesamtSumme.add(gesamtpreis);

            if (artikel instanceof Massengutartikel) {
                tableModel.addRow(
                        new Object[]{
                                artikel.getArtikelID(),
                                artikel.getBezeichnung() + " (" + ((Massengutartikel) artikel).getPackungGroesse() + " in der Packung)",
                                String.format("%.2f €", einzelpreis),
                                menge,
                                String.format("%.2f €", gesamtpreis)
                        }
                );
            } else {
                tableModel.addRow(
                        new Object[]{
                                artikel.getArtikelID(),
                                artikel.getBezeichnung(),
                                String.format("%.2f €", einzelpreis),
                                menge,
                                String.format("%.2f €", gesamtpreis)
                        }
                );
            }

        }

        // UNTERER BEREICH
        JPanel southPanel =
                new JPanel(
                        new BorderLayout()
                );

        summeLabel =
                new JLabel(
                        "Summe: "
                                + String.format(
                                "%.2f €",
                                gesamtSumme
                        )
                );

        summeLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        16
                )
        );

        southPanel.add(
                summeLabel,
                BorderLayout.NORTH
        );

        JPanel buttonPanel =
                new JPanel();

        mengeAendernButton =
                new JButton("✏ Menge ändern");

        artikelEntfernenButton =
                new JButton("\uD83D\uDDD1 Artikel entfernen");

        kaufenButton =
                new JButton("\uD83D\uDCB3 Kaufen");

        schliessenButton =
                new JButton("❌ Schließen");

        buttonPanel.add(mengeAendernButton);
        buttonPanel.add(artikelEntfernenButton);
        buttonPanel.add(kaufenButton);
        buttonPanel.add(schliessenButton);

        southPanel.add(
                buttonPanel,
                BorderLayout.SOUTH
        );

        add(
                southPanel,
                BorderLayout.SOUTH
        );

        // DIALOG SCHLIESSEN

        schliessenButton.addActionListener(
                e -> dispose()
        );
    }

    public JTable getWarenkorbTable() {
        return warenkorbTable;
    }

    public JButton getMengeAendernButton() {
        return mengeAendernButton;
    }

    public JButton getArtikelEntfernenButton() {
        return artikelEntfernenButton;
    }

    public JButton getKaufenButton() {
        return kaufenButton;
    }

    /**
     * Liefert die Menge des ausgewählten Artikels.
     */
    public int getAusgewaehlteMenge() {

        int selectedRow =
                warenkorbTable.getSelectedRow();

        return (Integer)
                warenkorbTable.getValueAt(
                        selectedRow,
                        3
                );
    }

    /**
     * Aktualisiert die Tabelle nach Änderungen
     * im Warenkorb.
     */
    public void ladeWarenkorbNeu(
            HashMap<Integer, Integer> warenkorb,
            HashMap<Integer, Artikel> artikelListe
    ) {

        // Alte Tabellenzeilen löschen
        tableModel.setRowCount(0);

        BigDecimal gesamtSumme = BigDecimal.ZERO;

        for (Integer artikelID : warenkorb.keySet()) {

            Artikel artikel = artikelListe.get(artikelID);


            int menge = warenkorb.get(artikelID);

            // Artikel mit Menge 0 nicht anzeigen
            if (menge <= 0) {
                continue;
            }

            BigDecimal einzelpreis = artikel.getPreis();

            BigDecimal gesamtpreis = einzelpreis.multiply(new BigDecimal(menge)).setScale(2, RoundingMode.HALF_EVEN);

            gesamtSumme = gesamtSumme.add(gesamtpreis);

            if (artikel instanceof Massengutartikel) {
                tableModel.addRow(
                        new Object[]{
                                artikel.getArtikelID(),
                                artikel.getBezeichnung() + " (" + ((Massengutartikel) artikel).getPackungGroesse() + " in der Packung)",
                                String.format("%.2f €", einzelpreis),
                                menge,
                                String.format("%.2f €", gesamtpreis)
                        }
                );
            } else {
                tableModel.addRow(
                        new Object[]{
                                artikel.getArtikelID(),
                                artikel.getBezeichnung(),
                                String.format("%.2f €", einzelpreis),
                                menge,
                                String.format("%.2f €", gesamtpreis)
                        }
                );
            }


        }

        // Neue Gesamtsumme anzeigen
        summeLabel.setText(
                "Summe: "
                        + String.format(
                        "%.2f €",
                        gesamtSumme
                )
        );
    }
}