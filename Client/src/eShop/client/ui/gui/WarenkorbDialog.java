package eShop.client.ui.gui;

import eShop.common.entities.Artikel;
import eShop.common.entities.Massengutartikel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;

/**
 * Stellt den Dialog für den Warenkorb dar.
 *
 * Der Dialog zeigt alle Artikel des Warenkorbs,
 * deren Mengen, Einzelpreise und Gesamtpreise an.
 * Außerdem können Mengen geändert, Artikel entfernt
 * oder der Kauf abgeschlossen werden.
 */
public class WarenkorbDialog extends JDialog {

    /**
     * Tabelle zur Anzeige aller Warenkorbartikel.
     */
    private JTable warenkorbTable;

    /**
     * Tabellenmodell der Warenkorbtabelle.
     */
    private DefaultTableModel tableModel;

    /**
     * Label zur Anzeige der Gesamtsumme.
     */
    private JLabel summeLabel;

    /**
     * Button zum Ändern der Artikelmenge.
     */
    private JButton mengeAendernButton;

    /**
     * Button zum Entfernen eines Artikels.
     */
    private JButton artikelEntfernenButton;

    /**
     * Button zum Abschließen des Kaufs.
     */
    private JButton kaufenButton;

    /**
     * Button zum Schließen des Dialogs.
     */
    private JButton schliessenButton;

