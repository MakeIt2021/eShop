package ui.gui;

import entities.Rechnung;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Dialog zur Anzeige der Rechnung
 * und zur Bestätigung eines Kaufs.
 */
public class KaufenDialog extends JDialog {

    // Buttons
    private JButton bestaetigenButton;
    private JButton abbrechenButton;
    private DefaultTableModel tabelleModel;
    private JTable ausgewaehlteArtikel;

    public KaufenDialog(
            JFrame parent,
            Rechnung rechnung
    ) {

        super(parent, "Rechnung", true);

        setSize(600, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel topBar = new JPanel(new BorderLayout());


        JLabel titleLabel = new JLabel("Rechnung");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        topBar.add(titleLabel, BorderLayout.NORTH);


        JPanel clientAndDatePanel = new JPanel(new FlowLayout());
        JLabel clientLabel = new JLabel("Kunde: " + rechnung.getKundeName());
        JLabel dateLabel = new JLabel("Datum: " + rechnung.getHeutigesDatum());
        clientAndDatePanel.add(clientLabel);
        clientAndDatePanel.add(dateLabel);
        topBar.add(clientAndDatePanel, BorderLayout.CENTER);

        JSeparator separator1 = new JSeparator(SwingConstants.HORIZONTAL);
        // Höhe an 1die Schriftgröße anpassen (z.B. 15 Pixel hoch, 10 Pixel Breite für Abstand)
        separator1.setPreferredSize(new Dimension(10, 15));
        topBar.add(separator1, BorderLayout.SOUTH);

        add(topBar, BorderLayout.NORTH);
        
        JPanel artikelInfoPanel = new JPanel(new BorderLayout());

        String[] spalten = {"Bezeichnung", "Menge", "Preis"};

        tabelleModel = new DefaultTableModel(spalten, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        ausgewaehlteArtikel = new JTable(tabelleModel);

        ausgewaehlteArtikel.getTableHeader().setBackground(new Color(70, 130, 180));
        ausgewaehlteArtikel.getTableHeader().setForeground(Color.WHITE);
        ausgewaehlteArtikel.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(ausgewaehlteArtikel);

        artikelInfoPanel.add(scrollPane, BorderLayout.CENTER);

        // Höhe an die Schriftgröße anpassen (z.B. 15 Pixel hoch, 10 Pixel Breite für Abstand)
        JSeparator separator2 = new JSeparator(SwingConstants.HORIZONTAL);
        // Höhe an 1die Schriftgröße anpassen (z.B. 15 Pixel hoch, 10 Pixel Breite für Abstand)
        separator2.setPreferredSize(new Dimension(10, 15));

        artikelInfoPanel.add(separator2, BorderLayout.SOUTH);

        JPanel summeInfo = new JPanel(new GridLayout(3, 1));
        JLabel summe = new JLabel("Summe: " + rechnung.getSumme() + "€");
        JLabel mwst = new JLabel("MwSt (19%): " + rechnung.getMwst() + "€");
        JLabel gesamtpreis = new JLabel("Gesamtpreis: " + rechnung.getGesamtPreis() + "€");

        summeInfo.add(summe);
        summeInfo.add(mwst);
        summeInfo.add(gesamtpreis);

        artikelInfoPanel.add(summeInfo, BorderLayout.SOUTH);

        add(artikelInfoPanel, BorderLayout.CENTER);

        // BUTTONS
        JPanel buttonPanel = new JPanel();

        bestaetigenButton =
                new JButton(
                        "✅ Kauf bestätigen"
                );

        abbrechenButton =
                new JButton(
                        "❌ Abbrechen"
                );

        buttonPanel.add(bestaetigenButton);

        buttonPanel.add(abbrechenButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // ABBRECHEN
        abbrechenButton.addActionListener(e -> dispose());
    }

    public void zeigeAusgewaehlteArtikel(Rechnung rechnung) {
        tabelleModel.setRowCount(0);

        // Alle gekauften Artikel anzeigen
        for (Rechnung.GekaufterArtikel artikel : rechnung.gibAlleGekaufteArtikel()) {
            tabelleModel.addRow(
                new Object[]{
                    artikel.bezeichnung(),
                    artikel.menge() + " * " + artikel.preis(),
                    artikel.summe()
                }
            );
        }
    }

    public JButton getBestaetigenButton() {
        return bestaetigenButton;
    }

    public JButton getAbbrechenButton() {
        return abbrechenButton;
    }
}