    /**
     * Erstellt einen neuen Warenkorb-Dialog.
     *
     * @param parent übergeordnetes Fenster
     * @param warenkorb aktueller Warenkorb
     * @param artikelListe Liste aller verfügbaren Artikel
     */
    public WarenkorbDialog(
            JFrame parent,
            HashMap<Integer, Integer> warenkorb,
            HashMap<Integer, Artikel> artikelListe
    ) {

        super(parent, "Warenkorb", true);

        // ===========================
        // Dialog konfigurieren
        // ===========================

        setSize(900, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        // ===========================
        // Tabelle erstellen
        // ===========================

        String[] spalten = {
                "Nr",
                "Bezeichnung",
                "Einzelpreis",
                "Menge",
                "Gesamtpreis"
        };

        tableModel = new DefaultTableModel(spalten, 0) {

            /**
             * Verhindert das Bearbeiten der Tabellenzellen.
             */
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        warenkorbTable = new JTable(tableModel);

        // Tabellenkopf gestalten.
        warenkorbTable.getTableHeader().setBackground(
                new Color(70, 130, 180));
        warenkorbTable.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane =
                new JScrollPane(warenkorbTable);

        scrollPane.setBorder(
                BorderFactory.createEmptyBorder(
                        0,
                        25,
                        0,
                        55
                )
        );

        add(scrollPane, BorderLayout.CENTER);

        // ===========================
        // Warenkorb laden
        // ===========================

        BigDecimal gesamtSumme = BigDecimal.ZERO;

        for (Integer artikelID : warenkorb.keySet()) {

            Artikel artikel = artikelListe.get(artikelID);

            int menge = warenkorb.get(artikelID);

            // Artikel mit Menge 0 nicht anzeigen.
            if (menge <= 0) {
                continue;
            }

            BigDecimal einzelpreis = artikel.getPreis();

            BigDecimal gesamtpreis =
                    einzelpreis.multiply(
                                    new BigDecimal(menge))
                            .setScale(2, RoundingMode.HALF_EVEN);

            // Gesamtpreis für Massengutartikel berechnen.
            if (artikel instanceof Massengutartikel) {

                gesamtpreis =
                        einzelpreis.multiply(
                                        new BigDecimal(menge))
                                .divide(
                                        new BigDecimal(
                                                ((Massengutartikel) artikel)
                                                        .getPackungGroesse()),
                                        2,
                                        RoundingMode.HALF_EVEN
                                );
            }

            gesamtSumme = gesamtSumme.add(gesamtpreis);

            // Artikel zur Tabelle hinzufügen.
            if (artikel instanceof Massengutartikel) {

                tableModel.addRow(
                        new Object[]{
                                artikel.getArtikelID(),
                                artikel.getBezeichnung()
                                        + " ("
                                        + ((Massengutartikel) artikel).getPackungGroesse()
                                        + " in der Packung)",
                                String.format("%.2f €", einzelpreis),
                                menge,
                                String.format("%.2f €", gesamtpreis)
                        });

            } else {

                tableModel.addRow(
                        new Object[]{
                                artikel.getArtikelID(),
                                artikel.getBezeichnung(),
                                String.format("%.2f €", einzelpreis),
                                menge,
                                String.format("%.2f €", gesamtpreis)
                        });
            }
        }

        // ===========================
        // Unterer Bereich
        // ===========================

        JPanel southPanel =
                new JPanel(new BorderLayout());

        summeLabel =
                new JLabel(
                        "Summe: "
                                + String.format("%.2f €", gesamtSumme)
                );

        summeLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        16
                )
        );

        southPanel.add(summeLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();

        mengeAendernButton =
                new JButton("✏ Menge ändern");

        artikelEntfernenButton =
                new JButton("🗑 Artikel entfernen");

        kaufenButton =
                new JButton("💳 Kaufen");

        schliessenButton =
                new JButton("❌ Schließen");

        buttonPanel.add(mengeAendernButton);
        buttonPanel.add(artikelEntfernenButton);
        buttonPanel.add(kaufenButton);
        buttonPanel.add(schliessenButton);

        southPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(southPanel, BorderLayout.SOUTH);

        // Dialog schließen.
        schliessenButton.addActionListener(
                e -> dispose()
        );
    }

    /**
     * Gibt die Warenkorbtabelle zurück.
     *
     * @return Warenkorbtabelle
     */
    public JTable getWarenkorbTable() {
        return warenkorbTable;
    }

    /**
     * Gibt den Button zum Ändern der Menge zurück.
     *
     * @return Mengenänderungs-Button
     */
    public JButton getMengeAendernButton() {
        return mengeAendernButton;
    }

    /**
     * Gibt den Button zum Entfernen eines Artikels zurück.
     *
     * @return Entfernen-Button
     */
    public JButton getArtikelEntfernenButton() {
        return artikelEntfernenButton;
    }

    /**
     * Gibt den Kaufen-Button zurück.
     *
     * @return Kaufen-Button
     */
    public JButton getKaufenButton() {
        return kaufenButton;
    }

    /**
     * Liefert die Menge des aktuell ausgewählten Artikels.
     *
     * @return ausgewählte Artikelmenge
     */
    public int getAusgewaehlteMenge() {

        int selectedRow = warenkorbTable.getSelectedRow();

        return (Integer) warenkorbTable.getValueAt(
                selectedRow,
                3
        );
    }

    /**
     * Aktualisiert den Warenkorb nach Änderungen.
     *
     * Die Tabelle wird vollständig neu aufgebaut
     * und die Gesamtsumme neu berechnet.
     *
     * @param warenkorb aktueller Warenkorb
     * @param artikelListe Liste aller Artikel
     */
    public void ladeWarenkorbNeu(
            HashMap<Integer, Integer> warenkorb,
            HashMap<Integer, Artikel> artikelListe) {

        // Alte Tabelleninhalte löschen.
        tableModel.setRowCount(0);

        BigDecimal gesamtSumme = BigDecimal.ZERO;

        for (Integer artikelID : warenkorb.keySet()) {
            Artikel artikel = artikelListe.get(artikelID);
            int menge = warenkorb.get(artikelID);

            // Artikel mit Menge 0 überspringen.
            if (menge <= 0) {
                continue;
            }

            BigDecimal einzelpreis = artikel.getPreis();
            BigDecimal gesamtpreis =
                    einzelpreis.multiply(
                                    new BigDecimal(menge))
                            .setScale(2, RoundingMode.HALF_EVEN);

            gesamtSumme = gesamtSumme.add(gesamtpreis);

            if (artikel instanceof Massengutartikel) {
                tableModel.addRow(
                        new Object[]{
                                artikel.getArtikelID(),
                                artikel.getBezeichnung()
                                        + " ("
                                        + ((Massengutartikel) artikel).getPackungGroesse()
                                        + " in der Packung)",
                                String.format("%.2f €", einzelpreis),
                                menge,
                                String.format("%.2f €", gesamtpreis)
                        });

            } else {
                tableModel.addRow(
                        new Object[]{
                                artikel.getArtikelID(),
                                artikel.getBezeichnung(),
                                String.format("%.2f €", einzelpreis),
                                menge,
                                String.format("%.2f €", gesamtpreis)
                        });
            }
        }

        // Neue Gesamtsumme anzeigen.
        summeLabel.setText(
                "Summe: "
                        + String.format("%.2f €", gesamtSumme)
        );
    }
}